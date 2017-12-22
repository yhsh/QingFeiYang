package com.xiayiye.yhsh.yhsh;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2017/12/19.10:38
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 * 应用包名：com.xiayiye.yhsh.yhsh
 * 项目名称；QingFeiYang
 */

public abstract class BaseActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = initView();
        setContentView(view);
        initData(view);
        initListener();
    }

    /**
     * 初始化布局
     *
     * @return
     */
    protected abstract View initView();

    /**
     * 初始化 数据
     *
     * @param view
     */
    protected abstract void initData(View view);

    /**
     * 监听的方法
     */
    protected abstract void initListener();
}
