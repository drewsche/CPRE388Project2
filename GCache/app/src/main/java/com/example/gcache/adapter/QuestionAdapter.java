package com.example.gcache.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gcache.R;
import com.example.gcache.model.Question;
import com.google.firebase.firestore.Query;

/**
 * Class to support making a new question in a post.
 */
public class QuestionAdapter extends FirestoreAdapter<QuestionAdapter.ViewHolder> {

    public QuestionAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position).toObject(Question.class));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView questionTextView;
        TextView answerTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionItem_textView_question);
            answerTextView = itemView.findViewById(R.id.questionItem_textView_answer);
        }

        public void bind(Question question) {
            questionTextView.setText(question.getQuestion());
            answerTextView.setText(question.getAnswer());
        }

    }
}
