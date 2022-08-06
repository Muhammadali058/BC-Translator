package com.braincoder.bctranslator.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.braincoder.bctranslator.Models.Languages;
import com.braincoder.bctranslator.R;
import com.braincoder.bctranslator.Utils.DB;
import com.braincoder.bctranslator.Utils.HP;
import com.braincoder.bctranslator.databinding.ActivityTranslatorBinding;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class TranslatorActivity extends AppCompatActivity {

    ActivityTranslatorBinding binding;
    TranslatorOptions translatorOptions;
    SharedPreferences prefs;
    Translator translator;
    ArrayAdapter<Languages> languagesAdapter;
    List<Languages> languages;
    DB db;
    boolean isModelDownloaded = false;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTranslatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Translator");

        runFirstTime();
    }

    private void runFirstTime(){
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("isFirstTime", true);

        if(isFirstTime){
            Intent intent = new Intent(TranslatorActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            initAds();
            init();
            setNavigationDrawer();
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
            downloadModelIfNeeded();
        }
    }

    private void initAds(){
        MobileAds.initialize(this);

        // For banner ad
//        binding.bannerAdView.loadAd(adRequest);

        loadAd();
    }

    private void loadAd(){
        AdRequest adRequest = new AdRequest.Builder().build();

        // For interstitial ad
        InterstitialAd.load(this, getString(R.string.interstitial_ad_unit_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d("Ad Message", "Failed to load Ad");
//                loadAd();
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);

                mInterstitialAd = interstitialAd;

            }
        });
    }

    private void showAd(){
        if(mInterstitialAd != null){
            mInterstitialAd.show(TranslatorActivity.this);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    mInterstitialAd = null;
//                    loadAd();
                    finish();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    mInterstitialAd = null;
//                    loadAd();
                    finish();
                }

            });

        }
    }

    @Override
    public void onBackPressed() {
        if(mInterstitialAd != null){
            showAd();
        }else {
            Log.d("Ad Message", "Failed to load Ad");
            finish();
        }

        super.onBackPressed();
    }

    private void init(){
        db = new DB(this);

        binding.sourceBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_GO){
                    translate(binding.sourceBox.getText().toString());
                    return true;
                }

                return false;
            }
        });

    }

    private void setNavigationDrawer(){
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,R.string.open, R.string.close);
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);;
        actionBarDrawerToggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.languages:
                        Intent languageIntent = new Intent(TranslatorActivity.this, LanguagesActivity.class);
                        startActivityForResult(languageIntent, 123);
                        closeDrawer();
                        break;
                    case R.id.settings:
                        Intent settingsIntent = new Intent(TranslatorActivity.this, SettingsActivity.class);
                        startActivityForResult(settingsIntent, 124);
                        closeDrawer();
                        break;
                    case R.id.exit:
                        finish();
                        break;
                }
                return true;
            }
        });
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

        setDefaultLanguages();
    }

    private void setDefaultLanguages(){
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

    private void downloadModelIfNeeded(){
        isModelDownloaded = false;
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
            Toast.makeText(this, "Please wait until model is downloading", Toast.LENGTH_SHORT).show();
        }
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

    private void closeDrawer(){
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager =(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && data != null){
            Languages language = (Languages) data.getSerializableExtra("language");
            languages.add(language);
            languagesAdapter.notifyDataSetChanged();
        }else if(requestCode == 124){
            setDefaultLanguages();
        }
    }

}