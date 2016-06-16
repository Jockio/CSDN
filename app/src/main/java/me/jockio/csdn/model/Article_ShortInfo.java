package me.jockio.csdn.model;

/*
 {
 	"_type":"csdn",
 	"_id":"8563447",
 	"_index":"so_blog_v2",
 	"object":{
 		"id":8563447,
 		"title":"Android 后台Service : 向服务器发送心跳包",
 		"source_type":"blog",
 		"_uid":"csdn#8563447",
 		"type":"blog"
 		},
 	"fields":[11.111973,3134213]
 }
*/

/**
 * Created by zhangsj-fnst on 2016/6/16/0016.
 */
public class Article_ShortInfo {
    private String _type;
    private String _id;
    private String _index;
    private ArticleObject articleObject;
    private String fields;

    public Article_ShortInfo() {
    }

    public Article_ShortInfo(String _type, String _id, String _index, ArticleObject articleObject, String fields) {
        this._type = _type;
        this._id = _id;
        this._index = _index;
        this.articleObject = articleObject;
        this.fields = fields;
    }

    @Override
    public String toString() {
        String string = "_type = " + _type
                + ", _id = " + _id
                + ", _index = " + _index
                + articleObject.toString()
                + ", fields = " + fields;
        return string;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_index() {
        return _index;
    }

    public void set_index(String _index) {
        this._index = _index;
    }

    public ArticleObject getArticleObject() {
        return articleObject;
    }

    public void setArticleObject(ArticleObject articleObject) {
        this.articleObject = articleObject;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }
}