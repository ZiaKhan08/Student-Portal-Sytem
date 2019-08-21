package com.example.the_strox.studentportal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.example.the_strox.studentportal.models.Message;
import com.example.the_strox.studentportal.models.Post;
import com.example.the_strox.studentportal.models.User;
import com.example.the_strox.studentportal.viewholder.MessageViewHolder;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.example.the_strox.studentportal.R;
import com.google.firebase.database.ValueEventListener;


public class InboxFragment extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private TextView emptyView;
    private CircularProgressView progressView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String username;

    public InboxFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mSwipeRefreshLayout=(SwipeRefreshLayout)rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        progressView =(CircularProgressView)rootView.findViewById(R.id.progress_view_inbox);
        progressView.startAnimation();

        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_view_message);
        mRecycler.setHasFixedSize(true);
        emptyView= (TextView)rootView.findViewById(R.id.empty_view_inbox) ;

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

        mDatabase.child("users").child(getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){
                            User user = dataSnapshot.getValue(User.class);
                            username = user.getUsername();

                            mDatabase.child("messages").orderByChild("receiver").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue()==null){
                                        emptyView.setVisibility(View.VISIBLE);
                                        progressView.stopAnimation();
                                        progressView.setVisibility(View.GONE);
                                    }
                                    else{
                                        mAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                                                Message.class,
                                                R.layout.item_message,
                                                MessageViewHolder.class,
                                                mDatabase.child("messages").orderByChild("receiver").equalTo(username)) {

                                            @Override
                                            protected void populateViewHolder(MessageViewHolder viewHolder,Message model, int position) {

                                                viewHolder.userView.setText(model.getSender());
                                                viewHolder.subjectView.setText(model.getSubject());
                                                viewHolder.messageView.setText(model.getMessage());
                                                viewHolder.timeView.setText(model.getTime());
                                            }
                                        };
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
                        else
                            Toast.makeText(getActivity(),"No USER FOUND", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity(),"ERROR DATATBASE", Toast.LENGTH_SHORT).show();
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
