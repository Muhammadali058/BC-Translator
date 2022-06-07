package com.braincoder.bctranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.braincoder.bctranslator.Models.Languages;
import com.braincoder.bctranslator.Utils.DB;
import com.braincoder.bctranslator.Utils.HP;
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
            language.setLanguage("English");
            language.setLanguageCode("en");
            db.addLanguage(language);

            language.setLanguage("Urdu");
            language.setLanguageCode("ur");
            db.addLanguage(language);


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

                String fromLanguage = HP.getLanguageCode(MainActivity.this, HP.languages[binding.sourceLanguage.getSelectedItemPosition()]);
                String toLanguage = HP.getLanguageCode(MainActivity.this, HP.languages[binding.targetLanguage.getSelectedItemPosition()]);

                TranslatorOptions options = new TranslatorOptions.Builder()
                        .setSourceLanguage(fromLanguage)
                        .setTargetLanguage(toLanguage)
                        .build();
                Translator translator = Translation.getClient(options);

                // Download model if needed
                translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("fromLanguage", HP.languages[binding.sourceLanguage.getSelectedItemPosition()]);
                        editor.putString("toLanguage", HP.languages[binding.targetLanguage.getSelectedItemPosition()]);
                        editor.commit();

                        Toast.makeText(MainActivity.this, "Model Downloaded", Toast.LENGTH_SHORT).show();

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

        binding.sourceLanguage.setSelection(11);
        binding.targetLanguage.setSelection(56);
    }

}