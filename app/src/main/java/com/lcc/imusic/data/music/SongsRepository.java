package com.lcc.imusic.data.music;

import com.lcc.imusic.bean.MusicItem;
import com.lcc.imusic.data.music.local.LocalSongsDataSource;
import com.lcc.imusic.data.music.remote.RemoteSongsDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lufficc on 2016/11/16.
 */

public class SongsRepository implements SongsDataSource {


    private static SongsRepository instance;
    private LocalSongsDataSource localSongsDataSource = LocalSongsDataSource.getInstance();
    private RemoteSongsDataSource remoteSongsDataSource = RemoteSongsDataSource.getInstance();

    private SongsRepository() {
    }

    public static SongsRepository getInstance() {
        if (instance == null) {
            synchronized (RemoteSongsDataSource.class) {
                if (instance == null) {
                    instance = new SongsRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public void getSongs(final int page, final LoadSongsCallback callback) {
        final List<MusicItem> list = new ArrayList<>();
        localSongsDataSource.getSongs(page, new LoadSongsCallback() {
            @Override
            public void onLoaded(List<MusicItem> musicItems) {
                list.addAll(musicItems);
                remoteSongsDataSource.getSongs(page, new LoadSongsCallback() {
                    @Override
                    public void onLoaded(List<MusicItem> musicItems) {
                        list.addAll(musicItems);
                        callback.onLoaded(list);
                    }

                    @Override
                    public void onFailed(String msg) {
                        callback.onFailed(msg);
                    }
                });
            }

            @Override
            public void onFailed(String msg) {
                callback.onFailed(msg);
            }
        });
    }
}
