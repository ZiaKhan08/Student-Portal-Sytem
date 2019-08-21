package com.example.the_strox.studentportal;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;


import com.example.the_strox.studentportal.models.DownloadFiles;
import com.example.the_strox.studentportal.models.DownloadList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = "UploadActivity";

    private DatabaseReference mDatabase;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://student-portal-4226e.appspot.com");
    private static final int RESULT_LOAD_IMAGE= 1;
    private static final int mId = 50;
    private String inputFile;
    private boolean test;

    private AutoCompleteTextView autoCompleteTextViewSubject;
    private AutoCompleteTextView autoCompleteTextViewCategory;
    private Button buttonUpload;
    List<String> subjectnameList = new ArrayList<String>();
    List<String> categorynameList = new ArrayList<String>();
    private DownloadList downloadList=new DownloadList();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload1);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upload Files");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        autoCompleteTextViewSubject = (AutoCompleteTextView) findViewById(R.id.autocompleteView_subject);
        autoCompleteTextViewCategory = (AutoCompleteTextView) findViewById(R.id.autocompleteView_category);
        buttonUpload = (Button) findViewById(R.id.button_upload) ;


        subjectnameList=  downloadList.getSubjectnameList();
        adapter = new ArrayAdapter<>(UploadActivity.this, android.R.layout.simple_dropdown_item_1line, subjectnameList);
        autoCompleteTextViewSubject.setAdapter(adapter);
        autoCompleteTextViewSubject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    autoCompleteTextViewSubject.showDropDown();
            }
        });


        categorynameList = downloadList.getCategorynameList();
        adapter = new ArrayAdapter<>(UploadActivity.this, android.R.layout.simple_dropdown_item_1line, categorynameList);
        autoCompleteTextViewCategory.setAdapter(adapter);
        autoCompleteTextViewCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    autoCompleteTextViewCategory.showDropDown();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String subject = autoCompleteTextViewSubject.getText().toString();
                final String category = autoCompleteTextViewCategory.getText().toString();
                test = true;

                if (!subjectnameList.contains(subject)) {
                    autoCompleteTextViewSubject.setError("Invalid");
                    Toast.makeText(UploadActivity.this,
                            "Invalid Subject Code.",
                            Toast.LENGTH_SHORT).show();
                    test = false;
                }

                if (!categorynameList.contains(category)) {
                    autoCompleteTextViewCategory.setError("Invalid");
                    Toast.makeText(UploadActivity.this,
                            "Invalid Category.",
                            Toast.LENGTH_SHORT).show();
                    test = false;
                }

                if(test){

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
            }
        });


    }

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
                Toast.makeText(getApplicationContext(), "Uploading File", Toast.LENGTH_SHORT).show();
                final String subject_code=autoCompleteTextViewSubject.getText().toString();
                final String category = autoCompleteTextViewCategory.getText().toString();

                Uri returnUri = data.getData();
                Cursor returnCursor =
                        getContentResolver().query(returnUri, null, null, null, null);

                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                final String fileName = returnCursor.getString(nameIndex);

                final StorageReference uploadRef = storageRef.child(fileName);
                final float[] size = new float[1];
                UploadTask uploadTask = uploadRef.putFile(returnUri);

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i("url", String.valueOf(taskSnapshot.getDownloadUrl()));
                        double fprogress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        String progress = String.format("%.2f", fprogress);
                        NotificationCompat.Builder mBuilder =
                                (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(android.R.drawable.stat_sys_upload)
                                        .setContentTitle("Uploading " + fileName)
                                        .setContentText(" " + progress + "% completed");

                        NotificationManager mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        mNotificationManager.notify(mId, mBuilder.build());
                        System.out.println("Upload is " + progress + "% done");
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Upload is paused");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        NotificationCompat.Builder mBuilder =
                                (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                                        .setContentTitle("Uploading " +  fileName)
                                        .setContentText("Upload complete");

                        NotificationManager mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(mId, mBuilder.build());
                        size[0] = taskSnapshot.getMetadata().getSizeBytes()/1024;
                        String key = mDatabase.child("files").push().getKey();
                        DownloadFiles downloadFiles = new DownloadFiles(fileName,subject_code,category,size[0]);
                        mDatabase.child("files").child(key).setValue(downloadFiles);

                    }
                });

            }
        }

}
