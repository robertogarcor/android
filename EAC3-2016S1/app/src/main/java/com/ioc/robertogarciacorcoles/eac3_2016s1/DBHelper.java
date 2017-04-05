package com.ioc.robertogarciacorcoles.eac3_2016s1;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Roberto on 22/10/2016.
 *
 * Classe que serveix d’ajuda per gestionar la creació
 * de bases de dades i gestió de versions.
 */
public class DBHelper extends SQLiteOpenHelper {

    /**
     * Constructor de la classe DBHelper
     */
    public DBHelper(Context con) {

        super(con, DBInterface.DB_NOM, null, DBInterface.VERSIO);
    }

    /**
     * Metode que permet crea la BD
     * @param db objecte bd
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DBInterface.DB_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metode que permet fer actualizacions de la DB
     * @param db objecte bd
     * @param VersioAntiga integer versio antiga de la bd
     * @param VersioNova integer versio nova de la bd
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int VersioAntiga,
                          int VersioNova) {
        Log.w(DBInterface.TAG, "Actualitzant Base de dades de la versió" + VersioAntiga
                + " a " + VersioNova + ". Destruirà totes les dades");
        db.execSQL("DROP TABLE IF EXISTS " + DBInterface.DB_TAULA);

        onCreate(db);
    }

}








