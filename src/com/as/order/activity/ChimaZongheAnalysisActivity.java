package com.as.order.activity;

import java.text.DecimalFormat;
import java.util.List;

import com.as.order.dao.ZhutiFenxiDAO;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ChimaZongheAnalysisActivity extends AbstractActivity {

	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<ZhutiFenxiDAO> mDataSet;
	private Button prevBtn;
	private Button nextBtn;
	
	private int currPage=0;
	private int totalPage=0;
	
	//订货会总款数
	private int totalWareCnt = 0;
	//订货会总订量
	private int totalWareNum = 0;
	//订货会订货总额
	private int totalPrice = 0;
	//订货会一定款式
	private int totalOrderedWareCnt = 0;	
	
	private DecimalFormat formatter = new DecimalFormat("0.00");
	@Override
	public void onClick(View v) {

	}

}
