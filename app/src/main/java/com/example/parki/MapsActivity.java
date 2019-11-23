package com.example.parki;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener
        //, RoutingListener
{

    private static final String TAG = "Maps Activity";

    private static final float DEFAULT_ZOOM = 30f;
    //private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    final int LOCATION_REQUEST_CODE = 1;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    boolean firstLoad = false;
    LatLng mLastLocation;
    private GoogleMap mMap;
    private ProgressBar progressBar;
    //private boolean routeDrawn = false;
    //private List<Polyline> polylines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_maps);
        //polylines = new ArrayList<>();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        SupportMapFragment mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            mMapFragment.getMapAsync(this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationEnabled();
    }

    public void checkLocationEnabled() {
        Log.e(TAG, "checkLocationEnabled");
        LocationManager lm = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user

            new AlertDialog.Builder(MapsActivity.this)
                    .setMessage("Location Not Enabled")
                    .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            MapsActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sign_out:
                MyApplication app = ((MyApplication) MapsActivity.this.getApplicationContext());
                app.logoutUser(MapsActivity.this);
                return true;
            case R.id.change_password:
                showChangePasswordDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showChangePasswordDialog() {
        Log.e("showChangePassword", "showChangePasswordDialog");
        //TODO :: Show Change Password Dialog

        final EditText oldPassword = new EditText(MapsActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        oldPassword.setLayoutParams(lp);
        oldPassword.setHint("Old Password");


        final EditText newPassword = new EditText(MapsActivity.this);
        newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPassword.setLayoutParams(lp);
        newPassword.setHint("New Password");

        LinearLayout layout = new LinearLayout(MapsActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(oldPassword);
        layout.addView(newPassword);


        new AlertDialog.Builder(MapsActivity.this)
                .setTitle("Change Your Password")
                .setView(layout)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (TextUtils.isEmpty(oldPassword.getText())) {
                            oldPassword.setError(getString(R.string.required_field));
                        } else if (TextUtils.isEmpty(newPassword.getText())) {
                            newPassword.setError(getString(R.string.required_field));
                        } else {
                            changePasswordApi(oldPassword.getText().toString(), newPassword.getText().toString());
                        }


                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void changePasswordApi(final String oldPassword, final String newPassword) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.CHANGE_PASSWORD_URL;
        url = url.replace(" ", "%20");

        Log.e("Login API", url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject apiResult = new JSONObject(response);
                    Toast.makeText(MapsActivity.this, apiResult.getString("message"), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    //e.printStackTrace();
                    //progressBar.setVisibility(View.GONE);
                    Toast.makeText(MapsActivity.this, "Server Error", Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Network Connection Error", Toast.LENGTH_LONG).show();
                //progressBar.setVisibility(View.GONE);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                MyApplication app = ((MyApplication) getApplication());

                params.put("password", oldPassword);
                params.put("new_password", newPassword);
                params.put("id", app.currentUser.getId());

                return params;
            }
        };

        queue.add(stringRequest);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady");

        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                final String tag = String.valueOf(marker.getTag());

                if (tag.equals("")) {
                    Log.e("Busy", "Busy Location");
                    Toast.makeText(MapsActivity.this, "Place Is Busy", Toast.LENGTH_LONG).show();

                } else {
                    Log.e("available", "available Location");
                    /*
                    routeDrawn = true;
                    double lat = Double.valueOf(tag.split(",")[0]);
                    double lng = Double.valueOf(tag.split(",")[1]);
                    LatLng markerLocation = new LatLng(lat, lng);
                    getRouteToMarker(markerLocation);
                    */

                    new AlertDialog.Builder(MapsActivity.this)
                            .setMessage("GO TO THIS PARK LOCATION ?")
                            .setPositiveButton("GO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    float lat = Float.valueOf(tag.split(",")[0]);
                                    float lon = Float.valueOf(tag.split(",")[1]);

                                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", lat, lon, "parkLocation");
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    intent.setPackage("com.google.android.apps.maps");
                                    MapsActivity.this.startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();


                }

                return false;
            }
        });


        buildGoogleApiClient();

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //call api to get markers
        markersApi();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "onConnected");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged");

        if (!firstLoad) {
            //LatLng jeddahuni = new LatLng(21.628105387300092, 39.10916827976223);
            mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
            //mLastLocation = new LatLng(21.560892, 39.216921);
            moveCamera(mLastLocation, DEFAULT_ZOOM);
            firstLoad = true;
        }

    }

    private void markersApi() {
        Log.e(TAG, "markersApi");

        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.MARKERS_URL;

        url = url.replace(" ", "%20");

        Log.e("Markers API", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONArray array = new JSONArray(response);
                    mMap.clear();
                    //Log.e("array", array.toString());

                    for (int i = 0; i < array.length(); i++) {


                        //Log.e("Marker Data ", array.get(i).toString());

                        LatLng courseLocation = new LatLng(Float.valueOf(array.getJSONObject(i).getString("lat"))
                                , Float.valueOf(array.getJSONObject(i).getString("lng")));

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(courseLocation);
                        if (array.getJSONObject(i).getString("slot_status").equals("busy")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.busy_pin));
                        } else {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_icon));
                        }

                        //available,busy
                        markerOptions.title(array.getJSONObject(i).getString("slot_status"));
                        //markerOptions.snippet(array.getJSONObject(i).getString("branche_name"));

                        Marker marker = mMap.addMarker(markerOptions);
                        if (array.getJSONObject(i).getString("slot_status").equals("available")) {
                            marker.setTag(array.getJSONObject(i).getString("lat") + "," + array.getJSONObject(i).getString("lng"));
                        } else {
                            marker.setTag("");
                        }

                    }


                    progressBar.setVisibility(View.GONE);


                } catch (JSONException e) {
                    //e.printStackTrace();
                    Toast.makeText(MapsActivity.this, "Network Connection Error", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Network Connection Error", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }
        );

        queue.add(stringRequest);

    }

    protected synchronized void buildGoogleApiClient() {
        Log.e(TAG, "buildGoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d("move camera", "moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
/*
    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void getRouteToMarker(LatLng pickupLatLng) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(mLastLocation, pickupLatLng)
                .build();
        routing.execute();
        routeDrawn = true;
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {
    }

    private void erasePolylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }*/
}
