package ouyj.hyena.com.testvolley;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ouyj.hyena.com.testvolley.model.Result;
import ouyj.hyena.com.testvolley.model.Weathers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    RequestQueue queue;
    TextView txtMessage;
    ProgressBar progressBar;
    ImageView showImg;
    NetworkImageView networkImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化请求队列
        queue = Volley.newRequestQueue(this);

        //查找视图
        txtMessage = findViewById(R.id.txt_message);
        progressBar = findViewById(R.id.progressBar);
        showImg =  findViewById(R.id.showImage);
        networkImg = findViewById(R.id.img_network);

        //设置按钮事件
        findViewById(R.id.string_request).setOnClickListener(this);
        findViewById(R.id.json_request).setOnClickListener(this);
        findViewById(R.id.image_request).setOnClickListener(this);
        findViewById(R.id.image_loader).setOnClickListener(this);
        findViewById(R.id.post).setOnClickListener(this);

        //getWebString();
        //getWebObject();
        //getNetImageView();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                //取消所有请求
                return true;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.string_request:
                getWebString();
                break;
            case R.id.json_request:
                getWebObject();
                break;
            case R.id.image_request:
                getWebImage();
                break;
            case R.id.image_loader:
                getWebImageView();
                break;
            case R.id.post:
                postRequest();
                break;
            default:
                break;
        }
    }
    /**
     * 获取Json字串数据
     */
    private void getWebString() {
        txtMessage.setText("");
        progressBar.setVisibility(View.VISIBLE);

        //发送Get请求
        String url = "http://api.k780.com/?app=weather.history&weaid=1&date=2019-10-30&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    //请求成功时的处理
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse：" + response);

                        //文本显示视图设置文本
                        txtMessage.setText(response);
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    //请求失败
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse：" + error.getMessage());
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
        //加入到请求队列中
        request.setTag("stringRequest");
        queue.add(request);
    }
    /**
     * 从返回的Json串获取到Java对象
     */
    private void getWebObject() {
        txtMessage.setText("");
        progressBar.setVisibility(View.VISIBLE);
        String url = "http://api.k780.com/?app=weather.history&weaid=1&date=2019-10-30&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                (String)null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //将Json字串反序列为指定对象
                        StringBuilder sb = new StringBuilder();
                        Weathers weatherList = new Gson().fromJson(response.toString(), Weathers.class);

                        if(weatherList != null) {
                            //显示对象信息（若无此键会返回null，值为数组）
                            List<Result> results=weatherList.getResult();
                            if(results!=null) {
                                for (Result r : weatherList.getResult()) {
                                    String result = String.format("城市：%s，天气：%s<br/>", r.getCitynm(), r.getWeather());
                                    sb.append(result);
                                    Log.d(TAG, result);
                                }
                            }
                        }
                        //使字串在文本显示视图中换行
                        txtMessage.append(Html.fromHtml(sb.toString()));
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse：" + error.getMessage());
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
        //加入到请求队列中
        queue.add(request);
    }
    /**
     * 获取网络上的一张图片
     */
    private void getWebImage() {
        txtMessage.setText("");
        progressBar.setVisibility(View.VISIBLE);
        showImg.setImageBitmap(null);

        String url = "https://www.btbttpic.com/upload/attach/000/147/9bb1d648e98051bb1a939d548ffd1e2d.jpg";
        ImageRequest imageRequest = new ImageRequest(
                url, //图片url
                new Response.Listener<Bitmap>() {
                    //正确返回数据
                    @Override
                    public void onResponse(Bitmap response) {
                        showImg.setImageBitmap(response);
                        progressBar.setVisibility(View.GONE);
                    }
                },
                0, //获取图片的最大宽高（0=忽略）
                0,
                ImageView.ScaleType.FIT_XY, //图片缩放类型
                Bitmap.Config.ARGB_8888, //压缩方式
                new Response.ErrorListener() {
                    //请求出现异常
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        txtMessage.setText(error.getMessage());
                    }
                }
        );
        queue.add(imageRequest);
    }
    /**
     * 使用网络图像视图（适宜于带图片的列表视）
     */
    private void getWebImageView() {
        txtMessage.setText("");
        progressBar.setVisibility(View.VISIBLE);
        showImg.setImageBitmap(null);

        //图像Url
        String url = "https://img14.360buyimg.com/n0/jfs/t9334/49/109195984/39953/e791fc17/59a0f2e3N90587133.jpg";

        //创建图像加载器（获取网络图片后，传递给内存缓存类）
        int cacheSize=LruBitmapCache.getCacheSize(getApplicationContext());
        ImageLoader imgLoader = new ImageLoader(
                queue,
                new LruBitmapCache(cacheSize)
        );
        imgLoader.get(
                url,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        showImg.setImageBitmap(response.getBitmap());
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        txtMessage.setText(error.getMessage());
                    }
                },
                0,
                0,
                ImageView.ScaleType.FIT_XY
        );

        //设置默认图片（直到图片加载完为止）和错误图片（网络加载出错时显示）
        int defaultImage = R.drawable.load_fail;
        networkImg.setDefaultImageResId(defaultImage);
        networkImg.setErrorImageResId(defaultImage);
        networkImg.setImageUrl(url, imgLoader);
    }


    private void postRequest() {
        txtMessage.setText("");
        progressBar.setVisibility(View.VISIBLE);
        String url = "http://httpbin.org/post";
        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response).getJSONObject("form");
                            String site = jsonResponse.getString("site"),
                            network = jsonResponse.getString("network");
                            System.out.println("Site: " + site + "\nNetwork: " + network);
                            txtMessage.setText("PostRequest==" + "Site: " + site + "\nNetwork: " + network);
                            progressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        txtMessage.setText("PostRequest error==" + error);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("site", "code");
                params.put("network", "tutsplus");
                return params;
            }
        };
        queue.add(postRequest);
    }





















}
