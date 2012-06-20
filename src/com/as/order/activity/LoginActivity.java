package com.as.order.activity;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.as.db.provider.AsContent;
import com.as.db.provider.AsContent.SaColorCode;
import com.as.db.provider.AsContent.SaPara;
import com.as.db.provider.AsContent.SaWareCode;
import com.as.db.provider.AsContent.SaWareColor;
import com.as.db.provider.AsContent.SaWareGroup;
import com.as.db.provider.AsContent.SaWareSize;
import com.as.db.provider.AsContent.SaWareType;
import com.as.db.provider.AsContent.ShowSize;
import com.as.order.R;
import com.as.order.ui.AsProgressDialog;
import com.as.ui.utils.AlertUtils;
import com.as.ui.utils.DataInitialUtils;

public class LoginActivity extends AbstractActivity {

	private LinearLayout login;
	private static final int ID_LOGIN_BTN = R.id.title_btn_right;
	private static final int ID_TITLE_BACK = R.id.title_btn_left;
	
	private EditText accountEt;
	private EditText passwdEt;
	
	//dlndl.vicp.cc
	private static final String REMOTE_HOST = "dlndl.vicp.cc";
	private static final String USER_NAME = "dln";
	private static final String PASSWORD = "dlnfeiyang";
//	private static final String USER_NAME = "admin";
//	private static final String PASSWORD = "admin";
	
	private int downloadFileSize = 0;
	private int totalFileSize = 0;
	
	//
	private static final String SERVER_HOST = "dlndl.vicp.cc";
	
	private AsProgressDialog mUpdatingDialog;
	private AsProgressDialog mUpdatingDataDialog;
	
	private String[] files = new String[]{
			"sawarecode.txt",
			"stpara.txt",
			"saware_color.txt",
			"saware_size.txt",
			"sacolorcode.txt",
			"showsize.txt",
			"type1.txt",
			"sawaretype.txt",
			"sawaregroup.txt"
	};
	
	private static final int ID_DOWNLOADING_DIALOG  = 1001;
	private static final int ID_UPDATING_DATA_DIALOG = 1002;
	
	private static final int MSG_DOWNLOADING_FILE = 2001;
	private static final int MSG_SHOW_PROGRESS_DIALOG = 2002;
	private static final int MSG_DISMISS_PROGRESS_DIALOG = 2003;
	private static final int MSG_UPDATE_DOWNLOAD_FILE_PROGRESS = 2004;
	private static final int MSG_INSERT_PROGRESS_DIALG = 2005;
	private static final int MSG_INSERT_DATA = 2006;
	private static final int MSG_DOWNLOAD_PIC = 2007;
	
	private static final int MSG_INSERT_SAWARECODE = 3001;
	private static final int MSG_INSERT_STPARA = 3002;
	
	private Handler mHandler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case MSG_DOWNLOADING_FILE:
				Thread t = new Thread(){
					public void run() {
						Looper.prepare();
						try {
							for(String f : files) {
								down_file(f, "/ORD/info");
							}
						} catch (IOException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
						dismissDialog(ID_DOWNLOADING_DIALOG);
						Message msg = mHandler.obtainMessage();
						msg.what = MSG_DOWNLOAD_PIC;
						msg.sendToTarget();
					};
				};
				t.start();
				break;
				
			case MSG_DOWNLOAD_PIC:
				showDialog(ID_DOWNLOADING_DIALOG);
				Thread tt = new Thread(){
					public void run() {
						Looper.prepare();
						try {
							down_pic();
						} catch (IOException e) {
							e.printStackTrace();
						}
						dismissDialog(ID_DOWNLOADING_DIALOG);
						Message msg = mHandler.obtainMessage();
						msg.what = MSG_INSERT_DATA;
						msg.sendToTarget();
					};
				};
				tt.start();
				break;
				
			case MSG_SHOW_PROGRESS_DIALOG:
				showDialog(ID_DOWNLOADING_DIALOG);
				break;
				
			case MSG_DISMISS_PROGRESS_DIALOG:
				if(mUpdatingDialog.isShowing()) {
					mUpdatingDialog.dismiss();
				} else {
					AlertUtils.toastMsg(LoginActivity.this, "no dialog");
				}
				break;
				
			case MSG_INSERT_SAWARECODE:
				showDialog(ID_UPDATING_DATA_DIALOG);
				Thread insertSawarecodeThread = new InsertSawareCodeThread();
				insertSawarecodeThread.start();
				break;
				
			case MSG_UPDATE_DOWNLOAD_FILE_PROGRESS:
				DownloadFileInfo fileInfo = (DownloadFileInfo) msg.obj;
				mUpdatingDialog.updateProgress(fileInfo.currSize);
				mUpdatingDialog.updatePorgressText(fileInfo.fileName + "," + fileInfo.currSize+"/" + fileInfo.totalSize);
				break;
				
			case MSG_INSERT_PROGRESS_DIALG:
				DialogMessage dm = (DialogMessage)msg.obj;
				mUpdatingDataDialog.updateProgress((int)dm.value);
				mUpdatingDataDialog.updatePorgressText(dm.text);
				break;
				
			case MSG_INSERT_STPARA:
				showDialog(ID_UPDATING_DATA_DIALOG);
				Thread insertStPara = new InsertSaPara();
				insertStPara.start();
				break;
				
			case MSG_INSERT_DATA:
				showDialog(ID_UPDATING_DATA_DIALOG);
				Thread insertData = new InsertData();
				insertData.start();
				break;
				
				default:
					break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		login = (LinearLayout) layoutInflater.inflate(R.layout.login, null);
		mRootView.addView(login, FF);
		
		accountEt = (EditText) findViewById(R.id.login_account);
		passwdEt = (EditText) findViewById(R.id.login_password);
		
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitleRightBtn(this.getString(R.string.login));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = getSharedPreferences("data_downloaded", 0);
		boolean isDataDownloaded = sp.getBoolean("data_downloaded", false);
		if(!isDataDownloaded) {
			showDialog(ID_DOWNLOADING_DIALOG);
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_DOWNLOADING_FILE;
			msg.sendToTarget();			
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) 
		{
		case ID_LOGIN_BTN:
			if(!("A110".equals(accountEt.getText().toString().trim()) && "111111".equals(passwdEt.getText().toString().trim()))) {
				AlertUtils.toastMsg(LoginActivity.this, "用户名或密码错误");
			} else {
				SharedPreferences sp = getSharedPreferences("user_account", Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("user_account", "A100");
				editor.commit();
				DataInitialUtils.initSaIndent(LoginActivity.this, "A100");
				Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(mainActivityIntent);
			}
			break;
			
		case ID_TITLE_BACK:
			finish();
			break;
			
			default:
				break;
		}
	}
	
	private void initSaIndent(String departCode) {
		String SQL = " INSERT "
			+ " saindent(departcode, warecode, colorcode, inputdate, inputman) "
			+ " select sawarecode.warecode, colorcode,";
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case ID_DOWNLOADING_DIALOG:
			mUpdatingDialog = new AsProgressDialog(LoginActivity.this, R.style.AsDialog, "下载数据文件");
			return mUpdatingDialog;
			
		case ID_UPDATING_DATA_DIALOG:
			mUpdatingDataDialog = new AsProgressDialog(LoginActivity.this, R.style.AsDialog, "更新数据库");
			return mUpdatingDataDialog;
			
			default:
				break;
		}
		return null;
	}
	
	public void down_file(String fileName, String path) throws Exception{
		try {
			FTPClient ftp = new FTPClient();
			ftp.connect(SERVER_HOST);
			boolean isLogined = ftp.login(USER_NAME, PASSWORD);
			if(isLogined) {
				ftp.setControlEncoding("UTF-16LE");
				ftp.changeWorkingDirectory(path);
				FTPFile[] ftpFiles = ftp.listFiles();
				InputStream is = ftp.retrieveFileStream(fileName);
				if(is!=null) {
					Log.e("===", "total file size: " + is.available());
//				int totalSize = is.available();
					int currSize = 0;
					mUpdatingDialog.setMax(is.available());
					File infoDir = new File(getCacheDir() + "/info");
					if(!infoDir.exists()) {
						infoDir.mkdirs();
					}
					File localFile = new File(getCacheDir() + "/info/"+fileName);
					if(!localFile.exists()) {
						localFile.createNewFile();
					}
					OutputStream os = new FileOutputStream(localFile);
					byte[] buff = new byte[1024];
					int len;
					while((len = is.read(buff)) != -1) {
						os.write(buff, 0, len);
						downloadFileSize += len;
						Message msg = mHandler.obtainMessage();
						currSize += len;
						DownloadFileInfo fileInfo = new DownloadFileInfo(fileName, path, is.available(), currSize);
						msg.obj = fileInfo;
						msg.what = MSG_UPDATE_DOWNLOAD_FILE_PROGRESS;
						msg.sendToTarget();
					}
					os.close();
					is.close();
					ftp.completePendingCommand();
					ftp.disconnect();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			throw new Exception("SocketException");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Exception("FileNotFoundException");
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("IOException");
		}
	}
	
	public void down_pic() throws IOException{
		FTPClient ftp = new FTPClient();
		ftp.connect(SERVER_HOST);
		boolean isLogined = ftp.login(USER_NAME, PASSWORD);
		if(isLogined) {
			//downpic=>pic
			//downpic新增
			ftp.changeWorkingDirectory("/ORD/pic");
			FTPFile[] files = ftp.listFiles();
			File picDir = new File(getCacheDir() + "/pic");
			if(!picDir.exists()) {
				picDir.mkdirs();
			}
			for(FTPFile file : files) {
				File pic = new File(picDir + "/" + file.getName());
				Log.e("====", " file name: " + file.getName());
				InputStream is = ftp.retrieveFileStream(file.getName());
				OutputStream os = new FileOutputStream(pic);
				
				if(is == null) {
					Log.e("===", "========= is null ==========");
				}
				int currSize = 0;
				int totalSize = is.available();
				mUpdatingDialog.setMax(totalSize);
				
				byte[] buff = new byte[1024];
				int len;
				while((len = is.read(buff)) != -1) {
					os.write(buff, 0, len);
					currSize += len;
					Message msg = mHandler.obtainMessage();
					currSize += len;
					DownloadFileInfo fileInfo = new DownloadFileInfo(pic.getName(), "", totalSize, currSize);
					msg.obj = fileInfo;
					msg.what = MSG_UPDATE_DOWNLOAD_FILE_PROGRESS;
					msg.sendToTarget();
				}
				os.close();
				is.close();
				ftp.completePendingCommand();
			}
		}
	}
	
	private class InsertSawareCodeThread extends Thread {
		@Override
		public void run() {
			File sawarcodeDataFile = new File(getCacheDir() + "/info/sawarecode.txt");
			int currentProgressValue = 0;
			try {
				mUpdatingDataDialog.setMax(getLinesForFile(sawarcodeDataFile));
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sawarcodeDataFile), "UTF-16LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaWareCode sawarecode = new SaWareCode();
					sawarecode.setWarecode(TextUtils.isEmpty(arr[0]) ? "" : arr[0]);
					sawarecode.setTrademarkcode(TextUtils.isEmpty(arr[1]) ? "" : arr[1]);
					sawarecode.setWaretypeid(TextUtils.isEmpty(arr[2]) ? "" : arr[2]);
					sawarecode.setId(TextUtils.isEmpty(arr[3]) ? "" : arr[3]);
					sawarecode.setSpecification(TextUtils.isEmpty(arr[4]) ? "" : arr[4]);
					sawarecode.setWarename(TextUtils.isEmpty(arr[5]) ? "" : arr[5]);
					sawarecode.setAdutunit(TextUtils.isEmpty(arr[6]) ? "" : arr[6]);
					sawarecode.setRetailprice(TextUtils.isEmpty(arr[7]) ? 0 : Double.parseDouble(arr[7]));
					sawarecode.setDate1(TextUtils.isEmpty(arr[8]) ? 0 : Long.parseLong(arr[8]));
					sawarecode.setType2(TextUtils.isEmpty(arr[9]) ? "" : arr[9]);
					sawarecode.setState(TextUtils.isEmpty(arr[10]) ? "" : arr[10]);
					sawarecode.setPy("");
					sawarecode.setFlag(TextUtils.isEmpty(arr[11]) ? "" : arr[11]);
					sawarecode.setSxz(TextUtils.isEmpty(arr[12]) ? "" : arr[12]);
					sawarecode.setPagenum(TextUtils.isEmpty(arr[13]) ? "" : arr[13]);
					sawarecode.setSpecdef(TextUtils.isEmpty(arr[14]) ? "" : arr[14]);
					sawarecode.setSex(TextUtils.isEmpty(arr[15]) ? "" : arr[15]);
					sawarecode.setStyle(TextUtils.isEmpty(arr[16]) ? "" : arr[16]);
					sawarecode.setTrait(TextUtils.isEmpty(arr[17]) ? "" : arr[17]);
					sawarecode.setPricecomment(TextUtils.isEmpty(arr[18]) ? "" : arr[18]);
					sawarecode.setPlandate(TextUtils.isEmpty(arr[19]) ? "" : arr[19]);
					sawarecode.setWaregoto(TextUtils.isEmpty(arr[20]) ? "" : arr[20]);
					sawarecode.setProdarea(TextUtils.isEmpty(arr[21]) ? "" : arr[21]);
					sawarecode.setPatten(TextUtils.isEmpty(arr[22]) ? "" : arr[22]);
					sawarecode.setSizeorder(TextUtils.isEmpty(arr[23]) ? "" : arr[23]);
					sawarecode.setRemark(TextUtils.isEmpty(arr[24]) ? "" : arr[24]);
					sawarecode.setDate3(TextUtils.isEmpty(arr[25]) ? 0 : Long.parseLong(arr[25]));
					sawarecode.setDate4(TextUtils.isEmpty(arr[26]) ? 0 : Long.parseLong(arr[26]));
					sawarecode.setProcuredate(TextUtils.isEmpty(arr[27]) ? 0 : Long.parseLong(arr[27]));
					sawarecode.setStylespec(TextUtils.isEmpty(arr[28]) ? "" : arr[28]);
					sawarecode.setFactorycode(TextUtils.isEmpty(arr[29]) ? "" : arr[29]);
					sawarecode.setDirection(TextUtils.isEmpty(arr[30]) ? "" : arr[30]);
					sawarecode.setCliencode(TextUtils.isEmpty(arr[31]) ? "" : arr[31]);
					sawarecode.setStdname(TextUtils.isEmpty(arr[32]) ? "" : arr[32]);
					sawarecode.setCtype(TextUtils.isEmpty(arr[33]) ? "" : arr[33]);
					sawarecode.setWaredegree(TextUtils.isEmpty(arr[34]) ? "" : arr[34]);
					sawarecode.setSaleprice(TextUtils.isEmpty(arr[35]) ? 0 : Double.parseDouble(arr[35]));
					sawarecode.setAvgpurhprice(TextUtils.isEmpty(arr[36]) ? 0 : Double.parseDouble(arr[36]));
					
					ContentValues values =  sawarecode.toContentValues();
					getContentResolver().insert(SaWareCode.CONTENT_URI, sawarecode.toContentValues());
					
					DialogMessage dm = new DialogMessage(++currentProgressValue, "sawarecode, warecode: " + arr[0]);
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();
				}
				dismissDialog(ID_UPDATING_DATA_DIALOG);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
	
	private class InsertSaPara extends Thread {
		@Override
		public void run() {
			File saParaFile = new File(getCacheDir() + "/info/sapara.txt");
			int currentProgressValue = 0;
			try {
				mUpdatingDataDialog.setMax(getLinesForFile(saParaFile));
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(saParaFile), "UTF-16LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaPara para =new SaPara();
					para.paraType = arr[0];
					para.para = arr[1];
					para.paraConnent = arr[2];
					para.connent = arr[3];
					
					ContentValues values = para.toContentValues();
					Uri u = getContentResolver().insert(SaPara.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currentProgressValue, "stpara, id: " + u.getPathSegments().get(1));
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();					
				}
				mUpdatingDataDialog.dismiss();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			super.run();
		}
	}
	
	private static final class DialogMessage {
		int value;
		String text;
		public DialogMessage(int value, String text) {
			this.value = value;
			this.text = text;
		}
	}
	
	private static final class DownloadFileInfo {
		String fileName;
		String path;
		int totalSize;
		int currSize;
		public DownloadFileInfo(String fileName, String path, int totalSize, int currSize) {
			this.fileName = fileName;
			this.path = path;
			this.totalSize = totalSize;
			this.currSize = currSize;
		}
	}
	
	private class InsertData extends Thread {
		private DialogMessage dm;
		private File localFile;
		
		int totalLines = 0;
		int currLine = 0;
		
		private void addSaWareCode() throws Exception{
			localFile = new File(getCacheDir() + "/info/sawarecode.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(SaWareCode.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaWareCode sawarecode = new SaWareCode();
					sawarecode.setWarecode(TextUtils.isEmpty(arr[0]) ? "" : arr[0]);
					sawarecode.setTrademarkcode(TextUtils.isEmpty(arr[1]) ? "" : arr[1]);
					sawarecode.setWaretypeid(TextUtils.isEmpty(arr[2]) ? "" : arr[2]);
					sawarecode.setId(TextUtils.isEmpty(arr[3]) ? "" : arr[3]);
					sawarecode.setSpecification(TextUtils.isEmpty(arr[4]) ? "" : arr[4]);
					sawarecode.setWarename(TextUtils.isEmpty(arr[5]) ? "" : arr[5]);
					sawarecode.setAdutunit(TextUtils.isEmpty(arr[6]) ? "" : arr[6]);
					sawarecode.setRetailprice(TextUtils.isEmpty(arr[7]) ? 0 : Double.parseDouble(arr[7]));
					sawarecode.setDate1(TextUtils.isEmpty(arr[8]) ? 0 : Long.parseLong(arr[8]));
					sawarecode.setType2(TextUtils.isEmpty(arr[9]) ? "" : arr[9]);
					sawarecode.setState(TextUtils.isEmpty(arr[10]) ? "" : arr[10]);
					sawarecode.setPy("");
					sawarecode.setFlag(TextUtils.isEmpty(arr[11]) ? "" : arr[11]);
					sawarecode.setSxz(TextUtils.isEmpty(arr[12]) ? "" : arr[12]);
					sawarecode.setPagenum(TextUtils.isEmpty(arr[13]) ? "" : arr[13]);
					sawarecode.setSpecdef(TextUtils.isEmpty(arr[14]) ? "" : arr[14]);
					sawarecode.setSex(TextUtils.isEmpty(arr[15]) ? "" : arr[15]);
					sawarecode.setStyle(TextUtils.isEmpty(arr[16]) ? "" : arr[16]);
					sawarecode.setTrait(TextUtils.isEmpty(arr[17]) ? "" : arr[17]);
					sawarecode.setPricecomment(TextUtils.isEmpty(arr[18]) ? "" : arr[18]);
					sawarecode.setPlandate(TextUtils.isEmpty(arr[19]) ? "" : arr[19]);
					sawarecode.setWaregoto(TextUtils.isEmpty(arr[20]) ? "" : arr[20]);
					sawarecode.setProdarea(TextUtils.isEmpty(arr[21]) ? "" : arr[21]);
					sawarecode.setPatten(TextUtils.isEmpty(arr[22]) ? "" : arr[22]);
					sawarecode.setSizeorder(TextUtils.isEmpty(arr[23]) ? "" : arr[23]);
					sawarecode.setRemark(TextUtils.isEmpty(arr[24]) ? "" : arr[24]);
					sawarecode.setDate3(TextUtils.isEmpty(arr[25]) ? 0 : Long.parseLong(arr[25]));
					sawarecode.setDate4(TextUtils.isEmpty(arr[26]) ? 0 : Long.parseLong(arr[26]));
					sawarecode.setProcuredate(TextUtils.isEmpty(arr[27]) ? 0 : Long.parseLong(arr[27]));
					sawarecode.setStylespec(TextUtils.isEmpty(arr[28]) ? "" : arr[28]);
					sawarecode.setFactorycode(TextUtils.isEmpty(arr[29]) ? "" : arr[29]);
					sawarecode.setDirection(TextUtils.isEmpty(arr[30]) ? "" : arr[30]);
					sawarecode.setCliencode(TextUtils.isEmpty(arr[31]) ? "" : arr[31]);
					sawarecode.setStdname(TextUtils.isEmpty(arr[32]) ? "" : arr[32]);
					sawarecode.setCtype(TextUtils.isEmpty(arr[33]) ? "" : arr[33]);
					sawarecode.setWaredegree(TextUtils.isEmpty(arr[34]) ? "" : arr[34]);
					sawarecode.setSaleprice(TextUtils.isEmpty(arr[35]) ? 0 : Double.parseDouble(arr[35]));
//					sawarecode.setAvgpurhprice(TextUtils.isEmpty(arr[36]) ? 0 : Double.parseDouble(arr[36]));
					
					ContentValues values =  sawarecode.toContentValues();
					getContentResolver().insert(SaWareCode.CONTENT_URI, sawarecode.toContentValues());
					
					DialogMessage dm = new DialogMessage(++currLine, "sawarecode, warecode: " + arr[0]);
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();					
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addStPara() throws Exception{
			localFile = new File(getCacheDir() + "/info/stpara.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(SaPara.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaPara para =new SaPara();
					if(arr.length==0) {
						continue;					
					} else if(arr.length == 1) {
						para.paraType = arr[0];
						para.para = "";
						para.paraConnent = "";
						para.connent = "";
					} else if(arr.length == 2) {
						para.paraType = arr[0];
						para.para = arr[1];
						para.paraConnent = "";
						para.connent = "";
					} else if(arr.length == 3) {
						para.paraType = arr[0];
						para.para = arr[1];
						para.paraConnent = arr[2];
						para.connent = "";
					} else {
						para.paraType = arr[0];
						para.para = arr[1];
						para.paraConnent = arr[2];
						para.connent = arr[3];
					}
					
					ContentValues values = para.toContentValues();
					Uri u = getContentResolver().insert(SaPara.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "stpara, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();						
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addSaColorCode() throws Exception{
			localFile = new File(getCacheDir() + "/info/sacolorcode.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(SaColorCode.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaColorCode sacolorcode = new SaColorCode();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = "";
						sacolorcode.py = "";
						sacolorcode.code = "";
						sacolorcode.remark = "";
						sacolorcode.pb = "";
						sacolorcode.trade = "";
					} else if(arr.length == 2) {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = arr[1];
						sacolorcode.py = "";
						sacolorcode.code = "";
						sacolorcode.remark = "";
						sacolorcode.pb = "";
						sacolorcode.trade = "";
					} else if(arr.length == 3) {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = arr[1];
						sacolorcode.py = arr[2];
						sacolorcode.code = "";
						sacolorcode.remark = "";
						sacolorcode.pb = "";
						sacolorcode.trade = "";						
					} else if(arr.length == 4) {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = arr[1];
						sacolorcode.py = arr[2];
						sacolorcode.code = arr[3];
						sacolorcode.remark = "";
						sacolorcode.pb = "";
						sacolorcode.trade = "";
					} else if(arr.length == 5) {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = arr[1];
						sacolorcode.py = arr[2];
						sacolorcode.code = arr[3];
						sacolorcode.remark = arr[4];
						sacolorcode.pb = "";
						sacolorcode.trade = "";
					} else if(arr.length == 6) {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = arr[1];
						sacolorcode.py = arr[2];
						sacolorcode.code = arr[3];
						sacolorcode.remark = arr[4];
						sacolorcode.pb = arr[5];
						sacolorcode.trade = "";						
					} else {
						sacolorcode.colorCode = arr[0];
						sacolorcode.colorName = arr[1];
						sacolorcode.py = arr[2];
						sacolorcode.code = arr[3];
						sacolorcode.remark = arr[4];
						sacolorcode.pb = arr[5];
						sacolorcode.trade = arr[6];
					}
					ContentValues values = sacolorcode.toContentValues();
					Uri u = getContentResolver().insert(SaColorCode.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "sacolorcode, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();		
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addShowSize() throws Exception{
			localFile = new File(getCacheDir() + "/info/showsize.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(ShowSize.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					ShowSize showsize = new ShowSize();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						showsize.size = arr[0];
						showsize.type = "";
						showsize.show = "";
						showsize.title = "";
						showsize.printTitle = "";
						showsize.sizeGroup = "";
						showsize.scale = "";
					} else if(arr.length == 2) {
						showsize.size = arr[0];
						showsize.type = arr[1];
						showsize.show = "";
						showsize.title = "";
						showsize.printTitle = "";
						showsize.sizeGroup = "";
						showsize.scale = "";
					} else if(arr.length == 3) {
						showsize.size = arr[0];
						showsize.type = arr[1];
						showsize.show = arr[2];
						showsize.title = "";
						showsize.printTitle = "";
						showsize.sizeGroup = "";
						showsize.scale = "";
					} else if(arr.length == 4) {
						showsize.size = arr[0];
						showsize.type = arr[1];
						showsize.show = arr[2];
						showsize.title = arr[3];
						showsize.printTitle = "";
						showsize.sizeGroup = "";
						showsize.scale = "";
					} else if(arr.length == 5) {
						showsize.size = arr[0];
						showsize.type = arr[1];
						showsize.show = arr[2];
						showsize.title = arr[3];
						showsize.printTitle = arr[4];
						showsize.sizeGroup = "";
						showsize.scale = "";
					} else if(arr.length == 6) {
						showsize.size = arr[0];
						showsize.type = arr[1];
						showsize.show = arr[2];
						showsize.title = arr[3];
						showsize.printTitle = arr[4];
						showsize.sizeGroup = arr[5];
						showsize.scale = "";
					} else {
						showsize.size = arr[0];
						showsize.type = arr[1];
						showsize.show = arr[2];
						showsize.title = arr[3];
						showsize.printTitle = arr[4];
						showsize.sizeGroup = arr[5];
						showsize.scale = arr[6];
					}
					ContentValues values = showsize.toContentValues();
					Uri u = getContentResolver().insert(ShowSize.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "showsize, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();	
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addSawareColor() throws Exception{
			localFile = new File(getCacheDir() + "/info/saware_color.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(SaWareColor.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaWareColor warecolor = new SaWareColor();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						warecolor.wareCode = arr[0];
						warecolor.colorCode = "";
						warecolor.colorComment = "";						
					} else if(arr.length == 2) {
						warecolor.wareCode = arr[0];
						warecolor.colorCode = arr[1];
						warecolor.colorComment = "";
					} else {
						warecolor.wareCode = arr[0];
						warecolor.colorCode = arr[1];
						warecolor.colorComment = arr[2];
					}
					ContentValues values = warecolor.toContentValues();
					Uri u = getContentResolver().insert(SaWareColor.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "saware_color, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();	
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addSaWareSize() throws Exception{
			localFile = new File(getCacheDir() + "/info/saware_size.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(SaWareSize.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaWareSize waresize = new SaWareSize();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						waresize.wareCode = arr[0];
						waresize.size = "";
						waresize.showSort = "";
						waresize.stand = "";
					} else if(arr.length == 2) {
						waresize.wareCode = arr[0];
						waresize.size = arr[1];
						waresize.showSort = "";
						waresize.stand = "";
					} else if(arr.length == 3) {
						waresize.wareCode = arr[0];
						waresize.size = arr[1];
						waresize.showSort = arr[2];
						waresize.stand = "";
					} else {
						waresize.wareCode = arr[0];
						waresize.size = arr[1];
						waresize.showSort = arr[2];
						waresize.stand = arr[3];
					}
					ContentValues values = waresize.toContentValues();
					Uri u = getContentResolver().insert(SaWareSize.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "saware_size, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();						
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addType1() throws Exception{
			localFile = new File(getCacheDir() + "/info/type1.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(AsContent.Type1.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					AsContent.Type1 type = new AsContent.Type1();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						type.iid = arr[0];
						type.type1 = "";
						type.wareTypeId = "";
						type.standard = "";
						type.remark = "";
					} else if(arr.length == 2) {
						type.iid = arr[0];
						type.type1 = arr[1];
						type.wareTypeId = "";
						type.standard = "";
						type.remark = "";
					} else if(arr.length == 3) {
						type.iid = arr[0];
						type.type1 = arr[1];
						type.wareTypeId = arr[2];
						type.standard = "";
						type.remark = "";
					} else if(arr.length == 4) {
						type.iid = arr[0];
						type.type1 = arr[1];
						type.wareTypeId = arr[2];
						type.standard = arr[3];
						type.remark = "";
					} else if(arr.length == 5) {
						type.iid = arr[0];
						type.type1 = arr[1];
						type.wareTypeId = arr[2];
						type.standard = arr[3];
						type.remark = arr[4];
					}
					ContentValues values = type.toContentValues();
					Uri u = getContentResolver().insert(AsContent.Type1.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "type1, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();	
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addSaWareType() throws Exception{
			localFile = new File(getCacheDir() + "/info/sawaretype.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(AsContent.SaWareType.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaWareType waretype = new SaWareType();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						waretype.wareTypeId = arr[0];
						waretype.wareTypeName = "";
						waretype.taxRate = "";
						waretype.standard = "";
						waretype.flag = "";
						waretype.sizeFlag = "";
					} else if(arr.length == 2) {
						waretype.wareTypeId = arr[0];
						waretype.wareTypeName = arr[1];
						waretype.taxRate = "";
						waretype.standard = "";
						waretype.flag = "";
						waretype.sizeFlag = "";
					} else if(arr.length == 3) {
						waretype.wareTypeId = arr[0];
						waretype.wareTypeName = arr[1];
						waretype.taxRate = arr[2];
						waretype.standard = "";
						waretype.flag = "";
						waretype.sizeFlag = "";
					} else if(arr.length == 4) {
						waretype.wareTypeId = arr[0];
						waretype.wareTypeName = arr[1];
						waretype.taxRate = arr[2];
						waretype.standard = arr[3];
						waretype.flag = "";
						waretype.sizeFlag = "";
					} else if(arr.length == 5) {
						waretype.wareTypeId = arr[0];
						waretype.wareTypeName = arr[1];
						waretype.taxRate = arr[2];
						waretype.standard = arr[3];
						waretype.flag = arr[4];
						waretype.sizeFlag = "";
					} else {
						waretype.wareTypeId = arr[0];
						waretype.wareTypeName = arr[1];
						waretype.taxRate = arr[2];
						waretype.standard = arr[3];
						waretype.flag = arr[4];
						waretype.sizeFlag = arr[5];
					}
					ContentValues values = waretype.toContentValues();
					Uri u = getContentResolver().insert(AsContent.SaWareType.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "sawaretype, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();	
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		private void addSaWareGroup() throws Exception{
			localFile = new File(getCacheDir() + "/info/sawaregroup.txt");
			totalLines = getLinesForFile(localFile);
			currLine = 0;
			mUpdatingDataDialog.setMax(totalLines);
			getContentResolver().delete(AsContent.SaWareGroup.CONTENT_URI, null, null);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF16-LE"));
				String line;
				while((line = br.readLine()) != null) {
					String[] arr = line.split("\t");
					SaWareGroup group = new SaWareGroup();
					if(arr.length == 0) {
						continue;
					} else if(arr.length == 1) {
						group.itemCode = arr[0];
						group.groupName = "";
						group.wareCode = "";
						group.colorCode = "";
						group.remark = "";
					} else if(arr.length == 2) {
						group.itemCode = arr[0];
						group.groupName = arr[1];
						group.wareCode = "";
						group.colorCode = "";
						group.remark = "";
					} else if(arr.length == 3) {
						group.itemCode = arr[0];
						group.groupName = arr[1];
						group.wareCode = arr[2];
						group.colorCode = "";
						group.remark = "";
					} else if(arr.length == 4) {
						group.itemCode = arr[0];
						group.groupName = arr[1];
						group.wareCode = arr[2];
						group.colorCode = arr[3];
						group.remark = "";
					} else {
						group.itemCode = arr[0];
						group.groupName = arr[1];
						group.wareCode = arr[2];
						group.colorCode = arr[3];
						group.remark = arr[4];
					}
					ContentValues values = group.toContentValues();
					Uri u = getContentResolver().insert(AsContent.SaWareGroup.CONTENT_URI, values);
					
					DialogMessage dm = new DialogMessage(++currLine, "sawaregroup, id: " + u.getPathSegments().get(1));
					
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_INSERT_PROGRESS_DIALG;
					msg.obj = dm;
					msg.sendToTarget();	
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			} 
		}
		
		@Override
		public void run() {
			Looper.prepare();
			showDialog(ID_UPDATING_DATA_DIALOG);
			try {
				addSaWareCode();
				addStPara();
				addSaColorCode();
				addShowSize();
				addSawareColor();
				addSaWareSize();
				addType1();
				addSaWareType();
				addSaWareGroup();
				dismissDialog(ID_UPDATING_DATA_DIALOG);
				SharedPreferences spe = getSharedPreferences("data_downloaded", 0); 
				SharedPreferences.Editor editor = spe.edit();
				editor.putBoolean("data_downloaded", true);
				editor.commit();
			} catch (Exception e) {
				e.printStackTrace();
				dismissDialog(ID_UPDATING_DATA_DIALOG);
				SharedPreferences spe = getSharedPreferences("data_downloaded", 0); 
				SharedPreferences.Editor editor = spe.edit();
				editor.putBoolean("data_downloaded", false);
				editor.commit();
			}

		}
	}
	
	private int getLinesForFile(File f) {
		int lines = 0;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF16-LE"));
			String line;
			while((line = br.readLine()) != null) {
				lines ++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
//			SharedPreferences spe = getSharedPreferences("data_downloaded", 0);
//			SharedPreferences.Editor editor = spe.edit();
//			editor.putBoolean("data_downloaded", false);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
//			SharedPreferences spe = getSharedPreferences("data_downloaded", 0);
//			SharedPreferences.Editor editor = spe.edit();
//			editor.putBoolean("data_downloaded", false);			
		} catch (IOException e) {
			e.printStackTrace();
//			SharedPreferences spe = getSharedPreferences("data_downloaded", 0);
//			SharedPreferences.Editor editor = spe.edit();
//			editor.putBoolean("data_downloaded", false);			
		}
		return lines;
	}
}
