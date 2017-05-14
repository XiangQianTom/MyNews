package com.si.mynews.app;

import android.os.Environment;

import java.io.File;

/**
 * Created by si on 2016/8/3.
 */
public class Constants {

    //================= TYPE ====================

    public static final int TYPE_NEWS = 101;

    public static final int TYPE_LIKE = 102;

    public static final int TYPE_ABOUT = 103;

    public static final int TYPE_JOKE = 104;

    public static final int TYPE_WECHAT = 105;

    //================= KEY ====================
    public static final String KEY_API_JISU = "8b25b99b079c7d2b";

    public static final String KEY_API_JUHE = "d1137c713f1e443946edd850aff8eed4";

    public static final String KEY_API_TIANXING = "52b7ec3471ac3bec6846577e79f20e4c";

    public static final String BUGLY_ID = "2a600fd01d";

    //================= PATH ====================

    public static final String PATH_DATA = App.getInstance().getCacheDir().getAbsolutePath() + File.separator + "data";

    public static final String PATH_CACHE = PATH_DATA + "/NetCache";

    public static final String PATH_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "si" + File.separator + "OwnNews";

    //================= PREFERENCE ====================


    public static final String SP_NO_IMAGE = "no_image";

    public static final String SP_AUTO_CACHE = "auto_cache";

    public static final String SP_CURRENT_ITEM = "current_item";

    public static final String SP_LIKE_POINT = "like_point";

    public static final String SP_VERSION_POINT = "version_point";

    public static final String SP_MANAGER_POINT = "manager_point";

    //================= INTENT ====================

    public static final String IT_DETAIL_TITLE = "title";

    public static final String IT_DETAIL_URL = "url";

    public static final String IT_DETAIL_IMG_URL = "img_url";

    public static final String IT_DETAIL_ID = "id";

    public static final String IT_DETAIL_TYPE = "type";

    public static final String IT_NEWS_TYPE = "news_type";

    public static final String IT_PAGE_TYPE = "page_type";

    public static final String IT_NEWS_MANAGER = "manager";

    public static final int TYPE_SCROLL = 1;
    public static final int TYPE_ITEM = 2;
    public static final int TYPE_CARD = 3;
    public static final String NEWSBEAN = "newsbean";
    public static final String TOPNEWSBEAN = "topnewsbean";
}
