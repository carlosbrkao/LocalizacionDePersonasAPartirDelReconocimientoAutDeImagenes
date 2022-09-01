package com.localizaciondepersonas.tfg.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.localizaciondepersonas.tfg.R;


public class FAppSplashInicio extends Fragment {
    /*
            INFO:   Fragment cuyo unico funcionamiento es visual al cargar la vista Splash inicial para
                    dar la bienvenida al usuario mostrando el logo y el titulo de la aplicacion .
     */
    //----------------------------------------------------------------------------------------------- ATRIBUTOS
    //----------------------------------------------------------------------------------------------- CONSTRUCTOR
    public FAppSplashInicio() {
        // Required empty public constructor
    }
    public static FAppSplashInicio newInstance() {
        FAppSplashInicio fragment = new FAppSplashInicio();
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
        View v = inflater.inflate(R.layout.f_app_splash_inicio, container, false);
        return v;
    }
}