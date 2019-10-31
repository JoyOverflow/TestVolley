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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.string_request:
                getWebString();
                break;
            case R.id.json_request:
                getWebObject();
                break;
            case R.id.image_request:
                //iamgeRequest();
                break;
            case R.id.image_loader:
                //imageLoader();
                break;
            case R.id.post:
                //postRequest();
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
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
        queue.add(jsonObjectRequest);
    }


















    /**
     * 从返回的Json串获取到Java对象
     */
    private void getWebObject2() {

        String url = "http://api.k780.com/?app=weather.history&weaid=1&date=2019-10-30&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //将Json字串反序列为指定对象
                        Weathers weatherList = new Gson().fromJson(response.toString(), Weathers.class);
                        if(weatherList != null) {
                            //显示对象信息
                            for(Result r : weatherList.getResult()) {
                                Log.d(TAG, "city:"+r.getCitynm() + "weather:"+ r.getWeather() +"\n");
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { }
                }
        );
        //入到请求队列中
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jsonObjectRequest);
    }



    /*
    //加载图片（使用ImageRequest）
    private void testImageRequest() {
        String url = "http://img3.imgtn.bdimg.com/it/u=2568996661,777819818&fm=27&gp=0.jpg";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        ImageRequest jsonObjectRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        if(response != null) {
                            mImageView.setImageBitmap(response);
                        }
                    }
                },
                0,
                0,
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { }
                }
        );
        queue.add(jsonObjectRequest);
    }
    //实践ImageLoader（内部同样使用的是ImageRequest）
    private void testImageLoader() {
        String url = "http://img3.imgtn.bdimg.com/it/u=2568996661,777819818&fm=27&gp=0.jpg";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }
            @Override
            public void putBitmap(String url, Bitmap bitmap) {
            }
        };
        ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(
                mImageView,
                R.mipmap.ic_launcher,
                R.mipmap.ic_launcher
        );
        ImageLoader imageLoader = new ImageLoader(queue,imageCache);
        imageLoader.get(url,imageListener);
    }
    */

    /**
     * 使用NetworkImageView视图控件
     * （其内封装ImageLoader，实际和使用ImageLoader一样）
     */
    private void getNetImageView() {

        //设置图像缓存接口
        ImageLoader.ImageCache cache = new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }
            @Override
            public void putBitmap(String url, Bitmap bitmap) { }
        };

        //创建请求队列对象和图像加载器（获取网络图片后，传递给内存缓存类）
        RequestQueue queue = Volley.newRequestQueue(this);
        ImageLoader imgLoader = new ImageLoader(queue,cache);

        //设置默认图片（直到图片加载完为止）和错误图片（网络加载出错时显示）
        int defaultImage = R.drawable.load_fail;
        NetworkImageView imgView = findViewById(R.id.img_network);
        imgView.setDefaultImageResId(defaultImage);
        imgView.setErrorImageResId(defaultImage);

        //开始显示图像
        String url = "https://img14.360buyimg.com/n0/jfs/t9334/49/109195984/39953/e791fc17/59a0f2e3N90587133.jpg";
        imgView.setImageUrl(url,imgLoader);
    }


}
