package com.nikitaend.instafeed.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nikitaend.instafeed.Volley.MyVolley;
import com.nikitaend.instafeed.sola.instagram.InstagramSession;
import com.nikitaend.instafeed.sola.instagram.io.UriFactory;
import com.nikitaend.instafeed.sola.instagram.model.Media;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Endaltsev Nikita
 *         start at 12.04.15.
 */
public class ImageRefresher {
    public static final String TAG = MainActivity.TAG;
    public static final int RESULTS_PAGE_SIZE = 3;
    
    private String currentMaxID = null;
    private InstagramSession session;
    private ArrayList<Holder> imagesUrlArray;
    private ArrayAdapter picassoAdapterFeed;
    private ListView listView;
    private Activity thatCalled;
    public String uri_count = null;
    private boolean mInError = false;
    
    public ImageRefresher(InstagramSession session, ArrayList<Holder> imagesUrlArray,
                          ArrayAdapter picassoAdapterFeed, ListView listView,
                          Activity thatCalled, String currentMaxID,  boolean inError) {
        this.session = session;
        this.imagesUrlArray = imagesUrlArray;
        this.picassoAdapterFeed = picassoAdapterFeed;
        this.listView = listView;
        this.thatCalled = thatCalled;
        this.currentMaxID = currentMaxID;
        this.mInError = inError;
        
        this.listView.setOnScrollListener(new EndlessScrollListener());
    }
    
    
    public void loadPage(final ListView listView,
                          String tagName, int count) {
        RequestQueue queue = MyVolley.getRequestQueue();

        tagName = tagName.replaceAll("^#*", "");
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("tag_name", tagName);
        String uri;
        if (uri_count != null) {
            uri = session.uriConstructor.constructUri(
                    UriFactory.Tags.GET_RECENT_TAGED_MEDIA, map, true) + uri_count;
        } else if (currentMaxID == null) {
            uri = session.uriConstructor.constructUri(
                    UriFactory.Tags.GET_RECENT_TAGED_MEDIA, map, true);

        } else {
            uri = session.uriConstructor.constructUri(
                    UriFactory.Tags.GET_RECENT_TAGED_MEDIA, map, true) + "&max_id=" + currentMaxID;
            Log.e("test", uri);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                uri,
                (JSONObject)null,
                createMyReqSuccessListener(),
                createMyReqErrorListener());
        queue.add(request);
    }
    
    

    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                try {
                    JSONObject pagination = object.optJSONObject("pagination");
                    JSONArray media = object.optJSONArray("data");


                    for (int i = 0; i < media.length(); i++) {
                        Media mediaI =
                                Media.fromJSON( media.getJSONObject(i), session.getAccessToken());
                        Holder item = new Holder(mediaI.getUser().getProfilePictureURI(),
                                mediaI.getStandardResolutionImage().getUri(),
                                mediaI.getUser().getUserName(),
                                mediaI.getLikeCount());

                        currentMaxID = mediaI.getCaption().getId();
                        Log.e("test", currentMaxID);
                        imagesUrlArray.add(item);
                    }
                    picassoAdapterFeed.notifyDataSetChanged();
                } catch (JSONException e) {
                    showErrorDialog();
                }
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorDialog();
            }
        };
    }


    private void showErrorDialog() {
        mInError = true;

        AlertDialog.Builder b =
                new AlertDialog.Builder(thatCalled);
        b.setMessage("Error occured");
        b.show();
    }
    
    
    public class EndlessScrollListener implements AbsListView.OnScrollListener {
        // how many entries earlier to start loading next page
        private int visibleThreshold = 1;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                loadPage(listView, TAG, RESULTS_PAGE_SIZE);
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }


        public int getCurrentPage() {
            return currentPage;
        }
    }
}
