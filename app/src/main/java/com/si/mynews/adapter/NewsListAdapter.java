package com.si.mynews.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.si.mynews.app.Constants;
import com.si.mynews.component.ImageLoader;
import com.si.mynews.model.bean.NewsListBean;
import com.si.mynews.model.bean.NewsTopListBean;
import com.si.mynews.widget.NewsDiffCallback;
import com.si.mynews.widget.SquareImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import si.mynews.R;

/**
 * Created by si on 16/8/13.
 * <p>
 * 一开始打算用ScrollView嵌套RecyclerView来实现
 * 但是RecyclerView23.1.1之后的版本嵌套会显示不全
 * Google也不推荐ScrollView嵌套RecyclerView
 * 还是采取getItemViewType来实现
 */

public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NewsListBean.ListBean> mList;
    private List<NewsTopListBean.DataBean> mTopList = new ArrayList<>();
    private LayoutInflater inflater;
    private Context mContext;
    private TopPagerAdapter mAdapter;
    private ViewPager topViewPager;
    private OnItemClickListener onItemClickListener;

    private int mPageType;

    public enum ITEM_TYPE {
        ITEM_TOP,       //滚动栏
        ITEM_CONTENT,    //条目内容
        CARD_CONTENT    //卡片内容
    }

    public NewsListAdapter(Context mContext, List<NewsListBean.ListBean> mList, int mPageType) {
        this.mList = mList;
        this.mContext = mContext;
        this.mPageType = mPageType;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        switch (mPageType) {
            case Constants.TYPE_SCROLL: {
                if (position == 0) {
                    return ITEM_TYPE.ITEM_TOP.ordinal();
                } else {
                    return ITEM_TYPE.ITEM_CONTENT.ordinal();
                }
            }
            case Constants.TYPE_CARD:
                return ITEM_TYPE.CARD_CONTENT.ordinal();
            default:
                return ITEM_TYPE.ITEM_CONTENT.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TOP.ordinal()) {
            mAdapter = new TopPagerAdapter(mContext, mTopList);
            return new TopViewHolder(inflater.inflate(R.layout.item_top, parent, false));
        }
        return new ContentViewHolder(inflater.inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentViewHolder) {
            int contentPosition = position;
            if (mPageType == Constants.TYPE_SCROLL) {
                contentPosition = position - 1;
            }
            ((ContentViewHolder) holder).title.setText(mList.get(contentPosition).getTitle());
            ImageLoader.load(mContext, mList.get(contentPosition).getPic(), ((ContentViewHolder) holder).image);
            final int finalContentPosition = contentPosition;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        ImageView iv = (ImageView) view.findViewById(R.id.iv_news_item_image);
                        onItemClickListener.onItemClick(finalContentPosition, iv);
                    }
                }
            });
        } else {
            ((TopViewHolder) holder).vpTop.setAdapter(mAdapter);
            topViewPager = ((TopViewHolder) holder).vpTop;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_daily_item_title)
        TextView title;
        @BindView(R.id.iv_news_item_image)
        SquareImageView image;

        public ContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class TopViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vp_top)
        ViewPager vpTop;
        @BindView(R.id.ll_point_container)
        LinearLayout llContainer;

        public TopViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void updateData(List<NewsListBean.ListBean> infos) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new NewsDiffCallback(mList, infos), true);
        this.mList = infos;
        diffResult.dispatchUpdatesTo(this);
//        notifyDataSetChanged();
    }

    public void addNewsTopData(List<NewsTopListBean.DataBean> infos) {
        if (null != mAdapter) {
            mAdapter.addNewsTopData(infos);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void changeTopPager(int currentCount) {
        if (mPageType == Constants.TYPE_SCROLL && null != topViewPager) {
            topViewPager.setCurrentItem(currentCount);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }
}
