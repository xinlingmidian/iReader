package com.yanghuabin.reader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class AnotherDetailActivity extends AppCompatActivity {
    public String NovelPath = "";
    private String NovelUpdate = "";
    private String NovelName = "";
    private String NovelAuthor = "";
    private String NovelStatus = "";
    private TextView contentview;
    private TextView chaptertitle;
    private DetailsInfo details;
    private Button previous;
    private Button control;
    private Button next;
    private Button goback;
    private boolean isVisible = true;
    private static String Chapterlink = "";
    private final int ACT_TXT = 1;
    private Handler handler =new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACT_TXT:// 在Activity中刷新Fragment
                    contentview.setText(details.txt);
                    chaptertitle.setText(details.cname);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initView();
    }


    private void initView() {
        //webView = (WebView) this.findViewById(R.id.webView_details);
        contentview = (TextView)this.findViewById(R.id.contentview);
        chaptertitle = (TextView)this.findViewById(R.id.chaptertitle);
        previous = (Button)this.findViewById(R.id.previousbutton);
        control = (Button)this.findViewById(R.id.control);
        next = (Button)this.findViewById(R.id.nextbutton);
        goback = (Button)this.findViewById(R.id.goback);


        Intent intent = getIntent();
        //final String Chaptername = intent.getStringExtra("chaptername");
        Chapterlink = intent.getStringExtra("chapterlink");
        NovelPath = getIntent().getStringExtra("path");
        NovelName = getIntent().getStringExtra("name");
        NovelAuthor = getIntent().getStringExtra("author");
        NovelUpdate = getIntent().getStringExtra("update");
        NovelStatus = getIntent().getStringExtra("status");

        /*AsyncHttpClient client = new AsyncHttpClient();
        client.get(Chapterlink, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int var1, Header[] var2, byte[] var3) {
                //super.onSuccess(content);
                String str ;
                try{
                    str = new String(var3,"GBK");//需要转码
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e("AnotherDetailActivity", e.getMessage());
                    str = ""; // prevent crash
                }
                details = NovelServer.getContentInfos(str);
                //details.cname = Chaptername;
                contentview.setText(details.txt);
                chaptertitle.setText(details.cname);

            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                //super.onFailure(i,headers,bytes,throwable);
                //pd.setMessage("失败...");
                //pd.show();
                //String str = new String(bytes);
                //Log.e("Error", String.valueOf(i));
                throwable.printStackTrace();
            }
        });*/
        try{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    details = NovelServer.getContentInfos(Chapterlink);
                    Message message = new Message();
                    message.what = ACT_TXT;
                    handler.sendMessage(message);
                }
            }).start();
            Log.i("success", "成功");
        }catch (Exception e) {
            e.printStackTrace();
            Log.i("onfailure", "失败");
        }

        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisible)
                {
                    isVisible = false;
                    previous.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    goback.setVisibility(View.VISIBLE);
                }
                else {
                    previous.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                    goback.setVisibility(View.GONE);
                    isVisible = true;
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AnotherDetailActivity.this, "上一章", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(AnotherDetailActivity.this, AnotherDetailActivity.class);
                intent1.putExtra("chapterlink",details.previouschapter);
                intent1.putExtra("path",NovelPath);
                intent1.putExtra("name",NovelName);
                intent1.putExtra("author",NovelAuthor);
                intent1.putExtra("update",NovelUpdate);
                intent1.putExtra("status",NovelStatus);
                startActivity(intent1);
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AnotherDetailActivity.this, "下一章", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(AnotherDetailActivity.this, AnotherDetailActivity.class);
                intent2.putExtra("chapterlink",details.nextchapter);
                intent2.putExtra("path",NovelPath);
                intent2.putExtra("name",NovelName);
                intent2.putExtra("author",NovelAuthor);
                intent2.putExtra("update",NovelUpdate);
                intent2.putExtra("status",NovelStatus);
                startActivity(intent2);
                finish();
            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AnotherDetailActivity.this, "返回目录", Toast.LENGTH_SHORT).show();
                //novelinfo = db1.query("novel",);
                Intent intent = new Intent(AnotherDetailActivity.this, ChapterActivity.class);
                intent.putExtra("path",NovelPath);
                intent.putExtra("name",NovelName);
                intent.putExtra("author",NovelAuthor);
                intent.putExtra("update",NovelUpdate);
                intent.putExtra("status",NovelStatus);
                startActivity(intent);
                finish();
            }
        });

    }
}
