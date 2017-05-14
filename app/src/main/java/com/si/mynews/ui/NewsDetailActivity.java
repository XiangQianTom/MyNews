package com.si.mynews.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.si.mynews.app.Constants;
import com.si.mynews.base.BaseActivity;
import com.si.mynews.component.ImageLoader;
import com.si.mynews.model.bean.NewsListBean;
import com.si.mynews.model.bean.NewsTopListBean;
import com.si.mynews.util.ShareUtil;
import com.si.mynews.util.SnackbarUtil;
import com.si.mynews.util.SystemUtil;
import com.si.mynews.widget.ProgressImageView;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import butterknife.BindView;
import butterknife.OnClick;
import si.mynews.R;

/**
 * Created by si on 16/8/13.
 */

public class NewsDetailActivity extends BaseActivity {

    @BindView(R.id.detail_bar_image)
    ImageView detailBarImage;
    @BindView(R.id.detail_bar_copyright)
    TextView detailBarCopyright;
    @BindView(R.id.view_toolbar)
    Toolbar viewToolbar;
    @BindView(R.id.clp_toolbar)
    CollapsingToolbarLayout clpToolbar;
    @BindView(R.id.wv_detail_content)
    WebView wvDetailContent;
    @BindView(R.id.iv_progress)
    ProgressImageView ivProgress;
    @BindView(R.id.nsv_scroller)
    NestedScrollView nsvScroller;
    @BindView(R.id.tv_detail_bottom_share)
    TextView tvDetailBottomShare;
    @BindView(R.id.ll_detail_bottom)
    FrameLayout llDetailBottom;
    @BindView(R.id.fab_like)
    FloatingActionButton fabLike;

    int id = 0;
    int allNum = 0;
    int shortNum = 0;
    int longNum = 0;
    String shareUrl;
    String imgUrl;
    boolean isBottomShow = true;
    boolean isImageShow = false;
    boolean isTransitionEnd = false;

    @Override
    protected void initInject() {
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_news_detail;
    }

    @Override
    protected void initEventAndData() {
        setToolBar(viewToolbar, "");
        NewsListBean.ListBean newsBean = getIntent().getParcelableExtra(Constants.NEWSBEAN);
        if (null != newsBean) {
            showNewsContent(newsBean);
        } else {
            NewsTopListBean.DataBean topNewsBean = getIntent().getParcelableExtra(Constants.TOPNEWSBEAN);
            showTopNewsContent(topNewsBean);
        }
        ivProgress.start();
        WebSettings settings = wvDetailContent.getSettings();
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        wvDetailContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (SystemUtil.isNetworkConnected()) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        }
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportZoom(true);
        wvDetailContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        nsvScroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY - oldScrollY > 0 && isBottomShow) {  //下移隐藏
                    isBottomShow = false;
                    llDetailBottom.animate().translationY(llDetailBottom.getHeight());
                } else if (scrollY - oldScrollY < 0 && !isBottomShow) {    //上移出现
                    isBottomShow = true;
                    llDetailBottom.animate().translationY(0);
                }
            }
        });
        (getWindow().getSharedElementEnterTransition()).addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                /**
                 * 测试发现部分手机(如红米note2)上加载图片会变形,没有达到centerCrop效果
                 * 查阅资料发现Glide配合SharedElementTransition是有坑的,需要在Transition动画结束后再加载图片
                 * https://github.com/TWiStErRob/glide-support/blob/master/src/glide3/java/com/bumptech/glide/supportapp/github/_847_shared_transition/DetailFragment.java
                 */
                isTransitionEnd = true;
                if (imgUrl != null) {
                    isImageShow = true;
                    ImageLoader.load(mContext, imgUrl, detailBarImage);
                }
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
    }

    private void showNewsContent(NewsListBean.ListBean listBean) {
        ivProgress.stop();
        imgUrl = listBean.getPic();
        shareUrl = listBean.getUrl();
        if (!isImageShow && isTransitionEnd) {
            ImageLoader.load(mContext, imgUrl, detailBarImage);
        }
        clpToolbar.setTitle(listBean.getTitle());
        detailBarCopyright.setText(listBean.getSrc());
        wvDetailContent.loadUrl(listBean.getUrl());
    }

    private void showTopNewsContent(NewsTopListBean.DataBean listBean) {
        ivProgress.stop();
        imgUrl = listBean.getThumbnail_pic_s();
        shareUrl = listBean.getUrl();
        if (!isImageShow && isTransitionEnd) {
            ImageLoader.load(mContext, imgUrl, detailBarImage);
        }
        clpToolbar.setTitle(listBean.getTitle());
        detailBarCopyright.setText(listBean.getAuthor_name());
        wvDetailContent.loadUrl(listBean.getUrl());
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wvDetailContent.canGoBack()) {
            wvDetailContent.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void showError(String msg) {
        ivProgress.stop();
        SnackbarUtil.showShort(getWindow().getDecorView(), msg);
    }

    @OnClick(R.id.tv_detail_bottom_share)
    void shareUrl() {
        ShareUtil.shareText(mContext, shareUrl, "分享一篇文章");
    }

    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            finishAfterTransition();
        }
    }
}
