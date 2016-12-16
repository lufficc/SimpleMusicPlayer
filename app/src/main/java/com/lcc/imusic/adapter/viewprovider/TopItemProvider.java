package com.lcc.imusic.adapter.viewprovider;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.lcc.imusic.R;
import com.lcc.imusic.adapter.ClubAdapter;
import com.lcc.imusic.bean.Msg;
import com.lcc.imusic.bean.TopicItem;
import com.lcc.imusic.manager.NetManager_;
import com.lcc.imusic.ui.musician.PublishTopicActivity;
import com.lcc.imusic.ui.musician.TopicActivity;
import com.lufficc.lightadapter.ViewHolderProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lufficc on 2016/12/17.
 */

public class TopItemProvider extends ViewHolderProvider<TopicItem, TopItemProvider.Holder> {
    ClubAdapter adapter;

    public TopItemProvider(ClubAdapter clubAdapter) {
        adapter = clubAdapter;
    }

    @Override
    public Holder onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup parent) {
        return new Holder(layoutInflater.inflate(R.layout.item_club, parent, false));
    }

    @Override
    public void onBindViewHolder(final TopicItem topicItem, final Holder viewHolder, final int position) {
        viewHolder.bind(topicItem);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewHolder.itemView.getContext(), TopicActivity.class);
                intent.putExtra("topic", topicItem);
                viewHolder.itemView.getContext().startActivity(intent);
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Context context = viewHolder.itemView.getContext();
                AlertDialog dialog = new AlertDialog
                        .Builder(context)
                        .setItems(new String[]{"删除", "修改"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        NetManager_.API().deleteTopic(topicItem.id)
                                                .enqueue(new Callback<Msg<JsonObject>>() {
                                                    @Override
                                                    public void onResponse(Call<Msg<JsonObject>> call, Response<Msg<JsonObject>> response) {
                                                        if (response.body().Code == 100) {
                                                            adapter.removeData(position);
                                                        } else {
                                                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Msg<JsonObject>> call, Throwable t) {
                                                        Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                        break;
                                    case 1:
                                        Intent intent = new Intent(context, PublishTopicActivity.class);
                                        intent.putExtra("topicId", topicItem.id);
                                        intent.putExtra("title", topicItem.title);
                                        intent.putExtra("content", topicItem.text);
                                        intent.putExtra("type", PublishTopicActivity.TYPE_UPDATE);
                                        ((Activity) context).startActivityForResult(intent, 1234);
                                        break;
                                }
                                dialog.dismiss();
                            }
                        })

                        .create();
                dialog.show();
                return true;
            }
        });

    }

    class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.club_text)
        TextView clubText;

        @BindView(R.id.club_viewCount)
        TextView club_viewCount;

        @BindView(R.id.club_replyCount)
        TextView club_replyCount;

        @BindView(R.id.club_time)
        TextView club_time;

        @BindView(R.id.auth_name)
        TextView auth_name;


        @BindView(R.id.club_avatar)
        ImageView club_avatar;

        int topicColor;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            topicColor = itemView.getContext().getResources().getColor(R.color.colorPrimary);
        }


        private void bind(TopicItem topicItem) {
            String topic = "#" + topicItem.title + "# ";
            String finial = topic + topicItem.text;


            SpannableString spannableString = new SpannableString(finial);
            spannableString.setSpan(new ForegroundColorSpan(topicColor), 0, topic.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            clubText.setText(spannableString);
            club_replyCount.setText("回复:" + topicItem.replycount);
            club_viewCount.setText("查看:" + topicItem.viewscount);
            club_time.setText(topicItem.addtime);

            auth_name.setText(topicItem.authorNmae);

            Glide.with(itemView.getContext())
                    .load(NetManager_.DOMAIN + topicItem.avatar)
                    .placeholder(R.mipmap.user)
                    .error(R.mipmap.user)
                    .into(club_avatar);
            Log.i("main", NetManager_.DOMAIN + topicItem.avatar);
        }
    }

}
