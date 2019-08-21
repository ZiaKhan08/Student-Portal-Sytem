package com.example.the_strox.studentportal.fragment;



import android.content.Intent;
import android.os.Bundle;
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

import com.example.the_strox.studentportal.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import com.example.the_strox.studentportal.R;
import com.example.the_strox.studentportal.models.Club;
import com.example.the_strox.studentportal.ClubPostActivity;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public  class SubscribedFragment extends Fragment  {
    private Map<String, Boolean> clubs = new HashMap<>();

    public static class ClubViewHolder extends RecyclerView.ViewHolder {
        public TextView clubNameTextView;



        public ClubViewHolder(View itemView) {
            super(itemView);
            clubNameTextView = (TextView) itemView.findViewById(R.id.list_category);

        }
    }

    private static final String TAG = "SubscribedFragment";
    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Club, ClubViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private TextView emptyView;
    private CircularProgressView progressView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public SubscribedFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_category_list, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mSwipeRefreshLayout=(SwipeRefreshLayout)rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_view_category_list);
        emptyView= (TextView)rootView.findViewById(R.id.empty_view) ;
        mRecycler.setHasFixedSize(true);
        progressView =(CircularProgressView)rootView.findViewById(R.id.progress_view);
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

        mDatabase.child("user-club").child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()==null){
                    emptyView.setVisibility(View.VISIBLE);
                    progressView.stopAnimation();
                    progressView.setVisibility(View.GONE);
                }
                else {
                    mAdapter = new FirebaseRecyclerAdapter<Club, ClubViewHolder>(
                            Club.class,
                            R.layout.item_categories,
                            ClubViewHolder.class,
                            mDatabase.child("user-club").child(getUid())) {

                        @Override
                        protected void populateViewHolder(ClubViewHolder viewHolder, Club club, int position) {
                            final DatabaseReference clubRef = getRef(position);
                            viewHolder.clubNameTextView.setText(club.getName());
                            final String clubKey = clubRef.getKey();

                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), ClubPostActivity.class);
                                    intent.putExtra(ClubPostActivity.EXTRA_CLUB_KEY, clubKey);
                                    startActivity(intent);
                                }
                            });
                        }
                    };
                    //}
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }



    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void refreshContent(){
        this.onActivityCreated(null);
        mSwipeRefreshLayout.setRefreshing(false);
    }

}

