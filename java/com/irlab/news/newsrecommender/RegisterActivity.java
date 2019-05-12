package com.irlab.news.newsrecommender;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity{
    EditText username,password,email,confirm_password;
    Button button;
    //AwesomeValidation awesomeValidation;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        //Intent intent = getIntent();
        username = findViewById(R.id.user);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        email = findViewById(R.id.email);
        button = findViewById(R.id.button1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTaskValidate asyncTaskValidate = new AsyncTaskValidate();
                asyncTaskValidate.execute(username.getText().toString());
                //boolean status = isUsernamePresent();
                //Toast.makeText(getApplicationContext(),Boolean.toString(status),Toast.LENGTH_SHORT).show();
                /*if(status == false){
                    Toast.makeText(getApplicationContext(),Boolean.toString(status),Toast.LENGTH_SHORT).show();
                }*/
                if(register(v)){
                    //Toast.makeText(getApplicationContext(),username.getText().toString().trim()+password.getText().toString()+email.getText().toString().trim(),Toast.LENGTH_SHORT).show();
                    AsyncTaskRegister asyncTaskRegister = new AsyncTaskRegister();
                    asyncTaskRegister.execute(username.getText().toString().trim(),password.getText().toString(),email.getText().toString());
                    //Toast.makeText(getApplicationContext(),email.getText().toString(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            }
        });


    }

    public boolean isEmail(EditText text){
        CharSequence email = text.getText().toString().trim();
        return (!Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public boolean isEmpty(EditText text){
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    public boolean isSame(EditText password,EditText confirm_password){
        if(password.getText().toString().equals(confirm_password.getText().toString())){
            return false;
        }
        return true;
    }



    public boolean register(View view){
        boolean flag = true;
        if (isEmpty(username)){
            username.setError("Username is necessary");
            flag = false;
        }
        if (isEmpty(password)){
            password.setError("Password is necessary");
            flag = false;
        }
        if (isEmpty(confirm_password)){
            confirm_password.setError("Confirm Password is necessary");
            flag = false;
        }
        if (isEmpty(email)){
            email.setError("Email is necessary");
            flag = false;
        }
        if (isEmail(email)){
            email.setError("Enter valid email");
            flag = false;
        }
        if (isSame(password,confirm_password)){
            confirm_password.setError("Passwords should match");
            flag = false;
        }

        //Toast.makeText(getApplicationContext(),username.getError().toString(),Toast.LENGTH_SHORT).show();
        /*if (username.getError().toString() != null){
            Toast.makeText(getApplicationContext(),username.getError().toString(),Toast.LENGTH_SHORT).show();
            //username.setError("Username is already present");
            flag = false;
        }*/
        return flag;
    }

    public class AsyncTaskValidate extends AsyncTask<String,String,String>{

        //ProgressBar spinner;
        String user_name;
        //String baseURL  = "http://192.168.137.1:5000";;
        String baseURL  = "http://13.127.64.217:5000";
        //String baseURL  = "http://13.127.145.134:5000";
        //String baseURL  = "http://192.168.43.107:5000";
        String URL;
        RequestQueue requestQueue;
        //JsonObjectRequest jsonObjectRequest;
        boolean response_msg;
        //JSONArray response_msg;

        @Override
        protected String doInBackground(String... strings) {
            user_name = strings[0];
            URL = baseURL + "/mongodb/api/v1.0/validate/" + user_name;
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, URL,null,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        response_msg = response.getBoolean("status");
                        if(response_msg == true){
                            username.setError("Username exists");
                            //button.setEnabled(false);
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

            return Boolean.toString(response_msg);
        }

        @Override
        protected void onPostExecute(String result) {
            //notify();
            //username.setError("Username exists");
            //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            //super.onPostExecute(aVoid);
            //spinner.setVisibility(View.GONE);
        }
    }



    private class AsyncTaskRegister extends AsyncTask<String,String,Void>{
        String emailid,username,password1;
        JSONObject jsonObject;
        JsonObjectRequest jsonObjectRequest;
        RequestQueue requestQueue;
        //String baseUrl = "http://192.168.137.1:5000";
        String baseUrl = "http://13.127.64.217:5000";
        //String baseUrl  = "http://13.127.145.134:5000";
        //String baseUrl = "http://192.168.43.107:5000";
        String url;
        MessageDigest messageDigest;

        @Override
        protected Void doInBackground(String... strings) {
            url = baseUrl + "/mongodb/api/v1.0/register"; //+ username + '/' + password1 + '/' + emailid;
            username = strings[0];
            password1 = strings[1];
            emailid = strings[2];
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            jsonObject = new JSONObject();
            try{
                messageDigest = java.security.MessageDigest.getInstance("MD5");
            }
            catch(NoSuchAlgorithmException e){
                e.printStackTrace();
            }
            messageDigest.update(password1.getBytes());
            byte digest[] = messageDigest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<digest.length; i++)
                hexString.append(Integer.toHexString(0xFF & digest[i]));

            try{
                jsonObject.put("username",username);
                jsonObject.put("email",emailid);
                jsonObject.put("password",hexString);
            }
            catch (JSONException e){
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
            //super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(),"Registration Complete !!!",Toast.LENGTH_SHORT).show();
        }
    }
}
