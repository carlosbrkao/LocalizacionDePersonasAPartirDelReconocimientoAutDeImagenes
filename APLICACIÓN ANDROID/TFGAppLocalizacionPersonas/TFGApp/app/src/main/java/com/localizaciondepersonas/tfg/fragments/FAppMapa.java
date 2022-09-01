package com.localizaciondepersonas.tfg.fragments;

import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.localizaciondepersonas.tfg.auxiliar.POI;
import com.localizaciondepersonas.tfg.R;
import com.localizaciondepersonas.tfg.ml.ModelMuseo;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.turf.TurfJoins;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FAppMapa extends Fragment {
    /*
            INFO:   Fragment principal de la aplicacion que recoge el comportamiento tanto cuando se intenta
                    localizar el usuario, tanto como si el usuario desea navegar libremente por el mapa
     */
    //----------------------------------------------------------------------------------------------- INFO
    // FORMATO MAPA PARES ETIQUETA+POI : Hashmap<NOMBRE_POI+"_"+PISO_POI,POI>
    //----------------------------------------------------------------------------------------------- ATRIBUTOS
    // MAPBOX
    private MapboxMap mapboxMap;
    private Style mapboxStyle;
    private SymbolManager manejadorSimbolos;
    private GeoJsonSource capaTrazosPlanta, capaPuntosPOI, capaLocalizacionPOI;
    private static final String JSON_PLANTA = "JSON_PLANTA";
    private static final String CAPA_PLANTA_RELLENO = "CAPA_RELLENO";
    private static final String CAPA_PLANTA_LINEAS = "CAPA_DELINEADO";
    private static final String JSON_LOCALIZACION = "JSON_LOCALIZACION";
    private static final String ICONO_LOCALIZACION = "ICONO_LOCALIZACION";
    private static final String CAPA_LOCALIZACION = "CAPA_LOCALIZACION";
    private static final String JSON_POIs = "JSON_POIs";
    private static final String ICONO_POIs = "ICONO_POIs";
    private static final String CAPA_POIs = "CAPA_POIs";
    private HashMap<String, POI> mapaParesEtiquetaPOI;
    private List<Feature> listadoPOIsPB;
    private POI POILocacizacion,POIActual;
    // DISEÑO
    private MapView mapView_mapa;
    private TextView textView_poi,textView_ubicacion,textView_descripcion,textView_probabilidad;
    private CardView cardView_probabilidad;
    private ImageView imageView_iconoMinMax;
    private LinearLayout linearLayout_contendorInfo;
    private ProgressBar progressBar_procesando;
    // LOGICA
    private boolean ventanaInformacionOculta = false,encuadreDesenfocado = false;
    private String etiquetaLocalizacionInferencia;
    // LOGICA
    private boolean localizacionActiva = false;
    private boolean procesoInferenciaActivo = false,porcentajesVisibles = false;
    // MODELO TF LITE
    private final int dimensionImagenEntrada = 300;
    private Bitmap imagenEntrada;
    private ActivityResultLauncher<Intent> onActivityResultCamara;
    private String rutaFotoTemporal;
    //----------------------------------------------------------------------------------------------- TAREAS ASYNC
    // TAREA PARA REALIZAR EL PROCESO DE INFERENCIA DE LA IMAGEN CAPTURADA
    private class TareaLocalizacionIA extends AsyncTask<Void,Integer, List<Category>> {
        //ºººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººº ATRIBUTOS
        private final Bitmap imagenLocalizacion;
        //ºººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººº CONSTRUCTOR
        public TareaLocalizacionIA(Bitmap imagenLocalizacion) {
            this.imagenLocalizacion = imagenLocalizacion;
        }
        //ºººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººº METODOS TAREA
        @Override
        protected List<Category> doInBackground(Void... voids) {
            Log.d("FAppMapa.TareaLocalizacionIA", "INICIO PROCESO INFERENCIA ");
            try {
                // Instanciamos modelo
                ModelMuseo modelo = ModelMuseo.newInstance(getContext());
                // Transforma la imagen bitmap para la entrada al modelo
                TensorImage image = TensorImage.fromBitmap(imagenLocalizacion);
                // Inicio de inferencia
                ModelMuseo.Outputs resultados = modelo.process(image);
                // Obtenemos resultados
                List<Category> listadoProbabilidades = resultados.getProbabilityAsCategoryList();
                // Cerramos el modelo
                modelo.close();
                return listadoProbabilidades;
            } catch (IOException ioe) {
                Log.e("FAppMapa.TareaLocalizacionIA", "ERROR: " + ioe.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Category> listadoProbabilidades) {
            /*
                    FORMATO ETIQUETA CLASIFICADOR SALAS:    SALA_PISO
                    FORMATO ETIQUETA CLASIFICADOR VITRINAS: SALA_PISO_NºVITRINA
             */
            Log.d("FAppMapa.TareaLocalizacionIA", "COMPROBACION RESULTADO INFERENCIA");
            super.onPostExecute(listadoProbabilidades);
            // Indicamos final del proceso
            if (listadoProbabilidades != null) {
                // INFERENCIA FINALIZADA CON EXITO
                Log.i("FAppMapa.TareaLocalizacionIA", "PROCESO INFERENCIA [OK]");
                // Ordenamiento del listado de mayor a menor probabilidad
                HashMap<String, Float> paresEtiquetaPorcentaje = new HashMap<>();
                for (int i = 0; i < listadoProbabilidades.size(); i++) paresEtiquetaPorcentaje.put(listadoProbabilidades.get(i).getLabel(), listadoProbabilidades.get(i).getScore());
                ArrayList<String> etiquetasOrdenMayorMenor = getEtiquetaOrdenMayorMenor(paresEtiquetaPorcentaje);
                // Listado de probabilidades (SOLO ACCESIBLE POR CONSOLA PARA PRUEBAS)
                for (int i = 0; i < etiquetasOrdenMayorMenor.size(); i++)  Log.i("FAppMapa.PORCENTAJES INFERENCIA", "ETIQUETA: " + etiquetasOrdenMayorMenor.get(i) + " [" + String.format("%.2f", paresEtiquetaPorcentaje.get(etiquetasOrdenMayorMenor.get(i))) + "]");
                // Rellenamos informacion del resultado de la prediccion
                etiquetaLocalizacionInferencia = etiquetasOrdenMayorMenor.get(0).split("_")[0]+"_"+etiquetasOrdenMayorMenor.get(0).split("_")[1];
                textView_probabilidad.setText(String.format("%.2f",paresEtiquetaPorcentaje.get(etiquetasOrdenMayorMenor.get(0))*100)+" %");
                // En caso de ser un modelo clasificador de vitrina, mostramos la detectada
                if(etiquetasOrdenMayorMenor.get(0).split("_").length == 3){
                    textView_descripcion.setText("VITRINA IDENTIFICADA: Nº "+etiquetasOrdenMayorMenor.get(0).split("_")[2]);
                }
                // Escala cromatica de fiabilidad de prediccion
                if(paresEtiquetaPorcentaje.get(etiquetasOrdenMayorMenor.get(0)) > 0.60f) cardView_probabilidad.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.verde));
                else if(paresEtiquetaPorcentaje.get(etiquetasOrdenMayorMenor.get(0)) > 0.30f) cardView_probabilidad.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.tostado));
                else cardView_probabilidad.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.rojo));
                cardView_probabilidad.setVisibility(View.VISIBLE);
                textView_probabilidad.setVisibility(View.VISIBLE);
                gestionLocalizacionExterna();
            } else {
                // INFERENCIA FALLIA
                Log.e("FAppMapa.TareaLocalizacionIA", "PROCESO INFERENCIA [FAIL]");
                Toast.makeText(getContext(), getString(R.string.avisoFotoErronea), Toast.LENGTH_LONG).show();
            }
            // HABILITAMOS VISTAS CON FINAL DE INFERENCIA
            procesoInferenciaActivo = false;
            gestionVistas();
        }
        //ººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººººº METODOS PRIVADOS
        // METODO PARA ORDENAR LISTADO DE RESULTADOS
        private ArrayList<String> getEtiquetaOrdenMayorMenor(HashMap<String,Float> auxEtiquetaPorcentaje){
            // Obtenemos listado de valores (menor a mayor)
            List<Map.Entry<String, Float>> listadoMenorMayor = new ArrayList<>(auxEtiquetaPorcentaje.entrySet());
            listadoMenorMayor.sort(Map.Entry.comparingByValue());
            // Reordenamos de mayor a menor
            ArrayList<String> listadoEtiquetasMayorMenor = new ArrayList<>();
            for(int i=listadoMenorMayor.size()-1; i >= 0; i--) listadoEtiquetasMayorMenor.add(listadoMenorMayor.get(i).getKey());
            return listadoEtiquetasMayorMenor;
        }
    }
    //----------------------------------------------------------------------------------------------- CONSTRUCTOR
    public FAppMapa() {}
    public static FAppMapa newInstance(Boolean localizacionActiva) {
        FAppMapa fragment = new FAppMapa();
        fragment.localizacionActiva = localizacionActiva;
        return fragment;
    }
    //----------------------------------------------------------------------------------------------- METODOS FRAGMENT
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Configuracion TOKEN Mapbox SDK
        Mapbox.getInstance(getContext(),getString(R.string.TOKEN_MAPBOX_PUBLICO));
        // Instanciamos manejador de respuesta de camara
        manejadorOnActivityResultCamara();
        // Control configuracion de localizacion
        if(localizacionActiva) llamadaCamaraRutaGuardadoTemporal();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar layout
        View v = inflater.inflate(R.layout.f_app_mapa, container, false);
        // Configuracion relacion diseño-logica
        mapView_mapa = v.findViewById(R.id.mapView_mapa_mapa);
        progressBar_procesando = v.findViewById(R.id.progressBar_cargando_mapa);
        cardView_probabilidad = v.findViewById(R.id.cardView_contenedorProbabilidad_mapa);
        textView_probabilidad = v.findViewById(R.id.textView_probabilidad_mapa);
        linearLayout_contendorInfo = v.findViewById(R.id.linearLayout_contenedorInformacion_mapa);
        textView_poi = v.findViewById(R.id.textView_poi_mapa);
        textView_ubicacion = v.findViewById(R.id.textView_ubicacion_mapa);
        textView_descripcion = v.findViewById(R.id.textView_descripcion_mapa);
        CardView cardView_botonMinMax = v.findViewById(R.id.cardView_botonMinMax_mapa);
        imageView_iconoMinMax = v.findViewById(R.id.imageView_iconoMinMax_mapa);
        CardView cardView_botonCamara = v.findViewById(R.id.cardView_botonCamara_mapa);
        // Configuracion inicial
        ventanaInformacionOculta = false;
        manejadorMinizarMaximizarInfo();
        if(!localizacionActiva){
            cardView_probabilidad.setVisibility(View.INVISIBLE);
            textView_probabilidad.setVisibility(View.INVISIBLE);
            progressBar_procesando.setVisibility(View.INVISIBLE);
        }
        // Configuracion mapa
        mapView_mapa.onCreate(savedInstanceState);
        configuracionMapbox();
        // Configuracion cardviews
        cardView_botonMinMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!encuadreDesenfocado){
                    // Indicamos seleccion actual, ocultando/mostrando el panel informativo
                    ventanaInformacionOculta = !ventanaInformacionOculta;
                    manejadorMinizarMaximizarInfo();
                }
            }
        });
        cardView_botonCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciamos proceso de ubicacion
                // Movemos punto al centro del mapa
                CameraPosition position = new CameraPosition.Builder().target(new LatLng(40.63587, -3.16865)).zoom(20).build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position),2000);
                progressBar_procesando.setVisibility(View.VISIBLE);
                // Realizamos llamada a camara
                llamadaCamaraRutaGuardadoTemporal();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView_mapa.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView_mapa.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView_mapa.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView_mapa.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView_mapa.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView_mapa.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView_mapa.onSaveInstanceState(outState);
    }
    //----------------------------------------------------------------------------------------------- METODOS PUBLICOS
    //----------------------------------------------------------------------------------------------- POP UPS
    //----------------------------------------------------------------------------------------------- MAPBOX
    // METODO PARA LA CONFIGURACION INICIAL DEL MAPBOX
    private void configuracionMapbox(){
        mapView_mapa.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMapCargado) {
                // Recogemos intancia al cargarse el mapa para manejarlo
                mapboxMap = mapboxMapCargado;
                // Cargamos mapa de POI
                obtenerMapaParesEtiquetaPOI();
                // Carga del estilo del mapa
                mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Recogemos instancia al cargarse el estilo para manejarlo
                        mapboxStyle = style;
                        // Configuracion encuadre en area de localizacion
                        mapboxMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
                            @Override
                            public void onCameraMove() {
                                // Configuracion del encuadre del entorno de la localizacion
                                manejadorEncuadreAreaLocalizacion();
                            }
                        });
                        // Configuracion manejador de simbolos
                        manejadorSimbolos = new SymbolManager(mapView_mapa,mapboxMap,mapboxStyle,null,new GeoJsonOptions().withTolerance(0.4f));
                        manejadorSimbolos.setIconAllowOverlap(true);
                        manejadorSimbolos.setTextAllowOverlap(true);
                        // Configuracion capas
                        capaTrazosPlanta = new GeoJsonSource(JSON_PLANTA,cargarJSONDesdeAssets("PlantaBaja.geojson"));
                        mapboxStyle.addSource(capaTrazosPlanta);
                        cargarCapaEstructuras();
                        // Configuracion POIs
                        capaPuntosPOI = new GeoJsonSource(JSON_POIs, FeatureCollection.fromFeatures(listadoPOIsPB));
                        mapboxStyle.addSource(capaPuntosPOI);
                        capaLocalizacionPOI = new GeoJsonSource(JSON_LOCALIZACION, FeatureCollection.fromFeatures(new ArrayList<>()));
                        mapboxStyle.addSource(capaLocalizacionPOI);
                        cargarCapaPOIs();
                    }
                });
                // Configuracion listener cuando se hace click sobre el mapa
                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        return manejadorSeleccionPOI(point);
                    }
                });
            }
        });
    }
    // METODO PARA EL MANEJO DEL ENCUADRE EN EL AREA DE LOCALIZACION
    private void manejadorEncuadreAreaLocalizacion(){
        // Definimos cuadrícula de coordenadas que delimitan el area de localizacion
        final List<Point> cuadriculaEncuadre = new ArrayList<>();
        cuadriculaEncuadre.add(Point.fromLngLat(-3.17273, 40.637508));
        cuadriculaEncuadre.add(Point.fromLngLat(-3.17273, 40.630986));
        cuadriculaEncuadre.add(Point.fromLngLat(-3.157806, 40.630986));
        cuadriculaEncuadre.add(Point.fromLngLat(-3.157806, 40.637508));
        final List<List<Point>> listadoCuadriculasEncuadre = new ArrayList<>();
        listadoCuadriculasEncuadre.add(cuadriculaEncuadre);
        // Comprobacion de localizacion de camara tras movimiento detectado
        if(mapboxMap.getCameraPosition().zoom < 16 || !TurfJoins.inside(Point.fromLngLat(mapboxMap.getCameraPosition().target.getLongitude(), mapboxMap.getCameraPosition().target.getLatitude()), Polygon.fromLngLats(listadoCuadriculasEncuadre))){
            // Zoom demasiado amplio o camara ubicada fuera de la cuadricula de encuadre, ocultamos paneles
            encuadreDesenfocado = true;
            if(linearLayout_contendorInfo.getVisibility() == View.VISIBLE){
                // En caso de estar visible se oculta la informacion
                linearLayout_contendorInfo.setVisibility(View.INVISIBLE);
                linearLayout_contendorInfo.setEnabled(false);
                ventanaInformacionOculta = true;
                manejadorMinizarMaximizarInfo();
            }
            Log.d("FAppMapa.manejadorSeleccionPOI","MAPA DESENFOCADO");
        }else{
            // Zoom correcto y camara ubicada en la cuadricula de encuadre, mostramos paneles
            encuadreDesenfocado = false;
            manejadorMinizarMaximizarInfo();
            Log.d("FAppMapa.manejadorSeleccionPOI","MAPA ENFOCADO");
        }
    }
    // METODO PARA EL MANEJO DE LA SELECCION DE UN POI
    private boolean manejadorSeleccionPOI(LatLng puntoClickado){
        // Desmarcamos posible resultado de ubicacion
        etiquetaLocalizacionInferencia = null;
        cardView_probabilidad.setVisibility(View.INVISIBLE);
        textView_probabilidad.setVisibility(View.INVISIBLE);
        // Obtenemos listado de POIs en la proyeccion del click efectuado
        List<Feature> features = mapboxMap.queryRenderedFeatures(mapboxMap.getProjection().toScreenLocation(puntoClickado), CAPA_POIs);
        if (!features.isEmpty()) {
            // Indicamos info si se encuentra un POI en la proyeccion del click
            Log.d("FAppMapa.manejadorSeleccionPOI","PUNTO DETECTADO: ["+features.get(0).getStringProperty(getString(R.string.clavePropiedadFeatureIdentificador))+"]");
            POILocacizacion = mapaParesEtiquetaPOI.get(features.get(0).getStringProperty(getString(R.string.clavePropiedadFeatureIdentificador)));
            textView_poi.setText(POILocacizacion.getNOMBRE_POI().toUpperCase(Locale.ROOT));
            textView_ubicacion.setText("PB ["+String.format("%.6f",POILocacizacion.getPUNTO_POI().getLatitude())+" Lat. | "+String.format("%.6f",POILocacizacion.getPUNTO_POI().getLongitude())+" Long.]");
            textView_descripcion.setText(POILocacizacion.getDESCRIPCION_POI());
            // Movemos punto al centro del mapa
            CameraPosition position = new CameraPosition.Builder().target(new LatLng(POILocacizacion.getPUNTO_POI().getLatitude(), POILocacizacion.getPUNTO_POI().getLongitude())).zoom(20).build();
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position),2000);
            // Mostramos info
            ventanaInformacionOculta = false;
            manejadorMinizarMaximizarInfo();
            // Marcamos punto
            capaLocalizacionPOI.setGeoJson(Feature.fromGeometry(Point.fromLngLat(POILocacizacion.getPUNTO_POI().getLongitude(), POILocacizacion.getPUNTO_POI().getLatitude())));
            return true;
        } else {
            return false;
        }
    }
    // METODO PARA CARGAR FICHEROS GEOJSON DESDE ASSET
    private String cargarJSONDesdeAssets(String nombreGeoJson){
        Log.d("FAppMapaConsulta.cargarJSONDesdeAssets","CARGANDO GEOJSON...["+nombreGeoJson+"]");
        try {
            // Abrimos stream para leer fichero
            InputStream is = getContext().getAssets().open(nombreGeoJson);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, Charset.forName("UTF-8"));

        } catch (IOException ioe) {
            Log.e("FAppMapaConsulta.cargarJSONDesdeAssets","ERROR: "+ioe.getMessage());
            return null;
        }
    }
    // METODO PARA ORGANIZAR LOS POIs Y UN MAPA GENERAL DE PARES ETIQUETA+POI
    private void obtenerMapaParesEtiquetaPOI(){
        Log.d("FAppMapa.obtenerMapaParesEtiquetaPOIXPisos","INICIO OBTENCION MAPA PARES ETIQUETA+POI");
        mapaParesEtiquetaPOI = new HashMap<>();
        // Lectura POIs planta baja (PB)
        ArrayList<POI> auxListadoPOIsPB = cargaPOIs("POIsPlantaBaja.geojson");
        listadoPOIsPB = new ArrayList<>();
        for(int i=0; i<auxListadoPOIsPB.size(); i++){
            mapaParesEtiquetaPOI.put(auxListadoPOIsPB.get(i).getNOMBRE_POI()+"_"+auxListadoPOIsPB.get(i).getPISO_POI(),auxListadoPOIsPB.get(i));
            Feature feature = Feature.fromGeometry(Point.fromLngLat(auxListadoPOIsPB.get(i).getPUNTO_POI().getLongitude(), auxListadoPOIsPB.get(i).getPUNTO_POI().getLatitude()));
            feature.addStringProperty(getString(R.string.clavePropiedadFeatureIdentificador),auxListadoPOIsPB.get(i).getNOMBRE_POI()+"_"+auxListadoPOIsPB.get(i).getPISO_POI());
            listadoPOIsPB.add(feature);
        }
    }
    // METODO PARA CARGAR FICHEROS GEOJSON POIs DESDE ASSETS
    private ArrayList<POI> cargaPOIs(String nombreGeojson){
        // Preparamos variable de salida
        ArrayList<POI> listadoPOIs = new ArrayList<>();
        // Leemos archivo
        String[] contenidoGeojson = cargarJSONDesdeAssets(nombreGeojson).split("\\r\\n");
        for(int i=0; i<contenidoGeojson.length; i++){
            if(contenidoGeojson[i].contains("properties")){
                // Obtencion de la informacion conociendo la estructura del geojson
                String nombre = contenidoGeojson[i].split("\"name\":")[1].split(",")[0].replaceAll("\"","");
                String descripcion = contenidoGeojson[i].split("\"description\":")[1].split(",")[0].replaceAll("\"","");
                String tipo = contenidoGeojson[i].split("\"type\":")[2].split(",")[0].replaceAll("\"","");
                String piso = contenidoGeojson[i].split("\"floor\":")[1].split("\\}")[0].replaceAll("\"","");
                Point punto = Point.fromJson(contenidoGeojson[i].split("\"geometry\":")[1].split("\\}")[0] + "}");
                // Introduccion del punto
                listadoPOIs.add(new POI(nombre,descripcion,tipo,Integer.parseInt(piso),new LatLng(punto.latitude(),punto.longitude())));
            }
        }
        return listadoPOIs;
    }
    // METODO PARA CARGAR CAPA DE SALAS Y CONTORNOS
    private void cargarCapaEstructuras(){
        // Se carga la capa de relleno sobre la que se traza los contornos
        FillLayer indoorBuildingLayer = new FillLayer(CAPA_PLANTA_RELLENO, JSON_PLANTA).withProperties(
                fillColor(getContext().getColor(R.color.transparente)),
                // Function.zoom is used here to fade out the indoor layer if zoom level is beyond 16. Only
                // necessary to show the indoor map at high zoom levels.
                fillOpacity(interpolate(exponential(1f), zoom(),
                        stop(16f, 0f),
                        stop(16.5f, 0.5f),
                        stop(17f, 1f))));

        mapboxStyle.addLayer(indoorBuildingLayer);
        // Se carga la capa de contorno que representa las salas
        LineLayer indoorBuildingLineLayer = new LineLayer(CAPA_PLANTA_LINEAS, JSON_PLANTA).withProperties(
                lineColor(getContext().getColor(R.color.negro)),
                lineWidth(1f),
                lineOpacity(interpolate(exponential(1f), zoom(),
                        stop(16f, 0f),
                        stop(16.5f, 0.5f),
                        stop(17f, 1f))));
        //style.addLayer(indoorBuildingLineLayer);
        mapboxStyle.addLayerBelow(indoorBuildingLineLayer,"tunnel-street-minor-low");
    }
    // METODO PARA CARGAR CAPA DE POIs
    private void cargarCapaPOIs(){
        // Capa para marcadores de POIs
        mapboxStyle.addImage(ICONO_POIs,Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.poi),75,75,false));
        SymbolLayer symbolLayerPOIs = new SymbolLayer(CAPA_POIs, JSON_POIs);
        symbolLayerPOIs.setProperties(iconImage(ICONO_POIs), iconAllowOverlap(true), iconIgnorePlacement(true),iconAnchor(ICON_ANCHOR_BOTTOM)/*,textOffset(new Float[]{0f,-1f}),textIgnorePlacement(true),textAllowOverlap(true),textField(Expression.concat(get("IDENTIFICADOR"),Expression.literal("-"),get("TIPO")))*/);
        mapboxStyle.addLayer(symbolLayerPOIs);

        // Capa para marcador de punto de localizacion
        mapboxStyle.addImage(ICONO_LOCALIZACION,Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ubicacion),150,150,false));
        SymbolLayer symbolLayerLocalizacion = new SymbolLayer(CAPA_LOCALIZACION, JSON_LOCALIZACION);
        symbolLayerLocalizacion.setProperties(iconImage(ICONO_LOCALIZACION), iconAllowOverlap(true), iconIgnorePlacement(true),iconAnchor(ICON_ANCHOR_BOTTOM));
        mapboxStyle.addLayer(symbolLayerLocalizacion);
    }
    //----------------------------------------------------------------------------------------------- METODOS PRIVADOS
    // METODO GESTION DE BOTON MININIZAR, MAXIMIZAR
    private void manejadorMinizarMaximizarInfo(){
        if(ventanaInformacionOculta){
            // Informacion esta oculta
            linearLayout_contendorInfo.setVisibility(View.INVISIBLE);
            linearLayout_contendorInfo.setEnabled(false);
            imageView_iconoMinMax.setImageResource(R.drawable.maximizar);
            if(etiquetaLocalizacionInferencia != null){
                cardView_probabilidad.setVisibility(View.INVISIBLE);
                textView_probabilidad.setVisibility(View.INVISIBLE);
            }
        }else{
            // Informacion en pantalla
            linearLayout_contendorInfo.setVisibility(View.VISIBLE);
            linearLayout_contendorInfo.setEnabled(true);
            imageView_iconoMinMax.setImageResource(R.drawable.mininizar);
            if(etiquetaLocalizacionInferencia != null){
                cardView_probabilidad.setVisibility(View.VISIBLE);
                textView_probabilidad.setVisibility(View.VISIBLE);
            }
        }
    }
    // METODO PARA ENFOCAR PUNTO DE LOCALIZACION ENCONTRADO FUERA DE FRAGMENT
    private void gestionLocalizacionExterna(){
        if(etiquetaLocalizacionInferencia != null){
            // Indicamos info
            POILocacizacion = mapaParesEtiquetaPOI.get(etiquetaLocalizacionInferencia);
            textView_poi.setText(POILocacizacion.getNOMBRE_POI().toUpperCase(Locale.ROOT));
            textView_ubicacion.setText("PB ["+String.format("%.6f",POILocacizacion.getPUNTO_POI().getLatitude())+" Lat. | "+String.format("%.6f",POILocacizacion.getPUNTO_POI().getLongitude())+" Long.]");
            if(textView_descripcion.getText().toString().isEmpty()) textView_descripcion.setText(POILocacizacion.getDESCRIPCION_POI());
            else textView_descripcion.setText(textView_descripcion.getText().toString()+"\n"+POILocacizacion.getDESCRIPCION_POI());
            // Movemos punto al centro del mapa
            CameraPosition position = new CameraPosition.Builder().target(new LatLng(POILocacizacion.getPUNTO_POI().getLatitude(), POILocacizacion.getPUNTO_POI().getLongitude())).zoom(20).build();
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position),2000);
            // Marcamos punto
            capaLocalizacionPOI.setGeoJson(Feature.fromGeometry(Point.fromLngLat(POILocacizacion.getPUNTO_POI().getLongitude(), POILocacizacion.getPUNTO_POI().getLatitude())));
            // Desplegamos ventana
            ventanaInformacionOculta = false;
            manejadorMinizarMaximizarInfo();
        }
    }
    // METODO ON ACTIVITY RESULT PARA OBTENER IMAGEN DE CÁMARA
    private void manejadorOnActivityResultCamara(){
        Log.d("ManejadorOnActivityResultCamara","CONFIGURANDO ON ACTIVITY RESULT CAMARA...");
        // Configuracion de los eventos al recibir una foto
        onActivityResultCamara = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d("ManejadorOnActivityResultCamara","FOTO TOMADA...");
                if(result.getResultCode() == Activity.RESULT_OK){
                    manejoRespuestaCamaraRutaGuardadoTemporal();
                }
            }
        });
    }
    // METODO PARA GENERAR URI DE FICHERO TEMPORAL DE FOTO
    private void llamadaCamaraRutaGuardadoTemporal(){
        // Configuramos acceso a camara
        Intent abrirCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(abrirCamara.resolveActivity(getActivity().getPackageManager()) != null){
            // Creacion de fichero para almacenar la imagen tomada temporalmente
            File fotoTemporal = null;
            try {
                fotoTemporal = creacionImagenTemporal();
            }catch (IOException ioe){
                Log.e("FAppCamara.CardView_camara","ERROR: "+ioe.getMessage());
            }
            if(fotoTemporal != null){
                Uri uriFotoTemporal = FileProvider.getUriForFile(getContext(), "com.localizaciondepersonas.tfg.provider", fotoTemporal);
                abrirCamara.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoTemporal);
                // Llamamos a la camara de forma que al finalizar su operación, desencadene el metodo para gestionar el resultado de la camara
                onActivityResultCamara.launch(abrirCamara);
            }
        }
    }
    // METODO PARA MANEJO DE IMAGEN TEMPORAL
    private void manejoRespuestaCamaraRutaGuardadoTemporal(){
        // Obtenemos imagen original
        imagenEntrada = BitmapFactory.decodeFile(rutaFotoTemporal);
        try {
            // Almacenamiento de imagen con la rotación original de captura
            ExifInterface ei = new ExifInterface(rutaFotoTemporal);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    imagenEntrada = rotateImage(imagenEntrada, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    imagenEntrada = rotateImage(imagenEntrada, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    imagenEntrada = rotateImage(imagenEntrada, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    imagenEntrada = imagenEntrada;
            }
        }catch (IOException ioe){
            Log.e("FAppMapa.manejadorActivityResultCamera.onActivityResult","ERROR: "+ioe.getMessage());
        }
        // Reescalamos entrada para el formato adecuado del modelo de inferencia
        imagenEntrada = reescaladoBitmapConCalidad(imagenEntrada,dimensionImagenEntrada,dimensionImagenEntrada);
        // Eliminamos imagen temporal
        new File(rutaFotoTemporal).delete();
        // Lanzamos tarea de ubicacion
        new TareaLocalizacionIA(imagenEntrada).execute();
        procesoInferenciaActivo = true;
        gestionVistas();
    }
    // METODO PARA LA GENERACION TEMPORAL DE LA IMAGEN EN MEMORIA
    private File creacionImagenTemporal() throws IOException {
        // Creacion del fichero
        String marcaTiempo = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreFichero = "TFG_" + marcaTiempo;
        File carpetaFotosTemporales = getContext().getExternalFilesDir(null);
        if(!carpetaFotosTemporales.exists()) carpetaFotosTemporales.mkdirs();
        File fotoTemporal = File.createTempFile(nombreFichero,".jpg",carpetaFotosTemporales);
        // Guardado de ruta para almacenar el resultado de la camara
        rutaFotoTemporal = fotoTemporal.getAbsolutePath();
        return fotoTemporal;
    }
    // METODO PARA ROTAR BITMAP
    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    // METODO PARA REDIMENSIONAR IMAGENES MANTENIENDO UNA OPTIMA CALIDAD
    private Bitmap reescaladoBitmapConCalidad(Bitmap bitmapOriginal, int nuevaWidth, int nuevaHeight){
        Bitmap bitmapReescalado = Bitmap.createBitmap(nuevaWidth, nuevaHeight, Bitmap.Config.ARGB_8888);
        // Definicion de escalado
        float ratioX = nuevaWidth / (float) bitmapOriginal.getWidth();
        float ratioY = nuevaHeight / (float) bitmapOriginal.getHeight();
        float middleX = nuevaWidth / 2.0f;
        float middleY = nuevaHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        // Canvas para reescalado
        Canvas canvas = new Canvas(bitmapReescalado);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmapOriginal, middleX - bitmapOriginal.getWidth() / 2, middleY - bitmapOriginal.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return bitmapReescalado;
    }
    // METODO PARA LA GESTION DE VISTAS
    private void gestionVistas(){
        if(procesoInferenciaActivo){
            progressBar_procesando.setVisibility(View.VISIBLE);
        }else{
            progressBar_procesando.setVisibility(View.INVISIBLE);
        }
    }
}