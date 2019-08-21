package com.example.the_strox.studentportal.fragment;

import android.support.v4.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class TopFragment extends PostListFragment {

    public TopFragment(){}
    @Override
    public Query getQuery(DatabaseReference databaseReference){
        Query topPostQuery=databaseReference.child("club-post").orderByChild("starcount");
        return topPostQuery;
    }
}