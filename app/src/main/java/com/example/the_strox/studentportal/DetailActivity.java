package com.example.the_strox.studentportal;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.the_strox.studentportal.models.Club;
import com.example.the_strox.studentportal.models.Comment;
import com.example.the_strox.studentportal.models.Post;
import com.example.the_strox.studentportal.models.User;
import com.example.the_strox.studentportal.viewholder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends BaseActivity implements View.OnClickListener{

    public static final String EXTRA_POST_KEY = "post_key";
    public static final String EXTRA_CLUB_KEY = "club_key";
    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private DatabaseReference mDatabase;

    private ValueEventListener mPostListener;
    private String mPostKey;
    private String mClubKey;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mCommentsRecycler;
    private TextView mAuthorView;
    private TextView mTitleView;
    private TextView mBodyView;
    private TextView mDateView;
    private CircleImageView mAuthorImageView;
    private EditText mReplyField;

    private ImageButton mFullButton;
    private ImageButton mSendButton;
    private FirebaseRecyclerAdapter<Comment, CommentViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        mClubKey = getIntent().getStringExtra(EXTRA_CLUB_KEY);
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("club-post").child(mClubKey).child(mPostKey);
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("club-post-comments").child(mClubKey).child(mPostKey);

        mAuthorView = (TextView) findViewById(R.id.post_author);
        mAuthorImageView = (CircleImageView) findViewById(R.id.post_author_photo) ;

        mTitleView = (TextView) findViewById(R.id.post_title);
        mTitleView.setTextSize(20);

        mBodyView = (TextView) findViewById(R.id.post_body);
        mBodyView.setEllipsize(null);
        mBodyView.setTextSize(16);
        mBodyView.setMaxLines(Integer.MAX_VALUE);
        mBodyView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        mBodyView.setTextColor(Color.parseColor("#000000"));

        mDateView =(TextView)  findViewById(R.id.post_time);

        mReplyField = (EditText) findViewById(R.id.field_reply);
        mSendButton=(ImageButton) findViewById(R.id.send);
        mFullButton = (ImageButton) findViewById(R.id.up);

        mSwipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mCommentsRecycler = (RecyclerView) findViewById(R.id.recycler_comments);
        mCommentsRecycler.setNestedScrollingEnabled(false);
        mSendButton.setOnClickListener(this);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                postComment();
                break;
        }
    }

    public void onStart() {
        super.onStart();
        showProgressDialog();
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                mAuthorView.setText(post.author);
                mTitleView.setText(post.title);
                mBodyView.setText(post.body);
                mDateView.setText(post.date);
                mDatabase.child("users").child(post.uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user= dataSnapshot.getValue(User.class);
                        String imgurl=user.getImgurl();
                        if(imgurl!=null)
                            Picasso.with(DetailActivity.this).load(imgurl).into(mAuthorImageView);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                getSupportActionBar().setTitle(post.title);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(
                Comment.class,
                R.layout.item_comment,
                CommentViewHolder.class,
                mCommentsReference
                ) {
            @Override
            protected void populateViewHolder(final CommentViewHolder viewHolder, final Comment model, int position) {
                viewHolder.authorView.setText(model.getAuthor());
                viewHolder.bodyView.setText(model.getText());
                mDatabase.child("users").child(model.uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user= dataSnapshot.getValue(User.class);
                        String imgurl=user.getImgurl();
                        if(imgurl!=null)
                            Picasso.with(DetailActivity.this).load(imgurl).into(viewHolder.avatarView);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.avatarView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String[] uid = new String[1];

                        mDatabase.child("users").orderByChild("username").equalTo(model.getAuthor()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                    uid[0] = childSnapshot.getKey();
                                }

                                if (Objects.equals(uid[0], FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    // Not signed in, launch the Sign In activity
                                   // Toast.makeText(ForeignUserActivity.this,"Inside if", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DetailActivity.this, UserActivity.class));
                                    finish();
                                    return;
                                }
                                else {
                                    Intent intent = new Intent(DetailActivity.this, ForeignUserActivity.class);
                                    intent.putExtra(ForeignUserActivity.EXTRA_USER_NAME, model.getAuthor());
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
            }
        };
        mCommentsRecycler.setAdapter(mAdapter);

        hideProgressDialog();

    }

    @Override
    public void onStop() {
        super.onStop();

        mAdapter.cleanup();
    }
    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.username;
                        String commentText = mReplyField.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        if(!commentText.isEmpty())
                            mCommentsReference.push().setValue(comment);
                        mReplyField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;
        public CircleImageView avatarView;


        public CommentViewHolder(View itemView) {
            super(itemView);

            authorView = (TextView) itemView.findViewById(R.id.comment_author);
            bodyView = (TextView) itemView.findViewById(R.id.comment_body);
            avatarView=(CircleImageView) itemView.findViewById(R.id.comment_photo);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshContent(){
       this.onStart();
        mSwipeRefreshLayout.setRefreshing(false);
    }

}
