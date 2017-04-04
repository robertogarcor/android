package com.ioc.robertogarciacorcoles.eac2_2016s1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ShowNoticia extends AppCompatActivity {

    //Variables
    private String titolNoticia;
    private String urlNoticia;
    private WebView web;
    private String pageHTMLNoticia;

    /**
     * Metode per crear la activity
     * Iniciar/obtenir elements activity
     * @param savedInstanceState paquet d'estat de la activity per possible canvi
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_noticia);

        // Mapagte del witget WebView
        web = (WebView) findViewById(R.id.wvShowNoticia);

        // Obtenir intents
        obtenirIntents();

        // Mostrar Noticia a WebView
        mostrarNoticiaWebView();
    }


    /**
     * Metodo per obtenir dades intent MainActivity de referencies enviades
     */
    private void obtenirIntents() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            urlNoticia = extras.getString("urlNoticia");
            titolNoticia = extras.getString("titolNoticia");
            pageHTMLNoticia = extras.getString("pageHTMLNoticia");

        }
    }

    /**
     * Metode per mostrar la noticia a WebView en funcio si hi ha xarxa o no
     * funció comprovada anteriorment
     */
    private void mostrarNoticiaWebView() {
        // Si hi ha url noticia mostren pagina oficial noticia
        if (urlNoticia != "" && urlNoticia != null) {
                this.setTitle(titolNoticia);

                // Serveix perque el webwiew agafi el control
                // de les peticions Web i no sigui el propi navegador
                web.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest url) {
                        web.loadUrl(url.toString());
                        return false;
                    }
                });
                web.loadUrl(urlNoticia);

            // Cas contrari mostren pagina personalitza noticia
            } else {
                this.setTitle(R.string.app_name);
                web.loadDataWithBaseURL(null, pageHTMLNoticia, "text/html", "UTF-8", null);
                //web.loadData(pageHTML, "text/html; charset=UTF-8", null);

        }
    }

}
