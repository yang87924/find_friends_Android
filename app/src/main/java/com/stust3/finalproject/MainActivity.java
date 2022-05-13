package com.stust3.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stust3.finalproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;  //宣告變數
    private StorageReference mStorageRef;
    private String email,passwd;
    /*protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 101) {
            Uri uri = data.getData();

            Glide.with(MainActivity.this)
                    .load(uri)
                    .into(binding.image2);
            mStorageRef.child("picture").child(binding.editUserID.getText().toString())
                    .putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MainActivity.this, "上傳成功", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("DEMMO", e.getMessage());
                        }
                    });
        }
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance(); //建立實體
        mStorageRef= FirebaseStorage.getInstance().getReference();//圖片
        Intent it = getIntent();
        email = it.getStringExtra("email");
        Intent it2 = getIntent();
        passwd = it2.getStringExtra("passwd");
        Log.d("Demo","帳號"+email+"  密碼"+passwd);
        if(email!=null  & passwd!=null)
        {
            binding.editUserID.setText(email);
            binding.editPasswd.setText(passwd);
        }


        binding.btn1.setOnClickListener(new View.OnClickListener() {//登入按鈕
            @Override
            public void onClick(View v) {
                String userID=binding.editUserID.getText().toString();
                String pwd =binding.editPasswd.getText().toString();
                mAuth.signInWithEmailAndPassword(userID,pwd)//驗證帳號密碼
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this,"登入成功",Toast.LENGTH_SHORT).show();
                                    /*Intent it = new Intent(MainActivity.this,Map.class);
                                    startActivity(it);*/

                                    //傳遞資料
                                    Intent it = new Intent(MainActivity.this, Map4.class);
                                    String email=binding.editUserID.getText().toString();
                                    Log.d("Demo",email);
                                    it.putExtra("data", email);
                                    startActivity(it);
                                    //showHideButton();
                                }else
                                {
                                    Toast.makeText(MainActivity.this,"認證失敗",Toast.LENGTH_SHORT).show();
                                    Log.d("Demo","認證失敗");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull  Exception e) {
                                Toast.makeText(MainActivity.this,"認證失敗",Toast.LENGTH_SHORT).show();
                                Log.d("Demo","認證Fail"+e.getMessage());
                            }
                        });
            }
        });
        binding.btnRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,registered_user.class);
                                    startActivity(it);
            }
        });
      /*  binding.image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picker=new Intent(Intent.ACTION_GET_CONTENT);
                picker.setType("image/*");
                startActivityForResult(picker,101);
            }
        });*/
       /* binding.GPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(MainActivity.this,GPS_test.class);
                startActivity(it);
            }
        });*/
    }

}