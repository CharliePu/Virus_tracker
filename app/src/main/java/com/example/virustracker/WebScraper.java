package com.example.virustracker;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.HashMap;


public class WebScraper {
    private static final String TAG = "WebScraper";

    public HashMap<String, Record> parseWeb(String url) throws Exception {
        Element element = Jsoup.connect(url).get().body();

        String s = element.getElementById("getAreaStat").html();

        Log.d(TAG, "parseWeb: data: "+s);

        s = s.substring(27,s.length()-11);
        JSONArray data = new JSONArray(s);

        HashMap<String, Record> records = new HashMap<>();

        for (int i = 0; i != data.length(); ++i){
            JSONObject provinceData = data.getJSONObject(i);
            records.put(provinceData.getString("provinceName"),
                    new Record(provinceData.getInt("confirmedCount"),
                            provinceData.getInt("suspectedCount"),
                            provinceData.getInt("curedCount"),
                            provinceData.getInt("deadCount")));

            JSONArray cityData = provinceData.getJSONArray("cities");

            for (int j = 0; j != cityData.length(); ++j){
                records.put(cityData.getJSONObject(j).getString("cityName"),
                        new Record(cityData.getJSONObject(j).getInt("confirmedCount"),
                                cityData.getJSONObject(j).getInt("suspectedCount"),
                                cityData.getJSONObject(j).getInt("curedCount"),
                                cityData.getJSONObject(j).getInt("deadCount")));
            }
        }

        return records;
    }
}
