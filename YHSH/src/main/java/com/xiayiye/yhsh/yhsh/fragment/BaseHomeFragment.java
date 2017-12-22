package com.xiayiye.yhsh.yhsh.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.xiayiye.yhsh.yhsh.R;

import java.lang.ref.WeakReference;

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2017/12/19.9:18
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 */

public abstract class BaseHomeFragment extends Fragment {
    protected WeakReference<View> mRootView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView();
        initData(view);
        initListener(view);



       /* if (mRootView == null || mRootView.get() == null) {
            mRootView = new WeakReference<>(view);
        } else {
            ViewGroup parent = (ViewGroup) mRootView.get().getParent();
            if (parent != null) {
                parent.removeView(mRootView.get());
            }
        }
        return mRootView.get();*/


        return view;
    }


    public abstract View initView();

    protected abstract void initData(View view);

    protected abstract void initListener(View view);
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        removeSelfFromParent(view);
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 将子View从它父View中移除
     *
     * @param child
     */
    public static void removeSelfFromParent(View child) {
        if (child != null) {
            ViewParent parent = child.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(child);//将子VIew从父View中移除
            }
        }
    }
}
