package com.xiayiye.yhsh.yhsh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.nineoldandroids.view.ViewHelper;
import com.xiayiye.yhsh.yhsh.api.YhshAPI;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkJsonData;
import com.xiayiye.yhsh.yhsh.tools.PreferenceUtil;
import com.xiayiye.yhsh.yhsh.view.CustomRelativeLayout;
import com.xiayiye.yhsh.yhsh.view.CustomSettingView;

import org.json.JSONException;
import org.json.JSONObject;
/*import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;*/

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2017/12/28.15:20
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 * 类的包名：com.xiayiye.yhsh.yhsh
 * 项目名称: QingFeiYang
 */
public class PlayMusicNewActivity extends Activity implements View.OnClickListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, LyricView.OnPlayerClickListener {
    private LyricView lyricView;
    private MediaPlayer mediaPlayer;
    private View statueBar;
    private SeekBar display_seek;
    private TextView display_total;
    private TextView display_title;
    private TextView display_position;
    private ImageView btnPre, btnPlay, btnNext, btnSetting;
    private int position = 0;
    private State currentState = State.STATE_STOP;
    private ValueAnimator press_animator, up_animator;
    private ViewStub setting_layout;
    private CustomSettingView customSettingView;
    private CustomRelativeLayout customRelativeLayout;
    private final int MSG_REFRESH = 0x167;
    private final int MSG_LOADING = 0x177;
    private final int MSG_LYRIC_SHOW = 0x187;
    private long animatorDuration = 120;
    private ArrayList<String> song_urls;
    private ArrayList<String> song_names;
    private ArrayList<String> singer_name;
    private int play_number;
    private boolean isChange = false;//默认进入此页面播放点击的歌曲
    private String song_final_url;
    private ProgressDialog pd;
    private String playPage;//用于识别播放来源页面是QQ还是酷狗

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
        setContentView(R.layout.activity_play_music_new);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus();
        }
        initAllViews();
        initAllData();
    }

    @TargetApi(19)
    private void setTranslucentStatus() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        final int status = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        params.flags |= status;
        window.setAttributes(params);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void initAllViews() {
        statueBar = findViewById(R.id.statue_bar);
        statueBar.getLayoutParams().height = getStatusBarHeight();
        display_title = (TextView) findViewById(R.id.title_view);
        display_position = (TextView) findViewById(android.R.id.text1);
        display_total = (TextView) findViewById(android.R.id.text2);
        display_seek = (SeekBar) findViewById(android.R.id.progress);
        display_seek.setOnSeekBarChangeListener(this);
        btnNext = (ImageView) findViewById(android.R.id.button3);
        btnPlay = (ImageView) findViewById(android.R.id.button2);
        btnPre = (ImageView) findViewById(android.R.id.button1);
        btnSetting = (ImageView) findViewById(R.id.action_setting);
        Button bt_player_download = findViewById(R.id.bt_player_download);
        btnSetting.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        bt_player_download.setOnClickListener(this);
        lyricView = (LyricView) findViewById(R.id.lyric_view);
        lyricView.setOnPlayerClickListener(this);
        lyricView.setLineSpace(PreferenceUtil.getInstance(PlayMusicNewActivity.this).getFloat(PreferenceUtil.KEY_TEXT_SIZE, 12.0f));
        lyricView.setTextSize(PreferenceUtil.getInstance(PlayMusicNewActivity.this).getFloat(PreferenceUtil.KEY_TEXT_SIZE, 15.0f));
        lyricView.setHighLightTextColor(PreferenceUtil.getInstance(PlayMusicNewActivity.this).getInt(PreferenceUtil.KEY_HIGHLIGHT_COLOR, Color.parseColor("#4FC5C7")));

        setting_layout = (ViewStub) findViewById(R.id.main_setting_layout);
    }

    private void initAllData() {
        play_number = getIntent().getIntExtra("play_number", 0);//被点击的歌曲的position值
        song_urls = (ArrayList<String>) getIntent().getSerializableExtra("sing_play_url");
        song_names = (ArrayList<String>) getIntent().getSerializableExtra("sing_name");
        singer_name = (ArrayList<String>) getIntent().getSerializableExtra("singer_name");
        playPage = getIntent().getStringExtra("playPage");
//        Log.e("播放地址", song_urls.toString());
        //通过歌曲名称搜索歌曲拿到歌曲的f值
        pd = ProgressDialog.show(this, "获取数据", "请稍等，获取歌曲中…………", false, false);
        getSongF(pd, play_number);


       /* song_lyrics = getResources().getStringArray(R.array.song_lyrics);
        song_names = getResources().getStringArray(R.array.song_names);
        song_urls = getResources().getStringArray(R.array.song_urls);*/

        mediaPlayerSetup();  // 准备
    }

    private void getSongF(ProgressDialog pd, int play_number) {
        try {
            String search_str = URLEncoder.encode(song_names.get(play_number), "utf-8");//将中文转码成16进制,播放点击的歌曲
//            String search_str = URLEncoder.encode("老公天下第一", "utf-8");//将中文转码成16进制
            GetNetworkJsonData.TakeNetworkData(YhshAPI.QQMUSIC_SING_SEARCH_BASE + "10" + YhshAPI.QQMUSIC_SING_SEARCH_END + search_str, handler, 5, pd, this, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 准备
     */
    private void mediaPlayerSetup() {
        //第一次进入播放页面显示点击的歌曲的信息
        display_title.setText(song_names.get(play_number) + "-" + singer_name.get(play_number));
        handler.removeMessages(MSG_LYRIC_SHOW);
        handler.sendEmptyMessageDelayed(MSG_LYRIC_SHOW, 1000);
    }

    /**
     * 停止
     */
    private void stop() {
        if (null != mediaPlayer) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeMessages(MSG_REFRESH);
        lyricView.reset("载入歌词ing...");
        setCurrentState(State.STATE_STOP);
    }

    /**
     * 暂停
     */
    private void pause() {
        if (mediaPlayer != null && currentState == State.STATE_PLAYING) {
            setCurrentState(State.STATE_PAUSE);
            mediaPlayer.pause();
            handler.removeMessages(MSG_REFRESH);
        }
    }

    /**
     * 开始
     */
    private void start() {
        if (mediaPlayer != null && (currentState == State.STATE_PAUSE || currentState == State.STATE_PREPARE)) {
            setCurrentState(State.STATE_PLAYING);
            mediaPlayer.start();
            handler.sendEmptyMessage(MSG_REFRESH);
        }
    }

    /**
     * 上一首
     */
    private void previous() {
        isChange = true;
        stop();
        play_number--;//获取上一首歌曲的f属性
        getSongF(pd, play_number);
        if (play_number < 0) {
            play_number = Math.min(Math.min(song_names.size(), singer_name.size()), song_urls.size()) - 1;
        }
//        Log.e("打印上一首歌曲", song_urls.get(position));
        mediaPlayerSetup();
    }

    /**
     * 下一首
     */
    private void next() {
        isChange = true;
        stop();
        play_number++;//获取下一首歌曲的f属性
        getSongF(pd, play_number);
        if (play_number >= Math.min(Math.min(song_names.size(), singer_name.size()), song_urls.size())) {
            play_number = 0;
        }
//        Log.e("打印下一首歌曲", song_urls.get(position));
        mediaPlayerSetup();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        setCurrentState(State.STATE_PREPARE);
        DecimalFormat format = new DecimalFormat("00");
        display_seek.setMax(mediaPlayer.getDuration());
        display_total.setText(format.format(mediaPlayer.getDuration() / 1000 / 60) + ":" + format.format(mediaPlayer.getDuration() / 1000 % 60));
        start();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        display_seek.setSecondaryProgress((int) (mediaPlayer.getDuration() * 1.00f * percent / 100.0f));
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        next();
    }

    /**
     * 设置当前播放状态
     */
    private void setCurrentState(State state) {
        if (state == this.currentState) {
            return;
        }
        this.currentState = state;
        switch (state) {
            case STATE_PAUSE:
                btnPlay.setImageResource(R.mipmap.m_icon_player_play_normal);
                break;
            case STATE_PLAYING:
                btnPlay.setImageResource(R.mipmap.m_icon_player_pause_normal);
                break;
            case STATE_PREPARE:
                if (lyricView != null) {
                    lyricView.setPlayable(true);
                }
                setLoading(false);
                break;
            case STATE_STOP:
                if (lyricView != null) {
                    lyricView.setPlayable(false);
                }
                display_position.setText("--:--");
                display_seek.setSecondaryProgress(0);
                display_seek.setProgress(0);
                display_seek.setMax(100);
                btnPlay.setImageResource(R.mipmap.m_icon_player_play_normal);
                setLoading(false);
                break;
            case STATE_SETUP:
//                File file = new File(Constant.lyricPath + song_names.get(position) + ".lrc");
                File file = new File(Constant.lyricPath + song_names.get(play_number) + ".lrc");
                if (file.exists()) {
                    lyricView.setLyricFile(file, "GBK");
                } else {
//                    downloadLyric(song_lyrics[position], file);
//                    downloadLyric(song_lyrics[play_number], file);
                }
                btnPlay.setImageResource(R.mipmap.m_icon_player_play_normal);
                setLoading(true);
                break;
            default:
                break;
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (playPage.equals("QQ")) {
                if (msg.what == 5) {
                    String search_song = (String) msg.obj;
                    //打印搜索到的json数据
//                Log.e("打印搜索数据", search_song);
                    initJsonData(search_song);
                }
            }
            handlerMethod(msg);
        }
    };

    private void initJsonData(String search_song) {
        //开始解析歌曲的f属性
        try {
            JSONObject jsonObject = new JSONObject(search_song);
            String song_f = jsonObject.getJSONObject("data").getJSONObject("song").getJSONArray("list").getJSONObject(0).getString("f");
//            Log.e("打印歌曲属性f", song_f);
            //将字符串切割，获取到倒数第六个属性进行拼接参数播放歌曲
            String[] split = song_f.split("\\|");
            String s = split[split.length - 5];//解析出来的f属性值来拼接播放歌曲
            song_final_url = YhshAPI.QQMUSIC_SING_URL_BASE + "C100" + s + YhshAPI.QQMUSIC_SING_ERL_END;
//            Log.e("打印歌曲属性f", split[split.length - 5] + "===" + song_final_url);
//            Log.e("QQ音乐最终播放地址", song_final_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handlerMethod(Message msg) {
        switch (msg.what) {
            case MSG_REFRESH:
                if (mediaPlayer != null) {
                    if (!display_seek.isPressed()) {
                        lyricView.setCurrentTimeMillis(mediaPlayer.getCurrentPosition());
                        DecimalFormat format = new DecimalFormat("00");
                        display_seek.setProgress(mediaPlayer.getCurrentPosition());
                        display_position.setText(format.format(mediaPlayer.getCurrentPosition() / 1000 / 60) + ":" + format.format(mediaPlayer.getCurrentPosition() / 1000 % 60));
                    }
                }
                handler.sendEmptyMessageDelayed(MSG_REFRESH, 120);
                break;
            case MSG_LYRIC_SHOW:
                try {
                    setCurrentState(State.STATE_SETUP);
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnPreparedListener(PlayMusicNewActivity.this);
                    mediaPlayer.setOnCompletionListener(PlayMusicNewActivity.this);
                    mediaPlayer.setOnBufferingUpdateListener(PlayMusicNewActivity.this);
                    if (playPage.equals("QQ")) {
                        if (TextUtils.isEmpty(song_final_url)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PlayMusicNewActivity.this);
                            builder.setTitle("提示").setMessage("数据初始化中……").setPositiveButton("确定", null).show();
                        } else {
                            mediaPlayer.setDataSource(song_final_url);//播放点击的那首歌曲
                        }
                    } else if (playPage.equals("KG")) {
                        mediaPlayer.setDataSource(song_urls.get(play_number));//播放点击的那首歌曲
                    }
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case MSG_LOADING:
                Drawable background = btnPlay.getBackground();
                int level = background.getLevel();
                level = level + 300;
                if (level > 10000) {
                    level = level - 10000;
                }
                background.setLevel(level);
                handler.sendEmptyMessageDelayed(MSG_LOADING, 50);
                break;
            default:
                break;
        }
    }

    private boolean mLoading = false;

    private void setLoading(boolean loading) {
        if (loading && !mLoading) {
            btnPlay.setBackgroundResource(R.drawable.rotate_player_loading);
            handler.sendEmptyMessageDelayed(MSG_LOADING, 200);
            mLoading = true;
            return;
        }
        if (!loading && mLoading) {
            handler.removeMessages(MSG_LOADING);
            btnPlay.setBackgroundColor(Color.TRANSPARENT);
            mLoading = false;
            return;
        }
    }

    @Override
    public void onPlayerClicked(long progress, String content) {
        if (mediaPlayer != null && (currentState == State.STATE_PLAYING || currentState == State.STATE_PAUSE)) {
            mediaPlayer.seekTo((int) progress);
            if (currentState == State.STATE_PAUSE) {
                start();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            DecimalFormat format = new DecimalFormat("00");
            display_position.setText(format.format(progress / 1000 / 60) + ":" + format.format(progress / 1000 % 60));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeMessages(MSG_REFRESH);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.seekTo(seekBar.getProgress());
        handler.sendEmptyMessageDelayed(MSG_REFRESH, 120);
    }

   /* private void downloadLyric(String url, File file) {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.download(url, file.getAbsolutePath(), true, true, new RequestCallBack<File>() {

            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                lyricView.setLyricFile(responseInfo.result, "GBK");
            }

            @Override
            public void onFailure(HttpException e, String s) {
                lyricView.setLyricFile(null, null);
            }
        });
    }*/

    @Override
    public void onClick(View view) {
        if (press_animator != null && press_animator.isRunning()) {
            press_animator.cancel();
        }
        if (up_animator != null && up_animator.isRunning()) {
            up_animator.cancel();
        }
        switch (view.getId()) {
            case android.R.id.button1:
                previous();
                break;
            case android.R.id.button2:
                if (currentState == State.STATE_PAUSE) {
                    start();
                    break;
                }
                if (currentState == State.STATE_PLAYING) {
                    pause();
                    break;
                }
                break;
            case android.R.id.button3:
                next();
                break;
            case R.id.action_setting:
                if (customRelativeLayout == null) {
                    customRelativeLayout = (CustomRelativeLayout) setting_layout.inflate();
                    initCustomSettingView();
                }
                customRelativeLayout.show();
                break;
            case R.id.bt_player_download:
                //下载对应的歌曲
                if (playPage.equals("QQ")) {
                    downSing(song_final_url, singer_name.get(play_number), song_names.get(play_number));
                } else if (playPage.equals("KG")) {
                    downSing(song_urls.get(play_number), singer_name.get(play_number), song_names.get(play_number));
                }
                break;
            default:
                break;
        }
        press_animator = pressAnimator(view);
        press_animator.start();
    }

    private void initCustomSettingView() {
        customSettingView = (CustomSettingView) customRelativeLayout.getChildAt(0);
        customSettingView.setOnTextSizeChangeListener(new TextSizeChangeListener());
        customSettingView.setOnColorItemChangeListener(new ColorItemClickListener());
        customSettingView.setOnDismissBtnClickListener(new DismissBtnClickListener());
        customSettingView.setOnLineSpaceChangeListener(new LineSpaceChangeListener());
    }

    private class TextSizeChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                lyricView.setTextSize(15.0f + 3 * progress / 100.0f);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            PreferenceUtil.getInstance(PlayMusicNewActivity.this).putFloat(PreferenceUtil.KEY_TEXT_SIZE, 15.0f + 3 * seekBar.getProgress() / 100.0f);
        }
    }

    private class LineSpaceChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                lyricView.setLineSpace(12.0f + 3 * progress / 100.0f);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            PreferenceUtil.getInstance(PlayMusicNewActivity.this).putFloat(PreferenceUtil.KEY_LINE_SPACE, 12.0f + 3 * seekBar.getProgress() / 100.0f);
        }
    }

    private class DismissBtnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (customRelativeLayout != null) {
                customRelativeLayout.dismiss();
            }
        }
    }

    private class ColorItemClickListener implements CustomSettingView.OnColorItemChangeListener {

        @Override
        public void onColorChanged(int color) {
            lyricView.setHighLightTextColor(color);
            PreferenceUtil.getInstance(PlayMusicNewActivity.this).putInt(PreferenceUtil.KEY_HIGHLIGHT_COLOR, color);
            if (customRelativeLayout != null) {
                customRelativeLayout.dismiss();
            }
        }
    }

    public ValueAnimator pressAnimator(final View view) {
        final float size = view.getScaleX();
        ValueAnimator animator = ValueAnimator.ofFloat(size, size * 0.7f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewHelper.setScaleX(view, (Float) animation.getAnimatedValue());
                ViewHelper.setScaleY(view, (Float) animation.getAnimatedValue());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ViewHelper.setScaleX(view, size * 0.7f);
                ViewHelper.setScaleY(view, size * 0.7f);
                up_animator = upAnimator(view);
                up_animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                ViewHelper.setScaleX(view, size * 0.7f);
                ViewHelper.setScaleY(view, size * 0.7f);
            }
        });
        animator.setDuration(animatorDuration);
        return animator;
    }

    public ValueAnimator upAnimator(final View view) {
        final float size = view.getScaleX();
        ValueAnimator animator = ValueAnimator.ofFloat(size, size * 10 / 7.00f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewHelper.setScaleX(view, (Float) animation.getAnimatedValue());
                ViewHelper.setScaleY(view, (Float) animation.getAnimatedValue());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ViewHelper.setScaleX(view, size * 10 / 7.00f);
                ViewHelper.setScaleY(view, size * 10 / 7.00f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                ViewHelper.setScaleX(view, size * 10 / 7.00f);
                ViewHelper.setScaleY(view, size * 10 / 7.00f);
            }
        });
        animator.setDuration(animatorDuration);
        return animator;
    }

    private enum State {
        STATE_STOP, STATE_SETUP, STATE_PREPARE, STATE_PLAYING, STATE_PAUSE;
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
                        InputStream is = huc.getInputStream();
                        File file1 = new File(Environment.getExternalStorageDirectory() + "/Music_download/");
                        File file2 = new File(Environment.getExternalStorageDirectory() + "/Music_download/", sing_name + "_" + singer_name + ".m4a");
                        //不存在创建
                        if (!file1.exists()) {
                            file1.mkdir();
                        }
                        if (file2.exists()) {
                            ShowSingExits(sing_name + "_" + singer_name + "已存在" + Environment.getExternalStorageDirectory() + "/Music_download/文件夹中" + "无需下载");
                            return;
                        }
                        ShowSingExits(sing_name + "_" + singer_name + "正在下载中,请到" + Environment.getExternalStorageDirectory() + "/Music_download/文件夹中查看！");
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

    /**
     * 进行下载状态提示的方法
     *
     * @param str 提示的信息
     */
    private void ShowSingExits(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PlayMusicNewActivity.this, str, Toast.LENGTH_LONG).show();
            }
        });
    }

}

