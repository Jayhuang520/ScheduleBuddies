package com.example.jay.whatshap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.location.Location;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

        private static final String TAG = MapsActivity.class.getSimpleName();
        private GoogleMap mMap;
        private CameraPosition mCameraPosition;

        // The entry point to Google Play services, used by the Places API and Fused Location Provider.
        private GoogleApiClient mGoogleApiClient;

        // A default location (Sydney, Australia) and default zoom to use when location permission is
        // not granted.
        private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
        private static final int DEFAULT_ZOOM = 15;
        private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
        private boolean mLocationPermissionGranted;

        // The geographical location where the device is currently located. That is, the last-known
        // location retrieved by the Fused Location Provider.
        private Location mLastKnownLocation;

        // Keys for storing activity state.
        private static final String KEY_CAMERA_POSITION = "camera_position";
        private static final String KEY_LOCATION = "location";

        // Used for selecting the current place.
        private final int mMaxEntries = 5;
        private String[] mLikelyPlaceNames = new String[mMaxEntries];
        private String[] mLikelyPlaceAddresses = new String[mMaxEntries];
        private String[] mLikelyPlaceAttributions = new String[mMaxEntries];
        private LatLng[] mLikelyPlaceLatLngs = new LatLng[mMaxEntries];

        private LatLng mLastKnownLocationLatLng;

        Menu dropDown;
        SubMenu subm;

        ArrayList<HashMap<String, String>> eventInfo ;

        JSONArray events = null;
        DBHandler eventList;
        ListView disp_list;

    // On create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps2);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Setup the find events button
        setupFindEvents();

        eventList = new DBHandler(this);
        disp_list = (ListView) findViewById(R.id.show_events);
        eventInfo = new ArrayList<HashMap<String, String>>();

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }


    // Set up the get events button
    public void setupFindEvents() {
        Button find_events = (Button) findViewById(R.id.event_button);
        find_events.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code for getting the events goes here
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback(){
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code

                                try {
                                    String res = object.toString();
                                    StringBuilder new_res = new StringBuilder(res.length()+15);
                                    new_res.append("{\"result\":[");
                                    new_res.append(res);
                                    new_res.append("]}");
                                    String RES = new_res.toString();

                                    JSONObject jsonObj = new JSONObject(RES);
                                    events = jsonObj.getJSONArray("result");

                                    for(int i=0;i<events.length();i++){
                                        JSONObject c = events.getJSONObject(i);
                                        System.out.println(c);
                                        String id = c.getString("id");
                                        String name = c.getString("name");
                                        String Events = c.getString("events");

                                        try {
                                            JSONObject data = new JSONObject(Events);
                                            JSONArray events = data.getJSONArray("data");

                                            LatLng eventLocation;

                                            for(int k = 0; k<events.length(); k++){
                                                JSONObject e = events.getJSONObject(k);
                                                String description = e.getString("description");
                                                //System.out.println(e.getString("name") +"THIS IS A TEST" + e);
                                                String end_time = e.getString("end_time");
                                                String ev_name = e.getString("name");
                                                String place = e.getString("place");
                                                String start_time = e.getString("start_time");
                                                String ev_id = e.getString("id");
                                                String rsvp_status = e.getString("rsvp_status");


                                                JSONObject place_info = new JSONObject(place);
                                                JSONObject location = place_info.getJSONObject("location");
                                                //JSONObject long_map = location.getJSONObject("longitude");
                                                //JSONObject lat_map = location.getJSONObject("latitude");
                                                String place_name = place_info.getString("name");
                                                String longitude = location.getString("longitude");
                                                String latitude = location.getString("latitude");
                                                Double long_map = Double.parseDouble(longitude);
                                                Double lat_map = Double.parseDouble(latitude);

                                                eventLocation = new LatLng(lat_map, long_map);
                                                mMap.addMarker(new MarkerOptions().title(ev_name).position(eventLocation).snippet("Event Time:"+ start_time + " to " + end_time + "\n"+ rsvp_status));


                                                HashMap<String,String> ev = new HashMap<String,String>();
                                                ev.put("name", ev_name);
                                                ev.put("place", place_name);
                                                ev.put("start_time", start_time);

                                                eventInfo.add(ev);
                                                eventList.addEvent(new Event(ev_id, ev_name, place, longitude, latitude, description, start_time, end_time, rsvp_status));
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    ListAdapter adapter = null;
                                    adapter = new SimpleAdapter(
                                            MapsActivity.this, eventInfo, R.layout.event_list,
                                            new String[]{"name","place","start_time"},
                                            new int[]{R.id.disp_id, R.id.disp_name, R.id.disp_events}
                                    );
                                    disp_list.setAdapter(adapter);
                                    System.out.println(eventList.getAllEvents().size());
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,events");
                request.setParameters(parameters);
                request.executeAsync();

            }
        });
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_facebook_logout, menu);
        this.dropDown = menu;

        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.facebook_logout:
                Intent intent =  new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout)findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        // Set markers for events
        //setEventMarkers();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        mLastKnownLocationLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().title("You are here.").position(mLastKnownLocationLatLng));
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    /*
    private void setEventMarkers(){

    }
    */
    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
//    private void showCurrentPlace() {
//        if (mMap == null) {
//            return;
//        }
//
//        if (mLocationPermissionGranted) {
//            // Get the likely places - that is, the businesses and other points of interest that
//            // are the best match for the device's current location.
//            @SuppressWarnings("MissingPermission")
//            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
//                    .getCurrentPlace(mGoogleApiClient, null);
//            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
//                @Override
//                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
//                    int i = 0;
//                    mLikelyPlaceNames = new String[mMaxEntries];
//                    mLikelyPlaceAddresses = new String[mMaxEntries];
//                    mLikelyPlaceAttributions = new String[mMaxEntries];
//                    mLikelyPlaceLatLngs = new LatLng[mMaxEntries];
//                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                        // Build a list of likely places to show the user. Max 5.
//                        mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
//                        mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace().getAddress();
//                        mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
//                                .getAttributions();
//                        mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();
//
//                        i++;
//                        if (i > (mMaxEntries - 1)) {
//                            break;
//                        }
//                    }
//                    // Release the place likelihood buffer, to avoid memory leaks.
//                    likelyPlaces.release();
//
//                    // Show a dialog offering the user the list of likely places, and add a
//                    // marker at the selected place.
//                    openPlacesDialog();
//                }
//            });
//        } else {
//            // Add a default marker, because the user hasn't selected a place.
//            mMap.addMarker(new MarkerOptions()
//                    .title(getString(R.string.default_info_title))
//                    .position(mDefaultLocation)
//                    .snippet(getString(R.string.default_info_snippet)));
//        }
//    }
//
//    /**
//     * Displays a form allowing the user to select a place from a list of likely places.
//     */
//    private void openPlacesDialog() {
//        // Ask the user to choose the place where they are now.
//        DialogInterface.OnClickListener listener =
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // The "which" argument contains the position of the selected item.
//                        LatLng markerLatLng = mLikelyPlaceLatLngs[which];
//                        String markerSnippet = mLikelyPlaceAddresses[which];
//                        if (mLikelyPlaceAttributions[which] != null) {
//                            markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
//                        }
//                        // Add a marker for the selected place, with an info window
//                        // showing information about that place.
//                        mMap.addMarker(new MarkerOptions()
//                                .title(mLikelyPlaceNames[which])
//                                .position(markerLatLng)
//                                .snippet(markerSnippet));
//
//                        // Position the map's camera at the location of the marker.
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
//                                DEFAULT_ZOOM));
//                    }
//                };
//
//        // Display the dialog.
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle(R.string.pick_place)
//                .setItems(mLikelyPlaceNames, listener)
//                .show();
//    }

}
