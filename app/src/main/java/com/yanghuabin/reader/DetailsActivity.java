package com.yanghuabin.reader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class DetailsActivity extends Activity {

	//private WebView webView;
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
	private static String Chaptername = "";
	private final int ACT_TXT = 1;
	private Handler handler =new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case ACT_TXT:// 在Activity中刷新Fragment
					contentview.setText(details.txt);
					chaptertitle.setText(Chaptername);
					break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		Toast.makeText(this, "正在加载...", Toast.LENGTH_SHORT).show();
		
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
		final Intent intent = getIntent();
		Chaptername = intent.getStringExtra("chaptername");
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
				String str ;
				try{
					str = new String(var3,"GBK");//需要转码
				}catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					Log.e("DetailActivity", e.getMessage());
					str = ""; // prevent crash
				}
				details = NovelServer.getContentInfos(str);
                details.cname = Chaptername;
                contentview.setText(details.txt);
				chaptertitle.setText(Chaptername);

			}
			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
				throwable.printStackTrace();
			}
		});*/
		try{
			new Thread(new Runnable() {
				@Override
				public void run() {
					details = NovelServer.getContentInfos(Chapterlink);
					details.cname = Chaptername;
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
				//Toast.makeText(DetailsActivity.this, "上一章", Toast.LENGTH_SHORT).show();
				Intent intent1 = new Intent(DetailsActivity.this, AnotherDetailActivity.class);
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
				//Toast.makeText(DetailsActivity.this, "下一章", Toast.LENGTH_SHORT).show();
				Intent intent2 = new Intent(DetailsActivity.this, AnotherDetailActivity.class);
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
				//Toast.makeText(DetailsActivity.this, "返回目录", Toast.LENGTH_SHORT).show();
				//Intent intent3 = new Intent(DetailsActivity.this, AnotherDetailActivity.class);
				//intent3.putExtra("chapterlink",details.previouschapter);
				//startActivity(intent3);
				finish();
			}
		});

	}

}
