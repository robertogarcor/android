/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.robertogarcia.listviewdinamicjdbc.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Roberto García Córcoles
 * @version 17/07/2017/A
 *
 * Class management mysql - JDBC
 */
public class GestorJDBC implements GestorPersistenciaJDBC {

    // Credentials - Url resource - pattern JDBC
    // Change credentials (USER,PASSWORD,URL) by Database Server mysql
    protected static final String DRIVER = "com.mysql.jdbc.Driver";
    protected static final String USER = "root";
    protected static final String PASSWORD = "";
    protected static final String URL = "jdbc:mysql://192.168.56.1:3306/android";
    protected Connection connection;

    // Open connection mysql
    @Override
    public Connection connetion(){
        try {
            connection = null;
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            if ( !connection.isClosed()) {
                Utils.setPsSelAllProducts(connection.prepareStatement(Utils.getSqlSelAllProducts()));
                Utils.setPsCountProducts(connection.prepareStatement(Utils.getSqlCountProducts()));
                System.out.println("Connection OK");

            }
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Fail Driver or connection");
            ex.printStackTrace();
        }
        return connection;
    }

    // Close connection mysql
    @Override
    public void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            conn = null;
        }
    }

    // Load paged data from mysql products (select by limit and offset products)
    @Override
    public ResultSet loadData(Connection conn, Integer limit, Integer offset) {
        PreparedStatement ps = Utils.getPsSelAllProducts();
        ResultSet rs = null;
        try {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    @Override
    public Integer countData(Connection conn) {
        PreparedStatement ps = Utils.getPsCountProducts();
        ResultSet rs = null;
        Integer count = 0;
        try {
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public void closeResultSet(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
