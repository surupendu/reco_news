package com.irlab.news.newsrecommender;

public class News_Article {
    private String _id,title,date;

    public News_Article(){

    }

    public News_Article(String _id,String title,String date){
        this._id = _id;
        this.title = title;
        this.date = date;
    }

    public String get_id(){
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
