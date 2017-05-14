package com.si.mynews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.si.mynews.model.bean.JokeListBean;

import java.util.List;

import si.mynews.R;

/**
 * Created by si on 16/8/13.
 */

public class JokePagerAdapter extends BasePageAdapter {

    private LastJokeListener lastJokeListener;
    private int lastJokeNum = 2;

    public JokePagerAdapter(Context context, List<?> mList) {
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
        JokeListBean.ListBean jokeBean = (JokeListBean.ListBean) mList.get(position);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_joke, container, false);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_joke_content);
        tvContent.setText(jokeBean.getContent());
        container.addView(view);

        if ((position > getCount() - lastJokeNum) && null != lastJokeListener) {
            lastJokeListener.isLastIng();
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void updateData(List<JokeListBean.ListBean> listBean) {
        mList = listBean;
    }

    public String getCurrentContent(int currentItem) {
        return ((JokeListBean.ListBean) mList.get(currentItem)).getContent();
    }

    public void setLastJokeListener(LastJokeListener lastJokeListener) {
        this.lastJokeListener = lastJokeListener;
    }

    public interface LastJokeListener {
        void isLastIng();
    }
}
