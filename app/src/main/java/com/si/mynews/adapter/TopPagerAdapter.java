package com.si.mynews.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.si.mynews.app.Constants;
import com.si.mynews.component.ImageLoader;
import com.si.mynews.model.bean.NewsTopListBean;
import com.si.mynews.ui.NewsDetailActivity;

import java.util.List;

import si.mynews.R;

/**
 * Created by si on 16/8/13.
 */

public class TopPagerAdapter extends BasePageAdapter {

    public TopPagerAdapter(Context context, List<?> mList) {
        super(context, mList);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final NewsTopListBean.DataBean newsBean = (NewsTopListBean.DataBean) mList.get(position);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_top_pager, container, false);
        ImageView ivImage = (ImageView) view.findViewById(R.id.iv_top_image);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_top_title);
        ImageLoader.load(mContext, newsBean.getThumbnail_pic_s(), ivImage);
        tvTitle.setText(newsBean.getTitle());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, NewsDetailActivity.class);
                intent.putExtra(Constants.TOPNEWSBEAN, newsBean);
                mContext.startActivity(intent);
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void addNewsTopData(List<NewsTopListBean.DataBean> infos) {
        mList = infos;
    }
}
