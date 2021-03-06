package com.as.order.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.as.order.R;

public class DinghuoZongheFenxiActivity extends AbstractActivity {

	private LinearLayout mLayout;
	
	private Button dalei;
	private Button xiaolei;
	private Button zhuti;
	private Button boduan;
	private Button yanse;
	private Button chima;
	private Button jiagedai;
	private Button shangxiazhuang;
	
	private Button daleiWdBtn;
	private Button xiaoleiWdBtn;
	private Button sxzWdBtn;
	private Button zhutiWdBtn;
	private Button boduanWdBtn;
	
	private int[] ids = new int[]{
			R.id.dalei,
			R.id.xiaolei,
			R.id.zhuti,
			R.id.boduan,
			R.id.yanse,
			R.id.chima,
			R.id.jiagedai,
			R.id.shangxiazhuang
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.order_zonghe_analysis, null);
		mRootView.addView(mLayout, FF);
		
		for(int i=0; i<ids.length; i++) {
			Button btn = (Button) findViewById(ids[i]);
			btn.setOnClickListener(this);
		}
		
		daleiWdBtn = (Button) findViewById(R.id.dalei_wd);
		xiaoleiWdBtn = (Button) findViewById(R.id.xiaolei_wd);
		sxzWdBtn = (Button) findViewById(R.id.sxz_wd);
		zhutiWdBtn = (Button) findViewById(R.id.zhuti_wd);
		boduanWdBtn = (Button) findViewById(R.id.boduan_wd);
		
		daleiWdBtn.setOnClickListener(this);
		xiaoleiWdBtn.setOnClickListener(this);
		sxzWdBtn.setOnClickListener(this);
		zhutiWdBtn.setOnClickListener(this);
		boduanWdBtn.setOnClickListener(this);

		setTextForTitle("�����ۺϷ���");
		setTextForLeftTitleBtn("����");
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.dalei:
			Intent daleiIntent = new Intent(DinghuoZongheFenxiActivity.this, DaLeiZongheAnalysisActivity.class);
			startActivity(daleiIntent);
			break;
			
		case R.id.xiaolei:
			Intent xiaoleiIntent = new Intent(DinghuoZongheFenxiActivity.this, XiaoleiZongheAnalysisActivity.class);
			startActivity(xiaoleiIntent);
			break;
			
		case R.id.zhuti:
			Intent zhutiIntent = new Intent(DinghuoZongheFenxiActivity.this, ZhutiZongheAnalysisActivity.class);
			startActivity(zhutiIntent);
			break;
			
		case R.id.boduan:
			Intent boduanIntent = new Intent(DinghuoZongheFenxiActivity.this, BoduanZongheAnalysisActivity.class);
			startActivity(boduanIntent);
			break;
			
		case R.id.chima:
			Intent chimaIntent = new Intent(DinghuoZongheFenxiActivity.this, ChimaZongheAnalysisActivity.class);
			startActivity(chimaIntent);
			break;
			
		case R.id.jiagedai:
			Intent jiagedaiInent = new Intent(DinghuoZongheFenxiActivity.this, JiagedaiZongheAnalysisActivity.class);
			startActivity(jiagedaiInent);
			break;
			
		case R.id.shangxiazhuang:
			Intent shangxiazhuangIntent = new Intent(DinghuoZongheFenxiActivity.this, ShangxiazuangZongheAnalysisAcitivity.class);
			startActivity(shangxiazhuangIntent);
			break;
			
		case R.id.yanse:
			Intent yanseIntent = new Intent(DinghuoZongheFenxiActivity.this, YanseZongheAnalysisActivity.class);
			startActivity(yanseIntent);
			break;
			
		case R.id.dalei_wd:
			Intent daleiwdIntent = new Intent(DinghuoZongheFenxiActivity.this, DaleiWdFenxi.class);
			startActivity(daleiwdIntent);
			break;
			
		case R.id.xiaolei_wd:
			Intent xiaoleiwdIntent = new Intent(DinghuoZongheFenxiActivity.this, XiaoleiWdFenxi.class);
			startActivity(xiaoleiwdIntent);
			break;
			
		case R.id.boduan_wd:
			Intent boduanWdIntent = new Intent(DinghuoZongheFenxiActivity.this, BoduanWdFenxi.class);
			startActivity(boduanWdIntent);
			break;
			
		case R.id.sxz_wd:
			Intent sxzIntent = new Intent(DinghuoZongheFenxiActivity.this, SxzWdFenxi.class);
			startActivity(sxzIntent);
			break;
			
		case R.id.zhuti_wd:
			Intent zhutiWdIntent = new Intent(DinghuoZongheFenxiActivity.this, ZhutiWdFenxi.class);
			startActivity(zhutiWdIntent);
			break;
			
			default:
				break;
		}
	}

}
