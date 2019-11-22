package com.example.weddingorganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    private Button applyChangesBtn;
    private EditText name, price, description;
    private ImageView imageView;
    private String productId = "";
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        productId = getIntent().getStringExtra("pid");

        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productId);

        applyChangesBtn = (Button) findViewById(R.id.apply_changes_btn);
        name = (EditText) findViewById(R.id.product_name_maintain);
        price = (EditText) findViewById(R.id.product_price_maintain);
        description = (EditText) findViewById(R.id.product_description_maintain);
        imageView = (ImageView) findViewById(R.id.product_image_maintain);

        displaySpecificProductInfo();

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChanges();
            }
        });
    }

    private void applyChanges() {

        String nameChanges = name.getText().toString();
        String priceChanges = price.getText().toString();
        String descriptionChanges = description.getText().toString();

        if (nameChanges.equals("")) {
            Toast.makeText(this, "Please Write Down The product name", Toast.LENGTH_SHORT).show();
        } else if (priceChanges.equals("")) {
            Toast.makeText(this, "Please Write Down The product price", Toast.LENGTH_SHORT).show();
        } else if (descriptionChanges.equals("")) {
            Toast.makeText(this, "Please Write Down The product description", Toast.LENGTH_SHORT).show();
        }else{

            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productId);
            productMap.put("name", nameChanges);
            productMap.put("price", priceChanges);
            productMap.put("description", descriptionChanges);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AdminMaintainProductsActivity.this, "Changes applied successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AdminMaintainProductsActivity.this, AdminCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }

    }

    private void displaySpecificProductInfo() {

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nameMaintain = dataSnapshot.child("name").getValue().toString();
                    String priceMaintain = dataSnapshot.child("price").getValue().toString();
                    String descriptionMaintain = dataSnapshot.child("description").getValue().toString();
                    String imageMaintain = dataSnapshot.child("image").getValue().toString();

                    name.setText(nameMaintain);
                    price.setText(priceMaintain);
                    description.setText(descriptionMaintain);
                    Picasso.get().load(imageMaintain).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
