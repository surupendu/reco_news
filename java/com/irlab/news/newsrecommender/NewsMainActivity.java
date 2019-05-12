package com.irlab.news.newsrecommender;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewsMainActivity extends AppCompatActivity {
    private String title;
    private String date;
    private String article;
    private String username;
    private JsonObjectRequest jsonObjectRequest;
    private String currentDateTime;
    private SimpleDateFormat dateFormat;
    //private String baseURL = "http://192.168.137.1:5000";
    //private String baseURL  = "http://192.168.43.107:5000";
    //String baseURL  = "http://13.127.145.134:5000";
    private String baseURL = "http://13.127.64.217:5000";
    private String url;
    private RequestQueue requestQueue;
    private JSONObject jsonObject;
    private String jsonArray;
    private Bundle bundle;
    ArrayList<String> arrayList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_article);


        Intent intent = getIntent();
        title = intent.getStringExtra("TITLE");
        date = intent.getStringExtra("DATE");
        article = intent.getStringExtra("ARTICLE");
        username = intent.getStringExtra("username");
        bundle = intent.getBundleExtra("articles");
        arrayList = (ArrayList<String>)bundle.getSerializable("ArrayList");
        //jsonArray = intent.getStringExtra("articles");
        //AsyncTaskLog asyncTaskLog = new AsyncTaskLog();
        //asyncTaskLog.execute();
        LocalData.setDefaultUser("username",username,getApplicationContext());
        //Toast.makeText(getApplicationContext(),LocalData.getDefaultUser(),Toast.LENGTH_SHORT).show();
        TextView Title = findViewById(R.id.news_title1);
        Title.setText(title);

        TextView Date = findViewById(R.id.news_date1);
        Date.setText(date);

        TextView Article = findViewById(R.id.news_article1);
        Article.setText(article);
        Article.setMovementMethod(new ScrollingMovementMethod());

        dateFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.ENGLISH);
        currentDateTime = dateFormat.format(new Date());
        currentDateTime = currentDateTime + " " + "IST";


        url = baseURL + "/mongodb/api/v1.0/click_log";

        jsonObject = new JSONObject();

        try{
            jsonObject.put("username",username);
            jsonObject.put("date",currentDateTime);
            jsonObject.put("article_title",title);
            jsonObject.put("articles",arrayList);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url,jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
        //Toast toast = Toast.makeText(this,username,Toast.LENGTH_SHORT);
        //toast.show();


    }

    private class AsyncTaskLog extends AsyncTask<String,Void,Void>{
        JsonObjectRequest jsonObjectRequest;
        //private String baseURL  = "http://192.168.43.107:5000";
        //private String baseURL = "http://192.168.137.1:5000";
        //String baseURL  = "http://13.233.131.98:5000";
        private String baseURL = "http://13.127.64.217:5000";
        private String url;
        private RequestQueue requestQueue;
        private JSONObject jsonObject;
        private String currentDateTime;
        private SimpleDateFormat dateFormat;

        @Override
        protected Void doInBackground(String... strings) {
            url = baseURL + "/mongodb/api/v1.0/click_log";
            dateFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm");
            currentDateTime = dateFormat.format(new Date());
            currentDateTime = currentDateTime + " " + "IST";
            //username = getIntent().getStringExtra("username");
            jsonObject = new JSONObject();
            try{
                jsonObject.put("USERNAME",username);
                jsonObject.put("ARTICLE_TITLE",title);
                jsonObject.put("DATE",currentDateTime);
            }
            catch(JSONException e){
                e.printStackTrace();
            }

            jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url,jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );
            requestQueue.add(jsonObjectRequest);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Toast.makeText(getApplicationContext(),jsonObject.toString(),Toast.LENGTH_SHORT).show();
        }
    }


}
