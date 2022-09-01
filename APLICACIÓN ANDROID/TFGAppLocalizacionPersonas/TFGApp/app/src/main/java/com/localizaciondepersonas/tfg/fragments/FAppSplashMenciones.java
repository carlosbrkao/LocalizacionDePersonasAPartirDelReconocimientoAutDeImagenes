package com.localizaciondepersonas.tfg.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.localizaciondepersonas.tfg.R;


public class FAppSplashMenciones extends Fragment {
    /*
            INFO:   Fragment cuyo unico funcionamiento es visual al cargar la vista Splash con menciones
                    de las partes importantes involucradas en el desarrollo del proyecto.
     */
    //----------------------------------------------------------------------------------------------- ATRIBUTOS
    //----------------------------------------------------------------------------------------------- CONSTRUCTOR
    public FAppSplashMenciones() {
        // Required empty public constructor
    }
    public static FAppSplashMenciones newInstance() {
        FAppSplashMenciones fragment = new FAppSplashMenciones();
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
        View v = inflater.inflate(R.layout.f_app_splash_menciones, container, false);
        return v;
    }
}