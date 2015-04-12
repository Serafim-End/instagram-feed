package com.nikitaend.instafeed.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nikitaend.instafeed.Adapter.PicassoAdapterFeed;
import com.nikitaend.instafeed.Observable.BaseActivity;
import com.nikitaend.instafeed.Observable.ObservableListView;
import com.nikitaend.instafeed.Observable.ObservableScrollViewCallbacks;
import com.nikitaend.instafeed.Observable.ScrollState;
import com.nikitaend.instafeed.Observable.ScrollUtils;
import com.nikitaend.instafeed.R;
import com.nikitaend.instafeed.Volley.MyVolley;
import com.nikitaend.instafeed.sola.instagram.InstagramSession;
import com.nikitaend.instafeed.sola.instagram.auth.AccessToken;
import com.nikitaend.instafeed.sola.instagram.auth.InstagramAuthentication;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

/**
 * @author Endaltsev Nikita
 *         start at 10.04.15.
 */
public class FlexibleSpaceWithImageListViewInstagramActivity extends BaseActivity
        implements ObservableScrollViewCallbacks {
    
    public static final int RESULTS_PAGE_SIZE = ImageRefresher.RESULTS_PAGE_SIZE;
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = false;
    public static final String TAG = ImageRefresher.TAG;
    private String currentMaxID = null;


    private final ArrayList<Holder> imagesUrlArray = new ArrayList<>();
    private PicassoAdapterFeed picassoAdapterFeed;
    private ObservableListView listView;

    private View mToolbar;
    private View mImageView;
    private View mOverlayView;
    private View mListBackgroundView;
    private TextView mTitleView;
    private View mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private int mToolbarColor;
    private boolean mFabIsShown;

    private boolean mInError = false;
    
    InstagramSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String COMPANY_NAME = "AppInTheAir";
        
        Intent activityThatCalled = getIntent();
        InstagramAuthentication auth = 
                (InstagramAuthentication) activityThatCalled.getSerializableExtra("auth");
        
        AccessToken token = (AccessToken) activityThatCalled.getSerializableExtra("token");  
        session = new InstagramSession(token);
        
        setContentView(R.layout.activity_flexiblespacewithimageinstagramlistview);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mFlexibleSpaceImageHeight =
                getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset =
                getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);

        mActionBarSize = getActionBarSize();
        mToolbarColor = getResources().getColor(R.color.primary);

        mToolbar = findViewById(R.id.toolbar);
        if (!TOOLBAR_IS_STICKY) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }
        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);

        listView = (ObservableListView) findViewById(R.id.list);
        listView.setScrollViewCallbacks(this);

        // Set padding view for ListView. This is the flexible space.
        View paddingView = new View(this);
        AbsListView.LayoutParams lp =
                new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                        mFlexibleSpaceImageHeight);
        paddingView.setLayoutParams(lp);

        // This is required to disable header's list selector effect
        paddingView.setClickable(true);

        listView.addHeaderView(paddingView);

        setInstagramData(listView);

        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(COMPANY_NAME);
        setTitle(null);

        mFab = findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int targetUserId = 1384495263; // app on the air id
                try {
                    // session.modifyRelationship(targetUserId, Relationship.Action.FOLLOW);
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/appintheair")));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                           Uri.parse("http://instagram.com/appintheair")));
                }
            }
            
            
        });
        
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        ViewHelper.setScaleX(mFab, 0);
        ViewHelper.setScaleY(mFab, 0);

        // mListBackgroundView makes ListView's background except header view.
        mListBackgroundView = findViewById(R.id.list_background);
        final View contentView = getWindow().getDecorView().findViewById(android.R.id.content);
        contentView.post(new Runnable() {
            @Override
            public void run() {
                // mListBackgroundView's should fill its parent vertically
                // but the height of the content view is 0 on 'onCreate'.
                // So we should get it with post().
                mListBackgroundView.getLayoutParams().height = contentView.getHeight();
            }
        });
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Translate list background
        ViewHelper.setTranslationY(mListBackgroundView, Math.max(0, -scrollY + mFlexibleSpaceImageHeight));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        setPivotXToTitle();
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        if (TOOLBAR_IS_STICKY) {
            titleTranslationY = Math.max(0, titleTranslationY);
        }
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
                mActionBarSize - mFab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            mFab.requestLayout();
        } else {
            ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
            ViewHelper.setTranslationY(mFab, fabTranslationY);
        }

        // Show/hide FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }

        if (TOOLBAR_IS_STICKY) {
            // Change alpha of toolbar background
            if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(1, mToolbarColor));
            } else {
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, mToolbarColor));
            }
        } else {
            // Translate Toolbar
            if (scrollY < mFlexibleSpaceImageHeight) {
                ViewHelper.setTranslationY(mToolbar, 0);
            } else {
                ViewHelper.setTranslationY(mToolbar, -scrollY);
            }
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle() {
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(mTitleView, findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(mTitleView, 0);
        }
    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }

    private void setInstagramData(ObservableListView listView) {
        try {
            makeInstagramView(listView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void makeInstagramView(ObservableListView listView) throws Exception {
        
        MyVolley.init(getApplicationContext());
        picassoAdapterFeed = new PicassoAdapterFeed(getApplicationContext(),
                R.layout.item_feed, imagesUrlArray, MyVolley.getImageLoader());
        listView.setAdapter(picassoAdapterFeed);
        
        ImageRefresher imageRefresher = 
                new ImageRefresher(session, imagesUrlArray, picassoAdapterFeed,
                        listView, FlexibleSpaceWithImageListViewInstagramActivity.this,
                        currentMaxID, mInError);

        imageRefresher.loadPage(listView, TAG, RESULTS_PAGE_SIZE);

    }

}

