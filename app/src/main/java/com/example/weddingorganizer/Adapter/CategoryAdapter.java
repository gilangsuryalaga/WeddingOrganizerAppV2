package com.example.weddingorganizer.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weddingorganizer.Model.Category;
import com.example.weddingorganizer.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> weddingCategory;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private List<Category> weddingCategory;
        public TextView categoryName,Harga;

        public ViewHolder(@NonNull View v) {
            super(v);
            this.weddingCategory = weddingCategory;
            categoryName = (TextView)v.findViewById(R.id.promo_text);
        }
    }
    public CategoryAdapter(List<Category> weddingCategory){

        this.weddingCategory= weddingCategory;

    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return  vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category category = weddingCategory.get(position);
        holder.categoryName.setText( String.valueOf( category.getNama_promo()));
    }


    @Override
    public int getItemCount() {
        return weddingCategory.size();
    }


}
