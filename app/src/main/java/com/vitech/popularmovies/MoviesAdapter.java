package com.vitech.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {
   private Context context;
   private JSONArray movies_list;
 private   OnItemClickListener onItemClickListener;

 void setOnItemClickListener(OnItemClickListener onItemClickListener){
     this.onItemClickListener = onItemClickListener;
 }
    MoviesAdapter(Context context, JSONArray movies_list){
    this.context = context;
    this.movies_list = movies_list;
}

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView movie   = new ImageView(context);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        movie.setLayoutParams(params);
        movie.setScaleType(ImageView.ScaleType.FIT_XY);
        movie.setAdjustViewBounds(true);
        return new MovieHolder(movie);
    }



    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
try{
    Picasso.with(context).load(buildImageUrl(movies_list.getJSONObject(position))).placeholder(R.drawable.drawable_loading).into(holder.view);
    holder.object = movies_list.getJSONObject(position);

}catch (JSONException j){
    j.printStackTrace();
}
    }

    @Override
    public int getItemCount() {

        return movies_list.length();
    }


    private String buildImageUrl(JSONObject object)throws JSONException
    {

        String posterPath = object.getString("poster_path");
        return context.getResources().getString(R.string.url_image_thumbnail,posterPath);

    }
    class MovieHolder extends RecyclerView.ViewHolder{
        ImageView view;
        JSONObject object;
      MovieHolder(View itemView) {
            super(itemView);

            view = (ImageView)itemView;
          view.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  onItemClickListener.onItemClick(object);
              }
          });
      }
    }
    interface OnItemClickListener{
        void onItemClick(JSONObject object);
    }
}

