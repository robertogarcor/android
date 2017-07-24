/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.robertogarcia.listviewdinamicjdbc.db;

import java.sql.Connection;
import java.sql.ResultSet;


/**
 *
 * @author Roberto García Córcoles
 * @version 17/07/2017/A
 *
 * Example interface persistence JDBC - mysql
 */
public interface GestorPersistenciaJDBC {

    /**
     * Method to create a connection with mysql server
     * @return the connection established
     */
    public abstract Connection connetion();

    /**
     * Method to close a connection
     * @param conn the connection
     */
    public abstract void closeConnection(Connection conn);

    /**
     * Method to load data from database (android) table products
     * @param conn the connection established
     * @param limit limit data to load
     * @param offset the range from which to load the data
     * @return the data of the database
     */
    public abstract ResultSet loadData(Connection conn, Integer limit, Integer offset);

    /**
     * Method to count number de records from table products
     * @param conn the connection established
     * @return the number of products
     */
    public abstract Integer countData(Connection conn);

    /**
     * Method to close resultSet
     * @param rs the ressultSet to close
     */
    public abstract void closeResultSet(ResultSet rs);
}
