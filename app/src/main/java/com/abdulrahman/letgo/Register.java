package com.abdulrahman.letgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
    private static final String TAG = "Let Go";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase db;
    private DatabaseReference root;
    UserInfo userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);
        userInfo = new UserInfo();
    }
    public void register(View view) {
        EditText firstName = (EditText)findViewById(R.id.firstName);
        String firstNameString = firstName.getText().toString();
        EditText lastName = (EditText)findViewById(R.id.lastName);
        String lastNameString = lastName.getText().toString();
        EditText email = (EditText)findViewById(R.id.email);
        String emailString = email.getText().toString();
        EditText password = (EditText)findViewById(R.id.password);
        EditText passwordConfirmation = (EditText)findViewById(R.id.passwordConfirmation);
        System.out.println(password.getText().toString() + " %% " + passwordConfirmation.getText().toString());
        if(!password.getText().toString().equals(passwordConfirmation.getText().toString())) {
            Toast.makeText(this, "Password and Password Confirmation are not equal", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.getText().toString().length() < 6) {
            Toast.makeText(this, "Password should not be less than 6 characters", Toast.LENGTH_LONG);
            return;
        }
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userInfo.setUserFirstName(firstNameString + " " + lastNameString);
                            System.out.println(userInfo.getUserFirstName());
                            userInfo.setUserEmail(emailString);
                            userInfo.setUserUID(user.getUid().toString());
                            userInfo.setUserMoney(500);
                            sendToDB();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    //Change UI according to user data.
    public void updateUI(FirebaseUser account) {
        if(account != null) {
            Toast.makeText(this,"You Signed In successfully",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,MainActivity.class));

        } else {
            Toast.makeText(this,"You Didn't signed in",Toast.LENGTH_LONG).show();
        }
    }

    public void sendToDB() {
        db = FirebaseDatabase.getInstance("https://letgo-db69e-default-rtdb.firebaseio.com/");
        root = db.getReference("Users/" + userInfo.getUserUID());

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                root.setValue(userInfo);
                // after adding this data we are showing toast message.
                Toast.makeText(Register.this, "data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Register.this, "Error Happened, Try Again later", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void switchToLogin(View view) {
        startActivity(new Intent(this,Login.class));
    }
}