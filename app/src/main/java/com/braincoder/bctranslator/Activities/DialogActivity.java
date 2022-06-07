package com.braincoder.bctranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.braincoder.bctranslator.Models.Languages;
import com.braincoder.bctranslator.R;
import com.braincoder.bctranslator.Utils.DB;
import com.braincoder.bctranslator.Utils.HP;
import com.braincoder.bctranslator.databinding.ActivityDialogBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class DialogActivity extends AppCompatActivity {

    ActivityDialogBinding binding;
    TranslatorOptions translatorOptions;
    SharedPreferences prefs;
    Translator translator;
    ArrayAdapter<Languages> languagesAdapter;
    List<Languages> languages;
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setFinishOnTouchOutside(true);

        init();
        initButtonClicks();
        setLanguageAdapters();

        // Create translator
        String fromLanguage = HP.getLanguageCode(this, languages.get(binding.sourceLanguage.getSelectedItemPosition()).getLanguage());
        String toLanguage = HP.getLanguageCode(this, languages.get(binding.targetLanguage.getSelectedItemPosition()).getLanguage());
        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(fromLanguage)
                .setTargetLanguage(toLanguage)
                .build();
        translator = Translation.getClient(translatorOptions);

        handleIntent(getIntent());
    }

    private void init(){
        db = new DB(this);
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
    }

    private void initButtonClicks(){
        binding.translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translate(binding.inputBox.getText().toString());
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

        binding.copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", binding.outputBox.getText());
                if(clipData != null)
                    clipboardManager.setPrimaryClip(clipData);
            }
        });

        binding.cutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", binding.outputBox.getText());
                if(clipData != null){
                    clipboardManager.setPrimaryClip(clipData);
                    binding.outputBox.setText("");
                }
            }
        });

        binding.pasteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                CharSequence text = clipboardManager.getPrimaryClip().getItemAt(0).getText();
                binding.outputBox.setText(text);
            }
        });

    }

    private void setLanguageAdapters(){
        languages = new ArrayList<>();
        languages.addAll(db.getAllLanguages());

        languagesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, languages);
        binding.sourceLanguage.setAdapter(languagesAdapter);
        binding.targetLanguage.setAdapter(languagesAdapter);

        String fromLanguage = prefs.getString("fromLanguage", "English");
        String toLanguage = prefs.getString("toLanguage", "Urdu");

        for (int i = 0; i < languages.size(); i++) {
            if(languages.get(i).getLanguage().equalsIgnoreCase(fromLanguage)){
                binding.sourceLanguage.setSelection(i);
            }

            if(languages.get(i).getLanguage().equalsIgnoreCase(toLanguage)){
                binding.targetLanguage.setSelection(i);
            }
        }
    }

    private void translate(String text){
        translator.translate(text)
                .addOnSuccessListener(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                binding.outputBox.setText(o.toString());
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.i("Error = ", e.getMessage());
                                Toast.makeText(DialogActivity.this, "Error in converting", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void changeLanguage(){
        String fromLanguage = languages.get(binding.sourceLanguage.getSelectedItemPosition()).getLanguage();
        String toLanguage = languages.get(binding.targetLanguage.getSelectedItemPosition()).getLanguage();

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(HP.getLanguageCode(this, fromLanguage))
                .setTargetLanguage(HP.getLanguageCode(this, toLanguage))
                .build();
        translator = Translation.getClient(translatorOptions);
        downloadModelIfNeeded();
    }

    private void downloadModelIfNeeded(){
        translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(DialogActivity.this, "Error in downloading model", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void handleIntent(Intent intent){
//        boolean readOnly = true;
//        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT_READONLY)) {
//            readOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false);
//        }

        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            String text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString();
            binding.inputBox.setText(text);
            translate(binding.inputBox.getText().toString());
        }else {
//            binding.inputBox.setText("How are you.");
//            translate(binding.inputBox.getText().toString());
        }
    }

    private void replaceText(String text){
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, text);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}