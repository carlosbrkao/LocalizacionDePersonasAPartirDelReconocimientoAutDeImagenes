package com.localizaciondepersonas.tfg.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.localizaciondepersonas.tfg.adaptadores.AdaptadorTutorial;
import com.localizaciondepersonas.tfg.auxiliar.GuiaTutorial;
import com.localizaciondepersonas.tfg.R;

import java.util.ArrayList;


public class FAppTutorial extends Fragment {
    /*
            INFO:   Fragment que presenta la opción del tutorial al usuario y gestiona su funcionamiento.
     */
    //----------------------------------------------------------------------------------------------- ATRIBUTOS
    // DISEÑO
    private LinearLayout linearLayout_estado;
    private TextView[] puntosGuia;
    // LOGICA
    private ArrayList<GuiaTutorial> listadoGuiaTutorial;
    //----------------------------------------------------------------------------------------------- CONSTRUCTOR
    public FAppTutorial() {
        // Required empty public constructor
    }
    public static FAppTutorial newInstance() {
        FAppTutorial fragment = new FAppTutorial();
        return fragment;
    }
    //----------------------------------------------------------------------------------------------- METODOS FRAGMENT
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Creacion de la informacion a desplegar en el tutorial
        if(listadoGuiaTutorial == null){
            listadoGuiaTutorial = new ArrayList<>();
            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloAplicacion),R.drawable.iconotfg,getString(R.string.tutorialAplicacion)));
            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloMenu),R.drawable.tutorial_menu,getString(R.string.tutorialMenu)));
            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloOpcionLocalizate),R.drawable.localizate,getString(R.string.tutorialOpcionLocalizate)));
            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloTomarFoto),R.drawable.tutorial_tomando_foto,getString(R.string.tutorialTomarFoto)));
            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloFotoTomada),R.drawable.tutorial_foto_tomada,getString(R.string.tutorialFotoTomada)));
            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloLocalizando),R.drawable.tutorial_carga_localizacion,getString(R.string.tutorialLocalizando)));
            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloLocalizado),R.drawable.tutorial_localizacion,getString(R.string.tutorialLocalizado)));

            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloOpcionMapa),R.drawable.mapa,getString(R.string.tutorialOpcionMapa)));
            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloMapa),R.drawable.tutorial_mapa,getString(R.string.tutorialMapa)));
            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloIconoPOI),R.drawable.poi,getString(R.string.tutorialIconoPOI)));
            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloIconoUbicacion),R.drawable.ubicacion,getString(R.string.tutorialIconoUbicacion)));
            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloPanelInfo),R.drawable.tutorial_panelinfo,getString(R.string.tutorialPanelInfo)));
            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloCamara),R.drawable.camara,getString(R.string.tutorialCamara)));
            listadoGuiaTutorial.add(new GuiaTutorial(getString(R.string.tutorialTituloSoporte),R.drawable.soporte,getString(R.string.tutorialSoporte)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar layout
        View v = inflater.inflate(R.layout.f_app_tutorial, container, false);
        // Configuracion de relacion diseño-logica
        ViewPager2 viewPager2_tutorial = v.findViewById(R.id.viewPager_contenedorTutorial_tutorial);
        linearLayout_estado = v.findViewById(R.id.linearLayout_contenedorEstado_tutorial);
        // Configuracion viewPager
        AdaptadorTutorial adaptadorTutorial = new AdaptadorTutorial(listadoGuiaTutorial);
        viewPager2_tutorial.setAdapter(adaptadorTutorial);
        // Configuracion puntos guia para indicar proceso del tutorial
        puntosGuia = new TextView[listadoGuiaTutorial.size()];
        // Añadimos puntos al contenedor
        for(int i=0; i<puntosGuia.length; i++){
            puntosGuia[i] = new TextView(getContext());
            puntosGuia[i].setText(Html.fromHtml("&#9679;"));
            puntosGuia[i].setTextSize(35);
            linearLayout_estado.addView(puntosGuia[i]);
        }
        // Configuracion transiccion de los puntos mediante el viewPager
        viewPager2_tutorial.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Actualizamos puntos guia con el estado actual, solo marcando con color el estado actual
                for(int i=0; i<puntosGuia.length; i++){
                    if(i == position) puntosGuia[i].setTextColor(getContext().getColor(R.color.tostado));
                    else puntosGuia[i].setTextColor(getContext().getColor(R.color.blanco));
                }
            }
        });

        return v;
    }
    //----------------------------------------------------------------------------------------------- METODOS PUBLICOS
    //----------------------------------------------------------------------------------------------- POP UPS
    //----------------------------------------------------------------------------------------------- METODOS PRIVADOS
}