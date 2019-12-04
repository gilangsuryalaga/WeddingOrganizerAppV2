package com.example.weddingorganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weddingorganizer.Model.Cart;
import com.example.weddingorganizer.Prevalent.Prevalent;
import com.example.weddingorganizer.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import es.dmoral.toasty.Toasty;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProccessBtn;
    private TextView txtTotalAmount, txtmsg;
    private int overTotalPrice = 0;
    private String totalamount = "";
    private String total = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        totalamount = getIntent().getStringExtra("Total Price");

        txtmsg = (TextView) findViewById(R.id.msg1);
        NextProccessBtn = (Button) findViewById(R.id.next_proccess_btn);
        txtTotalAmount = (TextView) findViewById(R.id.total_price);


        NextProccessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTotalAmount.setText("Total Price: Rp " + total);
                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", total);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        cekOrdersState();
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View")
                                .child(Prevalent.currentonlineUser.getPhone()).child("Products"), Cart.class)
                        .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull final Cart model) {
                NumberFormat formatter = new DecimalFormat("#,###");
                holder.txtProductQuantity.setText("Qty: " + model.getQuantity());
                String price = formatter.format(Integer.valueOf(model.getPrice()));
                holder.txtProductPrice.setText("Rp " + price);
                holder.txtProductName.setText(model.getName());

                int oneTypeProductTPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTypeProductTPrice;
                total = formatter.format(overTotalPrice);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]{
                                "Edit",
                                "Remove"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if (i == 1) {
                                    cartListRef.child("User View")
                                            .child(Prevalent.currentonlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toasty.success(CartActivity.this, "Item removed Successfully", Toast.LENGTH_SHORT, true).show();
                                                        Intent intent = new Intent(CartActivity.this, CartActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();


                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void cekOrdersState() {
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentonlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String shippingsState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    if (shippingsState.equals("shipped")) {
                        txtTotalAmount.setText("Dear " + userName + "\n Order is Shipped Successfullyy" + "\n With Total Price: Rp." + overTotalPrice);
                        recyclerView.setVisibility(View.GONE);
                        txtmsg.setVisibility(View.VISIBLE);
                        txtmsg.setText("Congratulation, Shipped Successfully... Wait for Verified by Admin");
                        NextProccessBtn.setVisibility(View.GONE);
                        Toasty.info(CartActivity.this, "Please Wait Until The Order is Completed for Purchase another Products", Toast.LENGTH_SHORT, true).show();
                    } else if (shippingsState.equals("not shipped")) {
                        txtTotalAmount.setText("Your Orders is Not Shipped yet");
                        recyclerView.setVisibility(View.GONE);

                        txtmsg.setVisibility(View.VISIBLE);
                        NextProccessBtn.setVisibility(View.GONE);

                        Toasty.info(CartActivity.this, "Please Wait Until The Order is Completed for Purchase another Products", Toast.LENGTH_SHORT, true).show();


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
