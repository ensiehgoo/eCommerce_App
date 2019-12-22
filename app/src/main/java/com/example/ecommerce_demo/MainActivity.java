package com.example.ecommerce_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerce_demo.Model.Users;
import com.example.ecommerce_demo.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.start_button);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,  LoginActivity.class);
                startActivity(intent);
            }
        });

        String userPhonekey = Paper.book().read(Prevalent.userPhonekey);
        String userPasswordkey = Paper.book().read(Prevalent.userPasswordkey);

        if(userPhonekey != "" && userPasswordkey != ""){
            if(!TextUtils.isEmpty(userPhonekey) && !TextUtils.isEmpty(userPhonekey)){

                allowAccess(userPhonekey, userPasswordkey);

                loadingBar.setTitle("Already logged in!");
                loadingBar.setMessage("Please wait..");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    private void allowAccess(final String phoneNumber, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Users").child(phoneNumber).exists()){

                    Users usersdata = dataSnapshot.child("Users").child(phoneNumber).getValue(Users.class);

                    if(usersdata.getPhoneNumber().equals(phoneNumber)){

                        if(usersdata.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "You are already Logged in!", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this,  HomeActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Log in unsuccessful", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Please, Enter correct password.", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Account with this phone number "+ phoneNumber+" doesn't exist!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Please, create a new account.", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
