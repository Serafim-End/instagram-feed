package com.nikitaend.instafeed.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.nikitaend.instafeed.R;

import java.util.ArrayList;

/**
 * @author Endaltsev Nikita
 *         start at 09.04.15.
 */
public class PicassoAdapter extends ArrayAdapter<String> {
    
    private ArrayList<String> images = null;
    private ImageLoader mImageLoader;
    //private int layoutResource;
    private Context context;
    
    public PicassoAdapter(Context context, int resource, 
                          ArrayList<String> objects, ImageLoader imageLoader) {
        super(context, R.layout.item_preview, objects);
        
        // this.layoutResource = resource;
        this.mImageLoader = imageLoader;
        this.images = objects;
        this.context = context;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context)
                .inflate(R.layout.item_preview, parent, false);

        String photoUrl = getItem(position);
        ((NetworkImageView) convertView.findViewById(R.id.ivFeedCenter))
                .setImageUrl(photoUrl, mImageLoader);
        
        return convertView;
    }


}
