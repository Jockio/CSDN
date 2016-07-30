package me.jockio.csdn.utils;

import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import me.jockio.csdn.activity.MainActivity;
import me.jockio.csdn.model.Article;
import me.jockio.csdn.model.Suggestion;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.id;

/**
 * Created by jockio on 16/6/11.
 */

public class Tools {
    private static List<Article> list = null;

    /**
     * 根据url, 获取博客信息
     * @param url
     * @param flag
     */
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

    /**
     * 根据搜索的关键词,从网络获取搜索结果
     * @param path
     * @param keyWord
     * @param page
     * @param flag
     */
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

    /**
     * 获取存储在本地的搜索建议
     * @return
     */
    public static String[] getSuggestions(){
        File file = new File(MyApplication.getContext().getFilesDir(), "suggestion.sg");

        BufferedReader br = null;
        try {
            if(!file.exists()) {
                file.createNewFile();
                return new String[0];
            }
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Suggestion> list = new ArrayList<>();
        String line;
        try {
            while((line = br.readLine()) != null){
                String[] strings = line.split(" ");
                list.add(new Suggestion(strings[0], Integer.valueOf(strings[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != br){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(list.size() == 0) return new String[0];
        quickSort(list, 0, list.size() - 1);

        String[] suggestions = new String[list.size()];
        for(int i = 0; i < list.size(); i++)
            suggestions[i] = list.get(i).getKeyword();

        return suggestions;
    }

    private static void quickSort(ArrayList<Suggestion> arrayList, int low, int high){
        if(low >= high) return;

        int left = low;
        int right = high;
        Suggestion suggestion = arrayList.get(low);

        while(left < right){
            while (left < right && arrayList.get(right).getTimes() <= suggestion.getTimes()) right--;
            arrayList.set(left, arrayList.get(right));
            while (left < right && arrayList.get(left).getTimes() >= suggestion.getTimes()) left++;
            arrayList.set(right, arrayList.get(left));
        }
        arrayList.set(left, suggestion);
        quickSort(arrayList, left + 1, high);
        quickSort(arrayList, low, left - 1);
    }

    /**
     * 向搜索建议中添加关键词
     * @param keyword
     * @return
     */
    public static boolean updateSuggestions(String keyword){
        File file = new File(MyApplication.getContext().getFilesDir(), "suggestion.sg");

        BufferedReader br = null;
        try {
            if(!file.exists()){
                file.createNewFile();
                return true;
            }
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Suggestion> list = new ArrayList<>();
        String line;
        try {
            boolean flag = false;
            while((line = br.readLine()) != null){
                String[] strings = line.split(" ");
                if(keyword.equals(strings[0])){
                    list.add(new Suggestion(strings[0], Integer.valueOf(strings[1]) + 1));
                    flag = true;
                } else {
                    list.add(new Suggestion(strings[0], Integer.valueOf(strings[1])));
                }
            }
            if(!flag){
                list.add(new Suggestion(keyword, 1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != br){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));

            for(int i = 0; i < list.size(); i++){
                Suggestion suggestion = list.get(i);
                if(i == list.size() - 1){
                    bw.write(suggestion.getKeyword() + " " + suggestion.getTimes());
                    continue;
                }
                bw.write(suggestion.getKeyword() + " " + suggestion.getTimes() + "\n");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
