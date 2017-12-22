package com.xiayiye.yhsh.yhsh.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2017/12/20.12:53
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 * 应用包名：com.xiayiye.yhsh.yhsh.tools
 * 项目名称: QingFeiYang
 */

public class GetNetworkImage {
    /**
     * 自己写的加载网络图片的方法
     * img_url 图片的网址
     *
     * @param welcomeImg 图片要显示在这个控件上面
     */
    public static void initNetWorkImage(final ImageView welcomeImg, final String img_url, final Activity context) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(img_url);
                    HttpURLConnection uct = (HttpURLConnection) url.openConnection();
                    uct.setRequestMethod("GET");
//                    uct.setReadTimeout(10000);
                    uct.setConnectTimeout(10000);
                    int responseCode = uct.getResponseCode();
                    if (responseCode == 200) {
                        //子线程更新UI（设置显示网络图片）？是否会阻塞线程？出现ANR？？
                        InputStream inputStream = uct.getInputStream();
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        context.runOnUiThread(new Runnable() {
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
}
