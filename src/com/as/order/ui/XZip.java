package com.as.order.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.util.Log;

public class XZip {
	
	private static final String TAG = "XZip";
	
	public XZip() {
	}

	public static void extracFile(String zipFileString, Context context) throws Exception {
		Log.e(TAG, "GetFileList");
		ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
		
		ZipEntry zipEntry;
		String szName = "";
		
		File picCacheDir = new File(context.getCacheDir()+"/pic");
		if(!picCacheDir.exists()) {
			picCacheDir.mkdirs();
		}
		
		int len;
		byte[] buff = new byte[1024];
		
		while((zipEntry = inZip.getNextEntry()) != null) {
			szName = zipEntry.getName();
			if(!(szName.indexOf(".jpg") > 0)) {
				continue;
			}
			File localFile = new File(context.getCacheDir()+"/pic/" + szName);
			if(!localFile.exists()) {
				localFile.createNewFile();
			}
			OutputStream os = new FileOutputStream(localFile);
			while((len = inZip.read(buff)) != -1) {
				os.write(buff, 0, len);
			}
			os.flush();
			os.close();
		}
		inZip.close();
	}
}
