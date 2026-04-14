/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ponce9;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author moeko
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DtoBusqueda {
    
    private String name;
    private String country;
    public String getName() {
        return name;
    }


    public String getCountry() {
        return country;
    }
    
    
    // Un método útil para mostrar en el menú
    public String getNombreCompleto() {
        return name + " - " + country;
    }
}