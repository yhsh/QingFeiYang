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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiayiye.yhsh.yhsh.NewsDetailActivity;
import com.xiayiye.yhsh.yhsh.R;
import com.xiayiye.yhsh.yhsh.api.YhshAPI;
import com.xiayiye.yhsh.yhsh.tools.GetNetworkImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2017/12/19.9:16
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 */

public class NewsFragment extends BaseHomeFragment {

    private ListView home_one_list;
    private String[] stringArray;
    private String[] data_url;
    ArrayList<String> news_tittle = new ArrayList<>();
    ArrayList<String> news_detail_id = new ArrayList<>();
    ArrayList<String> news_detail_url = new ArrayList<>();//新闻详情url
    String news_date;//新闻的日期
    ArrayList<String> news_img = new ArrayList<>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String zhihu_news = (String) msg.obj;
            if (msg.what == 0) {
                initJsonData(zhihu_news);
            } else if (msg.what == 1) {
                initJsonNewsDetail(zhihu_news);
            }
        }
    };

    /**
     * 知乎新闻详情页面
     *
     * @param zhihu_news 知乎新闻详情页面URL
     */
    private void initJsonNewsDetail(String zhihu_news) {
        try {
            JSONObject jsonObject = new JSONObject(zhihu_news);
           String share_url = jsonObject.getString("share_url");
            news_detail_url.add(share_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ProgressDialog progressDialog;

    /**
     * 解析json数据
     *
     * @param zhihu_news json数据
     */
    private void initJsonData(String zhihu_news) {
        try {
            JSONObject jsonObject = new JSONObject(zhihu_news);
            news_date = jsonObject.getString("date");
            JSONArray stories = jsonObject.getJSONArray("stories");
            for (int i = 0; i < stories.length(); i++) {
                String title = stories.getJSONObject(i).getString("title");
                String news_id = stories.getJSONObject(i).getString("id");
//                Log.e("打印图片网址：", stories.getJSONObject(i).getJSONArray("images").getString(0));
                String news_img_url = stories.getJSONObject(i).getJSONArray("images").getString(0);
                news_tittle.add(title);
                news_img.add(news_img_url);
                news_detail_id.add(news_id);
//                initNetWork(YhshAPI.ZHIHU_NEWS_BASE + news_detail_id.get(i), 1);//请求新闻详情页面获取URL
            }
            for (int i = 0; i < news_detail_id.size(); i++) {
                initNetWork(YhshAPI.ZHIHU_NEWS_BASE + news_detail_id.get(i), 1);//请求新闻详情页面获取URL
            }
//            Log.e("打印新闻标题:", news_tittle.toString());
            home_one_list.setAdapter(new MyBaseAdapter());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.activity_home_one, null);
    }

    @Override
    protected void initData(View view) {
        home_one_list = view.findViewById(R.id.home_one_list);
        stringArray = getResources().getStringArray(R.array.data);
        data_url = getResources().getStringArray(R.array.data_url);
        final int imgs[] = {R.mipmap.bank_bj, R.mipmap.bank_gd,
                R.mipmap.bank_gf, R.mipmap.bank_gs, R.mipmap.bank_hx,
                R.mipmap.bank_js, R.mipmap.bank_jt, R.mipmap.bank_ms,
                R.mipmap.bank_ny, R.mipmap.bank_pa, R.mipmap.bank_pf,
                R.mipmap.bank_sh, R.mipmap.bank_xy, R.mipmap.bank_yz,
                R.mipmap.bank_zg, R.mipmap.bank_zs, R.mipmap.bank_zx};
        progressDialog = ProgressDialog.show(getActivity(), "请稍后", "加载数据中…………", false, false);
        initNetWork(YhshAPI.ZHIHU_NEWS, 0);//初始化数据
//        home_one_list.setAdapter(new MyBaseAdapter());
    }

    @Override
    protected void initListener(View view) {
        home_one_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                initNetWork(YhshAPI.ZHIHU_NEWS_BASE + news_detail_id.get(i), 1);
                //跳转到新闻详情页面
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
//                intent.putExtra("detail_url", news_img.get(i));
//                intent.putExtra("detail_url", share_url);
                intent.putExtra("detail_url", news_detail_url.get(i));
//                Log.e("打印URL", share_url + "===========");
                startActivity(intent);
            }
        });
    }

    private void initNetWork(final String news_url, final int type) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(news_url);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(20000);
                    urlConnection.setConnectTimeout(20000);
                    if (urlConnection.getResponseCode() == 200) {
                        InputStream is = urlConnection.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int i = -1;
                        while ((i = is.read()) != -1) {
                            baos.write(i);
                        }
                        Message obtain = Message.obtain();
                        obtain.obj = baos.toString();
                        obtain.what = type;
                        handler.sendMessage(obtain);
//                        Log.e("打印获取到的数据：", baos.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
//            return stringArray.length;
            return news_tittle.size();
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
                convertView = View.inflate(getActivity(), R.layout.activity_home_one_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.home_one_list_item_tv = convertView.findViewById(R.id.home_one_list_item_tv);
                viewHolder.home_one_list_item_tv_date = convertView.findViewById(R.id.home_one_list_item_tv_date);
                viewHolder.home_one_list_item_iv = convertView.findViewById(R.id.home_one_list_item_iv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
//            home_one_list_item_tv.setText(stringArray[i]);
//            home_one_list_item_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgs[i], 0);
            GetNetworkImage.initNetWorkImage(viewHolder.home_one_list_item_iv, news_img.get(i), getActivity());//获取网络图片
            viewHolder.home_one_list_item_tv.setText(news_tittle.get(i));
            viewHolder.home_one_list_item_tv_date.setText(news_date);
            progressDialog.dismiss();
            return convertView;
        }
    }

    class ViewHolder {
        TextView home_one_list_item_tv, home_one_list_item_tv_date;
        ImageView home_one_list_item_iv;
    }
}
