package com.example.virustracker;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

// Code adapted from https://www.zoftino.com/android-search-dialog-with-search-suggestions-example

public class PickLocationActivity extends AppCompatActivity {
    private static final String TAG = "PickLocationActivity";

    private Toolbar toolbar;
    private ListView listView;

    private List<String> dataList;
    private int searchResultItemLayout;
    private SearchAdapter adapter;

    private class SearchAdapter extends ArrayAdapter<String>{
        public SearchAdapter(Context context, int resource, List<String> storeSourceDataLst){
            super(context, resource, storeSourceDataLst);
            dataList = storeSourceDataLst;
            searchResultItemLayout = resource;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return dataList.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(searchResultItemLayout, parent, false);
            }

            TextView resultItem = (TextView) convertView.findViewById(R.id.tv_search_item);
            resultItem.setText(getItem(position));
            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.list);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dataList = getIntent().getStringArrayListExtra("list");;
        dataList.add("hehe");

        adapter = new SearchAdapter(this, R.layout.search_item, dataList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("location",dataList.get(position));
                Log.d(TAG, "onItemClick: "+dataList.get(position));
                setResult(RESULT_OK,intent);
                finish();
            }
        });

//        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
//            String query = getIntent().getStringExtra(SearchManager.QUERY);
//            Log.d(TAG, "onCreate: "+query);
//            search(query);
//        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void search(String query){
            adapter.getFilter().filter(query);
    }
}
