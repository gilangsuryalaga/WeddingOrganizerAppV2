package com.example.weddingorganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.weddingorganizer.Model.AdminOrders;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExportPDFActivity extends AppCompatActivity {
    public TextView order_name, order_phone, order_address, order_date, order_total;
    public Button printPDF;
    private String userId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_pdf);

        order_name = (TextView) findViewById(R.id.order_name);
        order_phone = (TextView) findViewById(R.id.order_phone);
        order_address = (TextView) findViewById(R.id.order_address);
        order_date = (TextView) findViewById(R.id.order_date);
        printPDF = (Button) findViewById(R.id.print_pdf);
        order_total = (TextView)findViewById(R.id.order_total);


        userId = getIntent().getStringExtra("uid");

    }

    private void getOrderDetail(String userId) {
        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    AdminOrders order = dataSnapshot.getValue(AdminOrders.class);
                    order_name.setText(order.getName());
                    order_address.setText(order.getAddress() + "," + order.getCity());
                    order_date.setText(order.getDate());
                    order_phone.setText(order.getPhone());
                    order_total.setText(order.getTotalAmount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
