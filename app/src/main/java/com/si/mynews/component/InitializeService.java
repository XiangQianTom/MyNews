package com.si.mynews.component;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.orhanobut.logger.Logger;
import com.si.mynews.app.Constants;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import si.mynews.R;

import static com.si.mynews.app.App.mContext;


public class InitializeService extends IntentService {

    private static final String ACTION_INIT = "initApplication";

    public InitializeService() {
        super("InitializeService");
    }

    public static void start() {
        Intent intent = new Intent(mContext, InitializeService.class);
        intent.setAction(ACTION_INIT);
        mContext.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT.equals(action)) {
                initApplication();
            }
        }
    }

    private void initApplication() {
        //初始化日志
        Logger.init(getPackageName()).hideThreadInfo();

        //初始化错误收集
        initBugly();

        //初始化内存泄漏检测
//        LeakCanary.install(App.getInstance());

        //初始化过度绘制检测
//        BlockCanary.install(getApplicationContext(), new BlockCanaryContext()).start();
//        BlockCanary.install(getApplicationContext(), new BlockCanaryContext()).upload();

        //初始化tbs x5 webview
        initX5WebView();

        initIflytek();
    }

    private void initIflytek() {
        SpeechUtility.createUtility(mContext, "appid=" + getString(R.string.app_id) + "," + SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
    }

    private void initX5WebView() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        //TbsDownloader.needDownload(getApplicationContext(), false);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Log.d("app", "onDownloadFinish");
            }

            @Override
            public void onInstallFinish(int i) {
            }

            @Override
            public void onDownloadProgress(int i) {
            }
        });
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    private void initBugly() {
        CrashReport.initCrashReport(mContext, Constants.BUGLY_ID, true);
    }
}
