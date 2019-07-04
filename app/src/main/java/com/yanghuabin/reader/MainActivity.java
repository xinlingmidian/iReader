package com.yanghuabin.reader;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {



    // 当前的内容视图下(即侧滑菜单关闭状态下)ActionBar上的标题
    String currentContentTitle;
    Fragment mainFragment;

    FragmentTransaction transaction;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // 开始时显示全局标题“笔趣小说”
        currentContentTitle = getResources().getString(R.string.app_name);

        fragmentManager = getFragmentManager();

        mainFragment = new MainFragment();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, mainFragment).commit();
        Log.i("Error", "Mainactivity:");
    }
}
