package com.yanghuabin.reader;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 18016 on 2018/2/3.
 */

@SuppressWarnings({"UnusedDeclaration"})
public class GlobalConfig {

    public static final String saveFolderName = "saves";
    public static final String imgsSaveFolderName = "imgs";
    public static final String customFolderName = "custom";
    private static final String saveSearchHistoryFileName = "search_history.wk8";
    private static final String saveReadSavesFileName = "read_saves.wk8";
    private static final String saveReadSavesV1FileName = "read_saves_v1.wk8";
    private static final String saveLocalBookshelfFileName = "bookshelf_local.wk8";
    private static final String saveSetting = "settings.wk8";
    private static final String saveUserAccountFileName = "cert.wk8"; // certification file
    private static final String saveUserAvatarFileName = "avatar.jpg";
    private static int maxSearchHistory = 20; // default

    // vars
    private static boolean isInBookshelf = false;
    private static boolean isInLatest = false;
    private static boolean doLoadImage = true;
    private static boolean FirstStoragePathStatus = true;
    //private static Wenku8API.LANG currentLang = Wenku8API.LANG.SC;
    public static String pathPickedSave; // dir picker save path

    // static variables
    private static ArrayList<String> searchHistory = null;
    //private static ArrayList<ReadSaves> readSaves = null; // deprecated
    private static ArrayList<Integer> bookshelf = null;
   // private static ArrayList<ReadSavesV1> readSavesV1 = null; // deprecated
    private static ContentValues allSetting = null;






    public static String getFirstStoragePath() {
        return Environment.getExternalStorageDirectory() + File.separator
                + "Reader" + File.separator;
    }


    public static String getSecondStoragePath() {
        return MyApp.getContext().getFilesDir() + File.separator;
    }

    public static String LoadImage(String data)
    {
        HttpsTrustManager.trustAll();
        String image = "https://r.m.biquge5200.cc/files/article/image/10/10230/10230s.jpg";
        try{
            image = "";
            Document doc  = Jsoup.connect(data).timeout(60000).get();
            //Document doc = Jsoup.parse(data);
            Elements nov = doc.select("meta[property]");
            image = nov.get(3).getElementsByTag("meta").attr("content");
            char[] transform = image.toCharArray();
            if (transform[0]=='/')
                image = "https:"+image;
        }catch (Exception e){
            e.printStackTrace();
        }
        return image;
    }

    public static String LoadIntro(String data)
    {
        HttpsTrustManager.trustAll();
        String description = "";
        try{
            Document doc  = Jsoup.connect(data).timeout(60000).get();
            //Document doc = Jsoup.parse(data);
            Elements nov = doc.select("meta[property]");
            description = nov.get(2).getElementsByTag("meta").attr("content");

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return description;
    }

    public static void initImageLoader(Context context) {
        UnlimitedDiscCache localUnlimitedDiscCache = new UnlimitedDiscCache(
                new File(GlobalConfig.getFirstStoragePath() + "cache"),
                new File(context.getCacheDir() + File.separator + "imgs"));
        DisplayImageOptions localDisplayImageOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.ic_empty_image)
                .showImageForEmptyUri(R.drawable.ic_empty_image)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(250)).build();
        ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(context)
                .diskCache(localUnlimitedDiscCache)
                .defaultDisplayImageOptions(localDisplayImageOptions).build();
        //ImageLoaderConfiguration localImageLoaderConfiguration = ImageLoaderConfiguration.createDefault(context);
        ImageLoader.getInstance().init(localImageLoaderConfiguration);
    }
}
