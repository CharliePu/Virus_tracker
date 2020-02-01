package com.example.virustracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PICK_LOCATION_REQUEST_CODE = 0;

    Toolbar toolbar;
    TextView detail, confirmed, name;
    FloatingActionButton fab;
    ProgressBar loading;
    SearchView searchView;

    HashMap<String, Record> data;

    String location;

    class syncWebTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            if (strings.length > 0){
                try{
                    data =  new WebScraper().parseWeb(strings[0]);
                } catch(Exception e){
                    e.printStackTrace();
                    return "WebScraper Error: Web exception signaled";
                }
            }
            return "WebScraper Error: No input address";
        }

        @Override
        protected void onPostExecute(String s) {
            loading.setVisibility(View.INVISIBLE);
            confirmed.setVisibility(View.VISIBLE);
            detail.setVisibility(View.VISIBLE);

            Record record = data.get(location);
            if (record == null){
                Log.d(TAG, "onPostExecute: Failed to get record");
                return;
            }
            name.setText(location);
            confirmed.setText(record.confirmed.toString());
            detail.setText(getString(R.string.detail,record.suspected,record.cured,record.dead));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        detail = findViewById(R.id.tv_detail);
        confirmed = findViewById(R.id.tv_confirmed);
        name = findViewById(R.id.tv_name);
        loading = findViewById(R.id.pb_loadData);

        setSupportActionBar(toolbar);

        location = "武汉";

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        grabData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        // Code from https://developer.android.com/training/appbar/action-views
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu_main.findItem(R.id.action_search).getActionView();
//        // Assumes current activity is the searchable activity
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, PickLocationActivity.class)));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                grabData();
                return true;
            case R.id.action_location:
                Intent intent = new Intent(this, PickLocationActivity.class);

                // Pass names of city to the pick location activity
                ArrayList<String> list = new ArrayList<String>(data.keySet());
                intent.putStringArrayListExtra("list", list);
                startActivityForResult(intent, PICK_LOCATION_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: passed");
        if (resultCode == RESULT_OK && requestCode == PICK_LOCATION_REQUEST_CODE) {
            location = data.getStringExtra("location");
            applyChanges();
        }
    }

    public void grabData(){
        loading.setVisibility(View.VISIBLE);
        confirmed.setVisibility(View.INVISIBLE);
        detail.setVisibility(View.INVISIBLE);

        new syncWebTask().execute("https://3g.dxy.cn/newh5/view/pneumonia");
    }

    public void applyChanges()
    {
        Record record = data.get(location);
        if (record == null){
            Log.d(TAG, "applyChanges: Failed to get record");
            return;
        }
        name.setText(location);
        confirmed.setText(record.confirmed.toString());
        detail.setText(getString(R.string.detail,record.suspected,record.cured,record.dead));
    }
}
