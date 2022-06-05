package com.braincoder.bctranslator.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.braincoder.bctranslator.Models.Languages;
import com.braincoder.bctranslator.R;
import com.braincoder.bctranslator.databinding.LanguagesHolderBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.ViewHolder> implements Filterable {
    Context context;
    List<Languages> list;
    List<Languages> listFull;
    OnClickListener onClickListener;

    public LanguagesAdapter(Context context, List<Languages> list, OnClickListener onClickListener) {
        this.context = context;
        this.listFull = list;
        this.onClickListener = onClickListener;

        this.list = new ArrayList<>();
        this.list.addAll(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.languages_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Languages languages = list.get(position);
        String language = languages.getLanguage();
//        language = language.substring(0, 1).toUpperCase() + language.substring(1).toLowerCase();

        holder.binding.language.setText(language);

        holder.binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(languages);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Languages> filteredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(listFull);
            }else {
                String pattern = charSequence.toString().toLowerCase().trim();
                for(Languages languageHolder : listFull){
                    if(languageHolder.getLanguage().toLowerCase().contains(pattern)){
                        filteredList.add(languageHolder);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{
        LanguagesHolderBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LanguagesHolderBinding.bind(itemView);
        }
    }

    public interface OnClickListener{
        void onClick(Languages languageHolder);
    }
}
