package com.braincoder.bctranslator.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import com.braincoder.bctranslator.Adapters.LanguagesAdapter;
import com.braincoder.bctranslator.Models.Languages;
import com.braincoder.bctranslator.R;
import com.braincoder.bctranslator.Utils.DB;
import com.braincoder.bctranslator.Utils.HP;
import com.braincoder.bctranslator.databinding.ActivityLanguagesBinding;

import java.util.ArrayList;
import java.util.List;

public class LanguagesActivity extends AppCompatActivity {

    ActivityLanguagesBinding binding;
    LanguagesAdapter languagesAdapter;
    List<Languages> list;
    DB db;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init(){
        db = new DB(this);
        dialog = new ProgressDialog(this);

        list = new ArrayList<>();
        list.addAll(getAllLanguages());

        languagesAdapter = new LanguagesAdapter(this, list, new LanguagesAdapter.OnClickListener() {
            @Override
            public void onClick(Languages languageHolder) {
                addLanguage(languageHolder);
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(languagesAdapter);
    }

    public List<Languages> getAllLanguages(){
        List<Languages> languages = new ArrayList<>();

        for (int i = 0; i < HP.languages.length; i++) {
            String language = HP.languages[i].substring(0, 1).toUpperCase() + HP.languages[i].substring(1).toLowerCase();

            if(!db.isLanguageExists(language))
                languages.add(new Languages(language, HP.languageCodes[i]));
        }

        return languages;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchview_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                languagesAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
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

    private void addLanguage(Languages languages){
        db.addLanguage(languages);

        Intent intent = new Intent();
        intent.putExtra("language", languages);
        setResult(123, intent);

        onBackPressed();
    }
}