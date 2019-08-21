package com.example.the_strox.studentportal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.the_strox.studentportal.models.Club;
import com.example.the_strox.studentportal.models.Post;
import com.example.the_strox.studentportal.models.User;
import com.example.the_strox.studentportal.viewholder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public abstract class PostListActivity extends BaseActivity {

    private static final String TAG = "PostListActivity";
    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private TextView emptyView;
    private FloatingActionButton fabpost;
    private CircularProgressView progressView;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    public PostListActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_post);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query namequery= getNameQuery(mDatabase);
        namequery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Club club=dataSnapshot.getValue(Club.class);
                getSupportActionBar().setTitle(club.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //getSupportActionBar().setTitle(name);
        mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        fabpost= (FloatingActionButton) findViewById(R.id.fab_post);
        mRecycler = (RecyclerView)findViewById(R.id.recycler_view_list);
        emptyView= (TextView) findViewById(R.id.empty_view) ;
        progressView =(CircularProgressView) findViewById(R.id.progress_view);
        progressView.startAnimation();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        refreshContent();
    }


    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }




    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void refreshContent(){
        final Query postsQuery = getQuery(mDatabase);
        fabpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostListActivity.this, NewTopicActivity.class);
                intent.putExtra(NewTopicActivity.EXTRA_CLUB_KEY, postsQuery.getRef().getKey());
                startActivity(intent);
            }
        });
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        postsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    emptyView.setVisibility(View.VISIBLE);
                    progressView.stopAnimation();
                    progressView.setVisibility(View.GONE);
                }
                else{
                    mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.item_post,
                            PostViewHolder.class, postsQuery) {
                        @Override
                        protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
                            final DatabaseReference postRef = getRef(position);

                            final String postKey = postRef.getKey();
                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Launch PostDetailActivity
                                    Intent intent = new Intent(PostListActivity.this, DetailActivity.class);
                                    intent.putExtra(DetailActivity.EXTRA_POST_KEY, postKey);
                                    intent.putExtra(DetailActivity.EXTRA_CLUB_KEY, postsQuery.getRef().getKey());
                                    startActivity(intent);
                                }
                            });

                            if (model.stars.containsKey(getUid())) {
                                viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                            } else {
                                viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                            }

                           mDatabase.child("users").child(model.uid).addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   User user= dataSnapshot.getValue(User.class);
                                      String imgurl=user.getImgurl();
                                   viewHolder.bindToPost(imgurl,getApplicationContext(),model, new View.OnClickListener() {
                                       @Override
                                       public void onClick(View starView) {
                                           DatabaseReference globalPostRef = postsQuery.getRef().child(postRef.getKey());
                                           DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());
                                           DatabaseReference portalPostRef =mDatabase.child("portal-posts").child(postRef.getKey());

                                           onStarClicked(globalPostRef);
                                           onStarClicked(userPostRef);
                                           onStarClicked(portalPostRef);
                                       }
                                   });
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {

                               }
                           });
                        }
                    };
                    emptyView.setVisibility(View.GONE);
                    mRecycler.setAdapter(mAdapter);
                    progressView.stopAnimation();
                    progressView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mSwipeRefreshLayout.setRefreshing(false);
    }
    public abstract Query getQuery(DatabaseReference databaseReference);
    public abstract Query getNameQuery(DatabaseReference databaseReference);



}
