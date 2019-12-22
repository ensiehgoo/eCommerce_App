package com.example.ecommerce_demo.Model;

public class Products {
    private String name, description, price, category, image, date, pid,time;

    public Products() {
    }

    public Products(String name, String description, String price, String category, String image, String date, String pid, String time) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.image = image;
        this.date = date;
        this.pid = pid;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    public String getDate() {
        return date;
    }

    public String getPid() {
        return pid;
    }

    public String getTime() {
        return time;
    }
}
