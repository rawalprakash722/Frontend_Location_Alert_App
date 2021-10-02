package com.prakash.locationreminder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.prakash.locationreminder.R;
import com.prakash.locationreminder.adapters.LocationAdapter;
import com.prakash.locationreminder.interfaces.Url;
import com.prakash.locationreminder.models.LongLat;
import com.prakash.locationreminder.utils.ShakeDetector;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationlistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SensorManager manager;
    private Sensor mAccelerometer;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationlist);

//        //for shake detector while refresh the activity
////        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
////        mAccelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
////        shakeDetector = new ShakeDetector();
////        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
////            @Override
////            public void onShake(int count) {
////                Toast.makeText(LocationlistActivity.this, "Shake Detected!!!", Toast.LENGTH_SHORT).show();
////                getLongLat();
//            }
//        });
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        getLongLat();
    }
}

//    private void getLongLat() {
//        Url.getEndPoints().getlonglat(Url.cookie).enqueue(new Callback<List<LongLat>>() {
//            @Override
//            public void onResponse(Call<List<LongLat>> call, Response<List<LongLat>> response) {
//                LocationAdapter adapter = new LocationAdapter(LocationlistActivity.this, response.body());
//                recyclerView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onFailure(Call<List<LongLat>> call, Throwable t) {
//                Toast.makeText(LocationlistActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public void goBack(View view) {
//        super.onBackPressed();
//    }
//
//    public void refreshList(){
//        getLongLat();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        manager.registerListener(shakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        manager.registerListener(shakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        manager.unregisterListener(shakeDetector);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        manager.unregisterListener(shakeDetector);
//    }
//}