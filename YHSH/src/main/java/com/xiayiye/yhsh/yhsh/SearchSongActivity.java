package com.xiayiye.yhsh.yhsh;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class SearchSongActivity extends BaseActivity {

    private EditText home_search_song_et;
    private Button home_search_song_bt;
    private RefreshListView home_search_song_rl;
    private ProgressDialog pd;
    ArrayList<String> song_name = new ArrayList<>();//歌曲名字
    ArrayList<String> singer_name = new ArrayList<>();//歌手名字
    ArrayList<String> song_url_list = new ArrayList<>();//歌曲链接集合
    ArrayList<String> list_songmid = new ArrayList<>();//歌曲的songmid
    int default_num = 10;
    int page = 1;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String search_song = (String) msg.obj;
                //打印搜索到的json数据
//                Log.e("打印搜索数据", search_song);
                initJsonData(search_song);
                if (page > 1) {
                    adapter.notifyDataSetChanged();
                }
            } else if (msg.what == 1) {
                String search_song = (String) msg.obj;
                String sub_str = search_song.substring(9, search_song.length() - 1);
                Log.e("打印json数据", sub_str + "-----");
                initJsonData(sub_str);
            }
        }
    };
    private String song_url;
    private QQListAdapter adapter;
    private boolean isSearch = false;
    private boolean isOpen;
    private RadioButton play_source_rb_one;
    private RadioButton play_source_rb_two;

    @Override
    protected View initView() {
        return View.inflate(this, R.layout.activity_search_song, null);
    }

    @Override
    protected void initData(View view) {
        home_search_song_et = view.findViewById(R.id.home_search_song_et);
        home_search_song_bt = view.findViewById(R.id.home_search_song_bt);
        home_search_song_rl = view.findViewById(R.id.home_search_song_rl);
        play_source_rb_one = findViewById(R.id.play_source_rb_one);
        play_source_rb_two = findViewById(R.id.play_source_rb_two);
        home_search_song_bt.setOnClickListener(this);
        refreshAndLoadMoreData();//下拉刷新和下拉加载更多的方法
        longClickItemFunction();//长按歌曲下载
        clickItemFuntion();//点击歌曲进行播放
    }

    private void refreshAndLoadMoreData() {
        home_search_song_rl.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                song_name.clear();
                singer_name.clear();//清空之前的数据
                getSongMessage(default_num * page);
                home_search_song_rl.setOnLoadFinish();//加载完成
            }
        });
        home_search_song_rl.setOnLoadingMoreListener(new RefreshListView.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                page++;
                getSongMessage(default_num * page);
                home_search_song_rl.setOnLoadIngMoreFinish();
            }
        });
    }

    private void clickItemFuntion() {
        home_search_song_rl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchSongActivity.this, PlayMusicActivity.class);

                /*intent.putExtra("search_song_url_list", song_url_list);//歌曲播放链接
                intent.putExtra("search_song_name", song_name);//歌曲名字
                intent.putExtra("search_singer_name", singer_name);//歌手名字*/

                intent.putExtra("sing_play_url", song_url_list.get(i - 1));//歌曲播放链接
                intent.putExtra("sing_name", song_name.get(i - 1));//歌曲名字
                intent.putExtra("singer_name", singer_name.get(i - 1));//歌手名字
                startActivity(intent);
            }
        });
    }

    private void longClickItemFunction() {
        home_search_song_rl.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //长按弹出下载
                if (Build.VERSION.SDK_INT < 23) {
                    Toast.makeText(SearchSongActivity.this, "开始下载歌曲", Toast.LENGTH_LONG).show();
                    downSing(song_url_list.get(i - 1), singer_name.get(i - 1), song_name.get(i - 1));
                } else {
                    if (isOpen) {
                        downSing(song_url_list.get(i - 1), singer_name.get(i - 1), song_name.get(i - 1));
                    } else {
                        isOpen = true;//开启了权限，为了减小APK 体积，不调用动态权限申请，直接跳转到权限管理设置手动开启存储权限，假设跳到此页面后打开存储权限了
                        getAndroidVersion();
                    }
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
            case R.id.home_search_song_bt:
                if (isSearch) {
                    //清空所有之前搜索的数据
                    song_name.clear();
                    singer_name.clear();
                    song_url_list.clear();//清空之前的歌曲地址
                    page = 1;//再次搜索初始化第一页
                    adapter.notifyDataSetChanged();
                }
                //点击之前获取文本框歌曲信息
                getSongMessage(default_num * page);
                isSearch = true;
                break;
        }
    }

    private void getSongMessage(int page) {
        String song_name = home_search_song_et.getText().toString().trim();
        if (TextUtils.isEmpty(song_name)) {
            Toast.makeText(this, "歌曲名称不能为空!", Toast.LENGTH_LONG).show();
        } else {
            pd = ProgressDialog.show(this, "获取数据", "请稍等，获取歌曲中…………", false, false);
            //不为空，网络请求查找歌曲
            try {
                String search_str = URLEncoder.encode(song_name, "utf-8");//将中文转码成16进制,播放点击的歌曲
//            String search_str = URLEncoder.encode("老公天下第一", "utf-8");//将中文转码成16进制
                if (play_source_rb_one.isChecked()) {
                    GetNetworkJsonData.TakeNetworkData(YhshAPI.QQMUSIC_SING_SEARCH_BASE + page + YhshAPI.QQMUSIC_SING_SEARCH_END + search_str, handler, 0, pd, this, "UTF-8");
                } else if (play_source_rb_two.isChecked()) {
                    GetNetworkJsonData.TakeNetworkData(YhshAPI.QQMUSIC_SING_SEARCH_BASE2 + search_str + YhshAPI.QQMUSIC_SING_SEARCH_END2, handler, 1, pd, this, "UTF-8");
                }
//                Log.e("打印搜索数据", YhshAPI.QQMUSIC_SING_SEARCH_BASE + "20" + YhshAPI.QQMUSIC_SING_SEARCH_END + search_str);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void initJsonData(String search_song) {
        //开始解析歌曲的f属性
        try {
            JSONObject jsonObject = new JSONObject(search_song);
            if (play_source_rb_one.isChecked()) {
                //            String song_f = jsonObject.getJSONObject("data").getJSONObject("song").getJSONArray("list").getJSONObject(0).getString("f");
                JSONArray song_array = jsonObject.getJSONObject("data").getJSONObject("song").getJSONArray("list");
                for (int i = 0; i < song_array.length(); i++) {
                    String song_f = song_array.getJSONObject(i).getString("f");
                    //将字符串切割，获取到倒数第六个属性进行拼接参数播放歌曲
                    String[] split = song_f.split("\\|");
                    String s = split[split.length - 5];//解析出来的f属性值来拼接播放歌曲
                    song_url = YhshAPI.QQMUSIC_SING_URL_BASE + "C100" + s + YhshAPI.QQMUSIC_SING_ERL_END;
                    song_url_list.add(song_url);
                }
                JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONObject("song").getJSONArray("list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    song_name.add(jsonArray.getJSONObject(i).getString("fsong"));
                    singer_name.add(jsonArray.getJSONObject(i).getString("fsinger"));
                }
                //显示搜索的歌曲列表
                showSongList(song_name, singer_name);
//            Log.e("打印歌曲属性f", song_f);
            /*//将字符串切割，获取到倒数第六个属性进行拼接参数播放歌曲
            String[] split = song_f.split("\\|");
            String s = split[split.length - 5];//解析出来的f属性值来拼接播放歌曲
            song_url = YhshAPI.QQMUSIC_SING_URL_BASE + "C100" + s + YhshAPI.QQMUSIC_SING_ERL_END;
            song_url_list.add(song_url);*/
//            Log.e("打印歌曲属性f", split[split.length - 5] + "===" + song_final_url);
//            Log.e("QQ音乐最终播放地址", song_url_list.toString());
            } else if (play_source_rb_two.isChecked()) {
                JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONObject("song").getJSONArray("list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    list_songmid.add(jsonArray.getJSONObject(i).getString("songmid"));
                    song_name.add(jsonArray.getJSONObject(i).getString("songname"));
                    singer_name.add(jsonArray.getJSONObject(i).getJSONArray("singer").getJSONObject(0).getString("name"));
                    song_url_list.add(YhshAPI.QQMUSIC_SING_URL_BASE + "C100" + list_songmid.get(i) + YhshAPI.QQMUSIC_SING_ERL_END + "&guid=126548448");
                }
                //显示搜索的歌曲列表
                showSongList(song_name, singer_name);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void showSongList(final ArrayList<String> song_name, final ArrayList<String> singer_name) {
        adapter = new QQListAdapter();
        home_search_song_rl.setAdapter(adapter);
    }

    class QQListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return song_name.size();
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
                convertView = View.inflate(SearchSongActivity.this, R.layout.activity_home_srarch_song_item, null);
                viewHolder = new ViewHolder();
                viewHolder.home_search_song_song_name = convertView.findViewById(R.id.home_search_song_song_name);
                viewHolder.home_search_singer_song_name = convertView.findViewById(R.id.home_search_song_singer_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.home_search_song_song_name.setText("歌名：" + song_name.get(i));
            viewHolder.home_search_singer_song_name.setText("歌手：" + singer_name.get(i));
            return convertView;
        }
    }

    class ViewHolder {
        TextView home_search_song_song_name, home_search_singer_song_name;
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
                        File file2 = new File(Environment.getExternalStorageDirectory() + "/Music_download/", sing_name + "_" + singer_name + ".m4a");
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
                Toast.makeText(SearchSongActivity.this, str, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 获取Android手机型号以及版本
     */
    private void getAndroidVersion() {
        String sdk = android.os.Build.VERSION.SDK; // SDK号

        String model = android.os.Build.MODEL; // 手机型号

        String release = android.os.Build.VERSION.RELEASE; // android系统版本号
        String brand = Build.BRAND;//手机厂商
        if (TextUtils.equals(brand.toLowerCase(), "redmi") || TextUtils.equals(brand.toLowerCase(), "xiaomi")) {
            Toast.makeText(SearchSongActivity.this, "你的小米手机版本高于Android6.0，请手动打开下载存储权限", Toast.LENGTH_LONG).show();
            gotoMiuiPermission();//小米
        } else if (TextUtils.equals(brand.toLowerCase(), "meizu")) {
            Toast.makeText(SearchSongActivity.this, "你的魅族手机版本高于Android6.0，请手动打开下载存储权限", Toast.LENGTH_LONG).show();
            gotoMeizuPermission();
        } else if (TextUtils.equals(brand.toLowerCase(), "huawei") || TextUtils.equals(brand.toLowerCase(), "honor")) {
            Toast.makeText(SearchSongActivity.this, "你的华为手机版本高于Android6.0，请手动打开下载存储权限", Toast.LENGTH_LONG).show();
            gotoHuaweiPermission();
        } else {
            startActivity(getAppDetailSettingIntent());
        }
    }

    /**
     * 跳转到miui的权限管理页面
     */
    private void gotoMiuiPermission() {
        try { // MIUI 8
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", getPackageName());
            startActivity(localIntent);
        } catch (Exception e) {
            try { // MIUI 5/6/7
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", getPackageName());
                startActivity(localIntent);
            } catch (Exception e1) { // 否则跳转到应用详情
                startActivity(getAppDetailSettingIntent());
            }
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    private void gotoMeizuPermission() {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(getAppDetailSettingIntent());
        }
    }

    /**
     * 华为的权限管理页面
     */
    private void gotoHuaweiPermission() {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(getAppDetailSettingIntent());
        }

    }

    /**
     * 获取应用详情页面intent（如果找不到要跳转的界面，<span style="font-size:18px;">也可以先把用户引导到系统设置页面</span>）
     *
     * @return
     */
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        return localIntent;
    }
}
