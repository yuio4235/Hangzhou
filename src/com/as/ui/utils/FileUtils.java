package com.as.ui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FileUtils {

	private static final String TAG = "FileUtils";
	/**
	 * get bitmaps for specified sware code
	 * @param code
	 * @return
	 */
	public static Bitmap[] getBitmapsFileCode(Context context, String code) {
		List<Bitmap> bitmaps = new ArrayList<Bitmap>();
		AssetManager am = context.getAssets();
		try {
//			String[] files = am.list("pic");
			File picDir = new File(context.getCacheDir()+"/pic/");
			if(!picDir.exists()) {
				picDir.mkdirs();
			}
			String[] files = picDir.list();
			for(String f : files) {
				if(f.startsWith(code)) {
					FileInputStream fis = new FileInputStream(context.getCacheDir() + "/pic/" + f);
					bitmaps.add(BitmapFactory.decodeStream(fis));
					fis.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bitmap[] bitmapArr = new Bitmap[bitmaps.size()];
		return bitmaps.toArray(bitmapArr);
	}
	
	public static Bitmap[] getBitmapsByNames(Context context, String[] names) {
		List<Bitmap> bitmaps = new ArrayList<Bitmap>();
		AssetManager am = context.getAssets();
		try {
			for(String f : names) {
				FileInputStream fis = new FileInputStream(context.getCacheDir()+"/pic/" + f);
				bitmaps.add(BitmapFactory.decodeStream(fis));
				fis.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bitmap[] bitmapArr = new Bitmap[bitmaps.size()];
		return bitmaps.toArray(bitmapArr);
	}
	
	public static String[] getBitmapFileNames(Context context, String code) {
		List<String> names = new ArrayList<String>();
		AssetManager am = context.getAssets();
		File picDir = new File(context.getCacheDir()+"/pic/");
		if(!picDir.exists()) {
			picDir.mkdirs();
		}
		String[] files = picDir.list();
		for(String f : files) {
			if(f.startsWith(code)) {
				names.add(f);
			}
		}
		String[] nameArr = new String[names.size()];
		return names.toArray(nameArr);
	}
}
