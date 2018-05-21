package com.xiayiye.yhsh.yhsh.fragment;
/**
 * Copyright (c) 2018, smuyyh@gmail.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG             #
 * #                                                   #
 */


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xiayiye.yhsh.yhsh.PlayMusicActivity;
import com.xiayiye.yhsh.yhsh.R;
import com.xiayiye.yhsh.yhsh.api.YhshAPI;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkJsonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2018/5/21.11:44
 * 个人小站：http://wap.yhsh.ai(已挂)
 * 最新小站：http://www.iyhsh.icoc.in
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 * 空间名称：QingFeiYang
 * 项目包名：com.xiayiye.yhsh.yhsh.fragment
 */
public class NewMusicQQFragment extends BaseHomeFragment {
    ArrayList<String> sing_name = new ArrayList<>();//歌曲名称
    ArrayList<String> sing_url = new ArrayList<>();//歌曲url集合
    ArrayList<String> singer_name = new ArrayList<>();//歌手名字
    ArrayList<String> sing_large = new ArrayList<>();//歌曲大小
    ArrayList<String> singer_id = new ArrayList<>();//歌曲id
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                String new_music_json = (String) msg.obj;
                if (new_music_json != null) {
                    initNewMusicJson(new_music_json);
                }
                pd.dismiss();
            }
        }
    };
    private ProgressDialog pd;
    private ListView home_new_music_lv;

    private void initNewMusicJson(String new_music_json) {
        //拿到最新歌曲的json数据
        try {
            JSONObject json_music = new JSONObject(new_music_json);
            JSONArray songlist = json_music.getJSONArray("songlist");
            for (int i = 0; i < songlist.length(); i++) {
                sing_name.add(songlist.getJSONObject(i).getJSONObject("data").getString("songname"));
                singer_name.add(songlist.getJSONObject(i).getJSONObject("data").getJSONArray("singer").getJSONObject(0).getString("name"));
                singer_id.add(songlist.getJSONObject(i).getJSONObject("data").getString("songmid"));
            }
            home_new_music_lv.setAdapter(new NewMusicQQFragment.NewMusicBaseAdapter());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.activity_home_music_new, null);
    }

    @Override
    protected void initData(View view) {
        pd = ProgressDialog.show(getActivity(), "获取数据", "请稍等，获取最新歌曲中…………", false, false);
        GetNetworkJsonData.TakeNetworkData(YhshAPI.LATEST_QQ_MUSIC, handler, 0, pd, getActivity(), "UTF-8");
        home_new_music_lv = view.findViewById(R.id.home_new_music_lv);
    }

    @Override
    protected void initListener(View view) {
        home_new_music_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PlayMusicActivity.class);
                intent.putExtra("sing_name", sing_name.get(i));
                intent.putExtra("singer_name", singer_name.get(i));
                //用来识别是QQ还是酷狗播放来源
                intent.putExtra("playPage", "QQ");
                for (int j = 0; j < singer_id.size(); j++) {
                    sing_url.add(YhshAPI.QQMUSIC_SING_URL_BASE + "C100" + singer_id.get(j) + YhshAPI.QQMUSIC_SING_ERL_END + "&guid=126548448");
                }
                Log.e("打印url", sing_url.get(i) + "");
                intent.putExtra("sing_play_url", sing_url.get(i));
                startActivity(intent);
            }
        });
    }

    private class NewMusicBaseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return sing_name.size();
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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.activity_home_music_new_list_item_qq, null);
                viewHolder = new NewMusicQQFragment.ViewHolder();
                viewHolder.home_music_new_list_item_tv_sing_name = convertView.findViewById(R.id.home_music_new_list_item_tv_sing_name);
                viewHolder.home_music_new_list_item_tv_singer_name = convertView.findViewById(R.id.home_music_new_list_item_tv_singer_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (NewMusicQQFragment.ViewHolder) convertView.getTag();
            }
            viewHolder.home_music_new_list_item_tv_sing_name.setText("歌曲：" + sing_name.get(i));//歌曲名字
            viewHolder.home_music_new_list_item_tv_singer_name.setText("歌手：" + singer_name.get(i));//歌手名字
            return convertView;
        }
    }

    class ViewHolder {
        TextView home_music_new_list_item_tv_sing_name, home_music_new_list_item_tv_singer_name;
    }
}
