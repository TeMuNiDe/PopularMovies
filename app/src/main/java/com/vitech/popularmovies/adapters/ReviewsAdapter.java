package com.vitech.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vitech.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;



public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {

Context context;
JSONArray reviews;

public ReviewsAdapter(Context context,JSONArray reviews){
    this.context = context;
    this.reviews = reviews;
}
    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReviewHolder(LayoutInflater.from(context).inflate(R.layout.re_view_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        try {
            JSONObject review = reviews.getJSONObject(position);
            holder.reviewAuthor.setText(review.getString("author"));
            holder.reviewContent.setText(review.getString("content"));
        }catch (JSONException j){
            j.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reviews.length();
    }

    class ReviewHolder extends RecyclerView.ViewHolder{
@BindView(R.id.re_view_author)TextView reviewAuthor;
@BindView(R.id.re_view_content)TextView reviewContent;
    public ReviewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
}
