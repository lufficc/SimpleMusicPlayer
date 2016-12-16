package com.lcc.imusic.adapter;

import com.lcc.imusic.adapter.viewprovider.TopItemProvider;
import com.lcc.imusic.bean.TopicItem;
import com.lufficc.lightadapter.LightAdapter;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.lightadapter.LoadMoreFooterViewHolderProvider;

/**
 * Created by lcc_luffy on 2016/5/28.
 */
public class ClubAdapter extends LightAdapter {
    public LoadMoreFooterModel getFooterModel() {
        return footerModel;
    }

    LoadMoreFooterModel footerModel;

    public ClubAdapter() {
        addFooter(footerModel = new LoadMoreFooterModel());
        register(TopicItem.class, new TopItemProvider(this));
        register(LoadMoreFooterModel.class, new LoadMoreFooterViewHolderProvider());
    }
}
