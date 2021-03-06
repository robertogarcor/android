package com.example.robertogarcia.listviewdinamicjdbc.model;


/**
 * @author Roberto García Córcoles
 * @version 17/07/2017/A
 *
 * Class model products
 */

public class Product {

    private Integer id;
    private String name;
    private Float price;
    private Integer amount;
    private String description;
    private Integer active;


    public Product(){};

    public Product(Integer id, String name, Float price, Integer amount, String description, Integer active) {
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

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}
