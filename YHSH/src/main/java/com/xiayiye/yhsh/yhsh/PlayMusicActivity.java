package com.xiayiye.yhsh.yhsh;

import android.app.DownloadManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PlayMusicActivity extends BaseActivity {

    private boolean isPlay = false;//默认不播放
    private MediaPlayer mediaPlayer;
    private String sing_play_url;
    private String sing_name;
    private String singer_name;

    @Override
    protected View initView() {
        return View.inflate(this, R.layout.activity_play_music, null);
    }

    @Override
    protected void initData(View view) {
        sing_play_url = getIntent().getStringExtra("sing_play_url");
        sing_name = getIntent().getStringExtra("sing_name");
        singer_name = getIntent().getStringExtra("singer_name");
//        Log.e("打印歌曲：", sing_name + "歌手：" + singer_name);
        Button bt_player = view.findViewById(R.id.bt_player);
        Button bt_player_download = view.findViewById(R.id.bt_player_download);
        mediaPlayer = new MediaPlayer();
        bt_player.setOnClickListener(this);
        bt_player_download.setOnClickListener(this);
    }

    @Override
    protected void initListener() {

    }

    private void stopPlay(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
    }

    public void playSing(String sing_play_url, MediaPlayer mediaPlayer) {
        Uri parse = Uri.parse(sing_play_url);

        try {
            mediaPlayer.setDataSource(getApplicationContext(), parse);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e("MusicReceiver", "a");
                    mp.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_player:
                //播放暂停歌曲
                if (isPlay) {
                    stopPlay(mediaPlayer);
                } else {
                    playSing(sing_play_url, mediaPlayer);
                }
                isPlay = !isPlay;
                Toast.makeText(getApplicationContext(), isPlay ? "正在播放" : "停止播放了", Toast.LENGTH_LONG).show();
                break;
            case R.id.bt_player_download:
                //下载歌曲
                downSing(sing_play_url, singer_name, sing_name);
                break;
        }
    }

    /**
     * 根据播放地址下载歌曲的方法
     *
     * @param sing_play_url 歌曲的播放地址
     * @param singer_name   歌手名字
     * @param sing_name     歌曲名字
     */
    private void downSing(final String sing_play_url, final String singer_name, final String sing_name) {
        //根据播放歌曲地址下载音乐
        new Thread(new Runnable() {
            @Override
            public void run() {
                //请求网络
                try {
                    URL url = new URL(sing_play_url);
                    HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                    huc.setConnectTimeout(10000);
                    huc.setReadTimeout(10000);
                    if (huc.getResponseCode() == 200) {
                        //网络请求成功，获取流信息，转成歌曲文件
                       /* InputStream inputStream = huc.getInputStream();
                        File file = new File(Environment.getExternalStorageDirectory(), sing_name + "_" + singer_name + ".m4a");
                        Log.e("打印文件路径", file.getAbsolutePath());
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        while ((len = inputStream.read()) != -1) {
                            bos.write(bytes, 0, len);
                            bos.flush();
                        }
                        bos.close();
                        inputStream.close();*/
                        InputStream is = huc.getInputStream();
                        File file1 = new File(Environment.getExternalStorageDirectory() + "/Music_download/");
                        File file2 = new File(Environment.getExternalStorageDirectory() + "/Music_download/", sing_name + "_" + singer_name + ".m4a");
                        //不存在创建
                        if (!file1.exists()) {
                            file1.mkdir();
                        }
                        if (file2.exists()) {
                            ShowSingExits("歌曲已存在" + Environment.getExternalStorageDirectory() + "/Music_download/文件夹中" + "无需下载");
                            return;
                        }
                        ShowSingExits("歌曲正在下载中,请到" + Environment.getExternalStorageDirectory() + "/Music_download/文件夹中查看！");
                        //创建字节流
                        byte[] bs = new byte[1024];
                        int len;
                        OutputStream os = new FileOutputStream(file2);
                        //写数据
                        while ((len = is.read(bs)) != -1) {
                            os.write(bs, 0, len);
                        }
                        //完成后关闭流
                        os.close();
                        is.close();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }) {
        }.start();
    }

    private void ShowSingExits(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PlayMusicActivity.this, str, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Android自带提供的文件下载方式，四行代码搞定
     *
     * @param sing_play_url 歌曲的播放地址
     * @param singer_name   歌手名字
     * @param sing_name     歌曲名字
     */
    public void downSings(String sing_play_url, String singer_name, String sing_name) {
        //创建下载任务,downloadUrl就是下载链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(sing_play_url));
        //指定下载路径和下载文件名
        request.setDestinationInExternalPublicDir("/download/", sing_name + "_" + singer_name + ".m4a");
        //获取下载管理器
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (new File(Environment.getExternalStorageDirectory() + "/download/", sing_name + "_" + singer_name + ".m4a").exists()) {
            ShowSingExits("歌曲已存在" + Environment.getExternalStorageDirectory() + "/Music_download/文件夹中" + "无需下载");
            return;
        } else {
            ShowSingExits("歌曲正在下载中,请到" + Environment.getExternalStorageDirectory() + "/Music_download/文件夹中查看！");
            //将下载任务加入下载队列，否则不会进行下载
            downloadManager.enqueue(request);
        }
    }
}
