package com.as.order.net;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

public class CommitOrderUpdateDataAsyncTask extends
		AsyncTask<String, String, ServerResponse> {

	private Context mContext;
	private ResultListener mListener;
	private String hostUrl;
	private static final int SUCCESS = 1;
	private static final int FAIL = 0;
	private static final ServerResponse FAIL_RESPONSE = new ServerResponse(FAIL, "获取数据错误");
	
	public CommitOrderUpdateDataAsyncTask(Context context, String hostUrl, ResultListener listener) {
		this.hostUrl = hostUrl;
		this.mContext = context;
		this.mListener = listener;	
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mListener.onPostDataStart();
	}
	
	@Override
	protected ServerResponse doInBackground(String... params) {
		String deptCode = params[0];
		try {
			JSONObject obj = CommitHttpDataFetcher.getInstance().postData(hostUrl, deptCode);
			if(CommitHttpDataFetcher.SUCCESS == obj.getInt(CommitHttpDataFetcher.STATUS_KEY)) {
				return new ServerResponse(SUCCESS, obj.getString("res"));
			} else {
				return new ServerResponse(FAIL, obj.getString("msg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ServerResponse(FAIL, e.getMessage());
		}
		
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

}
