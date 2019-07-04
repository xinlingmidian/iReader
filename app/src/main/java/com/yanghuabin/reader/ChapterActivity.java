package com.yanghuabin.reader;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
//import com.afollestad.materialdialogs.GravityEnum;
//import com.afollestad.materialdialogs.MaterialDialog;
//import com.afollestad.materialdialogs.Theme;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class ChapterActivity extends AppCompatActivity {
    public String NovelPath = "";
    private String NovelUpdate = "";
    private String NovelName = "";
    private String NovelAuthor = "";
    private String NovelStatus = "";
    private ListView chapterList;
    public List<ChapterInfo> data = new ArrayList<ChapterInfo>();

    // constant
    private final String FromLocal = "fav";

    // private vars
    private int aid = 1;
    private String from = "", title = "";
    private boolean isLoading = true;
    private RelativeLayout rlMask = null; // mask layout
    private LinearLayout mLinearLayout = null;
    private LinearLayout llCardLayout = null;
    private static ImageView ivNovelCover = null;
    private TextView tvNovelTitle = null;
    private TextView tvNovelAuthor = null;
    private TextView tvNovelStatus = null;
    private TextView tvNovelUpdate = null;
    private TableRow tvNovelShortIntro = null; // need hide
    private TextView tvNovelFullIntro = null;
    private ImageButton ibNovelOption = null; // need hide
    //private MaterialDialog pDialog = null;
    private FloatingActionButton fabFavorate = null;
    private FloatingActionButton fabDownload = null;
    private FloatingActionsMenu famMenu = null;
    private SmoothProgressBar spb = null;
    //private NovelItemMeta mNovelItemMeta = null;
    //private List<VolumeList> listVolume = null;
    private String novelFullMeta = null, novelFullIntro = null, novelFullVolume = null;
    private final int ACT_REF = 1;
    private final int ACT_IMG = 2;
    private final int ACT_INFO = 3;
    private static String info = "";
    private static String image = "";
    private  Handler handler =new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACT_REF:// 在Activity中刷新Fragment
                    chapterList.setAdapter(new ChapterAdapter(ChapterActivity.this, R.layout.view_novel_chapter_item, getData()));
                    break;
                case ACT_IMG:
                    ImageLoader.getInstance().displayImage(image, ivNovelCover); // move to onCreateView!
                    break;
                case ACT_INFO:
                    tvNovelFullIntro.setText(info);
                    break;
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chapter_layout);

        Toast.makeText(this, "正在加载...", Toast.LENGTH_LONG).show();
        NovelPath = getIntent().getStringExtra("path");
        NovelName = getIntent().getStringExtra("name");
        NovelAuthor = getIntent().getStringExtra("author");
        NovelUpdate = getIntent().getStringExtra("update");
        NovelStatus = getIntent().getStringExtra("status");
        ///////////////////////////////////////////////////////////
        getChapterData(NovelPath);



        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        if(getSupportActionBar() != null && upArrow != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            upArrow.setColorFilter(getResources().getColor(R.color.default_white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        // change status bar color tint, and this require SDK16
        if (Build.VERSION.SDK_INT >= 16 ) {
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable all tint
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setTintAlpha(0.15f);
            tintManager.setNavigationBarAlpha(0.0f);
            // set all color
            tintManager.setTintColor(getResources().getColor(android.R.color.black));
            // set Navigation bar color
            if(Build.VERSION.SDK_INT >= 21)
                getWindow().setNavigationBarColor(getResources().getColor(R.color.myNavigationColor));
        }

        // UIL setting
        if(ImageLoader.getInstance() == null || !ImageLoader.getInstance().isInited()) {
            GlobalConfig.initImageLoader(this);
        }


        // get views
        chapterList = (ListView) findViewById(R.id.chapteritems);
        rlMask = (RelativeLayout) findViewById(R.id.white_mask);
        mLinearLayout = (LinearLayout) findViewById(R.id.novel_info_scroll);
        llCardLayout = (LinearLayout) findViewById(R.id.item_card);
        ivNovelCover = (ImageView) findViewById(R.id.novel_cover);
        tvNovelTitle = (TextView) findViewById(R.id.novel_title);
        tvNovelAuthor = (TextView) findViewById(R.id.novel_author);
        tvNovelStatus = (TextView) findViewById(R.id.novel_status);
        tvNovelUpdate = (TextView) findViewById(R.id.novel_update);
        tvNovelShortIntro = (TableRow) findViewById(R.id.novel_intro_row);
        tvNovelFullIntro = (TextView) findViewById(R.id.novel_intro_full);
        ibNovelOption = (ImageButton) findViewById(R.id.novel_option);
        fabFavorate = (FloatingActionButton) findViewById(R.id.fab_favorate);
        fabDownload = (FloatingActionButton) findViewById(R.id.fab_download);
        famMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        spb = (SmoothProgressBar) findViewById(R.id.spb);

        tvNovelUpdate.setText(NovelUpdate);
        tvNovelStatus.setText(NovelStatus);
        tvNovelAuthor.setText(NovelAuthor);

        // hide view and set colors
        tvNovelTitle.setText(NovelName);
        //if(LightCache.testFileExist(GlobalConfig.getFirstStoragePath() + "imgs" + File.separator + NovelName + ".jpg"))
        //    ImageLoader.getInstance().displayImage("file://" + GlobalConfig.getFirstStoragePath() + "imgs" + File.separator + NovelName + ".jpg", ivNovelCover);
        //else if(LightCache.testFileExist(GlobalConfig.getSecondStoragePath() + "imgs" + File.separator + NovelName+ ".jpg"))
        //    ImageLoader.getInstance().displayImage("file://" + GlobalConfig.getSecondStoragePath() + "imgs" + File.separator + NovelName+ ".jpg", ivNovelCover);
        //else
            //ImageLoader.getInstance().displayImage(data.get(0).image, ivNovelCover); // move to onCreateView!
        //    getImageData(NovelPath);
        //ImageLoader.getInstance().displayImage(NovelServer.image, ivNovelCover); // move to onCreateView!
        //Log.e("Error",data.get(1).image);

        tvNovelShortIntro.setVisibility(TextView.GONE);
        ibNovelOption.setVisibility(ImageButton.INVISIBLE);
        fabFavorate.setColorFilter(getResources().getColor(R.color.default_white), PorterDuff.Mode.SRC_ATOP);
        fabDownload.setColorFilter(getResources().getColor(R.color.default_white), PorterDuff.Mode.SRC_ATOP);
        llCardLayout.setBackgroundResource(R.color.menu_transparent);

        // fetch all info
        getSupportActionBar().setTitle(R.string.action_novel_info);
        spb.setVisibility(View.INVISIBLE); // wait for runnable
        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                spb.setVisibility(View.VISIBLE);
                //if (from.equals(FromLocal))
                //    refreshInfoFromLocal();
                //else
                //    refreshInfoFromCloud();
                onReflash();
            }
        }, 500);*/

        // set on click listeners
        famMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                rlMask.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                rlMask.setVisibility(View.INVISIBLE);
            }
        });
        rlMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collapse the fam
                if (famMenu.isExpanded())
                    famMenu.collapse();
            }
        });
        if(Build.VERSION.SDK_INT >= 16) {
            tvNovelTitle.setBackground(getResources().getDrawable(R.drawable.btn_menu_item));
            tvNovelAuthor.setBackground(getResources().getDrawable(R.drawable.btn_menu_item));
        }
        tvNovelTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoading) {
                    Toast.makeText(ChapterActivity.this, getResources().getString(R.string.system_loading_please_wait), Toast.LENGTH_SHORT).show();
                    return;
                }

                // show aid: title
                // Snackbar.make(mLinearLayout, aid + ": " + mNovelItemMeta.title, Snackbar.LENGTH_SHORT).show();
                /*new MaterialDialog.Builder(ChapterActivity.this)
                        .theme(Theme.LIGHT)
                        .titleColorRes(R.color.dlgTitleColor)
                        .backgroundColorRes(R.color.dlgBackgroundColor)
                        .contentColorRes(R.color.dlgContentColor)
                        .positiveColorRes(R.color.dlgPositiveButtonColor)
                        .title(R.string.dialog_content_novel_title)
                        .content(aid + ": " + mNovelItemMeta.title)
                        .contentGravity(GravityEnum.CENTER)
                        .positiveText(R.string.dialog_positive_known)
                        .show();*/

            }
        });
        tvNovelAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoading) {
                    Toast.makeText(ChapterActivity.this, getResources().getString(R.string.system_loading_please_wait), Toast.LENGTH_SHORT).show();
                    return;
                }

               /* new MaterialDialog.Builder(NovelInfoActivity.this)
                        .theme(Theme.LIGHT)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);

                                // search author name
                                Intent intent = new Intent(NovelInfoActivity.this, SearchResultActivity.class);
                                intent.putExtra("key", mNovelItemMeta.author);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // long-press will cause repetitions
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.hold);
                            }
                        })
                        .content(R.string.dialog_content_search_author)
                        .positiveText(R.string.dialog_positive_ok)
                        .negativeText(R.string.dialog_negative_biao)
                        .show();*/
            }
        });

        chapterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                // Toast.makeText(ChapterActivity.this, "yidian", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChapterActivity.this, DetailsActivity.class);
                intent.putExtra("chaptername", data.get(position).chaptername);
                intent.putExtra("chapterlink", data.get(position).chapterlink);
                intent.putExtra("path",NovelPath);
                intent.putExtra("name",NovelName);
                intent.putExtra("author",NovelAuthor);
                intent.putExtra("update",NovelUpdate);
                intent.putExtra("status",NovelStatus);
                startActivity(intent);
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getResources().getString(R.string.action_novel_info));
        getMenuInflater().inflate(R.menu.menu_novel_info, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        // end famMenu first
        if(famMenu.isExpanded()) {
            famMenu.collapse();
            return;
        }

        // normal exit
        if(Build.VERSION.SDK_INT < 21)
            finish();
        else
            finishAfterTransition(); // end directly
    }

    /*private void getImageData(String NovelPath)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setResponseTimeout(8000);
        client.setTimeout(8000);
        client.get(NovelPath, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int var1, Header[] var2, byte[] var3) {
                //super.onSuccess(content);
                //数据的解析和转化
                disableConnectionReuseIfNecessary();
                String str ;
                try{
                    str = new String(var3,"GBK");//需要转码
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e("ChapterActivity", e.getMessage());
                    str = ""; // prevent crash
                }
                //data.addAll(NovelServer.getChapterInfos(str));//解析小说章节的主页的内容
                tvNovelFullIntro.setText(GlobalConfig.LoadIntro(str));
                ImageLoader.getInstance().displayImage(GlobalConfig.LoadImage(str), ivNovelCover); // move to onCreateView!
                //novels.addAll(data);
                //showListView();
                //pd.setMessage("成功...");
                //pd.show();
//				novelList.setAdapter(new NovelsAdapter(getActivity(),novels));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                //super.onFailure(i,headers,bytes,throwable);
                //pd.setMessage("失败...");
                //pd.show();
                //String str = new String(bytes);
                //Log.e("Error", String.valueOf(i));
                throwable.printStackTrace();
                return;
            }
        });
    }*/





    private void getChapterData(final String NovelPath)
    {
        Log.e("Error",NovelPath);
        //String NovelPath1 = regex(NovelPath);
        //Log.e("Error",NovelPath1);
        /*AsyncHttpClient client = new AsyncHttpClient();
        client.setResponseTimeout(8000);
        client.setTimeout(8000);
        client.get(NovelPath, new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int var1, Header[] var2, byte[] var3) {
                //super.onSuccess(content);
                //数据的解析和转化
                disableConnectionReuseIfNecessary();
                String str ;
                try{
                    str = new String(var3,"GBK");
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e("ChapterActivity", e.getMessage());
                    str = ""; // prevent crash
                }
                data.addAll(NovelServer.getChapterInfos(str));//解析小说章节的主页的内容
                ImageLoader.getInstance().displayImage(GlobalConfig.LoadImage(str), ivNovelCover); // move to onCreateView!
                tvNovelFullIntro.setText(GlobalConfig.LoadIntro(str));
                chapterList.setAdapter(new ChapterAdapter(ChapterActivity.this, R.layout.view_novel_chapter_item, getData()));

            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                super.onFailure(i,headers,bytes,throwable);
                pd.setMessage("失败...");
                pd.show();
                String str = new String(bytes);
                Log.e("Error", String.valueOf(i));
                throwable.printStackTrace();
            }
        });*/
        try{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    data.addAll(NovelServer.getChapterInfos(NovelPath));//解析小说章节的主页的内容
                    Message message = new Message();
                    message.what = ACT_REF;
                    handler.sendMessage(message);
                }
            }).start();
            Log.i("success", "成功");
        }catch (Exception e) {
            e.printStackTrace();
            Log.i("onfailure", "失败");
        }
        try{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    image = GlobalConfig.LoadImage(NovelPath);
                    Message message = new Message();
                    message.what = ACT_IMG;
                    handler.sendMessage(message);
                }
            }).start();
            Log.i("success", "成功");
        }catch (Exception e) {
            e.printStackTrace();
            Log.i("onfailure", "失败");
        }
        try{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    info = GlobalConfig.LoadIntro(NovelPath);
                    Message message = new Message();
                    message.what = ACT_INFO;
                    handler.sendMessage(message);
                }
            }).start();
            Log.i("success", "成功");
        }catch (Exception e) {
            e.printStackTrace();
            Log.i("onfailure", "失败");
        }
    }


    public String regex(String waps)  //使用正则表达式
    {
        String wapspath = "https://m.biquge5200.cc/info-";
        Pattern p = Pattern.compile("([\\d-/]*)$");
        Matcher m = p.matcher(waps);
        if (m.find())
        {
            wapspath += m.group();
        }
        return wapspath;
    }


    public void onReflash() {
        data.clear();
        //page = 1;
        getChapterData(NovelPath);
        //novelList.reflashComplete();
    }

    public void disableConnectionReuseIfNecessary() {
        // Work around pre-Froyo bugs in HTTP connection reuse.
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");

        }
    }

    @Override
    protected void onDestroy() {
        // 回收该页面缓存在内存中的图片
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        super.onDestroy();
    }


    public ArrayList<String> getData(){
        ArrayList<String> list = new ArrayList<String>();
        for(ChapterInfo chapter : data){
            list.add(chapter.chaptername);
        }
        return list;
    }






    /*private void refreshInfoFromLocal() {
        isLoading = true;
        spb.progressiveStart();
        FetchInfoAsyncTask fetchInfoAsyncTask = new FetchInfoAsyncTask();
        fetchInfoAsyncTask.execute(1); // load from local
    }

    private void refreshInfoFromCloud() {
        isLoading = true;
        spb.progressiveStart();
        FetchInfoAsyncTask fetchInfoAsyncTask = new FetchInfoAsyncTask();
        fetchInfoAsyncTask.execute();
    }*/
}
