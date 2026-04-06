/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ponce9;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;
import javax.swing.JOptionPane;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


/**
 *
 * @author moeko
 */
public class Modelo {
    private static final String uri = "http://api.weatherapi.com/v1/forecast.json?key=64be5ac473da4381ac924404262703&q=Cartagena&days=7&aqi=no&alerts=yes";
    public static String traerInfo(){
        String json = "";
        try{
            HttpClient httpClient = HttpClient.newHttpClient(); 
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Accept-Encoding", "application/json; charset=utf-8")
                .build(); 
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            json = response.body()+";"+response.headers();     
            return json;
        }catch(Exception e){}
        return "";
    }
   
    public static void mapInfo(String json) {
        File cuerpo = new File("cuerpo.json");
        File cabeza = new File("cabeza.json");
        ObjectMapper mapper = new ObjectMapper();
        Elementos carga = new Elementos();
        JsonNode nodoCuerpo = null;
        
        
        
       
        
        
        if(json.equals("")){
            if(cuerpo.exists()&&cabeza.exists()){
                nodoCuerpo = mapper.readTree(cuerpo);
                json = mapper.readTree(cuerpo).toString().concat(";").concat(mapper.readTree(cabeza).toString());
            }else{
                JOptionPane.showMessageDialog(null, "¡Ingrese con internet la primera ves!");
            }
        }else{
            nodoCuerpo = mapper.readTree(json.split(";")[0]);
            mapper.writeValue(cuerpo, nodoCuerpo);
            mapper.writeValue(cabeza, json.split(";")[1]);
        }
        String[] jason = json.split(";");
        String fechalocal[] = nodoCuerpo.at("/location/localtime").asString().split(" ");
        traerInfoDias(nodoCuerpo.at("/forecast/forecastday"),fechalocal[0],carga);
        carga.setIs_day((nodoCuerpo.at("/current/is_day").asInt()));
        carga.setCloud(nodoCuerpo.at("/current/cloud").asInt());
        carga.setTemp_c(nodoCuerpo.at("/current/temp_c").asInt());
        carga.setText(nodoCuerpo.at("/current/condition/text").asString());
        carga.setHumidity(nodoCuerpo.at("/current/humidity").asInt());
        carga.setWind_kph(nodoCuerpo.at("/current/wind_kph").asInt());
        carga.setWind_dir(nodoCuerpo.at("/current/wind_dir").asString());
        carga.setDate(diaInfo(jason[1]), fechalocal[0], fechalocal[1].split(":"));
        carga.setHora(fechalocal[1]);
        
        carga.setUv(nodoCuerpo.at("/current/uv").asInt());
    }
    public static void traerInfoDias(JsonNode forecast, String hora, Elementos carga ){
   
        String fechalocal[] = hora.split(" ");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            c.setTime(sdf.parse(fechalocal[0]));
        } catch (ParseException ex) {
            System.getLogger(Modelo.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        int[] maxtemp_c = new int[7];
        int[] mintemp_c = new int[7];
        int[] daily_will_it_rain = new int[7];
        for(int i = 0; i < 7; i++){     
            for (JsonNode node : forecast) {
                if(node.get("date").asString().equals(sdf.format(c.getTime()))) {
                    maxtemp_c[i] = (int) node.get("day").get("maxtemp_c").asDouble();
                    mintemp_c[i] = (int) node.get("day").get("mintemp_c").asDouble();
                    daily_will_it_rain[i] = node.get("day").get("daily_will_it_rain").asInt();   
                }
            }
            c.add(Calendar.DATE, 1);
        }
        carga.setMaxtemp_c(maxtemp_c);
        carga.setMintemp_c(mintemp_c);
        carga.setDaily_will_it_rain(daily_will_it_rain);
    }
    public static String diaInfo(String headersa){
        //date = headers.split(",");
        String headers[] = headersa.split(",");
        String date = headers[16];
        String dia[] = date.split("\\[");
        String dialetras = dia[1];
        //if(17>24){
        //    dia siguiente
        //}
        return dialetras;
    }
}
