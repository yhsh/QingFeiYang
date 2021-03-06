package com.xiayiye.yhsh.yhsh.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xiayiye.yhsh.yhsh.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2017/12/19.9:17
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 */

public class MusicFragment extends BaseHomeFragment {
    Fragment fragment;
    List<Fragment> list_music = new ArrayList<>();

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.activity_home_music, null);
    }

    @Override
    protected void initData(View view) {
        FrameLayout home_music_ll_replace = view.findViewById(R.id.home_music_ll_replace);
        RadioGroup home_music_rg = view.findViewById(R.id.home_music_rg);
        RadioButton home_music_rb_qq_new = view.findViewById(R.id.home_music_rb_qq_new);
        RadioButton home_music_rb_kg = view.findViewById(R.id.home_music_rb_kg);
        //默认最新歌曲
        home_music_rb_qq_new.setChecked(true);
        fragment = new NewMusicQQFragment();
        getFragmentManager().beginTransaction().replace(R.id.home_music_ll_replace, fragment).commit();
        home_music_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                switch (i) {
                    case R.id.home_music_rb_qq_new:
                        fragment = new NewMusicQQFragment();
                        ft.replace(R.id.home_music_ll_replace, fragment).commit();
                        break;
                    case R.id.home_music_rb_new:
                        fragment = new NewMusicFragment();
                        ft.replace(R.id.home_music_ll_replace, fragment).commit();
                        break;
                    case R.id.home_music_rb_hot:
                        fragment = new HotMusicFragment();
                        ft.replace(R.id.home_music_ll_replace, fragment).commit();
                        break;
                    case R.id.home_music_rb_kg:
                        fragment = new KgMusicFragment();
                        ft.replace(R.id.home_music_ll_replace, fragment).commit();
                        break;
                    default:
                }
            }
        });
    }

    @Override
    protected void initListener(View view) {

    }
}
