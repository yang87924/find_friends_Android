package com.stust3.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stust3.finalproject.databinding.ActivityMap4Binding;
import com.stust3.finalproject.databinding.ActivityRegisteredUserBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class registered_user extends AppCompatActivity {
    private StorageReference mStorageRef;
    private ActivityRegisteredUserBinding binding;
    private FirebaseAuth mAuth;  //宣告變數
    static String collection = "friend";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
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
    private boolean changeimage=false;

    //---------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent d) {
        super.onActivityResult(requestCode, resultCode, d);
        if (resultCode == RESULT_OK && requestCode == 101) {
            Uri uri = d.getData();

            Glide.with(registered_user.this)
                    .load(uri)
                    .into(binding.imageView);
            mStorageRef.child(binding.editUserID.getText().toString())
                    .putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(registered_user.this, "上傳成功", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("DEMMO", e.getMessage());
                        }
                    });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_registered_user);
        binding = ActivityRegisteredUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance(); //建立實體
        mStorageRef= FirebaseStorage.getInstance().getReference();//圖片
        GPS();
        //Toast.makeText(registered_user.this,"認證失敗",Toast.LENGTH_SHORT).show();
        binding.btnRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeimage==false)
                {
                    Toast.makeText(registered_user.this,"請更換照片",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    //建立帳號
                    mAuth.createUserWithEmailAndPassword(binding.editUserID.getText().toString(),binding.editPasswd.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Log.d("Demo","新增OK");
                                    //註冊帳號
                                    Map<String ,Object> m = new HashMap<>();
                                    m.put("name",binding.edName.getText().toString());
                                    m.put("gender",binding.edGender.getText().toString());
                                    m.put("phone",binding.edPhone.getText().toString());
                                    m.put("bd",binding.edBd.getText().toString());
                                    m.put("E",locationX);
                                    m.put("N",locationY);
                                    m.put("id",""+binding.editUserID.getText().toString());

                                    Log.d("Demo", m.get("name").toString()+", "+m.get("age"));


                                    db.collection("friend")
                                            .document(binding.editUserID.getText().toString())
                                            .set(m)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                               /* Log.d("Demo","輸入成功");
                                                Toast.makeText(registered_user.this,"輸入成功: ",Toast.LENGTH_SHORT).show();*/

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("Demo","輸入 失敗");
                                                }
                                            });
                                    Log.d("Demo","註冊成功");
                                    Toast.makeText(registered_user.this,"註冊成功 ",Toast.LENGTH_SHORT).show();
                                    //傳遞資料
                                    Intent it = new Intent(registered_user.this, MainActivity.class);
                                    String email=binding.editUserID.getText().toString();
                                    Log.d("Demo",email);
                                    it.putExtra("email", email);
                                    startActivity(it);

                                    Intent it2 = new Intent(registered_user.this, MainActivity.class);
                                    String passwd=binding.editPasswd.getText().toString();
                                    Log.d("Demo",email);
                                    it.putExtra("passwd", passwd);
                                    startActivity(it);
                                    // Toast.makeText(registered_user.this,"新增成功: "+authResult.getUser().getEmail(),Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull  Exception e) {
                                    Log.d("Demo","建立失敗:"+e.getMessage());
                                    Toast.makeText(registered_user.this,"建立失敗: "+e.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            });
                }





                /*if(user==null)
                    Log.d("Demo","尚未登入");
                else
                {
                    Log.d("Demo","已登入"+user.getEmail()+", "+user.getUid()+", "+user.getDisplayName()+", "+user.getPhotoUrl().toString());
                }
            }*/

            }
        });
        binding.btnChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picker=new Intent(Intent.ACTION_GET_CONTENT);
                picker.setType("image/*");
                startActivityForResult(picker,101);
                changeimage=true;
            }
        });
        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }
    //抓取GPS
    private  void GPS(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(registered_user.this,"權限沒開",Toast.LENGTH_SHORT).show();
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
            //binding.textView2.setText(""+locationX);
            //binding.textView4.setText(""+locationY);
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
}