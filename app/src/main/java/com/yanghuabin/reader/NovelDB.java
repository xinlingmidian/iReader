package com.yanghuabin.reader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NovelDB extends SQLiteOpenHelper {

	public static final String TABLE_NAME = "novel";
	public static final String ID = "_id";
	public static final String TYPENAME = "typeName";// 小说类型名称
	public static final String BOOKID = "bookId";// 小说id
	public static final String AUTHOR = "author";// 小说作者
	public static final String UPDATETIME = "updateTime";// 更新日期
	public static final String NAME = "name";// 小说名称
	public static final String TYPE = "type";// 小说类型（1~10）
	public static final String NEWCHAPTER = "newChapter";// 最新章节
	public static final String SIZE = "size";// 小说大小
	public static final String PATH = "path";// 小说大小

	public NovelDB(Context context) {
		super(context, TABLE_NAME, null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("create table " + TABLE_NAME + "(" + ID
				+ " integer primary key autoincrement," + TYPENAME + " text,"
				+ BOOKID + " text unique," + AUTHOR + " text," + UPDATETIME + " text,"
				+ NAME + " text," + TYPE + " text," + NEWCHAPTER + " text,"
				+ SIZE +" text,"+ PATH + " text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


	}
}
