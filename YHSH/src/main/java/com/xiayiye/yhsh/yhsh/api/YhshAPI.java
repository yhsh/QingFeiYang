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
    String QQMUSIC_SING_SEARCH_BASE = "http://s.music.qq.com/fcgi-bin/music_search_new_platform?t=0&n=";//QQ音乐搜索歌曲基类
    String QQMUSIC_SING_SEARCH_END = "&aggr=1&cr=1&loginUin=0&format=json&inCharset=GB2312&outCharset=utf-8&notice=0&platform=jqminiframe.json&needNewCode=0&p=1&catZhida=0&remoteplace=sizer.newclient.next_song&w=";//QQ音乐搜索歌曲尾类后面加上搜歌歌曲歌名即可
    String QQMUSIC_SING_ERL_END = ".m4a?fromtag=46";//QQ音乐播放链接后缀
    String NEIHAN_TUIJIAN = "http://lf.snssdk.com/neihan/stream/mix/v1/?content_type=-101";//内涵段子首页推荐
    String NEIHAN_VIDEO = "http://lf.snssdk.com/neihan/stream/mix/v1/?content_type=-104";//内涵段子首页视频
    String NEIHAN_PICTURE = "http://lf.snssdk.com/neihan/stream/mix/v1/?content_type=-103";//内涵段子首页图片
    String NEIHAN_DUANZI = "http://lf.snssdk.com/neihan/stream/mix/v1/?content_type=-102";//内涵段子首页段子
    String GANHUO_PICTURE = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/";//干货美图地址
//    String GANHUO_PICTURE_END = "583/1";//干货美图地址


    String HISTORY_TODAY = "http://api.tianapi.com/txapi/lishi/?key=e666faa73326fd34b46b2ae23964a91b";//历史上的今天(天行数据API)
    String HEAD_HAIR = "http://api.tianapi.com/txapi/naowan/?key=e666faa73326fd34b46b2ae23964a91b&num=";//脑筋急转弯(天行数据API)，默认一条数据
    String JOKE = "http://api.tianapi.com/txapi/joke/?key=e666faa73326fd34b46b2ae23964a91b&num=";//雷人笑话(天行数据API)
    String RIDDLE = "http://api.tianapi.com/txapi/cityriddle/?key=e666faa73326fd34b46b2ae23964a91b&num=";//地名谜语(天行数据API)
    String TONGUE_TWISTER = "http://api.tianapi.com/txapi/rkl/?key=e666faa73326fd34b46b2ae23964a91b&num=";//绕口令(天行数据API)
    String IT_NEWS = "http://api.tianapi.com/it/?key=e666faa73326fd34b46b2ae23964a91b&num=";//IT新闻(天行数据API)
    String HOME_IMG_BANDER_ONE = "https://www.2345.com/right/homepage/img/block1701171030/tab1/20171226094434.jpg";//第一张轮播图
    String HOME_IMG_BANDER_TWO = "https://www.2345.com/right/homepage/img/block1701171030/tab1/20171229133309.jpg";//第二张轮播图
    String HOME_IMG_BANDER_THREE = "https://www.2345.com/right/homepage/img/block1701171030/tab2/20171230100122.jpg";//第三张轮播图
    String HOME_IMG_BANDER_FOUR = "http://www.2345.com/right/homepage/img/block1701171030/tab1/20171214095647.jpg";//第四张轮播图
    //    String KUGOU_MUSIC_ALL_BASE = "http://songsearch.kugou.com/song_search_v2?callback=jQuery1910026785707623246724_1490845878865&keyword=%E6%90%9C%E7%B4%A2%E6%AD%8C%E6%9B%B2%E5%90%8D&page=1&pagesize=";//后面加上int参数，现实多少首歌曲
    String KUGOU_MUSIC_ALL_BASE = "http://songsearch.kugou.com/song_search_v2?callback=jQuery1910026785707623246724_1490845878865&keyword=";//后面加上int参数，现实多少首歌曲
    String KUGOU_MUSIC_ALL_SONG_NAME = "&page=1&pagesize=";//歌曲名+此链接
    String KUGOU_MUSIC_ALL_BASE_PAGE = "&userid=-1&%20clientver=&platform=WebFilter&tag=em&filter=2&iscorrection=1&privilege_filter=0&_=1490845878887";//KUGOU_MUSIC_ALL_BASE拼接上这个
    String KUGOU_MUSIC_ALL_FILEHASH = "http://www.kugou.com/yy/index.php?r=play/getdata&hash=";//KUGOU_MUSIC_FileHash拼接上这个FileHash
    String TRAIN_BASE = "http://apis.juhe.cn/train/s?name=";//查询火车车次的url基类
    String TRAIN_KEY = "&key=f008317fef660e600c4acbc811d3493b";//查询火车车次的key
}
