package com.as.order.net;

public interface ResultListener {

	void onPostDataStart();
	void onPostDataSuccess(ServerResponse response);
	void onPostDataError(ServerResponse response);
	void onPostDataComplete(ServerResponse response);
	void onProgressMessage(String message);
}
