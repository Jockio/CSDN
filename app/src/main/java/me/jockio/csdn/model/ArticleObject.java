package me.jockio.csdn.model;

/**
 * Created by zhangsj-fnst on 2016/6/16/0016.
 */
public class ArticleObject {
    private String id;
    private String title;
    private String source_type;
    private String _uid;
    private String type;

    public ArticleObject() {
    }

    public ArticleObject(String id, String title, String source_type, String _uid, String type) {
        this.id = id;
        this.title = title;
        this.source_type = source_type;
        this._uid = _uid;
        this.type = type;
    }

    @Override
    public String toString() {
        String string = "{id = " + id
                + ", title = " + title
                + ", source_type = " + source_type
                + ", _uid = " + _uid
                + ", type = " + type
                + "}";
        return string;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource_type() {
        return source_type;
    }

    public void setSource_type(String source_type) {
        this.source_type = source_type;
    }

    public String get_uid() {
        return _uid;
    }

    public void set_uid(String _uid) {
        this._uid = _uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
