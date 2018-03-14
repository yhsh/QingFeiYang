package com.xiayiye.yhsh.yhsh;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class PlayMVActivity extends BaseActivity {

    private String replace_mv_sing_name;
    private String replace_mv_singer_name;

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
        TextView mv_name = view.findViewById(R.id.mv_name);

        if (mv_sing_name.contains("<em>")) {
            replace_mv_sing_name = mv_sing_name.replace("<em>", "").replace("</em>", "");
        } else {
            replace_mv_sing_name = mv_sing_name;
        }
        if (mv_singer_name.contains("<em>")) {
            replace_mv_singer_name = mv_singer_name.replace("<em>", "").replace("</em>", "");
        } else {
            replace_mv_singer_name = mv_singer_name;
        }

        mv_name.setText(replace_mv_singer_name + "-" + replace_mv_sing_name);
        play_mv.setMediaController(new MediaController(this));
//        play_mv.setVideoURI(Uri.parse("http://alcdn.hls.xiaoka.tv/2017427/14b/7b3/Jzq08Sl8BbyELNTo/index.m3u8"));
        play_mv.setVideoURI(Uri.parse(mv_play_url));
        play_mv.start();
        play_mv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(PlayMVActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
            }
        });
        Log.e("打印传递过来的MV地址", mv_play_url);
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View view) {

    }
}
