package com.example.weddingorganizer.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weddingorganizer.Interface.ItemClickListener;
import com.example.weddingorganizer.R;
import com.like.LikeButton;
import com.like.OnLikeListener;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtProductName, txtProductDescription, txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;
    public LikeButton loveBtn;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        loveBtn = (LikeButton) itemView.findViewById(R.id.love_button);
        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);
    }


    public void setItemClickListener(ItemClickListener listener, LikeButton loveBtn){
        this.listener = listener;
        this.loveBtn = loveBtn;

    }


    @Override
    public void onClick(View view) {
        loveBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                likeButton.setLikeDrawableRes(R.drawable.heart_on);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                likeButton.setUnlikeDrawableRes(R.drawable.heart_off);
            }
        });
        listener.OnClick(view, getAdapterPosition(), false);
    }

}
