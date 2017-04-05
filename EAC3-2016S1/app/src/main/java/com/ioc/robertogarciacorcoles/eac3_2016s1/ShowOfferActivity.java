package com.ioc.robertogarciacorcoles.eac3_2016s1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ShowOfferActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Variable Maps, llistes List i Map i variables Intent
    private GoogleMap mMap;
    private Map<String,OfertaTreball> llmarketsOffersMap = MapsMapActivity.marketsOffersMap;
    private List<OfertaTreball> llviewOffersWork = MainActivity.llOffersWork;
    private String marketSel;
    private String idOfferSelList;

    // Variable propietats Oferta treball
    private TextView titol,descripcio,tel,data;
    private Double lat,lon;

    /**
     * Mètode per la creacio de la activitat
     * @param savedInstanceState estat de la activitat si ha estat guardada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_offer);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.offerMap);
        mapFragment.getMapAsync(this);

        // Mapatge Witges
        titol = (TextView) findViewById(R.id.tvtitolOffer);
        descripcio = (TextView) findViewById(R.id.tvdescripcionOffer);
        tel = (TextView) findViewById(R.id.tvphoneOffer);
        data = (TextView) findViewById(R.id.tvdataOffer);

        obtenirDadesIntent();

        /**
         * Ens asegurem de les dades del intent enviades que no siguin nules
         * per carregar les dades que necessitem
         */
        if (marketSel != "" && marketSel != null) {
            obtenirDadesMarketMaps(llmarketsOffersMap);
        } else {
            obtenirDadesItemLlistOffers(llviewOffersWork);
        }

    }

    /**
     * Metodo per obtenir dades intent MapsMapActivity referencia market select
     */
    private void obtenirDadesIntent(){
        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            marketSel = extras.getString("idMarket");
            idOfferSelList = extras.getString("idOfferSellist");
        }
    }

    /**
     * Metode que permet obtenir de la llista Map la marca de oferta de treball
     * corresponent segons el id que hem passat amb l'intent de MapsMapActivity
     * @param llistaOffersMap la llista Map amb les key(id marker,oferta treball)
     */
    private void obtenirDadesMarketMaps(Map llistaOffersMap) {
        Iterator it = llistaOffersMap.keySet().iterator();
        while (it.hasNext()) {
            String idMarket = (String) it.next();
            OfertaTreball offer = (OfertaTreball) llistaOffersMap.get(idMarket);
            if (idMarket.equals(marketSel)) {
                titol.setText(offer.getTitol());
                descripcio.setText(offer.getDescripcio());
                tel.setText(offer.getTelefon().toString());
                data.setText(offer.getDataPublicacio());
                lat = offer.getLatitud();
                lon = offer.getLongitud();
            }
        }
    }

    /**
     * Metode que permet obtenir de la llista de ofertes (MainActivity)
     * la oferta de treball corresponent segons el id de la oferta asocciat
     * que hem passat amb l'intent de MainActivity
     * @param llOffers la llista de ofertes Objectes OfertaTreball
     */
    private void obtenirDadesItemLlistOffers(List<OfertaTreball> llOffers) {
        for (OfertaTreball offer : llOffers) {
            if (idOfferSelList.equals(offer.getIdBD().toString()) ) {
                titol.setText(offer.getTitol());
                descripcio.setText(offer.getDescripcio());
                tel.setText(offer.getTelefon().toString());
                data.setText(offer.getDataPublicacio());
                lat = offer.getLatitud();
                lon = offer.getLongitud();
            }
        }
    }

    /**
     * Metode per carregar el maps
     * @param googleMap el Maps
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        marketOfferLocation();
    }

    /**
     * Metode per afegir la marker de la oferta al fragment (vista detall) del maps
     */
    private void marketOfferLocation() {
        LatLng posicio = new LatLng(lat,lon);

        Marker marcaOferta = mMap.addMarker(new MarkerOptions()
                .title(titol.getText().toString())
                .position(posicio)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tagmaps))
        );

        marcaOferta.showInfoWindow();
        animateCameraZoom(posicio);
    }

    /**
     * Metode per donar animacio de la camera zoom google maps
     * @param posicio la posicio desti latitud/longitud
     */
    private void animateCameraZoom(LatLng posicio) {
        // Constructor de les opcions de animateCamera
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(posicio)
                .zoom(15)
                .bearing(30)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }



    /*******************************************************************/
    /*********** Ampliacio Aparta7 - Vistes tipus mapes ****************/
    /*******************************************************************/


    /**
     * Metode per crear Items a la toolbar
     * @param menu els items del icon desplegable toolbar
     * @return boolean true/false si ha element per mostrar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_showoffer, menu);

        MenuItem mapViewNormal = menu.findItem(R.id.action_mapNormal);
        MenuItem mapViewTerra = menu.findItem(R.id.action_mapTerrain);
        MenuItem mapViewSatellite = menu.findItem(R.id.action_mapSatellite);
        MenuItem mapViewHibrid = menu.findItem(R.id.action_mapHybrid);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Metode encarregat de la seleccio dels items de Toolbar per
     * realitzar la accio associada (Canvia la vista del Maps)
     * @param item seleccionat del menu
     * @return boolea true/false si s'ha produit acció o no del elements
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_mapNormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.action_mapTerrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.action_mapSatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.action_mapHybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
