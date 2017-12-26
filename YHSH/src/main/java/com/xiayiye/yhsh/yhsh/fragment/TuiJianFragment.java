package com.xiayiye.yhsh.yhsh.fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import com.xiayiye.yhsh.yhsh.R;
import com.xiayiye.yhsh.yhsh.api.YhshAPI;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkImage;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkJsonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2017/12/25.14:53
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 * 应用包名：com.xiayiye.yhsh.yhsh.fragment
 * 项目名称: QingFeiYang
 */

public class TuiJianFragment extends BaseHomeFragment {
    List<String> img_cover_url_list = new ArrayList<>();//视频播放预览图
    List<String> down_video_list = new ArrayList<>();//视频下载地址
    List<String> title_video_list = new ArrayList<>();//视频标题
    List<String> share_video_list = new ArrayList<>();//分享视频地址
    ProgressDialog pd;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String Str_tuijian = (String) msg.obj;
            initJson(Str_tuijian);
        }
    };
    private ListView home_tuijian_lv;

    private void initJson(String str_tuijian) {
        String down_str;
        //获取到的json数据
        try {
            JSONObject jsonObject = new JSONObject(str_tuijian);
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject group = jsonArray.getJSONObject(i).getJSONObject("group");
                if (group.has("download_url")) {
                    down_video_list.add(group.getString("download_url"));
                    title_video_list.add(group.getString("text"));
                    JSONArray img_url_list = group.getJSONObject("large_cover").getJSONArray("url_list");
                    img_cover_url_list.add(img_url_list.getJSONObject(0).getString("url"));
                } else {
                    share_video_list.add(group.getString("share_url"));
                }
//                Log.e("打印下载地址：", down_video_list.toString());
//                Log.e("打印分享地址：", share_video_list.toString());
//                Log.e("打印视频标题：", title_video_list.toString());
            }
            home_tuijian_lv.setAdapter(new MyBaseAdapter());
        } catch (JSONException e) {
            e.printStackTrace();
            pd.dismiss();
        }
//        Log.e("打印分享地址：", share_video_list.toString());
    }

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.activity_home_tuijian, null);
    }

    @Override
    protected void initData(View view) {
        pd = ProgressDialog.show(getActivity(), "加载数据中", "获取最新数据……", false, false);
        home_tuijian_lv = view.findViewById(R.id.home_tuijian_lv);
        initNetWork();
    }

    private void initNetWork() {
        GetNetworkJsonData.TakeNetworkData(YhshAPI.NEIHAN_TUIJIAN, handler, 0, pd, getActivity(), "utf-8");
    }

    @Override
    protected void initListener(View view) {

    }

    private class MyBaseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return share_video_list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(getActivity(), R.layout.activity_home_tuijian_listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tuijian_listview_item_title = view.findViewById(R.id.tuijian_listview_item_title);
                viewHolder.tuijian_listview_item_vv = view.findViewById(R.id.tuijian_listview_item_vv);
                viewHolder.home_tuijian_list_item_show_img = view.findViewById(R.id.home_tuijian_list_item_show_img);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tuijian_listview_item_title.setText(title_video_list.get(i));
            GetNetworkImage.initNetWorkImage(viewHolder.home_tuijian_list_item_show_img, img_cover_url_list.get(i), getActivity());
            Uri uri = Uri.parse(down_video_list.get(i));
            viewHolder.tuijian_listview_item_vv.setMediaController(new android.widget.MediaController(getActivity()));
            viewHolder.tuijian_listview_item_vv.setVideoURI(uri);
            //videoView.start();
            viewHolder.tuijian_listview_item_vv.requestFocus();
            viewHolder.home_tuijian_list_item_show_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.tuijian_listview_item_vv.isPlaying()) {
                        viewHolder.home_tuijian_list_item_show_img.setVisibility(View.GONE);
                        viewHolder.tuijian_listview_item_vv.pause();//暂停
                    } else {
                        viewHolder.home_tuijian_list_item_show_img.setVisibility(View.GONE);
                        viewHolder.tuijian_listview_item_vv.start();//播放
                    }
                }
            });
            return view;
        }
    }

    class ViewHolder {
        TextView tuijian_listview_item_title;
        VideoView tuijian_listview_item_vv;
        ImageView home_tuijian_list_item_show_img;
    }
}
