package com.as.order.activity;

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
		R.id.chima,
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
		
//		zongkuanzhanbi = (Button) findViewById(R.id.button01);
//		dinghuokuanzhanbi = (Button) findViewById(R.id.button02);
//		dingliangzhanbi = (Button) findViewById(R.id.button03);
//		jinezhanbi = (Button) findViewById(R.id.button04);
//		zongkuanzhanbi.setOnClickListener(this);
//		dinghuokuanzhanbi.setOnClickListener(this);
//		jinezhanbi.setOnClickListener(this);
//		dingliangzhanbi.setOnClickListener(this);
		
		setTextForLeftTitleBtn("·µ»Ø");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		int anaType = bundle.getInt("anaType");
		int optType = bundle.getInt("optType");
		String titleString = bundle.getString("title");
		setTextForTitle(titleString);
		
		if(anaType == ANATYPE_DALEI) {
			Map<String, Double> data = CommonDataUtils.chartDaleiFenxi(this, "", CommonDataUtils.ZKZB);
			
		} else if(anaType == ANATYPE_XIAOLEI) {
			
		} else if(anaType == ANATYPE_ZHUTI) {
			
		} else if(anaType == ANATYPE_BODUAN) {
			
		} else if(anaType == ANATYPE_YANSE) {
			
		} else if(anaType == ANATYPE_CHIMA) {
			
		} else if(anaType == ANATYPE_JIAGEDAI) {
			
		} else if(anaType == ANATYPE_SXZ) {
			
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		int i;
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.dalei:
			for(i=0; i<alloptids.length; i++) {
				if(!(alloptids[i] == R.id.dalei_detail)) {
					LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
					layout.setVisibility(LinearLayout.GONE);
				}
				LinearLayout cLayout = (LinearLayout) findViewById(R.id.dalei_detail);
				cLayout.setVisibility(LinearLayout.VISIBLE);
			}
			break;
			
		case R.id.xialei:
			for(i=0; i<alloptids.length; i++) {
				if(!(alloptids[i] == R.id.xiaolei_detail)) {
					LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
					layout.setVisibility(LinearLayout.GONE);
				}
				LinearLayout cLayout = (LinearLayout) findViewById(R.id.xiaolei_detail);
				cLayout.setVisibility(LinearLayout.VISIBLE);
			}
			break;
			
		case R.id.zhuti:
			for(i=0; i<alloptids.length; i++) {
				if(!(alloptids[i] == R.id.zhuti_detail)) {
					LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
					layout.setVisibility(LinearLayout.GONE);
				}
				LinearLayout cLayout = (LinearLayout) findViewById(R.id.zhuti_detail);
				cLayout.setVisibility(LinearLayout.VISIBLE);
			}			
			break;
			
		case R.id.yanse:
			for(i=0; i<alloptids.length; i++) {
				if(!(alloptids[i] == R.id.yanse_detail)) {
					LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
					layout.setVisibility(LinearLayout.GONE);
				}
				LinearLayout cLayout = (LinearLayout) findViewById(R.id.yanse_detail);
				cLayout.setVisibility(LinearLayout.VISIBLE);
			}			
			break;
			
		case R.id.boduan:
			for(i=0; i<alloptids.length; i++) {
				if(!(alloptids[i] == R.id.boduan_detail)) {
					LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
					layout.setVisibility(LinearLayout.GONE);
				}
				LinearLayout cLayout = (LinearLayout) findViewById(R.id.boduan_detail);
				cLayout.setVisibility(LinearLayout.VISIBLE);
			}			
			break;
			
		case R.id.chima:
			for(i=0; i<alloptids.length; i++) {
				if(!(alloptids[i] == R.id.chima_detail)) {
					LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
					layout.setVisibility(LinearLayout.GONE);
				}
				LinearLayout cLayout = (LinearLayout) findViewById(R.id.chima_detail);
				cLayout.setVisibility(LinearLayout.VISIBLE);
			}			
			break;
			
		case R.id.jiagedai:
			for(i=0; i<alloptids.length; i++) {
				if(!(alloptids[i] == R.id.jiagedai_detail)) {
					LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
					layout.setVisibility(LinearLayout.GONE);
				}
				LinearLayout cLayout = (LinearLayout) findViewById(R.id.jiagedai_detail);
				cLayout.setVisibility(LinearLayout.VISIBLE);
			}			
			break;
			
		case R.id.sxz:
			for(i=0; i<alloptids.length; i++) {
				if(!(alloptids[i] == R.id.sxz_detail)) {
					LinearLayout layout = (LinearLayout) findViewById(alloptids[i]);
					layout.setVisibility(LinearLayout.GONE);
				}
				LinearLayout cLayout = (LinearLayout) findViewById(R.id.sxz_detail);
				cLayout.setVisibility(LinearLayout.VISIBLE);
			}			
			break;
			
		case R.id.dalei_button01:
			break;
			
		case R.id.dalei_button02:
			break;
			
		case R.id.dalei_button03:
			break;
			
		case R.id.dalei_button04:
			break;
			
		case R.id.xiaolei_button01:
			break;
			
		case R.id.xiaolei_button02:
			break;
			
		case R.id.xiaolei_button03:
			break;
			
		case R.id.xiaolei_button04:
			break;
			
		case R.id.zhuti_button01:
			break;
			
		case R.id.zhuti_button02:
			break;
			
		case R.id.zhuti_button03:
			break;
			
		case R.id.zhuti_button04:
			break;
			
		case R.id.boduan_button01:
			break;
			
		case R.id.boduan_button02:
			break;
			
		case R.id.boduan_button03:
			break;
			
		case R.id.boduan_button04:
			break;
			
		case R.id.yanse_button01:
			break;
			
		case R.id.yanse_button02:
			break;
			
		case R.id.yanse_button03:
			break;
			
		case R.id.yanse_button04:
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
			break;
			
		case R.id.jiagedai_button02:
			break;
			
		case R.id.jiagedai_button03:
			break;
			
		case R.id.jiagedai_button04:
			break;
			
		case R.id.sxz_button01:
			break;
			
		case R.id.sxz_button02:
			break;
			
		case R.id.sxz_button03:
			break;
			
		case R.id.sxz_button04:
			break;
			
			
		default:
			break;
		}
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
			series.add(key+"( " + value + " )", value);
		}
		return ChartFactory.getPieChartView(this, series, renderer);
	}
}
