package com.example.jay.whatshap;

/**
 * Created by ElijahCFisher on 4/23/2017.
 */

public class Event {
    private String id;
    private String name;
    private String place;
    private String longitude;
    private String latitude;
    private String description;
    private String start_time;
    private String end_time;
    private String rsvp_status;

    public Event()
    {
    }
    public Event(String id, String name, String place, String longitude, String latitude, String description, String start_time, String end_time, String rsvp_status) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.start_time = start_time;
        this.end_time = end_time;
        this.rsvp_status = rsvp_status;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setEv_name(String name) {
        this.name = name;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public void setLongitude(String longitude){this.longitude = longitude;}
    public void setLatitude(String latitude){this.latitude = latitude;}
    public void setDescription(String description){this.description = description;}
    public void setStart_time(String start_time){this.start_time = start_time;}
    public void setEnd_time(String end_time){this.end_time = end_time;}
    public void setRsvp_status(String rsvp_status){this.rsvp_status = rsvp_status;}

    public String getId() {return id;}
    public String getName() {
        return name;
    }
    public String getPlace() {
        return place;
    }
    public String getLongitude() { return longitude; }
    public String getLatitude() { return latitude; }
    public String getDescription() { return description; }
    public String getStart_time() { return start_time;}
    public String getEnd_time() { return end_time; }
    public String getRsvp_status() { return rsvp_status; }
}