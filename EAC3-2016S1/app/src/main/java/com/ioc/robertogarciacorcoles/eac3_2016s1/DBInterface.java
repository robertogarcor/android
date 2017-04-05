package com.ioc.robertogarciacorcoles.eac3_2016s1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Roberto on 22/10/2016.
 * Classe model DBInteface per crear BD i interactuar amb la DB Sqlite
 */
public class DBInterface {

    //Constants

    public static final String CLAU_ID = "_id";
    public static final String CLAU_TITOL = "titol";
    public static final String CLAU_DESCRIPCIO = "descripcio";
    public static final String CLAU_COORD_LATITUD = "coordLatitut";
    public static final String CLAU_COORD_LONGITUD = "coordLongitut";
    public static final String CLAU_TELEFON = "telefon";
    public static final String CLAU_DATA = "dataPublicacio";

    public static final String TAG = "DBInterface";

    public static final String DB_NOM = "WorkOffers.sqlite";
    public static final String DB_TAULA = "offers";
    public static final int VERSIO = 1;

    public static final String DB_CREATE =
            "create table " + DB_TAULA + "( "
            + CLAU_ID + " integer primary key autoincrement, "
            + CLAU_TITOL + " text not null, "
            + CLAU_DESCRIPCIO + " text not null, "
            + CLAU_COORD_LATITUD + " real not null, "
            + CLAU_COORD_LONGITUD + " real not null, "
            + CLAU_TELEFON + " integer not null, "
            + CLAU_DATA + " text not null"
            + ");";


    private final Context context;
    private DBHelper helper;
    private SQLiteDatabase db;


    /**
     * Constructor Crear Objecte DBHelper
     * Guardar en una variable el context en què s’està executant la classe;
     * @param con context passat com a parametre
     */
    public DBInterface(Context con) {
        this.context = con;
        helper = new DBHelper(context);
    }

    /**
     * Metode que obre la BD
     * @return la intancia de la BD per operar
     * @throws SQLException es delega qualsevol error I/O
     */
    public DBInterface obre() throws SQLException {
        db = helper.getWritableDatabase();
        return this;
    }

    /**
     * Metode que tanca la BD
     */
    public void tanca(){
        helper.close();
    }


    /**
     * Metode que inserir una oferta
     * @param title titol de la oferta
     * @param description descripcio de la oferta
     * @param coordenadaLatitud coordenada latitud per calcul posicio
     * @param coordenadaLongitud coordenada longitut per calcul posicio
     * @param telefon telefon de la oferta
     * @param data data publicacio de la oferta
     * @return rsultat la insercio de la oferta a DB
     * @throws SQLException es delega qualsevol error I/O
     */
    public long insereixOffers(String title, String description,
                               Double coordenadaLatitud, Double coordenadaLongitud,
                               Integer telefon, String data) throws SQLException {


        //Pujada de valors per la seva inserció
        ContentValues initValues = new ContentValues();
        initValues.put(CLAU_TITOL, title);
        initValues.put(CLAU_DESCRIPCIO, description);
        initValues.put(CLAU_COORD_LATITUD, coordenadaLatitud);
        initValues.put(CLAU_COORD_LONGITUD, coordenadaLongitud);
        initValues.put(CLAU_TELEFON, telefon);
        initValues.put(CLAU_DATA, data);

        // Retornem resultat de la insercio ID ok -1 fail
        return db.insert(DB_TAULA,null,initValues);

    }

    /**
     * Metode que retorna totes les ofertes
     * @return totes les ofertes de la BD
     * @throws SQLException es delega qualsevol error I/O
     */
    public Cursor obtenerTotesOfertes() throws SQLException {
        String [] columnes = new String[]{CLAU_ID, CLAU_TITOL, CLAU_DESCRIPCIO,
                CLAU_COORD_LATITUD, CLAU_COORD_LONGITUD, CLAU_TELEFON, CLAU_DATA};
        return db.query(DB_TAULA, columnes, null, null, null, null, null);

    }

    /**
     * Metode que esborrar tots els registres de la taula offers
     * @throws SQLException es delega qualsevol error I/O
     */
    public void esborrarRegistresTaulaOffers() throws SQLException {
        db.execSQL("DELETE FROM " + DB_TAULA);

    }

    /**
     * Metode que esborrar la Taula offers de la BD
     * @throws SQLException es delega qualsevol error I/O
     */
    public void esborrarTaulaOffers() throws SQLException {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TAULA);
    }


}
