package com.example.the_strox.studentportal;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.the_strox.studentportal.models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForeignUserActivity extends AppCompatActivity {

    public static final String EXTRA_USER_NAME = "user_name";
    private TextView mUserView;
    private DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();

    private Button mMessageButton;
    private CircleImageView profile_photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_user);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profile_photo = (CircleImageView) findViewById(R.id.profile_photo);
        final String username= getIntent().getStringExtra(EXTRA_USER_NAME);
        getSupportActionBar().setTitle(username);
        final String[] mPhotoUrl = new String[1];
        mDatabase.child("users").orderByChild("username").equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mphotoUrl;
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    User user = childSnapshot.getValue(User.class);
                    mPhotoUrl[0] = user.getImgurl();}

                    if (mPhotoUrl != null) {
                        Picasso.with(ForeignUserActivity.this).load(mPhotoUrl[0]).into(profile_photo);
                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserView=(TextView)findViewById(R.id.author);

        mUserView.setText(username);
        mMessageButton=(Button) findViewById(R.id.button_message);
        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForeignUserActivity.this, NewMessageActivity.class);
                intent.putExtra(NewMessageActivity.EXTRA_USER_NAME, username);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent parentIntent = NavUtils.getParentActivityIntent(this);
                parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(parentIntent);
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
