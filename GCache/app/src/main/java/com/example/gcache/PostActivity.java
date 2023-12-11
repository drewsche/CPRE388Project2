package com.example.gcache;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gcache.adapter.QuestionAdapter;
import com.example.gcache.model.Post;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

public class PostActivity extends AppCompatActivity implements
//        View.OnClickListener,
        EventListener<DocumentSnapshot> {


    private static final String TAG = "PostActivity";

    public static final String KEY_POST_ID = "key_post_id";

    private ImageView photoImageView;
    private TextView whoWhenTextView;
    private TextView totalPointsTextView;
    private TextView whereTextView;
    private TextView distancePointsTextView;
    private TextView aboutMetPersonTextView;
    private RecyclerView questionsRecyclerView;

    private FirebaseFirestore mFirestore;
    private DocumentReference mPostRef;
    private ListenerRegistration mPostRegistration;

    private QuestionAdapter mQuestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        photoImageView = findViewById(R.id.postActivity_imageView_photo);
        whoWhenTextView = findViewById(R.id.postActivity_textView_whoWhen);
        totalPointsTextView = findViewById(R.id.postActivity_textView_totalPoints);
        whereTextView = findViewById(R.id.postActivity_textView_where);
        distancePointsTextView = findViewById(R.id.postActivity_textView_distancePoints);
        aboutMetPersonTextView = findViewById(R.id.postActivity_textView_aboutMetPerson);
        questionsRecyclerView = findViewById(R.id.postActivity_recyclerView_questions);

        // Get post ID from extras
        String postId = getIntent().getExtras().getString(KEY_POST_ID);
        if (postId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_POST_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the post
        mPostRef = mFirestore.collection("posts").document(postId);

        // Get questions
        Query questionsQuery = mPostRef
                .collection("questions")
                .orderBy("index", Query.Direction.ASCENDING)
                .limit(999);

        // RecyclerView
        mQuestionAdapter = new QuestionAdapter(questionsQuery) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    questionsRecyclerView.setVisibility(View.GONE);
                    aboutMetPersonTextView.setVisibility(View.GONE);
                } else {
                    questionsRecyclerView.setVisibility(View.VISIBLE);
                    aboutMetPersonTextView.setVisibility(View.VISIBLE);
                }
            }
        };

        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionsRecyclerView.setAdapter(mQuestionAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        mQuestionAdapter.startListening();
        mPostRegistration = mPostRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mQuestionAdapter.stopListening();

        if (mPostRegistration != null) {
            mPostRegistration.remove();
            mPostRegistration = null;
        }
    }

    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "post:onEvent", e);
            return;
        }

        onPostLoaded(snapshot.toObject(Post.class));
    }

    private void onPostLoaded(Post post) {

        String photoString = post.getPhoto();
        if(isBase64(photoString)) {
            // Decode the base64 string to a Bitmap
            Bitmap bitmap = decodeBase64(photoString);
            // Display the Bitmap using Glide
            Glide.with(photoImageView.getContext())
                    .load(bitmap)
                    .into(photoImageView);
        }
        else {
            // Load photo
            Glide.with(photoImageView.getContext())
                    .load(photoString)
                    .into(photoImageView);
        }

        Timestamp tempDateTime = post.getDateTime();
        whoWhenTextView.setText(post.getPoster() + " met " + post.getMetPerson() +
                        "\nOn " + tempDateTime.toDate());
        totalPointsTextView.setText("Total Points:\n" + post.getPoints());
        whereTextView.setText("Location:\n" +
                post.getLocationName());
        int tempDistancePoints = (int) ((Math.ceil(post.getDistance())) * 5);
        distancePointsTextView.setText("+" + tempDistancePoints + " Points");
        aboutMetPersonTextView.setText("About " + post.getMetPerson() + ":");
    }

    public static boolean isBase64(String str) {
        try {
            // Decode the string to check for errors
            byte[] decodedBytes = Base64.decode(str, Base64.DEFAULT);
            // Encode the decoded bytes to check for round-trip consistency
            String reencodedStr = Base64.encodeToString(decodedBytes, Base64.DEFAULT);
            // Check if the re-encoded string matches the original string
            return str.equals(reencodedStr);
        } catch (IllegalArgumentException e) {
            // An exception occurred during decoding, indicating that the string is not base64-encoded
            return false;
        }
    }

    private Bitmap decodeBase64(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void onAlbumClicked(View view) {
        Intent toAlbum = new Intent(this, AlbumActivity.class);
        startActivity(toAlbum);
    }
    public void onPublicClicked(View view) {
        Intent toPublic = new Intent(this, PublicActivity.class);
        startActivity(toPublic);
    }
    public void onMapsClicked(View v) {
        Intent toMaps = new Intent(this, MapsActivity.class);
        Log.d(TAG, "onMapsClicked: goToMaps");
        startActivity(toMaps);
    }
    public void onAccountClicked(View view) {
        Intent toAccount = new Intent(this, AccountActivity.class);
        startActivity(toAccount);
    }
}