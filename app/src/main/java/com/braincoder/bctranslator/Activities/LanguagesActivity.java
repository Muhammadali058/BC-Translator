package com.braincoder.bctranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.braincoder.bctranslator.Adapters.LanguagesAdapter;
import com.braincoder.bctranslator.Models.LanguageHolder;
import com.braincoder.bctranslator.R;
import com.braincoder.bctranslator.databinding.ActivityLanguagesBinding;

import java.util.ArrayList;
import java.util.List;

public class LanguagesActivity extends AppCompatActivity {

    ActivityLanguagesBinding binding;
    LanguagesAdapter languagesAdapter;
    List<LanguageHolder> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init(){
        list = new ArrayList<>();

        list.add(new LanguageHolder(R.drawable.english, "English"));
        list.add(new LanguageHolder(R.drawable.urdu, "Urdu"));
        list.add(new LanguageHolder(R.drawable.hindi, "Hindi"));

        languagesAdapter = new LanguagesAdapter(this, list, new LanguagesAdapter.OnClickListener() {
            @Override
            public void onClick(String language) {
                Toast.makeText(LanguagesActivity.this, language, Toast.LENGTH_SHORT).show();
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(languagesAdapter);
    }

}