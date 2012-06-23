package com.as.order.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.as.order.R;
import com.as.order.dao.DaleiFenxiDAO;
import com.as.ui.utils.ColorUtils;
import com.as.ui.utils.CommonDataUtils;

public class DaleiPipeChartActivity extends AbstractActivity {

	private static final String TAG = "DaleiPipeChartActivity";
	
	private static final String OPT_ZB = "3001";
	private static final String OPT_YDB = "3002";
	private static final String OPT_DHB = "3003";
	private static final String OPT_JEB = "3004";
	
	private static final int[] allFenxiBtns = new int[]{
		R.id.dalei,
		R.id.xialei,
		R.id.zhuti,
		R.id.boduan,
		R.id.yanse,
//		R.id.chima,
		R.id.chimabtn,
		R.id.jiagedai,
		R.id.sxz
	};
	
	private static final int[] alloptids = new int[]{
		R.id.dalei_detail,
		R.id.xiaolei_detail,
		R.id.zhuti_detail,
		R.id.boduan_detail,
		R.id.yanse_detail,
		R.id.chima_detail,
		R.id.jiagedai_detail,
		R.id.sxz_detail
	};
	
	private static final int[] daleiids = new int[]{
		R.id.dalei_button01,
		R.id.dalei_button02,
		R.id.dalei_button03,
		R.id.dalei_button04
	};
	
	private static final int[] xiaoleiids = new int[]{
		R.id.xiaolei_button01,
		R.id.xiaolei_button02,
		R.id.xiaolei_button03,
		R.id.xiaolei_button04
	};
	
	private static final int[] zhutiids = new int[]{
		R.id.zhuti_button01,
		R.id.zhuti_button02,
		R.id.zhuti_button03,
		R.id.zhuti_button04
	};
	
	private static final int[] boduanids = new int[]{
		R.id.boduan_button01,
		R.id.boduan_button02,
		R.id.boduan_button03,
		R.id.boduan_button04
	};
	
	private static final int[] yanseids = new int[]{
		R.id.yanse_button01,
		R.id.yanse_button02,
		R.id.yanse_button03,
		R.id.yanse_button04,
	};
	
	private static final int[] chimaids = new int[]{
		R.id.chima_button01,
		R.id.chima_button02,
		R.id.chima_button03,
		R.id.chima_button04
	};
	
	private static final int[] jiagedaiids = new int[]{
		R.id.jiagedai_button01,
		R.id.jiagedai_button02,
		R.id.jiagedai_button03,
		R.id.jiagedai_button04
	};
	
	private static final int[] sxzids = new int[]{
		R.id.sxz_button01,
		R.id.sxz_button02,
		R.id.sxz_button03,
		R.id.sxz_button04
	};
	
	private LinearLayout mLayout;
	private List<DaleiFenxiDAO> mDataSet;
	private GraphicalView mChart;
	
	//button01
	private Button zongkuanzhanbi;
	//button02
	private Button dinghuokuanzhanbi;
	//button03
	private Button dingliangzhanbi;
	//button04
	private Button jinezhanbi;
	LinearLayout chartRoot;
	private double[] values;
	
	private LinearLayout daleiLayout;
	private LinearLayout xiaoleiLayout;
	private LinearLayout zhutiLayout;
	private LinearLayout boduanLayout;
	private LinearLayout yanseLayout;
	private LinearLayout chimaLayout;
	private LinearLayout jiagedaiLayout;
	private LinearLayout sxzLayout;
	
	private Button dalei1;
	private Button dalei2;
	private Button dalei3;
	private Button dalei4;
	private Button xiaolei1;
	private Button xiaolei2;
	private Button xiaolei3;
	private Button xiaolei4;
	private Button zhuti1;
	private Button zhuti2;
	private Button zhuti3;
	private Button zhuti4;
	private Button boduan1;
	private Button buduan2;
	private Button boduan3;
	private Button boduan4;
	private Button yanse1;
	private Button yanse2;
	private Button yanse3;
	private Button yanse4;
	private Button chima1;
	private Button chima2;
	private Button chima3;
	private Button chima4;
	private Button jiagedai1;
	private Button jiagedai2;
	private Button jiagedai3;
	private Button jiagedai4;
	private Button sxz1;
	private Button sxz2;
	private Button sxz3;
	private Button sxz4;
	
	public static final String ANA_TYPE = "anaType";
	public static final String OPT_TYPE = "optType";
	public static final String ANA_TITLE = "title";
	public static final String ANA_CHIMAZU = "chimazu";
	
	public static final int ANATYPE_DALEI = 5001;
	public static final int ANATYPE_XIAOLEI = 5002;
	public static final int ANATYPE_ZHUTI = 5003;
	public static final int ANATYPE_BODUAN = 5004;
	public static final int ANATYPE_YANSE = 5005;
	public static final int ANATYPE_CHIMA = 5006;
	public static final int ANATYPE_JIAGEDAI = 5007;
	public static final int ANATYPE_SXZ = 5008;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.dalei_chart, null);
		mRootView.addView(mLayout, FF);
		chartRoot = (LinearLayout) findViewById(R.id.chart_view);
		
		int i;
		for(i=0; i<allFenxiBtns.length; i++) {
			Button btn = (Button) findViewById(allFenxiBtns[i]);
			btn.setOnClickListener(this);
		}
		
		for(i=0; i<daleiids.length; i++) {
			Button btn = (Button) findViewById(daleiids[i]);
			btn.setOnClickListener(this);
		}
		
		for(i=0; i<xiaoleiids.length; i++) {
			Button btn = (Button) findViewById(xiaoleiids[i]);
			btn.setOnClickListener(this);
		}
		
		for(i=0; i<zhutiids.length; i++) {
			Button btn = (Button) findViewById(zhutiids[i]);
			btn.setOnClickListener(this);
		}
		
		for(i=0; i<boduanids.length; i++) {
			Button btn = (Button) findViewById(boduanids[i]);
			btn.setOnClickListener(this);
		}
		
		for(i=0; i<yanseids.length; i++) {
			Button btn = (Button) findViewById(yanseids[i]);
			btn.setOnClickListener(this);
		}
		
		for(i=0; i<chimaids.length; i++) {
			Button btn = (Button) findViewById(chimaids[i]);
			btn.setOnClickListener(this);
		}
		
		for(i=0; i<jiagedaiids.length; i++) {
			Button btn = (Button) findViewById(jiagedaiids[i]);
			btn.setOnClickListener(this);
		}
		
		for(i=0; i<sxzids.length; i++) {
			Button btn = (Button) findViewById(sxzids[i]);
			btn.setOnClickListener(this);
		}
		
//		zongkuanzhanbi = (Button) findViewById(R.id.button01);
//		dinghuokuanzhanbi = (Button) findViewById(R.id.button02);
//		dingliangzhanbi = (Button) findViewById(R.id.button03);
//		jinezhanbi = (Button) findViewById(R.id.button04);
//		zongkuanzhanbi.setOnClickListener(this);
//		dinghuokuanzhanbi.setOnClickListener(this);
//		jinezhanbi.setOnClickListener(this);
//		dingliangzhanbi.setOnClickListener(this);
		
		setTextForLeftTitleBtn("返回");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		int anaType = bundle.getInt(this.ANA_TYPE);
		int optType = bundle.getInt(this.OPT_TYPE);
		String titleString = bundle.getString(this.ANA_TITLE);
		String chimazu = "";
		if(anaType == ANATYPE_CHIMA) {
			chimazu = bundle.getString(this.ANA_CHIMAZU);
		}
		setTextForTitle(titleString);
		
		int i;
		
		if(anaType == ANATYPE_DALEI) {
			displayDaleiOpts();
			Map<String, Double> data = CommonDataUtils.chartDaleiFenxi(this, "", optType);
			chartRoot.removeAllViews();
			chartRoot.addView(createChart(data, titleString));
		} else if(anaType == ANATYPE_XIAOLEI) {
			displayXiaoleiOpts();
			Map<String, Double> data = CommonDataUtils.chartXiaoleiFenxi(this, "", optType);
			chartRoot.removeAllViews();
//			chartRoot.addView(createChart(CommonDataUtils.chartXiaoleiFenxi(this, "", CommonDataUtils.ZKZB), "小类分析-总款占比"));
			chartRoot.addView(createChart(data, titleString));
		} else if(anaType == ANATYPE_ZHUTI) {
			displayZhutiOpts();
			Map<String, Double> data = CommonDataUtils.chartZhutiFenxi(this, "", optType);
			chartRoot.removeAllViews();
//			chartRoot.addView(createChart(CommonDataUtils.chartZhutiFenxi(this, "", CommonDataUtils.ZKZB), "主题分析"));
			chartRoot.addView(createChart(data, titleString));
		} else if(anaType == ANATYPE_BODUAN) {
			displayBoduanOpts();
			Map<String, Double> data = CommonDataUtils.chartBoduanFenxi(this, "", optType);
			chartRoot.removeAllViews();
			chartRoot.addView(createChart(data, titleString));
			
		} else if(anaType == ANATYPE_YANSE) {
			displayYanseOpts();
			Map<String, Double> data = CommonDataUtils.chartYanseFenxi(this, "", optType);
			chartRoot.removeAllViews();
			chartRoot.addView(createChart(data, titleString));
			
		} else if(anaType == ANATYPE_CHIMA) {
			displayChimaOpts();
			Map<String, Double> data = CommonDataUtils.chartChaimaFenxi(this, " sawarecode.flag = '1' ", CommonDataUtils.ZKZB);
			chartRoot.removeAllViews();
			chartRoot.addView(createChart(data, "尺码分析-尺码组-女装"));
			
		} else if(anaType == ANATYPE_JIAGEDAI) {
			displayJiagedaiOpts();
			Map<String, Double> data = CommonDataUtils.chartJiagedaiFenxi(this, "", optType);
			chartRoot.removeAllViews();
			chartRoot.addView(createChart(data, titleString));
			
		} else if(anaType == ANATYPE_SXZ) {
			displaySxzOpts();
			Map<String, Double> data = CommonDataUtils.chartSxzFenxi(this, "", optType);
			chartRoot.removeAllViews();
			chartRoot.addView(createChart(data, titleString));
			
		}
	}
	
	//display all options for dalei analysis
	private void displayDaleiOpts() {
		int i;
		for(i=0; i<alloptids.length; i++) {
			if(!(alloptids[i] == R.id.dalei_detail)) {
				LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
				layout.setVisibility(LinearLayout.GONE);
			}
			LinearLayout cLayout = (LinearLayout) findViewById(R.id.dalei_detail);
			cLayout.setVisibility(LinearLayout.VISIBLE);
		}
	}
	
	/**
	 * display all options for xiaolei analysis
	 */
	private void displayXiaoleiOpts() {
		int i;
		for(i=0; i<alloptids.length; i++) {
			if(!(alloptids[i] == R.id.xiaolei_detail)) {
				LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
				layout.setVisibility(LinearLayout.GONE);
			}
			LinearLayout cLayout = (LinearLayout) findViewById(R.id.xiaolei_detail);
			cLayout.setVisibility(LinearLayout.VISIBLE);
		}		
	}
	
	/**
	 * display all options for zhuti analysis
	 */
	private void displayZhutiOpts() {
		int i;
		for(i=0; i<alloptids.length; i++) {
			if(!(alloptids[i] == R.id.zhuti_detail)) {
				LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
				layout.setVisibility(LinearLayout.GONE);
			}
			LinearLayout cLayout = (LinearLayout) findViewById(R.id.zhuti_detail);
			cLayout.setVisibility(LinearLayout.VISIBLE);
		}		
	}
	
	/**
	 * display all options for boduan
	 */
	private void displayBoduanOpts() {
		int i;
		for(i=0; i<alloptids.length; i++) {
			if(!(alloptids[i] == R.id.boduan_detail)) {
				LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
				layout.setVisibility(LinearLayout.GONE);
			}
			LinearLayout cLayout = (LinearLayout) findViewById(R.id.boduan_detail);
			cLayout.setVisibility(LinearLayout.VISIBLE);
		}	
	}
	
	/**
	 * display all options for yanse analysis
	 */
	private void displayYanseOpts() {
		int i;
		for(i=0; i<alloptids.length; i++) {
			if(!(alloptids[i] == R.id.yanse_detail)) {
				LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
				layout.setVisibility(LinearLayout.GONE);
			}
			LinearLayout cLayout = (LinearLayout) findViewById(R.id.yanse_detail);
			cLayout.setVisibility(LinearLayout.VISIBLE);
		}	
	}
	
	/**
	 * display all options for chima analysis
	 */
	private void displayChimaOpts() {
		int i;
		for(i=0; i<alloptids.length; i++) {
			if(!(alloptids[i] == R.id.chima_detail)) {
				LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
				layout.setVisibility(LinearLayout.GONE);
			}
			LinearLayout cLayout = (LinearLayout) findViewById(R.id.chima_detail);
			cLayout.setVisibility(LinearLayout.VISIBLE);
		}	
	}
	
	/**
	 * display all options for jiagedai 
	 */
	private void displayJiagedaiOpts() {
		int i;
		for(i=0; i<alloptids.length; i++) {
			if(!(alloptids[i] == R.id.jiagedai_detail)) {
				LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
				layout.setVisibility(LinearLayout.GONE);
			}
			LinearLayout cLayout = (LinearLayout) findViewById(R.id.jiagedai_detail);
			cLayout.setVisibility(LinearLayout.VISIBLE);
		}		
	}
	
	/**
	 * display all options for sxz
	 */
	private void displaySxzOpts() {
		int i;
		for(i=0; i<alloptids.length; i++) {
			if(!(alloptids[i] == R.id.sxz_detail)) {
				LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
				layout.setVisibility(LinearLayout.GONE);
			}
			LinearLayout cLayout = (LinearLayout) findViewById(R.id.sxz_detail);
			cLayout.setVisibility(LinearLayout.VISIBLE);
		}	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		Map<String, Double> data = new HashMap<String, Double>();
		int i;
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.dalei:
			displayDaleiOpts();
			data = CommonDataUtils.chartDaleiFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "大类分析-总款占比");
			break;
			
		case R.id.xialei:
			displayXiaoleiOpts();
			data = CommonDataUtils.chartXiaoleiFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "小类分析-总款占比");
			break;
			
		case R.id.zhuti:
			displayZhutiOpts();
			data = CommonDataUtils.chartZhutiFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "主题分析-总款占比");
			break;
			
		case R.id.yanse:
			displayYanseOpts();
			data = CommonDataUtils.chartYanseFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "颜色分析-总款占比");
			break;
			
		case R.id.boduan:
			displayBoduanOpts();
			data = CommonDataUtils.chartBoduanFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "波段分析-总款占比");
			break;
			
		case R.id.chima:
			displayChimaOpts();
			break;
			
		case R.id.jiagedai:
			displayJiagedaiOpts();
			data = CommonDataUtils.chartJiagedaiFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "价格带分析-总款占比");
			break;
			
		case R.id.sxz:
			displaySxzOpts();
			data = CommonDataUtils.chartSxzFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "上下装分析-总款占比");
			break;
			
		case R.id.dalei_button01:
			data = CommonDataUtils.chartDaleiFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "大类分析-总款占比");
			break;
			
		case R.id.dalei_button02:
			data = CommonDataUtils.chartDaleiFenxi(this, "", CommonDataUtils.DHKZB);
			chart(data, "大类分析-订货款占比");
			break;
			
		case R.id.dalei_button03:
			data = CommonDataUtils.chartDaleiFenxi(this, "", CommonDataUtils.DLZB);
			chart(data, "大类分析-订量占比");
			break;
			
		case R.id.dalei_button04:
			data = CommonDataUtils.chartDaleiFenxi(this, "", CommonDataUtils.JEZB);
			chart(data, "大类分析-金额占比");
			break;
			
		case R.id.xiaolei_button01:
			data = CommonDataUtils.chartXiaoleiFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "小类分析-总款占比");
			break;
			
		case R.id.xiaolei_button02:
			data = CommonDataUtils.chartXiaoleiFenxi(this, "", CommonDataUtils.DHKZB);
			chart(data, "小类分析-订货款占比");
			break;
			
		case R.id.xiaolei_button03:
			data = CommonDataUtils.chartXiaoleiFenxi(this, "", CommonDataUtils.DLZB);
			chart(data, "小类分析-订量占比");
			break;
			
		case R.id.xiaolei_button04:
			data = CommonDataUtils.chartXiaoleiFenxi(this, "", CommonDataUtils.JEZB);
			chart(data, "小类分析-金额占比");
			break;
			
		case R.id.zhuti_button01:
			data = CommonDataUtils.chartZhutiFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "主题分析-总款占比");
			break;
			
		case R.id.zhuti_button02:
			data = CommonDataUtils.chartZhutiFenxi(this, "", CommonDataUtils.DHKZB);
			chart(data, "主题分析-订货款占比");
			break;
			
		case R.id.zhuti_button03:
			data = CommonDataUtils.chartZhutiFenxi(this, "", CommonDataUtils.DLZB);
			chart(data, "主题分析-订量占比");
			break;
			
		case R.id.zhuti_button04:
			data = CommonDataUtils.chartZhutiFenxi(this, "", CommonDataUtils.JEZB);
			chart(data, "主题分析-金额占比");
			break;
			
		case R.id.boduan_button01:
			data = CommonDataUtils.chartBoduanFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "波段分析-总款占比");
			break;
			
		case R.id.boduan_button02:
			data = CommonDataUtils.chartBoduanFenxi(this, "", CommonDataUtils.DHKZB);
			chart(data, "波段分析-订货款占比");
			break;
			
		case R.id.boduan_button03:
			data = CommonDataUtils.chartBoduanFenxi(this, "", CommonDataUtils.DLZB);
			chart(data, "波段分析-订量占比");
			break;
			
		case R.id.boduan_button04:
			data = CommonDataUtils.chartBoduanFenxi(this, "", CommonDataUtils.JEZB);
			chart(data, "波段分析-金额占比");
			break;
			
		case R.id.yanse_button01:
			data = CommonDataUtils.chartYanseFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "颜色分析-总款占比");
			break;
			
		case R.id.yanse_button02:
			data = CommonDataUtils.chartYanseFenxi(this, "", CommonDataUtils.DHKZB);
			chart(data, "颜色分析-订货款占比");
			break;
			
		case R.id.yanse_button03:
			data = CommonDataUtils.chartYanseFenxi(this, "", CommonDataUtils.DLZB);
			chart(data, "颜色分析-订量占比");
			break;
			
		case R.id.yanse_button04:
			data = CommonDataUtils.chartYanseFenxi(this, "", CommonDataUtils.JEZB);
			chart(data, "颜色分析-金额占比");
			break;
			
		case R.id.chima_button01:
			break;
			
		case R.id.chima_button02:
			break;
			
		case R.id.chima_button03:
			break;
			
		case R.id.chima_button04:
			break;
			
		case R.id.jiagedai_button01:
			data = CommonDataUtils.chartJiagedaiFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "价格带分析-总款占比");
			break;
			
		case R.id.jiagedai_button02:
			data = CommonDataUtils.chartJiagedaiFenxi(this, "", CommonDataUtils.DHKZB);
			chart(data, "价格带分析-订货款占比");
			break;
			
		case R.id.jiagedai_button03:
			data = CommonDataUtils.chartJiagedaiFenxi(this, "", CommonDataUtils.DLZB);
			chart(data, "价格带分析-订量占比");
			break;
			
		case R.id.jiagedai_button04:
			data = CommonDataUtils.chartJiagedaiFenxi(this, "", CommonDataUtils.JEZB);
			chart(data, "价格带分析-金额占比");
			break;
			
		case R.id.sxz_button01:
			data = CommonDataUtils.chartSxzFenxi(this, "", CommonDataUtils.ZKZB);
			chart(data, "上下装分析-总款占比");
			break;
			
		case R.id.sxz_button02:
			data = CommonDataUtils.chartSxzFenxi(this, "", CommonDataUtils.DHKZB);
			chart(data, "上下装分析-订货款占比");
			break;
			
		case R.id.sxz_button03:
			data = CommonDataUtils.chartSxzFenxi(this, "", CommonDataUtils.DLZB);
			chart(data, "上下装分析-订量占比");
			break;
			
		case R.id.sxz_button04:
			data = CommonDataUtils.chartSxzFenxi(this, "", CommonDataUtils.JEZB);
			chart(data, "上下装分析-金额占比");
			break;
			
			
		default:
			break;
		}
	}
	
	private void chart(Map<String, Double> data, String title) {
		setTextForTitle(title);
		chartRoot.removeAllViews();
		chartRoot.addView(createChart(data, title));
	}
	
//	private double[] getXiaoleiFenxiData(String where, String anaType) {
//		return null;
//	}
//	
//	private double[] getZhutiFenxiData(String where, String anaType) {
//		return null;
//	}
//	
//	private double[] getBoduanFenxiData(String where, String anaType) {
//		return null;
//	}
//	
//	private double[] getYanseFenxiData(String where, String anaType) {
//		return null;
//	}
//	
//	private double[] getChimaFenxiData(String where, String anaType) {
//		return null;
//	}
//	
//	private double[] getJiagedaiFenxiData(String where, String anaType) {
//		return null;
//	}
//	
//	private double[] getSxzFenxiData(String where, String anaType) {
//		return null;
//	}
//	
//	private double[] getDaleiFenxiData(String where, String anaType) {
////		if(mDataSet == null) {
////			mDataSet = new ArrayList<DaleiFenxiDAO>();
////		}
//		List<DaleiFenxiDAO> mData = new ArrayList<DaleiFenxiDAO>();
//		String sql = " SELECT "
//			+ " (select waretypename From sawaretype Where rtrim(sawaretype.waretypeid) = rtrim(sawarecode.waretypeid)) dalei, "
//			/*+ " (Select type1 From type1 Where rtrim(id) = trim(sawarecode.id)) xiaolei, "*/
//			+ " sum(saindent.[warenum]) amount, "
//			+ " Sum(saindent.[warenum] * Retailprice ) price, "
//			+ " count( distinct saindent.warecode) ware_cnt, "
//			+ " (Select count(warecode) From sawarecode B where rtrim(B.id) = Rtrim(sawarecode.id)) ware_all "
//			+ " FROM saindent,sawarecode "
//			+ " WHERE  Rtrim(saindent.warecode)=Rtrim(sawarecode.warecode) "
//			+ " And Rtrim(saindent.departcode) = 'A100' "
//		    + where
//		    + " And		saindent.[warenum] > 0 "
//			+ " GROUP BY  sawarecode.waretypeid";
//		
//		SQLiteDatabase db = AsProvider.getWriteableDatabase(DaleiPipeChartActivity.this);
//		Cursor cursor = db.rawQuery(sql, null);
//		try {
//			if(cursor != null && cursor.moveToFirst()) {
//				mData.clear();
//				while(!cursor.isAfterLast()) {
//					DaleiFenxiDAO dao = new DaleiFenxiDAO();
//					dao.setDalei(cursor.getString(DaLeiZongheAnalysisActivity.INDEX_DALEI));
//					dao.setWareAll(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_WARE_ALL));
//					dao.setWareCnt(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_WARE_CNT));
//					dao.setAmount(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_AMOUNT));
//					dao.setPrice(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_PRICE));
//					mData.add(dao);
//					cursor.moveToNext();
//				}
//			}
//		} finally {
//			if(cursor != null) {
//				cursor.close();
//			}
//			if(db != null) {
//				db.close();
//			}
//		}
//		
//		if(!TextUtils.isEmpty(anaType)) {
//			if("zhanzongkuanbi".equals(anaType)) {
//				double[] values = new double[mData.size()];
//				for(int i=0; i<values.length; i++) {
//					values[i] = mData.get(i).getWareAll();
//				}
//				return values;
//			} else if("yidingzhanbi".equals(anaType)) {
//				double[] values = new double[mData.size()];
//				for(int i=0; i<values.length;i++) {
//					values[i] = mData.get(i).getWareCnt();
//				}
//			}
//		}
//		return new double[0];
//	}

	private GraphicalView createChart(double[] mValues, String chartTitle) {
		int[] colors = ColorUtils.getColors(mValues.length);
		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setLabelsTextSize(20);
		renderer.setLegendTextSize(20);
		renderer.setChartTitleTextSize(30);
		renderer.setMargins(new int[]{20, 30, 15, 0});
		for(int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		renderer.setZoomButtonsVisible(true);
		renderer.setZoomEnabled(true);
		renderer.setChartTitle(chartTitle);
		renderer.setLabelsColor(Color.BLACK);
		CategorySeries series = new CategorySeries("categoryseries");
		int k=0;
		for(double value : values) {
			series.add(mDataSet.get(k++).getDalei()+"(" + value + ")", value);
		}
		return ChartFactory.getPieChartView(DaleiPipeChartActivity.this, series, renderer);
	}
	
	private GraphicalView createChart(Map<String, Double> data, String chartTitle) {
		int[] colors = ColorUtils.getColors(data.size());
		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setLabelsTextSize(20);
		renderer.setLegendTextSize(20);
		renderer.setChartTitleTextSize(30);
		renderer.setMargins(new int[]{20, 30, 15, 0});
		for(int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		renderer.setZoomButtonsVisible(true);
		renderer.setZoomEnabled(true);
		renderer.setChartTitle(chartTitle);
		renderer.setLabelsColor(Color.BLACK);
		CategorySeries series = new CategorySeries("categoryseries");
		Set<String> keys = data.keySet();
		Iterator iterator = keys.iterator();
		while(iterator.hasNext()) {
			String key = (String)iterator.next();
			double value = (double) data.get(key);
//			Log.e(TAG, "key: " + key + ", value: " + value);
			series.add(key+"( " + value + " )", value);
		}
		return ChartFactory.getPieChartView(this, series, renderer);
	}
}
