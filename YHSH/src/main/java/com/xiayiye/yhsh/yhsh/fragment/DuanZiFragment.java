package com.xiayiye.yhsh.yhsh.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiayiye.yhsh.yhsh.NewsDetailActivity;
import com.xiayiye.yhsh.yhsh.R;
import com.xiayiye.yhsh.yhsh.api.YhshAPI;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkImage;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkJsonData;
import com.xiayiye.yhsh.yhsh.view.RefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
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

public class DuanZiFragment extends BaseHomeFragment {
    int page = 1;//默认加载第一页
    int number = 10;//默认加载10条最新新闻
    ArrayList<String> list_news_ctime = new ArrayList<>();//新闻事件
    ArrayList<String> list_news_pic = new ArrayList<>();//新闻图片
    ArrayList<String> list_news_title = new ArrayList<>();//新闻标题
    ArrayList<String> list_news_url = new ArrayList<>();//新闻阅读的url地址
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            it_news_json_data = (String) msg.obj;
            initJsonData(it_news_json_data);
        }
    };
    //    private ListView home_duanzi_it_news;
    private RefreshListView home_duanzi_it_news;
    private MyBaseAdapter adapter;
    private String it_news_json_data;

    private void initJsonData(String it_news_json_data) {
        try {
            JSONObject jsonObject = new JSONObject(it_news_json_data);
            JSONArray newslist = jsonObject.getJSONArray("newslist");
            if (page > 5) {
                Toast.makeText(getActivity(), "已加载全部数据！", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();//刷新数据
                home_duanzi_it_news.setOnLoadIngMoreFinish();//加载更多完成
            } else {
                for (int i = (page - 1) * number; i < newslist.length(); i++) {
                    list_news_ctime.add(newslist.getJSONObject(i).getString("ctime"));
                    list_news_pic.add(newslist.getJSONObject(i).getString("picUrl"));
                    list_news_title.add(newslist.getJSONObject(i).getString("title"));
                    list_news_url.add(newslist.getJSONObject(i).getString("url"));
                }
                if (page > 1) {
                    adapter.notifyDataSetChanged();//刷新数据
                    home_duanzi_it_news.setOnLoadIngMoreFinish();//加载更多完成
                } else {
                    adapter = new MyBaseAdapter();
                    home_duanzi_it_news.setAdapter(adapter);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.activity_home_duanzi, null);
    }

    @Override
    protected void initData(View view) {
        home_duanzi_it_news = view.findViewById(R.id.home_duanzi_it_news);
        //初始化网络数据
        initNetworkData(page);
        //[4]设置刷新监听
        home_duanzi_it_news.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (page >= 5) {
                    initNetworkData(5);
                } else {
                    initNetworkData(page);
                }
                adapter.notifyDataSetChanged();//刷新数据
                home_duanzi_it_news.setOnLoadFinish();//刷新完成
            }
        });
        //[5]设置加载更多数据
        home_duanzi_it_news.setOnLoadingMoreListener(new RefreshListView.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                page++;
                if (page >= 5) {
                    initNetworkData(5);
                } else {
                    initNetworkData(page);
                }
                /*adapter.notifyDataSetChanged();//刷新数据
                // 设置加载完成后的逻辑
                home_duanzi_it_news.setOnLoadIngMoreFinish();//加载更多完成*/
            }
        });
    }

    private void initNetworkData(int page) {
        ProgressDialog pd = ProgressDialog.show(getActivity(), "加载数据", "更新新闻中", false, false);
        GetNetworkJsonData.TakeNetworkData(YhshAPI.IT_NEWS + page * number, handler, 0, pd, getActivity(), "UTF-8");
    }

    @Override
    protected void initListener(View view) {
        home_duanzi_it_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("detail_url", list_news_url.get(i - 1));
                startActivity(intent);
            }
        });
    }

    private class MyBaseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list_news_pic.size();
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
                convertView = View.inflate(getActivity(), R.layout.activity_home_duanzi_item, null);
                viewHolder = new ViewHolder();
                viewHolder.home_duanzi_it_news_item_title = convertView.findViewById(R.id.home_duanzi_it_news_item_title);
                viewHolder.home_duanzi_it_news_item_date = convertView.findViewById(R.id.home_duanzi_it_news_item_date);
                viewHolder.home_duanzi_it_news_item_iv = convertView.findViewById(R.id.home_duanzi_it_news_item_iv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.home_duanzi_it_news_item_title.setText(list_news_title.get(i));
            viewHolder.home_duanzi_it_news_item_date.setText(list_news_ctime.get(i));
            GetNetworkImage.initNetWorkImage(viewHolder.home_duanzi_it_news_item_iv, list_news_pic.get(i), getActivity());
            return convertView;
        }
    }

    class ViewHolder {
        TextView home_duanzi_it_news_item_title, home_duanzi_it_news_item_date;
        ImageView home_duanzi_it_news_item_iv;
    }
}
