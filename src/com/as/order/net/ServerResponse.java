package com.as.order.net;

import org.json.JSONObject;

public class ServerResponse {

	public int code;
	public String message;
	public JSONObject result;
	
	public ServerResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public ServerResponse(int code, String message, JSONObject result) {
		this.code = code;
		this.message = message;
		this.result = result;
	}
	
	public ServerResponse(int code, JSONObject result) {
		this.code = code;
		this.result = result;
	}	
}
