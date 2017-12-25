package com.xiayiye.yhsh.yhsh.tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2017/12/21.14:00
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 * 应用包名：com.xiayiye.yhsh.yhsh.tools
 * 项目名称: QingFeiYang
 */

public class GetNetworkJsonData {
    public static void TakeNetworkData(final String news_url, final Handler handler, final int type, final ProgressDialog progressDialog, final Activity activity, final String code) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(news_url);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(20000);
                    urlConnection.setConnectTimeout(20000);
                    if (urlConnection.getResponseCode() == 200) {
                        //4.读流，获取源码内容
                        InputStream inputStream = urlConnection.getInputStream();
                        //创建一个内存管道流,方便转string
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        byte[] buff = new byte[1024 * 10];
                        int len = 0;
                        while ((len = inputStream.read(buff)) > -1) {
                            out.write(buff, 0, len);
                            out.flush();
                        }
//                        String result = new String(out.getBytes(), "gbk");
                        byte[] lens = out.toByteArray();
//                        String result = new String(lens, "gbk");//将lens编码设为gbk即可解决乱码问题
                        String result = new String(lens, code);//将lens编码设为gbk即可解决乱码问题
                        out.close();
                        inputStream.close();
                        Message obtain = Message.obtain();
                        obtain.obj = result;
                        obtain.what = type;
                        handler.sendMessage(obtain);
                        dismissDialog(progressDialog, activity);//请求成功也关闭对话框
                    } else {
                        dismissDialog(progressDialog, activity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dismissDialog(progressDialog, activity);
                }
            }

            private void dismissDialog(ProgressDialog progressDialog, final Activity activity) {
                progressDialog.dismiss();//关闭对话框
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "服务器错误！", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }
}
