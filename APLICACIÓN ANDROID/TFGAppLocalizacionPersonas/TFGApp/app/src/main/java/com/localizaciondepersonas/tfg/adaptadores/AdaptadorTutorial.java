package com.localizaciondepersonas.tfg.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.localizaciondepersonas.tfg.R;
import com.localizaciondepersonas.tfg.auxiliar.GuiaTutorial;

import java.util.ArrayList;

public class AdaptadorTutorial extends RecyclerView.Adapter<AdaptadorTutorial.ViewHolder> {
    /*
            INFO:   Adaptador para cargar el listado de vistas del tutorial para su visulalizacion
     */
    //----------------------------------------------------------------------------------------------- ATRIBUTOS
    // CONTEXTO
    private Context contexto;
    // VIEWHOLDER
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_titulo;
        private ImageView imageView_icono;
        private TextView textView_explicacion;
        //ºººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººº CONSTRUCTOR
        public ViewHolder(View itemView){
            super(itemView);
            // Configuracion relacion interfaz-logica
            textView_titulo = (TextView) itemView.findViewById(R.id.textView_titulo_elGuia);
            imageView_icono = (ImageView) itemView.findViewById(R.id.imageView_icono_elGuia);
            textView_explicacion = (TextView) itemView.findViewById(R.id.textView_explicacion_elGuia);
        }
    }
    // LOGICA
    private ArrayList<GuiaTutorial> listadoGuias;
    //----------------------------------------------------------------------------------------------- CONSTRUCTOR
    public AdaptadorTutorial(ArrayList<GuiaTutorial> listadoGuias){
        this.listadoGuias = listadoGuias;
    }
    //----------------------------------------------------------------------------------------------- METODOS ADAPTADOR
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Creamos vista
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_guia,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Guia tutorial
        GuiaTutorial guia = listadoGuias.get(position);
        // Configuracion de informacion en vista
        holder.textView_titulo.setText(guia.getTITULO_GUIA());
        holder.imageView_icono.setImageResource(guia.getIMAGEN_GUIA());
        holder.textView_explicacion.setText(guia.getEXPLICACION_GUIA());
    }

    @Override
    public int getItemCount() {
        return listadoGuias.size();
    }
}
