package com.ioc.robertogarciacorcoles.eac2_2016s1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*************************************************************************/
/************************* APARTAT 7 i MILLORES **************************/
/*************************************************************************/

/**
 *
 * La App permet poder descarregar altres seccions RSS disponibles a la combo spinner
 * Inicialment si hi ha xarxa es descarrega la seccion Tecnologia com a seccio default
 *
 * Per fer apareixa la combo fer click al 1 item de toolbar.
 * Aquest item visualitza/amaga la combo. Per descarrega la seccion seleccionada press btn
 * del costat de costat asocciat.
 *
 * Descarregar la secció RSS seleccionada, emmagatzetma les noticies a BD (Taula news unica)
 * i oculta la combo.
 * Si no hi ha xarxa les notices s'obtenem de la BD - Ultima seccioRSS
 *
 * Posteriorment el comportament és el mateix indicat inicialment.
 *
 * Es preservat la url i titol corresponents a tecnologia com a tematica inicial.
 *
 * Canvis respecte al disseny i funcionalitats inicials proposades :
 * -Incloure un nou item de menu per descarregar seccions RSS
 * -Titol de la activity dinamic en funcio de RSS (Es preserva el titol inicial a Tecnologia)
 * -Visibilitat del layout cercador i seccionRSS (la seleccion de un amaga l'altre)
 * -S'ha afegeix al item update de la toolbar la funcionalitat de amagar la visibilitat dels
 * layouts cercador i seccioRSS en actualizació de les noticies si aquest son visibles.
 *
 * S'ha millorat la cerca de la App per poder filtrar tant per titol, descripcion, autor i categories
 * Detalls del filtre aplicat al metode: "obtenirTitolsNoticiesFilter(String filterCerca)"
 *
 * S'ha millorat el style de la caixa de text i la progresBar creant un style per la caixa
 * de text, afegim un placeholder i canviant el color de la progresBar.
 *
 * S'ha creat un metode per tancar el teclat virtual de forma programada despres de fer
 * click al btn cerca, ja que aquest s'ha de ocultar manualment.
 * Metode : "tancaTeclatVirtual(View v)"
 *
 * S'ha fet ús de la gestió del enllaços i peticions web perque si el propi webView
 * qui el gestioni i no pas el navegador. ShowNoticia - mètode:  webView.setWebViewClient(...)
 *
 */

public class MainActivity extends AppCompatActivity implements OnClickListener {

    // Constants connexio i URL RSS
    private static String url = "http://ep00.epimg.net/rss/tecnologia/portada.xml";
    private static boolean connXarxa = false;
    private static int titolActivity = R.string.seccioTecnoRSS;

    // Instacia de Classe DBInterface
    private DBInterface db;

    // Llista Noticies , Titols noticies, items seccions RSS, titols RSS i Adaptadors
    List<Entrada> llNoticies = new ArrayList<>();
    private List<String> llTitolsNoticies = new ArrayList<>();
    private ArrayAdapter<String> adplistNoticies;
    private String[] itemsSectionRSS;
    private String seccioRSS = "";

    // Altres witges
    private ProgressBar prgBar;
    private ListView listViewNoticies;
    private Toolbar toolbar;
    private LinearLayout layoutCercador;
    private LinearLayout layoutSectionRSS;
    private ImageButton btnCerca;
    private ImageButton btnDownloadRSS;
    private EditText textCerca;
    private Spinner spSectionItemRSS;


    /**
     * Metode per crear la activity
     * Iniciar/obtenir elements activity
     * @param savedInstanceState paquet d'estat de la activity per possible canvi
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Canviem el titol de la Activity inicial posteriorment segons secció cas xarxa
        this.setTitle(titolActivity);

        // Mapatge de witges i Listener btn i color progressBar
        layoutCercador = (LinearLayout) findViewById(R.id.linearLayoutCercador);
        layoutSectionRSS = (LinearLayout) findViewById(R.id.linearLayoutSectionRSS);
        spSectionItemRSS = (Spinner) findViewById(R.id.spItemsRSS);
        listViewNoticies = (ListView) findViewById(R.id.lwTitleNews);
        textCerca = (EditText) findViewById(R.id.edtCerca);
        prgBar = (ProgressBar) findViewById(R.id.prgbTasques);
        prgBar.getIndeterminateDrawable().setColorFilter(Color.rgb(125, 150, 140), PorterDuff.Mode.SRC_IN);

        btnCerca = (ImageButton) findViewById(R.id.btnCerca);
        btnCerca.setOnClickListener(this);

        btnDownloadRSS = (ImageButton) findViewById(R.id.btnDowloadRSS);
        btnDownloadRSS.setOnClickListener(this);

        // Adaptador de la ListView
        adplistNoticies = new ArrayAdapter<String>(MainActivity.this,
                R.layout.lista_titols, R.id.tvTitols, llTitolsNoticies);

        //Covertin els items del Recurs StringArray Section RSS XML a String Array
        itemsSectionRSS = getResources().getStringArray(R.array.sectionsRSS);

        //Asociacio seleccionItems a la ListView
        seleccióLongItemLlista(listViewNoticies);
        seleccióItemLlista(listViewNoticies);

        //Carregen dades Adapter Spinner RSS seccions
        carregarAdpDadesSectionsRSS();

        //Comprovar si hi ha Xarxa
        comprobarEstatXarxa();

        // Instanciem la BD
        db = new DBInterface(this);

        //Carregem news
        carregaNoticies();

    }

    /**
     * Metode que comprova la xarxa activa
     */
    public boolean comprobarEstatXarxa() {

        //Obtenir un gestor de connexions de xarxa
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Comprovar l'estat de la xarxa
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null) {
            connXarxa = false;
            //missagte("No Hi ha connexió Xarxa!!");
        } else {
            connXarxa = networkInfo.isConnected();
            //missagte("Hi ha connexió Xarxa!!");
        }
        return connXarxa;
    }

    /**
     * Metode que carregar les noticies d'una font externa o BD
     * Si hi ha xarxa fa servir AsyncTask per descarregar el feed XML de URL elpais.com
     * Si NO hi ha xarxa carregar les notices de la BD (Si hi han)
     */
    public void carregaNoticies() {
        if (connXarxa) {
            new DescarregaXmlTask().execute(url);
        } else {
            new CarregarOfflineNewsDB().execute();
            missagte("Càrrega offline!");
        }
    }

    /**
     * Implementació d'AsyncTask per descarregar el feed XML de elpais.com
     */
    private class DescarregaXmlTask extends AsyncTask<String, Void, List<Entrada>> {

        @Override
        // Execucio abans del background barra progres visible
        protected void onPreExecute() {
            prgBar.setVisibility(View.VISIBLE);
        }

        /**
         * Execucio en background de la tasca
         * @param url String de la tasca que executem
         * @return llista de noticies objectes Entrada
         */
        @Override
        protected List<Entrada> doInBackground(String... url) {
            try {
                //Carreguem l'XML
                llNoticies.clear();
                llNoticies = carregaXMLdelaXarxa(url[0]);
                //System.out.println("llistaEntradas: " + llNoticies.size());

                if (llNoticies != null) {
                    //Obrim la BD
                    db.obre();

                    // Esborrem el registres de la Taula news
                    db.esborrarRegistresTaulaNews();

                    //Insertem les news descarregues a la BD
                    inserirEntradesNoticiaBD(llNoticies);
                }

                // Tanquem BD
                db.tanca();

            } catch (IOException e) {
                e.getMessage();
                //missagte(e.getMessage());
            } catch (XmlPullParserException e) {
                e.getMessage();
                //missagte(e.getMessage());
            }
            return llNoticies;
        }

        /**
         * Metode d'execucio posterior al background
         * @param resNoticiesXML la llista de objectes Entrada XML retornada del la execucio backgound
         */
        @Override
        protected void onPostExecute(List<Entrada> resNoticiesXML) {

            //Esborren llista de titols i tornem a omplirla
            obtenirTotTitolsNoticiesXML(resNoticiesXML);

            // Notifiquen canvi a Adaptador llista noticies(Titols)
            adplistNoticies.notifyDataSetChanged();
            listViewNoticies.setAdapter(adplistNoticies);

            // Ocultem barra progres
            prgBar.setVisibility(View.GONE);
        }
    }

    /**
     * Metode per Descarregar XML elpais.com
     * @param urlString la url del feed RSS el pais
     * @return llista de noticies objectes Entrada
     * @throws XmlPullParserException es delega qualsevol excepcio produida per XmlPullParser
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    private List<Entrada> carregaXMLdelaXarxa(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        //Creem una instancia de l'analitzador
        PaisXmlParser analitzador = new PaisXmlParser();

        //Llista de entrades de noticies
        List<Entrada> entradesXML = null;

        try {
            //Obrim la connexio
            stream = ObreConnexioHTTP(urlString);

            //Obtenim la llista d'entrades a partir de l'stream de dades
            entradesXML = analitzador.analitza(stream);
        } catch (Exception e) {
            //missagte(e.getLocalizedMessage());
            e.getMessage();
        } finally {
            //Tanquem l'stream una vegada hem terminat de treballar amb ell
            if (stream != null) {
                stream.close();
            }
        }

        return entradesXML;
    }

    /**
     * Implementació d'AsyncTask per carregar les noticies de la BD en mode offline
     * Durant el proces la progess Bar es visualitza i s'amaga sembla que es necessari
     * operar sobre el witge en offline la primera vegada si no es visualitza tota la estona
     */
    private class CarregarOfflineNewsDB extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            prgBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<String> doInBackground(Void... params) {

            try {
                db.obre();
                //Cursor retornar tots els registres
                Cursor cNoticiesBD = db.obtenirTotesLesNews();

                //Esborren les dades de la llista titols i llista noticies
                llTitolsNoticies.clear();
                llNoticies.clear();

                // Si hi ha registres a cursor associem variables
                if (cNoticiesBD.moveToFirst()) {

                    // No es necessari crear les varibles int poden referencia la columna (numero)
                    // Nomes s'ha expossat la forma per indicar com extraure la columna
                    int idNew = cNoticiesBD.getColumnIndex(DBInterface.CLAU_ID);  // fa referencia a la columna 0
                    int titolNew = cNoticiesBD.getColumnIndex(DBInterface.CLAU_TITOL); // fa referencia a la columna 1
                    int linkNew = cNoticiesBD.getColumnIndex(DBInterface.CLAU_URL); // fa referencia a la columna 2
                    int autorNew = cNoticiesBD.getColumnIndex(DBInterface.CLAU_AUTOR); // fa referencia a la columna 3
                    int descripcioNew = cNoticiesBD.getColumnIndex(DBInterface.CLAU_DESCRIPCIO); // fa referencia a la columna 4
                    int data = cNoticiesBD.getColumnIndex(DBInterface.CLAU_DATA); // fa referencia a la columna 5
                    int cats = cNoticiesBD.getColumnIndex(DBInterface.CLAU_CATEGORIA); // fa referencia a la columna 6
                    int linkImg = cNoticiesBD.getColumnIndex(DBInterface.CLAU_URLIMG); // fa referencia a la columna 7
                    int srss = cNoticiesBD.getColumnIndex(DBInterface.CLAU_RSS); // fa referencia a la columna 8
                    seccioRSS = cNoticiesBD.getString(srss);
                    ////System.out.println("seccion : " + seccioRSS);
                    /**
                     * Recorrem el cursor
                     * Fem transformacio de dades de String categories BD > Array > Lista
                     * Per cada registre obtenim titol per llista de noticies
                     * Per cada registre(Tupla) creen objecte de tipus Entrada
                     * Afegim objectes Entrada a la llista de Noticies
                     */
                    do {

                        String categories = cNoticiesBD.getString(cats);
                        String[] acategories = categories.split(",");

                        List<String> llcategories = Arrays.asList(acategories);

                        llTitolsNoticies.add(cNoticiesBD.getString(titolNew));

                        Entrada outDB = new Entrada(cNoticiesBD.getString(titolNew), cNoticiesBD.getString(linkNew),
                                cNoticiesBD.getString(autorNew), cNoticiesBD.getString(descripcioNew),
                                cNoticiesBD.getString(data), llcategories, cNoticiesBD.getString(linkImg));
                        llNoticies.add(outDB);

                    } while (cNoticiesBD.moveToNext());
                }
                db.tanca();

            } catch (SQLException e) {
                //missagte(e.getMessage());
                e.getMessage();
            }
            // retornem resultat titols noticies
            return llTitolsNoticies;
        }

        @Override
        protected void onPostExecute(List<String> resTitlesNews) {
            // Notifiquen canvi a Adaptador llista noticies(Titols)
            adplistNoticies.notifyDataSetChanged();
            listViewNoticies.setAdapter(adplistNoticies);

            //Controlen canvi titol activity en mode Offline
            if (seccioRSS != "") {
                MainActivity.this.setTitle(seccioRSS);
            } else {
                MainActivity.this.setTitle(titolActivity);
            }

            // Ocultem barra progres
            prgBar.setVisibility(View.GONE);

        }
    }

    /**
     * Metode Implementacio per mostrar Toast descripcio noticia amb un click larg
     * del item llista seleccionat
     * @param llistaNoticies la listView de titols noticies
     */
    private void seleccióLongItemLlista(final ListView llistaNoticies) {
        llistaNoticies.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Agafen String titol del item seleccionat
                String selItemTitol = llistaNoticies.getItemAtPosition(position).toString();
                String descripcioResum = "";
                //Obtenim noticia corresponent a sel titol i obtenim descripcio
                Entrada noticia = obtenirEntradaNoticiaList(selItemTitol);
                descripcioResum = noticia.getResum();

                //Si la news té descripcio la mostren, cas contrari mostrem no té descripció
                if (descripcioResum.equals("") || descripcioResum == null) {
                    descripcioResum = "Aquesta noticia no té descripció!";
                    missagte(descripcioResum);
                } else {
                    missagte(descripcioResum);
                }
                return true;
            }
        });
    }

    /**
     * Metode Implementacio per mostrar la pagina oficial o pagina personalitzada de la noticia
     * amb un click del item llista seleccionat i en funcio si hi ha o no xarxa
     * @param llistaNoticies la listView de titols noticies
     */
    private void seleccióItemLlista(final ListView llistaNoticies) {
        llistaNoticies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Agafen String titol del item seleccionat
                String selItemTitol = llistaNoticies.getItemAtPosition(position).toString();
                //Mirem a la list Entrades el corresponent titol i obtenim noticia corresponent
                Entrada noticia = obtenirEntradaNoticiaList(selItemTitol);

                //Crem intent per enviar dades a Activity Browsable (Show Noticia)
                Intent i = new Intent(MainActivity.this, ShowNoticia.class);
                Bundle extra = new Bundle();

                // si ha xarxa envien Intent amb url, titol noticia i inicien Activity ShowNoticia
                if (comprobarEstatXarxa()) {
                    extra.putString("urlNoticia", noticia.getEnllac());
                    extra.putString("titolNoticia", noticia.getTitol());
                    i.putExtras(extra);
                    startActivity(i);
                    // Cas contrari envien String pagina HTML
                } else {
                    //Crear String pagina HTML
                    // S'ha de verificar a activity ShowNoticies enviar url="" and url != null
                    String pageHtml = htmlOfflineWebView(noticia);
                    extra.putString("pageHTMLNoticia", pageHtml);
                    i.putExtras(extra);
                    startActivity(i);
                }
            }
        });
    }

    /**
     * Metode per crear pagina HTML
     * @param entrada la entrada corresponent a la noticia seleccionada
     * @return String de la pagina html
     */
    private String htmlOfflineWebView(Entrada entrada) {

        // Si no hi ha descripcio mostrar descripcio no disponible
        String descripcio = null;
        if (entrada.getResum().isEmpty()) {
            descripcio = "Descripción noticia no disponible actualmente.";
        } else {
            descripcio = entrada.getResum();
        }

        StringBuilder shtml = new StringBuilder();

        // Conversio List categories > Array > String
        String[] acats = entrada.getCategories().toArray(new String[entrada.getCategories().size()]);
        String scats = TextUtils.join(", ", acats);

        shtml.append("<!DOCTYPE html>");
        shtml.append("<html lang='es'>");
        shtml.append("<head>");
        //shtml.append("<meta charset='UTF-8'>");
        shtml.append("<title>" + entrada.getTitol() + "</title>");
        shtml.append("</head>");
        shtml.append("<body>");
        shtml.append("<h2>" + entrada.getTitol() + "</h2>" + "<hr>");
        shtml.append("<p style=\"text-align:justify;\">" + descripcio + "</p>" + "<hr>");
        shtml.append("<p style=\"text-align:right;font-size:0.7em;\"><cite>" + entrada.getAutor() + "</cite></p>" + "<hr>");
        shtml.append("<p style=\"text-align:justify;font-size:0.8em;\"><strong>Categories:</strong> " + scats + "</p>");
        shtml.append("<p style=\"font-size:0.7em;\">" + entrada.getDataPublicacio() + "</p>");
        shtml.append("</body>");
        shtml.append("</html>");

        return shtml.toString();
    }

    /**
     * Metode per obtenir la noticia corresponent amb titol lists Titols
     * segon seleccio previa
     * @param titolLlista el titol de la noticia seleccionat previament
     * @return la entrada noticia corresponent
     */
    private Entrada obtenirEntradaNoticiaList(String titolLlista) {
        Entrada noticia = null;
        for (Entrada in : llNoticies) {
            if (in.getTitol().equals(titolLlista)) {
                noticia = in;
            }
        }
        return noticia;
    }

    /**
     * Metode que permet obtenir tots els titols de noticies de la descarregar XML
     * @param llNoticiesXML la llista de noticies descarregades XML
     */
    private void obtenirTotTitolsNoticiesXML(List<Entrada> llNoticiesXML) {
        llTitolsNoticies.clear();
        for (Entrada in : llNoticiesXML) {
            llTitolsNoticies.add(in.getTitol());
        }
    }

    /**
     * Metode que permet obtenir tots els titols de la llista de noticies (Entrades)
     */
    private void obtenirTotTitolsNoticiesList() {
        llTitolsNoticies.clear();
        for (Entrada in : llNoticies) {
            llTitolsNoticies.add(in.getTitol());
        }
    }


    /**
     * Metode que permet inserir totes les entrades (Noticies) de la List a la BD.
     * A mes de fer control de la seccio RSS corresponent per mantenir referencia offline
     * seccio RSS i mostrarla al titol de activity (No es emmagatzeda al objecte)
     * @param llista la llista de noticies
     */
    private void inserirEntradesNoticiaBD(List<Entrada> llista) {
        for (Entrada in : llista) {
            db.insereixNews(in.getTitol(), in.getEnllac(), in.getAutor(), in.getResum(),
                    in.getDataPublicacio(), in.getCategories(), in.getEnllacImg(), getResources().getString(titolActivity));
        }
    }

    /**
     * Metode per mostrar missatge toast per pantalla
     * @param msg el missatge a mostrar
     */
    private void missagte(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Metodo inicialitzacion per carregar i omplir la combo sections RSS
     */
    private void carregarAdpDadesSectionsRSS() {
        ArrayAdapter<String> adpSectionRSS = new ArrayAdapter<String>(MainActivity.this,
                R.layout.lista_sectionrss, R.id.tvSectionRSS, itemsSectionRSS);
        adpSectionRSS.notifyDataSetChanged();
        spSectionItemRSS.setAdapter(adpSectionRSS);
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

        // Mapatge dels items de la Toolbar
        MenuItem update = menu.findItem(R.id.action_update);
        MenuItem search = menu.findItem(R.id.action_search);
        MenuItem section = menu.findItem(R.id.action_section);

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

        // Obtenim id asociat i segons seleccio fem accio associada
        int id = item.getItemId();
        switch (id) {
            case R.id.action_update:
                layoutSectionRSS.setVisibility(View.GONE);
                layoutCercador.setVisibility(View.GONE);
                if (comprobarEstatXarxa()) {
                    new DescarregaXmlTask().execute(url);
                } else {
                    missagte("No hi ha connexio!");
                }
                return true;
            case R.id.action_search:
                visibilitatCercador();
                return true;
            case R.id.action_section:
                visibilitatSectionsRSS();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Metodo de la classe OnClickListener per la execució de la acció segons el btn click.
     * Si el btn pres es btnDownload es fa la descarregar de altres
     * seccions RSS disponibles a la combo Spinner, sempre que hi hagi connexió
     * Si el btn pres es btnCerca es fa un filtre segons condicions aplicades.
     * @param v event(id btn) que ha disparat la acció de la vista
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnCerca:
                String txtfilter = textCerca.getText().toString();
                obtenirTitolsNoticiesFilter(txtfilter);
                tancaTeclatVirtual(v);
                visibilitatCercador();
                break;
            case R.id.btnDowloadRSS:
                if (comprobarEstatXarxa()) {
                    String secRSS = spSectionItemRSS.getSelectedItem().toString();
                    switch (secRSS) {
                        case "Ciencia":
                            url = "http://ep00.epimg.net/rss/elpais/ciencia.xml";
                            titolActivity = R.string.seccioCienciaRSS;
                            break;
                        case "Cultura":
                            url = "http://ep00.epimg.net/rss/cultura/portada.xml";
                            titolActivity = R.string.seccioCulturaRSS;
                            break;
                        case "Deportes":
                            url = "http://ep00.epimg.net/rss/deportes/portada.xml";
                            titolActivity = R.string.seccioDeporteRSS;
                            break;
                        case "Economia":
                            url = "http://ep00.epimg.net/rss/economia/portada.xml";
                            titolActivity = R.string.seccioEconomiaRSS;
                            break;
                        case "Sociedad":
                            url = "http://ep00.epimg.net/rss/sociedad/portada.xml";
                            titolActivity = R.string.seccioSociedadRSS;
                            break;
                        case "Tecnologia":
                            url = "http://ep00.epimg.net/rss/tecnologia/portada.xml";
                            titolActivity = R.string.seccioTecnoRSS;
                        defaulf: break;
                    }
                    visibilitatSectionsRSS();
                    this.setTitle(titolActivity);
                    new DescarregaXmlTask().execute(url);
                } else {
                    visibilitatSectionsRSS();
                    missagte("No hi ha connexió!!, per descarregar necessites una xarxa activa.");
                }
        }
    }

    /**
     * Metode per amargar o visualitzar el Layout Seccion RSS
     * en funció de la visibilitat actual que té
     */
    private void visibilitatSectionsRSS() {

        layoutCercador.setVisibility(View.GONE);

        if (layoutSectionRSS.getVisibility() == View.GONE) {
            layoutSectionRSS.setVisibility(View.VISIBLE);
        } else {
            layoutSectionRSS.setVisibility(View.GONE);
        }
    }

    /**
     * Metode per amargar o visualitzar el Layout Cercador
     * en funció de la visibilitat actual que té
     */
    private void visibilitatCercador() {

        layoutSectionRSS.setVisibility(View.GONE);

        if (layoutCercador.getVisibility() == View.GONE) {
            layoutCercador.setVisibility(View.VISIBLE);
        } else {
            layoutCercador.setVisibility(View.GONE);
        }
    }

    /**
     * Metode que ocultar el teclat virtual de forma programada
     * @param v la View on se inicia la entrada de la accio
     */
    private void tancaTeclatVirtual(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Metode implementacio filter per filtrar text introduit al widget cercador.
     * Filtres aplicats case-insensitive (no es té en compte majuscules i minuscules,
     * si accents i espais en blanc):
     * <p/>
     * 1 Filtre qualsevol paraula continguda al titol i descripcio noticia
     * <p/>
     * 2 Filtre per autor, aqui es té el content que la pauraula a cerca sigui igual o major a 3 caracter.
     * Poden considerar que un nom personal com a minim té 3 caracter per generalitzar una mica.
     * En aquest cas pot ser una paraula igual o major a 3 caracter o el nom complet respectant accents i espais blanc
     * <p/>
     * 3 Filtre per categoria, el criteri es idem que pel filtre 2, consideren una paraula igual
     * o major a 3 caracter inclosos, corresponent o que sigui el nom de la categoria completa
     * <p/>
     * Si text "" dona totes les noticies
     * Cas contrari dona les conincidencies segons criteris i resulatats del filtre
     * <p/>
     * En tots els casos si hi ha coincidències omple l'adaptador amb el titols de les noticies
     * <p/>
     * Si no hi ha coincidencies retorna un missatge , indicant que no hi ha coincidencies.
     *
     * @param filterCerca text per establir el filtre
     */
    private void obtenirTitolsNoticiesFilter(String filterCerca) {

        if (filterCerca.equals("")) {
            llTitolsNoticies.clear();
            obtenirTotTitolsNoticiesList();
        } else {
            llTitolsNoticies.clear();
            for (Entrada in : llNoticies) {
                if (in.getTitol().toLowerCase().contains(filterCerca.toLowerCase()) ||
                        in.getResum().toLowerCase().contains(filterCerca.toLowerCase())) {
                    llTitolsNoticies.add(in.getTitol());

                } else if (filterCerca.length() >= 3) {
                    if (in.getAutor().equalsIgnoreCase(filterCerca) ||
                            in.getAutor().toLowerCase().contains(filterCerca.toLowerCase())) {

                        if (!trobarTitolaLlistaTitols(in.getTitol(),llTitolsNoticies)) {
                            llTitolsNoticies.add(in.getTitol());
                        }

                    } else if (filterCerca.length() >= 3){
                        for (String cat : in.getCategories()) {
                            if (cat.equalsIgnoreCase(filterCerca) ||
                                    cat.toLowerCase().contains(filterCerca.toLowerCase())) {

                                if (!trobarTitolaLlistaTitols(in.getTitol(),llTitolsNoticies)) {
                                    llTitolsNoticies.add(in.getTitol());
                                }
                            }
                        }
                    }
                }
            }
        }

        if (llTitolsNoticies.size() == 0) {
            missagte("No s'han trobat coincidencies per aquesta cerca!!!");
        }

        adplistNoticies.notifyDataSetChanged();
        listViewNoticies.setAdapter(adplistNoticies);

    }

    /**
     * Metode que permet trobar un titol de noticia qualsevol dins la llista de titols
     * @param titol el titol de la noticia a cerca dins la llista
     * @param llTitols la llista de titols
     * @return true si el troba / false en cas contrari
     */
    private boolean trobarTitolaLlistaTitols (String titol, List<String> llTitols) {
        boolean trobat = false;
        for (String t : llTitols) {
            if (titol.equals(t) ) {
                trobat = true;
            } else { trobat = false; }
        }
        return trobat;
    }

    /**
     * Metode que Obre una connexió HTTP a partir d'un URL i retorna un InputStream
     * @param adrecaURL enllac adreca URL del recurs
     * @return flux de dade corresponent descarregades
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    private InputStream ObreConnexioHTTP(String adrecaURL) throws IOException {
        InputStream in = null;        //Buffer de recepció
        int resposta = -1;            //Resposta de la connexió

        //Obtenim un URL a partir de l'String proporcionat
        URL url = new URL(adrecaURL);

        //Obtenim una nova connexió al recurs referenciat per la URL
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        try {

            //Preparem la petició

            httpConn.setReadTimeout(10000);            //Timeout de lectura en milisegons
            httpConn.setConnectTimeout(15000);        //Timeout de connexió en milisegons
            httpConn.setRequestMethod("GET");        //Petició al servidor
            httpConn.setDoInput(true);               //Si la connexió permet entrada

            //Es connecta al recurs.
            httpConn.connect();

            //Obtenim el codi de resposta obtingut del servidor remot HTTP
            resposta = httpConn.getResponseCode();

            //Comprovem si el servidor ens ha retornat un codi de resposta OK,
            //que correspon a que el contingut s'ha descarregat correctament
            if (resposta == HttpURLConnection.HTTP_OK) {
                //Obtenim un Input stream per llegir del servidor
                //in = new BufferedInputStream(httpConn.getInputStream());
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            //Hi ha hagut un problema al connectar
            throw new IOException("Error connectant!");
        }
        //Retornem el flux de dades
        return in;
    }



}











