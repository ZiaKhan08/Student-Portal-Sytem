package com.example.the_strox.studentportal.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.the_strox.studentportal.R;
import com.example.the_strox.studentportal.models.Post;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView userView;
    public TextView subjectView;
    public TextView messageView;
    public TextView timeView;

    public MessageViewHolder(View itemView) {
        super(itemView);

        userView = (TextView) itemView.findViewById(R.id.username);
        subjectView = (TextView) itemView.findViewById(R.id.subject);
        messageView = (TextView) itemView.findViewById(R.id.message);
        timeView = (TextView) itemView.findViewById(R.id.time);
    }


}
