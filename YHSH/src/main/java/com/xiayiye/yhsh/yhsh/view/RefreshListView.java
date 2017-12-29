package com.xiayiye.yhsh.yhsh.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiayiye.yhsh.yhsh.R;

public class RefreshListView extends ListView implements OnScrollListener {

    private int downY;
    private int headerHeight; // 头布局的高度
    private View mheaderView; // 头布局
    private final int PULL_REFRESH_STATE = 0; // 代表下拉刷新状态 ctrl + shift + X
    private final int RELEASE_STATE = 1; // 释放刷新状态
    private final int RELEASEING = 2; // 正在刷新状态

    private int header_current_state = PULL_REFRESH_STATE;// 默认当前是下拉刷新状态
    private RotateAnimation upAnimation;  //向上旋转的动画
    private RotateAnimation downAnimation; //向下旋转的动画
    private ProgressBar pb_progress;
    private TextView tv_update_state;
    private TextView tv_last_update_time;
    private ImageView iv_arrow;

    private boolean isStop = false; //默认没有滑动到底部

    private OnRefreshListener mOnRefreshListener;
    private OnLoadingMoreListener mOnLoadingMoreListener;
    private View mfootView;
    private int footHeight;  //脚布局高度

    public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initHeader();
        initFootView();
    }

    //初始化脚布局
    private void initFootView() {
        //[1]通过打气筒把一个布局转换成一个view对象
        mfootView = View.inflate(getContext(), R.layout.list_footer_view, null);
        mfootView.measure(0, 0);
        footHeight = mfootView.getMeasuredHeight();
        //[1]默认情况隐藏脚布局
        mfootView.setPadding(0, -footHeight, 0, 0);
        //[2]添加脚布局
        this.addFooterView(mfootView);
        //[3]给listview设置滑动监听
        this.setOnScrollListener(this);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RefreshListView(Context context) {
        this(context, null);
    }

    // 添加listview的头
    private void initHeader() {
        // [1]把我们画的自定义头布局转换成一个View
        mheaderView = View.inflate(getContext(), R.layout.list_header_view, null);
        //[1.1]找到关心的控件
        pb_progress = (ProgressBar) mheaderView.findViewById(R.id.pb_progress);
        tv_update_state = (TextView) mheaderView.findViewById(R.id.tv_updat_state);
        tv_last_update_time = (TextView) mheaderView.findViewById(R.id.tv_last_update_time);
        iv_arrow = (ImageView) mheaderView.findViewById(R.id.iv_arrow);
        //[1.1.1]初始化设置一下 时间
        tv_last_update_time.setText(getCurrentTimerr());
        // [2]获取mheaderview控件的高度
        // int height = mheaderView.getHeight(); //此方法是当控件完全显示到屏幕后 才可以获取高度
        // [3]对viwe进行测量
        mheaderView.measure(0, 0);// 让系统帮助我们去测量
        headerHeight = mheaderView.getMeasuredHeight();
        // System.out.println("测量后的高度:"+headerHeight);
        mheaderView.setPadding(0, -headerHeight, 0, 0);
        //[4]添加头布局
        this.addHeaderView(mheaderView);
        //[5]初始化 向上 向下图片旋转动画
        initAnim();
    }

    //初始化头布局 图片旋转的动画
    private void initAnim() {
        //向上旋转的动画
        upAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setDuration(500);//设置动画执行的时长
        upAnimation.setFillAfter(true);
        //向下旋转的动画
        downAnimation = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(500);//设置动画执行的时长
        downAnimation.setFillAfter(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下
                // [1]获取按下的Y轴的位置
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:  //移动
                int moveY = (int) ev.getY();
                // [2]计算移动的距离
                int diffY = moveY - downY;
                // [3]当listview处于第一个条目索引 并且 移动的距离>0才显示头布局
                if (getFirstVisiblePosition() == 0 && diffY > 0) {
                    // [4]算出paddinTop
                    int paddingTop = -headerHeight + diffY / 2;
                    // [4.1]判断paddingTop 来更新头布局
                    if (paddingTop > 0 && header_current_state != RELEASE_STATE) {
                        // System.out.println("进入释放刷新状态");
                        header_current_state = RELEASE_STATE;
                        // [4.2]更新头布局状态
                        updateHeaderView();
                    } else if (paddingTop < 0
                            && header_current_state != PULL_REFRESH_STATE) {
                        // System.out.println("~~~~下拉刷新状态~~~~");
                        header_current_state = PULL_REFRESH_STATE;
                        updateHeaderView();
                    }

                    // [5]设置头布局
                    mheaderView.setPadding(0, paddingTop, 0, 0);
                    return true;// 让当前view处理事件
                }
                break;
            case MotionEvent.ACTION_UP:   //手抬起事件
                if (header_current_state == PULL_REFRESH_STATE) {
                    updateHeaderView();
                } else if (header_current_state == RELEASE_STATE) {
                    //[1]把正在刷新状态 赋值给 当前状态
                    header_current_state = RELEASEING;
                    //[2]调用更新头布局的方法
                    updateHeaderView();
                    //[3]更新为下拉状态
//				header_current_state =PULL_REFRESH_STATE;
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onRefresh();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    //更新头布局的方法
    private void updateHeaderView() {
        switch (header_current_state) {
            case PULL_REFRESH_STATE:   //下拉刷新状态
                //[1]更改iv状态
                iv_arrow.startAnimation(downAnimation);
                //[2]设置 tv_update 状态
                tv_update_state.setText("下拉刷新");
                //[3]隐藏头布局
                mheaderView.setPadding(0, -headerHeight, 0, 0);
                break;
            case RELEASE_STATE:        //释放刷新状态
                iv_arrow.startAnimation(upAnimation);
                tv_update_state.setText("释放刷新");
                break;
            case RELEASEING:           //正在刷新的状态
                //[1]把动画图片隐藏
                iv_arrow.setVisibility(View.INVISIBLE);
                iv_arrow.clearAnimation();
                //[2]显示进度条
                pb_progress.setVisibility(View.VISIBLE);
                //[3]刷新状态的文字改为 正在刷新
                tv_update_state.setText("正在刷新ing");
                //[4]设置头布局回到屏幕顶部
                mheaderView.setPadding(0, 0, 0, 0);
                //[5]设置上次更新时间
//			tv_last_update_time.setText(getCurrentTime());
                break;
        }
    }

    //设置刷新的监听
    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public void setOnLoadingMoreListener(OnLoadingMoreListener l) {
        mOnLoadingMoreListener = l;
    }

    public interface OnLoadingMoreListener {
        public void onLoadingMore();
    }

    //刷新数据的接口
    public interface OnRefreshListener {
        public void onRefresh();
    }

    public String getCurrentTimerr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    //设置刷新数据后的逻辑
    @SuppressLint("SimpleDateFormat")
    public void setOnLoadFinish() {
        //[0]进度条隐藏
        pb_progress.setVisibility(View.INVISIBLE);
        //[2]在这里更新时间
        tv_last_update_time.setText(getCurrentTimerr());
        //[3]隐藏头布局
        mheaderView.setPadding(0, -headerHeight, 0, 0);
        //[4]把状态置为下拉状态
        header_current_state = PULL_REFRESH_STATE;
    }

    //当状态发生改变的时候
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
            //判断lisview 是否滑动到了底部
            if (getLastVisiblePosition() == getCount() - 1 && !isStop) {
                System.out.println("listview滑动到了底部");
                isStop = true;
                //[1]把脚布局显示出来
                mfootView.setPadding(0, 0, 0, 0);
                if (mOnLoadingMoreListener != null) {
                    mOnLoadingMoreListener.onLoadingMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    //加载更多完成 需要处理的逻辑
    public void setOnLoadIngMoreFinish() {
        //[1]把加载更多脚布局隐藏
        mfootView.setPadding(0, -footHeight, 0, 0);
        isStop = false;
    }
}