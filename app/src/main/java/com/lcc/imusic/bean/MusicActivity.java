package com.lcc.imusic.bean;

import java.util.List;

/**
 * Created by lufficc on 2016/11/16.
 */

public class MusicActivity {
    /**
     * Msg : OK
     * Code : 200
     * Result : {"totalRow":4,"pageNumber":1,"firstPage":true,"lastPage":true,"totalPage":1,"pageSize":20,"list":[{"addtime":"2016-10-23 23:47:26","admin":1,"id":21,"title":"www","type":2,"content":"<p>5<\/p>","views":0},{"addtime":"2016-06-13 01:09:24","admin":1,"id":19,"title":"重庆草莓音乐节","type":2,"content":"<p>举办时间：2016年6月18-19日<\/p><p>举办地点：重庆中央公园（渝北区）<\/p>","views":0},{"addtime":"2016-03-23 02:01:31","admin":1,"id":2,"title":"124","type":2,"content":"<p>1111<\/p>","views":0},{"addtime":null,"admin":null,"id":18,"title":"richtext2","type":2,"content":"<p><b><img asdfasf/><\/b><img sdfasdf/><\/p>","views":0}]}
     */

    public String Msg;
    public int Code;
    /**
     * totalRow : 4
     * pageNumber : 1
     * firstPage : true
     * lastPage : true
     * totalPage : 1
     * pageSize : 20
     * list : [{"addtime":"2016-10-23 23:47:26","admin":1,"id":21,"title":"www","type":2,"content":"<p>5<\/p>","views":0},{"addtime":"2016-06-13 01:09:24","admin":1,"id":19,"title":"重庆草莓音乐节","type":2,"content":"<p>举办时间：2016年6月18-19日<\/p><p>举办地点：重庆中央公园（渝北区）<\/p>","views":0},{"addtime":"2016-03-23 02:01:31","admin":1,"id":2,"title":"124","type":2,"content":"<p>1111<\/p>","views":0},{"addtime":null,"admin":null,"id":18,"title":"richtext2","type":2,"content":"<p><b><img asdfasf/><\/b><img sdfasdf/><\/p>","views":0}]
     */

    public ResultBean Result;

   

    public static class ResultBean {
        public int totalRow;
        public int pageNumber;
        public boolean firstPage;
        public boolean lastPage;
        public int totalPage;
        public int pageSize;
        /**
         * addtime : 2016-10-23 23:47:26
         * admin : 1
         * id : 21
         * title : www
         * type : 2
         * content : <p>5</p>
         * views : 0
         */

        public List<ActivityBean> list;


       
    }
}
