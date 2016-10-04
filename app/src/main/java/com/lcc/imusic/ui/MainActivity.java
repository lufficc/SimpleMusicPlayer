package com.lcc.imusic.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lcc.imusic.R;
import com.lcc.imusic.adapter.FragmentAdapter;
import com.lcc.imusic.base.activity.AccountDelegate;
import com.lcc.imusic.base.activity.PlayBarActivity;
import com.lcc.imusic.bean.DlBean;
import com.lcc.imusic.bean.MusicItem;
import com.lcc.imusic.manager.UserManager;
import com.lcc.imusic.model.CurrentMusicProviderImpl;
import com.lcc.imusic.service.DownLoadHelper;
import com.lcc.imusic.service.DownloadService;
import com.lcc.imusic.service.MusicPlayService;
import com.lcc.imusic.ui.home.MusicNewsFragment;
import com.lcc.imusic.ui.home.MusicianListFragment;
import com.lcc.imusic.ui.home.RemoteMusicFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnCheckedChanged;

public class MainActivity extends PlayBarActivity implements AccountDelegate.AccountListener, ViewPager.OnPageChangeListener {
    @BindView(R.id.actionbar_buttons)
    RadioGroup actionBarButtonGroup;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private AccountDelegate accountDelegate;

    @BindViews({R.id.actionbar_music, R.id.actionbar_friends, R.id.actionbar_discover})
    List<RadioButton> actionBarButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        accountDelegate = new AccountDelegate(this, toolbar, this);
        accountDelegate.init();
        if (UserManager.isLogin()) {
            accountDelegate.setAvatar(UserManager.avatar());
            accountDelegate.setUsername(UserManager.username());
        }
    }

    private void init() {
        actionBar.setDisplayShowTitleEnabled(false);
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(),
                new MusicianListFragment(), new RemoteMusicFragment(), new MusicNewsFragment());
        viewPager.setAdapter(adapter);
        actionBarButtons.get(0).setChecked(true);
        viewPager.addOnPageChangeListener(this);
        if (!MusicPlayService.HAS_STATED) {
            Intent intent = new Intent(this, MusicPlayService.class);
            startService(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                overridePendingTransition(R.anim.activity_bottom_open, R.anim.activity_close);
                break;
        }
        return true;
    }

    @NonNull
    @Override
    public List<IDrawerItem> onCreateMenuItem() {
        List<IDrawerItem> list = new ArrayList<>();
        list.add(new PrimaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home));
        list.add(new PrimaryDrawerItem().withName("下载管理").withIcon(FontAwesome.Icon.faw_cloud_download));
        list.add(new PrimaryDrawerItem().withName("退出登录").withIcon(FontAwesome.Icon.faw_sign_out));
        list.add(new PrimaryDrawerItem().withName("退出").withIcon(FontAwesome.Icon.faw_sign_out));
        list.add(new PrimaryDrawerItem().withName("减小音量").withIcon(FontAwesome.Icon.faw_sign_out));
        list.add(new PrimaryDrawerItem().withName("增大音量").withIcon(FontAwesome.Icon.faw_sign_out));
        list.add(new PrimaryDrawerItem().withName("下载所有").withIcon(FontAwesome.Icon.faw_sign_out));

        return list;
    }

    @Override
    protected void onDestroy() {
        accountDelegate.destroy();
        accountDelegate = null;
        Logger.i("onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onDrawerMenuSelected(View view, int position, IDrawerItem drawerItem) {
        accountDelegate.close();
        switch (position) {
            case 1:
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText("token", UserManager.token()));
                toast(UserManager.token());
                break;
            case 2:
                startActivity(new Intent(this, DownLoadActivity.class));
                break;
            case 3:
                Snackbar.make(toolbar, "确定退出吗？", Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserManager.logout()) {
                            accountDelegate.setUsername("点击登录");
                            toast("已退出登录");
                        } else {
                            toast("退出登录失败");
                        }
                    }
                }).show();
                break;
            case 4:
                finish();
                stopService(new Intent(this, MusicPlayService.class));
                break;
            case 5:
                if (isBind())
                    musicServiceBind.volumeDown();
                break;
            case 6:
                if (isBind())
                    musicServiceBind.volumeUp();
                break;
            case 7:
                if (!CurrentMusicProviderImpl.getMusicProvider().provideMusics().isEmpty()) {
                    for (MusicItem item : CurrentMusicProviderImpl.getMusicProvider().provideMusics()) {
                        DlBean<MusicItem> dlBean = new DlBean<>(item.data, item.title.trim() + "-" + item.artist.trim() + ".mp3", item);
                        DownLoadHelper.get().enqueue(dlBean);
                    }
                    Intent intent = new Intent(this, DownloadService.class);
                    startService(intent);
                    toast("开始下载");
                } else {
                    toast("列表为空");
                }

                break;
        }

        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        actionBarButtons.get(position).setChecked(true);

    }

    @OnCheckedChanged({R.id.actionbar_music, R.id.actionbar_friends, R.id.actionbar_discover})
    public void onActionbarCheck(RadioButton button, boolean check)
    {
        if (check)
        {
            int index = actionBarButtons.indexOf(button);
            viewPager.setCurrentItem(index);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
