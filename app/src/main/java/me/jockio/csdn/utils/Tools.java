package me.jockio.csdn.utils;

import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.jockio.csdn.activity.MainActivity;
import me.jockio.csdn.model.Article;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jockio on 16/6/11.
 */

public class Tools {
    private static List<Article> list = null;

    public static void getInfo(final String url, final int flag){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){
                            JSONArray array = null;
                            try {
                                list = new ArrayList<Article>();
                                array = new JSONArray(response.body().string());
                                for(int i = 0; i < array.length(); i++){
                                    Article article = new Article();
                                    JSONObject object = array.getJSONObject(i);
                                    article.setAgo(object.getString("ago"));
                                    article.setArticleid(object.getString("articleid"));
                                    article.setChannel(object.getString("channel"));
                                    article.setChannelId(object.getString("channelId"));
                                    article.setPagecount(object.getInt("pagecount"));
                                    article.setPageindex(object.getInt("pageindex"));
                                    article.setPhoto(object.getString("photo"));
                                    article.setPosttime(object.getString("posttime"));
                                    article.setUsername(object.getString("username"));
                                    article.setViewcount(object.getInt("viewcount"));
                                    article.setTitle(object.getString("title"));
                                    list.add(article);
                                    Log.v("json", article.toString());
                                }
                                Message msg = Message.obtain();
                                msg.what = flag;
                                msg.obj = list;
                                MainActivity.handler.sendMessage(msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    public static void getSearchResult(final String path, final String keyWord, final int page, final int flag){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                FormBody formBody = new FormBody.Builder()
                        .add("keyword", keyWord)
                        .add("page", String.valueOf(page))
                        .build();
                Request request = new Request.Builder()
                        .url(path)
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()) {
                            String result = response.body().string();
                            try {
                                list = new ArrayList<>();
                                JSONObject object = new JSONObject(result);
                                String total = object.getString("total");
                                String hits = object.getString("total_hits");
                                Log.v("SearchResult", total + "  " + hits);
                                JSONArray array = object.getJSONArray("hits");
                                for(int i = 0; i < array.length(); i++){
                                    JSONObject obj = array.getJSONObject(i);
                                    /*暂时不需要这些相信的信息
                                    Article_ShortInfo shortInfo = new Article_ShortInfo();
                                    shortInfo.set_type(obj.getString("_type"));
                                    shortInfo.set_id(obj.getString("_id"));
                                    shortInfo.set_index(obj.getString("_index"));

                                    JSONObject articleJson = obj.getJSONObject("object");
                                    ArticleObject articleObject = new ArticleObject();
                                    articleObject.setId(articleJson.getString("id"));
                                    articleObject.setTitle(articleJson.getString("title"));
                                    articleObject.setSource_type(articleJson.getString("source_type"));
                                    articleObject.set_uid(articleJson.getString("_uid"));
                                    articleObject.setType(articleJson.getString("type"));

                                    shortInfo.setArticleObject(articleObject);
                                    shortInfo.setFields(obj.getString("fields"));

                                    Log.v("SearchResult", shortInfo.toString());
                                    */
                                    getDetail(obj.getString("_id"));
                                }
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }finally {
                                    Message msg = Message.obtain();
                                    msg.what = flag;
                                    msg.obj = list;
                                    MainActivity.handler.sendMessage(msg);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    public static void getDetail(String articleId){
        //这里的id是articleId
        String path = "http://m.blog.csdn.net/article/getdetails?id=" + articleId;
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(path)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String result = response.body().string();
                    try {
                        JSONObject object = new JSONObject(result);
                        Article article = new Article();
                        article.setArticleid(object.getString("articleid"));
                        article.setUsername(object.getString("username"));
                        article.setPosttime(object.getString("posttime"));
                        article.setAgo(object.getString("timeago"));
                        article.setViewcount(object.getInt("viewcount"));
                        article.setChannelId(object.getString("blogid"));
                        article.setChannel(object.getString("digg"));
                        article.setPageindex(0);
                        article.setPagecount(object.getInt("commentcount"));
                        article.setPhoto(object.getString("photo"));
                        article.setTitle(object.getString("title"));
                        Log.v("ArticleDetail", article.toString());
                        list.add(article);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
