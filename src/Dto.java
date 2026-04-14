/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ponce9;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Dto {
    
    public Location location;
    public Current current;
    public Forecast forecast;

    // --- SUBCLASES ---

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        public String name;
        public String country;
        public String localtime;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {
        public double temp_c;
        public int is_day;
        public Condition condition;
        public double wind_kph;
        public String wind_dir;
        public int humidity;
        public double uv;
        public int cloud;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Condition {
        public String text;
        public String icon;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Forecast {
        public List<ForecastDay> forecastday;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastDay {
        public String date;
        public Day day;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Day {
        public double maxtemp_c;
        public double mintemp_c;
        public int daily_will_it_rain;
        public double uv;
    }
    /*
    location
        name, country, localtime
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
