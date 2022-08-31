# APP PARA LA LOCALIZACIÓN DE PERSONAS A PARTIR DEL RECONOCIMIENTO AUTOMÁTICO DE IMÁGENES, UAH
### ¡¡ SUBIDA DEL CÓDIGO EN LOS PRÓXIMOS DÍAS !!

## Resumen
Aplicación Android para la localización de personas dentro del **Museo Provincial de Guadalajara**, pudiendo servir de plantilla para 
aplicarse en otros entornos

Esta aplicación es el resultado del TFG de mismo nombre de la **Universidad de Alcalá**, con el fin de crear una solución Android para 
la localización de personas en entornos donde la solución GPS es insuficiente. Para ello se crea un clasificador de imágenes en formato
'.tflite' que se integra en la app para a partir de una relación entre etiqueta del modelo y ubicación, determinar la ubicación del 
usuario.

## Descargo de responsabilidad
1. Hace uso de **TensorFlow** y sus derivadas de IA, las cuales tienen su propio soporte.
2. Hace uso de **Mapbox SDK** para la representación de mapas, la cual tiene su propio soporte.

## Requerimientos
* Versión Android
  La versión mínima que soporta todas las herramientas y librerías usadas del proyecto
  * Android 7.0 (SDK 24)
* Dependencias
  Conjunto de dependencias a completar en el fichero "build.gradle (Module: App.app)"
  * 'com.google.android.material:material:1.4.0'
  * 'org.tensorflow:tensorflow-lite-support:0.1.0'
  * 'org.tensorflow:tensorflow-lite-metadata:0.1.0'
  * 'com.mapbox.mapboxsdk:mapbox-android-sdk:9.7.1'
  * 'com.mapbox.mapboxsdk:mapbox-sdk-turf:5.6.0'
  * 'com.mapbox.mapboxsdk:mapbox-android-plugin-locationlayer:0.5.0'
  * 'com.mapbox.mapboxsdk:mapbox-android-plugin-markerview-v9:0.4.0'
  * 'com.mapbox.mapboxsdk:mapbox-android-plugin-annotation-v9:0.9.0'
  
## Menciones
1. El desarrollo usando **Mapbox SDK** se basa en el proyecto **"MuseoIndoorPosition"** disponible en el repositorio Github: https://github.com/dquezadag/MuseoIndoorPosition
2. Para el diseño de la aplicación se han utilizado iconos libres del sitio web **FlatIcon** (https://www.flaticon.es), por lo que se adjunta
   una tabla de relación entre icono utilizado y autor.
   
   ![image](https://user-images.githubusercontent.com/37403705/187795172-082f1bcf-49fa-4c8e-92d8-cbcbfd99ec1f.png)
