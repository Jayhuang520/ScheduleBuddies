package com.example.jay.whatshap;

import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ElijahCFisher on 4/23/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_EVENTS = "events";

    public DBHandler(Context context) { super(context, "eventsInfo", null, DATABASE_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + "events" + "("
        + "id" + " TEXT," + "name" + " TEXT," + "place" + " TEXT," + "long" + " TEXT," +
                "lat" + " TEXT," + "desc" + " TEXT," + "start" + " TEXT," +
                "end" + " TEXT," + "rsvp" + " TEXT" + ");";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "events");
        onCreate(db);
    }

    public void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        try{
            String countQuery = "SELECT * FROM " + "events WHERE id = " + event.getId();
            Cursor cursor = db.rawQuery(countQuery, null);

            int gc = cursor.getCount();
            cursor.close();
            if(gc > 0) return;
        }catch(IllegalStateException e) {

        }


        values.put("id",event.getId());
        values.put("name",event.getName());
        values.put("place",event.getPlace());
        values.put("long",event.getLongitude());
        values.put("lat",event.getLatitude());
        values.put("desc",event.getDescription());
        values.put("start",event.getStart_time());
        values.put("end",event.getEnd_time());
        values.put("rsvp",event.getRsvp_status());
        db.insert(TABLE_EVENTS, null, values);
        db.close();
    }

    public Event getEvent(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("events", new String[]{"id",
                "name", "place", "long", "lat", "desc", "start", "end", "rspv"}, "id" + "=?",
        new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Event contact = new Event(cursor.getString(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
        return contact;
    }

    public ArrayList<HashMap<String, String>> getAllEventsPartial() {
        ArrayList<HashMap<String, String>> eventList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + "events";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                //HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//
                HashMap<String, String> event = new HashMap<String, String>();
                //event.put("id",cursor.getString(0));
                event.put("name",cursor.getString(1));
                event.put("place",cursor.getString(2));
                //event.put("longitude",cursor.getString(3));
                //event.put("latitude",cursor.getString(4));
                //event.put("description",cursor.getString(5));
                event.put("start",cursor.getString(6));
                //event.put("end_time",cursor.getString(7));
                //event.put("rsvp_status",cursor.getString(8));
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        return eventList;
    }

    public ArrayList<HashMap<String, String>> getAllEvents() {
        ArrayList<HashMap<String, String>> eventList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + "events";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                //HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//
                HashMap<String, String> event = new HashMap<String, String>();
                event.put("id",cursor.getString(0));
                event.put("name",cursor.getString(1));
                event.put("place",cursor.getString(2));
                event.put("long",cursor.getString(3));
                event.put("lat",cursor.getString(4));
                event.put("desc",cursor.getString(5));
                event.put("start",cursor.getString(6));
                event.put("end",cursor.getString(7));
                event.put("rsvp",cursor.getString(8));
                eventList.add(event);
            } while (cursor.moveToNext());
        }

        return eventList;
    }

    public int getEventsCount() {
        String countQuery = "SELECT * FROM " + "events";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int gc = cursor.getCount();
        cursor.close();

        return gc;
    }
    public int updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id",event.getId());
        values.put("name",event.getName());
        values.put("place",event.getPlace());
        values.put("long",event.getLongitude());
        values.put("lat",event.getLatitude());
        values.put("desc",event.getDescription());
        values.put("start",event.getStart_time());
        values.put("end",event.getEnd_time());
        values.put("rsvp",event.getRsvp_status());

// updating row
        return db.update("events", values, "id" + " = ?",
        new String[]{String.valueOf(event.getId())});
    }

    // Deleting a event
    public void deleteEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("events", "id" + " = ?",
        new String[] { String.valueOf(event.getId()) });
        db.close();
    }
}