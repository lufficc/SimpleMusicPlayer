package com.lcc.imusic.ui.home;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lcc.imusic.R;
import com.lcc.imusic.adapter.LoadMoreAdapter;
import com.lcc.imusic.adapter.OnItemClickListener;
import com.lcc.imusic.adapter.SimpleMusicListAdapter;
import com.lcc.imusic.base.fragment.AttachFragment;
import com.lcc.imusic.bean.MusicItem;
import com.lcc.imusic.data.music.SongsDataSource;
import com.lcc.imusic.data.music.SongsRepository;
import com.lcc.imusic.wiget.DefaultItemDecoration;
import com.lufficc.stateLayout.StateLayout;

import java.util.List;

import butterknife.BindView;

/**
 * Created by lcc_luffy on 2016/3/8.
 */
public class RemoteMusicFragment extends AttachFragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreAdapter.LoadMoreListener {
    @BindView(R.id.stateLayout)
    StateLayout stateLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    SimpleMusicListAdapter adapter;

    private int currentPageNum = 1;


    @Override
    public void initialize(@Nullable Bundle savedInstanceState) {
        refreshLayout.setColorSchemeResources(R.color.selectedRed);
        refreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DefaultItemDecoration(
                ContextCompat.getColor(getContext(), R.color.icon_enabled),
                ContextCompat.getColor(getContext(), R.color.divider),
                getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin)
        ));
        adapter = new SimpleMusicListAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                playMusic(position);
            }
        });
        stateLayout.setErrorAndEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });

        getData(1);
        adapter.setLoadMoreListener(this);
        adapter.canLoadMore();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.onDestroy();
        adapter = null;
    }


    @Override
    public void onPlayingIndexChange(int index) {
        if (adapter != null)
            adapter.playingIndexChangeTo(index);
    }

    public void getData(final int pageNum) {
        if (adapter.getItemCount() == 0)
            stateLayout.showProgressView();
        SongsRepository.getInstance().getSongs(pageNum, new SongsDataSource.LoadSongsCallback() {
            @Override
            public void onLoaded(List<MusicItem> musicItems) {
                refreshLayout.setRefreshing(false);
                if (pageNum == 1) {
                    adapter.canLoadMore();
                    adapter.setData(musicItems);
                } else {
                    if (musicItems.isEmpty()) {
                        adapter.noMoreData();
                    } else {
                        adapter.addData(musicItems);
                    }
                }
                if (adapter.isDataEmpty()) {
                    stateLayout.showEmptyView();
                } else {
                    stateLayout.showContentView();
                }
            }

            @Override
            public void onFailed(String msg) {
                if (adapter.isDataEmpty()) {
                    stateLayout.showErrorView("网络出错");
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_list;
    }

    @Override
    public String toString() {
        return "联网音乐";
    }

    @Override
    public void onRefresh() {
        currentPageNum = 1;
        getData(currentPageNum);
    }

    @Override
    public void onLoadMore() {
        currentPageNum++;
        getData(currentPageNum);
    }
}
