package com.abdulrahman.letgo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static com.abdulrahman.letgo.R.id.gridview;
import static com.abdulrahman.letgo.R.id.userpic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            setContentView(R.layout.activity_user_profile);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView name = findViewById(R.id.name);
            TextView email = findViewById(R.id.email);
            setSupportActionBar(toolbar);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase[UserProfile]", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase[UserProfile]", "done");
                            name.setText(task.getResult().child("userFirstName").getValue().toString());
                            email.setText(task.getResult().child("userEmail").getValue().toString());
                        }
                    }
                });
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
            ImageView userImg = (ImageView) findViewById(R.id.userpic);
            GridView gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(this));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Toast.makeText(getApplicationContext(), "" + position,
                            Toast.LENGTH_SHORT).show();
                }
            });

            Picasso.with(getApplicationContext())
                    .load("https://firebasestorage.googleapis.com/v0/b/letgo-db69e.appspot.com/o/DefaultAvatar.png?alt=media&token=158f0d26-f324-48f3-acae-5d998505b14a")
                    .transform(new RoundedTransformation(100, 10))
                    .fit()
                    .centerCrop().into(userImg);
        } else {
            startActivity(new Intent(this, Login.class));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class RoundedTransformation implements com.squareup.picasso.Transformation {
        private final int radius;
        private final int margin;  // dp

        // radius is corner radii in dp
        // margin is the board in dp
        public RoundedTransformation(final int radius, final int margin) {
            this.radius = radius;
            this.margin = margin;
        }

        @Override
        public Bitmap transform(final Bitmap source) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            StorageReference storage = FirebaseStorage.getInstance().getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

            if (source != output) {
                source.recycle();
            }

            return output;
        }

        @Override
        public String key() {
            return "rounded";
        }
    }
}
