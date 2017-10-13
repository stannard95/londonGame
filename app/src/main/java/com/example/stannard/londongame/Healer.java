package com.example.stannard.londongame;

import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by keir on 15/11/2016.
 */
public class Healer {

    private int id;
    private String name; //Name of Enemy
    private MarkerOptions marker; //The marker represented on the map



    public Healer(int id, MarkerOptions marker) {
        this.id = id;
        this.name = "Healer";
        this.marker = marker;


    }

    public int getId() {
        return id;
    }

    public MarkerOptions getMarker() {
        return marker;
    }


    public String getName() {
        return name;
    }
    

}
