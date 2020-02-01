package com.example.virustracker;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class PickLocationActivity extends AppCompatActivity {
    private static final String TAG = "PickLocationActivity";

    private Toolbar toolbar;
    private ListView listView;

    private List<String> dataList;
    private int searchResultItemLayout;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.list);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        dataList = getIntent().getStringArrayListExtra("list");

        adapter =new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                dataList);
        // adapter = new SearchAdapter(this, R.layout.search_item, dataList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("location",adapter.getItem(position));
                Log.d(TAG, "onItemClick: "+dataList.get(position));
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pick_location, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        // code from https://www.geeksforgeeks.org/android-searchview-with-example/

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query)
                    {
                        if (dataList.contains(query)) {
                            adapter.getFilter().filter(query);
                        }
                        else {
                            // Search query not found in List View
                            Log.d(TAG, "onQueryTextSubmit: query not found");
                        }
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText)
                    {
                        adapter.getFilter().filter(newText);
                        return false;
                    }
                });

        return true;
    }
}
