package com.irlab.news.newsrecommender;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class News_Adapter extends RecyclerView.Adapter<News_Adapter.MyViewHolder> {

    private List<News_Article> newsArticleList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView news_title,news_date;

        public MyViewHolder(View view){
            super(view);
            news_title = (TextView)view.findViewById(R.id.news_title);
            news_date = (TextView)view.findViewById(R.id.news_date);
        }
    }

    public News_Adapter(List<News_Article> news_articleList){this.newsArticleList=news_articleList;}

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder,int position){
        News_Article news = newsArticleList.get(position);
        myViewHolder.news_title.setText(news.getTitle());
        myViewHolder.news_date.setText(news.getDate());

    }

    @Override
    public int getItemCount() {
        return newsArticleList.size();
    }
}
