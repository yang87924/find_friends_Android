package com.stust3.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stust3.finalproject.databinding.ActivityManagerLayoutBinding;

import java.util.ArrayList;

public class manager_layout extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityManagerLayoutBinding binding;
    static String collection = "friend";
    double E,N;
    String data;
    ArrayList data_name, data_gender, data_phone, data_bd,data_id;
    GeoPoint GPS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_manager_layout);
        binding = ActivityManagerLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent it = getIntent();
        data = it.getStringExtra("data");

         /*SQLiteDatabase bb = openOrCreateDatabase("friend", MODE_PRIVATE, null);

       db.collection("friend")
                .document(binding.etName.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isComplete()){
                            if(task.isSuccessful()) {
                                DocumentSnapshot ds=task.getResult();
                                friend s=ds.toObject(friend.class);
                                //bb.insert(s.id.toString(),s.name.toString(),s.gender,s.city.toString());
                                //showData();
                            }
                        }
                    }
                });*/

        showdata();

       /* try {
            String Str = "create table product" +
                    "(Name text," +
                    " Gender text," +
                    " Phone text," +
                    " bd text," +
                    " GPS text," +
                    " id text)";
            bb.execSQL(Str);
        }catch (Exception err){

        }*/

        binding.managerLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                binding.etId.setText(data_id.get(position).toString());
                binding.etName.setText(data_name.get(position).toString());
                binding.etGender.setText(data_gender.get(position).toString());
                binding.etPhone.setText(data_phone.get(position).toString());
                binding.etBd.setText(data_bd.get(position).toString());
                //binding.tvGPS.setText(GPS.toString());
            }
        });

        binding.btnRevise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String myid = binding.etId.getText().toString();
                friend m1 = new friend();
                //m1.GPS = new GeoPoint(120,15);

                m1.E=E;
                m1.N=N;
                m1.id = binding.etId.getText().toString();
                m1.name = binding.etName.getText().toString();
                m1.gender = binding.etGender.getText().toString();
                m1.phone = binding.etPhone.getText().toString();
                m1.bd = binding.etBd.getText().toString();
                db.collection(collection)
                        .document(myid)
                        .set(m1);
                showdata();
            }
        });

    }
    public class MyAdapter extends BaseAdapter {

        Context mContext;
        LayoutInflater mylayout;

        public MyAdapter(Context mContext) {
            this.mContext = mContext;
            mylayout = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return data_name.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txt_name, txt_gender,txt_phone,txt_bd;
            //連接layout
            convertView = mylayout.inflate(R.layout.manager_itemlayout_layout, null);
            //連接元件
            txt_name = convertView.findViewById(R.id.name);
            txt_gender = convertView.findViewById(R.id.gender);
            txt_phone = convertView.findViewById(R.id.phone);
            txt_bd = convertView.findViewById(R.id.bd);
            //設定內容
            txt_name.setText(data_name.get(position).toString());
            txt_gender.setText(data_gender.get(position).toString());
            txt_phone.setText(data_phone.get(position).toString());
            txt_bd.setText(data_bd.get(position).toString());
            return convertView;

        }
    }
    public void showdata()
    {
        db.collection(collection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            if (task.isSuccessful()) {
                                data_id = new ArrayList<>();
                                data_name = new ArrayList<>();
                                data_gender = new ArrayList<>();
                                data_phone = new ArrayList<>();
                                data_bd = new ArrayList<>();
                                //data_GPS = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //if(document.getString("id").toString().equals(data))
                                    //{
                                    E=document.getDouble("E");
                                    N=document.getDouble("N");
                                    data_id.add(document.getString("id"));
                                    data_name.add(document.getString("name"));
                                    data_gender.add(document.getString("gender"));
                                    data_phone.add(document.getString("phone"));
                                    data_bd.add(document.getString("bd"));
                                    //}
                                    /*GeoPoint geoPoint = document.getGeoPoint("GPS");
                                    LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());*/
                                    //friend ms = document.toObject(friend.class);

                                    //GPS=new GeoPoint(document.getGeoPoint("GPS"));
                                    //data_GPS.add(document.getString("GPS"));
                                    /*Log.d("DEMMO",document.getGeoPoint("GPS").toString());
                                    Log.d("DEMMO",latLng.toString());*/
                                }
                                MyAdapter adapter = new manager_layout.MyAdapter(manager_layout.this);
                                binding.managerLv.setAdapter(adapter);
                            }
                        }
                    }
                });
    }
}