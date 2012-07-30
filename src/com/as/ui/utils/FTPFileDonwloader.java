package com.as.ui.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class FTPFileDonwloader {
	
	private static final String TAG = "FTPFileDownloader";

	public static void downloadFile(Context context, String remotePath, String localCachePath) {
		SharedPreferences spp = PreferenceManager.getDefaultSharedPreferences(context);
		String host = spp.getString("ftp_url", "dlndl.vicp.cc");
		String username = spp.getString("ftp_username", "dln");
		String password = spp.getString("ftp_password", "dlnfeiyang");
		
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(host);
			boolean isLogined = ftpClient.login(username, password);
			if(isLogined) {
				ftpClient.setControlEncoding("GB2312");
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				ftpClient.enterLocalPassiveMode();
				ftpClient.changeWorkingDirectory(remotePath);
				FTPFile[] serverFiles = ftpClient.listFiles();
				int len;
				byte[] buff = new byte[1024];
				File picCacheDir = new File(context.getCacheDir()+ localCachePath);
				if(!picCacheDir.exists()) {
					picCacheDir.mkdirs();
				}
				for(FTPFile file : serverFiles) {
					Log.e(TAG, "current download file name: " + file.getName());
					File localCacheFile = new File(context.getCacheDir()+localCachePath+"/" + file.getName());
					InputStream is = ftpClient.retrieveFileStream(file.getName());
					OutputStream os = new FileOutputStream(localCacheFile);
					if(!(is == null)) {
						while((len = is.read(buff)) != -1) {
							os.write(buff, 0, len);
						}
						os.flush();
						os.close();
						is.close();						
					}
				}
				
				ftpClient.completePendingCommand();
				ftpClient.disconnect();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
