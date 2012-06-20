package com.as.order.activity;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.order.charts.DaleiFenxiPipeChart;
import com.as.order.dao.DaleiFenxiDAO;
import com.as.ui.utils.ColorUtils;

public class DaleiPipeChartActivity extends AbstractActivity {

	private static final String TAG = "DaleiPipeChartActivity";
	
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.dalei_chart, null);
		mRootView.addView(mLayout, FF);
		chartRoot = (LinearLayout) findViewById(R.id.chart_view);
		
		zongkuanzhanbi = (Button) findViewById(R.id.button01);
		dinghuokuanzhanbi = (Button) findViewById(R.id.button02);
		dingliangzhanbi = (Button) findViewById(R.id.button03);
		jinezhanbi = (Button) findViewById(R.id.button04);
		zongkuanzhanbi.setOnClickListener(this);
		dinghuokuanzhanbi.setOnClickListener(this);
		jinezhanbi.setOnClickListener(this);
		dingliangzhanbi.setOnClickListener(this);
		
		setTextForTitle("大类综合分析-饼状图");
		setTextForLeftTitleBtn("返回");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		getDaleiFenxiData("");
//		zongkuanzhanbiChart();
	}
	
//	private void zongkuanzhanbiChart() {
//		values = new double[mDataSet.size()];
//		for(int i=0; i<values.length; i++) {
//			values[i] = mDataSet.get(i).getWareAll();
//		}
//		GraphicalView gv = createChart("总款占比");
//		chartRoot.removeAllViews();
//		chartRoot.addView(gv);
//	}
//	
//	private void dinghuokuanzhanbiChart() {
//		values = new double[mDataSet.size()];
//		for(int i=0; i<values.length; i++){
//			values[i] = mDataSet.get(i).getWareCnt();
//		}
//		GraphicalView gv1 = createChart("订货款占比");
//		chartRoot.removeAllViews();
//		chartRoot.addView(gv1);
//	}
//	
//	private void yidingzhanbiChart() {
//		values = new double[mDataSet.size()];
//		for(int i=0; i<values.length; i++) {
//			values[i] = mDataSet.get(i).getWareCnt();
//		}
//	}
//	
//	private void dingliangzhanbiChart() {
//		values = new double[mDataSet.size()];
//		for(int i=0; i<values.length; i++) {
//			values[i] = mDataSet.get(i).getAmount();
//		}
//		GraphicalView gv = createChart("已订占比");
//		chartRoot.removeAllViews();
//		chartRoot.addView(gv);
//	}
//	private void jinezhanbiChart() {
//		values = new double[mDataSet.size()];
//		for(int i=0; i<values.length; i++) {
//			values[i] = mDataSet.get(i).getPrice();
//		}
//		GraphicalView gv = createChart("金额占比");
//		chartRoot.removeAllViews();
//		chartRoot.addView(gv);
//	}
	
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
			
		case R.id.button01:
//			zongkuanzhanbiChart();
			break;
			
		case R.id.button02:
//			dinghuokuanzhanbiChart();
			break;
			
		case R.id.button03:
//			dingliangzhanbiChart();
			break;
			
		case R.id.button04:
//			jinezhanbiChart();
			break;
			
			
		default:
			break;
		}
	}

	private double[] getDaleiFenxiData(String where, String anaType) {
//		if(mDataSet == null) {
//			mDataSet = new ArrayList<DaleiFenxiDAO>();
//		}
		List<DaleiFenxiDAO> mData = new ArrayList<DaleiFenxiDAO>();
		String sql = " SELECT "
			+ " (select waretypename From sawaretype Where rtrim(sawaretype.waretypeid) = rtrim(sawarecode.waretypeid)) dalei, "
			/*+ " (Select type1 From type1 Where rtrim(id) = trim(sawarecode.id)) xiaolei, "*/
			+ " sum(saindent.[warenum]) amount, "
			+ " Sum(saindent.[warenum] * Retailprice ) price, "
			+ " count( distinct saindent.warecode) ware_cnt, "
			+ " (Select count(warecode) From sawarecode B where rtrim(B.id) = Rtrim(sawarecode.id)) ware_all "
			+ " FROM saindent,sawarecode "
			+ " WHERE  Rtrim(saindent.warecode)=Rtrim(sawarecode.warecode) "
			+ " And Rtrim(saindent.departcode) = 'A100' "
		    + where
		    + " And		saindent.[warenum] > 0 "
			+ " GROUP BY  sawarecode.waretypeid";
		
		SQLiteDatabase db = AsProvider.getWriteableDatabase(DaleiPipeChartActivity.this);
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				mData.clear();
				while(!cursor.isAfterLast()) {
					DaleiFenxiDAO dao = new DaleiFenxiDAO();
					dao.setDalei(cursor.getString(DaLeiZongheAnalysisActivity.INDEX_DALEI));
					dao.setWareAll(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_WARE_ALL));
					dao.setWareCnt(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_WARE_CNT));
					dao.setAmount(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_AMOUNT));
					dao.setPrice(cursor.getInt(DaLeiZongheAnalysisActivity.INDEX_PRICE));
					mData.add(dao);
					cursor.moveToNext();
				}
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
			if(db != null) {
				db.close();
			}
		}
		
		if(!TextUtils.isEmpty(anaType)) {
			if("zhanzongkuanbi".equals(anaType)) {
				double[] values = new double[mData.size()];
				for(int i=0; i<values.length; i++) {
					values[i] = mData.get(i).getWareAll();
				}
				return values;
			} else if("yidingzhanbi".equals(anaType)) {
				double[] values = new double[mData.size()];
				for(int i=0; i<values.length;i++) {
					values[i] = mData.get(i).getWareCnt();
				}
			}
		}
		return new double[0];
	}

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
}
