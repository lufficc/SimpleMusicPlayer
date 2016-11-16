package com.lcc.imusic.data.music.local;

import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import com.lcc.imusic.bean.MusicItem;
import com.lcc.imusic.config.App;
import com.lcc.imusic.data.music.SongsDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lufficc on 2016/11/16.
 */

public class LocalSongsDataSource implements SongsDataSource {
    private static LocalSongsDataSource instance;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static String[] projection = {
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media._ID
    };
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private LocalSongsDataSource() {
    }

    public static LocalSongsDataSource getInstance() {
        if (instance == null) {
            synchronized (LocalSongsDataSource.class) {
                if (instance == null) {
                    instance = new LocalSongsDataSource();
                }
            }
        }
        return instance;
    }

    Random random = new Random(System.currentTimeMillis());
    private String url1 = "http://www.n63.com/zutu/n63/?N=X2hiJTI2MC4tJTI4LSUyRiU1RCU1QzElMkJZJTJBMCU1QjAlNUQlMkIlMkElMkMtJTVFJTI4JTI4JTI5JTJGJTJDWiUyQiU1QyUyQjElMkMwWSUyNyUyQzBZ&v=.jpg";
    private String url2 = "http://upload.jianshu.io/users/upload_avatars/1438934/e9fe359cbaf2.jpeg";

    private String url3 = "http://img.666ccc.com/SpecialPic3/pic2010/19642.jpg";

    @Override
    public void getSongs(int page, final LoadSongsCallback callback) {
        if (page > 1) {
            callback.onLoaded(new ArrayList<MusicItem>());
            return;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = App.getApp().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, MediaStore.Audio.Media.DURATION + " > 20000", null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                if (cursor == null)
                    return;

                cursor.moveToFirst();
                int count = cursor.getCount();
                final List<MusicItem> musicList = new ArrayList<MusicItem>(count);
                for (int i = 0; i < count; i++) {
                    String name = cursor.getString(0);
                    String artist = cursor.getString(1);
                    String path = cursor.getString(2);
                    int duration = cursor.getInt(3) / 1000;
                    long id = cursor.getLong(5);

                    MusicItem musicItem = new MusicItem();
                    musicItem.data = path;
                    musicItem.title = name;
                    musicItem.artist = artist;
                    musicItem.duration = duration;
                    musicItem.id = id;
                    int r = random.nextInt(3);
                    if (r == 0)
                        musicItem.cover = url1;
                    else if (r == 1)
                        musicItem.cover = url2;
                    else
                        musicItem.cover = url3;

                    musicList.add(musicItem);
                    cursor.moveToNext();
                }
                cursor.close();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onLoaded(musicList);
                    }
                });
            }
        });
    }
}
