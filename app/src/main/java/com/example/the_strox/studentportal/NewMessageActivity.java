package com.example.the_strox.studentportal;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.the_strox.studentportal.models.Message;
import com.example.the_strox.studentportal.models.Post;
import com.example.the_strox.studentportal.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewMessageActivity extends BaseActivity {

    private static final String TAG = "NewMessagetActivity";
    private static final String REQUIRED = "Required";
    public static final String EXTRA_USER_NAME = "user_name";

    private DatabaseReference mDatabase;
    private String mReceiverUid;

    private EditText mToField;
    private EditText mSubejctField;
    private EditText mMessageField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message1);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Message");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mToField = (EditText) findViewById(R.id.field_to);
        mSubejctField = (EditText) findViewById(R.id.field_subject);
        mMessageField = (EditText) findViewById(R.id.field_message);

        String username= getIntent().getStringExtra(EXTRA_USER_NAME);
        if(username!=null){
            mToField.setText(username);
            mToField.setEnabled(false);
        }
        mReceiverUid =mToField.getText().toString();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_topic, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                submitMessage();
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

    private void submitMessage() {
        final String to = mToField.getText().toString();
        final String subject = mSubejctField.getText().toString();
        final String message = mMessageField.getText().toString();
        final String time = DateFormat.getDateTimeInstance().format(new Date());

        if (TextUtils.isEmpty(to)) {
            mToField.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(message)) {
            mMessageField.setError(REQUIRED);
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
                            Toast.makeText(NewMessageActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewMessage(user.username, to, subject, message, time);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
    private void writeNewMessage(final String username,final String to,final String subject,final String message,final String time) {

        mDatabase.child("users").orderByChild("username").equalTo(to).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()==null){
                    Toast.makeText(NewMessageActivity.this,"User Not Found. Please check the username",Toast.LENGTH_SHORT).show();

                }
                else{
                    Message message1= new Message();
                    message1.setSender(username);
                    message1.setReceiver(to);
                    message1.setSubject(subject);
                    message1.setMessage(message);
                    message1.setTime(time);
                    mDatabase.child("messages").push().setValue(message1);
                    Toast.makeText(NewMessageActivity.this,"Message Sucessfully sent!",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
