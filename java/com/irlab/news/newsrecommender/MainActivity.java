package com.irlab.news.newsrecommender;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends AppCompatActivity {
    EditText username,password;
    Button button;
    TextView signup;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //createNotificationChannel();
        //createNotification();
        setContentView(R.layout.user);
        username = findViewById(R.id.user);
        password = findViewById(R.id.password);
        button = findViewById(R.id.button1);
        signup = findViewById(R.id.register);

        sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);

        if(sharedPreferences.getBoolean("logged",false)){
            String verify_username = sharedPreferences.getString("username","");
            String verify_password = sharedPreferences.getString("password","");
            LocalData localData = new LocalData(getApplicationContext());
            String user = sharedPreferences.getString("username","");//username.getText().toString().trim();
            localData.setUsername(user);
            //Toast.makeText(getApplicationContext(),localData.getUsername(),Toast.LENGTH_SHORT).show();
            SharedPreferenceValidate sharedPreferenceValidate = new SharedPreferenceValidate();
            sharedPreferenceValidate.execute(verify_username,verify_password);
            LocalData.setDefaultUser("username",localData.getUsername(),getApplicationContext());
        }



        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AsyncTaskValidate asyncTaskValidate = new AsyncTaskValidate();
                asyncTaskValidate.execute(username.getText().toString().trim(),password.getText().toString().trim());
                LocalData localData = new LocalData(getApplicationContext());
                LocalData.setDefaultUser("username",localData.getUsername(),getApplicationContext());
            }
        });

        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Toast.makeText(getApplicationContext(),"Sign up",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }});

    }


    public void onClick(){
        sharedPreferences.edit().putBoolean("logged",true).apply();
        //sharedPreferences.edit().putString("username",username.getText().toString().trim()).apply();
        Intent intent = new Intent(getApplicationContext(),NewsRecyclerActivity.class);
        intent.putExtra("username",sharedPreferences.getString("username",""));
        startActivity(intent);
    }


    private class SharedPreferenceValidate extends AsyncTask<String,String,String>{

        String user_name,pass_word;
        private ProgressBar spinner;
        //String baseURL  = "http://192.168.43.107:5000";
        //String baseURL  = "http://192.168.137.1:5000";
        String baseURL  = "http://13.127.64.217:5000";
        //String baseURL  = "http://13.127.145.134:5000";
        String URL;
        RequestQueue requestQueue;
        //JsonObjectRequest jsonObjectRequest;
        boolean response_msg;
        MessageDigest messageDigest;
        //SharedPreferences sharedPreferences = getSharedPreferences("login",Context.MODE_PRIVATE);
        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            spinner = findViewById(R.id.progressBar1);

            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            user_name = strings[0];
            pass_word = strings[1];

            URL = baseURL + "/mongodb/api/v1.0/login/" + user_name + "/" + pass_word;
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, URL,null,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try{
                        response_msg = response.getBoolean("status");
                        if(response_msg == true){
                            //username.setError("Username or Password is incorrect");
                            spinner.setVisibility(View.VISIBLE);
                        }
                        else{

                            /*username.setError(null);
                            sharedPreferences.edit().putString("username",username.getText().toString().trim()).apply();
                            sharedPreferences.edit().putString("password",hexString.toString()).apply();*/
                            onClick();
                        }
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
            requestQueue.add(jsonObjectRequest);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }





    private class AsyncTaskValidate extends AsyncTask<String,String,Void>{

        String user_name;
        String pass_word;
        //String baseURL  = "http://192.168.43.107:5000";
        //String baseURL  = "http://192.168.137.1:5000";
        String baseURL  = "http://13.127.64.217:5000";
        //String baseURL  = "http://13.127.145.134:5000";

        String URL;
        RequestQueue requestQueue;
        //JsonObjectRequest jsonObjectRequest;
        boolean response_msg;
        MessageDigest messageDigest;
        //SharedPreferences sharedPreferences = getSharedPreferences("login",Context.MODE_PRIVATE);
        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            user_name = strings[0];
            pass_word = strings[1];
            try{
                messageDigest = java.security.MessageDigest.getInstance("MD5");
            }
            catch(NoSuchAlgorithmException e){
                e.printStackTrace();
            }
            messageDigest.update(pass_word.getBytes());
            byte digest[] = messageDigest.digest();
            final StringBuffer hexString = new StringBuffer();
            for (int i=0; i<digest.length; i++)
                hexString.append(Integer.toHexString(0xFF & digest[i]));

            URL = baseURL + "/mongodb/api/v1.0/login/" + user_name + "/" + hexString;
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, URL,null,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try{
                        response_msg = response.getBoolean("status");
                        if(response_msg == true){
                            username.setError("Username or Password is incorrect");

                        }
                        else{

                            username.setError(null);
                            sharedPreferences.edit().putString("username",username.getText().toString().trim()).apply();
                            sharedPreferences.edit().putString("password",hexString.toString()).apply();
                            onClick();
                        }
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
            requestQueue.add(jsonObjectRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

}
