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

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;

public class HttpDataFetcher {

	private static HttpDataFetcher mFetcher = new HttpDataFetcher();
	private static final String TAG = "Http Data Fetcher";
	private static final String JSON_DATA = "jsonData";
	
	private static final int mTimeoutConnection = 20000;
	private static final int mTimeoutSocket = 30000;

	public static final String STATUS_KEY = "RESULT_STATUS";
	private static final Integer SUCCESS = Integer.valueOf(1);
	private static final Integer FAIL = Integer.valueOf(0);
	
	private static JSONObject FAIL_JSON = new JSONObject();
	
	private HttpDataFetcher() {
		
	}
	
	public static HttpDataFetcher getInstance() {
		return mFetcher;
	}
	
	public final JSONObject postData(String subUrl, JSONObject parameters) throws Exception {
		DefaultHttpClient mHttpClient = null;
		try {
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			formParams.add(new BasicNameValuePair(JSON_DATA, parameters.toString()));
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
				throw new Exception("********< error >********: server return code:" + httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("error"); 
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
			throw new Exception("error");
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
