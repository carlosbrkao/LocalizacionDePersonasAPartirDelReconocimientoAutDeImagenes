package com.localizaciondepersonas.tfg;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.localizaciondepersonas.tfg.fragments.FAppMapa;
import com.localizaciondepersonas.tfg.fragments.FAppMenu;
import com.localizaciondepersonas.tfg.fragments.FAppSoporte;
import com.localizaciondepersonas.tfg.fragments.FAppSplashInicio;
import com.localizaciondepersonas.tfg.fragments.FAppSplashMenciones;
import com.localizaciondepersonas.tfg.fragments.FAppTutorial;
import com.localizaciondepersonas.tfg.popup.PopUpSalirApp;

public class Aplicacion extends AppCompatActivity {
    /*
            INFO:   Actividad manejadora de la aplicacion gestionando el manejo y ejecucion de los distintos
                    fragments.
     */
    //----------------------------------------------------------------------------------------------- ATRIBUTOS
    // LOGICA
    private final int ID_RESPUESTA_PERMISOS = 10;
    // FRAGMENTS
    private FAppSplashInicio fragmentSplashInicio;
    private FAppSplashMenciones fragmentSplashMenciones;
    private FAppMenu fragmentMenu;
    private FAppMapa fragmentMapa;
    private FAppTutorial fragmentTutorial;
    private FAppSoporte fragmentSoporte;
    // CONTROL DE FRAGMENTS
    private boolean fSplashInicioActivo = false;
    private boolean fSplashMencionesActivo = false;
    private boolean fMenuActivo = false;
    private boolean fMapaActivo = false;
    private boolean fTutorialActivo = false;
    private boolean fSoporteActivo = false;
    //----------------------------------------------------------------------------------------------- TAREA ASYNC
    //----------------------------------------------------------------------------------------------- METODOS ACTIVITY
    // METODO DE ACTIVIDAD PARA LA CREACIÓN DE LA VISTA
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Aplicacion.onCreate","CREANDO ACTIVITY...");
        super.onCreate(savedInstanceState);
        // Relacion con diseño
        setContentView(R.layout.aplicacion);
        // Comprobacion de permisos
        comprobacionPermisos();
        // Instanciamos inicio
        inicioSplash();
    }

    // METODO DE ACTIVIDAD PARA LA GESTION DE LA PULSACION DE OPCIÓN ATRÁS EN LA NAVEGACION DEL MOVIL
    @Override
    public void onBackPressed() {
        if(fSplashInicioActivo || fSplashMencionesActivo || fMenuActivo) {
            // Pop up para salir de la aplicacion
            popUpSalirAppShow();
        }else if(fMapaActivo){
            // Retrocedemos al menu
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_contenedorFragment_aplicacion,fragmentMenu).commit();
            fMapaActivo = false;
            fMenuActivo = true;
        }else if(fTutorialActivo){
            // Retrocedemos al menu
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_contenedorFragment_aplicacion,fragmentMenu).commit();
            fTutorialActivo = false;
            fMenuActivo = true;
        }else if(fSoporteActivo){
            // Retrocedemos al menu
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_contenedorFragment_aplicacion,fragmentMenu).commit();
            fSoporteActivo = false;
            fMenuActivo = true;
        }
    }

    // METODO DE ACTIVIDAD PARA MANEJAR LA RESPUESTA A LA SOLICITUD DE PERMISOS
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == ID_RESPUESTA_PERMISOS){
            // Respuesta de solicitud de permisos
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // PERMISOS ACEPTADOS
                Log.d("Aplicacion.onRequestPermissionsResult","ESTADO DE PERMISOS [OK]");
            }else{
                // PERMISOS DENEGADOS
                // Cerramos la aplicacion
                finishAndRemoveTask();
                Log.e("Aplicacion.onRequestPermissionsResult","ESTADO DE PERMISOS [FAIL]");
            }
        }
    }

    //----------------------------------------------------------------------------------------------- METODOS PRIVADOS
    // METODO DE INICIO APP CON SPLASH DOBLE
    private void inicioSplash(){
        fragmentSplashInicio = FAppSplashInicio.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_contenedorFragment_aplicacion,fragmentSplashInicio).commit();
        fSplashInicioActivo = true;
        // Configuramos la transicion a la vista de patrocinadores y colaboraciones al cabo de 3 segundos
        Handler splashInicio = new Handler();
        splashInicio.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Instanciamos menciones
                fragmentSplashMenciones = FAppSplashMenciones.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_contenedorFragment_aplicacion,fragmentSplashMenciones).commit();
                fSplashMencionesActivo = true;
                fSplashInicioActivo = false;
                // Configuramos la transicion a la vista de menu al cabo de 4 segundos
                Handler splashMeciones = new Handler();
                splashMeciones.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Instanciamos menu
                        fragmentMenu = instanciaFAppMenu();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_contenedorFragment_aplicacion,fragmentMenu).commit();
                        fMenuActivo = true;
                        fSplashMencionesActivo = false;
                    }
                },4000);
            }
        },3000);
    }

    // METODO PARA COMPROBAR PERMISOS DE APLICACION
    private void comprobacionPermisos(){
        // Estado de permisos
        int estadoPermisoCamara = ContextCompat.checkSelfPermission(Aplicacion.this, Manifest.permission.CAMERA);
        int estadoPermisoEscritura = ContextCompat.checkSelfPermission(Aplicacion.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // Solicitud de permisos que faltan
        if(estadoPermisoCamara != PackageManager.PERMISSION_GRANTED && estadoPermisoEscritura != PackageManager.PERMISSION_GRANTED){
            // Sin permisos de CAMERA y WRITE_EXTERNAL_STORAGE, se solicitan
            ActivityCompat.requestPermissions(Aplicacion.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},ID_RESPUESTA_PERMISOS);
            Log.d("Aplicacion.comprobacionPermisos","ESTADO DE PERMISOS [CAMARA:FAIL][ESCRITURA:FAIL]");
        }else if(estadoPermisoCamara != PackageManager.PERMISSION_GRANTED){
            // Sin permisos de CAMERA, se solicitan
            ActivityCompat.requestPermissions(Aplicacion.this,new String[]{Manifest.permission.CAMERA},ID_RESPUESTA_PERMISOS);
            Log.d("Aplicacion.comprobacionPermisos","ESTADO DE PERMISOS [CAMARA:FAIL][ESCRITURA:OK]");
        }else if(estadoPermisoEscritura != PackageManager.PERMISSION_GRANTED){
            // Sin permisos de WRITE_EXTERNAL_STORAGE, se solicitan
            ActivityCompat.requestPermissions(Aplicacion.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},ID_RESPUESTA_PERMISOS);
            Log.d("Aplicacion.comprobacionPermisos","ESTADO DE PERMISOS [CAMARA:OK][ESCRITURA:FAIL]");
        }
    }
    //----------------------------------------------------------------------------------------------- METODOS POPUP
    // METODO PARA MOSTRAR POPUP PARA CONFIRMAR SALIDA DE APP
    private void popUpSalirAppShow(){
        // Cargamos dialog
        PopUpSalirApp popUpSalirApp = PopUpSalirApp.getInstance(new PopUpSalirApp.OnEventosFragmentSalirApp() {
            @Override
            public void salirApp() {
                // Cerramos aplicacion
                finishAndRemoveTask();
                Log.d("Aplicacion.popUpSalirAppShow","CERRANDO APLICACION...");
            }
        });
        // Mostrar popup
        popUpSalirApp.show(getSupportFragmentManager(),"POP UP SALIR APP");
    }
    //----------------------------------------------------------------------------------------------- METODOS FRAGMENT
    // METODO PARA INSTANCIAR FRAGMENT MENU
    private FAppMenu instanciaFAppMenu() {
        return FAppMenu.newInstance(new FAppMenu.OnEventos_FAppMenu() {
            @Override
            public void seleccionLocalizate() {
                // CAMBIO DE FRAGMENT: MENU --> MAPA (CON LOCALIZACION ACTIVA)
                fragmentMapa = instanciaFAppMapa(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_contenedorFragment_aplicacion, fragmentMapa).commit();
                fMenuActivo = false;
                fMapaActivo = true;
            }

            @Override
            public void seleccionMapa() {
                // CAMBIO DE FRAGMENT: MENU --> MAPA (CON LOCALIZACION DESHABILITADA)
                fragmentMapa = instanciaFAppMapa(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_contenedorFragment_aplicacion, fragmentMapa).commit();
                fMenuActivo = false;
                fMapaActivo = true;
            }

            @Override
            public void seleccionTutorial() {
                // CAMBIO DE FRAGMENT: MENU --> TUTORIAL
                fragmentTutorial = instanciaFAppTutorial();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_contenedorFragment_aplicacion, fragmentTutorial).commit();
                fMenuActivo = false;
                fTutorialActivo = true;
            }

            @Override
            public void seleccionSoporte() {
                // CAMBIO DE FRAGMENT: MENU --> SOPORTE
                fragmentSoporte = instanciaFAppSoporte();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_contenedorFragment_aplicacion, fragmentSoporte).commit();
                fMenuActivo = false;
                fSoporteActivo = true;
            }
        });
    }

    // METODO PARA INSTANCIAR FRAGMENT MAPA
    private FAppMapa instanciaFAppMapa(boolean localizacionActiva){
        return FAppMapa.newInstance(localizacionActiva);
    }
    // METODO PARA INSTANCIAR FRAGMENT TUTORIAL
    private FAppTutorial instanciaFAppTutorial(){
        return FAppTutorial.newInstance();
    }
    // METODO PARA INSTANCIAR FRAGMENT SOPORTE
    private FAppSoporte instanciaFAppSoporte(){
        return FAppSoporte.newInstance();
    }
}