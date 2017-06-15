package com.vitech.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.vitech.popularmovies.adapters.ReviewsAdapter;
import com.vitech.popularmovies.adapters.TrailersAdapter;
import com.vitech.popularmovies.data.DataUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<HashMap<String,String>> {
    @BindView(R.id.movie_poster) ImageView poster;
    @BindView(R.id.movie_title) TextView movieTitle;
    @BindView(R.id.movie_synopsis) TextView movieSynopsis;
    @BindView(R.id.movie_rating) TextView movieRating ;
    @BindView(R.id.movie_release_date) TextView movieReleaseDate ;
    @BindView(R.id.favorite)FloatingActionButton favorite;
    @BindView(R.id.trailer_view)RecyclerView trailerView;
    @BindView(R.id.re_view_view)RecyclerView reviewView;
    @BindView(R.id.loading_trailers_and_reviews) ProgressBar loadingBar;
   public static final String  KEY_TRAILERS = "trailers";
   public static final String  KEY_REVIEWS = "reviews";
   public static final String  KEY_ERROR = "error";
   public static final String ARG_MOVIE_ID = "movie_id";
   @SuppressWarnings("FieldCanBeLocal")
   private static int LOADER_ID = 125817;
   HashMap<String,String> results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        try {
            final JSONObject movie = new JSONObject(getIntent().getStringExtra("movie"));
            Picasso.with(this).load(buildImageUrl(movie)).placeholder(R.drawable.drawable_loading).error(R.drawable.ic_warning_black_24dp).into(poster);
            movieTitle.setText(movie.getString("original_title"));
            movieSynopsis.setText(movie.getString("overview"));
            movieRating.setText(String.format(Locale.getDefault(),"%.1f",movie.getDouble("vote_average")));
            movieReleaseDate.setText(movie.getString("release_date"));
            favorite.setImageResource(getIntent().getBooleanExtra("favorite",false)?R.drawable.ic_delete_black_24dp:R.drawable.ic_favorite_black_24dp);
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    try {
                        if (getIntent().getBooleanExtra("favorite", false)) {
                            DataUtils.removeFavorite(MovieDetails.this, movie);
                            Toast.makeText(MovieDetails.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            DataUtils.markFavorite(MovieDetails.this, movie);
                            Toast.makeText(MovieDetails.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    }

            });

            Bundle args = new Bundle();
            args.putInt(ARG_MOVIE_ID,movie.getInt("id"));
            getSupportLoaderManager().restartLoader(LOADER_ID,args,this);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private String buildImageUrl(JSONObject object)throws JSONException
    {

        String posterPath = object.getString("backdrop_path");
        return getResources().getString(R.string.url_image_backdrop,posterPath);

    }

    @Override
    public Loader<HashMap<String,String >> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<HashMap<String,String >>(this) {
            @Override
            protected void onStartLoading(){
                super.onStartLoading();

                if(results==null){
                    loadingBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }else {

                    deliverResult(results);
                }
            }
            @Override
            public HashMap<String,String> loadInBackground() {

                return DataUtils.getTrailersAndReviewsFromWeb(MovieDetails.this,args);
            }

            @Override
            public void deliverResult(HashMap<String, String> data) {

                results = data;
                super.deliverResult(data);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String,String>> loader,HashMap<String,String> data) {
        loadingBar.setVisibility(View.GONE);

        try{
            TrailersAdapter adapter = new TrailersAdapter(MovieDetails.this,new JSONArray(data.get(KEY_TRAILERS)));
            trailerView.setAdapter(adapter);
            adapter.setOnTrailerClickedListener(new TrailersAdapter.OnTrailerClickedListener() {
                @Override
                public void onTrailerClicked(JSONObject trailer) {
try{
    startActivity(trailer);
}catch (JSONException j){
    j.printStackTrace();
}
                }
            });
            reviewView.setAdapter(new ReviewsAdapter(MovieDetails.this,new JSONArray(data.get(KEY_REVIEWS))));
            trailerView.setLayoutManager(new LinearLayoutManager(MovieDetails.this,LinearLayoutManager.VERTICAL,false){
                @SuppressWarnings("MethodReturnAlwaysConstant")
                @Override
                public boolean canScrollVertically() {
                   return false;
                }
            });
            reviewView.setLayoutManager(new LinearLayoutManager(MovieDetails.this,LinearLayoutManager.VERTICAL,false){
                @SuppressWarnings("MethodReturnAlwaysConstant")
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });



        }catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(MovieDetails.this,data.get(KEY_ERROR),Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onLoaderReset(Loader<HashMap<String,String>> loader) {

    }
    void startActivity(JSONObject object) throws JSONException{
Uri target = Uri.parse(getResources().getString(R.string.url_video_link,object.getString("key")));
Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(target);
        startActivity(Intent.createChooser(intent,""));
    }
}
