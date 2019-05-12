package com.irlab.news.newsrecommender;

public class User {
    private String user,password,place,date,time;

    public User(){

    }

    public User(String user,String password,String place,String date,String time){
        this.user = user;
        this.password = password;
        this.place = place;
        this.date = date;
        this.time = time;
    }

    public String getUser(){
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlace(){
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
