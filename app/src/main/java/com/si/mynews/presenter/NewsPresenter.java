package com.si.mynews.presenter;

import android.util.Log;

import com.si.mynews.base.RxPresenter;
import com.si.mynews.model.bean.NewsListBean;
import com.si.mynews.model.bean.NewsTopListBean;
import com.si.mynews.model.http.RetrofitHelper;
import com.si.mynews.model.http.response.JiSuHttpResponse;
import com.si.mynews.presenter.contract.NewsContract;
import com.si.mynews.util.RxUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by si on 16/11/27.
 */

public class NewsPresenter extends RxPresenter<NewsContract.View> implements NewsContract.Presenter {

    private static final int NUM_EACH_PAGE = 20;
    private static final int NUM_HOT_LIMIT = 3;
    private static final String TAG = NewsPresenter.class.getSimpleName();

    private RetrofitHelper mRetrofitHelper;
    private List<NewsListBean.ListBean> totalList = new ArrayList<>();
    private boolean isHotList = true;
    private int currentPage = 0;
    private String mType;
    private static final int INTERVAL_INSTANCE = 6;
    private Subscription intervalSubscription;
    private int topCount = 0;
    private int currentTopCount = 0;

    @Inject
    public NewsPresenter(RetrofitHelper mRetrofitHelper) {
        this.mRetrofitHelper = mRetrofitHelper;
    }

    @Override
    public void getNewsTopData(String newsType) {
        getScrollTopData(newsType);
    }

    @Override
    public void startInterval() {
        intervalSubscription = Observable.interval(INTERVAL_INSTANCE, TimeUnit.SECONDS)
                .compose(RxUtil.<Long>rxSchedulerHelper())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (currentTopCount == topCount)
                            currentTopCount = 0;
                        mView.doInterval(currentTopCount++);
                    }
                });
        addSubscrebe(intervalSubscription);
    }

    @Override
    public void stopInterval() {
        if (intervalSubscription != null) {
            intervalSubscription.unsubscribe();
        }
    }

    @Override
    public void getNewsData(String type) {
        mType = type;
        currentPage = 0;
        totalList.clear();
        Subscription subscribe = mRetrofitHelper.fetchNewsList(mType, 0, NUM_EACH_PAGE).compose(RxUtil.<JiSuHttpResponse<NewsListBean>>rxSchedulerHelper())
                .compose(RxUtil.<NewsListBean>handleNewsResult())
                .subscribe(new Action1<NewsListBean>() {
                    @Override
                    public void call(NewsListBean newsListBean) {
                        List<NewsListBean.ListBean> newsList = newsListBean.getList();
                        totalList.addAll(newsList);
                        mView.showContent(totalList);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mView.showError("数据加载失败ヽ(≧Д≦)ノ");
                    }
                });
        addSubscrebe(subscribe);
    }

    @Override
    public void getMoreNewsData() {
        Subscription subscribe = mRetrofitHelper.fetchNewsList(mType, NUM_EACH_PAGE * (++currentPage), NUM_EACH_PAGE)
                .compose(RxUtil.<JiSuHttpResponse<NewsListBean>>rxSchedulerHelper())
                .compose(RxUtil.<NewsListBean>handleNewsResult())
                .subscribe(new Action1<NewsListBean>() {
                    @Override
                    public void call(NewsListBean newsListBean) {
                        List<NewsListBean.ListBean> newsListBeanList = newsListBean.getList();
                        totalList.addAll(newsListBeanList);
                        mView.showMoreContent(totalList, totalList.size(), totalList.size() + NUM_EACH_PAGE);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "getMoreNewsData\t" + throwable.getMessage());
                        mView.showError("数据加载失败ヽ(≧Д≦)ノ");
                    }
                });
        addSubscrebe(subscribe);
    }

    private void getScrollTopData(String newsType) {
        switch (newsType) {
            case "头条":
                getScrollTopList("top");
                break;
            case "财经":
                getScrollTopList("caijing");
                break;
            case "体育":
                getScrollTopList("tiyu");
                break;
            case "娱乐":
                getScrollTopList("yule");
                break;
            case "军事":
                getScrollTopList("junshi");
                break;
            case "科技":
                getScrollTopList("keji");
                break;
        }
    }

    private void getScrollTopList(String type) {
        Subscription subscribe = mRetrofitHelper.fetchNewsList(type)
                .compose(RxUtil.<JiSuHttpResponse<NewsTopListBean>>rxSchedulerHelper())
                .compose(RxUtil.<NewsTopListBean>handleNewsResult()).subscribe(new Action1<NewsTopListBean>() {
                    @Override
                    public void call(NewsTopListBean newsTopListBeen) {
                        List<NewsTopListBean.DataBean> data = newsTopListBeen.getData();
                        topCount = data.size();
                        mView.showTopContent(data);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "getScrollTopList\t" + throwable.getMessage());
                        mView.showError("数据加载失败ヽ(≧Д≦)ノ");
                    }
                });
        addSubscrebe(subscribe);
    }
}
