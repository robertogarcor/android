package com.ioc.robertogarciacorcoles.eac2_2016s1;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Roberto on 06/10/2016.
 */
public class PaisXmlParser {

    // No fem servir namespaces
    private static final String ns = null;

    /**
     * Metode per analisis del Xml
     * @param in Canal/flux de entrada que es rep
     * @return llista de noticies
     * @throws XmlPullParserException es delega qualsevol excepcio produida per XmlPullParser
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    public List<Entrada> analitza(InputStream in) throws XmlPullParserException, IOException {
        try {
            //Obtenim analitzador
            XmlPullParser parser = Xml.newPullParser();

            //No fem servir namespaces
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            //Especifica l'entrada de l'analitzador
            parser.setInput(in, null);

            //Obtenim la primera etiqueta
            parser.nextTag();

            //Retornem la llista de noticies
            return llegirNoticies(parser);
        } finally {
            in.close();
        }
    }

    /**
     * Metode que llegeix una llista de noticies de Pais a partir del parser
     * i retorna una llista d'Entrades
     * @param parser objecte analitzador Xml
     * @return llista d'Entrades
     * @throws XmlPullParserException es delega qualsevol excepcio produida per XmlPullParser
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    private List<Entrada> llegirNoticies(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entrada> llistaEntrades = new ArrayList<Entrada>();

        //Comprova si l'event actual és del tipus esperat (START_TAG) i de nom "rss"
        parser.require(XmlPullParser.START_TAG, ns, "rss");

        //Mentre que no arribem al final d'etiqueta
        while (parser.next() != XmlPullParser.END_TAG) {
            //Ignorem tots els events que no siguin un comenament d'etiqueta
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                //Saltem al seguent event
                continue;
            }

            String name = parser.getName();
            // Si etiqueta es channel recorrem tot es seu contingut
            if (name.equals("channel")) {

                //Comprova si l'event actual és del tipus esperat (START_TAG) i del nom "channel"
                parser.require(XmlPullParser.START_TAG, ns, "channel");


                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }

                    //Obtenim el nom de l'etiqueta
                    name = parser.getName();

                    // Si aquesta etiqueta és una entrada de noticia item
                    if (name.equals("item")) {
                        //Afegim l'entrada a la llista
                        llistaEntrades.add(llegirEntrada(parser));
                    } else {
                        //Si és una altra cosa la saltem
                        saltar(parser);
                    }
                }
            } else {
                //Si és una altra cosa la saltem
                saltar(parser);
            }
        }
        //Retornem llista de entrades
        return llistaEntrades;
    }


    /**
     * Metode que serveix per saltar-se una etiqueta i les seves subetiquetes aniuades.
     * @param parser objecte analitzador Xml
     * @throws XmlPullParserException es delega qualsevol excepcio produida per XmlPullParser
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    private void saltar(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Si no és un comenament d'etiqueta: ERROR
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;

        //Comprova que ha passat per tantes etiquetes de començament com acabament d'etiqueta

        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    //Cada vegada que es tanca una etiqueta resta 1
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    //Cada vegada que s'obre una etiqueta augmenta 1
                    depth++;
                    break;
            }
        }
    }

    /**
     * Metode que analitza el contingut d'una entrada. Si troba un titol, enllaç, autor, resum,
     * enllaç data, categoria crida els mètodes de lecturapropis per processar-los.
     * Si no, ignora l'etiqueta.
     * @param parser objecte analitzador Xml
     * @return la entrada corresponent
     * @throws XmlPullParserException es delega qualsevol excepcio produida per XmlPullParser
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    private Entrada llegirEntrada(XmlPullParser parser) throws XmlPullParserException, IOException {
        String titol = null;
        String enllac = null;
        String autor = null;
        String resum = null;
        String dataPublicacio = null;
        List<String> categories = new ArrayList<>();
        String enllacImg = null;
        Boolean linkImgtrobat = false;

        //L'etiqueta actual ha de ser "item"
        parser.require(XmlPullParser.START_TAG, ns, "item");

        //Mentre que no acabe l'etiqueta de "item"
        while (parser.next() != XmlPullParser.END_TAG) {
            //Ignora fins que no trobem un començament d'etiqueta
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            //Obtenim el nom de l'etiqueta
            String etiqueta = parser.getName();


            //Si és un títol de noticia
            if (etiqueta.equals("title")) {
                titol = llegirTitol(parser);
            }
            //Si l'etiqueta és un link de noticia
            else if (etiqueta.equals("link")) {
                enllac = llegirEnllac(parser);
            }
            //Si l'etiqueta es autor de noticia
            else if (etiqueta.equals("dc:creator")){
                autor = llegirAutor(parser);
            }
            //Si és una resum description
            else if (etiqueta.equals("description")) {
                resum = llegirResum(parser);
            }
            //Si és una Date
            else if (etiqueta.equals("pubDate")) {
                dataPublicacio = llegirData(parser);
            }
            //Si es una categoria
            else if(etiqueta.equals("category")) {
                categories.add(llegirCategories(parser));
            }
            //Si es un enllac a image
            //Nomes volen la primera ocurrencia la imagen miniatura
            else if(etiqueta.equals("enclosure")){
                if (linkImgtrobat == false) {
                    enllacImg = llegirEnllacImg(parser);
                    linkImgtrobat = true;
                } else {
                    saltar(parser);
                }

            //les altres etiquetes les saltem
            } else {
                saltar(parser);
            }
        }

        //Creem una nova entrada amb aquestes dades i la retornem

        return new Entrada(titol, enllac, autor, resum, dataPublicacio, categories, enllacImg);
    }

    /**
     * Metode que llegeix el títol de una notcia del feed i el retorna com String
     * @param parser objecte analitzador Xml
     * @return el titol de la noticia
     * @throws XmlPullParserException es delega qualsevol excepcio produida per XmlPullParser
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    private String llegirTitol(XmlPullParser parser) throws IOException, XmlPullParserException {

        //L'etiqueta actual ha de ser "title"
        parser.require(XmlPullParser.START_TAG, ns, "title");

        //Llegeix
        String titol = llegeixText(parser);

        //Fi d'etiqueta
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return titol;
    }

    /**
     * Metode que llegeix l'enllaç de una notícia/image del feed i el retorna com String
     * @param parser objecte analitzador Xml
     * @return l'enllac com a String
     * @throws XmlPullParserException es delega qualsevol excepcio produida per XmlPullParser
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    private String llegirEnllac(XmlPullParser parser) throws IOException, XmlPullParserException {

        //L'etiqueta actual ha de ser "link". Revisar format XML elpais.com
        parser.require(XmlPullParser.START_TAG, ns, "link");

        String enllac = llegeixText(parser);

        //Fi d'etiqueta
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return enllac;
    }

    /**
     * Metode que llegeix el resum de una notícia del feed i el retorna com String
     * @param parser objecte analitzador Xml
     * @return el resum noticia com String
     * @throws XmlPullParserException es delega qualsevol excepcio produida per XmlPullParser
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    private String llegirResum(XmlPullParser parser) throws IOException, XmlPullParserException {

        //L'etiqueta actual ha de ser "description"
        parser.require(XmlPullParser.START_TAG, ns, "description");

        String resum = llegeixText(parser);

        parser.require(XmlPullParser.END_TAG, ns, "description");
        return resum;
    }


    /**
     * Metode que llegeix el autor de una notícia del feed i el retorna com String
     * @param parser objecte analitzador Xml
     * @return el autor noticia com String
     * @throws XmlPullParserException es delega qualsevol excepcio produida per XmlPullParser
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    private String llegirAutor(XmlPullParser parser) throws IOException, XmlPullParserException {

        //L'etiqueta actual ha de ser "dc:creator"
        parser.require(XmlPullParser.START_TAG, ns, "dc:creator");

        String autor = llegeixText(parser);

        parser.require(XmlPullParser.END_TAG, ns, "dc:creator");
        return autor;
    }


    /**
     * Metode que llegeix el data de una notícia del feed i el retorna com Date
     * @param parser objecte analitzador Xml
     * @return la data de la noticia com String
     * @throws XmlPullParserException es delega qualsevol excepcio produida per XmlPullParser
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    private String llegirData(XmlPullParser parser) throws IOException, XmlPullParserException {

        //L'etiqueta actual ha de ser "pubDate"

        parser.require(XmlPullParser.START_TAG, ns, "pubDate");

        String data = llegeixText(parser);

        parser.require(XmlPullParser.END_TAG, ns, "pubDate");
        return data;
    }


    /**
     * Metode que llegeix la categoria de una notícia del feed i el retorna com String
     * @param parser objecte analitzador Xml
     * @return la categoria de la noticia com String
     * @throws XmlPullParserException es delega qualsevol excepcio produida per XmlPullParser
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    private String llegirCategories(XmlPullParser parser) throws IOException, XmlPullParserException {
        //L'etiqueta actual ha de ser "category"
        parser.require(XmlPullParser.START_TAG, ns, "category");

        String categoria = llegeixText(parser);

        parser.require(XmlPullParser.END_TAG, ns, "category");
        return categoria;
    }


    /**
     * Metode que llegeix l'enllaç de una image del feed i el retorna com String
     * @param parser objecte analitzador Xml
     * @return l'enllac de la imatge com a String
     * @throws XmlPullParserException es delega qualsevol excepcio produida per XmlPullParser
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    private String llegirEnllacImg(XmlPullParser parser) throws IOException, XmlPullParserException {
        String enllac = "";
        //L'etiqueta actual ha de ser "enclosure"
        parser.require(XmlPullParser.START_TAG, ns, "enclosure");

        //Obtenim l'etiqueta
        String tag = parser.getName();

        //Obtenim l'atribut url (mirar l'XML de elpais.com)
        enllac = parser.getAttributeValue(null, "url");

        parser.nextTag();

        //Fi d'etiqueta
        parser.require(XmlPullParser.END_TAG, ns, "enclosure");
        return enllac;
    }


    /**
     * Metode que Extrau el valor de text per les etiquetes de text titol, resum, autor,...
     * @param parser objecte analitzador Xml
     * @return el text de les etiquetes corresponents
     * @throws XmlPullParserException es delega qualsevol excepcio produida per XmlPullParser
     * @throws IOException es delega qualsevol excepció produida per I/O
     */
    private String llegeixText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String resultat = "";

        if (parser.next() == XmlPullParser.TEXT) {
            resultat = parser.getText();
            parser.nextTag();
        }
        return resultat;
    }


}






