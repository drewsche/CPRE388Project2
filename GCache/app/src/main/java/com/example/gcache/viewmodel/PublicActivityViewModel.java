package com.example.gcache.viewmodel;

import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.gcache.Filters;
import com.example.gcache.PublicActivity;

public class PublicActivityViewModel extends ViewModel {

    private Filters mFilters;

    public PublicActivityViewModel() {
        mFilters = Filters.getDefault();
    }

    public Filters getFilters() {
        return mFilters;
    }
    public void setFilters(Filters mFilters) {
        this.mFilters = mFilters;
    }
}
