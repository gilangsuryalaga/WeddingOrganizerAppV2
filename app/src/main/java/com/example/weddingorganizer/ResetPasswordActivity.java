package com.example.weddingorganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weddingorganizer.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check = "";
    private EditText phoneNumber, question1, question2;
    private TextView pageTitle, titleQuestions;
    private Button verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");

        pageTitle = findViewById(R.id.page_title);
        phoneNumber = findViewById(R.id.find_phone_number);
        question1 = findViewById(R.id.question1);
        question2 = findViewById(R.id.question2);
        titleQuestions = findViewById(R.id.title_question);
        verifyButton = findViewById(R.id.verify_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        phoneNumber.setVisibility(View.GONE);

        if (check.equals("settings")) {
            titleQuestions.setText("Set Questions");
            pageTitle.setText("Please Answer for The Following Security Questions");
            verifyButton.setText("Set");

            displayPreviousAnswer();

            verifyButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    setAnswer();
                }
            });

        } else if (check.equals("login")) {
            phoneNumber.setVisibility(View.VISIBLE);

            verifyButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    verifyUsers();
                }
            });

        }
    }

    private void verifyUsers() {
        final String phone = phoneNumber.getText().toString();
        final String answer1 = question1.getText().toString().toLowerCase();
        final String answer2 = question2.getText().toString().toLowerCase();

        if (!phone.equals("") && !answer1.equals("") && !answer2.equals("")) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(phone);

            ref.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String mPhone = dataSnapshot.child("phone").getValue().toString();

                        if (dataSnapshot.hasChild("Security Questions")) {
                            String ans1 = dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String ans2 = dataSnapshot.child("Security Questions").child("answer2").getValue().toString();

                            if (!ans1.equals(answer1)) {
                                Toasty.error(ResetPasswordActivity.this, "Your First Answer is incorrect", Toast.LENGTH_SHORT, true).show();
                            } else if (!ans2.equals(answer2)) {
                                Toasty.error(ResetPasswordActivity.this, "Your Second Answer is incorrect", Toast.LENGTH_SHORT, true).show();

                            } else {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New Password");

                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("Write your new Password here");
                                builder.setView(newPassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(final DialogInterface dialogInterface, int i) {
                                        if (!newPassword.getText().toString().equals("")) {
                                            ref.child("password")
                                                    .setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                dialogInterface.dismiss();
                                                                Toasty.success(ResetPasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT, true).show();
                                                                Intent intent = new Intent(ResetPasswordActivity.this, loginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                builder.show();
                            }

                        } else {
                            Toasty.warning(ResetPasswordActivity.this, "You have not set the security question", Toast.LENGTH_SHORT, true).show();
                            }

                    } else {
                        Toasty.error(ResetPasswordActivity.this, "This Phone Number not exist", Toast.LENGTH_SHORT, true).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            Toasty.warning(this, "Please Complete the form", Toast.LENGTH_SHORT, true).show();
        }

    }

    private void setAnswer() {

        String answer1 = question1.getText().toString().toLowerCase();
        String answer2 = question2.getText().toString().toLowerCase();

        if (question1.equals("") && question2.equals("")) {
            Toasty.warning(ResetPasswordActivity.this, "Please answer both questions", Toast.LENGTH_SHORT, true).show();
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Prevalent.currentonlineUser.getPhone());

            HashMap<String, Object> userdataMap = new HashMap<>();
            userdataMap.put("answer1", answer1);
            userdataMap.put("answer2", answer2);

            ref.child("Security Questions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toasty.success(ResetPasswordActivity.this, "Security Question set Successfully", Toast.LENGTH_SHORT, true).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });

        }
    }

    private void displayPreviousAnswer() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Prevalent.currentonlineUser.getPhone());

        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ans1 = dataSnapshot.child("answer1").getValue().toString();
                    String ans2 = dataSnapshot.child("answer2").getValue().toString();

                    question1.setText(ans1);
                    question2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
