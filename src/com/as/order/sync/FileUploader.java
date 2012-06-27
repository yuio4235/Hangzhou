package com.as.order.sync;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.client.UserTokenHandler;

import android.content.Context;
import android.database.Cursor;

import com.as.db.provider.AsContent.SaIndent;
import com.as.ui.utils.AlertUtils;
import com.as.ui.utils.UserUtils;

public class FileUploader {
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd MM:mm:ss");
	
	public static boolean createSaIndentFile(Context context) {
		File updatapath = new File(context.getCacheDir() + "/updata");
		if(!updatapath.exists()) {
			updatapath.mkdirs();
		}
		Cursor cursor = 
			context.getContentResolver().query(
					SaIndent.CONTENT_URI, 
					SaIndent.CONTENT_PROJECTION, 
					null, null, 
					SaIndent.RECORD_ID + " asc "
			);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				String updataFileName = UserUtils.getUserAccount(context);
				File updataFile = new File(updatapath + "/" + updataFileName + ".txt");
				if(!updataFile.exists()) {
					updataFile.createNewFile();
				}
				BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(updataFile)));
				while(!cursor.isAfterLast()) {
					StringBuffer sb = new StringBuffer();
					sb.append(cursor.getString(SaIndent.CONTENT_INDENTNO_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_DEPARTCODE_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_WARECODE_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_COLORCODE_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S01_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S02_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S03_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S04_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S05_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S06_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S07_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S08_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S09_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S10_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S11_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S12_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S13_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S14_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S15_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S16_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S17_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S18_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S19_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_S20_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_INPUTDATE_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_INPUTMAN_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_WARENUM_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_REMARK_COLUMN));
					sb.append("\t");
					sb.append(cursor.getString(SaIndent.CONTENT_OFLAG_COLUMN));
					sb.append("\n");
					wr.write(sb.toString());
					wr.flush();
					cursor.moveToNext();
				}
				wr.flush();
				wr.close();
				return true;
			} 
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		return false;
	}
	
	public static boolean uploadSaIndent(Context context) {
		FTPClient ftpClient = new FTPClient();
		File indentFile = new File(context.getCacheDir() + "/updata/" + UserUtils.getUserAccount(context) + ".txt");
		String REMOTE_HOST = "dlndl.vicp.cc";
		String USER_NAME = "dln";
		String PASSWORD = "dlnfeiyang";
		FileInputStream fis = null;
		if(!indentFile.exists()) {
			return false;
		}
		try {
			ftpClient.connect(REMOTE_HOST);
			boolean loginResult = ftpClient.login(USER_NAME, PASSWORD);
			if(!loginResult) {
				return false;
			}
			int returnCode = ftpClient.getReplyCode();
			if(loginResult && FTPReply.isPositiveCompletion(returnCode)) {
				ftpClient.changeWorkingDirectory("/ORD/updata");
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-16LE");
				ftpClient.enterLocalPassiveMode();
				fis = new FileInputStream(indentFile);
				ftpClient.storeFile(UserUtils.getUserAccount(context)+".txt", fis);
				return true;
			} else {
				return false;
			}
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
