package com.lcc.imusic.ui.home;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lcc.imusic.R;
import com.lcc.imusic.adapter.MusicNewsAdapter;
import com.lcc.imusic.base.fragment.AttachFragment;
import com.lcc.imusic.bean.ActivityBean;
import com.lcc.imusic.bean.MusicActivity;
import com.lcc.imusic.manager.NetManager_;
import com.lcc.imusic.wiget.DefaultItemDecoration;
import com.lufficc.stateLayout.StateLayout;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lcc_luffy on 2016/3/8.
 */
public class MusicNewsFragment extends AttachFragment implements OnRefreshListener {
    @BindView(R.id.stateLayout)
    StateLayout stateLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    MusicNewsAdapter adapter;

    @Override
    public void initialize(@Nullable Bundle savedInstanceState) {
        super.initialize(savedInstanceState);
        refreshLayout.setOnRefreshListener(this);
        adapter = new MusicNewsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DefaultItemDecoration(
                ContextCompat.getColor(getContext(), R.color.icon_enabled),
                ContextCompat.getColor(getContext(), R.color.divider),
                getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin)
        ));
        getData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_list;
    }


    private void getData() {
        if (adapter.isDataEmpty())
            stateLayout.showProgressView();
        NetManager_.API().activities().enqueue(new Callback<MusicActivity>() {
            @Override
            public void onResponse(Call<MusicActivity> call, Response<MusicActivity> response) {
                stateLayout.showContentView();
                refreshLayout.setRefreshing(false);
                if (response.isSuccess()) {
                    List<ActivityBean> activityBeen = response.body().Result.list;
                    adapter.setData(activityBeen);
                }

            }

            @Override
            public void onFailure(Call<MusicActivity> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                stateLayout.showErrorView(t.toString());
            }
        });
    }

    @Override
    public String toString() {
        return "音乐资讯";
    }

    @Override
    public void onRefresh() {
        getData();
    }
}
