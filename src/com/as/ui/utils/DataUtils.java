package com.as.ui.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.as.db.provider.AsContent.SaWareCode;
import com.as.order.ui.AsProgressDialog;

public class DataUtils {
	
	private static final String TAG = "DataUtils";

	private static final String INFO_SAWARECODE = "/info/sawarecode.txt";
	
	public static void initSaWareCode(Context context, AsProgressDialog progressDialog) {
		File sawarcodeDataFile = new File(context.getCacheDir() + INFO_SAWARECODE);
		progressDialog.setMax(getLinesForFile(sawarcodeDataFile));
		int currentProgressValue = 0;
		try {
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
				context.getContentResolver().insert(SaWareCode.CONTENT_URI, sawarecode.toContentValues());
				
				progressDialog.updateProgress(++currentProgressValue);
				progressDialog.updatePorgressText("¸üÐÂsawarecode, sawarecode:" + arr[0]);
			}
			progressDialog.dismiss();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static int getLinesForFile(File f) {
		int lines = 0;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF16-LE"));
			String line;
			while((line = br.readLine()) != null) {
				lines ++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
}
