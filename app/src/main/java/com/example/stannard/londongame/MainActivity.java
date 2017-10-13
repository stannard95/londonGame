package com.example.stannard.londongame;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private GoogleMap map;
    private Button inventory, start, stats, save, load;
    private Player player;
    private Marker playerMarker, previousClickedMarker;
    private boolean firstMove = true;
    private boolean gameStarted = false;
    private int ticketPrice = 0;
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private ArrayList<Healer> healers = new ArrayList<Healer>();
    private ArrayList<Item> items = new ArrayList<Item>();
    private PolylineOptions line = new PolylineOptions();
    private ArrayList<LatLng> lineCords = new ArrayList<LatLng>();

    ArrayList<Site> sites = new ArrayList<Site>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final String appUrl = getString(R.string.datapoint_base_url);
        start = (Button) findViewById(R.id.start_button);
        load = (Button) findViewById(R.id.load_button);
        FragmentManager fragMan = getFragmentManager();
        MapFragment mapFrag = (MapFragment) fragMan.findFragmentById(R.id.map_fragment);
        map = mapFrag.getMap();
        player = new Player("Player");

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gameStarted = true;

                try {
                    FileInputStream input = openFileInput("data.txt");
                    DataInputStream din = new DataInputStream(input);
                    int sz = din.readInt(); // Read line count
                    int sz1 = din.readInt();
                    int sz2 = din.readInt();
                    int sz3 = din.readInt();
                    int sz4 = din.readInt();

                    //Healers
                    for (int i = 0; i < sz; i++) {
                        String str = din.readUTF();
                        Log.v("read", str);
                        String[] stringArray = str.split(",");
                        int id = Integer.parseInt(stringArray[0]);
                        double latitude = Double.parseDouble(stringArray[1]);
                        double longitude = Double.parseDouble(stringArray[2]);
                        refillHealersMap(id, latitude, longitude);
                    }
                    //Enemies
                    for (int i = sz; i < sz + sz1; i++) {
                        String str = din.readUTF();
                        Log.v("read", str);
                        String[] stringArray = str.split(",");
                        int id = Integer.parseInt(stringArray[0]);
                        double health = Double.parseDouble(stringArray[1]);
                        int gold = Integer.parseInt(stringArray[2]);
                        double latitude = Double.parseDouble(stringArray[3]);
                        double longitude = Double.parseDouble(stringArray[4]);
                        refillEnemiesMap(id, health, gold, latitude, longitude);
                    }
                    //Items
                    for (int i = sz1; i < sz + sz1 + sz2; i++) {
                        String str = din.readUTF();
                        Log.v("read", str);
                        String[] stringArray = str.split(",");
                        int id;
                        try {
                            id = Integer.parseInt(stringArray[0]);
                        } catch (NumberFormatException e) {
                            break;
                        }

                        String name = stringArray[1];
                        String description = stringArray[2];
                        int level = Integer.parseInt(stringArray[3]);
                        int effect = Integer.parseInt(stringArray[4]);
                        System.out.println("EFFECT" + effect);
                        double latitude = Double.parseDouble(stringArray[5]);
                        double longitude = Double.parseDouble(stringArray[6]);
                        refillItemsMap(id, name, description, level, effect, latitude, longitude);
                    }

                    //Stops
                    for (int i = sz2; i < sz + sz1 + sz2 + sz3; i++) {
                        String str = din.readUTF();
                        Log.v("read", str);
                        String[] stringArray = str.split(",");
                        double latitude = 0.0, longitude = 0.0;

                        String id1 = stringArray[0];
                        String name = stringArray[1];
                        String name2 = stringArray[2];
                        String name3 = "";

                        try {
                            latitude = Double.parseDouble(stringArray[3]);
                            longitude = Double.parseDouble(stringArray[4]);
                            name = name + ", " + name2;

                        } catch (NumberFormatException e) {
                            name3 = stringArray[3];
                            latitude = Double.parseDouble(stringArray[4]);
                            longitude = Double.parseDouble(stringArray[5]);
                            name = name + ", " + name2 + ", " + name3;

                        }
                        refillStopsMap(id1, name, latitude, longitude);
                    }

                    din.close();

                } catch (IOException exc) {
                    exc.printStackTrace();
                }

                try {

                    FileInputStream input = openFileInput("lines.txt");
                    DataInputStream di = new DataInputStream(input);
                    int siz = di.readInt();
                    for (int i = 0; i < siz; i++) {
                        String str = di.readUTF();
                        Log.v("read", str);
                        String[] stringArray = str.split(",");
                        double lat1 = Double.parseDouble(stringArray[0]);
                        double lon1 = Double.parseDouble(stringArray[1]);
                        try {

                            double lat2 = Double.parseDouble(stringArray[2]);
                            double lon2 = Double.parseDouble(stringArray[3]);
                            reDraw(lat1, lon1, lat2, lon2);
                        } catch (NumberFormatException e) {
                        break;
                    }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Players
                int size;
                player = new Player(refillPlayer().get(0));
                player.setLevel(Integer.parseInt(refillPlayer().get(1)));
                Double lat = Double.parseDouble(refillPlayer().get(2));
                Double lon = Double.parseDouble(refillPlayer().get(3));
                player.setHealth(Double.parseDouble(refillPlayer().get(4)));
                player.setMaxHealth(Double.parseDouble(refillPlayer().get(5)));
                player.setGold((Integer.parseInt(refillPlayer().get(6))));
                player.setCurrentExp(Double.parseDouble(refillPlayer().get(7)));
                player.setExpNeeded(Double.parseDouble(refillPlayer().get(8)));
                player.setStrength(Integer.parseInt(refillPlayer().get(9)));
                player.setMovesTaken(Integer.parseInt(refillPlayer().get(10)));
                size = Integer.parseInt(refillPlayer().get(12));

                if (size > 0) {
                    System.out.println(size);
                    Item item = new Item(Integer.parseInt(refillPlayer().get(13)), refillPlayer().get(14),
                            refillPlayer().get(15), Integer.parseInt(refillPlayer().get(16)));
                    item.setItemEffect(Integer.parseInt(refillPlayer().get(17)));
                    player.addItem(item);
                }
                if (size == 2) {
                    System.out.println(size);
                    Item item1 = new Item(Integer.parseInt(refillPlayer().get(18)), refillPlayer().get(19),
                            refillPlayer().get(20), Integer.parseInt(refillPlayer().get(21)));
                    item1.setItemEffectValue();
                    item1.setItemEffect(Integer.parseInt(refillPlayer().get(22)));
                    player.addItem(item1);
                }

                LatLng playerLocal = new LatLng(lat, lon);
                firstMove = false;

                playerMarker = map.addMarker(new MarkerOptions().position(playerLocal).title(refillPlayer().get(11)).icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                player.setMarker(playerMarker);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(player.getMarker().getPosition(), 14));

                start.setX(-500);
                load.setX(-500);

            }
        });


        //Starts the game
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameStarted = true;
                new JSONTask().execute(appUrl);
                start.setX(-500);
                load.setX(-500);
                Toast.makeText(getApplicationContext(), "Pick a purple marker where to start",
                        Toast.LENGTH_LONG).show();

            }
        });


        //When the player clicks on a marker
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.getTitle().equals("Enemy")) {
                    displayEnemyDialog(player, marker);
                }
                if (checkPlayerLocation(player, marker)) {
                    displayStatsDialog(player);
                }
                if (!marker.getTitle().equals("Weapon") && !marker.getTitle().equals("Enemy") && !marker.getTitle().equals("Spawn")
                        && !marker.getTitle().equals("Healer") && !marker.getTitle().equals("Shield") && !checkPlayerLocation(player, marker)) {
                    displayStopDialog(player, marker);
                }
                if (marker.getTitle().equals("Healer")) {
                    displayHealerDialog(player, marker);
                }
                if (marker.getTitle().equals("Weapon") || marker.getTitle().equals("Shield")) {
                    displayItemDialog(player, marker);
                }

                return true;
            }
        });

    }

    //Redraws the lines on the map
    private void reDraw(double lat1, double lon1, double lat2, double lon2) {
        LatLng local1 = new LatLng(lat1, lon1);
        LatLng local2 = new LatLng(lat2, lon2);
        line.add(local1, local2).width(5).color(Color.RED);

    }


    //Saves the game
    private void saveGame() {
        try {
            FileOutputStream output = openFileOutput("data.txt",
                    Context.MODE_PRIVATE);
            DataOutputStream doutData = new DataOutputStream(output);
            doutData.writeInt(healers.size());
            doutData.writeInt(enemies.size());
            doutData.writeInt(items.size()); // Save line count
            doutData.writeInt(sites.size());
            doutData.writeInt(1);
            for (Healer point : healers) {
                doutData.writeUTF(point.getId() + "," +
                        point.getMarker().getPosition().latitude + "," + point.getMarker().getPosition().longitude);
            }
            for (Enemy point : enemies) {
                doutData.writeUTF(point.getId() + "," + point.getHealth() + "," + point.getGold() + "," +
                        point.getMarker().getPosition().latitude + "," + point.getMarker().getPosition().longitude);
            }
            for (Item point : items) {
                doutData.writeUTF(point.getId() + "," + point.getName() + "," + point.getDescription() + "," + point.getItemLevel()
                        + "," + point.getItemEffectValue() + "," + point.getMarker().getPosition().latitude
                        + "," + point.getMarker().getPosition().longitude);
            }
            for (Site point : sites) {
                doutData.writeUTF(point.getId() + "," + point.getName() + "," + point.getLocation().latitude + "," +
                        point.getLocation().longitude);
            }

            doutData.flush(); // Flush stream ...
            doutData.close(); // ... and close.
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("player.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(player.getName() + "\n" + player.getLevel() + "\n" + player.getMarker().getPosition().latitude + "\n"
                    + player.getMarker().getPosition().longitude + "\n" + player.getHealth() + "\n" + player.getMaxHealth() + "\n" + player.getGold()
                    + "\n" + player.getCurrentExp() + "\n" + player.getExpNeeded() + "\n" + player.getStrength() + "\n" + player.getMoves() + "\n" +
                    player.getMarker().getTitle() + "\n" + player.getItems().size() + "\n");
            if (!player.getItems().isEmpty()) {

                for (int i = 0; i < player.getItems().size(); i++) {
                    outputStreamWriter.write(player.getItems().get(i).getId() + "\n" + player.getItems().get(i).getName()
                            + "\n" + player.getItems().get(i).getDescription() + "\n" + player.getItems().get(i).getItemLevel() + "\n"
                            + player.getItems().get(i).getItemEffectValue() + "\n");
                }
            }
            outputStreamWriter.close();
        } catch (IOException e) {

        }

        try {
            FileOutputStream output = openFileOutput("lines.txt",
                    Context.MODE_PRIVATE);
            DataOutputStream doutData = new DataOutputStream(output);
            doutData.writeInt(lineCords.size());

            for (int i=0; i<lineCords.size(); i++) {
                if (i == lineCords.size()) {
                    doutData.writeUTF(
                            lineCords.get(i).latitude + "," + lineCords.get(i).longitude);

                }
                else if(i + 1 <lineCords.size()){
                    doutData.writeUTF(
                            lineCords.get(i).latitude + "," + lineCords.get(i).longitude
                                    + "," + lineCords.get(i + 1).latitude + "," + lineCords.get(i + 1).longitude);
                }
            }
            doutData.flush(); // Flush stream ...
            doutData.close(); // ... and close.
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "Game is saved",
                Toast.LENGTH_LONG).show();
    }

    //Refills the player
    private List<String> refillPlayer() {
        List<String> list = new ArrayList<String>();
        String str = null;
        try {
            InputStream inputStream = openFileInput("player.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                while ((str = bufferedReader.readLine()) != null) {
                    list.add(str);
                }

                inputStream.close();
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {

        }
        return list;
    }

    //Refills the bikestops
    private void refillStopsMap(String id, String name, double lat, double lon) {

        LatLng stopLocal = new LatLng(lat, lon);
        MarkerOptions option = new MarkerOptions().position(stopLocal).title(name).icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        sites.add(new Site(id, name, stopLocal));
        map.addMarker(option);
    }

    //Refills the healers
    private void refillHealersMap(int id, double lat, double lon) {
        LatLng healerLocal = new LatLng(lat, lon);
        MarkerOptions healOption = new MarkerOptions().position(healerLocal).title("Healer").snippet(Integer.toString(id)).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        healers.add(new Healer(id, healOption));
        map.addMarker(healOption);
    }

    //Refills the items
    private void refillItemsMap(int id, String name, String description, int level, int effect, double lat, double lon) {
        LatLng itemLocal = new LatLng(lat, lon);
        Item item = new Item(id, name, description, effect);
        System.out.println("EFFECTREFILL" + effect);
        item.setItemLevel(level);
        item.setItemEffect(effect);
        MarkerOptions itemOption = new MarkerOptions().position(itemLocal).title(item.getName()).snippet(Integer.toString(item.getId()));
        if (name.equals("Weapon")) {
            itemOption.icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        } else {
            itemOption.icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        }
        item.setMarker(itemOption);
        System.out.println("EFFECTaognaiongon" + item.getItemEffectValue());
        items.add(item);
        map.addMarker(itemOption);
    }

    //Refills the enemies
    private void refillEnemiesMap(int id, double health, int gold, double lat, double lon) {
        LatLng enemyLocal = new LatLng(lat, lon);
        MarkerOptions enemyOption = new MarkerOptions().position(enemyLocal).title("Enemy").snippet(Integer.toString(id)).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        enemies.add(new Enemy(id, "Enemy", "Monster of London", health, enemyOption, gold));
        map.addMarker(enemyOption);
    }

    //The players health is 0
    private void hasLost() {
        Toast.makeText(getApplicationContext(), "You have died, reload the game or start again!",
                Toast.LENGTH_LONG).show();
        start.setTranslationX(0);
        load.setTranslationX(0);
        player.resetPlayer();
        enemies.clear();
        items.clear();
        sites.clear();
        map.clear();
        firstMove = true;
        gameStarted = false;
        line = new PolylineOptions();
    }

    //End game
    private void hasWon() {
        Toast.makeText(getApplicationContext(), "All enemies have been defeated! \n This took you " +
                        player.getMoves() + " moves, press start to restart and beat your score!",
                Toast.LENGTH_LONG).show();
        start.setTranslationX(0);
        player.resetPlayer();
        enemies.clear();
        items.clear();
        sites.clear();
        map.clear();
        gameStarted = false;
        line = new PolylineOptions();

    }

    //Checks location of the player
    private boolean checkPlayerLocation(final Player player, final Marker marker) {
        if (player.getMarker().getPosition().equals(marker.getPosition())) {
            return true;
        } else {
            return false;
        }
    }

    //Dialog for when the player clicks on an healer
    private void displayHealerDialog(final Player player, final Marker marker) {
        Location l1 = new Location("");
        Location l2 = new Location("");
        l1.setLatitude(player.getMarker().getPosition().latitude);
        l1.setLongitude(player.getMarker().getPosition().longitude);
        l2.setLatitude(marker.getPosition().latitude);
        l2.setLongitude(marker.getPosition().longitude);
        float distance = l1.distanceTo(l2);
        if (distance > 100) {
            Toast.makeText(getApplicationContext(), "Healer is to far away...",
                    Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("To heal you it will cost 50g");
            builder.setTitle(marker.getTitle());
            builder.setCancelable(true);

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Heal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (player.getGold() >= 50) {
                        player.heal();
                        player.loseGold(50);
                        Toast.makeText(getApplicationContext(), "You have been healed",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "You do not have enough gold to heal",
                                Toast.LENGTH_SHORT).show();
                    }

                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //Dialog for when the player clicks on an item on the map
    private void displayItemDialog(final Player player, final Marker marker) {
        Location l1 = new Location("");
        Location l2 = new Location("");
        l1.setLatitude(player.getMarker().getPosition().latitude);
        l1.setLongitude(player.getMarker().getPosition().longitude);
        l2.setLatitude(marker.getPosition().latitude);
        l2.setLongitude(marker.getPosition().longitude);
        float distance = l1.distanceTo(l2);
        if (distance > 100) {
            Toast.makeText(getApplicationContext(), "Item is to far away...",
                    Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getItem(marker).toString());
            builder.setTitle(marker.getTitle());
            builder.setCancelable(true);

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Pickup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (getItem(marker).getName().equals("Shield")) {
                        if (player.hasWeapon()) {
                            if (player.getLevel() >= getItem(marker).getItemLevel()) {
                                Toast.makeText(getApplicationContext(), "You have a new " + getItem(marker).getName(),
                                        Toast.LENGTH_SHORT).show();
                                marker.remove();
                                player.addItem(getItem(marker));
                                items.remove(getItem(marker));
                                player.replaceItem();

                            } else {
                                Toast.makeText(getApplicationContext(), "You are not level " + getItem(marker).getItemLevel(),
                                        Toast.LENGTH_SHORT).show();
                            }

                            dialog.cancel();
                        } else {
                            Toast.makeText(getApplicationContext(), "You need a weapon to use a shield",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (player.getLevel() >= getItem(marker).getItemLevel()) {
                            Toast.makeText(getApplicationContext(), "You have a new " + getItem(marker).getName(),
                                    Toast.LENGTH_SHORT).show();
                            marker.remove();
                            player.addItem(getItem(marker));
                            items.remove(getItem(marker));
                            player.replaceItem();

                        } else {
                            Toast.makeText(getApplicationContext(), "You are not level " + getItem(marker).getItemLevel(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        dialog.cancel();
                    }
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //Dialog for when the player clicks on an enemy
    private void displayEnemyDialog(final Player player, final Marker marker) {
        Location l1 = new Location("");
        Location l2 = new Location("");
        l1.setLatitude(player.getMarker().getPosition().latitude);
        l1.setLongitude(player.getMarker().getPosition().longitude);
        l2.setLatitude(marker.getPosition().latitude);
        l2.setLongitude(marker.getPosition().longitude);
        float distance = l1.distanceTo(l2);
        if (distance > 100) {
            Toast.makeText(getApplicationContext(), "Enemy is to far away...",
                    Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getEnemy(marker).toString());
            builder.setTitle("Enemy");
            builder.setCancelable(true);

            builder.setPositiveButton("Fight", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Random rn = new Random();
                    int win = rn.nextInt(47 - 40) + 40;
                    if(getEnemy(marker).getHealth()>175 && getEnemy(marker).getHealth()<200) {
                        win+=10;
                        player.increaseExp(35);
                    }
                    if(getEnemy(marker).getHealth()>150 && getEnemy(marker).getHealth()<175) {
                        win+=5;
                        player.increaseExp(30);
                    }
                    win = win - player.getStrength() - player.getItemEffect("Weapon") - player.getItemEffect("Shield");
                    if (win < 0) {
                        win = 0;
                    }

                    player.loseHealth(win);
                    Toast.makeText(getApplicationContext(), "You lost " + win + " health", Toast.LENGTH_LONG).show();
                    if(getEnemy(marker).getHealth()<150) {
                        player.increaseExp(25);
                    }
                    player.increaseGold(getEnemy(marker).getGold());
                    enemies.remove(getEnemy(marker));
                    marker.remove();
                    if (enemies.isEmpty()) {
                        hasWon();
                    }

                    if (player.getHealth() <= 0) {
                        hasLost();
                    }
                    dialog.cancel();

                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //Sets the price of a ticket
    private int setTicketPrice(float distance) {
        if (distance <= 500) {
            ticketPrice = 5;
        } else if (distance >= 500 && distance <= 1000) {
            ticketPrice = 10;
        } else if (distance >= 1000 && distance <= 2000) {
            ticketPrice = 20;
        } else if (distance >= 2000 && distance <= 5000) {
            ticketPrice = 30;
        } else if (distance >= 5000 && distance <= 7500) {
            ticketPrice = 50;
        } else if (distance >= 7500 && distance <= 10000) {
            ticketPrice = 100;
        } else if (distance >= 10000) {
            ticketPrice = 200;
        }

        return ticketPrice;
    }

    //Dialog for the players inventory
    private void displayInventoryDialog(final Player player) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Inventory");
        builder.setMessage(player.itemToString());
        builder.setCancelable(true);


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    //Dialog for when the player clicks on the stats
    private void displayStatsDialog(final Player player) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(player.toString());
        builder.setTitle("Stats");
        builder.setCancelable(true);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Display's the dialog for when the player clicks on a marker
    private void displayStopDialog(final Player player, final Marker marker) {

        if (!firstMove) {
            player.getMarker().setVisible(true);
            Location l1 = new Location("");
            Location l2 = new Location("");
            l1.setLatitude(player.getMarker().getPosition().latitude);
            l1.setLongitude(player.getMarker().getPosition().longitude);
            l2.setLatitude(marker.getPosition().latitude);
            l2.setLongitude(marker.getPosition().longitude);
            float distance = l1.distanceTo(l2);
            setTicketPrice(distance);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This is " + marker.getTitle() + "\n" + "This will cost:"
                    + Integer.toString(ticketPrice) + " gold");
            builder.setCancelable(true);


            builder.setPositiveButton("Move", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (previousClickedMarker != null && ticketPrice <= player.getGold()) {
                        previousClickedMarker.setVisible(true);

                    }

                    if (ticketPrice <= player.getGold()) {
                        LatLng clickedMarkerLocation = marker.getPosition();
                        marker.setVisible(false);
                        player.getMarker().setPosition(clickedMarkerLocation);
                        player.getMarker().setTitle(marker.getTitle());
                        player.loseGold(ticketPrice);
                        player.increaseExp(5);
                        player.increaseMoves();

                        previousClickedMarker = marker;
                        line.add(player.getMarker().getPosition(), marker.getPosition()).width(5).color(Color.RED);

                        lineCords.add(new LatLng(playerMarker.getPosition().latitude, playerMarker.getPosition().longitude));
                        lineCords.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
                        map.addPolyline(line);

                        dialog.cancel();
                    } else {
                        Toast.makeText(getApplicationContext(), "You do not have enough gold to travel here",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            player.getMarker().setVisible(true);
            LatLng clickedMarkerLocation = marker.getPosition();
            if (previousClickedMarker != null) {
                previousClickedMarker.setVisible(true);

            }
            marker.setVisible(false);
            player.getMarker().setPosition(clickedMarkerLocation);
            player.getMarker().setTitle(marker.getTitle());
            player.loseGold(ticketPrice);
            player.increaseExp(5);
            firstMove = false;
            previousClickedMarker = marker;
            line.add(player.getMarker().getPosition(), marker.getPosition()).width(5).color(Color.RED);

        }

    }


    //Returns an enemy from the enemies list
    private Enemy getEnemy(Marker marker) {
        Enemy e = null;
        int id = Integer.parseInt(marker.getSnippet());
        for (int i = 0; i < enemies.size(); i++) {
            if (id == enemies.get(i).getId()) {
                e = enemies.get(i);
                break;
            }
        }
        return e;
    }

    //Returns an item from the items list
    private Item getItem(Marker marker) {
        Item e = null;
        int id = Integer.parseInt(marker.getSnippet());
        for (int i = 0; i < items.size(); i++) {
            if (id == items.get(i).getId()) {
                e = items.get(i);
                break;
            }
        }
        return e;
    }

    //Fills the map with bikepoints
    public class JSONTask extends AsyncTask<String, String, ArrayList<Site>> {

        @Override
        protected ArrayList<Site> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;


            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader((new InputStreamReader(stream)));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String bikePoints = buffer.toString();
                try {

                    JSONArray finalArray = new JSONArray(bikePoints);
                    for (int i = 0; i < finalArray.length(); i++) {
                        JSONObject iObject = finalArray.getJSONObject(i);
                        String id = iObject.getString("id");
                        String name = iObject.getString("commonName");
                        Double lat = Double.parseDouble(iObject.getString("lat"));
                        Double lon = Double.parseDouble(iObject.getString("lon"));
                        LatLng location = new LatLng(lat, lon);
                        sites.add(new Site(id, name,
                                location));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

                return sites;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException c) {
                c.printStackTrace();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Site> result) {
            super.onPostExecute(result);
            Random r = new Random();

            for (int i = 0; i < result.size(); i++) {
                int i1 = r.nextInt(100 - 20) + 20; //Determines if an enemy spawns
                MarkerOptions option = new MarkerOptions().position(result.get(i).getLocation())
                        .title(result.get(i).getName()).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                if (i1 < 30) {
                    LatLng enemyLocal = new LatLng(result.get(i).getLocation().latitude + 0.0004,
                            result.get(i).getLocation().longitude + 0.0004);

                    //Enemy
                    int gold = r.nextInt(50 - 20) + 20;
                    int health = r.nextInt(200-100) + 100;
                    MarkerOptions enemyOption = new MarkerOptions().position(enemyLocal).title("Enemy").snippet(Integer.toString(i)).icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    enemies.add(new Enemy(i, "Enemy", "Monster of London", health, enemyOption, gold));
                    map.addMarker(enemyOption);

                }


                //Weapon
                if (i1 == 22) {
                    LatLng itemLocal = new LatLng(result.get(i).getLocation().latitude - 0.0004,
                            result.get(i).getLocation().longitude - 0.0010);
                    Item item = new Item(i, "Weapon", "Used to strengthen your attacks", r.nextInt(10 - 1) + 1);
                    item.setItemEffectValue();
                    MarkerOptions itemOption = new MarkerOptions().position(itemLocal).title(item.getName()).snippet(Integer.toString(i)).icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    item.setMarker(itemOption);
                    items.add(item);
                    map.addMarker(itemOption);

                }

                //Shield

                if (i1 == 23) {
                    LatLng shieldLocal = new LatLng(result.get(i).getLocation().latitude - 0.00010,
                            result.get(i).getLocation().longitude + 0.0010);
                    Item item = new Item(i, "Shield", "Used to reduce damage requires a weapon", r.nextInt(10 - 1) + 1);
                    item.setItemEffectValue();
                    MarkerOptions shieldOption = new MarkerOptions().position(shieldLocal).title(item.getName()).snippet(Integer.toString(i)).icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    item.setMarker(shieldOption);
                    items.add(item);
                    map.addMarker(shieldOption);
                }


                //Healer
                if (i1 == 25) {
                    LatLng healerLocal = new LatLng(result.get(i).getLocation().latitude + 0.0004,
                            result.get(i).getLocation().longitude - 0.0004);
                    MarkerOptions healOption = new MarkerOptions().position(healerLocal).title("Healer").snippet(Integer.toString(i)).icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    healers.add(new Healer(i, healOption));
                    map.addMarker(healOption);

                }

                map.addMarker(option);
            }
            playerMarker = map.addMarker(new MarkerOptions().position(sites.get(0).getLocation()).title(sites.get(0).getName()).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            player.setMarker(playerMarker);
            player.getMarker().setVisible(false);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(player.getMarker().getPosition(), 14));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (gameStarted) {
            if (id == R.id.action_inventory) {
                displayInventoryDialog(player);

            }
            else if(id == R.id.action_stats) {
                displayStatsDialog(player);
            }
            else if(id == R.id.action_save) {
                saveGame();
            }

        }
        return true;
    }
}