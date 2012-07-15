package com.as.order.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.as.order.R;
import com.as.ui.utils.Constant;
import com.as.ui.utils.UserUtils;

public class SummrizeAnalysisActivity extends AbstractActivity {

	private LinearLayout mLayout;
	ProgressDialog pd;
	Handler handler;
	WebView wv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.summarize_ana, null);
		mRootView.addView(mLayout, FF);
		
		titleHomeBtn.setVisibility(Button.VISIBLE);
		
		wv = (WebView) findViewById(R.id.sum_analysis);
		
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitle("在线分析");
		
//		wv = (WebView) findViewById(R.id.sum_ana);
//		wv.getSettings().setJavaScriptEnabled(true);
//		wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//		wv.setScrollBarStyle(0);
//		wv.setWebViewClient(new WebViewClient(){
//			@Override
//			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				return super.shouldOverrideUrlLoading(view, url);
//			}
//			
//		});
		initData();
//		loadUrl(wv, reportUrl);
//        handler=new Handler(){
//        	public void handleMessage(Message msg)
//    	    {
//    	      if (!Thread.currentThread().isInterrupted())
//    	      {
//    	        switch (msg.what)
//    	        {
//    	        case 0:
//    	        	pd.show();
//    	        	break;
//    	        case 1:
//    	        	pd.hide();
//    	        	break;
//    	        }
//    	      }
//    	      super.handleMessage(msg);
//    	    }
//        };
	}
	
	private void initData() {
//    	wv=(WebView)findViewById(R.id.summrize_analysis);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setScrollBarStyle(0);
		SharedPreferences spp = PreferenceManager.getDefaultSharedPreferences(SummrizeAnalysisActivity.this);
		String reportUrl = spp.getString(Constant.SP_REPORT_URL, "");
		Log.e("在线分析", "report: " + reportUrl);
        wv.loadUrl(reportUrl+"?deptcode=" + UserUtils.getUserAccount(this));
//        wv.setWebViewClient(new WebViewClient(){   
//            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
//            	loadUrl(view,url);
//                return true;   
//            }
// 
//        });
//        wv.setWebChromeClient(new WebChromeClient(){
//        	public void onProgressChanged(WebView view,int progress){
//             	if(progress==100){
//            		handler.sendEmptyMessage(1);
//            	}   
//                super.onProgressChanged(view, progress);   
//            }   
//        });
// 
//    	pd=new ProgressDialog(SummrizeAnalysisActivity.this);
//        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pd.setMessage("数据载入中，请稍候！");
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
			default:
				break;
		}
	}
	
	private void loadUrl(final WebView view, final String url) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				view.loadUrl(url);
			}
		};
	}

}
