package com.example.ecommerce_demo.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_demo.Interface.ItemClickListener;
import com.example.ecommerce_demo.R;

import java.util.Objects;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView txtProductName, txtProductDescription,txtProductPrice;
    public ImageView imageView;
    public  ItemClickListener listener;


    public ProductViewHolder(View itemView){
        super(itemView);
        Log.d("Product", "Hello There");
            imageView = itemView.findViewById(R.id.product_image);
            txtProductName = itemView.findViewById(R.id.product_name);
            txtProductDescription = itemView.findViewById(R.id.product_description);
            txtProductPrice = itemView.findViewById(R.id.product_price);



    }
    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
       listener.onClick(view,getAdapterPosition(),false);
        }
}
