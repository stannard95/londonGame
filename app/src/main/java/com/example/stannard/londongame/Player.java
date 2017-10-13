package com.example.stannard.londongame;

/**
 * Created by keir on 28/10/2016.
 */


    import android.widget.Toast;

    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.Marker;

    import java.lang.reflect.Array;
    import java.util.ArrayList;

/**
 * Created by 14066588 on 12/10/2016.
 */
public class Player {

    private String name;
    private int gold;
    private int movesTaken;
    private Marker marker;
    private double maxHealth;
    private double health;
    private int strength;
    private int level;
    private double currentExp;
    private double expNeeded;
    private ArrayList<Item> items = new ArrayList<Item>();

    public Player(String name) {
        this.name = name;
        this.gold = 100;
        this.maxHealth = 100;
        this.health = 100;
        this.strength = 3;
        this.level = 1;

        this.currentExp = 0;
        this.expNeeded = 100;
        this.movesTaken = 0;
        this.marker = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setMovesTaken(int movesTaken) {
        this.movesTaken = movesTaken;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setCurrentExp(double currentExp) {
        this.currentExp = currentExp;
    }

    public void setExpNeeded(double expNeeded) {
        this.expNeeded = expNeeded;
    }



    public void increaseMoves() {
        this.movesTaken++;
    }

    public int getMoves() {
        return movesTaken;
    }

    public void resetPlayer() {
        this.gold = 100;
        this.maxHealth = 100;
        this.health = 100;
        this.strength = 3;
        this.level = 1;
        this.currentExp = 0;
        this.expNeeded = 100;
        this.movesTaken = 0;
        this.marker = null;
        this.items.clear();
    }

    //The players weapon will always be the one with the highest damage
    public void replaceItem() {
        Item itemHighest = items.get(0);
        Item otherItem = null;
        for(int i=0; i<items.size(); i++) {
            if(items.get(i).getName().equals(itemHighest.getName())) {
                if (items.get(i).getItemEffectValue() >= itemHighest.getItemEffectValue()) {
                    itemHighest = items.get(i);
                }
            }
            else {
                otherItem = items.get(i);
                break;
            }
        }
        items.clear();
        items.add(itemHighest);
        if(otherItem!=null) {
            items.add(otherItem);
        }
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getName() {
        return name;
    }

    public double getExpNeeded() {
        return expNeeded;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public boolean hasWeapon() {
        boolean valid = false;
        for(int i=0; i< items.size(); i++) {
            if(items.get(i).getName().equals("Weapon")) {
                valid = true;
                break;
            }
            else {
                valid = false;
            }

        }
        return valid;
    }

    public void removeItem(int id) {

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == id) {
                items.remove(i);
                break;
            }
        }
    }

    public int getItemEffect(String option) {
        Item item = null;
        int effect = 0;
        if(!items.isEmpty()) {
            for (Item ite : items) {
                if(option.equals("Weapon")) {
                    if (ite.getName().equals("Weapon")) {
                        item = ite;
                    }
                }
                else {
                    if (ite.getName().equals("Shield")) {
                        item = ite;
                    }
                }
            }
            if (item != null) {
                effect = item.getItemEffectValue();
            }
        }
        else {
            effect = 0;
        }
        return effect;
    }

    public double getCurrentExp() {
        return currentExp;
    }

    public int getLevel() {
        return level;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getHealth() {
        return health;
    }

    public int getGold() {
        return gold;
    }

    public int getStrength() {
        return strength;
    }

    public void increaseStats() {

            this.level++;
            this.maxHealth = this.maxHealth * 1.10;
            this.health = this.maxHealth;
            this.strength += 1;
            this.currentExp = 0;
            this.expNeeded = this.expNeeded * 1.25;
            this.gold+=50;

        }

    public void heal() {
        if(this.health+50>=this.maxHealth) {
            this.health = this.maxHealth;
        }
        else {
            this.health+=50;
        }

    }

    public void loseHealth(double value) {
        if(this.health-value<=0) {
            this.health = 0;
        }
        else {
            this.health-=value;
        }
    }

    public void increaseGold(int value) {
        this.gold+=value;
    }

    public void loseGold(int value) {
        if(this.gold-value<=0) {
            this.gold = 0;
        }
        else {
            this.gold-=value;
        }
    }

    public void increaseExp(double value) {
        if(value+this.currentExp>= this.expNeeded) {
            increaseStats();
        }
        else {
            this.currentExp+=value;
        }
    }



    public String itemToString() {
        String effectText = "";
        String result ="";
        for(Item item: items) {
            if(item.getName().equals("Weapon")) {
                effectText = "Damage: ";
            }
            else {
                effectText = "Armor: ";
            }
            result+= "Name: " + item.getName() + "\n" + "Description: " + item.getDescription() + "\n" +
                    "Level: " + item.getItemLevel() + "\n" + effectText + item.getItemEffectValue() + "\n";
        }
        return result;
    }

    public String toString() {
        return "Name: " + this.name + "\n" + "Health: "  + (int)this.health + "/" + (int)this.maxHealth + "\n" + "Strength: " + this.strength +
                  " + " + getItemEffect("Weapon") + "\n" + "Gold: " + this.gold
                + "\n" + "Level: " + this.level + "\n" + "Exp points: " + (int)this.currentExp + "\n" +
                "Exp needed: " + (int)this.expNeeded + "\n" + "Location: " + this.marker.getTitle() + "\n" +
                "Moves Taken: " + this.movesTaken;
    }


}

