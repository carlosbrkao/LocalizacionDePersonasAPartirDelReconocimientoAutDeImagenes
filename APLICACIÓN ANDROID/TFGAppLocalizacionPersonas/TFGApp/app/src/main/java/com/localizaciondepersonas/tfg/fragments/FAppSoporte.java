package com.localizaciondepersonas.tfg.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.localizaciondepersonas.tfg.R;


public class FAppSoporte extends Fragment {
    /*
            INFO:   Fragment cuyo unico funcionamiento es visual al cargar la vista donde se mostrarían los
                    datos de contacto con el soporte tecnico detras del funcionamiento de la aplicacion.
     */
    //----------------------------------------------------------------------------------------------- ATRIBUTOS
    //----------------------------------------------------------------------------------------------- CONSTRUCTOR
    public FAppSoporte() {
        // Required empty public constructor
    }
    public static FAppSoporte newInstance() {
        FAppSoporte fragment = new FAppSoporte();
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
        View v = inflater.inflate(R.layout.f_app_soporte, container, false);
        return v;
    }
    //----------------------------------------------------------------------------------------------- METODOS PUBLICOS
    //----------------------------------------------------------------------------------------------- POP UPS
    //----------------------------------------------------------------------------------------------- METODOS PRIVADOS
}