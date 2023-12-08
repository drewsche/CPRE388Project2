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

import com.example.gcache.adapter.PostAdapter;
import com.example.gcache.viewmodel.PublicActivityViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class PublicActivity extends AppCompatActivity implements
//        View.OnClickListener,
        FilterDialogFragment.FilterListener,
        PostAdapter.OnPostSelectedListener {

    private static final String TAG = "PublicActivity";
    private static final int LIMIT = 50;

    private TextView mContentView;
    private TextView mSortByView;
    private RecyclerView publicPostsRecycler;
    private TextView mEmptyView;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FilterDialogFragment mFilterDialog;

    private PostAdapter mAdapter;

    private PublicActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public);

        mContentView = findViewById(R.id.publicActivity_textView_contents);
        mSortByView = findViewById(R.id.publicActivity_textView_sortBy);
        publicPostsRecycler = findViewById(R.id.postActivity_recyclerView_questions);
        mEmptyView = findViewById(R.id.publicActivity_textView_noResults);

        mViewModel = new ViewModelProvider(this).get(PublicActivityViewModel.class);

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

        checkCurrentUser();
    }

    public void checkCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
//            Toast.makeText(MainActivity.this, "<!!DEVELOPER TOOL!!>" +
////                    " UID:\n" + user.getUid(),
////                    " Display Name:\n" + user.getDisplayName(),
//                    " E-mail:\n" + user.getEmail(),
//                    Toast.LENGTH_LONG).show();
        } else {
            // No user is signed in
            Intent toLogin = new Intent(this, LoginActivity.class);
            startActivity(toLogin);
        }
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new PostAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    publicPostsRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    publicPostsRecycler.setVisibility(View.VISIBLE);
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

        publicPostsRecycler.setLayoutManager(new LinearLayoutManager(this));
        publicPostsRecycler.setAdapter(mAdapter);
    }

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

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onFilter(Filters filters) {
        filters.setVisibility("Public");
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

    public void onFilterClicked(View view) {
        // Show the dialog containing filter options
        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
    }

    @Override
    public void onPostSelected(DocumentSnapshot post) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra(PostActivity.KEY_POST_ID, post.getId());

        startActivity(intent);
    }

    public void onCameraClicked(View view) {
            Log.d(TAG, "onClick: called");
            Intent toCamera = new Intent(this, GeoCamera.class);
            startActivity(toCamera);
    }

    public void onAlbumClicked(View view) {
        Intent toAlbum = new Intent(this, AlbumActivity.class);
        startActivity(toAlbum);
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