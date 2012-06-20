package com.as.ui.utils;

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
			String[] files = am.list("");
			for(String f : files) {
				if(f.startsWith(code)) {
					bitmaps.add(BitmapFactory.decodeStream(am.open(f)));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bitmap[] bitmapArr = new Bitmap[bitmaps.size()];
		return bitmaps.toArray(bitmapArr);
	}
}
