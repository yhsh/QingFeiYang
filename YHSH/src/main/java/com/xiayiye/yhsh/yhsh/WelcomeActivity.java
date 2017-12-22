package com.xiayiye.yhsh.yhsh;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//import butterknife.ButterKnife;
//import butterknife.InjectView;

//import com.bumptech.glide.Glide;

public class WelcomeActivity extends Activity {
    String img_url = "http://img1.imgtn.bdimg.com/it/u=34748887,920152242&fm=27&gp=0.jpg";
    String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1513597298855&di=22615a6d2e937cef5743462d74a40302&imgtype=0&src=http%3A%2F%2Fattimg.dospy.com%2Fimg%2Fday_130903%2F20130903_117bb49ea7b386f6036ftAD7dW1SAa1S.jpg";
    //    @InjectView(R.id.welcome_img)
    ImageView welcomeImg;
    boolean isEnterHome = false;//默认没有进入首页
    private SharedPreferences sp_isEnterHome;
    private TextView welcome_tv_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
//        ButterKnife.inject(this);
        welcomeImg = findViewById(R.id.welcome_img);
        welcome_tv_number = findViewById(R.id.welcome_tv_number);
        CountDownTimer countDownTimer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long l) {
                welcome_tv_number.setText(l / 1000 + "S");
            }

            @Override
            public void onFinish() {

            }
        }.start();
        sp_isEnterHome = getSharedPreferences("isEnterHome", MODE_PRIVATE);
        boolean isEnterHome = sp_isEnterHome.getBoolean("isEnterHome", false);
        if (isEnterHome) {
            enterHome();//直接进入首页
        } else {
            initWelcome();//初始化欢迎界面
        }
    }

    private void initWelcome() {
        initWelcomeImage(welcomeImg);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                SystemClock.sleep(3000);
                enterHome();//3秒后进入首页
                isEnterHome = true;
                sp_isEnterHome.edit().putBoolean("isEnterHome", isEnterHome).apply();
            }
        }, 5000);
    }

    /**
     * 自己写的加载网络图片的方法
     *
     * @param welcomeImg 图片要显示在这个控件上面
     */
    private void initWelcomeImage(final ImageView welcomeImg) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(img_url);
                    HttpURLConnection uct = (HttpURLConnection) url.openConnection();
                    uct.setRequestMethod("GET");
                    uct.setReadTimeout(10000);
                    uct.setConnectTimeout(10000);
                    int responseCode = uct.getResponseCode();
                    if (responseCode == 200) {
                        //子线程更新UI（设置显示网络图片）？是否会阻塞线程？出现ANR？？
                        InputStream inputStream = uct.getInputStream();
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                welcomeImg.setImageBitmap(bitmap);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void enterHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    /**
     * 加载欢迎界面图片的方法
     *
     * @param welcomeImg 图片要显示在这个控件上面
     */
  /*  private void initWelcomeImage(ImageView welcomeImg) {
        Glide.with(this)
                .load(url)
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(welcomeImg);
    }*/
}
