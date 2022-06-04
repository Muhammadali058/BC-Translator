package com.braincoder.bctranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.braincoder.bctranslator.Adapters.LanguagesAdapter;
import com.braincoder.bctranslator.Models.LanguageHolder;
import com.braincoder.bctranslator.R;
import com.braincoder.bctranslator.databinding.ActivityLanguagesBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;

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