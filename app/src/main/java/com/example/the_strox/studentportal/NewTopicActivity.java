package com.example.the_strox.studentportal;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import com.example.the_strox.studentportal.models.Club;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.the_strox.studentportal.models.User;
import com.example.the_strox.studentportal.models.Post;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewTopicActivity extends BaseActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";
    public static final String EXTRA_CLUB_KEY = "club_key";
    private String mClubKey;
    private DatabaseReference mDatabase;
    private AutoCompleteTextView autoCompleteTextView;
    private EditText mTitleField;
    private EditText mBodyField;
    private CheckBox mCheckBox;
    ArrayAdapter<String> adapter;
    List<String> clubnameList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic2);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Topic");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mClubKey=getIntent().getStringExtra(EXTRA_CLUB_KEY);
        mTitleField = (EditText) findViewById(R.id.field_title);
        mBodyField = (EditText) findViewById(R.id.field_body);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocompleteView);
        mCheckBox = (CheckBox) findViewById(R.id.check);

        if (mClubKey!=null){
            mDatabase.child("clubs").child(mClubKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Club club= dataSnapshot.getValue(Club.class);
                    String club_name=club.getName();
                    if (club_name.equals("Blog")){
                        mCheckBox.setVisibility(View.GONE);
                    }
                    autoCompleteTextView.setText(club_name);
                    autoCompleteTextView.setEnabled(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }else{
            mDatabase.child("clubs").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        Club club =postSnapshot.getValue(Club.class);
                        clubnameList.add(club.getName());
                    }

                    adapter = new ArrayAdapter<>(NewTopicActivity.this, android.R.layout.simple_dropdown_item_1line, clubnameList);
                    autoCompleteTextView.setAdapter(adapter);
                    autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(hasFocus)
                                autoCompleteTextView.showDropDown();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(NewTopicActivity.this, "Failed to load post.", Toast.LENGTH_SHORT).show();
                }
            });
        }




    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                submitPost();
                break;
            case android.R.id.home:
                Intent parentIntent = NavUtils.getParentActivityIntent(this);
                parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(parentIntent);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_topic, menu);
        return true;
    }
    private void submitPost() {

        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();
        final String to = autoCompleteTextView.getText().toString();
        final boolean check_global = mCheckBox.isChecked();

        if (!clubnameList.contains(to) && autoCompleteTextView.isEnabled() ){
            Toast.makeText(NewTopicActivity.this,
                    "Invalid Group Name.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewTopicActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewPost(userId, user.username,to, title, body,check_global,user.imgurl);
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
    private void writeNewPost(final String userId,final String username,final String to,final String title,final String body,final boolean check_global,final String imgurl) {
        final String[] clubkey = new String[1];
        mDatabase.child("clubs").orderByChild("name").equalTo(to).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    clubkey[0] = childSnapshot.getKey();}
                String postkey = mDatabase.child("club-post").child(clubkey[0]).push().getKey();
                Post post = new Post(userId, username, title, body, DateFormat.getDateTimeInstance().format(new Date()));
                Map<String, Object> postValues = post.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/club-post/" + clubkey[0] +"/"+ postkey, postValues);
                childUpdates.put("/user-posts/" + userId + "/" + postkey, postValues);
                if(check_global){
                   childUpdates.put("/portal-posts/"+postkey,postValues);
                }
                mDatabase.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
