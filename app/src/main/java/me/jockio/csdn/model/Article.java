package me.jockio.csdn.model;

/**
 * Created by jockio on 16/6/11.
 */

/*
{"articleid":51628994,"title":"LeetCode:Triangle","username":"itismelzp",
"posttime":"2016-06-10 22:20:29","ago":"昨天 22:20","viewcount":813,
"channelId":16,"channel":"编程语言","pageindex":2,"pagecount":35,
"photo":"http://avatar.csdn.net/5/5/7/1_itismelzp.jpg"}
*/
public class Article {
    /**
     * articleid : 51628994
     * username : itismelzp
     * posttime : 2016-06-10 22:20:29
     * ago : 昨天 22:20
     * viewcount : 813
     * channelId : 16
     * channel : 编程语言
     * pageindex : 2
     * pagecount : 35
     * photo : http://avatar.csdn.net/5/5/7/1_itismelzp.jpg
     * title : android 开发
     */

    private int articleid;
    private String username;
    private String posttime;
    private String ago;
    private int viewcount;
    private int channelId;
    private String channel;
    private int pageindex;
    private int pagecount;
    private String photo;
    private String title;

    public Article(){}

    @Override
    public String toString() {
        String str = "articleid = " + articleid
                + ", username = "  + username
                + ", posttime = "  + posttime
                + ", ago = "  + ago
                + ", viewcount = "  + viewcount
                + ", channelId = "  + channelId
                + ", channel = "  + channel
                + ", pageindex = "  + pageindex
                + ", pagecount = "  + pagecount
                + ", photo = "  + photo
                + ", title = "  + title;
        return str;
    }

    public int getArticleid() {
        return articleid;
    }

    public void setArticleid(int articleid) {
        this.articleid = articleid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPosttime() {
        return posttime;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }

    public String getAgo() {
        return ago;
    }

    public void setAgo(String ago) {
        this.ago = ago;
    }

    public int getViewcount() {
        return viewcount;
    }

    public void setViewcount(int viewcount) {
        this.viewcount = viewcount;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getPageindex() {
        return pageindex;
    }

    public void setPageindex(int pageindex) {
        this.pageindex = pageindex;
    }

    public int getPagecount() {
        return pagecount;
    }

    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
