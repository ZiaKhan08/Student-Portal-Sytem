package com.example.the_strox.studentportal.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class ParticipatedFragment extends PostListFragment {

    public ParticipatedFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query portalPostQuery = databaseReference.child("user-posts").child(getUid());
        return portalPostQuery;
    }
}