package com.example.jay.whatshap;

/**
 * Created by ElijahCFisher on 4/23/2017.
 */

public class Event {
    private String id;
    private String name;
    private String events;
    public Event()
    {
    }
    public Event(String id, String name, String events)
    {
        this.id=id;
        this.name=name;
        this.events=events;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setEvents(String events) {
        this.events = events;
    }
    public String getId() {
        return id;
    }
    public String getEvents() {
        return events;
    }
    public String getName() {
        return name;
    }
}