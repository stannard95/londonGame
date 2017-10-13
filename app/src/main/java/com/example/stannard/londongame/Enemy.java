package com.example.stannard.londongame;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Random;

/**
 * Created by keir on 06/11/2016.
 */
public class Enemy {

    private int id;
    private String name; //Name of Enemy
    private String description; //description of the Enemy
    private double health; //Health of the Enemy
    private MarkerOptions marker; //The marker represented on the map
    private int gold; //The gold the enemy has


    public Enemy(int id, String name, String description, double health, MarkerOptions marker, int gold) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.health = health;
        this.marker = marker;
        this.gold = gold;

    }

    public int getId() {
        return id;
    }

    public MarkerOptions getMarker() {
        return marker;
    }

    public double getHealth() {
        return health;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getGold() {
        return gold;
    }

    public String toString()
    {
        return "Name: " + this.name + "\n" + "Description: " + this.description + "\n" + "Health: " + (int)this.health
                + "\n" + "Gold " + this.gold;
    }
}
