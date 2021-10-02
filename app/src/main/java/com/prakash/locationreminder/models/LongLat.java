package com.prakash.locationreminder.models;

public class LongLat {

    private String _id;
    private String name;
    private String task;
    private String lon;
    private String lat;

    public LongLat(String name, String task, String lon, String lat) {
        this.name = name;
        this.task = task;
        this.lon = lon;
        this.lat = lat;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}


