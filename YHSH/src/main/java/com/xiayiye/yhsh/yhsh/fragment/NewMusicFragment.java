package com.xiayiye.yhsh.yhsh.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
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
import android.widget.Toast;

import com.xiayiye.yhsh.yhsh.PlayMusicActivity;
import com.xiayiye.yhsh.yhsh.PlayMusicNewActivity;
import com.xiayiye.yhsh.yhsh.R;
import com.xiayiye.yhsh.yhsh.api.YhshAPI;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkImage;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkJsonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2017/12/21.13:06
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 * 应用包名：com.xiayiye.yhsh.yhsh.fragment
 * 项目名称: QingFeiYang
 */

public class NewMusicFragment extends BaseHomeFragment {
    ArrayList<String> sing_name = new ArrayList<>();//歌曲名称
    ArrayList<String> sing_url = new ArrayList<>();//歌曲url集合
    ArrayList<String> singer_name = new ArrayList<>();//歌手名字
    ArrayList<String> sing_large = new ArrayList<>();//歌曲大小
    ArrayList<String> sing_albumId = new ArrayList<>();//歌曲图片id
    ArrayList<String> singer_id = new ArrayList<>();//歌曲id
    Handler handler = new Handler() {
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
        String sub_str = new_music_json.substring(13, new_music_json.length() - 48);
//        sub_str = sub_str.substring(0, sub_str.length() - 1);
        sub_str.substring(sub_str.length());
//        Log.e("打印最后一个", sub_str.substring(sub_str.length() - 10, sub_str.length()));
        //解析json数据
        try {
            JSONObject json_music = new JSONObject(sub_str);
            JSONArray songlist = json_music.getJSONArray("songlist");
            for (int i = 0; i < songlist.length(); i++) {
                sing_name.add(songlist.getJSONObject(i).getString("songName"));
                singer_name.add(songlist.getJSONObject(i).getString("singerName"));
                singer_id.add(songlist.getJSONObject(i).getString("id"));
                sing_large.add(songlist.getJSONObject(i).getString("playtime"));
                sing_albumId.add(songlist.getJSONObject(i).getString("albumId"));
            }
//            Log.e("打印歌曲：", sing_name + "歌手：" + singer_name);
//            Toast.makeText(getActivity(), sing_name.toString(), Toast.LENGTH_LONG).show();
            home_new_music_lv.setAdapter(new NewMusicBaseAdapter());
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
        GetNetworkJsonData.TakeNetworkData(YhshAPI.QQMUSIC_NEW_MUSIC, handler, 0, pd, getActivity(), "gbk");
        home_new_music_lv = view.findViewById(R.id.home_new_music_lv);
    }

    @Override
    protected void initListener(View view) {
        home_new_music_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sing_id = singer_id.get(i);
                String sing_play_url = YhshAPI.QQMUSIC_SING_URL_BASE + sing_id + YhshAPI.QQMUSIC_SING_ERL_END;
                Log.e("最新歌曲播放链接", sing_play_url);
//                Intent intent = new Intent(getActivity(), PlayMusicActivity.class);
                Intent intent = new Intent(getActivity(), PlayMusicNewActivity.class);
                /*intent.putExtra("sing_name", sing_name.get(i));
                intent.putExtra("singer_name", singer_name.get(i));
                intent.putExtra("sing_play_url", sing_play_url);*/
                intent.putExtra("sing_name", sing_name);
                intent.putExtra("play_number", i);
                intent.putExtra("singer_name", singer_name);
                intent.putExtra("playPage", "QQ");//用来识别是QQ还是酷狗播放来源
                for (int j = 0; j < singer_id.size(); j++) {
                    sing_url.add(YhshAPI.QQMUSIC_SING_URL_BASE + singer_id.get(j) + YhshAPI.QQMUSIC_SING_ERL_END);
                }
                intent.putExtra("sing_play_url", sing_url);
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
                convertView = View.inflate(getActivity(), R.layout.activity_home_music_new_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.home_music_new_list_item_iv_sing_img = convertView.findViewById(R.id.home_music_new_list_item_iv_sing_img);
                viewHolder.home_music_new_list_item_tv_sing_name = convertView.findViewById(R.id.home_music_new_list_item_tv_sing_name);
                viewHolder.home_music_new_list_item_tv_singer_name = convertView.findViewById(R.id.home_music_new_list_item_tv_singer_name);
                viewHolder.home_music_new_list_item_tv_sing_large = convertView.findViewById(R.id.home_music_new_list_item_tv_sing_large);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            int img_id = Integer.valueOf(sing_albumId.get(i));
//            {image_id%100}/${width}_albumpic_${image_id}
            String s_img_id = String.valueOf(img_id % 100);
            s_img_id = s_img_id + "/300" + "_albumpic_" + sing_albumId.get(i);
//            Log.e("图片地址：", YhshAPI.QQMUSIC_MUSIC_IMG_URL_BASE + s_img_id + YhshAPI.QQMUSIC_IMG_URL_END);
            GetNetworkImage.initNetWorkImage(viewHolder.home_music_new_list_item_iv_sing_img, YhshAPI.QQMUSIC_MUSIC_IMG_URL_BASE + s_img_id + YhshAPI.QQMUSIC_IMG_URL_END, getActivity());//获取网络图片
            viewHolder.home_music_new_list_item_tv_sing_name.setText("歌曲：" + sing_name.get(i));//歌曲名字
            viewHolder.home_music_new_list_item_tv_singer_name.setText("歌手：" + singer_name.get(i));//歌手名字
            viewHolder.home_music_new_list_item_tv_sing_large.setText("时间：" + Integer.valueOf(sing_large.get(i)) / 60 + ":" + Integer.valueOf(sing_large.get(i)) % 60);//歌曲的时间
            return convertView;
        }
    }

    class ViewHolder {
        ImageView home_music_new_list_item_iv_sing_img;
        TextView home_music_new_list_item_tv_sing_name, home_music_new_list_item_tv_singer_name, home_music_new_list_item_tv_sing_large;
    }
}
