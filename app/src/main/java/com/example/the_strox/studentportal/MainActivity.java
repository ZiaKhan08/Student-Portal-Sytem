package com.example.the_strox.studentportal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.the_strox.studentportal.fragment.ClubTabFragment;
import com.example.the_strox.studentportal.fragment.HomeTabFragment;
import com.example.the_strox.studentportal.fragment.MessageTabFragment;
import com.example.the_strox.studentportal.fragment.ResourcesTabFragment;
import com.example.the_strox.studentportal.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener{

    private DrawerLayout mDrawerLayout;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    private static final String TAG = "MainActivity";
    private String mUsername;
    private String mPhotoUrl;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabase;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        View header = navigationView.getHeaderView(0);
       final TextView username =(TextView) header.findViewById(R.id.field_username) ;
       final CircleImageView userphoto=(CircleImageView) header.findViewById(R.id.profile_photo);

        if (mFirebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            String uid = mFirebaseAuth.getCurrentUser().getUid();
            mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    mUsername=user.getUsername();
                    mPhotoUrl=user.getImgurl();
                    username.setText(mUsername);
                    if (mPhotoUrl!=null) {
                        Picasso.with(MainActivity.this).load(mPhotoUrl).into(userphoto);
                    }
                    else
                        Toast.makeText(MainActivity.this, "NULL PHOTO", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        userphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_container,new HomeTabFragment()).commit();
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.navItem1) {
                            FragmentTransaction fragmentTransaction =getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container,new HomeTabFragment()).commit();


                        }

                        else if (menuItem.getItemId() == R.id.navItem2) {
                          FragmentTransaction xfragmentTransaction = getSupportFragmentManager().beginTransaction();
                           xfragmentTransaction.replace(R.id.fragment_container,new ClubTabFragment()).commit();

                        }

                        else if (menuItem.getItemId() == R.id.navItem3) {
                           FragmentTransaction yfragmentTransaction = getSupportFragmentManager().beginTransaction();
                           yfragmentTransaction.replace(R.id.fragment_container,new ResourcesTabFragment()).commit();

                        }

                        else if (menuItem.getItemId() == R.id.navItem4) {
                            FragmentTransaction zfragmentTransaction = getSupportFragmentManager().beginTransaction();
                            zfragmentTransaction.replace(R.id.fragment_container,new MessageTabFragment()).commit();

                        }

                        else if (menuItem.getItemId() == R.id.navItem5) {
                            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intent);
                        }

                        else if (menuItem.getItemId() == R.id.navItem6) {
                            mFirebaseAuth.signOut();
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                            return true;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
         if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
