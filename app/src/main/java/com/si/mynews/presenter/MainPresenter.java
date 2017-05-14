package com.si.mynews.presenter;

import android.Manifest;

import com.si.mynews.base.RxPresenter;
import com.si.mynews.model.http.RetrofitHelper;
import com.si.mynews.presenter.contract.MainContract;
import com.tbruyelle.rxpermissions.RxPermissions;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by si on 16/8/9.
 */

public class MainPresenter extends RxPresenter<MainContract.View> implements MainContract.Presenter{

    private RetrofitHelper mRetrofitHelper;

    @Inject
    public MainPresenter(RetrofitHelper mRetrofitHelper) {
        this.mRetrofitHelper = mRetrofitHelper;

    }

    @Override
    public void checkVersion(final String currentVersion) {

    }

    @Override
    public void checkPermissions(RxPermissions rxPermissions) {
        Subscription rxSubscription = rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            mView.startDownloadService();
                        } else {
                            mView.showError("下载应用需要文件写入权限哦~");
                        }
                    }
                });
        addSubscrebe(rxSubscription);
    }
}
