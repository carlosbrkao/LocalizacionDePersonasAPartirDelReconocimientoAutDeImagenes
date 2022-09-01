package com.localizaciondepersonas.tfg.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.localizaciondepersonas.tfg.R;


public class FAppMenu extends Fragment {
    /*
            INFO:   Fragment encargado de hacer fluir la aplicacion al gestionar las decisiones que toma
                    el usuario en el Menu y notificando a la actividad manejadora la funcion que se quiere
                    ejecutar.
     */
    //----------------------------------------------------------------------------------------------- ATRIBUTOS
    // INTERFAZ
    public interface OnEventos_FAppMenu{
        void seleccionLocalizate();
        void seleccionMapa();
        void seleccionTutorial();
        void seleccionSoporte();
    }
    private OnEventos_FAppMenu listener;
    //----------------------------------------------------------------------------------------------- CONSTRUCTOR
    public FAppMenu() {
        // Required empty public constructor
    }
    public static FAppMenu newInstance(OnEventos_FAppMenu listener) {
        FAppMenu fragment = new FAppMenu();
        fragment.listener = listener;
        return fragment;
    }
    //----------------------------------------------------------------------------------------------- METODOS FRAGMENT
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar layout
        View v = inflater.inflate(R.layout.f_app_menu, container, false);
        // Configuracion de relacion diseño-logica
        CardView cardView_localizate = v.findViewById(R.id.cardView_botonLocalizate_menu);
        CardView cardView_mapa = v.findViewById(R.id.cardView_botonMapa_menu);
        CardView cardView_tutorial = v.findViewById(R.id.cardView_botonTutorial_menu);
        CardView cardView_soporte = v.findViewById(R.id.cardView_botonSoporte_menu);
        // Configuracion de listeners
        cardView_localizate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Notificamos a la clase contenedora de la accion de la opcion LOCALIZATE
                listener.seleccionLocalizate();
            }
        });
        cardView_mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Notificamos a la clase contenedora de la accion de la opcion MAPA
                listener.seleccionMapa();
            }
        });
        cardView_tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Notificamos a la clase contenedora de la accion de la opcion TUTORIAL
                listener.seleccionTutorial();
            }
        });
        cardView_soporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Notificamos a la clase contenedora de la accion de la opcion SOPORTE
                listener.seleccionSoporte();
            }
        });
        return v;
    }
}