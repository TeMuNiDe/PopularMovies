package com.vitech.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MoviesDashboard extends AppCompatActivity {
RecyclerView moviesView;
MoviesAdapter moviesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_dashboard);
    moviesView = (RecyclerView)findViewById(R.id.moviesView);
        setTitle(R.string.menu_sort_popular);
new MovieLoader().execute(R.string.url_popular);
    }
   private class MovieLoader extends AsyncTask<Integer,Void,String>{
        ProgressDialog dialog;
       @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MoviesDashboard.this);
            dialog.setMessage("Loading...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {
            String API_KEY = getResources().getString(R.string.api_key);
            String response = "";


            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(getResources().getString(params[0], API_KEY)).openConnection();
                connection.setConnectTimeout(30 * 1000);
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream responseStream = connection.getInputStream();
                Scanner scanner = new Scanner(responseStream);
                scanner.useDelimiter("\\A");
                if (scanner.hasNext()) {
                    response = scanner.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof UnknownHostException) {
                    response = getResources().getString(R.string.no_internet);
                } else if (e instanceof SocketException) {
                    response = getResources().getString(R.string.connection_error);
                } else if (e instanceof SocketTimeoutException) {
                    response = getResources().getString(R.string.connection_timed_out);
                } else {
                    response = getResources().getString(R.string.unknown_error);
                }
            }
        return response;

        }

        @Override
        protected void onPostExecute(String s) {
            dialog.cancel();

            try{
                JSONObject object = new JSONObject(s);
                moviesAdapter = new MoviesAdapter(getApplicationContext(),object.getJSONArray("results"));
                moviesView.setAdapter(moviesAdapter);
                moviesAdapter.setOnItemClickListener(new MoviesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(JSONObject object) {
                       startActivity(new Intent(MoviesDashboard.this,MovieDetails.class).putExtra("movie",object.toString()));
                    }
                });
                moviesView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));


            }catch (JSONException j){
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
            }



        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals(getResources().getString(R.string.menu_sort_popular))){
        item.setTitle(R.string.menu_sort_top_rated);
            setTitle(R.string.menu_sort_popular);
        new MovieLoader().execute(R.string.url_popular);
        }else {
            item.setTitle(R.string.menu_sort_popular);
            setTitle(R.string.menu_sort_top_rated);
            new MovieLoader().execute(R.string.url_top_rated);
        }



        return true;


    }
}
