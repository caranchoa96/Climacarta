/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


// ... dentro de tu clase Modelo ...


package ponce9;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import tools.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import ponce9.Dto.ForecastDay;
/**
 *
 * @author moeko
 */
public class Modelo {
    public static String traerInfo(String q,String uri) throws UnsupportedEncodingException{
        q = URLEncoder.encode(q, StandardCharsets.UTF_8.toString());
        String json;
        //+"Las peliblancas son lindas"+
        try{
            if(uri.equals("")){
                uri = "http://api.weatherapi.com/v1/forecast.json?key="+"Las peliblancas son lindas"+"&days=3&aqi=no&alerts=no&lang=es&q="+q;
            }
            HttpClient httpClient = HttpClient.newHttpClient(); 
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Accept-Encoding", "application/json; charset=utf-8")
                .build(); 
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            json = response.body();     
            return json;
        }catch(IOException | InterruptedException e){}
        return "";
        
    }
   
    public static void mapInfo(String json) {
        File cuerpo = new File("cuerpo.json");
        ObjectMapper mapper = new ObjectMapper();
        if(json.equals("")||json.isEmpty()){
            if(cuerpo.exists()){
                json = mapper.readTree(cuerpo).asString();
                
            }else{
                javax.swing.JDialog dialogo = new javax.swing.JDialog();
                dialogo.setTitle("Mensaje");
                dialogo.setModal(true); // Para que bloquee el resto de la app hasta que lo cierres
                dialogo.setSize(350, 150);
                dialogo.setLocationRelativeTo(null); // Centrar en la pantalla
                dialogo.setLayout(new java.awt.BorderLayout());

                // Agregamos el texto centrado
                javax.swing.JLabel etiquetaTexto = new javax.swing.JLabel("¡Ingrese con internet la primera vez!", javax.swing.SwingConstants.CENTER);
                dialogo.add(etiquetaTexto, java.awt.BorderLayout.CENTER);

                // Mostramos la ventana
                dialogo.setVisible(true);
                System.exit(0);
            }
        }else{
            
            mapper.writeValue(cuerpo, json);
        }
        actElementos(mapper.readValue(json, Dto.class));
    }
    public static void traerInfoDias(List<ForecastDay> forecastday, Elementos carga){
   
       /* String fechalocal[] = hora.split(" ");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            c.setTime(sdf.parse(fechalocal[0]));
        } catch (ParseException ex) {
            System.getLogger(Modelo.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }*/
        int[] maxtemp_c = new int[3];
        int[] mintemp_c = new int[3];
        int[] daily_will_it_rain = new int[3];
        
        for(int i = 0; i < 3; i++){
                    maxtemp_c[i] = (int) forecastday.get(i).day.maxtemp_c;
                    mintemp_c[i] = (int) forecastday.get(i).day.mintemp_c;
                    daily_will_it_rain[i] = (int) forecastday.get(i).day.daily_will_it_rain;
        }
        
            //c.add(Calendar.DATE, 1);
            
        carga.setMaxtemp_c(maxtemp_c);
        carga.setMintemp_c(mintemp_c);
        carga.setDaily_will_it_rain(daily_will_it_rain);
    }
    public static void actElementos(Dto obdto){
        Elementos carga = new Elementos();
        carga.setName(obdto.location.name);
        carga.setCountry(obdto.location.country);
        carga.setIs_day( (int) (obdto.current.is_day));
        carga.setCloud( (int) (obdto.current.cloud));
        carga.setTemp_c( (int) (obdto.current.temp_c));
        carga.setText(obdto.current.condition.text);
        carga.setHumidity((int)(obdto.current.humidity));
        carga.setWind_kph((int)(obdto.current.wind_kph));
        carga.setWind_dir(obdto.current.wind_dir);
        carga.setUv((int)(obdto.current.uv));
        carga.setDate(obdto.location.localtime);
        traerInfoDias(obdto.forecast.forecastday,carga);
    }
    public static List<DtoBusqueda> buscarInfo(String textoBuscado) throws UnsupportedEncodingException{
        textoBuscado = URLEncoder.encode(textoBuscado, StandardCharsets.UTF_8.toString());
        String URL = "http://api.weatherapi.com/v1/search.json?key="+"Las peliblancas son lindas"+"&q="+textoBuscado;
        String jsonBusqueda = Modelo.traerInfo("", URL);
        ObjectMapper mapperdto = new ObjectMapper();
        
        List<DtoBusqueda> resultado = new ArrayList<>();
        if(!jsonBusqueda.equals("[]")&&!jsonBusqueda.isEmpty()){
        
        DtoBusqueda[] resulta = mapperdto.readValue(jsonBusqueda, DtoBusqueda[].class);
        resultado.addAll(Arrays.asList(resulta));
        }
        // 2. Jackson convierte el JSON (que es un arreglo) a una Lista de Java
        //ObjectMapper mapper = new ObjectMapper();
        //List<ResultadoBusqueda> resultados = mapper.readValue(jsonBusqueda,
        //new com.fasterxml.jackson.core.type.TypeReference<List<ResultadoBusqueda>>(){});
        return resultado;
    }
}
