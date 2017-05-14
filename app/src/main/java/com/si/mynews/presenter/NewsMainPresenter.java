package com.si.mynews.presenter;

import com.si.mynews.app.Constants;
import com.si.mynews.base.RxPresenter;
import com.si.mynews.component.RxBus;
import com.si.mynews.model.bean.NewsManagerBean;
import com.si.mynews.model.bean.NewsManagerItemBean;
import com.si.mynews.model.db.RealmHelper;
import com.si.mynews.presenter.contract.NewsMainContract;
import com.si.mynews.util.RxUtil;
import com.si.mynews.util.SharedPreferenceUtil;

import javax.inject.Inject;

import io.realm.RealmList;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by si on 16/11/28.
 */

public class NewsMainPresenter extends RxPresenter<NewsMainContract.View> implements NewsMainContract.Presenter{

    private RealmHelper mRealmHelper;
    private RealmList<NewsManagerItemBean> mList;

    @Inject
    public NewsMainPresenter(RealmHelper mRealHelper) {
        this.mRealmHelper = mRealHelper;
        registerEvent();
    }

    private void registerEvent() {
        Subscription subscribe = RxBus.getDefault().toObservable(NewsManagerBean.class)
                .compose(RxUtil.<NewsManagerBean>rxSchedulerHelper())
                .subscribe(new Action1<NewsManagerBean>() {
                    @Override
                    public void call(NewsManagerBean newsManagerBean) {
                        mRealmHelper.updateNewsManagerList(newsManagerBean);
                        mView.updateTab(newsManagerBean.getManagerList());
                    }
                });
        addSubscrebe(subscribe);
    }

    @Override
    public void initManagerList() {
        if (SharedPreferenceUtil.getManagerPoint()) {
            //第一次使用，生成默认ManagerList
            initList();
            mRealmHelper.updateNewsManagerList(new NewsManagerBean(mList));
            mView.updateTab(mList);
        } else {
            if (mRealmHelper.getNewsManagerList() == null) {
                initList();
                mRealmHelper.updateNewsManagerList(new NewsManagerBean(mList));
            } else {
                mList = mRealmHelper.getNewsManagerList().getManagerList();
            }
            mView.updateTab(mList);
        }
    }

    @Override
    public void setManagerList() {
        mView.jumpToManager(mRealmHelper.getNewsManagerList());
    }

    private void initList() {
        mList = new RealmList<>();
        mList.add(new NewsManagerItemBean(0, true, "头条", getPageType("头条")));
        mList.add(new NewsManagerItemBean(1, true, "新闻", getPageType("新闻")));
        mList.add(new NewsManagerItemBean(2, true, "财经", getPageType("财经")));
        mList.add(new NewsManagerItemBean(3, true, "体育", getPageType("体育")));
        mList.add(new NewsManagerItemBean(4, true, "娱乐", getPageType("娱乐")));
        mList.add(new NewsManagerItemBean(5, true, "军事", getPageType("军事")));
        mList.add(new NewsManagerItemBean(6, true, "教育", getPageType("教育")));
        mList.add(new NewsManagerItemBean(8, true, "NBA", getPageType("NBA")));
        mList.add(new NewsManagerItemBean(9, true, "股票", getPageType("股票")));
        mList.add(new NewsManagerItemBean(10, true, "星座", getPageType("星座")));
        mList.add(new NewsManagerItemBean(11, true, "女性", getPageType("女性")));
        mList.add(new NewsManagerItemBean(12, true, "健康", getPageType("健康")));
    }

    private int getPageType(String newsType) {
        switch (newsType) {
            case "头条":
            case "财经":
            case "体育":
            case "娱乐":
            case "军事":
            case "科技":
                return Constants.TYPE_SCROLL;
            default:
                return Constants.TYPE_ITEM;
        }
    }
}
