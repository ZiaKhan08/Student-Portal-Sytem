package com.example.the_strox.studentportal;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.the_strox.studentportal.models.Club;
import com.example.the_strox.studentportal.models.User;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StreamDownloadTask;

import java.util.HashMap;
import java.util.Map;

public  class ClubPostActivity extends PostListActivity {

    private DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();
    private Menu menu;
    final String uid = getUid();
    public static final String EXTRA_CLUB_KEY = "club_key";
    private String mClubKey;
    private int test;
    private Map<String, Boolean> clubs = new HashMap<>();
    private String name;
    private String dept;
    private String desc;
    private String clubName;


    public ClubPostActivity(){}
    @Override
    public Query getQuery(DatabaseReference databaseReference){
        mClubKey=getIntent().getStringExtra(EXTRA_CLUB_KEY);
        Query recentPostsQuery = databaseReference.child("club-post").child(mClubKey);

        return recentPostsQuery;
    }

    @Override
    public Query getNameQuery(DatabaseReference databaseReference){
        mClubKey=getIntent().getStringExtra(EXTRA_CLUB_KEY);
        Query recentPostsQuery = databaseReference.child("clubs").child(mClubKey);

        return recentPostsQuery;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu=menu;
        getMenuInflater().inflate(R.menu.menu_club, menu);

        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu){
        super.onPrepareOptionsMenu(menu);

        //mClubKey =getIntent().getStringExtra(EXTRA_CLUB_KEY);


           /* mDatabase.child("users").child(uid).child("clubs").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // User user = dataSnapshot.getValue(User.class);
                    // clubs= user.clubs;
                    if (dataSnapshot.getValue() != null) {
                        Map<String, Boolean> map = (Map<String, Boolean>) dataSnapshot.getValue();
                        clubs = map;
                        //test= clubs.size();
                        // boolean bol=clubs.get("-KLpcURDV68BcbAvlPFy");
                        // String test =user.email;
                        // Toast.makeText(ClubPostActivity.this,String.valueOf(bol),
                        //   Toast.LENGTH_SHORT).show();
                        //Toast.makeText(ClubPostActivity.this,String.valueOf(test),
                        // Toast.LENGTH_SHORT).show();
                        if (clubs.containsKey(mClubKey))
                            menu.findItem(R.id.action_subscribed).setVisible(true);
                        else
                            menu.findItem(R.id.action_subscribe).setVisible(true);


                    }
                    else
                        menu.findItem(R.id.action_subscribe).setVisible(true);

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ClubPostActivity.this, "Failed to load post.",
                            Toast.LENGTH_SHORT).show();

                }

            });*/


        mDatabase.child("user-club").child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // User user = dataSnapshot.getValue(User.class);
                // clubs= user.clubs;
                if (dataSnapshot.getValue() != null) {
                  //  Map<String, Boolean> map = (Map<String, Boolean>) dataSnapshot.getValue();
                   // clubs = map;
                    if(dataSnapshot.hasChild(mClubKey))
                        menu.findItem(R.id.action_subscribed).setVisible(true);
                    else
                        menu.findItem(R.id.action_subscribe).setVisible(true);

                    //test= clubs.size();
                    // boolean bol=clubs.get("-KLpcURDV68BcbAvlPFy");
                    // String test =user.email;
                    // Toast.makeText(ClubPostActivity.this,String.valueOf(bol),
                    //   Toast.LENGTH_SHORT).show();
                    //Toast.makeText(ClubPostActivity.this,String.valueOf(test),
                    // Toast.LENGTH_SHORT).show();
                   // if (clubs.containsKey(uid))
                }
                else
                    menu.findItem(R.id.action_subscribe).setVisible(true);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ClubPostActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();

            }

        });
       /*if(mDatabase.child("Users").child(uid).orderByChild("clubs").equalTo(EXTRA_CLUB_KEY)) {
            menu.findItem(R.id.action_subscribe).setVisible(true);
        }
        else{
            menu.findItem(R.id.action_subscribed).setVisible(true);
        }*/

        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MenuItem xitem= menu.findItem(R.id.action_subscribe);
        final MenuItem yitem= menu.findItem(R.id.action_subscribed);
        //mDatabase



        switch (item.getItemId()) {
            case android.R.id.home:
                Intent parentIntent = NavUtils.getParentActivityIntent(this);
                parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(parentIntent);
                finish();
                return true;

            case R.id.action_subscribe:
                mDatabase.child("clubs").child(mClubKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Club club = dataSnapshot.getValue(Club.class);
                        name=club.getName();

                        dept=club.getDept();
                        desc=club.getDesc();
                        Club clubb=new Club(name,dept,desc);
                        Map<String, Object> clubbValues = clubb.toMap();
                        mDatabase.child("user-club").child(uid).child(mClubKey).setValue(clubbValues);
                        xitem.setVisible(false);
                        yitem.setVisible(true);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ClubPostActivity.this, "Failed to load post.", Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case R.id.action_subscribed:
               // Toast.makeText(ClubPostActivity.this, "CLICKED SUBSCRIBED", Toast.LENGTH_SHORT).show();
                mDatabase.child("user-club").child(uid).child(mClubKey).removeValue();
              // mDatabase.child("clubs").child(mClubKey).child("users").child(uid).removeValue();
                yitem.setVisible(false);
                xitem.setVisible(true);
                break;

            case R.id.action_info:

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
