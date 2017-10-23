package com.clarifai.android.starter.api.v2;

import java.util.ArrayList;

/**
 * Created by Lior on 9/5/2017.
 */

public class HistoryDataSource extends ArrayList<HistoryDataSource> {


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String url;
    String date;

    public HistoryDataSource(String url,String date){
        this.url = url;
        this.date = date;
    }




}
