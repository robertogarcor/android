package com.ioc.robertogarciacorcoles.eac2_2016s1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;


/**
 * Created by Roberto on 07/10/2016.
 */
public class DBInterface {

    //Constants

    public static final String CLAU_ID = "_id";
    public static final String CLAU_TITOL = "titol";
    public static final String CLAU_URL = "url";
    public static final String CLAU_AUTOR = "autor";
    public static final String CLAU_DESCRIPCIO = "descripcio";
    public static final String CLAU_DATA = "dataPublicacio";
    public static final String CLAU_CATEGORIA = "categoria";
    public static final String CLAU_URLIMG = "urlImg";
    public static final String CLAU_RSS = "seccionRss";

    public static final String TAG = "DBInterface";

    public static final String BD_NOM = "DBNoticies.sqlite";
    public static final String BD_TAULA ="news";
    public static final int VERSIO = 1;

    public static final String BD_CREATE =
            "create table " + BD_TAULA + "( "
                    + CLAU_ID + " integer primary key autoincrement, "
                    + CLAU_TITOL + " text not null, "
                    + CLAU_URL + " text, "
                    + CLAU_AUTOR + " text not null, "
                    + CLAU_DESCRIPCIO + " text not null, "
                    + CLAU_DATA + " text not null, "
                    + CLAU_CATEGORIA + " text, "
                    + CLAU_URLIMG + " text, "
                    + CLAU_RSS + " text"
                    + ");";

    private final Context context;
    private HelperBD helper;
    private SQLiteDatabase bd;


    /**
     * Constructor Crear Objecte AjudaBD
     * Guardar en una variable el context en què s’està executant la classe;
     * @param con context passat com a parametre
     */
    public DBInterface(Context con) {
        this.context = con;
        helper = new HelperBD(context);
    }


    /**
     * Metode que obre la BD
     * @return la intancia de la BD per operar
     * @throws SQLException es delega qualsevol error I/O
     */
    public DBInterface obre() throws SQLException {
        bd = helper.getWritableDatabase();
        return this;

    }

    /**
     * Metode que tanca la BD
     */
    public void tanca() {
        helper.close();
    }

    /**
     * Metode que inserir una noticia (news)
     * @param title titol de la noticia
     * @param urlNews url enllac a la noticia
     * @param author autor de la noticia
     * @param description descripcio de la noticia
     * @param data data publicacio de la notica
     * @param categories Llista categories de la noticia
     * @param urlImgNews url enllac a image de la noticia
     * @param rss refencia a la secció RSS de les noticies
     * @return la insercio de la noticia a BD
     * @throws SQLException es delega qualsevol error I/O
     */
    public long insereixNews(String title, String urlNews,
                             String author, String description, String data,
                             List<String> categories, String urlImgNews, String rss) throws SQLException {

        // Convertim List categories en array > String separator ","
        String[] acats = categories.toArray(new String[categories.size()]);
        String scats = TextUtils.join(",",acats);

        // Pujada de valor per la seva insercio
        ContentValues initialValues = new ContentValues();
        initialValues.put(CLAU_TITOL, title);
        initialValues.put(CLAU_URL, urlNews);
        initialValues.put(CLAU_AUTOR, author);
        initialValues.put(CLAU_DESCRIPCIO, description);
        initialValues.put(CLAU_DATA, data);
        initialValues.put(CLAU_CATEGORIA, scats);
        initialValues.put(CLAU_URLIMG, urlImgNews);
        initialValues.put(CLAU_RSS, rss);

        // Retornem resultat de la insercio ID ok -1 fail
        return bd.insert(BD_TAULA, null, initialValues);

    }

    /**
     * Metode que retorna totes les Noticies (news)
     * @return totes les noticies
     * @throws SQLException es delega qualsevol error I/O
     */
    public Cursor obtenirTotesLesNews() throws SQLException {
        return bd.query(BD_TAULA,
                new String[] {CLAU_ID,CLAU_TITOL,CLAU_URL, CLAU_AUTOR,
                        CLAU_DESCRIPCIO,CLAU_DATA, CLAU_CATEGORIA, CLAU_URLIMG, CLAU_RSS},
                        null, null, null, null, null);
    }


    /**
     * Metode que esborrar tots els registres de la taula news (Noticies)
     * @throws SQLException es delega qualsevol error I/O
     */
    public void esborrarRegistresTaulaNews() throws SQLException {
        bd.execSQL("DELETE FROM " + BD_TAULA );

    }

    /**
     * Metode que esborrar la Taula de la BD
     * @throws SQLException es delega qualsevol error I/O
     */
    public void esborrarTaula() throws SQLException {
        bd.execSQL("DROP TABLE IF EXISTS " + BD_TAULA);
    }


    /***********************************************************************************/
    /*********************** CLASSE HELPERBD (Es pot separar ) *************************/
    /***********************************************************************************/


    /**
     * classe que serveix d’ajuda per gestionar la creació
     * de bases de dades i gestió de versions.
     */
    private static class HelperBD extends SQLiteOpenHelper {
        HelperBD(Context con) {
            super(con, BD_NOM, null, VERSIO);
        }

        /**
         * Metode que permet crea la BD
         * @param db objecte bd
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(BD_CREATE);
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
            Log.w(TAG, "Actualitzant Base de dades de la versió" + VersioAntiga
                    + " a " + VersioNova + ". Destruirà totes les dades");
            db.execSQL("DROP TABLE IF EXISTS " + BD_TAULA);

            onCreate(db);
        }
    }









}
