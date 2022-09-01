package com.localizaciondepersonas.tfg.auxiliar;

import android.widget.LinearLayout;

public class GuiaTutorial {
    /*
            INFO:   Clase que refleja el conjunto de informacion que una vista explicativa del tutorial recoge
     */
    //----------------------------------------------------------------------------------------------- ATRIBUTOS
    private String TITULO_GUIA;
    private int IMAGEN_GUIA;
    private String EXPLICACION_GUIA;
    //----------------------------------------------------------------------------------------------- CONSTRUCTOR
    public GuiaTutorial(String titulo,int imagen,String explicacion){
        this.TITULO_GUIA = titulo;
        this.IMAGEN_GUIA = imagen;
        this.EXPLICACION_GUIA = explicacion;
    }
    //----------------------------------------------------------------------------------------------- METODOS GETTER Y SETTER
    public String getTITULO_GUIA() {
        return TITULO_GUIA;
    }

    public void setTITULO_GUIA(String TITULO_GUIA) {
        this.TITULO_GUIA = TITULO_GUIA;
    }

    public int getIMAGEN_GUIA() {
        return IMAGEN_GUIA;
    }

    public void setIMAGEN_GUIA(int IMAGEN_GUIA) {
        this.IMAGEN_GUIA = IMAGEN_GUIA;
    }

    public String getEXPLICACION_GUIA() {
        return EXPLICACION_GUIA;
    }

    public void setEXPLICACION_GUIA(String EXPLICACION_GUIA) {
        this.EXPLICACION_GUIA = EXPLICACION_GUIA;
    }
}
