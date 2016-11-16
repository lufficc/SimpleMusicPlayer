package com.lcc.imusic.data.music;

import com.lcc.imusic.bean.MusicItem;

import java.util.List;

/**
 * Created by lufficc on 2016/11/16.
 */

public interface SongsDataSource {
    interface LoadSongsCallback {
        void onLoaded(List<MusicItem> musicItems);

        void onFailed(String msg);
    }

    void getSongs(int page, LoadSongsCallback callback);
}
