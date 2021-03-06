package com.iboomboom.grace.base.fragment;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;


import com.iboomboom.grace.R;
import com.iboomboom.grace.base.BaseRecyclerAdapter;
import com.iboomboom.grace.base.RefreshType;
import com.iboomboom.grace.base.activity.BaseActivity;
import com.iboomboom.grace.view.BackTopRecyclerView;
import com.iboomboom.grace.view.RecyclerRefreshLayout;

/**
 * Created by DEVIN on 2017/9/25.
 */

public abstract class BaseListFragment extends BaseFragment implements BackTopRecyclerView.OnRecyclerViewScollToTopListener {

    public BackTopRecyclerView mRecyclerView;
    public RelativeLayout rlTopContainer;
    public RecyclerRefreshLayout mRefreshLayout;
    private BaseActivity parentActivity;

    private int page = 1;
    private int totalPage = 0;

    private boolean isRefreshing = true;

    @Override
    protected int getLayoutId() {
        return R.layout.abc_activity_base_recyclerview;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        rlTopContainer = view.findViewById(R.id.rl_top_container);
        mRecyclerView = view.findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (getAdapter() != null) {
            mRecyclerView.setAdapter(getAdapter());
        }

        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setEnabled(false);
    }


    @Override
    protected void initData() {
        super.initData();
        requestData(RefreshType.PULL_TO_REFRESH);
    }

    /**
     * 列表使用分页加载和下拉刷新功能
     */
    public void setRefresh() {
        mRefreshLayout.setCanLoadMore(true);
        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setSuperRefreshLayoutListener(new RecyclerRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                if (isRefreshing) return;
                isRefreshing = true;
                requestData(RefreshType.PULL_TO_REFRESH);
            }

            @Override
            public void onLoadMore() {
                if (page == totalPage) {
                    onRequestComplete();
                    return;
                }
                if (isRefreshing) return;
                isRefreshing = true;
                requestData(RefreshType.LOAD_MORE);
            }
        });
    }


    /**
     * 加载数据
     */
    protected void requestData(RefreshType refreshType) {

    }

    /**
     * 设置总页码数量
     *
     * @param pageNum
     */
    public void setTotalPage(int pageNum) {
        totalPage = pageNum;
    }

    public void onRequestComplete() {
        isRefreshing = false;
        mRefreshLayout.onComplete();
    }

    /**
     * 递增页数
     *
     * @param refreshType
     * @return
     */
    public int nextPage(RefreshType refreshType) {
        page = refreshType == RefreshType.PULL_TO_REFRESH ? 1 : page + 1;
        return page;
    }

    /**
     * 加载错误时，返回上一个页码
     *
     * @return
     */
    public int backPrePage() {
        page = page - 1;
        return page;
    }

    /**
     * 点击activity的标题返回顶部
     * 备注： 当fragment在viewpager中使用时,
     * 需要监听{@link ViewPager#addOnPageChangeListener(ViewPager.OnPageChangeListener)}
     * 并在onPageSelected方法中调用本方法。
     *
     * @param title 标题
     */
    public void clickTitleBackTop(String title) {
        mRecyclerView.setOnScrollToTopListener(this);
        parentActivity = (BaseActivity) getActivity();
        parentActivity.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.scrollToPosition(0);
            }
        });
        this.title = title;
        parentActivity.setTitle(this.title);
    }


    protected abstract BaseRecyclerAdapter getAdapter();

    @Override
    public void onScrollToTop() {
        parentActivity.setTitle("点击标题返回顶部");
    }

    @Override
    public void onScrollIdle() {
        parentActivity.setTitle(title);
    }
}
