package com.ioc.robertogarciacorcoles.eac2_2016s1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roberto on 07/10/2016.
 *
 * Aquesta classe representa una entrada de noticia del RSS Feed
 * Permet instancia objecte de tipus Entrada per fer un model de
 * de noticia extret de RSS Feed elpais.com
 */
public class Entrada {
        private String titol;                                    //Títol de la noticia
        private String enllac;                                   //Enllaç a la noticia completa
        private String autor;                                    //Autor de la noticia
        private String resum;                                    //Resum (Descripcio) de la noticia
        private String dataPublicacio;                           //Data de publicacio noticia
        private List<String> categories = new ArrayList<>();     //Categories de la noticia
        private String enllacImg;                                //Enllaç a la image noticia



    public Entrada(String title, String link, String author, String summary, String data, List<String> category, String linkImg) {
            this.titol = title;
            this.enllac = link;
            this.autor = author;
            this.resum = summary;
            this.dataPublicacio = data;
            this.categories = category;
            this.enllacImg = linkImg;

        }


    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getEnllac() {
        return enllac;
    }

    public void setEnllac(String enllac) {
        this.enllac = enllac;
    }

    public String getResum() {
        return resum;
    }

    public void setResum(String resum) {
        this.resum = resum;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDataPublicacio() {
        return dataPublicacio;
    }

    public void setDataPublicacio(String dataPublicacio) {
        this.dataPublicacio = dataPublicacio;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getEnllacImg() {
        return enllacImg;
    }

    public void setEnllacImg(String enllacImg) {
        this.enllacImg = enllacImg;
    }
}





