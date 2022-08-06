package com.braincoder.bctranslator.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.braincoder.bctranslator.Models.Languages;
import com.braincoder.bctranslator.R;
import com.braincoder.bctranslator.Utils.DB;
import com.braincoder.bctranslator.databinding.ActivitySettingsBinding;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    ArrayAdapter<Languages> languagesAdapter;
    List<Languages> languages;
    SharedPreferences prefs;
    DB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        setLanguageAdapters();
    }

    private void init(){
        db = new DB(this);
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromLanguage = languages.get(binding.sourceLanguage.getSelectedItemPosition()).getLanguage();
                String toLanguage = languages.get(binding.targetLanguage.getSelectedItemPosition()).getLanguage();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("fromLanguage", fromLanguage);
                editor.putString("toLanguage", toLanguage);
                editor.commit();

                finish();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

}