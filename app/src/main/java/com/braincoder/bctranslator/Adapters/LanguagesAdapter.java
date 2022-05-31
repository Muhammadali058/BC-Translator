package com.braincoder.bctranslator.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.braincoder.bctranslator.Models.LanguageHolder;
import com.braincoder.bctranslator.R;
import com.braincoder.bctranslator.databinding.LanguagesHolderBinding;

import java.util.List;

public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.ViewHolder> {
    Context context;
    List<LanguageHolder> list;
    OnClickListener onClickListener;

    public LanguagesAdapter(Context context, List<LanguageHolder> list, OnClickListener onClickListener) {
        this.context = context;
        this.list = list;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.languages_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LanguageHolder languageHolder = list.get(position);

        holder.binding.image.setImageResource(languageHolder.getImage());
        holder.binding.language.setText(languageHolder.getLanguage());

        holder.binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLanguage(languageHolder.getLanguage());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLanguage(languageHolder.getLanguage());
            }
        });
    }

    private void addLanguage(String language){
        onClickListener.onClick(language);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        LanguagesHolderBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LanguagesHolderBinding.bind(itemView);
        }
    }

    public interface OnClickListener{
        void onClick(String language);
    }
}
