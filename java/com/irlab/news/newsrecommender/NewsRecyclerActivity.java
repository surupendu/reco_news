package com.irlab.news.newsrecommender;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NewsRecyclerActivity extends AppCompatActivity{

    private List<News_Article> newsList = new ArrayList<>();
    private List<String> newsHeadlines = new ArrayList<>();
    private RecyclerView recyclerView;
    private News_Adapter newsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        if(username==null){
            username = LocalData.getDefaultUser();
        }

        //Toast.makeText(getApplicationContext(),LocalData.getDefaultUser(),Toast.LENGTH_SHORT).show();
        AsyncTaskManager runner = new AsyncTaskManager();
        runner.execute();
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);
        recyclerView = findViewById(R.id.recycler_view);

        newsAdapter = new News_Adapter(newsList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(newsAdapter);

        //onBackPressed();
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        newsList.clear();
                        AsyncTaskReset refresher = new AsyncTaskReset();
                        refresher.execute((ArrayList<String>) newsHeadlines);
                    }
                }
        );

        recyclerView.addOnItemTouchListener(
                new RecyclerViewListener(getApplicationContext(), new RecyclerViewListener.OnItemClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        News_Article news = newsList.get(position);
                        String title = news.getTitle();
                        String date = news.getDate();
                        AsyncTaskArticle task = new AsyncTaskArticle();
                        task.execute(title,date);
                    }
                }


                )
        );

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent){
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(layoutManager.findFirstCompletelyVisibleItemPosition()==0){
                super.onBackPressed();
            }else {
                recyclerView.smoothScrollToPosition(0);
            }
        }
        return true;
    }



    private void prepareNewsdata(JSONArray jsonArray,ProgressBar spinner){
        int i;
        for (i=0;i<jsonArray.length();i++){
            try{
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String _id = jsonObject.getString("_id");
                String title = jsonObject.getString("TITLE");
                String date = jsonObject.getString("DATE");
                News_Article news = new News_Article(_id,title,date);
                newsList.add(news);
                newsHeadlines.add("\"" + title + "\"");
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        News_Article news1 = new News_Article("","Recommended News For You","");
        //Toast.makeText(getApplicationContext(),news1.toString(),Toast.LENGTH_SHORT).show();
        newsList.add(0,news1);
        spinner.setVisibility(View.GONE);
        newsAdapter.notifyDataSetChanged();


    }

    private class AsyncTaskReset extends AsyncTask<ArrayList<String>,String,Void>{
        private ProgressBar spinner;
        private String currentDateTime;
        private SimpleDateFormat dateFormat;
        //private String baseURL  = "http://192.168.43.107:5000";
        //private String baseURL = "http://192.168.137.1:5000";
        private String baseURL = "http://13.127.64.217:5000";
        //String baseURL  = "http://13.127.145.134:5000";
        private String url;
        private RequestQueue requestQueue;
        private JSONArray jsonArray;
        private ArrayList<String> arrayList;

        @Override
        protected void onPreExecute() {
            dateFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm");
            currentDateTime = dateFormat.format(new Date());
            currentDateTime = currentDateTime + " " + "IST";
            spinner = findViewById(R.id.progressBar1);
            spinner.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(ArrayList<String>... arrayLists) {
            arrayList = arrayLists[0];
            requestQueue = Volley.newRequestQueue(getApplicationContext());

            url = baseURL + "/mongodb/api/v1.0/getallnews_refresh/" + currentDateTime + "/" + username + "/" + arrayList;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, url,null,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        jsonArray = response.getJSONArray("headlines");
                        prepareNewsdata(jsonArray,spinner);
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );
            int socketTimeout = 88000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    private class AsyncTaskArticle extends AsyncTask<String,String,Void>{
        private String title;
        private String date;
        private String article;
        int length;
        //private String baseURL  = "http://192.168.43.107:5000";
        //private String baseURL = "http://192.168.137.1:5000";
        private String baseURL = "http://13.127.64.217:5000";
        //String baseURL  = "http://13.127.145.134:5000";
        private String url;
        private RequestQueue requestQueue;
        private JSONArray jsonArray;
        private JSONObject jsonObject;


        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            //Toast.makeText(getApplicationContext(),username,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            title = strings[0];
            date = strings[1];
            requestQueue = Volley.newRequestQueue(getApplicationContext());

            url = baseURL + "/mongodb/api/v1.0/getnewsarticle/" + title + "/" + date;


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, url,null,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        jsonArray = response.getJSONArray("ARTICLE");
                        jsonObject = jsonArray.getJSONObject(0);
                        article = jsonObject.getString("ARTICLE");

                        Bundle args = new Bundle();
                        args.putSerializable("ArrayList",(Serializable)newsHeadlines);
                        Intent intent = new Intent(getApplicationContext(),NewsMainActivity.class);
                        intent.putExtra("TITLE",title);
                        intent.putExtra("DATE",date);
                        intent.putExtra("ARTICLE",article);
                        intent.putExtra("username",username);
                        intent.putExtra("articles",args);
                        startActivity(intent);
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );
            int socketTimeout = 88000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

    }

    private class AsyncTaskManager extends AsyncTask<Void,Void,Void>{
        private ProgressBar spinner;
        private String currentDateTime;
        private SimpleDateFormat dateFormat;
        //private String baseURL  = "http://192.168.43.107:5000";
        //private String baseURL = "http://192.168.137.1:5000";
        private String baseURL = "http://13.127.64.217:5000";
        //String baseURL  = "http://13.127.145.134:5000";
        private String url;
        private RequestQueue requestQueue;
        private JSONArray jsonArray;
        private  JSONObject jsonObject;
        @Override
        protected void onPreExecute() {
            dateFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm");
            currentDateTime = dateFormat.format(new Date());
            currentDateTime = currentDateTime + " " + "IST";
            spinner = findViewById(R.id.progressBar1);
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());

            url = baseURL + "/mongodb/api/v1.0/getallnews/" + currentDateTime + "/" + username;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, url,null,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        jsonArray = response.getJSONArray("headlines");
                        prepareNewsdata(jsonArray,spinner);
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );
            int socketTimeout = 80000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Toast.makeText(getApplicationContext(),jsonObject.toString(),Toast.LENGTH_SHORT).show();

        }
    }

}
