package com.example.weddingorganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weddingorganizer.Admin.AdminCategoryActivity;
import com.example.weddingorganizer.Model.Users;
import com.example.weddingorganizer.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;

public class loginActivity extends AppCompatActivity {

    private EditText InputNumber, InputPassword;
    private TextView AdminLink, NotAdminLink, forgetPaswordLink;
    private Button loginBtn;
    private ProgressDialog loadingBar;
    private CheckBox chckbxRememberme;
    private String parentDbName = "Users";
    RelativeLayout relay1;
    Handler handler = new Handler();
    Runnable runable = new Runnable() {
        @Override
        public void run() {
            relay1.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.login_btn);
        InputNumber = (EditText) findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.login_password_input);

        loadingBar = new ProgressDialog(this);

        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        forgetPaswordLink = (TextView) findViewById(R.id.forget_password_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        chckbxRememberme = (CheckBox) findViewById(R.id.remember_me_check);

        relay1 = (RelativeLayout) findViewById(R.id.rellay1);
        handler.postDelayed( runable, 2000 );
        Paper.init(this);

        forgetPaswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText(getString(R.string.login_admin));
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                chckbxRememberme.setVisibility(View.INVISIBLE);
                parentDbName = "Admins";

            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText(getString(R.string.login));
                chckbxRememberme.setVisibility(View.VISIBLE);
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";

            }
        });
    }

    private void loginUser() {

        String phone = InputNumber.getText().toString();
        String password = InputPassword.getText().toString();


        if (TextUtils.isEmpty(phone)) {

            Toasty.warning(this, getString(R.string.phone_empty), Toast.LENGTH_SHORT, true).show();

        } else if (TextUtils.isEmpty(password)) {
            Toasty.warning(this, getString(R.string.pass_empty), Toast.LENGTH_SHORT, true).show();


        } else {

            loadingBar.setTitle(R.string.login_account);
            loadingBar.setMessage("Please Wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccout(phone, password);
        }
    }

    private void AllowAccessToAccout(final String phone, final String password) {
        if (chckbxRememberme.isChecked()) {

            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);

        }

        final DatabaseReference RootRef;

        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists()) {

                    Users usersdata = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersdata.getPhone().equals(phone)) {
                        if (usersdata.getPassword().equals(password)) {
                            if (parentDbName.equals("Admins")) {

                                Toasty.success(loginActivity.this, "Welcome Admin, You are Logged in Successfully...", Toast.LENGTH_SHORT, true).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(loginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);

                            } else if (parentDbName.equals("Users")) {

                                Toasty.success(loginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT, true).show();

                                loadingBar.dismiss();

                                Intent intent = new Intent(loginActivity.this, HomeActivity.class);
                                Prevalent.currentonlineUser = usersdata;
                                startActivity(intent);

                            }
                        } else {
                            Toasty.error(loginActivity.this, getString(R.string.password_incorrect), Toast.LENGTH_SHORT, true).show();
                            loadingBar.dismiss();

                        }
                    }
                } else {
                    Toasty.error(loginActivity.this,  "Account with this " + phone + " number Do not Exists", Toast.LENGTH_SHORT, true).show();
                    loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(loginActivity.this, MainActivity.class);
        startActivity(intent);

    }
}
