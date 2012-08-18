package com.as.ui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.as.db.provider.AsContent.SaIndent;
import com.as.db.provider.AsContent.SaIndentColumns;
import com.as.order.net.ResultListener;
import com.as.order.net.ServerResponse;
import com.as.order.sync.FileUploader;

public class OrderCommitAsyncTask extends
		AsyncTask<Map<String, JSONArray>, Integer, ServerResponse> {
	
	private static final String TAG = "OrderCommitAsyncTask";
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	private ResultListener mListener;
	private Context mContext;
	private static final int SUCCESS = 1;
	private static final int FAIL = 0;
	
	private static final ServerResponse FAIL_RESPONSE = new ServerResponse(FAIL, "获取数据错误");
	
	private String hostUrl;
	
	public OrderCommitAsyncTask(Context context, String hostUrl, ResultListener listener) {
		this.mContext = context;
		this.mListener = listener;
		this.hostUrl = hostUrl;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mListener.onPostDataStart();
	}

	@Override
	protected ServerResponse doInBackground(Map<String, JSONArray>... params) {
//		for(int i=0; i<=100; i++) {
//			publishProgress(i);
//			try {
//				Log.e(TAG, "------ curr i: " + i);
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//				return new ServerResponse(FAIL, e.getMessage());
//			}
//		}
		
		try {
			if(FileUploader.createSaIndentConfirmFile(mContext)) {
				String cupdateFileName = UserUtils.getUserAccount(mContext);
				FTPClient ftpClient = new FTPClient();
				File cIndentFile = new File(mContext.getCacheDir()+"/cupdata/" + cupdateFileName + ".txt");
				if(!cIndentFile.exists()) {
					return new ServerResponse(FAIL, "导出订单文件出错");
				}
				SharedPreferences spp = PreferenceManager.getDefaultSharedPreferences(mContext);
				String REMOTE_HOST = spp.getString(Constant.SP_FTP_HOST, Constant.DEFAULT_SP_FTP_HOST);
				String USER_NAME = spp.getString(Constant.SP_FTP_USERNAME, Constant.DEFAULT_FTP_USERNAME);
				String PASSWORD = spp.getString(Constant.SP_FTP_PASSWORD, Constant.DEFAULT_FTP_PASSWORD);
				FileInputStream fis = null;
				ftpClient.connect(REMOTE_HOST);
				boolean loginResult = ftpClient.login(USER_NAME, PASSWORD);
				if(!loginResult) {
					return new ServerResponse(FAIL, "登录FTP服务器出错");
				}
				int returnCode = ftpClient.getReplyCode();
				if(loginResult && FTPReply.isPositiveCompletion(returnCode)) {
					ftpClient.changeWorkingDirectory("/ORD/cupdata");
					ftpClient.setBufferSize(1024);
					ftpClient.setControlEncoding("UTF-16LE");
					ftpClient.enterLocalPassiveMode();
					fis = new FileInputStream(cIndentFile);
					boolean rs = ftpClient.storeFile(UserUtils.getUserAccount(mContext)+".txt", fis);
					if(rs) {
						return new ServerResponse(SUCCESS, "订单已经确认");
					} else {
						return new ServerResponse(FAIL, "上传订单文件出错，请检查网络");
					}
				} else {
					return new ServerResponse(FAIL, "FTP服务器出错");
				}
			} else {
				return new ServerResponse(FAIL, "导出订单文件出错");
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return new ServerResponse(FAIL, e1.getMessage());
		} catch (SocketException e) {
			e.printStackTrace();
			return new ServerResponse(FAIL, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return new ServerResponse(FAIL, e.getMessage());
		}
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		mListener.onUpdateProgressValue(values[0]);
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(ServerResponse result) {
		super.onPostExecute(result);
		if(result.code == SUCCESS) {
			mListener.onPostDataSuccess(result);
		} else {
			mListener.onPostDataError(result);
		}
	}
	
	private JSONArray genPostParm() throws Exception{
		JSONArray rows = new JSONArray();
		Cursor cursor = null;
		int currValue = 0;
		try {
			cursor = mContext.getContentResolver().query(SaIndent.CONTENT_URI, SaIndent.CONTENT_PROJECTION, null, null, SaIndentColumns.ID + " asc ");
			if(cursor!=null && cursor.moveToFirst()) {
				mListener.onUpdateProgressMax(cursor.getCount());
				while(!cursor.isAfterLast()) {
					JSONObject row = new JSONObject();
					row.put(SaIndentColumns.INDENTNO, (TextUtils.isEmpty(cursor.getString(SaIndent.CONTENT_INDENTNO_COLUMN)) || "null".equalsIgnoreCase(cursor.getString(SaIndent.CONTENT_INDENTNO_COLUMN))) ? "" : cursor.getString(SaIndent.CONTENT_INDENTNO_COLUMN));
					row.put(SaIndentColumns.DEPARTCODE, cursor.getString(SaIndent.CONTENT_DEPARTCODE_COLUMN));
					row.put(SaIndentColumns.WARECODE, cursor.getString(SaIndent.CONTENT_WARECODE_COLUMN));
					row.put(SaIndentColumns.COLORCODE, cursor.getString(SaIndent.CONTENT_COLORCODE_COLUMN));
					row.put(SaIndentColumns.S01, cursor.getString(SaIndent.CONTENT_S01_COLUMN));
					row.put(SaIndentColumns.S02, cursor.getString(SaIndent.CONTENT_S02_COLUMN));
					row.put(SaIndentColumns.S03, cursor.getString(SaIndent.CONTENT_S03_COLUMN));
					row.put(SaIndentColumns.S04, cursor.getString(SaIndent.CONTENT_S04_COLUMN));
					row.put(SaIndentColumns.S05, cursor.getString(SaIndent.CONTENT_S05_COLUMN));
					row.put(SaIndentColumns.S06, cursor.getString(SaIndent.CONTENT_S06_COLUMN));
					row.put(SaIndentColumns.S07, cursor.getString(SaIndent.CONTENT_S07_COLUMN));
					row.put(SaIndentColumns.S08, cursor.getString(SaIndent.CONTENT_S08_COLUMN));
					row.put(SaIndentColumns.S09, cursor.getString(SaIndent.CONTENT_S09_COLUMN));
					row.put(SaIndentColumns.S10, cursor.getString(SaIndent.CONTENT_S10_COLUMN));
					row.put(SaIndentColumns.S11, cursor.getString(SaIndent.CONTENT_S11_COLUMN));
					row.put(SaIndentColumns.S12, cursor.getString(SaIndent.CONTENT_S12_COLUMN));
					row.put(SaIndentColumns.S13, cursor.getString(SaIndent.CONTENT_S13_COLUMN));
					row.put(SaIndentColumns.S14, cursor.getString(SaIndent.CONTENT_S14_COLUMN));
					row.put(SaIndentColumns.S15, cursor.getString(SaIndent.CONTENT_S15_COLUMN));
					row.put(SaIndentColumns.S16, cursor.getString(SaIndent.CONTENT_S16_COLUMN));
					row.put(SaIndentColumns.S17, cursor.getString(SaIndent.CONTENT_S17_COLUMN));
					row.put(SaIndentColumns.S18, cursor.getString(SaIndent.CONTENT_S18_COLUMN));
					row.put(SaIndentColumns.S19, cursor.getString(SaIndent.CONTENT_S19_COLUMN));
					row.put(SaIndentColumns.S20, cursor.getString(SaIndent.CONTENT_S20_COLUMN));
					row.put(SaIndentColumns.INPUTDATE, (TextUtils.isEmpty(cursor.getString(SaIndent.CONTENT_INPUTDATE_COLUMN)) || "null".equalsIgnoreCase(cursor.getString(SaIndent.CONTENT_INPUTDATE_COLUMN))) ? formatter.format(new Date()) + "" : cursor.getString(SaIndent.CONTENT_INPUTDATE_COLUMN));
					row.put(SaIndentColumns.INPUTMAN, (TextUtils.isEmpty(cursor.getString(SaIndent.CONTENT_INPUTMAN_COLUMN)) || "null".equalsIgnoreCase(cursor.getString(SaIndent.CONTENT_INPUTMAN_COLUMN))) ? "" : cursor.getString(SaIndent.CONTENT_INPUTMAN_COLUMN));
					row.put(SaIndentColumns.WARENUM, TextUtils.isEmpty(cursor.getString(SaIndent.CONTENT_WARENUM_COLUMN)) ? "" : cursor.getString(SaIndent.CONTENT_WARENUM_COLUMN));
					row.put(SaIndentColumns.REMARK, (TextUtils.isEmpty(cursor.getString(SaIndent.CONTENT_REMARK_COLUMN)) || "null".equalsIgnoreCase(cursor.getString(SaIndent.CONTENT_REMARK_COLUMN))) ? "" : cursor.getString(SaIndent.CONTENT_REMARK_COLUMN));
					row.put(SaIndentColumns.OFLAG, (TextUtils.isEmpty(cursor.getString(SaIndent.CONTENT_OFLAG_COLUMN)) || "null".equalsIgnoreCase(cursor.getString(SaIndent.CONTENT_OFLAG_COLUMN))) ? "0" : cursor.getString(SaIndent.CONTENT_OFLAG_COLUMN));
					rows.put(row);
					publishProgress(++currValue);
					cursor.moveToNext();
				}
			} else {
				throw new Exception(mContext.getResources().getString(com.as.order.R.string.exception_value_query_saindent));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(mContext.getResources().getString(com.as.order.R.string.exception_value_query_saindent));
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		return rows;
	}
}
