package com.stust3.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Instrumentation;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stust3.finalproject.databinding.ActivityUserLayoutBinding;

import java.util.ArrayList;

public class user_Layout extends AppCompatActivity {

    private StorageReference mStorageRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityUserLayoutBinding binding;
    static String collection = "friend";
    private String data;
    double E,N;
    ArrayList data_name, data_gender, data_phone, data_bd,data_id;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent d) {
        super.onActivityResult(requestCode, resultCode, d);
        if (resultCode == RESULT_OK && requestCode == 101) {
            Uri uri = d.getData();

            Glide.with(user_Layout.this)
                    .load(uri)
                    .into(binding.image2);
            mStorageRef.child(data)
                    .putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(user_Layout.this, "上傳成功", Toast.LENGTH_LONG).show();
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
        //setContentView(R.layout.activity_user_layout);
        binding = ActivityUserLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent it = getIntent();
        data = it.getStringExtra("data");
        mStorageRef= FirebaseStorage.getInstance().getReference();//圖片
        SQLiteDatabase bb = openOrCreateDatabase("friend", MODE_PRIVATE, null);
        showimage();
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
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //friend ms = document.toObject(friend.class);
                                    if(document.getString("id").toString().equals(data))
                                    {
                                        binding.edName.setText(document.getString("name"));
                                        binding.etGender.setText(document.getString("gender"));
                                        binding.etPhone.setText(document.getString("phone"));
                                        binding.edBd.setText(document.getString("bd"));
                                    }
                                    data_id.add(document.getString("id"));
                                    data_name.add(document.getString("name"));
                                    data_gender.add(document.getString("gender"));
                                    data_phone.add(document.getString("phone"));
                                    data_bd.add(document.getString("bd"));
                                    E=document.getDouble("E");
                                    N=document.getDouble("N");
                                }
                            }
                        }
                    }
                });
        binding.image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picker=new Intent(Intent.ACTION_GET_CONTENT);
                picker.setType("image/*");
                startActivityForResult(picker,101);
            }
        });
        /*binding.image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picker=new Intent(Intent.ACTION_GET_CONTENT);
                picker.setType("image/*");
                startActivityForResult(picker,101);
            }
        });*/
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
        binding.modifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String myid = data;
                friend m1 = new friend();

                m1.E=E;
                m1.N=N;
                m1.id=data;
                m1.name = binding.edName.getText().toString();
                m1.gender = binding.etGender.getText().toString();
                m1.phone = binding.etPhone.getText().toString();
                m1.bd = binding.edBd.getText().toString();
                db.collection(collection)
                        .document(myid)
                        .set(m1);

            }
        });
    }
    private void showimage()
    {
        String imgName =data;
        mStorageRef.child(imgName)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(user_Layout.this)
                                .load(uri)
                                .into(binding.image2);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DEMMO",e.getMessage());
                        Toast.makeText(user_Layout.this,"錯誤訊息 \n"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

}