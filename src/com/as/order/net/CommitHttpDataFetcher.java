package com.as.order.net;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.as.ui.utils.AlertUtils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;

public class CommitHttpDataFetcher {

	private static CommitHttpDataFetcher mFetcher = new CommitHttpDataFetcher();
	private static final String TAG = "Http Data Fetcher";
	private static final String JSON_DATA = "jsonData";
	private static final String ORDER_DATA = "OrderData";
	private static final String DEPT_CODE = "DeptCode";
	
	private static final int mTimeoutConnection = 60000;
	private static final int mTimeoutSocket = 120000;

	public static final String STATUS_KEY = "RESULT_STATUS";
	public static final Integer SUCCESS = Integer.valueOf(1);
	public static final Integer FAIL = Integer.valueOf(0);
	
	private static JSONObject FAIL_JSON = new JSONObject();
	
	private Context mContext;
	
	private CommitHttpDataFetcher() {
	}
	
	public static CommitHttpDataFetcher getInstance() {
		return mFetcher;
	}
	
	public final JSONObject postData(String subUrl, String parameters) throws Exception {
		DefaultHttpClient mHttpClient = null;
		try {
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			formParams.add(new BasicNameValuePair(DEPT_CODE, parameters));
			HttpEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");
			Log.i(TAG, subUrl);
			HttpResponse httpResponse = null;
			HttpPost request = new HttpPost(subUrl);
			request.setEntity(entity);
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, mTimeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, mTimeoutSocket);
			mHttpClient = new DefaultHttpClient(httpParameters);
			httpResponse = mHttpClient.execute(request);
			
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				return genSuccessResult(new JSONObject(retSrc));
			} else {
//				throw new Exception("********< error >********: server return code:" + httpResponse.getStatusLine().getStatusCode());
				Log.e(TAG, "********< error >********: server return code:" + httpResponse.getStatusLine().getStatusCode());
				return genFailResult("网络请求出错, 状态码: " + httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage()); 
		} finally {
			
		}
	}
	
	public final JSONObject getData(String url, JSONObject params)  throws Exception {
		DefaultHttpClient mHttpClient = null;
//		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
//		Iterator keyIterator = params.keys();
//		while(keyIterator.hasNext()) {
//			String key = (String)keyIterator.next();
//			NameValuePair pair = new BasicNameValuePair(key, params.getString(key));
//			formParams.add(pair);
//		}
//		HttpEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");
		try {
			HttpResponse httpResponse = null;
			HttpGet request = new HttpGet(url);
			
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, mTimeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParams, mTimeoutSocket);
			mHttpClient = new DefaultHttpClient(httpParams);
			httpResponse = mHttpClient.execute(request);
			
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				Log.e(TAG, retSrc);
				if(!TextUtils.isEmpty(retSrc)) {
					JSONObject resObj = new JSONObject(retSrc);
					String resSts = resObj.getString("sts");
					if("Y".equals(resSts)) {
//						JSONArray array = resObj.getJSONArray("res");
						JSONArray dataArray = resObj.getJSONArray("res");
						return genSuccessResult(dataArray.getJSONObject(0));
					} 
					if("N".equals(resSts)) {
						return genFailResult(resObj.getString("res"));
					}
				}
			} else {
				throw new Exception("********< error >********: server return code:" + httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
//			throw new Exception("error");
//			AlertUtils.toastMsg(, msg)
			return genFailResult("网络出错");
		}
		return genFailResult("没有返回结果");
	}
	
	private static JSONObject genSuccessResult(JSONObject result) {
		try {
			return result.put(STATUS_KEY, SUCCESS);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static JSONObject genFailResult(String message) {
		try {
			FAIL_JSON.put(STATUS_KEY, FAIL);
			FAIL_JSON.put("msg", message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return FAIL_JSON;
	}
}
