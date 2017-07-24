package com.example.robertogarcia.listviewdinamic.model;


/**
 * Created by Roberto on 08/07/2017.
 */

public class Product {

    private Integer id;
    private String name;
    private Float price;
    private Integer amount;
    private String description;
    private Boolean active;


    public Product(){};

    public Product(Integer id, String name, Float price, Integer amount, String description, Boolean active) {
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.description = description;
        this.active = active;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
