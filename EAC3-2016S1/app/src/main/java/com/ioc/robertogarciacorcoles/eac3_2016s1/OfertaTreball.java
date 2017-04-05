package com.ioc.robertogarciacorcoles.eac3_2016s1;

/**
 * Created by Roberto on 22/10/2016.
 * Clase model OfertaTreball per crear objectes de tipus
 */
public class OfertaTreball {

    private Integer idBD;
    private String titol;
    private String descripcio;
    private Double latitud;
    private Double longitud;
    private Integer telefon;
    private String dataPublicacio;


    /**
     * Constructor Objecte OfertaTreball
     * @param title titol de la oferta
     * @param summary descripcio de la oferta
     * @param latitud coordenada latitud posicio oferta
     * @param longitud coordenada longitud posicio oferta
     * @param telefon telefon de oferta
     * @param data data de publicacio de la oferta
     */
    public OfertaTreball(Integer id, String title, String summary, Double latitud, Double longitud, Integer telefon, String data) {
        this.idBD = id;
        this.titol = title;
        this.descripcio = summary;
        this.latitud = latitud;
        this.longitud = longitud;
        this.telefon = telefon;
        this.dataPublicacio = data;
    }

    // Getters i Setters

    public Integer getIdBD() { return idBD; }

    public void setIdBD(Integer idBD) {
        this.idBD = idBD;
    }

    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Integer getTelefon() {
        return telefon;
    }

    public void setTelefon(Integer telefon) {
        this.telefon = telefon;
    }

    public String getDataPublicacio() {
        return dataPublicacio;
    }

    public void setDataPublicacio(String dataPublicacio) {
        this.dataPublicacio = dataPublicacio;
    }
}
