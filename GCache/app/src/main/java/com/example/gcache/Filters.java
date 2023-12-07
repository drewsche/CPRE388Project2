package com.example.gcache;

import android.content.Context;
import android.text.TextUtils;

import com.example.gcache.model.Post;
import com.google.firebase.firestore.Query;

public class Filters {

    private String poster = null;
    private String visibility = null;
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public Filters() {}

    public static Filters getDefault() {
        Filters filters = new Filters();
        filters.setSortBy(Post.FIELD_DATE_TIME);
        filters.setSortDirection(Query.Direction.DESCENDING);

        return filters;
    }

    public boolean hasPoster() {
        return !(TextUtils.isEmpty(poster));
    }
    public String getPoster() {
        return poster;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }

    public boolean hasVisibility() {
        return !(TextUtils.isEmpty(visibility));
    }
    public String getVisibility() {
        return visibility;
    }
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }
    public String getSortBy() {
        return sortBy;
    }
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }
    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (poster == null) {
            desc.append("<b>");
            desc.append("All ");
            desc.append("</b>");
        }
        else {
            desc.append("<b>");
            desc.append(poster + "'s ");
            desc.append("</b>");
        }

        if (visibility != null) {
            desc.append("<b>");
            desc.append(visibility + " ");
            desc.append("</b>");
        }

        desc.append("<b>");
        desc.append("Posts");
        desc.append("</b>");

        return desc.toString();
    }

    public String getOrderDescription(Context context) {
        if (Post.FIELD_POINTS.equals(sortBy)) {
            return "Sorted By Points";
        } else {
            return "Sorted By Date";
        }
    }
}
