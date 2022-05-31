package com.braincoder.bctranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.braincoder.bctranslator.Models.Languages;
import com.braincoder.bctranslator.databinding.ActivityTranslatorBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class TranslatorActivity extends AppCompatActivity {

    ActivityTranslatorBinding binding;
    TranslatorOptions translatorOptions;
    Translator translator;
    ArrayAdapter<Languages> languagesAdapter;
    List<Languages> languages;
    DB db;
    boolean isModelDownloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTranslatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        initButtonClicks();
        setLanguageAdapters();
        downloadModelIfNeeded();
    }

    private void init(){
        db = new DB(this);
    }

    private void initButtonClicks(){
        binding.translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translate(binding.sourceBox.getText().toString());
            }
        });

        binding.swapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int source = binding.sourceLanguage.getSelectedItemPosition();
                int target = binding.targetLanguage.getSelectedItemPosition();

                binding.sourceLanguage.setSelection(target);
                binding.targetLanguage.setSelection(source);

                changeLanguage();
            }
        });

        binding.sourceLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeLanguage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.targetLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeLanguage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.sourceCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", binding.sourceBox.getText());
                if(clipData != null)
                    clipboardManager.setPrimaryClip(clipData);
            }
        });

        binding.sourceCutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", binding.sourceBox.getText());
                if(clipData != null){
                    clipboardManager.setPrimaryClip(clipData);
                    binding.sourceBox.setText("");
                }
            }
        });

        binding.sourcePasteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                CharSequence text = clipboardManager.getPrimaryClip().getItemAt(0).getText();
                binding.sourceBox.setText(text);
            }
        });

        binding.targetCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", binding.targetBox.getText());
                if(clipData != null)
                    clipboardManager.setPrimaryClip(clipData);
            }
        });

        binding.targetCutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", binding.targetBox.getText());
                if(clipData != null){
                    clipboardManager.setPrimaryClip(clipData);
                    binding.targetBox.setText("");
                }
            }
        });

        binding.targetPasteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                CharSequence text = clipboardManager.getPrimaryClip().getItemAt(0).getText();
                binding.targetBox.setText(text);
            }
        });

    }

    private void setLanguageAdapters(){
        languages = new ArrayList<>();
        languages.addAll(db.getAllLanguages());

        languagesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, languages);
        binding.sourceLanguage.setAdapter(languagesAdapter);
        binding.targetLanguage.setAdapter(languagesAdapter);

        binding.sourceLanguage.setSelection(0);
        binding.targetLanguage.setSelection(1);
    }

    private void downloadModelIfNeeded(){
        String fromLanguage = HP.getLanguageCode(languages.get(binding.sourceLanguage.getSelectedItemPosition()).getLanguage());
        String toLanguage = HP.getLanguageCode(languages.get(binding.targetLanguage.getSelectedItemPosition()).getLanguage());

        // Create translator
        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(fromLanguage)
                .setTargetLanguage(toLanguage)
                .build();
        translator = Translation.getClient(translatorOptions);

        // Download model if needed
        translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                isModelDownloaded = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                isModelDownloaded = false;
            }
        });

    }

    private void translate(String text){
        if(isModelDownloaded) {
            translator.translate(text)
                    .addOnSuccessListener(
                            new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    binding.targetBox.setText(o.toString());

                                    hideKeyboard();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Log.i("Error = ", e.getMessage());
                                    Toast.makeText(TranslatorActivity.this, "Error in converting", Toast.LENGTH_SHORT).show();
                                }
                            });
        }else {
            Toast.makeText(this, "Please until model is downloading", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeLanguage(){
        String fromLanguage = languages.get(binding.sourceLanguage.getSelectedItemPosition()).getLanguage();
        String toLanguage = languages.get(binding.targetLanguage.getSelectedItemPosition()).getLanguage();

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(HP.getLanguageCode(fromLanguage))
                .setTargetLanguage(HP.getLanguageCode(toLanguage))
                .build();
        translator = Translation.getClient(translatorOptions);
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager =(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}