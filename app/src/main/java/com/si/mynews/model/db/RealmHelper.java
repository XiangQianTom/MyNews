package com.si.mynews.model.db;

import android.content.Context;

import com.si.mynews.model.bean.NewsManagerBean;
import com.si.mynews.model.bean.RealmLikeBean;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by si on 16/8/16.
 */

public class RealmHelper {

    private static final String DB_NAME = "myRealm.realm";

    private Realm mRealm;

    public RealmHelper(Context mContext) {
        mRealm = Realm.getInstance(new RealmConfiguration.Builder(mContext)
                .deleteRealmIfMigrationNeeded()
                .name(DB_NAME)
                .build());
    }

    /**
     * 更新 新闻首页管理列表
     *
     * @param bean
     */
    public void updateNewsManagerList(NewsManagerBean bean) {
        NewsManagerBean data = mRealm.where(NewsManagerBean.class).findFirst();
        mRealm.beginTransaction();
        if (data != null) {
            data.deleteFromRealm();
        }
        mRealm.copyToRealm(bean);
        mRealm.commitTransaction();
    }

    /**
     * 获取 新闻首页管理列表
     *
     * @return
     */
    public NewsManagerBean getNewsManagerList() {
        NewsManagerBean bean = mRealm.where(NewsManagerBean.class).findFirst();
        if (bean == null)
            return null;
        return mRealm.copyFromRealm(bean);
    }

    /**
     * 查询 收藏记录
     *
     * @param id
     * @return
     */
    public boolean queryLikeId(String id) {
        RealmResults<RealmLikeBean> results = mRealm.where(RealmLikeBean.class).findAll();
        for (RealmLikeBean item : results) {
            if (item.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除 收藏记录
     *
     * @param id
     */
    public void deleteLikeBean(String id) {
        RealmLikeBean data = mRealm.where(RealmLikeBean.class).equalTo("id", id).findFirst();
        mRealm.beginTransaction();
        if (data != null) {
            data.deleteFromRealm();
        }
        mRealm.commitTransaction();
    }

    /**
     * 增加 收藏记录
     *
     * @param bean
     */
    public void insertLikeBean(RealmLikeBean bean) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(bean);
        mRealm.commitTransaction();
    }

    public List<RealmLikeBean> getLikeList() {
        //使用findAllSort ,先findAll再result.sort无效
        RealmResults<RealmLikeBean> results = mRealm.where(RealmLikeBean.class).findAllSorted("time");
        return mRealm.copyFromRealm(results);
    }

    /**
     * 修改 收藏记录 时间戳以重新排序
     *
     * @param id
     * @param time
     * @param isPlus
     */
    public void changeLikeTime(String id, long time, boolean isPlus) {
        RealmLikeBean bean = mRealm.where(RealmLikeBean.class).equalTo("id", id).findFirst();
        mRealm.beginTransaction();
        if (isPlus) {
            bean.setTime(++time);
        } else {
            bean.setTime(--time);
        }
        mRealm.commitTransaction();
    }
}
