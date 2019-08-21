package com.example.the_strox.studentportal.fragment;

import android.support.v4.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class PortalFragment extends PostListFragment {

    public PortalFragment(){}
    @Override
    public Query getQuery(DatabaseReference databaseReference){
        Query portalPostQuery=databaseReference.child("portal-posts");
        return portalPostQuery;
    }
}