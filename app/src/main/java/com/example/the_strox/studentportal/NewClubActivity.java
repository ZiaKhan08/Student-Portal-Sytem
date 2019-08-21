package com.example.the_strox.studentportal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.HashMap;
import java.util.Map;

public class NewClubActivity extends BaseActivity {

    private static final String TAG = "NewClubActivity";
    private static final String REQUIRED = "Required";
    private DatabaseReference mDatabase;
    private EditText mNameField;
    private EditText mDepartmentField;
    private EditText mDescriptionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_club1);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Club");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mNameField = (EditText) findViewById(R.id.field_name);
        mDepartmentField = (EditText) findViewById(R.id.field_dep);
        mDescriptionField = (EditText) findViewById(R.id.field_desc);

    }
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
        getMenuInflater().inflate(R.menu.menu_new_topic, menu);
        return true;
    }
    private void submitPost() {
        final String name = mNameField.getText().toString();
        final String dept = mDepartmentField.getText().toString();
        final String desc = mDescriptionField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mNameField.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(dept)) {
            mDepartmentField.setError(REQUIRED);
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
                            Toast.makeText(NewClubActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewPost(name, dept, desc);
                        }
                        finish();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
    private void writeNewPost(String name,String dept,String desc) {
        String key = mDatabase.child("clubs").push().getKey();
        Club club = new Club(name, dept,desc);
        mDatabase.child("clubs").child(key).setValue(club);
    }
}
