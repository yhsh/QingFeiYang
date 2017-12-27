package com.xiayiye.yhsh.yhsh.api;

/**
 * 创 建 者：下一页5（轻飞扬）
 * 创建时间：2017/12/19.17:05
 * 个人小站：http://wap.yhsh.ai
 * 联系作者：企鹅 13343401268
 * 博客地址：http://blog.csdn.net/xiayiye5
 * 应用包名：com.xiayiye.yhsh.yhsh.api
 * 项目名称: QingFeiYang
 */

public interface YhshAPI {
    String DOUBAN_MOVE = "https://api.douban.com/v2/movie/in_theaters";
    String ZHIHU_NEWS = "http://news-at.zhihu.com/api/4/news/latest";
    String ZHIHU_NEWS_BASE = "http://news-at.zhihu.com/api/4/news/";//后面加上新闻的id值才可以查看新闻详情
    String DOUBAN_MOVE_BASE = "https://api.douban.com/v2/movie/subject/";//后面加上电影的id值才可以查看电影详情
    String QQMUSIC_NEW_MUSIC = "http://music.qq.com/musicbox/shop/v3/data/hit/hit_newsong.js";//QQ音乐最新歌曲API
    String QQMUSIC_HOT_MUSIC = "http://music.qq.com/musicbox/shop/v3/data/hit/hit_all.js";//QQ音乐最热歌曲API
    //    String QQMUSIC_MUSIC_IMG_URL = "http://imgcache.qq.com/music/photo/album_300/${image_id%100}/${width}_albumpic_${image_id}_0.jpg";//QQ音乐歌曲图片
    String QQMUSIC_MUSIC_IMG_URL = "http://imgcache.qq.com/music/photo/album_300/image_id%100/300_albumpic_image_id_0.jpg";//QQ音乐歌曲图片
    //    String QQMUSIC_MUSIC_IMG_URL = "http://imgcache.qq.com/music/photo/album_300/20/300_albumpic_140820_0.jpg";//QQ音乐歌曲图片
    String QQMUSIC_MUSIC_IMG_URL_BASE = "http://imgcache.qq.com/music/photo/album_300/";//QQ音乐歌曲图片
    String QQMUSIC_IMG_URL_END = "_0.jpg";//QQ音乐图片后缀名称
    //http://ws.stream.qqmusic.qq.com/101369814.m4a?fromtag=46
    String QQMUSIC_SING_URL_BASE = "http://ws.stream.qqmusic.qq.com/";//QQ音乐播放链接基类
    String QQMUSIC_SING_ERL_END = ".m4a?fromtag=46";//QQ音乐播放链接后缀
    String NEIHAN_TUIJIAN = "http://lf.snssdk.com/neihan/stream/mix/v1/?content_type=-101";//内涵段子首页推荐
    String NEIHAN_VIDEO = "http://lf.snssdk.com/neihan/stream/mix/v1/?content_type=-104";//内涵段子首页视频
    String NEIHAN_PICTURE = "http://lf.snssdk.com/neihan/stream/mix/v1/?content_type=-103";//内涵段子首页图片
    String NEIHAN_DUANZI = "http://lf.snssdk.com/neihan/stream/mix/v1/?content_type=-102";//内涵段子首页段子
    String GANHUO_PICTURE = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/";//干货美图地址
//    String GANHUO_PICTURE_END = "583/1";//干货美图地址
}
