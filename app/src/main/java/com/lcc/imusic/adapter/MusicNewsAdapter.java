package com.lcc.imusic.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lcc.imusic.R;
import com.lcc.imusic.bean.MusicNews;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lcc_luffy on 2016/3/23.
 */
public class MusicNewsAdapter extends SimpleAdapter<MusicNewsAdapter.Holder, MusicNews> {

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
        @Bind(R.id.musicNews_pic)
        public ImageView picture;

        @Bind(R.id.musicNews_title)
        public TextView title;

        @Bind(R.id.musicNews_subtitle)
        public TextView description;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(MusicNews musicNews) {
            Glide.with(itemView.getContext())
                    .load(musicNews.picture)
                    .into(picture);

            title.setText(musicNews.title);
            description.setText(musicNews.description);
        }
    }
}
