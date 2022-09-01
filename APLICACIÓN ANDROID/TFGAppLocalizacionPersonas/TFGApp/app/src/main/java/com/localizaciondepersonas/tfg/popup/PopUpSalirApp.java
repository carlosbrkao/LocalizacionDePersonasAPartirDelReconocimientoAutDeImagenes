package com.localizaciondepersonas.tfg.popup;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.localizaciondepersonas.tfg.R;

public class PopUpSalirApp extends DialogFragment {
    /*
            INFO:   Dialog que se despliega como una ventana flotante para permitir al usuario cerrar
                    la aplicación desde el Menu.
     */
    //----------------------------------------------------------------------------------------------- ATRIBUTOS
    // INTERFAZ
    public interface OnEventosFragmentSalirApp{
        void salirApp();
    }
    private OnEventosFragmentSalirApp listener;
    //----------------------------------------------------------------------------------------------- CONSTRUCTOR
    private PopUpSalirApp() { }
    public static PopUpSalirApp getInstance(OnEventosFragmentSalirApp listener){
        PopUpSalirApp popUpSalirApp = new PopUpSalirApp();
        popUpSalirApp.listener = listener;
        return popUpSalirApp;
    }
    //----------------------------------------------------------------------------------------------- METODOS POP UP
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflador de vista
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Carga de vista
        View v = inflater.inflate(R.layout.popup_salir_app, null);
        // Vinculación con vista
        builder.setView(v);
        // Configuracion relacion interfaz-logica
        CardView cardView_cancelar = (CardView) v.findViewById(R.id.cardView_botonCancelar_popUpSalirApp);
        CardView cardView_salir = (CardView) v.findViewById(R.id.cardView_botonSalir_popUpSalirApp);
        // Configuracion cardViews
        cardView_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cerramos el popup y nos mantenemos en la aplicacion
                dismiss();
            }
        });
        cardView_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Notificamos a la clase contenedora la accion de SALIR DE APP
                listener.salirApp();
                dismiss();
            }
        });
        // Bloqueo de acciones fuera del popup que puedan cerrar esta
        setCancelable(false);

        return builder.create();
    }
    //----------------------------------------------------------------------------------------------- METODOS PRIVADOS
}
