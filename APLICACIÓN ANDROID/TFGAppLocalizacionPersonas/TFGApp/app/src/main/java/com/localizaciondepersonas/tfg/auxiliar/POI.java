package com.localizaciondepersonas.tfg.auxiliar;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class POI {
    /*
            INFO:   Clase que refleja una ubicación disponible para la localizacion (Point Of Interest)
     */
    //----------------------------------------------------------------------------------------------- ATRIBUTOS
    private String NOMBRE_POI;
    private String DESCRIPCION_POI;
    private String TIPO_POI;
    private int PISO_POI;
    private LatLng PUNTO_POI;
    //----------------------------------------------------------------------------------------------- CONSTRUCTOR
    public POI(String nombre,String descripcion,String tipo,int piso,LatLng punto){
        this.NOMBRE_POI = nombre;
        this.DESCRIPCION_POI = configuracionRetornosDeCarro(descripcion);
        this.TIPO_POI = tipo;
        this.PISO_POI = piso;
        this.PUNTO_POI = punto;
    }
    //----------------------------------------------------------------------------------------------- METODOS PRIVADOS
    // METODO PARA LOS RETORNO DE CARROS
    private String configuracionRetornosDeCarro(String textoCrudo){
        String cadenaConRetornos = "";
        String[] lineasTexto = textoCrudo.split("\\\\n");
        for(int i=0; i<lineasTexto.length; i++){
            if(i+1 == lineasTexto.length) cadenaConRetornos += lineasTexto[i];
            else cadenaConRetornos += lineasTexto[i]+"\n";
        }
        return cadenaConRetornos;
    }
    //----------------------------------------------------------------------------------------------- METODOS GETTER Y SETTER
    public String getNOMBRE_POI() {
        return NOMBRE_POI;
    }

    public void setNOMBRE_POI(String NOMBRE_POI) {
        this.NOMBRE_POI = NOMBRE_POI;
    }

    public String getDESCRIPCION_POI() {
        return DESCRIPCION_POI;
    }

    public void setDESCRIPCION_POI(String DESCRIPCION_POI) {
        this.DESCRIPCION_POI = DESCRIPCION_POI;
    }

    public String getTIPO_POI() {
        return TIPO_POI;
    }

    public void setTIPO_POI(String TIPO_POI) {
        this.TIPO_POI = TIPO_POI;
    }

    public int getPISO_POI() {
        return PISO_POI;
    }

    public void setPISO_POI(int PISO_POI) {
        this.PISO_POI = PISO_POI;
    }

    public LatLng getPUNTO_POI() {
        return PUNTO_POI;
    }

    public void setPUNTO_POI(LatLng PUNTO_POI) {
        this.PUNTO_POI = PUNTO_POI;
    }
}
