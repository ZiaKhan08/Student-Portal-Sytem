package com.example.the_strox.studentportal;

import android.*;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.the_strox.studentportal.models.DownloadFiles;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import java.io.File;
import java.util.List;


public class DownloadActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks{

    public static class DownloadViewHolder extends RecyclerView.ViewHolder {
        public TextView fileNameTextView;
        public TextView fileSizeTextView;
        public ImageView downloadImageView;




        public DownloadViewHolder(View itemView) {
            super(itemView);
            fileNameTextView = (TextView) itemView.findViewById(R.id.field_filename);
            fileSizeTextView =(TextView) itemView.findViewById(R.id.field_filesize);
            downloadImageView = (ImageView) itemView.findViewById(R.id.download_image);

        }
    }

    private static final int RC_STORAGE_PERMS = 102;

    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
           android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };




    public static final String EXTRA_SUBJECT_CODE_CATEGORY = "subject_code_category";
    private String msubject_code_category;
    private static final int mId = 50;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<DownloadFiles, DownloadViewHolder> mAdapter;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://student-portal-4226e.appspot.com");

    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private TextView emptyView;
    private CircularProgressView progressView;
    private SwipeRefreshLayout mSwipeRefreshLayout;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Download Files");
        msubject_code_category=getIntent().getStringExtra(EXTRA_SUBJECT_CODE_CATEGORY);
        Toolbar toolbar =
                (Toolbar) findViewById(R.id.toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        mSwipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mRecycler = (RecyclerView) findViewById(R.id.recycler_view_dcategory_list);
        emptyView= (TextView)findViewById(R.id.empty_view) ;
        mRecycler.setHasFixedSize(true);
        progressView =(CircularProgressView)findViewById(R.id.progress_view);
        progressView.startAnimation();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        refreshContent();

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

    private void refreshContent(){
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        mDatabase.child("files").orderByChild("subjectcode_type").equalTo(msubject_code_category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()==null){
                    emptyView.setVisibility(View.VISIBLE);
                    progressView.stopAnimation();
                    progressView.setVisibility(View.GONE);
                }
                else {
                    mAdapter = new FirebaseRecyclerAdapter<DownloadFiles, DownloadViewHolder>(
                            DownloadFiles.class,
                            R.layout.item_download_list,
                            DownloadViewHolder.class,
                            mDatabase.child("files").orderByChild("subjectcode_type").equalTo(msubject_code_category)) {

                        @Override
                        protected void populateViewHolder(final DownloadViewHolder viewHolder, final DownloadFiles model, int position) {
                            String size=String.format("%.2f",model.getSize()/1024 );
                            viewHolder.fileNameTextView.setText(model.getName());
                            viewHolder.fileSizeTextView.setText("Size: "+size+" MB");


                            viewHolder.downloadImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                @AfterPermissionGranted(RC_STORAGE_PERMS)
                                public void onClick(View v) {
                                    viewHolder.downloadImageView.setEnabled(false);
                                    String perm = android.Manifest.permission.READ_EXTERNAL_STORAGE;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                            && !EasyPermissions.hasPermissions(DownloadActivity.this, perm)) {
                                        EasyPermissions.requestPermissions(DownloadActivity.this, "Need access to external storage for downloading",
                                                RC_STORAGE_PERMS, perm);
                                        return;
                                    }
                                    StorageReference islandRef = storageRef.child(model.getName());
                                    File localFile = null;
                                    try {
                                        localFile = new File(Environment.getExternalStorageDirectory(), model.getName());
                                        Toast.makeText(getApplicationContext(), "Downloading", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    islandRef.getFile(localFile).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            double fprogress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                            String progress = String.format("%.2f", fprogress);
                                           int bytes = (int)taskSnapshot.getBytesTransferred();
                                           int constant = 1000;
                                            if(bytes%constant == 0) {
                                                NotificationCompat.Builder mBuilder =
                                                        (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                                                                .setSmallIcon(android.R.drawable.stat_sys_download)
                                                                .setContentTitle("Downloading " + model.getName())
                                                                .setContentText(" " + progress + "% completed");

                                                NotificationManager mNotificationManager =
                                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                                mNotificationManager.notify(mId, mBuilder.build());

                                            }
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            NotificationCompat.Builder mBuilder =
                                                    (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                                                            .setSmallIcon(android.R.drawable.stat_sys_download_done)
                                                            .setContentTitle("Downloading " + model.getName())
                                                            .setContentText("Download completed" );

                                            NotificationManager mNotificationManager =
                                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                            mNotificationManager.notify(mId, mBuilder.build());
                                            viewHolder.downloadImageView.setEnabled(true);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });
                        }
                    };
                    //}
                    mRecycler.setAdapter(mAdapter);
                    emptyView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    progressView.stopAnimation();
                    progressView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "DATABASE ERROR", Toast.LENGTH_SHORT).show();
            }
        });
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {}

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {}
}
