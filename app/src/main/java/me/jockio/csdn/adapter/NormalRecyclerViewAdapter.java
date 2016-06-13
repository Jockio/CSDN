package me.jockio.csdn.adapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import me.jockio.csdn.R;
import me.jockio.csdn.activity.WebViewActivity;
import me.jockio.csdn.model.Article;
import me.jockio.csdn.utils.MyApplication;


/**
 * Created by jockio on 16/6/11.
 */

public class NormalRecyclerViewAdapter extends RecyclerView.Adapter<NormalRecyclerViewAdapter.NormalTextViewHolder> {

    private List<Article> articleList = null;

    public NormalRecyclerViewAdapter(){
        this.articleList = new ArrayList<>();
    }

    @Override
    public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalTextViewHolder(
                LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_recyclerview,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.titleTextView.setText(article.getTitle());
        holder.authorTextView.setText(article.getUsername());
        holder.readTimeTextView.setText(article.getAgo());
        holder.readCountTextView.setText(article.getViewcount() + " 阅读");

        Uri uri = Uri.parse(article.getPhoto());
        holder.draweeView.setImageURI(uri);

        holder.titleTextView.setOnClickListener(new MyOnClickListener(position));
        holder.draweeView.setOnClickListener(new MyOnClickListener(position));
    }

    @Override
    public int getItemCount() {
        return articleList == null ? 0 : articleList.size();
    }

    public static class NormalTextViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;
        TextView authorTextView;
        TextView readTimeTextView;
        TextView readCountTextView;
        SimpleDraweeView draweeView;

        public NormalTextViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title_textView);
            authorTextView = (TextView) itemView.findViewById(R.id.author_textView);
            readTimeTextView = (TextView) itemView.findViewById(R.id.readTime_textView);
            readCountTextView = (TextView) itemView.findViewById(R.id.readCount_textView);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.imageView);
        }
    }

    public class MyOnClickListener implements View.OnClickListener{
        private int position;

        public MyOnClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Article article = articleList.get(position);
            Intent intent = new Intent();
            switch (v.getId()){
                case R.id.title_textView:
                    intent.setClass(MyApplication.getContext(), WebViewActivity.class);
                    intent.putExtra("url",
                            MyApplication.getContext().getResources().getString(R.string.base_url)
                                    + "/article/details?id="
                                    + article.getArticleid());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getContext().startActivity(intent);
                    break;
                case R.id.imageView:
                    intent.setClass(MyApplication.getContext(), WebViewActivity.class);
                    intent.putExtra("url",
                            MyApplication.getContext().getResources().getString(R.string.base_url)
                            + "/blog/index?username=" + article.getUsername());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getContext().startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }

    //下拉刷新 添加数据
    public void addItem(List<Article> articleList) {
        this.articleList.clear();
        //this.articleList.removeAll(this.articleList);
        this.articleList.addAll(articleList);
        notifyDataSetChanged();
    }

    //上拉加载 添加数据
    public void addMoreItem(List<Article> articleList) {
        this.articleList.addAll(articleList);
        notifyDataSetChanged();
    }
}
