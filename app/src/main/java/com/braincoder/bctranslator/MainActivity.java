package com.braincoder.bctranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.braincoder.bctranslator.Models.Languages;
import com.braincoder.bctranslator.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    SharedPreferences prefs;
    List<Languages> list;
    DB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init(){
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        boolean isFirstTime = prefs.getBoolean("isFirstTime", true);
        if(isFirstTime){
            db = new DB(this);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstTime", false);
            editor.commit();

            Languages language = new Languages();
            for (String lan : HP.installedLanguages){
                language.setLanguage(lan);
                db.addLanguage(language);
            }

            list = new ArrayList<>();
            for(String lan: HP.languages){
                list.add(new Languages(lan));
            }

            setLanguageAdapters();
        }else {
            startTranslatorActivity();
        }

        binding.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.downloadLayout.setVisibility(View.VISIBLE);

                String fromLanguage = HP.getLanguageCode(HP.languages[binding.sourceLanguage.getSelectedItemPosition()]);
                String toLanguage = HP.getLanguageCode(HP.languages[binding.targetLanguage.getSelectedItemPosition()]);

                TranslatorOptions options = new TranslatorOptions.Builder()
                        .setSourceLanguage(fromLanguage)
                        .setTargetLanguage(toLanguage)
                        .build();
                Translator translator = Translation.getClient(options);

                // Download model if needed
                translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        startTranslatorActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.i("Failed = ", "Model download failed");
                    }
                });
            }
        });

        binding.swapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int source = binding.sourceLanguage.getSelectedItemPosition();
                int target = binding.targetLanguage.getSelectedItemPosition();

                binding.sourceLanguage.setSelection(target);
                binding.targetLanguage.setSelection(source);
            }
        });

    }

    private void startTranslatorActivity(){
        Intent intent = new Intent(MainActivity.this, TranslatorActivity.class);
        startActivity(intent);
        finish();
    }

    private void setLanguageAdapters(){
        ArrayAdapter<Languages> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        binding.sourceLanguage.setAdapter(arrayAdapter);
        binding.targetLanguage.setAdapter(arrayAdapter);

        binding.sourceLanguage.setSelection(0);
        binding.targetLanguage.setSelection(1);
    }

    private void downloadModel(){
        RemoteModelManager modelManager = RemoteModelManager.getInstance();

        TranslateRemoteModel model =
                new TranslateRemoteModel.Builder(TranslateLanguage.ENGLISH).build();

        DownloadConditions conditions = new DownloadConditions.Builder()
                .build();

        modelManager.download(model, conditions)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.i("Success = ", "Model Downloaded");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.i("Failed = ", "Model download failed");
                    }
                });
    }
}