package com.si.mynews.ui;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.si.mynews.app.App;
import com.si.mynews.app.Constants;
import com.si.mynews.base.BaseActivity;
import com.si.mynews.fragment.AboutFragment;
import com.si.mynews.fragment.JokeFragment;
import com.si.mynews.fragment.LikeFragment;
import com.si.mynews.fragment.NewsMainFragment;
import com.si.mynews.fragment.WechatMainFragment;
import com.si.mynews.presenter.MainPresenter;
import com.si.mynews.presenter.contract.MainContract;
import com.si.mynews.util.SharedPreferenceUtil;
import com.si.mynews.util.SnackbarUtil;
import com.si.mynews.util.SystemUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.BindView;
import me.yokeyword.fragmentation.SupportFragment;
import si.mynews.R;


/**
 * Created by si on 16/8/9.
 */

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    @BindView(R.id.drawer)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation)
    NavigationView mNavigationView;

    ActionBarDrawerToggle mDrawerToggle;
    NewsMainFragment mNewsFragment;
    JokeFragment mJokeFragment;
    WechatMainFragment mWechatFragment;
    LikeFragment mLikeFragment;
    AboutFragment mAboutFragment;

    MenuItem mLastMenuItem;

    private int hideFragment = Constants.TYPE_NEWS;
    private int showFragment = Constants.TYPE_NEWS;

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEventAndData() {
        setToolBar(mToolbar, "新闻专栏");
        mNewsFragment = new NewsMainFragment();
        mWechatFragment = new WechatMainFragment();
        mJokeFragment = new JokeFragment();
        mLikeFragment = new LikeFragment();
        mAboutFragment = new AboutFragment();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mLastMenuItem = mNavigationView.getMenu().findItem(R.id.drawer_news);
        loadMultipleRootFragment(R.id.fl_main_content, 0, mNewsFragment, mWechatFragment, mJokeFragment, mLikeFragment, mAboutFragment);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.drawer_news:
                        showFragment = Constants.TYPE_NEWS;
                        break;
                    case R.id.drawer_wechat:
                        showFragment = Constants.TYPE_WECHAT;
                        break;
                    case R.id.drawer_joke:
                        showFragment = Constants.TYPE_JOKE;
                        break;
                    case R.id.drawer_like:
                        showFragment = Constants.TYPE_LIKE;
                        break;
                    case R.id.drawer_about:
                        showFragment = Constants.TYPE_ABOUT;
                        break;
                }
                if (mLastMenuItem != null) {
                    mLastMenuItem.setChecked(false);
                }
                mLastMenuItem = menuItem;
                SharedPreferenceUtil.setCurrentItem(showFragment);
                menuItem.setChecked(true);
                mToolbar.setTitle(menuItem.getTitle());
                mDrawerLayout.closeDrawers();
                showHideFragment(getTargetFragment(showFragment), getTargetFragment(hideFragment));
                hideFragment = showFragment;
                return true;
            }
        });
        if (!SharedPreferenceUtil.getVersionPoint() && SystemUtil.isWifiConnected()) {
            SharedPreferenceUtil.setVersionPoint(true);
            try {
                PackageManager pm = getPackageManager();
                PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
                String versionName = pi.versionName;
                mPresenter.checkVersion(versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showError(String msg) {
        SnackbarUtil.showShort(mToolbar, msg);
    }

    @Override
    public void onBackPressedSupport() {
        showExitDialog();
    }

    private void showExitDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定退出吗");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                App.getInstance().exitApp();
            }
        });
        builder.show();
    }

    private SupportFragment getTargetFragment(int item) {
        switch (item) {
            case Constants.TYPE_NEWS:
                return mNewsFragment;
            case Constants.TYPE_WECHAT:
                return mWechatFragment;
            case Constants.TYPE_JOKE:
                return mJokeFragment;
            case Constants.TYPE_LIKE:
                return mLikeFragment;
            case Constants.TYPE_ABOUT:
                return mAboutFragment;
        }
        return mNewsFragment;
    }

    @Override
    public void showUpdateDialog(String versionContent) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("检测到新版本!");
        builder.setMessage(versionContent);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("马上更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkPermissions();
            }
        });
        builder.show();
    }

    @Override
    public void startDownloadService() {
        //startService(new Intent(mContext, UpdateService.class));
    }

    public void checkPermissions() {
        mPresenter.checkPermissions(new RxPermissions(this));
    }
}
