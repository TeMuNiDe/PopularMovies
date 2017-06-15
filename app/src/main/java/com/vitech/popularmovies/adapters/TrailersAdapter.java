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

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerHolder> {
    Context context;
    JSONArray trailers;
    OnTrailerClickedListener onTrailerClickedListener;
   public  TrailersAdapter(Context context, JSONArray trailers){
    this.context = context;
    this.trailers = trailers;

    }

    public void setOnTrailerClickedListener(OnTrailerClickedListener onTrailerClickedListener) {
        this.onTrailerClickedListener = onTrailerClickedListener;
    }

    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrailerHolder(LayoutInflater.from(context).inflate(R.layout.trailer_item,parent,false));
    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, int position) {
        try {
            holder.trailerName.setText(trailers.getJSONObject(position).getString("name"));
            holder.trailer  = trailers.getJSONObject(position);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return trailers.length();
    }

    class TrailerHolder extends RecyclerView.ViewHolder{
        JSONObject trailer;
    @BindView(R.id.trailer_name) TextView trailerName;
    public TrailerHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onTrailerClickedListener!=null){
                    onTrailerClickedListener.onTrailerClicked(trailer);
                }
            }
        });
    }
}
public interface OnTrailerClickedListener{
    void onTrailerClicked(JSONObject trailer);
}
}
