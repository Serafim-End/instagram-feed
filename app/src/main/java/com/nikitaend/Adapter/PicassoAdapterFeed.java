package com.nikitaend.instafeed.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.nikitaend.instafeed.Activity.Holder;
import com.nikitaend.instafeed.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Endaltsev Nikita
 *         start at 10.04.15.
 */
public class PicassoAdapterFeed extends ArrayAdapter<Holder> {


    private ImageLoader mImageLoader;
    private ArrayList<Holder> mImages = null;
    private Context mContext;
    
    
    public PicassoAdapterFeed(Context context, int resource,
                              ArrayList<Holder> objects, ImageLoader imageLoader) {
        super(context, R.layout.item_feed, objects);

        this.mImageLoader = imageLoader;
        this.mImages = objects;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_feed, parent, false);

        Holder item = getItem(position);
        String profilePhotoUrl = item.profileUrl;
        Picasso.with(convertView.getContext())
                .load(profilePhotoUrl)
                .resize(100, 100)
                .into((ImageView) convertView.findViewById(R.id.ivUserProfile));
        
        String imagePhotoUrl = item.imageUrl;
        ((NetworkImageView) convertView.findViewById(R.id.ivFeedCenter))
                .setImageUrl(imagePhotoUrl, mImageLoader);

        ((TextView) convertView.findViewById(R.id.nameUserTextView)).setText(item.userName);
        ((TextSwitcher) convertView.findViewById(R.id.tsLikesCounter))
                    .setCurrentText(item.countOfLikes + "");



        return convertView;
    }
}
