package com.lcc.imusic.ui.musician;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;
import com.lcc.imusic.R;
import com.lcc.imusic.adapter.LoadMoreAdapter;
import com.lcc.imusic.adapter.TopicReplyAdapter;
import com.lcc.imusic.base.activity.BaseActivity;
import com.lcc.imusic.bean.Msg;
import com.lcc.imusic.bean.TopicItem;
import com.lcc.imusic.bean.TopicReply;
import com.lcc.imusic.manager.NetManager_;
import com.lcc.imusic.manager.UserManager;
import com.lufficc.stateLayout.StateLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    @BindView(R.id.comment_progress)
    ProgressBar comment_progress;

    @BindView(R.id.commentEditText)
    EditText commentEditView;

    @BindView(R.id.commentSubmit)
    Button commentSubmitBtn;
    private long topicId;

    private int currentPage = 1;

    TopicReplyAdapter adapter;

    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TopicItem topicItem = (TopicItem) getIntent().getSerializableExtra("topic");
        topicId = topicItem.id;
        setTitle(topicItem.title);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TopicReplyAdapter();
        adapter.setLoadMoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        commentEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    commentSubmitBtn.setEnabled(false);
                } else {
                    commentSubmitBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        commentSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment();
            }
        });
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

    private void comment() {
        final String content = commentEditView.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        commentSubmitBtn.setVisibility(View.GONE);
        comment_progress.setVisibility(View.VISIBLE);
        NetManager_.API().replyToTopic(topicId, content).enqueue(new Callback<Msg<JsonObject>>() {
            @Override
            public void onResponse(Call<Msg<JsonObject>> call, Response<Msg<JsonObject>> response) {
                commentSubmitBtn.setVisibility(View.VISIBLE);
                comment_progress.setVisibility(View.GONE);
                Msg<JsonObject> msg = response.body();
                if (msg != null && msg.Code == 100) {
                    TopicReply.TopicReplyItem replyItem = new TopicReply.TopicReplyItem();
                    toast("评论成功");
                    replyItem.enable = 1;
                    replyItem.text = content;
                    replyItem.avatar = UserManager.avatarWithOutDomain();
                    replyItem.authorName = UserManager.username();
                    replyItem.topicid = (int) topicId;
                    replyItem.userid = UserManager.id();

                    if (dateFormat == null)
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm::ss", Locale.CHINA);
                    replyItem.addtime = dateFormat.format(new Date());
                    adapter.insert(0, replyItem);
                    recyclerView.smoothScrollToPosition(0);
                } else {
                    toast("评论失败," + (msg != null ? msg.Msg : null));
                }
            }

            @Override
            public void onFailure(Call<Msg<JsonObject>> call, Throwable t) {
                commentSubmitBtn.setVisibility(View.VISIBLE);
                comment_progress.setVisibility(View.GONE);
                toast("评论失败," + t.getMessage());
            }
        });
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
                refreshLayout.setRefreshing(false);
                if (topicReply != null) {
                    if (pageNum == 1) {
                        adapter.canLoadMore();
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
