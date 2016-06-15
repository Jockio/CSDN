package me.jockio.csdn.utils;

import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.jockio.csdn.R;
import me.jockio.csdn.activity.MainActivity;
import me.jockio.csdn.model.Article;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
                                    article.setArticleid(object.getInt("articleid"));
                                    article.setChannel(object.getString("channel"));
                                    article.setChannelId(object.getInt("channelId"));
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

    public static void getSearchResult(final String path){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(path)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()) {
                            JSONArray array = null;
                            try {
                                JSONObject obj = new JSONObject(response.body().string());
                                array = obj.getJSONArray("hits");
                                for(int i = 0; i < array.length(); i++){
                                    JSONObject object = array.getJSONObject(i);
                                    Log.v("HELLO", object.toString());
                                }

                                Message msg = Message.obtain();
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
}
