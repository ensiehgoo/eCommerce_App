package com.example.ecommerce_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce_demo.Model.Users;
import com.example.ecommerce_demo.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button loginSignupButton, loginButton;
    private EditText inputPhoneNumber, inputPassword;
    private ProgressDialog loadingBar;
    private TextView adminLink, notAdminLink;

    private String parentDbName = "Users";

    private CheckBox chkbxRememberme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginSignupButton = (Button) findViewById(R.id.login_signup_button);
        loginButton = (Button) findViewById(R.id.login_button);

        inputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        inputPassword = (EditText) findViewById(R.id.login_password_input);

        loadingBar = new ProgressDialog(this);

        adminLink = (TextView) findViewById(R.id.admin_panel_link);
        notAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);

        chkbxRememberme = (CheckBox) findViewById(R.id.remember_me_chkbx);
        Paper.init(this);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        loginSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,  RegisterActivity.class);
                startActivity(intent);
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginButton.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
/*
                Toast.makeText(LoginActivity.this, "Changing parentdbname", Toast.LENGTH_SHORT).show();
*/
                parentDbName = "Admins";
            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginButton.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });
    }

    private void loginUser() {

        String phoneNumber = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

       /* Toast.makeText(this,"phone number "+ phoneNumber, Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"password "+ password, Toast.LENGTH_SHORT).show();*/

        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Please,Enter your phone number..", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please,Enter your password..", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Log in");
            loadingBar.setMessage("Please wait, while we are checking the credentials..");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phoneNumber, password);

        }

    }

    private void AllowAccessToAccount(final String phoneNumber, final String password) {

        if(chkbxRememberme.isChecked()){
            Paper.book().write(Prevalent.userPhonekey, phoneNumber);
            Paper.book().write(Prevalent.userPasswordkey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

/*
     the problem starts with rootref
                Toast.makeText(LoginActivity.this, "Inside onDataChange!",Toast.LENGTH_SHORT).show();
*/
                if(dataSnapshot.child(parentDbName).child(phoneNumber).exists()){

                    Users usersData = dataSnapshot.child(parentDbName).child(phoneNumber).getValue(Users.class);
                    /*Toast.makeText(LoginActivity.this,"phone: "+ usersData.getPhoneNumber(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "password: "+usersData.getPassword(),Toast.LENGTH_SHORT).show();*/

                    if(usersData.getPhoneNumber().equals(phoneNumber))
                    {
                        if(usersData.getPassword().equals(password)){

                            if(parentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, "Welcome Admin, you are Logged in Successfully!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this,  AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if(parentDbName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this,  HomeActivity.class);
                                startActivity(intent);
                            }
                        }

                        else {
                            Toast.makeText(LoginActivity.this, "Log in unsuccessful", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Please, Enter correct password.", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "Account with this phone number "+ phoneNumber+" doesn't exist!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Please, create a new account.", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
