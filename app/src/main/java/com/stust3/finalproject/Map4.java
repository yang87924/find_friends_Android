package com.stust3.finalproject;

import android.Manifest;
import android.app.Instrumentation;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stust3.finalproject.databinding.ActivityMap4Binding;


import java.util.ArrayList;
import java.util.List;

public class Map4 extends FragmentActivity implements OnMapReadyCallback {

    static String collection = "friend";//集合名稱
    private GoogleMap mMap;
    private ActivityMap4Binding binding;
    LatLng latLng2 ;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Spinner
    private String[] user={"請選擇","使用者設定","登出"};
    private String[] admin={"請選擇","管理者設定","登出"};
    //Spinner結束
    private String data,showdata;
    private String id,name,gender,bd,phone;
    double E,N;

    String myid = data;
    friend m1 = new friend();
    //GPS---------
    private double locationX = 0.0;
    private double locationY = 0.0;
    boolean gpsON = false;
    LocationManager mlocationManager;
    //---------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMap4Binding.inflate(getLayoutInflater());

        Intent it = getIntent();
        data = it.getStringExtra("data");


        setContentView(binding.getRoot());
        GPS();//抓取GPS位置
        //接收資料

        Log.d("DEMMO",data);
        binding.useret.setText(data);
        //接收資料結束---------------------------------


        //m1.GPS = new GeoPoint(120,15);


        if(data.equals("4A690103@stust.edu.tw"))
        {
            //spinner設定
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Map4.this,
                    android.R.layout.simple_spinner_item,admin);
            binding.spinner.setAdapter(adapter);
            binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String sel = adapterView.getSelectedItem().toString();
                    if(i==1){
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Map4.this,
                                android.R.layout.simple_spinner_item,admin);
                        binding.spinner.setAdapter(adapter);
                        Intent it = new Intent(Map4.this,manager_layout.class);
                        startActivity(it);
                        String email=binding.useret.getText().toString();
                        Log.d("Demo",email);
                        it.putExtra("data", email);
                        startActivity(it);
                    }
                    else if(i==2){
                        new Thread() {
                            public void run() {
                                try {
                                    Instrumentation inst = new Instrumentation();
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                                } catch (Exception e) {
                                }
                            }
                        }.start();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            //sprinner結束------------------------------
        }
        else
        {
            //spinner設定
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Map4.this,
                    android.R.layout.simple_spinner_item,user);
            binding.spinner.setAdapter(adapter);
            binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String sel = adapterView.getSelectedItem().toString();
                    if(i==1){
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Map4.this,
                                android.R.layout.simple_spinner_item,user);
                        binding.spinner.setAdapter(adapter);
                        Intent it = new Intent(Map4.this,user_Layout.class);
                        String email=binding.useret.getText().toString();
                        Log.d("Demo",email);
                        it.putExtra("data", email);
                        startActivity(it);
                    }
                    else if(i==2){
                        new Thread() {
                            public void run() {
                                try {
                                    Instrumentation inst = new Instrumentation();
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                                } catch (Exception e) {
                                }
                            }
                        }.start();
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            //sprinner結束------------------------------
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    //抓取GPS
    private  void GPS(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(Map4.this,"權限沒開",Toast.LENGTH_SHORT).show();
            //binding.textlatitude.setText("權限沒開");
//            如果是6.0以上的去需求權限
            requestCameraPermission();
            return;
        }
        else
        {
            //binding.textlatitude.setText("");
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
    //更新GPS
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
            binding.textView2.setText(""+locationX);
            binding.textView4.setText(""+locationY);
        }
    }
    //監聽器會自動提供 Location 的資訊
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
    //開啟GPS權限
    private void requestCameraPermission(){
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
    //去設定權限頁面
    private void goToAppSetting(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", this.getPackageName(), null));
        startActivityForResult(intent , 0x00);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    /*@Override

    protected void onResume() {

        super.onResume();

        onCreate(null);

    }*/
    @Override
    //地圖定位
    public void onMapReady(GoogleMap googleMap) {
        //fb_update();
        mMap = googleMap;
        GeoPoint g;

        db.collection("friend")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        showdata=binding.useret.getText().toString();
                        //  LatLng latLng2 = null;
                        if (task.isComplete()) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("DEMMO", data+"test2"+showdata);
                                    //GeoPoint geoPoint = document.getGeoPoint("GPS");
                                    if(document.getString("id").equals(data)) {
                                        name = document.getString("name");
                                        id = document.getString("id");
                                        Log.d("DEMMO", data+"test1"+id);
                                        gender = document.getString("gender");
                                        phone= document.getString("phone");
                                        bd= document.getString("bd");

                                        E=document.getDouble("E");
                                        N=document.getDouble("N");

                                        m1.E =locationX;
                                        m1.N=locationY;
                                        m1.id=id;
                                        m1.gender=gender;
                                        m1.bd=bd;
                                        m1.name=name;
                                        m1.phone=phone;

                                        db.collection(collection)
                                                .document(data)
                                                .set(m1);
                                        LatLng latLng = new LatLng(E, N);
                                        //refresh();
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                                        latLng2 = latLng;  //設定最後的景點位置為地圖中間位置
                                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                                .target(latLng2).zoom(18).build();
                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)); //動畫方式移至對應地點
                                    }
                                    else
                                    {
                                        name = document.getString("name");
                                        E=document.getDouble("E");
                                        N=document.getDouble("N");
                                        LatLng latLng = new LatLng(E, N);
                                        //refresh();
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(name));

                                    }




                                    //ERROR
                                    //latLng2 = latLng;
                                    //Log.d("DEMMO", "" + geoPoint.getLongitude()+",,,,"+binding.useret.getText().toString());

                                    // if(data.equals(binding.useret.getText().toString()))
                                    //{




                                    //Log.d("DEMMO", "" + geoPoint.getLongitude()+",,,,"+id);

                                    //}




                                }
                            }
                        }



                    }
                });

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //    CameraPosition cameraPosition=new CameraPosition.Builder().target(stust).zoom(14).build();
        //    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)); //動畫方式移至對應地點



        mMap.getUiSettings().setZoomControlsEnabled(true); //顯示放大縮小的圖示
    }
    private  void refresh(){
        onCreate((null));
    }
    /*private void fb_update(){
        db.collection("friend")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //  LatLng latLng2 = null;
                        if (task.isComplete()) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {


                                    name = document.getString("name");
                                    id = document.getString("id");
                                    gender = document.getString("gender");
                                    phone = document.getString("phone");
                                    bd = document.getString("bd");
                                }
                            }}
                        }
    }*/

}
// Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}*/