package com.yanghuabin.reader;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yanghuabin.reader.NovelDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.safety.Whitelist ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NovelServer {
	public static String image ="https://r.m.biquge5200.cc/files/article/image/10/10230/10230s.jpg";
	/**
	 * 解析小说HTML数据
	 * @param data
	 * @return
	 */
	public static List<NovelInfo> getNovelInfos(String data){

		//Document doc  = Jsoup.connect(data).timeout(60000).validateTLSCertificates(false).get();
		ArrayList<NovelInfo> novelInfos = new ArrayList<NovelInfo>();
		try{
			HttpsTrustManager.trustAll();
			Document doc  = Jsoup.connect(data).timeout(60000).get();
			//Document doc = Jsoup.parse(data);
			Element  Tab = doc.select("table.grid").first();
			Elements lists = Tab.getElementsByTag("tr");
			int size = lists.size();
			for (int i=1;i<size;i++)
			{
				Element item = lists.get(i);
				Elements els = item.getElementsByTag("td");
				NovelInfo novel = new NovelInfo();
				novel.name = els.get(0).getElementsByClass("odd").text();
				novel.path = els.get(0).getElementsByTag("a").attr("href");
				novel.newChapter = els.get(1).getElementsByClass("even").text();
				novel.author = els.get(2).getElementsByClass("odd").text();
				novel.size = els.get(3).getElementsByClass("even").text();
				novel.updateTime = els.get(4).getElementsByClass("odd").text();
				novel.type = els.get(5).getElementsByClass("even").text();
				novelInfos.add(novel);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return novelInfos;
	}
	
	/**
	 * 解析章节HTML数据
	 * @param data
	 * @return
	 */
	public static List<ChapterInfo> getChapterInfos(String data) {
		ArrayList<ChapterInfo> chapterInfo = new ArrayList<ChapterInfo>();
		try {
			Log.e("Error","getChapterInfos");
			HttpsTrustManager.trustAll();
			Document doc  = Jsoup.connect(data).timeout(60000).get();
			//Document doc = Jsoup.parse(data);
			Elements nov = doc.select("meta[property]");
			//String description = nov.get(2).getElementsByTag("meta").attr("content");
			//String image = nov.get(3).getElementsByTag("meta").attr("content");
			image = nov.get(3).getElementsByTag("meta").attr("content");
			//String status = nov.get(8).getElementsByTag("meta").attr("content");
			//String author_link = nov.get(9).getElementsByTag("meta").attr("content");
			//Log.e("Error",author_link);
			Elements chapters = doc.getElementsByTag("dd");
			int size = chapters.size();
			String k = String.valueOf(size);
			for (int i=0;i<size;i++)
			{
				Log.e("Error",k);
				ChapterInfo items = new ChapterInfo();
				items.chapterlink = chapters.get(i).getElementsByTag("a").attr("href");
				items.chaptername = chapters.get(i).getElementsByTag("a").text();
				//items.author_link = author_link;
				//items.image = image;
				//items.description = description;
				//items.status = status;
				chapterInfo.add(items);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		return chapterInfo;
	}
	
	/**
	 * 解析章节内容HTML数据
	 * @param data
	 * @return
	 */
	public static DetailsInfo getContentInfos(String data){
		DetailsInfo content = new DetailsInfo();
		try {
			Log.e("Error","getContentInfos");
			HttpsTrustManager.trustAll();
			Document doc  = Jsoup.connect(data).timeout(60000).get();
			//doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
			Elements nov = doc.select("div[id]");
			Elements infos = doc.getElementsByTag("script");
			String[] ins = infos.get(2).data().toString().split("var");
			Element chapter_name = doc.getElementsByClass("bookname").first();
			content.txt ="    "+nov.get(1).text().replaceAll("。 ","。"+"\r\n"+"    ");
			String[] ins1 = ins[4].split("\"");
			content.previouschapter = ins1[1];
			Log.e("Errorpre",content.previouschapter);
			ins1 = ins[5].split("\"");
			content.nextchapter = ins1[1];
			Log.e("Errornext",content.nextchapter);
			ins1 = ins[6].split("\"");
			content.contents = ins1[1];
			Log.e("Errorcontents",content.contents);
			content.cname = chapter_name.getElementsByTag("h1").text();
			Log.e("Errorcname",content.cname);
		}catch (Exception e) {
			e.printStackTrace();
		}

		return content;
	}

	/**
	 * 添加历史纪录
	 * @param novels
	 * @param db
	 *
	 * @param position
	 */
	public static void insert(List<NovelInfo> novels, SQLiteDatabase db,
                              int position) {
		ContentValues values = new ContentValues();
		values.put(NovelDB.TYPENAME, novels.get(position-1).typeName);
		values.put(NovelDB.BOOKID, novels.get(position-1).bookId);
		values.put(NovelDB.AUTHOR, novels.get(position-1).author);
		values.put(NovelDB.UPDATETIME, novels.get(position-1).updateTime);
		values.put(NovelDB.NAME, novels.get(position-1).name);
		values.put(NovelDB.TYPE, novels.get(position-1).type);
		values.put(NovelDB.NEWCHAPTER, novels.get(position-1).newChapter);
		values.put(NovelDB.SIZE, novels.get(position-1).size);
		values.put(NovelDB.PATH, novels.get(position-1).path);
		db.insert(NovelDB.TABLE_NAME, null, values);
		
	}
	
}
