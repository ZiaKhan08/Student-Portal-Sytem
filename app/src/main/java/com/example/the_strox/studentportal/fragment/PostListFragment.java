package com.example.the_strox.studentportal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.the_strox.studentportal.DetailActivity;

import com.example.the_strox.studentportal.models.Post;
import com.example.the_strox.studentportal.models.User;


import com.example.the_strox.studentportal.viewholder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.github.clans.fab.FloatingActionButton;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.example.the_strox.studentportal.R;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public abstract class PostListFragment extends Fragment {


    private static final String TAG = "PostListFragment";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private TextView emptyView;
    private CircularProgressView progressView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String username;


    public PostListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_post, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_view_list);
        mRecycler.setHasFixedSize(true);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        progressView = (CircularProgressView) rootView.findViewById(R.id.progress_view);
        progressView.startAnimation();


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        final Query postsQuery = getQuery(mDatabase);
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
                            final String[] mclubkey = new String[1];

                            mDatabase.child("club-post").orderByChild(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                        mclubkey[0] = childSnapshot.getKey();

                                    }
                                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getActivity(), DetailActivity.class);
                                            intent.putExtra(DetailActivity.EXTRA_POST_KEY, postKey);
                                            intent.putExtra(DetailActivity.EXTRA_CLUB_KEY,mclubkey[0]);
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

                                            viewHolder.bindToPost(imgurl,getActivity(),model, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View starView) {
                                                    DatabaseReference portalPostRef = mDatabase.child("portal-posts").child(postKey);
                                                    DatabaseReference userPostRef = mDatabase.child("user-posts").child(getUid()).child(postKey);
                                                    DatabaseReference globalPostRef = mDatabase.child("club-post").child(mclubkey[0]).child(postKey);

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

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getActivity(), "Failed to load CLUB ID.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }
    private void refreshContent(){
        this.onActivityCreated(null);
        mSwipeRefreshLayout.setRefreshing(false);
    }
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
