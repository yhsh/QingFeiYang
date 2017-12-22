package com.xiayiye.yhsh.yhsh.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2017/12/19.9:17
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 */

public class MoveFragment extends BaseHomeFragment {
    ArrayList<ImageView> list = new ArrayList<>();
    ArrayList<String> list_move_detail_url = new ArrayList<>();//电影详情的URL
    ArrayList<String> list_move_detail_id_url = new ArrayList<>();//电影详情id的URL
    ArrayList<String> list_move_iv_url = new ArrayList<>();//电影图片URL
    ArrayList<String> list_move_detail_id = new ArrayList<>();//电影详情id值
    ArrayList<String> list_move_tittle = new ArrayList<>();//电影名字
    ArrayList<String> list_move_leader = new ArrayList<>();//导演
    ArrayList<String> list_move_actor = new ArrayList<>();//演员
    ArrayList<String> list_move_rating = new ArrayList<>();//电影评分
    ArrayList<String> list_move_type = new ArrayList<>();//电影类型

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String str_result = (String) msg.obj;
            if (msg.what == 0) {
                jsonInit(str_result);
            } else if (msg.what == 1) {
                initJsonMoveDetail(str_result);
            }
        }
    };

    private void initJsonMoveDetail(String str_result) {
        try {
            JSONObject jsonObject = new JSONObject(str_result);
            String share_url = jsonObject.getString("share_url");
            list_move_detail_url.add(share_url);
//            Log.e("打印url", list_move_detail_url.toString()+"-----");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ListView home_two_content_list;
    private ProgressDialog progressDialog;
    private ViewPager home_two_header_viewpager;

    private void jsonInit(String str_result) {
        //拿到的json串
        try {
            JSONObject jsonObject = new JSONObject(str_result);
            String title = jsonObject.getString("title");
            JSONArray subjects = jsonObject.getJSONArray("subjects");
            JSONObject jsonArray = subjects.getJSONObject(0);
            String original_title = jsonArray.getString("original_title");
            //保存对应的数据到集合
            for (int i = 0; i < subjects.length(); i++) {
                list_move_tittle.add(subjects.getJSONObject(i).getString("original_title"));//电影名字
                if (subjects.getJSONObject(i).getJSONArray("directors").length() == 0) {
                    list_move_leader.add("未知");
                } else {
                    list_move_leader.add(subjects.getJSONObject(i).getJSONArray("directors").getJSONObject(0).getString("name"));//导演
                }
                JSONArray casts = subjects.getJSONObject(i).getJSONArray("casts");
                for (int j = 0; j < casts.length(); j++) {
                    list_move_actor.add(casts.getJSONObject(j).getString("name"));//演员
                }
                JSONArray genres = subjects.getJSONObject(i).getJSONArray("genres");
                for (int j = 0; j < genres.length(); j++) {
                    list_move_type.add(genres.getString(0));//电影类型
                }
                list_move_rating.add(subjects.getJSONObject(i).getJSONObject("rating").getString("average"));//评分
                list_move_iv_url.add(subjects.getJSONObject(i).getJSONObject("images").getString("medium"));//电影海报图片
                list_move_detail_id.add(subjects.getJSONObject(i).getString("id"));//电影详情ID值
//                initNetWork(YhshAPI.DOUBAN_MOVE_BASE + list_move_detail_id.get(i), 1);
            }
            for (int i = 0; i < list_move_detail_id.size(); i++) {
                list_move_detail_id_url.add(YhshAPI.DOUBAN_MOVE_BASE + list_move_detail_id.get(i));
            }
            for (int i = 0; i < list_move_detail_id_url.size(); i++) {
                initNetWork(YhshAPI.DOUBAN_MOVE_BASE + list_move_detail_id.get(i), 1);
            }
//            Log.e("打印id", list_move_detail_id.toString());
//            Toast.makeText(getActivity(), "打印名字" + list_move_tittle.toString() + "\n打印导演" + list_move_leader.toString() + "\n打印演员" + list_move_actor.toString(), Toast.LENGTH_LONG).show();
//            Log.e("打印名字", list_move_tittle.toString() + "\n打印导演" + list_move_leader.toString() + "\n打印演员" + list_move_actor.toString());
            home_two_content_list.setAdapter(new MyBaseAdapter());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.activity_home_two, null);
//        return null;
    }

    @Override
    protected void initData(View view) {
        progressDialog = ProgressDialog.show(getActivity(), "请稍后", "加载数据中…………", false, false);
        initNetWork(YhshAPI.DOUBAN_MOVE, 0);
        ImageView imageView1 = new ImageView(getActivity());
        imageView1.setImageResource(R.mipmap.guide0);
        imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView imageView2 = new ImageView(getActivity());
        imageView2.setImageResource(R.mipmap.guide1);
        imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView imageView3 = new ImageView(getActivity());
        imageView3.setImageResource(R.mipmap.guide2);
        imageView3.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView imageView4 = new ImageView(getActivity());
        imageView4.setImageResource(R.mipmap.guide3);
        imageView4.setScaleType(ImageView.ScaleType.FIT_XY);
        list.add(imageView1);
        list.add(imageView2);
        list.add(imageView3);
        list.add(imageView4);
        home_two_header_viewpager = view.findViewById(R.id.home_two_header_viewpager);
        home_two_content_list = view.findViewById(R.id.home_two_content_list);
        home_two_header_viewpager.setAdapter(new MyPagerAdapter());
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
                    } else {
                        dismissDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dismissDialog();
                }

            }
        }.start();
    }

    private void dismissDialog() {
        progressDialog.dismiss();//关闭对话框
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "服务器错误！", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void initListener(View view) {
        home_two_content_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
//                intent.putExtra("detail_url", news_img.get(i));
                Log.e("打印url", list_move_detail_url.toString() + "-----" + list_move_detail_url.size());
                intent.putExtra("detail_url", list_move_detail_url.get(i));
//                Log.e("打印URL", share_url + "===========");
                startActivity(intent);
            }
        });
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
//            return true;
            return view == o;//不能直接返回true，会出乱图片覆盖错位
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView(list.get(position));
        }
    }

    private class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.activity_home_two_move, null);
                viewHolder = new ViewHolder();
                viewHolder.home_two_move_iv = convertView.findViewById(R.id.home_two_move_iv);
                viewHolder.home_two_move_tv_move_name = convertView.findViewById(R.id.home_two_move_tv_move_name);
                viewHolder.home_two_move_tv_author = convertView.findViewById(R.id.home_two_move_tv_author);
                viewHolder.home_two_move_tv_actor = convertView.findViewById(R.id.home_two_move_tv_actor);
                viewHolder.home_two_move_tv_move_type = convertView.findViewById(R.id.home_two_move_tv_move_type);
                viewHolder.home_two_move_tv_score = convertView.findViewById(R.id.home_two_move_tv_score);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
//            Log.e("打印数据", list_move_tittle.size()+"==");
            GetNetworkImage.initNetWorkImage(viewHolder.home_two_move_iv, list_move_iv_url.get(i), getActivity());
            progressDialog.dismiss();//关闭对话框
            viewHolder.home_two_move_tv_move_name.setText("电影名称：" + list_move_tittle.get(i));//电影名字
            viewHolder.home_two_move_tv_author.setText("导演：" + list_move_leader.get(i));//导演
            viewHolder.home_two_move_tv_actor.setText("演员：" + list_move_actor.get(i));//演员
            viewHolder.home_two_move_tv_move_type.setText("类型：" + list_move_type.get(i));//电影类型
            viewHolder.home_two_move_tv_score.setText("评分：" + list_move_rating.get(i));//电影评分
            return convertView;
        }

    }

    class ViewHolder {
        ImageView home_two_move_iv;
        TextView home_two_move_tv_move_name, home_two_move_tv_author, home_two_move_tv_actor, home_two_move_tv_move_type, home_two_move_tv_score;
    }
}
