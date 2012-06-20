package com.as.order.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.as.order.R;
import com.as.order.dao.DaleiFenxiDAO;
import com.as.order.dao.JiagedaiFenxiDAO;
import com.as.ui.utils.AnaUtils;
import com.as.ui.utils.ListViewUtils;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class JiagedaiZongheAnalysisActivity extends AbstractActivity {
	
	private static final String TAG = "JiagedaiZongheAnalysisActivity";
	
	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<JiagedaiFenxiDAO> mDataSet;
	private Button prevBtn;
	private Button nextBtn;
	
	private int currPage=0;
	private int totalPage=0;
	
	//�������ܿ���
	private int totalWareCnt = 0;
	//�������ܶ���
	private int totalWareNum = 0;
	//�����ᶩ���ܶ�
	private int totalPrice = 0;
	//������һ����ʽ
	private int totalOrderedWareCnt = 0;
	
	private DecimalFormat formatter = new DecimalFormat("0.00");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.jiagedai_fenxi, null);
		mRootView.addView(mLayout, FF);
		
		prevBtn = (Button) findViewById(R.id.prev_page);
		nextBtn = (Button) findViewById(R.id.next_page);
		
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		mList = (ListView) findViewById(R.id.as_list);
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"�۸��",
				"�ܿ���",
				"�ܿ���ռ��",
				"������",
				"ռ���ܱ�",
				"ռ�Ѷ���",
				"����",
				"����ռ��",
				"�������",
				"���ռ��"
		}, JiagedaiZongheAnalysisActivity.this));
		mDataSet = new ArrayList<JiagedaiFenxiDAO>();
		
		
		setTextForTitle("�۸���ۺϷ���");
		setTextForLeftTitleBtn("����");
		setTextForTitleRightBtn("��ѯ");
	}

	private void initTotalData() {
		totalWareCnt = AnaUtils.getTotalWareCnt(JiagedaiZongheAnalysisActivity.this);
		totalWareNum = AnaUtils.getTotalWareNum(JiagedaiZongheAnalysisActivity.this);
		totalPrice = AnaUtils.getTotalPrice(JiagedaiZongheAnalysisActivity.this);
		totalOrderedWareCnt = AnaUtils.getTotalOrderedWareCnt(JiagedaiZongheAnalysisActivity.this);
	}
	
	private void initData() {
		
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				JiagedaiFenxiDAO dao = mDataSet.get(currPage*15+position);
				return ListViewUtils.generateRow(new String[]{
						dao.getJiagedai(),
						dao.getWareCnt()+"",
						formatter.format((((double)dao.getWareCnt()/totalWareCnt)*100))+"%",
						dao.getOrderedWareCnt()+"",
						formatter.format((((double)dao.getOrderedWareCnt()/totalWareCnt)*100)) +"%",
						formatter.format((((double)dao.getOrderedWareCnt()/totalOrderedWareCnt)*100))+"%",
						dao.getWarenum()+"",
						formatter.format((((double)dao.getWarenum()/totalWareNum)*100))+"%",
						dao.getOrderedPrice()+"",
						formatter.format((((double)dao.getOrderedPrice()/totalPrice)*100))+"%"
				}, JiagedaiZongheAnalysisActivity.this);
			}
			
			@Override
			public long getItemId(int position) {
				return 0;
			}
			
			@Override
			public Object getItem(int position) {
				return null;
			}
			
			@Override
			public int getCount() {
				if(mDataSet.size() < 15) {
					return mDataSet.size();
				} else if((currPage+1)*15 > mDataSet.size()) {
					return mDataSet.size()%15;
				}
				return 15;
			}
		};
		
		mList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initTotalData();
		getJiagedaiFenxiData("");
		initData();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.title_btn_right:
			break;
			
		case R.id.prev_page:
			if(currPage <= 0) {
				return;
			}
			currPage --;
			mAdapter.notifyDataSetChanged();
			break;
			
		case R.id.next_page:
			if(currPage >= totalPage -1) {
				return;
			}
			currPage ++;
			mAdapter.notifyDataSetChanged();
			break;
			
			default:
				break;
		}
	}

	private void getJiagedaiFenxiData(String where) {
		
	}
}