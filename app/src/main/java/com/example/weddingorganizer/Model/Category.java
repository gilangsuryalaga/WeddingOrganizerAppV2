package com.example.weddingorganizer.Model;

import androidx.cardview.widget.CardView;

public class Category {

    private String nama_promo;

    public Category(String nama_promo) {
        this.nama_promo = nama_promo;
    }

    public String getNama_promo() {
        return nama_promo;
    }

    public void setNama_promo(String nama_promo) {
        this.nama_promo = nama_promo;
    }

}
