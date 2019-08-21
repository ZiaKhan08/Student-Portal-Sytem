package com.example.the_strox.studentportal.fragment;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;


public class BlogFragment extends PostListFragment {

    public BlogFragment(){}
    @Override
    public Query getQuery(DatabaseReference databaseReference){
        Query blogPostQuery=databaseReference.child("club-post").child("-KO5j6REBsLw0_36bHAi");
        return blogPostQuery;
    }
}