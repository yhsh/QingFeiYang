package com.xiayiye.yhsh.yhsh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiayiye.yhsh.yhsh.api.YhshAPI;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkJsonData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrainSearchActivity extends Activity {
    ArrayList<String> list_station_name = new ArrayList<>();
    ArrayList<String> list_arrived_time = new ArrayList<>();
    ArrayList<String> list_leave_time = new ArrayList<>();
    private EditText train_search_et;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String train_json = (String) msg.obj;
            Log.e("打印火车数据", train_json);
            initJson(train_json);
        }
    };
    private ListView train_search_lv;

    private void initJson(String train_json) {
        try {
            JSONObject jsonObject = new JSONObject(train_json);
            String resultcode = jsonObject.getString("resultcode");
            if (resultcode.equals("200")) {
                JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("station_list");//车站列表
                String starttime = jsonObject.getJSONObject("result").getJSONObject("train_info").getString("starttime");//始发站时间
                String endtime = jsonObject.getJSONObject("result").getJSONObject("train_info").getString("endtime");//终点站时间
                String start = jsonObject.getJSONObject("result").getJSONObject("train_info").getString("start");//火车始发站
                String end = jsonObject.getJSONObject("result").getJSONObject("train_info").getString("end");//火车终点站
                String name = jsonObject.getJSONObject("result").getJSONObject("train_info").getString("name");//火车名字
                Log.e("打印集合1", starttime + "==" + endtime + "==" + start + "==" + end + "==" + name);
                list_station_name.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String station_name = jsonArray.getJSONObject(i).getString("station_name");//车站名字
                    String arrived_time = jsonArray.getJSONObject(i).getString("arrived_time");//到达时间
                    String leave_time = jsonArray.getJSONObject(i).getString("leave_time");//离开时间
                    list_station_name.add(station_name);
                    list_arrived_time.add(arrived_time);
                    list_leave_time.add(leave_time);
                }
//            Log.e("打印集合", list_station_name.toString());
//            train_search_lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list_station_name));
                train_search_lv.setVisibility(View.VISIBLE);//显示布局
                train_search_lv.setAdapter(new MyBaseAdapter());
            } else {
                Toast.makeText(this, jsonObject.getString("reason"), Toast.LENGTH_LONG).show();
                list_station_name.clear();
                list_arrived_time.clear();
                train_search_lv.setVisibility(View.INVISIBLE);//隐藏布局
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_search_activity);
        train_search_et = findViewById(R.id.train_search_et);
        Button train_search_bt = findViewById(R.id.train_search_bt);
        train_search_lv = findViewById(R.id.train_search_lv);
        train_search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trim = train_search_et.getText().toString().trim();
                if (!TextUtils.isEmpty(trim)) {
                    searchTrain(trim);
                } else {
                    Toast.makeText(TrainSearchActivity.this, "请输入车次在进行查询，例如：Z53", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchTrain(String trim) {
        ProgressDialog pd = ProgressDialog.show(this, "加载数据中", "正在加载", false, false);
        GetNetworkJsonData.TakeNetworkData(YhshAPI.TRAIN_BASE + trim + YhshAPI.TRAIN_KEY, handler, 0, pd, this, "UTF-8");
    }

    class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list_station_name.size();
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
                convertView = View.inflate(TrainSearchActivity.this, R.layout.train_search_activity_item, null);
                viewHolder = new ViewHolder();
                viewHolder.train_station_name = convertView.findViewById(R.id.train_station_name);
                viewHolder.train_station_time = convertView.findViewById(R.id.train_station_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.train_station_name.setText("所到车站：" + list_station_name.get(i));
            viewHolder.train_station_time.setText("到站时间：" + list_arrived_time.get(i));

            return convertView;
        }
    }

    class ViewHolder {
        TextView train_station_name, train_station_time;
    }
}
