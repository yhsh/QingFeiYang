package com.xiayiye.yhsh.yhsh;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class NewsDetailActivity extends BaseActivity {

    private WebView home_one_list_news_detail_web_view;


    @Override
    protected View initView() {
        return View.inflate(this, R.layout.activity_news_detail, null);
    }

    @Override
    protected void initData(View view) {
        TextView home_one_list_news_detail_tittle = view.findViewById(R.id.home_one_list_news_detail_tittle);
        home_one_list_news_detail_web_view = view.findViewById(R.id.home_one_list_news_detail_web_view);
        WebSettings webSettings = home_one_list_news_detail_web_view.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        final String detail_url = getIntent().getStringExtra("detail_url");
        home_one_list_news_detail_web_view.loadUrl(detail_url);
        home_one_list_news_detail_web_view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        /**
         * S 代表下载地址
         */
        home_one_list_news_detail_web_view.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                //调用系统浏览器下载
                Uri uri = Uri.parse(s);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        home_one_list_news_detail_tittle.setOnClickListener(this);
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_one_list_news_detail_tittle:
                finish();//关闭本页面
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //网页返回上一页面的方法
        if (home_one_list_news_detail_web_view.canGoBack()) {
            home_one_list_news_detail_web_view.goBack();
        } else {
            finish();
        }
    }
}
