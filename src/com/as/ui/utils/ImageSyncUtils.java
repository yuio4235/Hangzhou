package com.as.ui.utils;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.as.db.provider.AsProvider;
import com.as.order.ui.AsProgressDialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.preference.PreferenceManager;

public class ImageSyncUtils {

	public static void clearUpLocalImgs(Context context) {
		File file = new File(context.getCacheDir() + "/pic");
		if(!file.exists()) {
			return;
		}
		File[] imgs = file.listFiles();
		for(File img : imgs) {
			img.delete();
		}
	}
	
	public void downLoadImgs(Context context) {
		SharedPreferences spp = PreferenceManager.getDefaultSharedPreferences(context);
		String host = spp.getString(Constant.SP_FTP_HOST, "");
		String username = spp.getString(Constant.SP_FTP_USERNAME, "");
		String password = spp.getString(Constant.SP_FTP_PASSWORD, "");
		FTPClient ftp = new FTPClient();
		try {
			ftp.connect(host);
			boolean isLogined = ftp.login(username, password);
			if(isLogined) {
				ftp.changeWorkingDirectory("/ORD/pic");
				FTPFile[] files = ftp.listFiles();
				File picDir = new File(context.getCacheDir()+"/pic");
				if(!picDir.exists()) {
					picDir.mkdirs();
				}
				for(FTPFile file : files) {
					File pic = new File(picDir + "/" + file.getName());
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void cleanUpLocalInfoFiles(Context context, String[] files) {
		for(int i=0; i<files.length; i++) {
			File file = new File(context.getCacheDir() + "/info/" + files[i]);
			if(file.exists()) {
				file.delete();
			}
		}
	}
	
	public static void checkWareCodeInSaIndent(Context context) {
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		String delSql = " delete from saindent where warecode not in ( select distinct warecode from sawarecode) ";
		if(db != null) {
			try {
				db.execSQL(delSql);
			} finally {
				db.close();
			}
		}
		DataInitialUtils.initSaIndent(context, UserUtils.getUserAccount(context));
	}
	
	public static void checkSaOrderScore(Context context) {
		SQLiteDatabase db = AsProvider.getWriteableDatabase(context);
		String sql = " delete from saorderscore where departcode <> '"+UserUtils.getUserAccount(context)+"' or warecode not in ( select distinct warecode from sawarecode ) ";
		if(db != null) {
			try {
				db.execSQL(sql);
			} finally {
				db.close();
			}
		}
	}
}
