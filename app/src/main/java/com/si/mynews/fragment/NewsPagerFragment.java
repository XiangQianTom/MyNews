package com.si.mynews.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.si.mynews.adapter.NewsListAdapter;
import com.si.mynews.app.Constants;
import com.si.mynews.base.BaseFragment;
import com.si.mynews.model.bean.NewsListBean;
import com.si.mynews.model.bean.NewsTopListBean;
import com.si.mynews.presenter.NewsPresenter;
import com.si.mynews.presenter.contract.NewsContract;
import com.si.mynews.ui.NewsDetailActivity;
import com.si.mynews.util.SnackbarUtil;
import com.si.mynews.widget.NewsItemDecoration;
import com.si.mynews.widget.ProgressImageView;
import com.si.mynews.widget.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import si.mynews.R;

/**
 * Created by si on 16/11/27.
 */

public class NewsPagerFragment extends BaseFragment<NewsPresenter> implements NewsContract.View {

    @BindView(R.id.rv_content)
    RecyclerView rvNewsList;
    @BindView(R.id.iv_progress)
    ProgressImageView ivProgress;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private NewsListAdapter mAdapter;
    private NewsItemDecoration mDecoration;

    private boolean isLoadingMore = false;
    private String mNewsType;
    private int mPageType;
    private List<NewsListBean.ListBean> mList = new ArrayList<>();

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_common_list;
    }

    @Override
    protected void initEventAndData() {
        mNewsType = getArguments().getString(Constants.IT_NEWS_TYPE);
        mPageType = getArguments().getInt(Constants.IT_PAGE_TYPE);
        mAdapter = new NewsListAdapter(mContext, mList, mPageType);
        mAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View shareView) {
//                if(mAdapter.getIsBefore()) {
//                    mAdapter.notifyItemChanged(position + 1);
                Intent intent = new Intent();
                intent.setClass(mContext, NewsDetailActivity.class);
                intent.putExtra(Constants.NEWSBEAN, mList.get(position));
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(mActivity, shareView, "shareView");
                mContext.startActivity(intent, options.toBundle());
            }
        });
        rvNewsList.setLayoutManager(new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvNewsList.setAdapter(mAdapter);
        mDecoration = new NewsItemDecoration();
        rvNewsList.addItemDecoration(mDecoration);
        rvNewsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) rvNewsList.getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = rvNewsList.getLayoutManager().getItemCount();
                if (lastVisibleItem >= totalItemCount - 2 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        mPresenter.getMoreNewsData();
                    }
                }
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getNewsData(mNewsType);
            }
        });
        ivProgress.start();
        mPresenter.getNewsData(mNewsType);
        if (mPageType == Constants.TYPE_SCROLL) {
            mPresenter.getNewsTopData(mNewsType);
        }
    }

    @Override
    public void showContent(List<NewsListBean.ListBean> beanList) {
        Log.e("11111111","showContent\t"+beanList.size());
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        } else {
            ivProgress.stop();
        }
        mList = beanList;
        mAdapter.updateData(beanList);
        mAdapter.notifyDataSetChanged();
    }

    public void showTopContent(List<NewsTopListBean.DataBean> newsTopListBean) {
        Log.e("11111111","showTopContent\t"+newsTopListBean.size());
        mAdapter.addNewsTopData(newsTopListBean);
        mPresenter.stopInterval();
        mPresenter.startInterval();
    }

    @Override
    public void doInterval(int currentCount) {
        mAdapter.changeTopPager(currentCount);
    }

    @Override
    public void showMoreContent(List<NewsListBean.ListBean> newsMoreListBean, int start, int end) {
        mAdapter.updateData(newsMoreListBean);
        mAdapter.notifyItemRangeInserted(start, end);
        isLoadingMore = false;
    }

    @Override
    public void showError(String msg) {
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        } else {
            ivProgress.stop();
        }
        SnackbarUtil.showShort(rvNewsList, msg);
    }
}
