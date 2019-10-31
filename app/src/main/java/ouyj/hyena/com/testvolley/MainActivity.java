package ouyj.hyena.com.testvolley;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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

import ouyj.hyena.com.testvolley.model.Result;
import ouyj.hyena.com.testvolley.model.Weathers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getWebString();
        //getWebObject();
        //getNetImageView();
    }







    /**
     * 获取Json字串数据
     */
    private void getWebString() {
        String url = "http://api.k780.com/?app=weather.history&weaid=1&date=2019-10-30&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    //请求成功时的处理
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse：" + response);
                    }
                },
                new Response.ErrorListener() {
                    //请求失败
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse：" + error.getMessage());
                    }
                }
        );
        //加入到请求队列中
        request.setTag("stringRequest");
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    /**
     * 从返回的Json串获取到Java对象
     */
    private void getWebObject() {

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

    @Override
    public void onClick(View v) {

    }
}
