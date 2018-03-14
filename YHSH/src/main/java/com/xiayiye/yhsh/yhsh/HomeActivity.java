package com.xiayiye.yhsh.yhsh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xiayiye.yhsh.yhsh.api.YhshAPI;
import com.xiayiye.yhsh.yhsh.fragment.FourFragment;
import com.xiayiye.yhsh.yhsh.fragment.MoveFragment;
import com.xiayiye.yhsh.yhsh.fragment.MusicFragment;
import com.xiayiye.yhsh.yhsh.fragment.NewsFragment;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkJsonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity {
    ArrayList<String> list_date = new ArrayList<>();//相同日期的年份
    ArrayList<String> list_tittle = new ArrayList<>();//历史今天发生的事情
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String str = (String) msg.obj;
            initJson(str);
        }
    };
    private TextView home_tv_pmd;
    private String history_today = "";
    private RadioButton rb_move;
    private RadioButton rb_news;
    private RadioButton rb_three;
    private RadioButton rb_four;
    private boolean isNo;

    private void initJson(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            if (jsonObject.getString("code").equals("200")) {
                JSONArray newslist = jsonObject.getJSONArray("newslist");
                for (int i = 0; i < newslist.length(); i++) {
                    list_date.add(newslist.getJSONObject(i).getString("lsdate"));
                    list_tittle.add(newslist.getJSONObject(i).getString("title"));
                    history_today = history_today + list_date.get(i) + "-" + list_tittle.get(i) + "\n" + "                      点击此处：联系本人QQ！                     ";
                }
                home_tv_pmd.setText("                           历史上的今天：" + history_today);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getFragmentManager().beginTransaction().replace(R.id.fl_replace, new MoveFragment()).commit();
        FrameLayout fl_replace = findViewById(R.id.fl_replace);
        home_tv_pmd = findViewById(R.id.home_tv_pmd);
        RadioGroup rg_bottom = findViewById(R.id.rg_bottom);
        rb_move = findViewById(R.id.rb_move);
        rb_news = findViewById(R.id.rb_news);
        rb_three = findViewById(R.id.rb_three);
        rb_four = findViewById(R.id.rb_four);
        home_tv_pmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isQQClientAvailable(HomeActivity.this)) {
                    //联系我QQ
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=13343401268&version=1")));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setTitle("温馨提示：").setMessage("清先安装QQ再联系本人");
                    builder.setPositiveButton("安装", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //跳转到默认应用市场安装QQ
                            boolean b = goToMarket();
                            if (b) {
                                //如果没有应用市场，就跳转默认浏览器
                                Uri uri = Uri.parse("https://im.qq.com/download/");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        }
                    });
                    builder.setNegativeButton("不安装", null);
                    builder.show();
                }
            }
        });
        rg_bottom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Fragment fragment;
                switch (i) {
                    case R.id.rb_move:
                        fragment = new MoveFragment();
                        fragmentTransaction.replace(R.id.fl_replace, fragment).commit();
                        break;
                    case R.id.rb_news:
                        fragment = new NewsFragment();
                        fragmentTransaction.replace(R.id.fl_replace, fragment).commit();
                        break;
                    case R.id.rb_three:
                        fragment = new MusicFragment();
                        fragmentTransaction.replace(R.id.fl_replace, fragment).commit();
                        break;
                    case R.id.rb_four:
                        fragment = new FourFragment();
                        fragmentTransaction.replace(R.id.fl_replace, fragment).commit();
                        break;
                }
            }
        });
        ProgressDialog pd = ProgressDialog.show(this, "", "");
        GetNetworkJsonData.TakeNetworkData(YhshAPI.HISTORY_TODAY, handler, 0, pd, this, "UTF-8");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_main_default:
                //切换默认主题
                changeToTheme(HomeActivity.this, 1);
                break;
            case R.id.home_main_night:
                //切换夜间主题
                changeToTheme(HomeActivity.this, 2);
                rb_move.setTextColor(getResources().getColor(R.color.dodgerblue));
                rb_news.setTextColor(getResources().getColor(R.color.dodgerblue));
                rb_three.setTextColor(getResources().getColor(R.color.dodgerblue));
                rb_four.setTextColor(getResources().getColor(R.color.dodgerblue));
                break;
            case R.id.home_main_light:
                //切换白天主题
                changeToTheme(HomeActivity.this, 3);
                break;
            case R.id.home_main_tools:
                //跳转到小工具页面
                startActivity(new Intent(this, SmallToolsActivity.class));
                break;
            case R.id.home_main_about:
                //跳转到关于页面
                startActivity(new Intent(this, AboutYHSHActivity.class));
                break;
            case R.id.home_song_search:
                //跳转到搜索歌曲页面
                startActivity(new Intent(this, SearchSongActivity.class));
                break;
            case R.id.home_mv_search:
                //跳转到搜索MV页面
                startActivity(new Intent(this, SearchMVActivity.class));
                break;
        }
        return true;
    }

    /* @Override
     public boolean onMenuItemSelected(int featureId, MenuItem item) {
         return super.onMenuItemSelected(featureId, item);
     }*/
    private static int themes;

    public static void changeToTheme(Activity activity, int theme) {
        themes = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        //通过下面的一个动画基本看不出来页面跳转
        activity.overridePendingTransition(0, 0);
    }

    public void onActivityCreateSetTheme(Activity activity) {
        switch (themes) {
            case 1:
                activity.setTheme(R.style.AppTheme);
                break;
            case 2:
                activity.setTheme(R.style.ThemeNight);
                break;
            case 3:
                activity.setTheme(R.style.themeLight);
                break;
        }
    }

    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                Log.e("打印包名", "pn = " + pn);
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 跳转到应用市场下载
     * @return 根据返回的Boolean
     * 判断是进入应用市场下载还是打开默认浏览器进行网页下载
     */
    public boolean goToMarket() {
        Uri uri = Uri.parse("market://details?id=" + "com.tencent.mobileqq");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
            isNo = false;//表示应用市场存在
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

}
