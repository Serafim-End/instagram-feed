package com.nikitaend.instafeed.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nikitaend.instafeed.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Endaltsev Nikita
 *         start at 09.04.15.
 */
public class PicassoAdapter extends ArrayAdapter<String> {
    
    private ArrayList<String> images = null;
    //private int layoutResource;
    private Context context;
    
    public PicassoAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, R.layout.item_preview, objects);
        
        // this.layoutResource = resource;
        this.images = objects;
        this.context = context;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context)
                .inflate(R.layout.item_preview, parent, false);
        
        String photoUrl = getItem(position);
        Picasso.with(convertView.getContext())
                .load(photoUrl).resize(100, 100)
                .into((ImageView) convertView.findViewById(R.id.ivFeedCenter));
        
        return convertView;
    }


}
