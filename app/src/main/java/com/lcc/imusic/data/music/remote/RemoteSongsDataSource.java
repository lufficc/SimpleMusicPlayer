package com.lcc.imusic.data.music.remote;

import com.lcc.imusic.bean.Msg;
import com.lcc.imusic.bean.MusicItem;
import com.lcc.imusic.bean.SongsBean;
import com.lcc.imusic.data.music.SongsDataSource;
import com.lcc.imusic.manager.NetManager_;
import com.lcc.imusic.model.RemoteMusicProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lufficc on 2016/11/16.
 */

public class RemoteSongsDataSource implements SongsDataSource {
    private static RemoteSongsDataSource instance;

    private RemoteSongsDataSource() {
    }

    public static RemoteSongsDataSource getInstance() {
        if (instance == null) {
            synchronized (RemoteSongsDataSource.class) {
                if (instance == null) {
                    instance = new RemoteSongsDataSource();
                }
            }
        }
        return instance;
    }

    @Override
    public void getSongs(int page, final LoadSongsCallback callback) {
        NetManager_.API().songs(page).enqueue(new Callback<Msg<SongsBean>>() {
            @Override
            public void onResponse(Call<Msg<SongsBean>> call, Response<Msg<SongsBean>> response) {
                SongsBean songsBean = response.body().Result;
                if (songsBean != null) {
                    List<MusicItem> list = RemoteMusicProvider.m2l(songsBean);
                    callback.onLoaded(list);
                } else {
                    callback.onFailed("failed");
                }
            }

            @Override
            public void onFailure(Call<Msg<SongsBean>> call, Throwable t) {
                callback.onFailed("failed");
            }
        });
    }
}
