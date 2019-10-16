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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button signupButton;
    private EditText inputName, inputCountry, inputPhoneNumber, inputPassword, inputConfirmPassword;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        signupButton = (Button) findViewById(R.id.signup_button);

        inputName = (EditText) findViewById(R.id.signup_name_input);
        inputCountry = (EditText) findViewById(R.id.signup_country_input);
        inputPhoneNumber = (EditText) findViewById(R.id.signup_phone_number_input);
        inputPassword = (EditText) findViewById(R.id.signup_password_input);
        inputConfirmPassword = (EditText) findViewById(R.id.signup_con_password_input);

        loadingBar = new ProgressDialog(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

    }
    public void createAccount(){
        String name = inputName.getText().toString();
        String country = inputCountry.getText().toString();
        String phoneNumber = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();
        String conPassword = inputConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please,Enter your name..", Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(country)){
            Toast.makeText(this, "Please,Enter your country name..", Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Please,Enter your phone number..", Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please,Enter your password..", Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(conPassword)){
            Toast.makeText(this, "Please, confirm your password..", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials..");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatePhoneNumber(name, phoneNumber, country, password);
        }
    }
    public void validatePhoneNumber(final String name, final String phoneNumber, final String cntry, final  String pass){
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("Users").child(phoneNumber).exists()){

                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put("name", name);
                    userdataMap.put("phoneNumber", phoneNumber);
                    userdataMap.put("country", cntry);
                    userdataMap.put("password", pass);

                    RootRef.child("Users").child(phoneNumber).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this,"Congratulations! your account has been created!",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this,  LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this,"Network Error! Please try again.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(RegisterActivity.this, "this phone number "+ phoneNumber+" already exists!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please, try again with another phone number.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this,  MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
