package com.example.ecommerce_demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, description, price, pName, saveCurrentDate, saveCurrentTime;
    private Button addNewProductButton;
    private ImageView inputProductImage;
    private EditText inputProductName, inputProductDescription, inputProductPrice;
    private static final int GALLERY_PICK = 1;
    private String productRandomKey, downloadImageUrl;
    private ProgressDialog loadingBar;

    private StorageReference productImagesRef;

    private DatabaseReference productRef;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        categoryName = getIntent().getExtras().get("category").toString();

        addNewProductButton = (Button) findViewById(R.id.add_new_product);
        inputProductImage = (ImageView) findViewById(R.id.select_product_image);
        inputProductName = (EditText) findViewById(R.id.product_name);
        inputProductDescription = (EditText) findViewById(R.id.product_description);
        inputProductPrice= (EditText) findViewById(R.id.product_price);

        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        loadingBar = new ProgressDialog(this);

        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }

        });
        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateProductData();
            }
        });
    }


    private void openGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK && data!=null)
        {
            imageUri = data.getData();
            inputProductImage.setImageURI(imageUri);
        }
    }

    private void validateProductData() {

        pName = inputProductName.getText().toString();
        description = inputProductDescription.getText().toString();
        price = inputProductPrice.getText().toString();

        if(imageUri==null){

            Toast.makeText(this,"Product image is mandatory!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){

            Toast.makeText(this,"Please, write product description..",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pName)){

            Toast.makeText(this,"Please, write product name..",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(price)){

            Toast.makeText(this,"Please, write product price..",Toast.LENGTH_SHORT).show();
        }
        else{

            storeProductInformation();
        }

    }

    private void storeProductInformation() {

        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Please wait, while we are adding the new product..");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd ,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate+saveCurrentTime;

        final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment()+productRandomKey+ ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this,"Product image uploaded Successfully!",Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                         if(!task.isSuccessful())
                         {
                             throw task.getException();

                         }

                         downloadImageUrl = filePath.getDownloadUrl().toString();
                         return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if(task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this,"got Product image Url successfully..",Toast.LENGTH_SHORT).show();

                            saveProductInfoToDatabasse();
                        }
                    }
                });
            }
        });
    }
    private void   saveProductInfoToDatabasse(){

        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("pId",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",description);
        productMap.put("price",price);
        productMap.put("name",pName);
        productMap.put("category",categoryName);
        productMap.put("image",downloadImageUrl);

        productRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminAddNewProductActivity.this,  AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this,"Product is added successfully!",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this,"Error: "+ message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
