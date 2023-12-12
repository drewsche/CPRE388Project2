package com.example.gcache;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.gcache.model.Post;
import com.google.firebase.firestore.Query;

/**
 * This class handles the filter options on the public/album activity. The filter options allow the user to sort/filter the posts.
 */
public class FilterDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "FilterDialog";

    interface FilterListener {

        void onFilter(Filters filters);

    }

    private View mRootView;

    private Spinner mSortSpinner;

    private FilterListener mFilterListener;

    /**
     * Setup for the filters dialog i.e. all the buttons
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return returns a view to be used in the public/album activity
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_filters, container, false);

        mSortSpinner = mRootView.findViewById(R.id.spinner_sort);

        mRootView.findViewById(R.id.button_search).setOnClickListener(this);
        mRootView.findViewById(R.id.button_cancel).setOnClickListener(this);

        return mRootView;
    }

    /**
     * Called when the fragment is attached to the given context. For example the album/public pages
     * @param context given context to attach to
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        }
    }

    /**
     * Android Lifecycle Management
     */
    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Called when clicks are registered
     * @param v view associated with Filter Dialog Fragment
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_search) {
            onSearchClicked();
        }
        else if (v.getId() == R.id.button_cancel) {
            onCancelClicked();
        }
    }

    /**
     * Called when search is clicked
     */
    public void onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }

        dismiss();
    }

    /**
     * Called when cancel is clicked
     */
    public void onCancelClicked() {
        dismiss();
    }

    /**
     * Called when the sort by menu is opened. Provides the user the option to select which attribute to sort by.
     * @return
     */
    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if ("Sort By Date".equals(selected)) {
            return Post.FIELD_DATE_TIME;
        } if ("Sort By Points".equals(selected)) {
            return Post.FIELD_POINTS;
        }

        return null;
    }

    /**
     * Determines which sorting behaviour to use.
     * @return
     */
    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if ("Sort By Date".equals(selected)) {
            return Query.Direction.DESCENDING;
        } if ("Sort By Points".equals(selected)) {
            return Query.Direction.DESCENDING;
        }

        return null;
    }

    /**
     * Removes all filters selected by the user without.
     */
    public void resetFilters() {
        if (mRootView != null) {
            mSortSpinner.setSelection(0);
        }
    }

    /**
     * Returns the filters available.
     * @return
     */
    public Filters getFilters() {
        Filters filters = new Filters();

        if (mRootView != null) {
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(getSortDirection());
        }

        return filters;
    }
}
