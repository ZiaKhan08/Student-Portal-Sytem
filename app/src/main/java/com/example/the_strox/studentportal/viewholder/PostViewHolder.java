package com.example.the_strox.studentportal.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.the_strox.studentportal.R;
import com.example.the_strox.studentportal.models.Post;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;
    public CircleImageView authorImageView;
    public TextView dateView;


    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorImageView = (CircleImageView) itemView.findViewById(R.id.post_author_photo) ;
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        starView = (ImageView) itemView.findViewById(R.id.star);
        numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        dateView = (TextView) itemView.findViewById(R.id.post_time);
    }

    public void bindToPost(String imgurl,Context ctx, Post post, View.OnClickListener starClickListener) {
        titleView.setText(post.title);
        authorView.setText(post.author);
        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);
        dateView.setText(post.date);
        if(imgurl!=null)
            Picasso.with(ctx).load(imgurl).into(authorImageView);
        starView.setOnClickListener(starClickListener);
    }
}