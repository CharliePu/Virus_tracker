package com.example.virustracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PICK_LOCATION_REQUEST_CODE = 0;

    Toolbar toolbar;
    TextView detail, confirmed, name, updateMessage;
    ProgressBar loading;
    SearchView searchView;

    SharedPreferences sharedPref;

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

            String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
            updateMessage.setText(getString(R.string.update_message,currentDateTimeString));
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
        updateMessage = findViewById(R.id.tv_update_message);

        setSupportActionBar(toolbar);

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        location = sharedPref.getString("location","武汉");

        grabData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                grabData();
                return true;
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_location:
                    intent = new Intent(this, PickLocationActivity.class);

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
        sharedPref.edit().putString("location",location).apply();

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
