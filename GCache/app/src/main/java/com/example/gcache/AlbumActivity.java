package com.example.gcache;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gcache.adapter.PostAdapter;
import com.example.gcache.viewmodel.AlbumActivityViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

/**
 * This class represents the album in which a user can view
 * their recorded posts on this page.
 */
public class AlbumActivity extends AppCompatActivity implements
//        View.OnClickListener,
        FilterDialogFragment.FilterListener,
        PostAdapter.OnPostSelectedListener {

    private static final String TAG = "AlbumActivity";
    private static final int LIMIT = 50;

    private TextView mContentView;
    private TextView mSortByView;
    private RecyclerView albumPostsRecycler;
    private TextView mEmptyView;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FilterDialogFragment mFilterDialog;

    private PostAdapter mAdapter;

    private AlbumActivityViewModel mViewModel;

    /**
     * Creates all the initial content
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        mContentView = findViewById(R.id.albumActivity_textView_contents);
        mSortByView = findViewById(R.id.albumActivity_textView_sortBy);
        albumPostsRecycler = findViewById(R.id.albumActivity_recyclerView_albumPosts);
        mEmptyView = findViewById(R.id.albumActivity_textView_noResults);

        mViewModel = new ViewModelProvider(this).get(AlbumActivityViewModel.class);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Initialize Firestore and the main RecyclerView
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("posts")
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .limit(LIMIT);
        initRecyclerView();

        // Filter Dialog
        mFilterDialog = new FilterDialogFragment();
    }

    /**
     * Creates a recycler view that updates when posts/filters change the content
     */
    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new PostAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    albumPostsRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    albumPostsRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        albumPostsRecycler.setLayoutManager(new LinearLayoutManager(this));
        albumPostsRecycler.setAdapter(mAdapter);
    }

    /**
     * Starts connection to firebase
     */
    @Override
    public void onStart() {
        super.onStart();

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }

        // Apply filters
        onFilter(mViewModel.getFilters());
    }

    /**
     * Stops connection with firebase.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    /**
     * Modifies the filters that are active
     * @param filters list of the filters that can be used to sort the posts
     */
    @Override
    public void onFilter(Filters filters) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        filters.setPosterUID(user.getUid());
        Query query = mFirestore.collection("posts"); //hint: CollectionReference extends Query

        if(filters.hasPosterUID()) {
            query = query.whereEqualTo("posterUID",filters.getPosterUID());
        }

        if(filters.hasVisibility()) {
            query = query.whereEqualTo("visibility",filters.getVisibility());
        }

        if(filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        // Limit items
        query = query.limit(LIMIT);

        // Update the query
        mQuery = query;
        mAdapter.setQuery(query);

        // Set header
        mContentView.setText(
                Html.fromHtml(filters.getSearchDescription(this)));
        mSortByView.setText(filters.getOrderDescription(this));

        // Save filters
        mViewModel.setFilters(filters);
    }

    /**
     * handles button show/hide filter dialogue
     * @param view
     */
    public void onFilterClicked(View view) {
        // Show the dialog containing filter options
        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
    }

    /**
     * Expands a specific post from the timeline
     * @param post which post to expand
     */
    @Override
    public void onPostSelected(DocumentSnapshot post) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra(PostActivity.KEY_POST_ID, post.getId());

        startActivity(intent);
    }

    /**
     * Moves the user to the camera page to take a photo
     * @param view activity_album view xml
     */
    public void onCameraClicked(View view) {
        Log.d(TAG, "onClick: called");
        Intent toCamera = new Intent(this, GeoCamera.class);
        startActivity(toCamera);
    }

    /**
     * Moves user to the public album page if not already there
     * @param view activity_album view xml
     */
    public void onPublicClicked(View view) {
        Intent toPublic = new Intent(this, PublicActivity.class);
        startActivity(toPublic);
    }

    /**
     * Moves the user to the maps page if not already there
     * @param v activity_album view xml
     */
    public void onMapsClicked(View v) {
        Intent toMaps = new Intent(this, MapsActivity.class);
        Log.d(TAG, "onMapsClicked: goToMaps");
        startActivity(toMaps);
    }

    /**
     * Moves the user to the manage account page if not already there.
     * @param view activity_album view xml
     */
    public void onAccountClicked(View view) {
        Intent toAccount = new Intent(this, AccountActivity.class);
        startActivity(toAccount);
    }
}