package org.app.music.db;

import org.app.music.bean.Album;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AlbumDao {
	private MusicDB dbHpler;

	public AlbumDao(Context context) {
		dbHpler = new MusicDB(context);
	}

	/**
	 * 判断专辑是否存在，存在返回id
	 * */
	public int isExist(String name) {
		int id = -1;
		SQLiteDatabase db = dbHpler.getReadableDatabase();
		Cursor cr = db.rawQuery(
				"SELECT " + DBData.ALBUM_ID + " FROM " + DBData.ALBUM_TABLENAME
						+ " WHERE " + DBData.ALBUM_NAME + "=?",
				new String[] { name });
		if (cr.moveToNext()) {
			id = cr.getInt(0);
		}
		cr.close();
		db.close();
		return id;
	}

	/**
	 * 添加
	 * */
	public long add(Album album) {
		SQLiteDatabase db = dbHpler.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBData.ALBUM_NAME, album.getName());
		values.put(DBData.ALBUM_PICPATH, album.getPicPath());
		long rs = db.insert(DBData.ALBUM_TABLENAME, DBData.ALBUM_NAME, values);
		db.close();
		return rs;
	}

	/**
	 * 删除
	 * */
	public int delete(int id) {
		SQLiteDatabase db = dbHpler.getWritableDatabase();
		int rs = db.delete(DBData.ALBUM_TABLENAME, DBData.ALBUM_ID + "=?",
				new String[] { String.valueOf(id) });
		db.close();
		return rs;
	}

}