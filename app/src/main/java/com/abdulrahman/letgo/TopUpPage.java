package com.abdulrahman.letgo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class TopUpPage extends AppCompatActivity {
    public UserInfo userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_page);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        userInfo = new UserInfo();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = mAuth.getCurrentUser().getUid().toString();
            database.getReference("Users/" + uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        userInfo.setUserMoney(Integer.parseInt(task.getResult().child("userMoney").getValue().toString()));
                        Log.d("firebase", "Data Imported");
                        System.out.println(userInfo.getUserMoney());
                    }
                }
            });
        }
    }
    public void topUpOne(View view) {
        DatabaseReference ref =  FirebaseDatabase.getInstance()
                .getReference(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userMoney");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                int newMoney = userInfo.getUserMoney() + 500;
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("userMoney").setValue(newMoney);
                Toast.makeText(TopUpPage.this, "Thanks for your purchase", Toast.LENGTH_SHORT).show();
                sendBack(view);
            }
        });
    }
    public void topUpTwo(View view) {
        DatabaseReference ref =  FirebaseDatabase.getInstance()
                .getReference(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userMoney");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                int newMoney = userInfo.getUserMoney() + 1500;
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("userMoney").setValue(newMoney);
                Toast.makeText(TopUpPage.this, "Thanks for your purchase", Toast.LENGTH_SHORT).show();
                sendBack(view);
            }
        });
    }
    public void topUpThree(View view) {
        DatabaseReference ref =  FirebaseDatabase.getInstance()
                .getReference(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userMoney");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                int newMoney = userInfo.getUserMoney() + 3000;
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("userMoney").setValue(newMoney);
                Toast.makeText(TopUpPage.this, "Thanks for your purchase", Toast.LENGTH_SHORT).show();
                sendBack(view);
            }
        });
    }
    public void sendBack(View view) {
        finish();
    }
}