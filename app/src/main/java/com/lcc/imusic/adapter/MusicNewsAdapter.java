package com.lcc.imusic.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lcc.imusic.R;
import com.lcc.imusic.bean.ActivityBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lcc_luffy on 2016/3/23.
 */
public class MusicNewsAdapter extends SimpleAdapter<MusicNewsAdapter.Holder, ActivityBean> {

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        super.onCreateViewHolder(parent, viewType);
        return new Holder(inflater.inflate(R.layout.item_music_news, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.bind(data.get(position));
    }


    class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.musicNews_title)
        public TextView title;

        @BindView(R.id.musicNews_subtitle)
        public TextView description;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(ActivityBean musicNews) {
            title.setText(musicNews.title);

            description.setText(Html.fromHtml(musicNews.content));
        }
    }
}
