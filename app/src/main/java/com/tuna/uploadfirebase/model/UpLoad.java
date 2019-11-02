package com.tuna.uploadfirebase.model;

public class UpLoad {
    private String Id,Images,Title,Address,Description,Phone;
    private Double price,Arearoom;

    public UpLoad() {
    }

    public UpLoad(String images, String title, String address, String description, String phone, Double price, Double arearoom) {
        Images = images;
        Title = title;
        Address = address;
        Description = description;
        Phone = phone;
        this.price = price;
        Arearoom = arearoom;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getArearoom() {
        return Arearoom;
    }

    public void setArearoom(Double arearoom) {
        Arearoom = arearoom;
    }
}
