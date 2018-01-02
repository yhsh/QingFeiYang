package com.xiayiye.yhsh.yhsh.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xiayiye.yhsh.yhsh.R;
import com.xiayiye.yhsh.yhsh.api.YhshAPI;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkJsonData;
import com.xiayiye.yhsh.yhsh.view.RefreshListView;

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

public class ChatFragment extends BaseHomeFragment {
    int num = 10;//默认10条笑话
    int page = 1;
    //    ArrayList<String> head_hair_title = new ArrayList<>();
    ArrayList<String> head_hair_content = new ArrayList<>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String head_hair = (String) msg.obj;
            initJsonData(head_hair);
            Log.e("打印", head_hair);
        }
    };
    private MyBaseAdapter adapter;

    private void initJsonData(String head_hair) {
        try {
            JSONObject jsonObject = new JSONObject(head_hair);
            JSONArray newslist = jsonObject.getJSONArray("newslist");
            if (page > 5) {
                Toast.makeText(getActivity(), "已加载全部数据！", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = (page - 1) * num; i < newslist.length(); i++) {
//                    head_hair_title.add(newslist.getJSONObject(i).getString("title"));
                    head_hair_content.add(newslist.getJSONObject(i).getString("content"));
                }
                if (page > 1) {
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new MyBaseAdapter();
                    dingyue_list.setAdapter(adapter);
                }
            }
//            dingyue_list.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_gallery_item, head_hair_quest));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private RefreshListView dingyue_list;

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.activity_home_city, null);
    }

    @Override
    protected void initData(View view) {
        dingyue_list = view.findViewById(R.id.home_city_list);
        dingyue_list.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新数据
                initNetworkData(num * page);
                dingyue_list.setOnLoadFinish();
            }
        });
        dingyue_list.setOnLoadingMoreListener(new RefreshListView.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                page++;
                initNetworkData(num * page);
                dingyue_list.setOnLoadIngMoreFinish();
            }
        });
        initNetworkData(num * page);
    }

    private void initNetworkData(int number) {
        ProgressDialog pd = ProgressDialog.show(getActivity(), "加载数据", "正在加载更多数据", false, false);
        GetNetworkJsonData.TakeNetworkData(YhshAPI.TONGUE_TWISTER + number, handler, 0, pd, getActivity(), "UTF-8");
    }

    @Override
    protected void initListener(View view) {
        dingyue_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                doSendSMSTo("", head_hair_content.get(i));
            }
        });
    }

    private class MyBaseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return head_hair_content.size();
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
                convertView = View.inflate(getActivity(), R.layout.activity_home_dingyue_item, null);
                viewHolder = new ViewHolder();
                viewHolder.dingyue_list_item_request = convertView.findViewById(R.id.dingyue_list_item_request);
                viewHolder.dingyue_list_item_result = convertView.findViewById(R.id.dingyue_list_item_result);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.dingyue_list_item_request.setText("标题：");
            viewHolder.dingyue_list_item_result.setText("内容：" + head_hair_content.get(i).replace("<br/>",""));
            return convertView;
        }
    }

    class ViewHolder {
        TextView dingyue_list_item_request, dingyue_list_item_result;
    }

    /**
     * 调起系统发短信功能
     *
     * @param phoneNumber
     * @param message
     */
    public void doSendSMSTo(String phoneNumber, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body", message);
        startActivity(intent);

//        打开系统短信界面的方法
       /* Intent intentFinalMessage = new Intent(Intent.ACTION_VIEW);
        intentFinalMessage.setType("vnd.android-dir/mms-sms");
        startActivity(intentFinalMessage);
        Uri uri2 = Uri.parse("smsto:"+10086);
        Intent intentMessage = new Intent(Intent.ACTION_VIEW,uri2);
        startActivity(intentMessage);*/


    }
}
