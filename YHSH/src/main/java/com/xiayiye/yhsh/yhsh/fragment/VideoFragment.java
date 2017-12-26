package com.xiayiye.yhsh.yhsh.fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2017/12/25.14:53
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 * 应用包名：com.xiayiye.yhsh.yhsh.fragment
 * 项目名称: QingFeiYang
 */

public class VideoFragment extends BaseHomeFragment {
    ArrayList<String> video_url_title = new ArrayList<>();//视频地址标题集合
    ArrayList<String> video_url_list = new ArrayList<>();//视频的url地址集合
    ArrayList<String> img_cover_url_list = new ArrayList<>();//视频预览图的url地址集合
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String video_str = (String) msg.obj;
            initJsonData(video_str);
        }
    };
    private ProgressDialog pd;
    private ListView home_video_list;

    private void initJsonData(String video_str) {
        //拿到获取的json数据解析
        try {
            JSONObject jsonObject = new JSONObject(video_str);
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject group = jsonArray.getJSONObject(i).getJSONObject("group");
                if (group.has("download_url")) {
                    video_url_list.add(group.getString("download_url"));
                    video_url_title.add(group.getString("content"));
                    JSONArray img_url_list = group.getJSONObject("large_cover").getJSONArray("url_list");
                    img_cover_url_list.add(img_url_list.getJSONObject(0).getString("url"));
//                        Log.e("打印视频预览图", img_cover_url_list.toString());
                } else {
                    return;//不获取任何数据
                }
            }
//            Log.e("打印视频地址", video_url_list.toString() + "预览图地址：" + img_cover_url_list.toString());
            home_video_list.setAdapter(new MyBaseAdapter());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.activity_home_video, null);
    }

    @Override
    protected void initData(View view) {
        home_video_list = view.findViewById(R.id.home_video_list);
        pd = ProgressDialog.show(getActivity(), "加载数据中", "正在请求数据", false, false);
        //初始化网络
        initNetwork();
    }

    private void initNetwork() {
        GetNetworkJsonData.TakeNetworkData(YhshAPI.NEIHAN_VIDEO, handler, 0, pd, getActivity(), "UTF-8");
    }

    @Override
    protected void initListener(View view) {

    }

    private class MyBaseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return video_url_list.size();
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
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            final viewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.activity_home_video_item, null);
                viewHolder = new viewHolder();
                viewHolder.home_video_list_item_title = convertView.findViewById(R.id.home_video_list_item_title);
                viewHolder.home_video_list_item_vv = convertView.findViewById(R.id.home_video_list_item_vv);
                viewHolder.home_video_list_item_show_img = convertView.findViewById(R.id.home_video_list_item_show_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (VideoFragment.viewHolder) convertView.getTag();
            }
            viewHolder.home_video_list_item_title.setText(video_url_title.get(i));
//            Log.e("打印预览图",img_cover_url_list.get(i));
            GetNetworkImage.initNetWorkImage(viewHolder.home_video_list_item_show_img, img_cover_url_list.get(i), getActivity());
            viewHolder.home_video_list_item_vv.setMediaController(new android.widget.MediaController(getActivity()));
            Uri uri = Uri.parse(video_url_list.get(i));
            viewHolder.home_video_list_item_vv.setVideoURI(uri);
            viewHolder.home_video_list_item_vv.requestFocus();
            viewHolder.home_video_list_item_show_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.home_video_list_item_vv.isPlaying()) {
                        viewHolder.home_video_list_item_show_img.setVisibility(View.GONE);
                        viewHolder.home_video_list_item_vv.pause();//暂停
                    } else {
                        viewHolder.home_video_list_item_show_img.setVisibility(View.GONE);
                        viewHolder.home_video_list_item_vv.start();//播放
                    }
                }
            });
            return convertView;
        }
    }

    class viewHolder {
        TextView home_video_list_item_title;
        VideoView home_video_list_item_vv;
        ImageView home_video_list_item_show_img;
    }
}
