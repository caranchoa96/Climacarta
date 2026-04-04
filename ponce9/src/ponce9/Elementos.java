/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ponce9;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author moeko
 */
public class Elementos {
    public final static String[] semana = {"Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo"};
    public static LinkedList<String> diasSemana = new LinkedList<>();
    public static String Date;
    public static String is_day;
    public static String cloud;
    public static String text;
    public static String hora;
    public static int temp_c;
    public static int humidity;
    public static int wind_kph;
    public static String wind_dir;
    public static int[] maxtemp_c = new int[7];
    public static int[] mintemp_c = new int[7];
    public static int[] daily_will_it_rain = new int[7];
    public static int uv;

    public void setDiasSemana(String hoy,String[] semana) {
        int o=0;
        boolean bande=false;
        //{"Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo"};
        // 0       1          2          3         4        5        6 
        //                    2          3         4        5        6         0      1 
        for(int i = 0; i < 7; i++){
            if(!semana[i].equals(hoy)){
                if(bande==true){
                    diasSemana.add(semana[i]);
                }
            }else{
                o=i;
                bande=true;
            }
        }
        if(o!=0)for(int i = 0; i <o; i++){
            diasSemana.add(semana[i]);
        }
        // 0        1       2            3          4      5          6
        //martes miercoles jueves viernes sabado domingo
        //Martes miercoles jueves viernes sabado domingo
        //Miercoles jueves viernes sabado domingo lunes martes
    }

    public void setDate(String dialetra, String dian, String[] horas) {
        int horac = Integer.parseInt(horas[0]);
        String[] mes = dian.split("-");
        switch(mes[1]){
            case "01" -> mes[1] = "Enero";
            case "02" -> mes[1] = "Febrero";
            case "03" -> mes[1] = "Marzo";
            case "04" -> mes[1] = "Abril";
            case "05" -> mes[1] = "Mayo";
            case "06" -> mes[1] = "Junio";
            case "07" -> mes[1] = "Julio";
            case "08" -> mes[1] = "Agosto";
            case "09" -> mes[1] = "Septiembre";
            case "10" -> mes[1] = "Octubre";
            case "11" -> mes[1] = "Noviembre";
            case "12" -> mes[1] = "Diciembre";
        }
        if(horac >= 19){
            switch(dialetra){
                case "Mon" -> dialetra = "Domingo";
                case "Tue" -> dialetra = "Lunes";
                case "Wed" -> dialetra = "Martes";
                case "Thu" -> dialetra = "Miercoles";
                case "Fri" -> dialetra = "Jueves";
                case "Sat" -> dialetra = "Viernes";
                case "Sun" -> dialetra = "Sabado";
            }
        }else{
            switch(dialetra){
                case "Mon" -> dialetra = "Lunes";
                case "Tue" -> dialetra = "Martes";
                case "Wed" -> dialetra = "Miercoles";
                case "Thu" -> dialetra = "Jueves";
                case "Fri" -> dialetra = "Viernes";
                case "Sat" -> dialetra = "Sabado";
                case "Sun" -> dialetra = "Domingo";
            }
        }
        String date = dialetra+", "+mes[2]+" de "+mes[1];
        Date = date;
        String hoy[] = Date.split(",");
        setDiasSemana(hoy[0],semana);
    }

    public void setIs_day(int is_day) {
        Elementos.is_day = is_day == 1 ? "☀️" : "🌙";
    }



    public void setCloud(int cloud) {
        Elementos.cloud = cloud > 50 ? "☁️" : "☀️";
        if(daily_will_it_rain[0]==1)Elementos.cloud = "🌧️";
    }

    public void setText(String text) {
        switch(text){
            case "Cloudy" -> text = "Nublado";
            case "Patchy rain nearby" -> text = "Lluvia irregular cerca";
            case "Clear" -> text = "Despejado";
            case "Partly cloudy" -> text = "Parcialmente nublado";
            case "Sunny" -> text = "Soleado";
            case "Light rain" -> text = "Lluvia ligera";
            case "Light drizzle" -> text = "Llovizna ligera";
            case "Light rain shower" -> text = "Llovizna";
        }
        Elementos.text = text;
    }


    public void setMaxtemp_c(int[] maxtemp_c) {
        Elementos.maxtemp_c = maxtemp_c;
    }

    public void setMintemp_c(int[] mintemp_c) {
        Elementos.mintemp_c = mintemp_c;
    }

    public void setDaily_will_it_rain(int[] daily_will_it_rain) {   
        Elementos.daily_will_it_rain = daily_will_it_rain;
    }

    public static String getUv() {
        String indice;
        indice = switch (uv) {
            case 0, 1, 2 -> "Bajo";
            case 3, 4, 5 -> "Moderado";
            case 6, 7 -> "Alto";
            case 8, 9, 10 -> "Muy alto";
            default -> "Extremo";
        };
        return indice;
    }

    public void setUv(int uv) {
        Elementos.uv = uv;
    }
    void setTemp_c(int asInt) {
        Elementos.temp_c = asInt;
    }

    void setHumidity(int asInt) {
        Elementos.humidity = asInt;
    }

    void setWind_kph(int asInt) {
        Elementos.wind_kph = asInt;
    }

    void setWind_dir(String asString) {
        Elementos.wind_dir = asString;
    }

    void setHora(String string) {
        Elementos.hora = string;
    }
    
    /*
    location
        name, country
    Response Headers
        Date
    current
        condition
            is_day, cloud (emojis = icon)
            text
        temp_c
        humidity
        wind_kph, wind_dir
    forecast
        forecastday
            day
                maxtemp_c, mintemp_c
                daily_will_it_rain
            uv (0-2)bajo
                (3-5) Moderado
                (6-7) Alto
                (8-10) Muy alto
                (11+) Extremo
    */
    /* 
    Date = Date+(1...7) fifo - lifo
    current
        condition
            is_day, cloud (emojis = icon)
    forecast
        forecastday
            day
                maxtemp_c, mintemp_c
                daily_will_it_rain
    */ 
}
