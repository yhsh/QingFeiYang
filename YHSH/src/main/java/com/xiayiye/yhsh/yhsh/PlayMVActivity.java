package com.xiayiye.yhsh.yhsh;

import android.util.Log;
import android.view.View;
import android.widget.VideoView;

public class PlayMVActivity extends BaseActivity {

    @Override
    protected View initView() {
        return View.inflate(this, R.layout.activity_play_mv, null);
    }

    @Override
    protected void initData(View view) {
        String mv_play_url = getIntent().getStringExtra("mv_play_url");
        String mv_sing_name = getIntent().getStringExtra("mv_sing_name");
        String mv_singer_name = getIntent().getStringExtra("mv_singer_name");
        VideoView play_mv = view.findViewById(R.id.play_mv);
        Log.e("打印传递过来的MV地址",mv_play_url);
        play_mv.setVideoPath(mv_play_url);
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View view) {

    }
}
