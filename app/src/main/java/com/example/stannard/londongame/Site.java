package com.example.stannard.londongame;


import com.google.android.gms.maps.model.LatLng;

public class Site {

    private String id;
    private String name;
    private LatLng location;

        public Site(String id, String name, LatLng location) {
            this.id = id;
            this.name = name;
            this.location = location;
        }

        public String getId() {
            return id;
        }
        public String getName() {
            return name;
        }

        public LatLng getLocation() {
            return location;
        }
    }

