package com.ioc.robertogarciacorcoles.eac3_2016s1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.LocationListener;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsMapActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener, OnInfoWindowClickListener {

    // Constant per referencia els permisos demanats
    private static final int MAPS_MAP_PERMISSION = 1;

    // Varibles de maps i llista ofertes per tractament de les markets ofertes
    protected static Map<String, OfertaTreball> marketsOffersMap;
    private List<OfertaTreball> llistOffersWork = MainActivity.llOffersWork;

    // Variables per tractament de ubicacio, maps, gestor provider i animació inicial
    private GoogleMap mMap;
    private double lat, lon;
    private LocationManager gestorLoc;
    private Marker markers;
    private boolean animacio = false;

    /**
     * Mètode per la creacio de la activitat
     * @param savedInstanceState estat de la activitat si ha estat guardada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Assignacio obtencio del servei de localització
        gestorLoc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Si versio skd < 23 no demanem permisos, cas contrari >= 23 si demanem permisos
        // En versions sdk inferior sembla que funciona sense aquesta comprovació
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /**
             * Demanar permis de acces a la posicio aproximada wifi/GPS si no ho tenim
             * Cas contrari creen el proveidor amb les opcions que volen i crident al LocationManager
             */
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&

                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, MAPS_MAP_PERMISSION);
            } else {
                Criteria opcions = criteriCercaProveidor();
                // Proveidor, temps actualizacio, variacio distancia, listener
                gestorLoc.requestLocationUpdates(gestorLoc.getBestProvider(opcions, true), 1000, 0, this);
            }
        } else {
            Criteria opcions = criteriCercaProveidor();
            gestorLoc.requestLocationUpdates(gestorLoc.getBestProvider(opcions, true), 1000, 0, this);
        }

    }


    /**
     * Mètode per establir un provider segons una opcions donades
     * @return objecte Criteria amb les opcions que volen
     */
    private Criteria criteriCercaProveidor() {
        Criteria options = new Criteria();
        options.setAccuracy(Criteria.ACCURACY_FINE);
        options.setPowerRequirement(Criteria.POWER_MEDIUM);
        options.setAltitudeRequired(false);
        options.setBearingRequired(false);
        options.setSpeedRequired(false);
        return options;
    }



    /**
     * Mètode reposta del resultat de la resposta del usuari a donar permis o no a localització
     * @param requestCode constant que identifica la acció
     * @param permissions Array del permisos demanants
     * @param grantResults el resultat de la resposta del usuari.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MAPS_MAP_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED
                        && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    // Codi si s'ha rebutjat
                    missage("Error, has d'acceptar el permís per poder continuar");
                    finish();
                } else {
                    // Codi si s'han acceptat els permissos
                    // En principi no fem res entra directament a la activity
                    return;
                }
                break;
        }
    }

    /**
     * Metodo per mostrar un missagte Toast a la interface (activity)
     * @param s Missatge a mostrar
     */
    private void missage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Afegim els control de zoom (btns +/-)
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Afegim el listener del maps
        mMap.setOnInfoWindowClickListener(this);

        afegirMarkesOffersMaps();
    }

    /**
     * Mètode per inserir les markers de les ofertes al maps
     * i afegirles a la llista Map per ser utilitzades
     */
    private void afegirMarkesOffersMaps() {
        marketsOffersMap = new HashMap<String, OfertaTreball>();
        for (OfertaTreball offer : llistOffersWork) {
            // Donem el valor correspoenent a la localitzacio segons offers
            lat = offer.getLatitud();
            lon = offer.getLongitud();

            // Creem un LatLng a partir de la posicio indicada
            LatLng posicio = new LatLng(lat, lon);

            //Creem i afegin el marcador a icon personalitzat i afegin oferta a Maps
            markers = mMap.addMarker(new MarkerOptions()
                    .title(offer.getTitol())
                    .position(posicio)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tagmaps))
            );

            marketsOffersMap.put(markers.getId(), offer);
            //Fem que es mostri la informacio (sense Click)
            markers.showInfoWindow();
        }

    }


    /**
     * Metode per donar animmacio de la camera zoom google maps
     * @param posicio la posicio desti latitud/longitud
     */
    private void animateCameraZoom(LatLng posicio) {
        // Constructor de les opcions de animateCamera
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(posicio)        // La meva posicio unicial
                .zoom(11)               // Nivel de zoom final
                .bearing(60)            // Posicio de la camera final rotacio 0º perpendicular +60º (sentit contrari a rellotge)
                .tilt(30)               // Inclinacio de la camera a 30º
                .build();               // Construeix el paquet amb les opcionns animateCamera
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     *
     * Metode per actualitzar la meva posicio si es detecta una canvi
     * @param location la localització actual latitud/longitud
     */
    @Override
    public void onLocationChanged(Location location) {

        lat = location.getLatitude();
        lon = location.getLongitude();
        LatLng posicio = new LatLng(lat, lon);

        if (mMap != null) {
            if (markers.getTitle().equals("You're here!")) {
                markers.setPosition(posicio);
                //missage("Actualizat posicio");

            } else {
                MarkerOptions optMyMarker = new MarkerOptions();
                optMyMarker.title("You're here!");
                optMyMarker.position(posicio);
                optMyMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_youtagmap));
                markers = mMap.addMarker(optMyMarker);
                // Animacio inicial al obrir Maps General
                if (!animacio) {
                    animateCameraZoom(posicio);
                    animacio = true;
                }
            }
            markers.showInfoWindow();
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //No fem res
    }


    @Override
    public void onProviderEnabled(String provider) {
        //No fem res
    }


    @Override
    public void onProviderDisabled(String provider) {
        //No fem res
    }

    /**
     * Metode listener de les markets creades al maps
     * @param marker la marker que ha sigut seleccionada al maps
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        // Si click a mymarker
        if (marker.getTitle().equals("You're here!")) {
            //No fem res
            //missage("Soc aqui");
            return;

        } else {
            // Si click a markers offers (les altres marques del maps)
            Intent markerOferta = new Intent(this, ShowOfferActivity.class);
            Bundle extra = new Bundle();
            extra.putString("idMarket", marker.getId());
            markerOferta.putExtras(extra);
            startActivity(markerOferta);
        }
    }

    /**
     * En onPause aturem el Listener les actualizacions de la posicio
     * Es necessari confirmar que tenim el permisos adequats
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                gestorLoc.removeUpdates(this);
            }
        } else {
            gestorLoc.removeUpdates(this);
        }
    }

    /**
     * En onResume iniciem un altre vegada el Listener de les actualizacions de la posicio
     * Es necessari confirmar que tenim el permisos adequats
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                Criteria opcions = criteriCercaProveidor();
                gestorLoc.requestLocationUpdates(gestorLoc.getBestProvider(opcions, true), 1000, 0, this);
            }
        } else {
            Criteria opcions = criteriCercaProveidor();
            gestorLoc.requestLocationUpdates(gestorLoc.getBestProvider(opcions, true), 1000, 0, this);
        }
    }


}
