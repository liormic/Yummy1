package com.clarifai.android.starter.api.v2.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clarifai.android.starter.api.v2.DatabaseHandler;
import com.clarifai.android.starter.api.v2.HistoryDataSource;
import com.clarifai.android.starter.api.v2.R;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Lior on 8/31/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>{
    private static final String TAG = "TAG" ;
//Declaring the array
   private ArrayList<HistoryDataSource> dataSources = new ArrayList<>();


    //Adapter constructor

  public HistoryAdapter (ArrayList<HistoryDataSource> dataSources){

      this.dataSources = dataSources;

  }

    DatabaseHandler databaseHandler ;


    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_list_iten,parent,false);
        HistoryViewHolder viewHolder = new HistoryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        String suri =dataSources.get(position).getUrl();
        Uri uri = Uri.parse(suri);
        Context context=  holder.imageView.getContext();
        holder.bindHistory(uri,context);

    }

    @Override
    public int getItemCount() {

        return dataSources.size();
    }



    //nested class to bind the data

    public class HistoryViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public ImageView imageView;



        public HistoryViewHolder(View itemView) {
            super(itemView);
           // textView = (TextView)itemView.findViewById(R.id.textView);
            imageView = (ImageView)itemView.findViewById(R.id.imageView);

        }

        public void bindHistory(Uri uri,Context context){
         //   textView = (TextView)itemView.findViewById(R.id.textView);
            imageView = (ImageView)itemView.findViewById(R.id.imageView);
              String suri ="InputStream stream = getContentResolver().openInputStream(uri)";

           imageView.setImageURI(uri);
            Uri suri2 = Uri.parse(suri);
         //   Picasso.with(context).load(uri).resize(600, 200).into(imageView);

        }
    }
}
