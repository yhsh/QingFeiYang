package com.xiayiye.yhsh.yhsh;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutYHSHActivity extends Activity implements View.OnClickListener {

    private WebView wv_my_blog;
    private RelativeLayout view_gone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_yhsh);
        TextView about = findViewById(R.id.about);
        TextView open_my_blog = findViewById(R.id.open_my_blog);
        TextView look_my_blog = findViewById(R.id.look_my_blog);
        view_gone = findViewById(R.id.view_gone);
        wv_my_blog = findViewById(R.id.wv_my_blog);
        about.setOnClickListener(this);
        open_my_blog.setOnClickListener(this);
        look_my_blog.setOnClickListener(this);

        WebSettings settings = wv_my_blog.getSettings();
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
//      settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
//      settings.setLoadWithOverviewMode(true);
//      settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);
        wv_my_blog.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_my_blog:
                //打开我的博客
                Uri uri = Uri.parse("http://blog.csdn.net/xiayiye5");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.look_my_blog:
                wv_my_blog.setVisibility(View.VISIBLE);
                view_gone.setVisibility(View.GONE);
                //查看我的博客
                wv_my_blog.loadUrl("http://blog.csdn.net/xiayiye5");
                break;
            case R.id.about:
                //跳转到我的资料卡
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqapi://card/show_pslcard?src_type=internal&source=sharecard&version=1&uin=2703401268")));//跳转到QQ资料
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&card_type=group&source=qrcode&uin=485761716")));//跳转到QQ群
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&version=1&uin=2703401268")));//跳转到临时会话
                break;
        }
    }
}
