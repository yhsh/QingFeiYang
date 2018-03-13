package com.xiayiye.yhsh.yhsh;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiayiye.yhsh.yhsh.api.YhshAPI;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkJsonData;
import com.xiayiye.yhsh.yhsh.view.RefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchMVActivity extends BaseActivity {

    private EditText home_search_mv_et;
    private Button home_search_mv_bt;
    private ProgressDialog pd;
    private boolean isSearch = false;
    private boolean isOpen = false;
    private int default_num = 10;
    private RefreshListView home_search_mv_rl;
    private int page = 1;
    ArrayList<String> hash_list = new ArrayList<>();//mv的hash值的集合
    ArrayList<String> singer_list = new ArrayList<>();//歌手值的集合
    ArrayList<String> song_list = new ArrayList<>();//歌曲值的集合
    ArrayList<String> mv_url_list = new ArrayList<>();//MV的播放地址
    private KGMVListAdapter adapter;
    //    private String mv_download_url = "http://fs.mv.web.kugou.com/201803121636/bfa7b16538a850c3d4b4033e90fcdb07/G042/M06/00/1F/yoYBAFXuwdiAXc_2AU4xQp80K64234.mkv";//MV的下载地址
    private String mv_download_url;//MV的下载地址
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            jsonMsg(msg);
        }
    };

    private void jsonMsg(Message msg) {
        if (msg.what == 0) {
            String search_song = (String) msg.obj;
            //打印搜索到的json数据
//                Log.e("打印data搜索数据", search_song);
            initJsonData(search_song);
            if (page > 1) {
                adapter.notifyDataSetChanged();
            }
        } else if (msg.what == 1) {
            //通过hash值拼接搜索得到MV的播放地址
            String str_mv_data = (String) msg.obj;
//                Log.e("打印搜索数据", str_mv_data);
            initJsonDataMVUrl(str_mv_data);
        }
    }

    private void initJsonDataMVUrl(String str_mv_data) {
        //解析MV的播放地址
        try {
            JSONObject jsonObject = new JSONObject(str_mv_data);
            mv_download_url = jsonObject.getJSONObject("mvdata").getJSONObject("sd").getString("downurl");
//            Log.e("打印MV地址", mv_download_url);
            mv_url_list.add(mv_download_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initJsonData(String search_song) {
        //开始解析歌曲的f属性
        try {
            JSONObject jsonObject = new JSONObject(search_song);
            JSONArray json_list = jsonObject.getJSONObject("data").getJSONArray("lists");
            if (page == 1) {
                hash_list.clear();
                singer_list.clear();
                song_list.clear();
            }
            for (int i = 0; i < json_list.length(); i++) {
                hash_list.add(json_list.getJSONObject(i).getString("MvHash"));
                singer_list.add(json_list.getJSONObject(i).getString("SingerName"));
                song_list.add(json_list.getJSONObject(i).getString("MvName"));
            }
            //通过hash值搜索MV的播放地址
            for (int i = 0; i < hash_list.size(); i++) {
                GetNetworkJsonData.TakeNetworkData(YhshAPI.KUGOU_MUSIC_MV_URL_BASE + hash_list.get(i) + YhshAPI.KUGOU_MUSIC_MV_URL_QUALITY, handler, 1, pd, this, "UTF-8");
            }
            showMVList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMVList() {
        adapter = new KGMVListAdapter();
        home_search_mv_rl.setAdapter(adapter);
    }

    class KGMVListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return hash_list.size();
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
                convertView = View.inflate(SearchMVActivity.this, R.layout.activity_home_srarch_song_item, null);
                viewHolder = new ViewHolder();
                viewHolder.home_search_song_song_name = convertView.findViewById(R.id.home_search_song_song_name);
                viewHolder.home_search_singer_song_name = convertView.findViewById(R.id.home_search_song_singer_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
//            Log.e("打印歌曲数量", song_list.size() + "");
            /*if (song_list.get(i).contains("<")) {
                String sp_song_name = song_list.get(i).substring(4, song_list.get(i).length() - 5);
                viewHolder.home_search_song_song_name.setText("MV歌名：" + sp_song_name);
            } else {
                viewHolder.home_search_song_song_name.setText("MV歌名：" + song_list.get(i));
            }
            viewHolder.home_search_singer_song_name.setText("MV歌手：" + singer_list.get(i));*/


            if (song_list.get(i).contains("<em>")) {
//                String sp_song_name = song_list.get(i).substring(4, song_list.get(i).length() - 5);
                viewHolder.home_search_song_song_name.setText("MV歌名：" + song_list.get(i).replace("<em>", "").replace("</em>", ""));
            } else {
                viewHolder.home_search_song_song_name.setText("MV歌名：" + song_list.get(i));
            }
            if (singer_list.get(i).contains("<em>")) {
                viewHolder.home_search_singer_song_name.setText("MV歌手：" + singer_list.get(i).replace("<em>", "").replace("</em>", ""));
            } else {
                viewHolder.home_search_singer_song_name.setText("MV歌手：" + singer_list.get(i));
            }
            return convertView;
        }
    }

    class ViewHolder {
        TextView home_search_song_song_name, home_search_singer_song_name;
    }

    @Override
    protected View initView() {
        return View.inflate(this, R.layout.activity_search_mv, null);
    }

    @Override
    protected void initData(View view) {
        home_search_mv_et = view.findViewById(R.id.home_search_mv_et);
        home_search_mv_bt = view.findViewById(R.id.home_search_mv_bt);
        home_search_mv_rl = view.findViewById(R.id.home_search_mv_rl);
        home_search_mv_bt.setOnClickListener(this);
        refreshAndLoadMoreData();//下拉刷新和下拉加载更多的方法
        longClickItemFunction();//长按歌曲下载
        clickItemFuntion();//点击歌曲进行播放
    }

    private void refreshAndLoadMoreData() {
        home_search_mv_rl.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                song_list.clear();
//                singer_list.clear();//清空之前的数据
                page = 1;//初始化page为第一页
                getMVMessage(page);
                home_search_mv_rl.setOnLoadFinish();//加载完成
            }
        });
        home_search_mv_rl.setOnLoadingMoreListener(new RefreshListView.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                page++;
                getMVMessage(page);
                home_search_mv_rl.setOnLoadIngMoreFinish();
            }
        });
    }

    private void clickItemFuntion() {
        home_search_mv_rl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GetNetworkJsonData.TakeNetworkData(YhshAPI.KUGOU_MUSIC_MV_URL_BASE + hash_list.get(i - 1) + YhshAPI.KUGOU_MUSIC_MV_URL_QUALITY, handler, 1, pd, SearchMVActivity.this, "UTF-8");
                Toast.makeText(SearchMVActivity.this, "跳转播放歌MV", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SearchMVActivity.this, PlayMVActivity.class);
                intent.putExtra("mv_play_url", mv_url_list.get(i - 1));//歌曲播放链接
                intent.putExtra("mv_sing_name", song_list.get(i - 1));//歌曲名字
                intent.putExtra("mv_singer_name", singer_list.get(i - 1));//歌手名字
                startActivity(intent);
            }
        });
    }

    private void longClickItemFunction() {
        home_search_mv_rl.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //长按弹出下载
                if (Build.VERSION.SDK_INT < 23) {
                    GetNetworkJsonData.TakeNetworkData(YhshAPI.KUGOU_MUSIC_MV_URL_BASE + hash_list.get(i - 1) + YhshAPI.KUGOU_MUSIC_MV_URL_QUALITY, handler, 1, pd, SearchMVActivity.this, "UTF-8");
                    Toast.makeText(SearchMVActivity.this, "开始下载MV", Toast.LENGTH_LONG).show();
                    if (song_list.get(i - 1).contains("<em>")) {
                        String sp_song_name = song_list.get(i - 1).replace("<em>", "").replace("</em>", "");//替换特殊符号后的歌曲名字
                        if (singer_list.get(i - 1).contains("<em>")) {
                            String sp_singer_name = singer_list.get(i - 1).replace("<em>", "").replace("</em>", "");//替换特殊符号后的歌手名字
                            downSing(mv_url_list.get(i - 1), sp_singer_name, sp_song_name);
                        } else {
                            downSing(mv_url_list.get(i - 1), singer_list.get(i - 1), sp_song_name);
                        }
                    } else {
                        if (singer_list.get(i - 1).contains("<em>")) {
                            String sp_singer_name = singer_list.get(i - 1).replace("<em>", "").replace("</em>", "");//替换特殊符号后的歌手名字
                            downSing(mv_url_list.get(i - 1), sp_singer_name, song_list.get(i - 1));
                        } else {
                            downSing(mv_url_list.get(i - 1), singer_list.get(i - 1), song_list.get(i - 1));
                        }
                    }
                } else {
                    Toast.makeText(SearchMVActivity.this, "你手机版本高于Android6.0请手动打开权限下载MV", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_search_mv_bt:
                if (isSearch) {
                    //清空所有之前搜索的数据
                    song_list.clear();
                    singer_list.clear();
//                    song_url_list.clear();//清空之前的歌曲地址
                    page = 1;//再次搜索初始化第一页
                }
                //点击之前获取文本框歌曲信息
                getMVMessage(page);
                isSearch = true;
                break;
        }
    }

    private void getMVMessage(int page) {
        String song_name = home_search_mv_et.getText().toString().trim();
        if (TextUtils.isEmpty(song_name)) {
            Toast.makeText(this, "歌曲名称不能为空!", Toast.LENGTH_LONG).show();
        } else {
            pd = ProgressDialog.show(this, "获取数据", "请稍等，获取歌曲中…………", false, false);
            //不为空，网络请求查找歌曲
            try {
                String search_str = URLEncoder.encode(song_name, "utf-8");//将中文转码成16进制,播放点击的歌曲
//            String search_str = URLEncoder.encode("老公天下第一", "utf-8");//将中文转码成16进制
                GetNetworkJsonData.TakeNetworkData(YhshAPI.KUGOU_MUSIC_MV_SEARCH_BASE + search_str + YhshAPI.KUGOU_MUSIC_MV_SEARCH_PAGE + page + YhshAPI.KUGOU_MUSIC_MV_SEARCH_PAGESIZE + default_num + YhshAPI.KUGOU_MUSIC_MV_SEARCH_OTHER, handler, 0, pd, this, "UTF-8");
//                Log.e("打印搜索数据", YhshAPI.KUGOU_MUSIC_MV_SEARCH_BASE + search_str + YhshAPI.KUGOU_MUSIC_MV_SEARCH_PAGE + page + YhshAPI.KUGOU_MUSIC_MV_SEARCH_PAGESIZE + default_num + YhshAPI.KUGOU_MUSIC_MV_SEARCH_OTHER);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据播放地址下载歌曲的方法
     *
     * @param sing_play_url 歌曲的播放地址
     * @param singer_name   歌手名字
     * @param sing_name     歌曲名字
     */
    private void downSing(final String sing_play_url, final String singer_name, final String sing_name) {
        //根据播放歌曲地址下载音乐
        new Thread(new Runnable() {
            @Override
            public void run() {
                //请求网络
                try {
                    URL url = new URL(sing_play_url);
                    HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                    huc.setConnectTimeout(10000);
                    huc.setReadTimeout(10000);
                    if (huc.getResponseCode() == 200) {
                        //网络请求成功，获取流信息，转成歌曲文件
                        InputStream is = huc.getInputStream();
                        File file1 = new File(Environment.getExternalStorageDirectory() + "/Music_download/");
                        File file2 = new File(Environment.getExternalStorageDirectory() + "/Music_download/", sing_name + "_" + singer_name + ".mkv");
                        //不存在创建
                        if (!file1.exists()) {
                            file1.mkdir();
                        }
                        if (file2.exists()) {
                            ShowSingExits(sing_name + "_" + singer_name + "已存在" + Environment.getExternalStorageDirectory() + "/Music_download/文件夹中" + "无需下载");
                            return;
                        }
                        ShowSingExits(sing_name + "_" + singer_name + "正在下载中,请到" + Environment.getExternalStorageDirectory() + "/Music_download/文件夹中查看！");
                        //创建字节流
                        byte[] bs = new byte[1024];
                        int len;
                        OutputStream os = new FileOutputStream(file2);
                        //写数据
                        while ((len = is.read(bs)) != -1) {
                            os.write(bs, 0, len);
                        }
                        //完成后关闭流
                        os.close();
                        is.close();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }) {
        }.start();
    }

    /**
     * 进行下载状态提示的方法
     *
     * @param str 提示的信息
     */
    private void ShowSingExits(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SearchMVActivity.this, str, Toast.LENGTH_LONG).show();
            }
        });
    }
}
