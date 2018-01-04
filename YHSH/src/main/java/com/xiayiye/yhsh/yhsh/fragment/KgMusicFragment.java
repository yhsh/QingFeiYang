package com.xiayiye.yhsh.yhsh.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xiayiye.yhsh.yhsh.PlayMusicNewActivity;
import com.xiayiye.yhsh.yhsh.R;
import com.xiayiye.yhsh.yhsh.api.YhshAPI;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkJsonData;
import com.xiayiye.yhsh.yhsh.view.RefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2018/1/4.10:35
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 * 类的包名：com.xiayiye.yhsh.yhsh.fragment
 * 项目名称: QingFeiYang
 */

public class KgMusicFragment extends BaseHomeFragment {
    private boolean isClearListData = false;//是否情况集合，默认不请空
    String search_str = "搜索歌曲名";
    ArrayList<ListView> list_kg = new ArrayList<>();
    ArrayList<String> song_play_url = new ArrayList<>();//酷狗音乐播放地址
    ArrayList<String> song_play_lyrics = new ArrayList<>();//酷狗音乐歌词地址
    ArrayList<String> song_play_filename = new ArrayList<>();//酷狗音乐名字
    ArrayList<String> song_play_size = new ArrayList<>();//酷狗音乐大小
    ArrayList<String> song_play_singer_name = new ArrayList<>();//歌手名
    ArrayList<String> song_play_song_name = new ArrayList<>();//歌曲名
    int page = 1;
    int num = 20;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                initDataJsonOne((String) msg.obj);
            } else if (msg.what == 1) {
                initDataJsonOneUrl((String) msg.obj);
            } else if (msg.what == 2) {
                initDataJsonOneTwo((String) msg.obj);
            }
        }
    };

    private void initDataJsonOneTwo(String obj) {
        String search_song_json = obj.substring(43, obj.length()).substring(0, obj.substring(43, obj.length()).length() - 2);
//        Log.e("打印搜索歌曲数据", search_song_json);
        try {
            JSONObject jsonObject = new JSONObject(search_song_json);
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("lists");
            if (page <= 1) {
                GotJsonValues(jsonArray);
                adapter = new MyBaseAdapterOne();//1代表第一页，2代表第二页
                lv2.setAdapter(adapter);
                RefershAndLoadMore(lv2);
                isClearListData = true;//清楚集合数据
            } else if (page > 10) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("暂无更多数据！").setTitle("提示").setNegativeButton("取消", null).show();
            } else {
                GotJsonValues(jsonArray);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ProgressDialog pd;
    private RefreshListView lv1;
    private RefreshListView lv2;
    private MyBaseAdapterOne adapter;
    private EditText home_music_kg_list_item_et;

    private void initDataJsonOneUrl(String obj) {
        try {
            JSONObject js = new JSONObject(obj);
            song_play_url.add(js.getJSONObject("data").getString("play_url"));
            song_play_lyrics.add(js.getJSONObject("data").getString("lyrics"));
//            Log.e("打印歌曲地址", song_play_url.toString() + "歌词：" + song_play_lyrics.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initDataJsonOne(String obj) {
        String new_str = obj.substring(43, obj.length());
//        Log.e("打印酷狗数据", new_str.substring(0, new_str.length() - 2));
        String kg_json = new_str.substring(0, new_str.length() - 2);
        try {
            JSONObject jsonObject = new JSONObject(kg_json);
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("lists");
            if (page <= 1) {
                GotJsonValues(jsonArray);
                adapter = new MyBaseAdapterOne();
                lv1.setAdapter(adapter);
                lv2.setAdapter(adapter);
                RefershAndLoadMore(lv1);
                RefershAndLoadMore(lv2);
            } else if (page > 10) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("暂无更多数据！").setTitle("提示").setNegativeButton("取消", null).show();
            } else {
                GotJsonValues(jsonArray);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void GotJsonValues(JSONArray jsonArray) throws JSONException {
        for (int i = (page - 1) * num; i < jsonArray.length(); i++) {
            GetNetworkJsonData.TakeNetworkData(YhshAPI.KUGOU_MUSIC_ALL_FILEHASH + jsonArray.getJSONObject(i).getString("FileHash"), handler, 1, pd, getActivity(), "UTF-8");
            String file_Name = jsonArray.getJSONObject(i).getString("FileName");
            String songName = jsonArray.getJSONObject(i).getString("SongName");
            if (file_Name.contains("<em>") || file_Name.contains("</em>") || songName.contains("<em>") || songName.contains("</em>")) {
                String replace_em_filename = file_Name.replace("<em>", "").replace("</em>", "");
                String replace_em_songName = songName.replace("<em>", "").replace("</em>", "");
                song_play_filename.add(replace_em_filename);
                song_play_song_name.add(replace_em_songName);
            } else {
                song_play_filename.add(jsonArray.getJSONObject(i).getString("FileName"));
                song_play_song_name.add(jsonArray.getJSONObject(i).getString("SongName"));
            }
            song_play_size.add(jsonArray.getJSONObject(i).getString("FileSize"));
            song_play_singer_name.add(jsonArray.getJSONObject(i).getString("SingerName"));
        }
    }

    private void RefershAndLoadMore(final RefreshListView ls) {
        ls.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initNetWorkData(page, search_str);//刷新数据
                ls.setOnLoadFinish();
            }
        });
        ls.setOnLoadingMoreListener(new RefreshListView.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                page++;//加载更多
                initNetWorkData(page, search_str);//刷新数据
                ls.setOnLoadIngMoreFinish();
            }
        });
    }

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.activity_home_music_kg, null);
    }

    @Override
    protected void initData(View view) {
        try {
            search_str = URLEncoder.encode(search_str, "utf-8");//将中文转码成16进制
            Log.e("打印十六进制", search_str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        initNetWorkData(page, search_str);
        lv1 = new RefreshListView(getActivity());
        lv1.setBackgroundDrawable(getResources().getDrawable(R.drawable.kg_one));
        list_kg.add(lv1);
        lv2 = new RefreshListView(getActivity());
        lv2.setBackgroundDrawable(getResources().getDrawable(R.drawable.kg_two));
        list_kg.add(lv2);
        View inflate = View.inflate(getActivity(), R.layout.activity_home_music_kg_search_header, null);
        lv2.addHeaderView(inflate);
        home_music_kg_list_item_et = inflate.findViewById(R.id.home_music_kg_list_item_et);
        TextView home_music_kg_list_item_tv = inflate.findViewById(R.id.home_music_kg_list_item_tv);
        home_music_kg_list_item_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(home_music_kg_list_item_et.getText().toString().trim())) {
                    try {
                        String song_name = URLEncoder.encode(home_music_kg_list_item_et.getText().toString().trim(), "UTF-8");
                        initNetWorkDataSearch(page, song_name);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        ViewPager home_music_kg_vp = view.findViewById(R.id.home_music_kg_vp);
        home_music_kg_vp.setAdapter(new MyPagerAdapter());
    }


    private void initNetWorkData(int page, String song_name) {
        pd = ProgressDialog.show(getActivity(), "请求数据", "刷新酷狗音乐", false, false);
        GetNetworkJsonData.TakeNetworkData(YhshAPI.KUGOU_MUSIC_ALL_BASE + song_name + YhshAPI.KUGOU_MUSIC_ALL_SONG_NAME + page * num + YhshAPI.KUGOU_MUSIC_ALL_BASE_PAGE, handler, 0, pd, getActivity(), "UTF-8");
    }

    private void initNetWorkDataSearch(int page, String song_name) {
        pd = ProgressDialog.show(getActivity(), "请求数据", "刷新酷狗音乐", false, false);
        GetNetworkJsonData.TakeNetworkData(YhshAPI.KUGOU_MUSIC_ALL_BASE + song_name + YhshAPI.KUGOU_MUSIC_ALL_SONG_NAME + page * num + YhshAPI.KUGOU_MUSIC_ALL_BASE_PAGE, handler, 2, pd, getActivity(), "UTF-8");
    }

    @Override
    protected void initListener(View view) {
        playSong(lv1, 1);//减去头布局
        playSong(lv2, 2);//减去两个头布局
    }

    private void playSong(RefreshListView lv, final int num) {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PlayMusicNewActivity.class);
                intent.putExtra("sing_play_url", song_play_url);
                intent.putExtra("play_number", i - num);
                intent.putExtra("sing_name", song_play_song_name);
                intent.putExtra("singer_name", song_play_singer_name);
                Log.e("打印歌曲", song_play_song_name.get(i - num));
                startActivity(intent);
            }
        });
    }

    private class MyBaseAdapterOne extends BaseAdapter {

        @Override
        public int getCount() {
            return song_play_filename.size();
//            return num == 1 ? song_play_filename.size() : song_play_filename.size();
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
                convertView = View.inflate(getActivity(), R.layout.activity_home_music_kg_item, null);
                viewHolder = new ViewHolder();
                viewHolder.music_kg_song_name = convertView.findViewById(R.id.music_kg_song_name);
                viewHolder.music_kg_song_size = convertView.findViewById(R.id.music_kg_song_size);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.music_kg_song_name.setText("歌名：" + song_play_filename.get(i));
//            String song_size = Integer.valueOf(song_play_size.get(i)) / 1024 / 1024 +"."+ Integer.valueOf(song_play_size.get(i)) % 1024 / 1024+"M";
            int intNum = Integer.valueOf(song_play_size.get(i)) / 1024 / 1024;//3
            double doubleNum = (Integer.valueOf(song_play_size.get(i)) % 1000) / 102.4;//0.4
            int float_num = (int) (doubleNum % 10);
//            Log.e("打印歌曲大小", intNum + "===" + float_num);
            String song_size = intNum + "." + float_num + "M";
            viewHolder.music_kg_song_size.setText("大小：" + song_size);
            return convertView;
        }
    }

    class ViewHolder {
        TextView music_kg_song_name, music_kg_song_size;
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return list_kg.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list_kg.get(position));
            return list_kg.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            container.removeView(list_kg.get(position));
        }
    }

}
