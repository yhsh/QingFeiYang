package com.xiayiye.yhsh.yhsh;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.xiayiye.yhsh.yhsh.tools.GetNetworkImage;

public class BigPictureActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_picture);
        ImageView big_picture_iv = findViewById(R.id.big_picture_iv);
        big_picture_iv.setScaleType(ImageView.ScaleType.FIT_XY);//设置图片充满全屏
        GetNetworkImage.initNetWorkImage(big_picture_iv, getIntent().getStringExtra("picture_url"), this);
    }
}
