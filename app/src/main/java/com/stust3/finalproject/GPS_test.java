package com.stust3.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.stust3.finalproject.databinding.ActivityGpsTestBinding;

import java.util.ArrayList;
import java.util.List;

public class GPS_test extends AppCompatActivity {
    private double locationX = 0.0;
    private double locationY = 0.0;
    private ActivityGpsTestBinding binding;
    boolean gpsON = false;
    LocationManager mlocationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_gps_test);
        binding = ActivityGpsTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(GPS_test.this,"權限沒開",Toast.LENGTH_SHORT).show();
            binding.textlatitude.setText("權限沒開");
//            如果是6.0以上的去需求權限
            requestCameraPermission();
            return;
        }
        else
        {
            binding.textlatitude.setText("");
        }
        mlocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);//設置允許產生資費
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = mlocationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = mlocationManager.getLastKnownLocation(provider);
        updateLocation(location);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mlocationManager.requestLocationUpdates(provider, 3000, 0, locationListener);
        gpsON = true;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1
            );
        }else{
            Location location1 = mlocationManager.getLastKnownLocation(provider);
            updateLocation(location);
            mlocationManager.requestLocationUpdates(provider, 3000, 0, locationListener);
        }

    }
    private void updateLocation(Location location) {
        if (location != null) {
            locationX = location.getLatitude();
            locationY  = location.getLongitude();
        } else {
            locationX = 0.0;
            locationY = 0.0;
        }
        //背景執行時關閉顯示地點
        if(gpsON == true){
            //Toast.makeText(GPS_test.this, "" + "x:" + locationX  + " y:" + locationY , Toast.LENGTH_SHORT).show();
            binding.textlongitude.setText("經度:"+locationX);
            binding.textlatitude.setText("緯度:"+locationY);
        }
    }
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location)
        {
            updateLocation(location);

        }
        public void onProviderDisabled(String provider){
            updateLocation(null);
        }
        public void onProviderEnabled(String provider)
        {

        }
        public void onStatusChanged(String provider, int status,Bundle extras){

        }
    };
    private void requestCameraPermission(){//開啟GPS權限
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
            return;

        final List<String> permissionsList = new ArrayList<>();
        if(this.checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.CAMERA);
        if(this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionsList.size()<1)
            return;
        if(this.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
            this.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]) , 0x00);
        else
            goToAppSetting();
    }
    private void goToAppSetting(){//去設定權限頁面
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", this.getPackageName(), null));
        startActivityForResult(intent , 0x00);
    }

}