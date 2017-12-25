package com.xiayiye.yhsh.yhsh.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.xiayiye.yhsh.yhsh.R;

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2017/12/19.9:17
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 */

public class FourFragment extends BaseHomeFragment implements View.OnClickListener {

    private TextView home_neihan_tuijian;
    private TextView home_neihan_video;
    private TextView home_neihan_picture;
    private TextView home_neihan_duanzi;
    private TextView home_neihan_dingyue;
    private TextView home_neihan_city;
    private TextView home_neihan_chat;
    private Fragment fragment;

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.activity_home_neihan, null);
    }

    @Override
    protected void initData(View view) {
        home_neihan_tuijian = view.findViewById(R.id.home_neihan_tuijian);
        home_neihan_video = view.findViewById(R.id.home_neihan_video);
        home_neihan_picture = view.findViewById(R.id.home_neihan_picture);
        home_neihan_duanzi = view.findViewById(R.id.home_neihan_duanzi);
        home_neihan_dingyue = view.findViewById(R.id.home_neihan_dingyue);
        home_neihan_city = view.findViewById(R.id.home_neihan_city);
        home_neihan_chat = view.findViewById(R.id.home_neihan_chat);
        getFragmentManager().beginTransaction().replace(R.id.neihan_fl_replace,new TuiJianFragment()).commit();
    }

    @Override
    protected void initListener(View view) {
        home_neihan_tuijian.setOnClickListener(this);
        home_neihan_video.setOnClickListener(this);
        home_neihan_picture.setOnClickListener(this);
        home_neihan_duanzi.setOnClickListener(this);
        home_neihan_dingyue.setOnClickListener(this);
        home_neihan_city.setOnClickListener(this);
        home_neihan_chat.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        home_neihan_tuijian.setTextColor(getResources().getColor(R.color.black));
        home_neihan_video.setTextColor(getResources().getColor(R.color.black));
        home_neihan_picture.setTextColor(getResources().getColor(R.color.black));
        home_neihan_duanzi.setTextColor(getResources().getColor(R.color.black));
        home_neihan_dingyue.setTextColor(getResources().getColor(R.color.black));
        home_neihan_city.setTextColor(getResources().getColor(R.color.black));
        home_neihan_chat.setTextColor(getResources().getColor(R.color.black));
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.home_neihan_tuijian:
                fragment = new TuiJianFragment();
                ft.replace(R.id.neihan_fl_replace, fragment).commit();
                home_neihan_tuijian.setTextColor(getResources().getColor(R.color.green));
                break;
            case R.id.home_neihan_video:
                fragment = new VideoFragment();
                ft.replace(R.id.neihan_fl_replace, fragment).commit();
                home_neihan_video.setTextColor(getResources().getColor(R.color.green));
                break;
            case R.id.home_neihan_picture:
                fragment = new PictureFragment();
                ft.replace(R.id.neihan_fl_replace, fragment).commit();
                home_neihan_picture.setTextColor(getResources().getColor(R.color.green));
                break;
            case R.id.home_neihan_duanzi:
                fragment = new DuanZiFragment();
                ft.replace(R.id.neihan_fl_replace, fragment).commit();
                home_neihan_duanzi.setTextColor(getResources().getColor(R.color.green));
                break;
            case R.id.home_neihan_dingyue:
                fragment = new DingYueFragment();
                ft.replace(R.id.neihan_fl_replace, fragment).commit();
                home_neihan_dingyue.setTextColor(getResources().getColor(R.color.green));
                break;
            case R.id.home_neihan_city:
                fragment = new CityFragment();
                ft.replace(R.id.neihan_fl_replace, fragment).commit();
                home_neihan_city.setTextColor(getResources().getColor(R.color.green));
                break;
            case R.id.home_neihan_chat:
                fragment = new ChatFragment();
                ft.replace(R.id.neihan_fl_replace, fragment).commit();
                home_neihan_chat.setTextColor(getResources().getColor(R.color.green));
                break;
        }
    }
}
