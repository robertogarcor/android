package com.example.robertogarcia.listviewdinamicjdbc.db;

import java.sql.PreparedStatement;

/**
 *
 * @author Roberto García Córcoles
 * @version 17/07/2017/A
 *
 * Class utils for preparedStatements and querys SQL
 */

public class Utils {

    private static PreparedStatement psSelAllProducts;
    private static String sqlSelAllProducts = "SELECT * FROM productos LIMIT " + "?" + " OFFSET " + "?";

    private static PreparedStatement psCountProducts;
    private static String sqlCountProducts = "SELECT COUNT(*) FROM productos";


    // Getters and Setters

    public static PreparedStatement getPsSelAllProducts() {
        return psSelAllProducts;
    }

    public static void setPsSelAllProducts(PreparedStatement psSelAllProducts) {
        Utils.psSelAllProducts = psSelAllProducts;
    }

    public static String getSqlSelAllProducts() {
        return sqlSelAllProducts;
    }

    public static void setSqlSelAllProducts(String sqlSelAllProducts) {
        Utils.sqlSelAllProducts = sqlSelAllProducts;
    }

    public static PreparedStatement getPsCountProducts() {
        return psCountProducts;
    }

    public static void setPsCountProducts(PreparedStatement psCountProducts) {
        Utils.psCountProducts = psCountProducts;
    }

    public static String getSqlCountProducts() {
        return sqlCountProducts;
    }

    public static void setSqlCountProducts(String sqlCountProducts) {
        Utils.sqlCountProducts = sqlCountProducts;
    }
}
