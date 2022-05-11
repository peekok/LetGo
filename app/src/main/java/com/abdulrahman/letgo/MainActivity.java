package com.abdulrahman.letgo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulrahman.letgo.data.model.Photo;
import com.abdulrahman.letgo.ui.ItemClickSupport;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.abdulrahman.letgo.data.UnsplashService;
import com.abdulrahman.letgo.ui.ForegroundImageView;


import java.util.List;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PHOTO_COUNT = 12;

    @Bind(R.id.image_grid)
    RecyclerView grid;
    @Bind(android.R.id.empty)
    ProgressBar empty;
    @BindInt(R.integer.photo_grid_columns) int columns = 3;
    @BindDimen(R.dimen.grid_item_spacing) int gridSpacing;
    private PhotoAdapter adapter;
    private String photoUrlBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        grid = (RecyclerView) findViewById(R.id.image_grid);
        empty = (ProgressBar) findViewById(R.id.empty);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Upload will be available in next release", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ButterKnife.bind(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columns);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                /* emulating https://material-design.storage.googleapis.com/publish/material_v_4/material_ext_publish/0B6Okdz75tqQsck9lUkgxNVZza1U/style_imagery_integration_scale1.png */
                switch (position % 6) {
                    case 0:
                        return 1;
                    case 1:
                        return 1;
                    case 2:
                        return 1;
                    case 4:
                        return 1;
                    case 3:
                        return 2;
                    default:
                        return 3;
                }
            }
        });
        grid.setLayoutManager(gridLayoutManager);
        grid.addItemDecoration(new GridMarginDecoration(gridSpacing));
        grid.setHasFixedSize(true);

        photoUrlBase = "https://picsum.photos/"
                + getResources().getDisplayMetrics().widthPixels
                + "/";

        ItemClickSupport.addTo(grid).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                Photo photo = adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(photoUrlBase + photo.id));
                intent.putExtra(DetailActivity.EXTRA_AUTHOR, photo.author);
                MainActivity.this.startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, view,
                                view.getTransitionName()).toBundle());
            }
        });

        UnsplashService unsplashApi = new RestAdapter.Builder()
                .setEndpoint(UnsplashService.ENDPOINT)
                .build()
                .create(UnsplashService.class);

        unsplashApi.getFeed(new Callback<List<Photo>>() {
            @Override
            public void success(List<Photo> photos, Response response) {
                //grid.setAdapter(new PhotoAdapter(photos));
                // the first items are really boring, get the last <n>
                adapter = new PhotoAdapter(photos.subList(photos.size() - PHOTO_COUNT, photos
                        .size()));
                grid.setAdapter(adapter);
                empty.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(getClass().getCanonicalName(), "Error retrieving Unsplash feed:", error);
            }
        });
    }

    /* protected */ static class PhotoViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.photo) ForegroundImageView imageView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ButterKnife.bind(this, imageView);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);

//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));

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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.view_profile) {
            Intent intent = new Intent(this,UserProfile.class);
            startActivity(intent);
        }
        if (id == R.id.register) {
            Intent intent = new Intent(this,Register.class);
            startActivity(intent);
        }

        if (id == R.id.topUp) {
            Intent intent = new Intent(this, TopUpPage.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GridMarginDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public GridMarginDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.top = space;
            outRect.right = space;
            outRect.bottom = space;
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

        private final List<Photo> photos;

        public PhotoAdapter(List<Photo> photos) {
            this.photos = photos;
        }

        @NonNull
        @Override
        public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PhotoViewHolder(
                    LayoutInflater.from(MainActivity.this)
                            .inflate(R.layout.photo_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
            final Photo photo = photos.get(position);
            String url = photoUrlBase + photo.id;
            Picasso.with(MainActivity.this)
                    .load(url)
                    .placeholder(R.color.placeholder)
                    .into((ImageView) holder.itemView);
        }

        @Override
        public int getItemCount() {
            return photos.size();
        }

        @Override
        public long getItemId(int position) {
            return photos.get(position).id;
        }

        public Photo getItem(int position) {
            return photos.get(position);
        }
    }
}
