package com.vitech.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MovieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        try {
            JSONObject movie = new JSONObject(getIntent().getStringExtra("movie"));
            ImageView poster = (ImageView)findViewById(R.id.movie_poster);
            TextView movieTitle = (TextView)findViewById(R.id.movie_title);
            TextView movieSynopsis  = (TextView)findViewById(R.id.movie_synopsis);
            TextView movieRating = (TextView)findViewById(R.id.movie_rating);
            TextView movieReleaseDate  = (TextView)findViewById(R.id.movie_release_date);
            Picasso.with(this).load(buildImageUrl(movie)).placeholder(R.drawable.drawable_loading).into(poster);
movieTitle.setText(movie.getString("original_title"));
movieSynopsis.setText(movie.getString("overview"));
movieRating.setText(String.format(Locale.getDefault(),"%.1f",movie.getDouble("vote_average")));
movieReleaseDate.setText(movie.getString("release_date"));




        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private String buildImageUrl(JSONObject object)throws JSONException
    {

        String posterPath = object.getString("poster_path");
        return getResources().getString(R.string.url_image_backdrop,posterPath);

    }
}
