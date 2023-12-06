package com.example.gcache.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gcache.R;
import com.example.gcache.model.Post;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class PostAdapter extends FirestoreAdapter<PostAdapter.ViewHolder> {

    public interface OnPostSelectedListener {

        void onPostSelected(DocumentSnapshot post);

    }

    private OnPostSelectedListener mListener;

    public PostAdapter(Query query, OnPostSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dateTimeView;
        TextView locationNameView;
        TextView whoMetWhoView;
        ImageView photoView;
        TextView pointsView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTimeView = itemView.findViewById(R.id.postItem_textView_dateTime);
            locationNameView = itemView.findViewById(R.id.postItem_textView_locationName);
            whoMetWhoView = itemView.findViewById(R.id.postItem_textView_whoMetWho);
            photoView = itemView.findViewById(R.id.postItem_imageView_photo);
            pointsView = itemView.findViewById(R.id.postItem_textView_points);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnPostSelectedListener listener) {

            Post post = snapshot.toObject(Post.class);
            Resources resources = itemView.getResources();

            // Load photo
            Glide.with(photoView.getContext())
                    .load(post.getPhoto())
                    .into(photoView);

            Timestamp tempDateTime = post.getDateTime();
            dateTimeView.setText("On " + tempDateTime.toString());
            locationNameView.setText("At " + post.getLocationName());
            String poster = post.getPoster();
            String metPerson = post.getMetPerson();
            whoMetWhoView.setText(poster + " met " + metPerson);
            pointsView.setText("Points:\n" + post.getPoints());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onPostSelected(snapshot);
                    }
                }
            });
        }

    }
}
