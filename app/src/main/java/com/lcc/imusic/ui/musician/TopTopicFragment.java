package com.lcc.imusic.ui.musician;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lcc.imusic.R;
import com.lcc.imusic.adapter.ClubAdapter;
import com.lcc.imusic.base.fragment.AttachFragment;
import com.lcc.imusic.bean.Msg;
import com.lcc.imusic.bean.TopTopic;
import com.lcc.imusic.manager.NetManager_;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.stateLayout.StateLayout;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by lcc_luffy on 2016/3/8.
 */
public class TopTopicFragment extends AttachFragment implements LoadMoreFooterModel.LoadMoreListener {

    @BindView(R.id.stateLayout)
    StateLayout stateLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    ClubAdapter adapter;
    LoadMoreFooterModel footerModel;


    private int currentPage = 1;

    @Override
    public void initialize(@Nullable Bundle savedInstanceState) {
        stateLayout.setInfoContentViewMargin(0, -275, 0, 0);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ClubAdapter();
        footerModel = adapter.getFooterModel();
        recyclerView.setAdapter(adapter);

        stateLayout.setEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(1);
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                getData(1);
            }
        });



        footerModel.setLoadMoreListener(this);
        getData(1);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234) {
            recyclerView.smoothScrollToPosition(0);
            currentPage = 1;
            getData(1);
        }
    }

    private void getData(final int pageNum) {
        if (adapter.isDataEmpty())
            stateLayout.showProgressView();
        NetManager_.API().topTopic(pageNum).enqueue(new Callback<Msg<TopTopic>>() {
            @Override
            public void onResponse(Call<Msg<TopTopic>> call, Response<Msg<TopTopic>> response) {
                TopTopic topTopic = response.body().Result;
                refreshLayout.setRefreshing(false);
                if (topTopic != null) {
                    if (pageNum == 1) {
                        adapter.setData(topTopic.list);
                    } else {
                        if (topTopic.list.isEmpty()) {
                            footerModel.noMoreData();
                        } else {
                            adapter.addData(topTopic.list);
                        }
                    }
                }

                if (adapter.isDataEmpty()) {
                    stateLayout.showEmptyView("TA还没有粉丝团呢");
                } else {
                    stateLayout.showContentView();
                }
            }

            @Override
            public void onFailure(Call<Msg<TopTopic>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                if (adapter.isDataEmpty()) {
                    stateLayout.showErrorView(t.toString());
                } else {
                    footerModel.noMoreData(t.toString());
                }
            }
        });
    }

    @Override
    public void onPlayingIndexChange(int index) {
        super.onPlayingIndexChange(index);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter = null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_top_topic;
    }

    @Override
    public String toString() {
        return "粉丝团";
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        getData(currentPage);
    }


}
