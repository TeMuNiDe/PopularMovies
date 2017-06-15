package com.vitech.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.vitech.popularmovies.adapters.MoviesAdapter;
import com.vitech.popularmovies.data.DataUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesDashboard extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,SharedPreferences.OnSharedPreferenceChangeListener {
    @BindView(R.id.moviesView)RecyclerView moviesView;
    ProgressDialog dialog;
    SharedPreferences userpreferences;
    public static final String QUERY_ENDPOINT = "endpoint";
    static final int LOADER_ID=1258;
    SparseArray<String> results;
    LoaderManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_dashboard);
        ButterKnife.bind(this);
        userpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setCorrectTitle();
        results = new SparseArray<>();
        if(savedInstanceState!=null){
            results.put(userpreferences.getInt(QUERY_ENDPOINT,R.string.url_popular),savedInstanceState.getString(Integer.toString(userpreferences.getInt(QUERY_ENDPOINT,R.string.url_popular))));
        }
        dialog = new ProgressDialog(MoviesDashboard.this);
        manager = getSupportLoaderManager();
        Bundle args = new Bundle();
        args.putInt(QUERY_ENDPOINT,userpreferences.getInt(QUERY_ENDPOINT,R.string.url_popular));
        manager.restartLoader(LOADER_ID,args,this);
    }
    void setCorrectTitle(){
        switch (userpreferences.getInt(QUERY_ENDPOINT,R.string.url_popular)){
            case R.string.url_popular:setTitle(R.string.menu_sort_popular);break;
            case R.string.url_top_rated:setTitle(R.string.menu_sort_top_rated);break;
            case R.string.url_favorite:setTitle(R.string.menu_sort_favorite);break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        userpreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        userpreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Loader<String> onCreateLoader(int id,final  Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(results.get(args.getInt(QUERY_ENDPOINT))==null||args.getInt(QUERY_ENDPOINT)==R.string.url_favorite){
                    Log.d("LOADER","entry not found");
                    Log.d("LOADER","started");
                    dialog.setMessage("Loading...");
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.show();
                    forceLoad();
                }
                else {
                    deliverResult(results.get(args.getInt(QUERY_ENDPOINT)));
                }
            }

            @Override
            public String loadInBackground() {

                if(args.getInt(QUERY_ENDPOINT)==R.string.url_favorite){

                    return DataUtils.getDataFromLocalContentProvider(MoviesDashboard.this);
                }else {

                   return DataUtils.getFromWeb(MoviesDashboard.this,args);
                }


            }

            @Override
            public void deliverResult(String data) {
                results.put(args.getInt(QUERY_ENDPOINT),data);
                super.deliverResult(data);
            }
        };



    }




    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d("LOADER","loading finished");

        dialog.cancel();

        try{
            JSONArray object = new JSONArray(data);
            MoviesAdapter moviesAdapter;
            moviesAdapter = new MoviesAdapter(getApplicationContext(),object);
            moviesView.setAdapter(moviesAdapter);
            moviesAdapter.setOnItemClickListener(new MoviesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(JSONObject object) {
                    startActivity(new Intent(MoviesDashboard.this,MovieDetails.class).putExtra("movie",object.toString()).putExtra("favorite",userpreferences.getInt(QUERY_ENDPOINT,R.string.url_popular)==R.string.url_favorite));
                }
            });
            moviesView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));


        }catch (JSONException j){
            Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            SharedPreferences.Editor args = userpreferences.edit();
            switch (item.getItemId()){
                case R.id.menu_sort_favorite:args.putInt(QUERY_ENDPOINT,R.string.url_favorite);break;
                case R.id.menu_sort_popular:args.putInt(QUERY_ENDPOINT,R.string.url_popular);break;
                case R.id.menu_sort_top_rated:args.putInt(QUERY_ENDPOINT,R.string.url_top_rated);break;
            }
            args.apply();

        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
        Bundle args = new Bundle();
        args.putInt(QUERY_ENDPOINT,sharedPreferences.getInt(QUERY_ENDPOINT,R.string.url_popular));
        manager.restartLoader(LOADER_ID,args,this);
        setCorrectTitle();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Integer.toString(userpreferences.getInt(QUERY_ENDPOINT,R.string.url_popular)),results.get(userpreferences.getInt(QUERY_ENDPOINT,R.string.url_popular)));
    }

}
