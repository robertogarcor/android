package com.ioc.robertogarciacorcoles.eac3_2016s1;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Apartat 7 millores i funcionalitat extra
 *
 * Maps
 *
 * S'activat els botons de zoom al Maps tant a Vista General com Vista Detall
 * per millor facilitat de control del zoom - botons (+/-)
 *
 * A l'activitat ShowOfferActivity s'ha annexat un item de menu a la toolbar
 * per poder canviar la vista detall del Maps entre els tipus de vistes de
 * mapes que hi ha a la API Google (Normal, Terrain, Satellite, Hibrid)
 *
 * S'ha fet un estil propi per la app en color verd
 *
 */
public class MainActivity extends AppCompatActivity {

    // Varible DBInterface
    private DBInterface db;

    // Listes Ofertes i titolsDescripcio Ofertes
    protected static List<OfertaTreball> llOffersWork;
    private List<String> llTtDescripcioOffersWork;

    //List View, ProgressBar i Adaptador
    private ListView llViewTlDescriptionOffersWork;
    private ArrayAdapter<String> adplistTtDescription;
    private ProgressBar prgBar;


    /**
     * Metode per crear la activity
     * Iniciar/obtenir elements activity
     * @param savedInstanceState paquet d'estat de la activity per possible canvi
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Mapatge list View offers i progress Bar
        llViewTlDescriptionOffersWork = (ListView) findViewById(R.id.lwTtDescriptionOffers);
        prgBar = (ProgressBar) findViewById(R.id.prgBarTasca);
        prgBar.getIndeterminateDrawable().setColorFilter(Color.rgb(125, 150, 140), PorterDuff.Mode.SRC_IN);

        //Inicialitzen llistes offers i llista description offers
        llOffersWork = new ArrayList<OfertaTreball>();
        llTtDescripcioOffersWork = new ArrayList<String>();

        //Canviar titol activity
        this.setTitle(R.string.title_mainActivity);

        // Instanciem la BD
        db = new DBInterface(this);

        // Adaptador de la ListView
        adplistTtDescription = new ArrayAdapter<String>(MainActivity.this,
               R.layout.llista_ttdescription, R.id.tvTtDescription, llTtDescripcioOffersWork);

        // Associem la llistView per ItemClickListener
        seleccioOfferLlista(llViewTlDescriptionOffersWork);

        // Executem la insercio i carregar de Ofertes al sistema
        new InsertarCarregarOfertesDB().execute();

    }


    /**
     * Metode per crear opcion del menu de la Toolbar
     * @param menu els items del recurs xml del menu associat
     * @return boolean true/false si ha element per mostrar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem profile = menu.findItem(R.id.action_profile);
        MenuItem mapsmap = menu.findItem(R.id.action_mapsmap);

        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Metode encarregat de la seleccio dels items de menu de la Toolbar per
     * realitzar la accio associada
     * @param item seleccionat del menu
     * @return boolea true/false si s'ha produit acció o no del elements
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_profile:
                Intent showProfile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(showProfile);
                return true;
            case R.id.action_mapsmap:
                Intent showMaps = new Intent(MainActivity.this, MapsMapActivity.class);
                startActivity(showMaps);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Metode Implementacio per mostrar la Oferta de treball
     * amb un click del item listView seleccionat
     * @param llistaOffers la listView de titols/descripcio ofertes
     */
    private void seleccioOfferLlista(final ListView llistaOffers) {
        llistaOffers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selItem = llistaOffers.getItemAtPosition(position).toString();

                OfertaTreball oferta = obtenirOfferList(selItem);

                Intent showOffer = new Intent(MainActivity.this, ShowOfferActivity.class);
                Bundle extra = new Bundle();
                extra.putString("idOfferSellist", oferta.getIdBD().toString());
                showOffer.putExtras(extra);
                startActivity(showOffer);
            }
        });
    }

    /**
     * Metode per obtenir la oferta corresponent amb titol listTitols segon seleccio previa
     * @param titolOfferLlista el titol de la oferta seleccionat previament
     * @return la oferta de treball corresponent
     */
    private OfertaTreball obtenirOfferList(String titolOfferLlista) {
        OfertaTreball offer = null;
        for (OfertaTreball in : llOffersWork) {
            String ttdescripcio = in.getTitol() + "\n\n" + in.getDescripcio();
            if (ttdescripcio.equals(titolOfferLlista)) {
                offer = in;
            }
        }
        return offer;
    }


    /**
     * Metodo per mostrar un missagte Toast a la interface (activity)
     * @param s Missatge a mostrar
     */
    private void missage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }



    /************************************************************************************/
    /** METODES PER LA CARREGAR I DESCARREGAR DE DADES A BD I EL SISTEMA PER TREBALLAR **/
    /************************************************************************************/

    /**
     * Implementació per la carregar de dades a la BD i descarregar de dades BD al sistema
     * per treballar
     */
    private class InsertarCarregarOfertesDB extends AsyncTask<Void, Void , List<OfertaTreball>> {

        @Override
        protected void onPreExecute() {
            prgBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<OfertaTreball> doInBackground(Void... params) {

            db.obre();

            db.esborrarRegistresTaulaOffers();

            insertarOffersDB();

            llOffersWork = obtenirOffersDB();

            db.tanca();

            return llOffersWork;
        }

        @Override
        protected void onPostExecute(List<OfertaTreball> ofertesTreball) {

            obtenirTitolDescripioOffersList(ofertesTreball);

            //Omplir adaptador amb titol i descripcio ofertes
            adplistTtDescription.notifyDataSetChanged();
            llViewTlDescriptionOffersWork.setAdapter(adplistTtDescription);

            prgBar.setVisibility(View.GONE);

        }
    }

    /**
     * Metode que permet inserir ofertes a la Base de Dates Offers
     */
    private void insertarOffersDB() {

        //IdBD es un camp autoincrementable no necessitem fer la inserció

        db.insereixOffers("Offer_#1", "Lorem Ipsum is simply dummy text of the printing and" +
                " typesetting industry. Lorem Ipsum has been the industry's standard dummy" +
                " text ever since the 1500s, when an unknown printer took a galley of type" +
                " and scrambled it to make a type specimen book. It has survived not only" +
                " five centuries, but also the leap into electronic typesetting, remaining" +
                " essentially unchanged. Many desktop publishing packages and web page editors" +
                " now use Lorem Ipsum as their default model text, and a search for" +
                " 'lorem ipsum' will uncover many web sites still in their infancy." +
                " Many desktop publishing packages and web page editors" +
                " now use Lorem Ipsum as their default model text, and a search for" +
                " 'lorem ipsum' will uncover many web sites still in" +
                " their infancy.", 41.393176, 2.151431, 666123456, "2016-10-01");

        db.insereixOffers("Offer_#2", "Many desktop publishing packages and web page editors" +
                " now use Lorem Ipsum as their default model text, and a search for" +
                " 'lorem ipsum' will uncover many web sites still in" +
                " their infancy. It is a long established fact that a reader will be" +
                " distracted by the readable content of a page when looking" +
                " at its layout.", 41.355321, 2.122807, 666222111, "2016-05-15");

        db.insereixOffers("Offer_#3", "It is a long established fact that a reader will be" +
                " distracted by the readable content of a page when looking at its layout." +
                " The point of using Lorem Ipsum is that it has a more-or-less normal" +
                " distribution of letters, as opposed to using 'Content here, content here'," +
                " making it look like readable English. There are many variations of passages" +
                " of Lorem Ipsum available, but the majority have suffered alteration in some" +
                " form, by injected humour, or randomised words which don't look even slightly" +
                " believable. Many desktop publishing packages and web page editors" +
                " now use Lorem Ipsum as their default model text, and a search for" +
                " 'lorem ipsum'.", 41.360062, 2.108664, 666444555, "2016-07-10");

        db.insereixOffers("Offer_#4", "There are many variations of passages of Lorem Ipsum" +
                " available, but the majority have suffered alteration in some form, by" +
                " injected humour, or randomised words which don't look even slightly" +
                " believable. Many desktop publishing packages and web page editors" +
                " now use Lorem Ipsum as their default model text, and a search for" +
                " 'lorem ipsum' will uncover many web sites still in" +
                " their infancy.", 41.387469, 2.123681, 666888777, "2016-04-23");

    }


    /**
     * Mètode que obte les dades de les ofertes de treball de la BD
     * i crea una llista amb les ofertes
     * @return la llista de objectes OfertaTreball
     */
    private List<OfertaTreball> obtenirOffersDB() {
        Cursor ofertes = db.obtenerTotesOfertes();
        if (ofertes.moveToFirst()) {
            do {
                llOffersWork.add(new OfertaTreball(ofertes.getInt(0),ofertes.getString(1),ofertes.getString(2),
                                        ofertes.getDouble(3),ofertes.getDouble(4),
                                        ofertes.getInt(5),ofertes.getString(6)));
            }while (ofertes.moveToNext());
        }
        return llOffersWork;
    }

    /**
     * Mètode per obtenir el titol + descripcio de les ofertes de treball i
     * omplir una llista de ofertes amb les dades corresponents per inflate la listView.
     * @param llistaOfertes la llista de totes les ofertes de treball
     */
    private void obtenirTitolDescripioOffersList(List<OfertaTreball> llistaOfertes) {
        for (OfertaTreball of : llistaOfertes) {
           llTtDescripcioOffersWork.add(of.getTitol() + "\n\n" + of.getDescripcio() );
        }
    }

}


