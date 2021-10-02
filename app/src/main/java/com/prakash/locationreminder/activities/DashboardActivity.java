package com.prakash.locationreminder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.prakash.locationreminder.R;
import com.prakash.locationreminder.BLL.AddTaskBLL;
import com.prakash.locationreminder.StrictMod.StrictMod;
import com.prakash.locationreminder.interfaces.Url;
import com.prakash.locationreminder.models.LongLat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.prakash.locationreminder.interfaces.Url.cookie;


public class DashboardActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, LocationListener {

    private DrawerLayout drawerLayout;
    private GoogleMap map;
    private ImageView imgAddAlarm;
    private View navHome,navTask,navSetting,navHelp,navAbout,navExit;
    private SearchView search_map;
    private Double latitude,longitude;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 2000;
    private List<LongLat> longLatList = new ArrayList<>();
    private LocationManager manager;
    private boolean vibration;
    private boolean sound;
    private int radius;
    private Marker marker;
    private SharedPreferences preferences;
    private boolean isDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        drawerLayout = findViewById(R.id.drawerLayout);
        imgAddAlarm = findViewById(R.id.imgAddAlarm);
        navHome = findViewById(R.id.navHome);
        navTask = findViewById(R.id.navTask);
        navSetting = findViewById(R.id.navSetting);
        navHelp = findViewById(R.id.navHelp);
        navAbout = findViewById(R.id.navAbout);
        navExit = findViewById(R.id.navExit);
        search_map = findViewById(R.id.search_map);

        //for Search location
        search_map.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = search_map.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(DashboardActivity.this);
                try{
                    addressList = geocoder.getFromLocationName(location,1);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                map.addMarker(new MarkerOptions().position(latLng).title(location));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        imgAddAlarm.setOnClickListener(this);
        navHome.setOnClickListener(this);
        navTask.setOnClickListener(this);
        navSetting.setOnClickListener(this);
        navHelp.setOnClickListener(this);
        navAbout.setOnClickListener(this);
        navExit.setOnClickListener(this);
        preferences = getSharedPreferences("Locationreminder", MODE_PRIVATE);
        vibration = preferences.getBoolean("Vibration", false);
        sound = preferences.getBoolean("Sound", false);
        radius = preferences.getInt("radius", 200);
    }

    //for show the selected location after adding the location and task
    private void getlonglat() {
        Url.getEndPoints().getlonglat(cookie).enqueue(new Callback<List<LongLat>>() {
            @Override
            public void onResponse(Call<List<LongLat>> call, Response<List<LongLat>> response) {
                if (response.isSuccessful()) {
                    longLatList = response.body();
                    for (LongLat longLat : longLatList) {
                        CircleOptions options = new CircleOptions();
                        options.radius(radius);
                        options.center(new LatLng(Double.parseDouble(longLat.getLat()), Double.parseDouble(longLat.getLon())));
                        options.strokeWidth(0);
                        options.fillColor(Color.parseColor("#500084d3"));
                        map.addCircle(options);
                    }

                }
            }

            @Override
            public void onFailure(Call<List<LongLat>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //for open drawer
    public void openDrawer(View view) {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    //for current location
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.setMyLocationEnabled(true);
        getlonglat();
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 001);
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) return;
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        marker = map.addMarker(new MarkerOptions().position(myLocation).title("Me"));
    }
        //for different view in map
    public void sateliteview(View view) {
        if (map.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

    }else if
        (map.getMapType() == GoogleMap.MAP_TYPE_SATELLITE){
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }else if(map.getMapType() ==GoogleMap.MAP_TYPE_HYBRID){
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

    }

    //for add location and task
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgAddAlarm) {
            final LatLng latLng = new LatLng(map.getCameraPosition().target.latitude, map.getCameraPosition().target.longitude);
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.layout_add_alarm);
            final EditText etName = dialog.findViewById(R.id.etName);
            final EditText etTask = dialog.findViewById(R.id.etTask);
            TextView tvLatitude = dialog.findViewById(R.id.tvLatitude);
            TextView tvLongitude = dialog.findViewById(R.id.tvLongitude);
            Button btnAdd = dialog.findViewById(R.id.btnAdd);
            Button btnCancel = dialog.findViewById(R.id.btnCancel);

            tvLatitude.setText("Latitude : " + latLng.latitude);
            tvLongitude.setText("Longitude : " + latLng.longitude);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                        etName.setError("Please enter name.");
                        etName.requestFocus();
                        return;
                    }
                    Toast.makeText(DashboardActivity.this, "Alarm Added Successfully.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            dialog.show();
            dialog.show();
        } else if (v.getId() == R.id.navHome) {
            startActivity(new Intent(this, MapActivity.class));
            finish();
        } else if (v.getId() == R.id.navTask) {
            startActivity(new Intent(this, LocationlistActivity.class));
        } else if (v.getId() == R.id.navSetting) {
            startActivity(new Intent(this, SettingActivity.class));
        } else if (v.getId() == R.id.navHelp) {
            startActivity(new Intent(this, HelpActivity.class));
        } else if (v.getId() == R.id.navAbout) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (v.getId() == R.id.navExit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure want to Exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           dialogInterface.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

//for finding nearbylocation from current location
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        switch(v.getId())
        {
            case R.id.search_map:
                EditText tf_location =  findViewById(R.id.TF_location);
                String location = tf_location.getText().toString();
                List<Address> addressList;


                if(!location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 5);

                        if(addressList != null)
                        {
                            for(int i = 0;i<addressList.size();i++)
                            {
                                LatLng latLng = new LatLng(addressList.get(i).getLatitude() , addressList.get(i).getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(location);
                                map.addMarker(markerOptions);
                                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                map.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.B_hospitals:
                map.clear();
                String hospital = "hospital";
                String url = getUrl(latitude, longitude, hospital);
                dataTransfer[0] = map;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(DashboardActivity.this, "Showing Nearby hospital", Toast.LENGTH_SHORT).show();
                break;
            case R.id.B_shopping:
                map.clear();
                String shopping_mall = "shopping_mall";
                url = getUrl(latitude, longitude, shopping_mall);
                dataTransfer[0] = map;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(DashboardActivity.this, "Showing Nearby shopping mall", Toast.LENGTH_SHORT).show();
                break;

            case R.id.B_supermarket:
                map.clear();
                String grocery_or_supermarket = "grocery_or_supermarket";
                url = getUrl(latitude, longitude, grocery_or_supermarket);
                dataTransfer[0] = map;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(DashboardActivity.this, "Showing Nearby Fitness hub", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {
        StringBuilder googlePlaceUrl = new StringBuilder(" https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyBXZ3UwEv6aLotx19i2e8xbbqmJV70NWPk");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    //for location change and run function vibration and ringtone when user reached in the destination
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LongLat foundLocation = null;
        for (LongLat longLatL : longLatList) {
            Location pointLocation = new Location("");
            pointLocation.setLatitude(Double.parseDouble(longLatL.getLat()));
            pointLocation.setLongitude(Double.parseDouble(longLatL.getLon()));

            if(marker == null)return;
            marker.remove();
            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            marker = map.addMarker(new MarkerOptions().position(myLocation).title("Me"));

            if (location.distanceTo(pointLocation) < radius) {
                if (vibration) {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        v.vibrate(2000);
                    }
                }
                final MediaPlayer mPlayer = MediaPlayer.create(DashboardActivity.this, R.raw.song);
                if (sound) {
                    mPlayer.start();
                }
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.layout_dismiss);
                dialog.setCancelable(false);
                TextView tvText = dialog.findViewById(R.id.tvText);
                Button btnDismiss = dialog.findViewById(R.id.btnDismiss);
                tvText.setText("You have reached " + longLatL.getName());
                showNotification("Near to " + longLatL.getName(), longLatL.getTask());
                btnDismiss.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        mPlayer.stop();
                        isDialogShowing = false;
                    }
                });
                dialog.show();
                isDialogShowing = true;
                foundLocation = longLatL;
                continue;
            }
        }
        if (foundLocation != null) {
            Url.getEndPoints().deletelonglat(cookie, foundLocation.get_id()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(!response.isSuccessful()){
                        Toast.makeText(DashboardActivity.this, "Failed to delete.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(DashboardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            map.clear();
            getlonglat();
            longLatList.remove(foundLocation);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //for notification function
    private void showNotification(String title, String desc) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "Channel1";
            String description = "This is channel 1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Channel1", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Channel1")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(desc)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(1, builder.build());

    }
}
