package com.yanghuabin.reader;

import java.io.UnsupportedEncodingException;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.loopj.android.http.*;
import com.yanghuabin.reader.ReFlashListView.ILoadMoreDataListener;
import com.yanghuabin.reader.ReFlashListView.IReflashListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yanghuabin.reader.NovelInfo;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;



/**
 * Created by 18016 on 2018/1/27.
 */

public class MainFragment extends Fragment implements ILoadMoreDataListener,IReflashListener {


    private EditText searchText;
    private ImageButton search;
    private ReFlashListView novelList;
    private ProgressDialog pd;
    private int page = 1;
    private String keyword = "";
    private List<NovelInfo> novels = new ArrayList<NovelInfo>();
    private NovelsAdapter adapter;
    private NovelDB novelDB;
    private SQLiteDatabase db;
    private final int ACT_REF = 1;
    public static String url1 = "https://www.biquge5200.cc";
    private  Handler handler =new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACT_REF:// 在Activity中刷新Fragment
                    showListView();
                    break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        searchText = (EditText) view.findViewById(R.id.et_search);
        search = (ImageButton) view.findViewById(R.id.ibtn_search);
        novelList = (ReFlashListView) view.findViewById(R.id.lv_result);
        pd = new ProgressDialog(getActivity());
        pd.setMessage("加载中...");
        /*if (keyword == "")
        {
            return ;
        }*/
        getNovelsData(page, keyword);
        novelDB = new NovelDB(getActivity());
        db = novelDB.getWritableDatabase();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = searchText.getText().toString().trim();
                Log.i("Error", "inview");
                onReflash();
            }
        });

        novelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //添加进历史纪录
                NovelServer.insert(novels, db, position);
                Intent intent = new Intent(getActivity(), ChapterActivity.class);
                intent.putExtra("path", novels.get(position - 1).path);
                intent.putExtra("update", novels.get(position - 1).updateTime);
                intent.putExtra("name", novels.get(position - 1).name);
                intent.putExtra("author", novels.get(position - 1).author);
                intent.putExtra("status", novels.get(position - 1).type);
                startActivity(intent);
            }

        });

    }

    @Override
    public void onReflash() {
        novels.clear();
        page = 1;
        getNovelsData(page, keyword);
        novelList.reflashComplete();
    }


    /*public static SchemeRegistry getSchemeRegistry() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpConnectionParams.setSoTimeout(params, 10000);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            return registry;
         }
        catch(Exception e)
        {
            return null;
         }
}
*/


    private void getNovelsData(int page, String keyword){

        //pd.show();

        if(keyword != "")
        {
            url1 = "https://www.biquge5200.cc/modules/article/search.php?searchkey=" + encodeToHttp(keyword);
        }
        Log.i("Error", "getNovelsData:"+url1);
        //pd.setMessage(url1);
        //pd.show();
        //根据输入内容搜索相关小说并接收数据到APP
        //URL url = new URL(url1);
        //HttpsTrustManager.trustAll();
        /*AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.get(url1, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int var1, Header[] var2, byte[] var3) {
                //super.onSuccess(content);
                //数据的解析和转化
                String str ;
                try{
                    str = new String(var3,"GBK");//需要转码
                    Log.i("success", "成功>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.i("MainFragment", e.getMessage());
                    str = ""; // prevent crash
                }
                List<NovelInfo> data = NovelServer.getNovelInfos(str);
                novels.addAll(data);
                showListView();
                //pd.setMessage("成功...");
                //pd.show();
//				novelList.setAdapter(new NovelsAdapter(getActivity(),novels));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                //super.onFailure(i,headers,bytes,throwable);
                pd.setMessage("失败...");
                Log.i("onfailure", "失败/////////////////////////////////////////////////////////////////");
                pd.show();
                //List<NovelInfo> data = NovelServer.getNovelInfos(u);
                //novels.addAll(data);
                //showListView();
            }

        });*/
        try{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<NovelInfo> data = NovelServer.getNovelInfos(url1);
                    novels.addAll(data);
                    Message message = new Message();
                    message.what = ACT_REF;
                    handler.sendMessage(message);
                    //Message msg = new Message();
                    //msg.what = COMPLETED;
                    //handler.sendMessage(msg);
                }
            }).start();
            Log.i("success", "成功");
        }catch (Exception e) {
            e.printStackTrace();
            Log.i("onfailure", "失败");
        }


    }

    protected void showListView() {
        if(adapter == null){
            adapter = new NovelsAdapter(getActivity(), novels);
            novelList.setInterface(this);
            novelList.setLoadMoreInterface(this);
            novelList.setAdapter(adapter);
        }else{
            adapter.setDataChangeLinstener(novels);
        }
        pd.dismiss();
    }

    public static String encodeToHttp(String str) {
        String enc;
        try {
            enc = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.i("MainFragment", e.getMessage());
            enc = ""; // prevent crash
        }
        return enc;
    }

    @Override
    public void onLoadMoreData() {
        page += 1;
        getNovelsData(page, keyword);
        novelList.loadComplete();
    }

}
