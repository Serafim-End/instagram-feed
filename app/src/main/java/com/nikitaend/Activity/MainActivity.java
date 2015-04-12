package com.nikitaend.instafeed.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nikitaend.instafeed.Adapter.PicassoAdapter;
import com.nikitaend.instafeed.R;
import com.nikitaend.instafeed.View.CircleButton;
import com.nikitaend.instafeed.View.HorizontalListView;
import com.nikitaend.instafeed.sola.instagram.InstagramSession;
import com.nikitaend.instafeed.sola.instagram.auth.AccessToken;
import com.nikitaend.instafeed.sola.instagram.auth.InstagramAuthentication;
import com.nikitaend.instafeed.sola.instagram.exception.InstagramException;
import com.nikitaend.instafeed.sola.instagram.io.UriFactory;
import com.nikitaend.instafeed.sola.instagram.model.Media;
import com.nikitaend.instafeed.sola.instagram.util.PaginationIterator;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {
    
    public static final String TAG = "#AppInTheAir";
    private ArrayList<String> imagesUrlArray = new ArrayList<>();
    private PicassoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        try {
            makeInstagramView();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void makeInstagramView() throws Exception {
        final InstagramAuthentication auth = new InstagramAuthentication();
        auth.setClientId("");
        auth.setClientSecret("");
        auth.setRedirectUri("http://localhost");

        final AccessToken token = new AccessToken("");
        auth.setScope("comments");

        final InstagramSession session = new InstagramSession(token);

        final HorizontalListView horizontalListView;
        horizontalListView = (HorizontalListView) findViewById(R.id.HorizontalListView);
        mAdapter = new PicassoAdapter(getApplicationContext(),
                R.layout.item_preview, imagesUrlArray);
        horizontalListView.setAdapter(mAdapter);
        
        getRecentMediaForTag(session, "home", 6);

        
        CircleButton circleButton = (CircleButton) findViewById(R.id.circle_button);
        circleButton.bringToFront();
        circleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        FlexibleSpaceWithImageListViewInstagramActivity.class);

                intent.putExtra("auth", auth);
                intent.putExtra("token", token);
                // intent.putExtra("search_tag", TAG);
                
                startActivity(intent);
            }
        });
    }

    public void getRecentMediaForTag(final InstagramSession instagramSession,
                                                           String tagName, int count) throws Exception {

        
        
        tagName = tagName.replaceAll("^#*", "");
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("tag_name", tagName);
        String uri = instagramSession.uriConstructor.constructUri(
                UriFactory.Tags.GET_RECENT_TAGED_MEDIA, map, true) + "&&count=" + count;

        
        ArrayList<Media> media = new ArrayList<Media>();
        PaginationIterator<Media> iterator =  new PaginationIterator<Media>(media, uri, count) {
            @Override
            public void handleLoad(JSONArray mediaItems) throws JSONException {
                for (int i = 0; i < mediaItems.length(); i++) {
                    list.add(Media.fromJSON(mediaItems.getJSONObject(i), 
                            instagramSession.getAccessToken()));
                    mAdapter.add(list.get(i).getThumbnailImage().getUri());
                }
            }
        };
    }
    
    private String authenticationCodeUrl(InstagramAuthentication auth) {
        String authUrl = null;
        
        try {
            authUrl = auth.setRedirectUri("http://localhost") // redirect url
            .setClientSecret("c38046f2f8ff49a8b646717305a6efd0") // app secret
            .setClientId("a1dbb86cf84d4eabbc80b4e43e2aa1ac") // client id
            .setScope("comments")
            .getAuthorizationUri();
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return authUrl;
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
