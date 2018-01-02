package com.xiayiye.yhsh.yhsh;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xiayiye.yhsh.yhsh.api.YhshAPI;
import com.xiayiye.yhsh.yhsh.fragment.FourFragment;
import com.xiayiye.yhsh.yhsh.fragment.NewsFragment;
import com.xiayiye.yhsh.yhsh.fragment.MusicFragment;
import com.xiayiye.yhsh.yhsh.fragment.MoveFragment;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkJsonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    private void initJson(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            if (jsonObject.getString("code").equals("200")) {
                JSONArray newslist = jsonObject.getJSONArray("newslist");
                for (int i = 0; i < newslist.length(); i++) {
                    list_date.add(newslist.getJSONObject(i).getString("lsdate"));
                    list_tittle.add(newslist.getJSONObject(i).getString("title"));
                    history_today = history_today + list_date.get(i) + "-" + list_tittle.get(i) + "\n";
                }
                home_tv_pmd.setText(history_today);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getFragmentManager().beginTransaction().replace(R.id.fl_replace, new MoveFragment()).commit();
        FrameLayout fl_replace = findViewById(R.id.fl_replace);
        home_tv_pmd = findViewById(R.id.home_tv_pmd);
        RadioGroup rg_bottom = findViewById(R.id.rg_bottom);
        RadioButton rb_one = findViewById(R.id.rb_move);
        RadioButton rb_two = findViewById(R.id.rb_news);
        RadioButton rb_three = findViewById(R.id.rb_three);
        RadioButton rb_four = findViewById(R.id.rb_four);
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
}
