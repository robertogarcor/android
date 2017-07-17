package com.example.robertogarcia.utils;

import com.example.robertogarcia.model.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Roberto on 14/07/2017.
 */

public class DataSource {

        // DataSource List of products data pattern (id, name, price, amount, description, active)

        private List<Product> products = Arrays.asList(
                new Product(1, "cinta abdesiva", 10.5f, 6, "Cinta abdesiva", true),
                new Product(2, "alicates", 12.75f, 10, "Alicates verdes", true),
                new Product(3, "destornillador estrella", 4.6f, 0, "destornillador estrella verde", false),
                new Product(4, "destornillador normal", 4.4f, 10, "destornillador normal verde", true),
                new Product(5, "cable verde", 5.7f, 15, "cable verde", true),
                new Product(6, "cable rojo", 5.7f, 10, "cable rojo", true),
                new Product(7, "cable amarillo", 5.9f, 0, "cable amarillo", false),
                new Product(8, "partillo pequeño", 10.4f, 3, "martillo pequeño madera", true),
                new Product(9, "tornillo grande", 2.5f, 100, "tornillo grande acero", true),
                new Product(10, "tornillo pequeño", 1.7f, 200, "tornillo pequeño acero", true),
                new Product(11, "tornillo mediano", 2.0f, 150, "tornillo mediano", true),
                new Product(12, "martillo mediano", 12.7f, 15, "martillo mediano madera", true),
                new Product(13, "tenazas", 6.0f, 12, "tenazas de acero", true),
                new Product(14, "gafas", 3.2f, 0, "gafas de plastico", false),
                new Product(15, "pegamento", 2.0f, 50, "pegamento de contacto", true),
                new Product(16, "casco", 10.3f, 7, "casco de obra", true),
                new Product(17, "chaqueta", 23.4f, 20, "chaqueta de obra", true),
                new Product(18, "guantes", 7.75f, 0, "guantes de obra", false),
                new Product(19, "botas", 35.0f, 50, "botas de obra", true),
                new Product(20, "macho", 50.4f, 3, "macho de acero fundido", true),
                new Product(21, "anclajes", 3.5f, 75, "anchajes de acero", true),
                new Product(22, "mortero quimico", 32.0f, 40, "mortero quimico en tubo", true),
                new Product(23, "pala", 24.2f, 0, "pala de acero y madera", true),
                new Product(24, "clavos acero", 5.0f, 120, "clavos de acero", true),
                new Product(25, "clavos hierro", 3.0f, 0, "clavos de hierro", false),
                new Product(26, "tuercas acero", 5.2f, 130, "tuercas de acero", true),
                new Product(27, "arandela hierro", 3.1f, 300, "arandela de hierro", true),
                new Product(28, "arandela goma", 1.1f, 0, "arandela de goma", true),
                new Product(29, "arandela acero", 5.1f, 200, "arandela de acero", true),
                new Product(30, "llave inglesa", 15.8f, 30, "llave inglesa acero", true),
                new Product(31, "led rojo", 0.5f, 500, "led de color rojo", true),
                new Product(32, "lef verde", 0.5f, 300, "led de color verde", false),
                new Product(33, "cinturon", 25.1f, 40, "cinturon de herramientas", true),
                new Product(34, "metro", 6.6f, 40, "metro de hierro", true),
                new Product(35, "plomada", 20.0f, 10, "plomada de acero", true),
                new Product(36, "cuerda", 10.0f, 42, "cuerda de nylon", true));


    /**
     * Add datasource products to list
     * @param limit limit of products to add
     * @param offset index to init list products
     * @return Return list of products
     */
        public List<Product> getMoreProductData(Integer limit , Integer offset) {
                List<Product> rs = new ArrayList<>();
                Integer length = offset+limit;

                if (length > products.size()){
                    length = products.size();
                }

                for (int pr = offset; pr <= length - 1; pr++){
                        rs.add(products.get(pr));
                        }

                return rs;
        }

    // Getter and Setters

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
};
