package com.clarifai.android.starter.api.v2.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.clarifai.android.starter.api.v2.DatabaseHandler;
import com.clarifai.android.starter.api.v2.HistoryDataSource;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.adapter.HistoryAdapter;

import java.util.ArrayList;

public class History extends AppCompatActivity {




    ArrayList<HistoryDataSource> dataSources = new ArrayList<>();
    DatabaseHandler databaseHandler = new DatabaseHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);



        databaseHandler.getReadableDatabase();

        dataSources = databaseHandler.retreiveUrls();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HistoryAdapter adapter = new HistoryAdapter(dataSources);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


            }

}

























