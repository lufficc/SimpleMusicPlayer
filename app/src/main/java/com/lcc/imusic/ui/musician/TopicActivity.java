package com.lcc.imusic.ui.musician;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lcc.imusic.R;
import com.lcc.imusic.adapter.LoadMoreAdapter;
import com.lcc.imusic.adapter.TopicReplyAdapter;
import com.lcc.imusic.base.activity.BaseActivity;
import com.lcc.imusic.bean.Msg;
import com.lcc.imusic.bean.TopicItem;
import com.lcc.imusic.bean.TopicReply;
import com.lcc.imusic.manager.NetManager_;
import com.lcc.imusic.utils.Json;
import com.lufficc.stateLayout.StateLayout;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicActivity extends BaseActivity implements LoadMoreAdapter.LoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.stateLayout)
    StateLayout stateLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;


    private long topicId;

    private int currentPage = 1;

    TopicReplyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TopicItem topicItem = (TopicItem) getIntent().getSerializableExtra("topic");
        topicId = topicItem.id;
        setTitle(topicItem.title);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TopicReplyAdapter();
        adapter.setLoadMoreListener(this);
        recyclerView.setAdapter(adapter);


        stateLayout.setErrorAndEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        bind(topicItem);
        getTopicReplies(1);
    }

    private void bind(TopicItem topicItem) {

    }

    public void getTopicReplies(final int pageNum) {
        if (adapter.isDataEmpty())
            stateLayout.showProgressView();
        NetManager_.API().topicReplies(topicId, pageNum).enqueue(new Callback<Msg<TopicReply>>() {
            @Override
            public void onResponse(Call<Msg<TopicReply>> call, Response<Msg<TopicReply>> response) {
                TopicReply topicReply = response.body().Result;

                Logger.i(Json.toJson(topicReply));
                refreshLayout.setRefreshing(false);
                if (topicReply != null) {
                    if (pageNum == 1) {
                        adapter.setData(topicReply.list);
                    } else {
                        if (topicReply.list.isEmpty()) {
                            adapter.noMoreData();
                        } else {
                            adapter.addData(topicReply.list);
                        }
                    }
                }

                if (adapter.isDataEmpty()) {
                    stateLayout.showEmptyView("还没有评论");
                } else {
                    stateLayout.showContentView();
                }
            }

            @Override
            public void onFailure(Call<Msg<TopicReply>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                if (adapter.isDataEmpty()) {
                    stateLayout.showErrorView(t.toString());
                } else {
                    adapter.noMoreData(t.toString());
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        getTopicReplies(currentPage);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_topic;
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        getTopicReplies(1);
    }
}
