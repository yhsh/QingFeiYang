package com.xiayiye.yhsh.yhsh.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiayiye.yhsh.yhsh.BigPictureActivity;
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

public class PictureFragment extends BaseHomeFragment {
    List<String> picture_url = new ArrayList<>();//图片地址
    List<String> picture_des = new ArrayList<>();//图片描述
    private ProgressDialog pd;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String picture_json = (String) msg.obj;
            initPictureJson(picture_json);
        }
    };
    private boolean isSwitch = false;//默认GridView视图
    private ListView home_picture_lv;
    private GridView home_picture_gv;
    private Button home_picture_bt;

    private void initPictureJson(String picture_json) {
        //解析图片json数据
        try {
            JSONObject jsonObject = new JSONObject(picture_json);
            JSONArray results = jsonObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                picture_url.add(results.getJSONObject(i).getString("url"));//图片地址
                picture_des.add(results.getJSONObject(i).getString("who") + ":" + results.getJSONObject(i).getString("desc"));//图片描述信息
            }
            home_picture_gv.setAdapter(new MyBaseAdapter());
            home_picture_lv.setAdapter(new MyBaseAdapter());
            switchPicture(home_picture_bt);
            home_picture_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    Toast.makeText(getActivity(), "点击了图片1", Toast.LENGTH_SHORT).show();
                    //跳转到大图浏览
                    enterBigPicture(picture_url.get(i));
                }
            });
            home_picture_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    Toast.makeText(getActivity(), "点击了图片2", Toast.LENGTH_SHORT).show();
                    //跳转到大图浏览
                    enterBigPicture(picture_url.get(i));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.activity_home_picture, null);
    }

    @Override
    protected void initData(View view) {
        home_picture_gv = view.findViewById(R.id.home_picture_gv);
        home_picture_lv = view.findViewById(R.id.home_picture_lv);
        home_picture_bt = view.findViewById(R.id.home_picture_bt);
        pd = ProgressDialog.show(getActivity(), "请求数据中", "获取最新数据", false, false);
        getNetworkData();
    }

    /**
     * 切换视图的方法
     *
     * @param home_picture_bt
     */
    private void switchPicture(Button home_picture_bt) {
        home_picture_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSwitch) {
                    //切换GridView视图
                    home_picture_gv.setVisibility(View.VISIBLE);
                    home_picture_lv.setVisibility(View.GONE);
                } else {
                    //切换listview视图
                    home_picture_gv.setVisibility(View.GONE);
                    home_picture_lv.setVisibility(View.VISIBLE);
                }
                isSwitch = !isSwitch;
            }
        });
    }

    private void getNetworkData() {
        GetNetworkJsonData.TakeNetworkData(YhshAPI.GANHUO_PICTURE + "10/1", handler, 0, pd, getActivity(), "UTF_8");//预先默认加载10张
    }

    @Override
    protected void initListener(View view) {

    }

    private void enterBigPicture(String s) {
        Intent intent = new Intent(getActivity(), BigPictureActivity.class);
        intent.putExtra("picture_url", s);
        startActivity(intent);
    }

    private class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return picture_url.size();
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
                convertView = View.inflate(getActivity(), R.layout.activity_home_picture_item, null);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
                viewHolder.home_picture_lv_item_des = convertView.findViewById(R.id.home_picture_lv_item_des);
                viewHolder.home_picture_lv_item_iv = convertView.findViewById(R.id.home_picture_lv_item_iv);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.home_picture_lv_item_des.setText(picture_des.get(i));//设置图片描述信息
            GetNetworkImage.initNetWorkImage(viewHolder.home_picture_lv_item_iv, picture_url.get(i), getActivity());//获取网络图片
            return convertView;
        }
    }

    class ViewHolder {
        TextView home_picture_lv_item_des;
        ImageView home_picture_lv_item_iv;
    }
}
