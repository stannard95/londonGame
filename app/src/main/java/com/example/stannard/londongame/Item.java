package com.example.stannard.londongame;


import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Random;

/**
 * Created by 14066588 on 12/10/2016.
 */
public class Item {

    private int id; // item id
    private String name; // Name of the item
    private String description; //Description of the item
    private int itemLevel; // Item level required
    private int itemEffectValue; // item effect value
    private MarkerOptions marker;

    public Item(int id, String name, String description, int itemLevel) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.itemLevel = itemLevel;
        this.itemEffectValue = 0;
        this.marker = null;
    }

    public int getId() {
        return id;
    }

    public MarkerOptions getMarker() {
        return marker;
    }

    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }

    public String getName() {
        return name;
    }

    public int getItemEffectValue() {
        return itemEffectValue;
    }

    public int getItemLevel() {
        return itemLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setItemLevel(int value) {
        this.itemLevel=value;
    }

    public void setItemEffect(int value) {
        this.itemEffectValue = value;
    }


    public void setItemEffectValue() {
        Random rn = new Random();
        if (this.itemLevel==1) {
            this.itemEffectValue = rn.nextInt(3-1) + 1;
        }
        else if(this.itemLevel > 1 && this.itemLevel <= 3) {
            this.itemEffectValue = rn.nextInt(5-2) + 2;
        }
        else if(this.itemLevel > 3 && this.itemLevel <= 5) {
            this.itemEffectValue = rn.nextInt(9-4) + 4;
        }
        else if(this.itemLevel > 5 && this.itemLevel <= 7) {
            this.itemEffectValue = rn.nextInt(12-8) + 8;
        }
        else if(this.itemLevel > 7 && this.itemLevel <=10) {
            this.itemEffectValue = rn.nextInt(15-11) + 11;
        }
    }

    public String toString() {
        String effectText = "";

            if(this.getName().equals("Weapon")) {
                effectText = "Damage: ";
            }
            else {
                effectText = "Armor: ";
            }
        return "Name: " + this.name + "\n" + "Description: " + this.description + "\n" + "Level required: " + this.itemLevel +
                "\n" + effectText + this.itemEffectValue + "\n";
    }

}

