package com.as.order.net;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class JsonAsyncTask extends AsyncTask<JSONObject, String, ServerResponse> {

	private ResultListener mListener;
	private static final int SUCCESS = 1;
	private static final int FAIL = 0;
	
	private static final ServerResponse FAIL_RESPONSE = new ServerResponse(FAIL, "获取数据错误");
	public String mUrl = "";
	
	public JsonAsyncTask(String url, ResultListener listener) {
		this.mListener = listener;
		this.mUrl = url;
	}
	
	@Override
	protected ServerResponse doInBackground(JSONObject... params) {
		mListener.onPostDataStart();

		try {
			JSONObject data = HttpDataFetcher.getInstance().getData(mUrl, params[0]);
			int sts = data.getInt("RESULT_STATUS");
			if(sts == 1) {
				return new ServerResponse(SUCCESS, data);
			} 
			
			if(sts == 0) {
				return new ServerResponse(FAIL, data);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
	
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
	}
}
