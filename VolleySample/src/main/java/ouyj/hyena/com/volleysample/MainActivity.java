package ouyj.hyena.com.volleysample;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import ouyj.hyena.com.volleysample.model.AppConstants;
import ouyj.hyena.com.volleysample.model.LatestNewsPojo;
import ouyj.hyena.com.volleysample.model.MySingleton;
import ouyj.hyena.com.volleysample.model.News;

public class MainActivity extends AppCompatActivity {

    Button retrofitParsingButton;
    public String postData;
    private ArrayList<News> LatestNewsArray;
    private LatestNewsPojo LatestDetail;
    private static final String TAG = MainActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofitParsingButton = findViewById(R.id.retrofit_parsing);
        retrofitParsingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volleyJsonParsing();
            }
        });
    }


    private void volleyJsonParsing() {

        //创建和显示对话框
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.show();
        
        //要Post的数据
        postData = "data=" + makeJsonData();
        
        
        JsonObjectRequest jor = new JsonObjectRequest(
                Request.Method.POST,
                AppConstants.API_Base_URL + AppConstants.GET_LATEST_NEWS_URL,
                (String)null,


                new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                                //关闭对话框
                                dialog.dismiss();
                                try {
                                    if(response.getString("Status").equals("1")){
                //Use gson libtrary to parse json data
                                        GsonBuilder gsonBuilder = new GsonBuilder();
                                        Gson gson = gsonBuilder.create();
                                        LatestDetail = gson.fromJson(String.valueOf(response), LatestNewsPojo.class);
                                        if (!LatestDetail.getNews().isEmpty()){
                //Here we get array list from json parsing add it into array list and notify the adapter
                                            LatestNewsArray.addAll(LatestDetail.getNews()) ;
                                            //latestNewsAdapter.notifyDataSetChanged();
                                        }
                                        else {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    "You have reached the end",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                },

                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //关闭对话框
                dialog.dismiss();

                NetworkResponse networkInfo = error.networkResponse;
                String errorInfo = "Unknown error";
                if (networkInfo == null) {
                    if (error.getClass().equals(TimeoutError.class))
                        errorInfo = "Request timeout";
                    else if (error.getClass().equals(NoConnectionError.class))
                        errorInfo = "Failed to connect server";
                }
                else {
                    String result = new String(networkInfo.data);
                    try {
                        //字串转换为Json对象
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");
                        Log.e(TAG, status);
                        Log.e(TAG, message);

                        //返回状态码
                        if (networkInfo.statusCode == 404) {
                            errorInfo = "Resource not found";
                        } else if (networkInfo.statusCode == 401) {
                            errorInfo = message+" Please login again";
                        } else if (networkInfo.statusCode == 400) {
                            errorInfo = message+ " Check your inputs";
                        } else if (networkInfo.statusCode == 500) {
                            errorInfo = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(TAG, errorInfo);
                Toast.makeText(
                        getApplicationContext(),
                        "Error",
                        Toast.LENGTH_SHORT
                ).show();
                error.printStackTrace();
            }
        }){

            //here we set the parsing method
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
            @Override
            public byte[] getBody() {
                try {
                    return postData == null ? null : postData.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", postData, "utf-8");
                    return null;
                }
            }
        };
        //设置volley请求的超时时间
        jor.setRetryPolicy(new
                DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        //创建和加入到请求队列
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jor);
    }

    /**
     * 需Post的数据
     * @return
     */
    private JSONObject makeJsonData() {
        //创建原生Json对象
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ACCESS_TOKEN", "ajhgd7834934908_839973");
            jsonObject.put("KEY_PAGE_NUMBER", 10);
            jsonObject.put("KEY_NUMBER_OF_RECORDS_PER_PAGE", 1);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
