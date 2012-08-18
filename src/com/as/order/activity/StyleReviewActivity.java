package com.as.order.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.order.dao.MustOrderDAO;
import com.as.ui.utils.ListViewUtils;

public class StyleReviewActivity extends AbstractActivity implements OnRatingBarChangeListener{

	private LinearLayout mLayout;
	private ListView mList;
	private BaseAdapter mAdapter;
	private List<MustOrderDAO> mData = new ArrayList<MustOrderDAO>();
	private RatingBar mRatingBar;
	
	private int pageNum = 0;
	private int totalPage = 0;
	
	private Button prevPageButton;
	private Button nextPageButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.style_review, null);
		mRootView.addView(mLayout, FF);
		
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitle(this.getString(R.string.main_style_review));
//		setTextForTitleRightBtn("查询");
		
		prevPageButton = (Button) findViewById(R.id.prev_page);
		nextPageButton = (Button) findViewById(R.id.next_page);
		
		prevPageButton.setOnClickListener(this);
		nextPageButton.setOnClickListener(this);
		mRatingBar = (RatingBar) findViewById(R.id.review_rating_bar);
		mRatingBar.setOnRatingBarChangeListener(this);
		
		mList = (ListView) findViewById(R.id.as_list);
		mList.addHeaderView(ListViewUtils.generateListViewHeader(new String[]{
				"序号", "编号", "上柜日期", "波段", "品类", "主题", "货号", "价格", "已订量"
		}, StyleReviewActivity.this));
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
//				return ListViewUtils.generateMustOrderItem(mData.get(pageNum*15+position), StyleReviewActivity.this);
				MustOrderDAO dao = mData.get(pageNum*10+position);
				dao.setSerialNo(pageNum*10+position+1);
				return ListViewUtils.generateReviewItem(dao, StyleReviewActivity.this);
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
				if(mData.size() < 10) {
					return mData.size();
				} else if((pageNum +1)*10 > mData.size()) {
					return mData.size()%10;
				}
				return 10;
			}
		};
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(position == 0) {
					return;
				}
				Intent intent = new Intent(StyleReviewActivity.this, OrderByStyleActivity.class);
				MustOrderDAO dao = mData.get(pageNum*10 + (position-1));
				intent.putExtra("style_code", dao.getSpecNo()+"");
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getData("");
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.prev_page:
			if(pageNum <= 0) {
				return;
			} else {
				pageNum --;
				mAdapter.notifyDataSetChanged();
			}
			break;
			
		case R.id.next_page:
			if(pageNum >= totalPage -1) {
				return;
			} else {
				pageNum ++;
				mAdapter.notifyDataSetChanged();
			}
			break;
			
			default:
				break;
		}
	}

	private List<MustOrderDAO> getData(String where) {
		if(mData == null) {
			mData = new ArrayList<MustOrderDAO>();
		}
		mData.clear();
		String SQL = " select a.specification, a.date3, a.state, a.paraconnent, a.waretypeid, a.waretypename, a.style, a.specification, a.retailprice, b.wareNum, a.pagenum"
			+ " from "
			+ " ( select sawarecode.[warecode], sawarecode.[specification], sawarecode.[date3], sawarecode.[state], sapara.[paraconnent], sawarecode.[waretypeid], sawaretype.[waretypename], sawarecode.[style], sawarecode.[specification], sawarecode.[retailprice], sawarecode.pagenum "
			+ " from sawarecode "
			+ " left join sapara on sawarecode.[state] = sapara.[para] "
			+ " left join sawaretype on sawarecode.[waretypeid] = sawaretype.[waretypeid] "
			+ " where sawarecode.warecode in ( select warecode from saorderscore where "+(mRatingBar.getRating()==0? " score > 0" : " score = " + mRatingBar.getRating())+") and  sapara.[paratype] = 'PD' "+ (TextUtils.isEmpty(where) ? "" :  where )+" ) a "
			+ " left join "
			+ " (select warecode,  sum(wareNum) wareNum from saindent group by warecode) b "
			+ " on a.warecode = b.warecode ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(StyleReviewActivity.this);
		Cursor cursor = db.rawQuery(SQL, null);
		if(db != null && cursor != null) {
			try {
				if(cursor.moveToFirst()) {
					totalPage = (cursor.getCount()%15==0?cursor.getCount()/15:(cursor.getCount()/15+1));
					while(!cursor.isAfterLast()) {
						MustOrderDAO dao = new MustOrderDAO();
						dao.setSpecNo(TextUtils.isEmpty(cursor.getString(0)) ? "" : cursor.getString(0));
						dao.setDate3(TextUtils.isEmpty(cursor.getString(1)) ? "" : cursor.getString(1));
						dao.setWave(TextUtils.isEmpty(cursor.getString(2)) ? "" : cursor.getString(3));
						dao.setWareType(TextUtils.isEmpty(cursor.getString(5)) ? "" : cursor.getString(5));
						dao.setTheme(TextUtils.isEmpty(cursor.getString(6)) ? "" : cursor.getString(6));
						dao.setHuohao(TextUtils.isEmpty(cursor.getString(10)) ? "" : cursor.getString(10));
						dao.setRetailPrice(TextUtils.isEmpty(cursor.getString(8)) ? "" : cursor.getString(8));
						dao.setWareNum(cursor.getInt(9));
						mData.add(dao);
						cursor.moveToNext();
//						mAdapter.notifyDataSetChanged();
					}
					mAdapter.notifyDataSetChanged();
				} else {
					mAdapter.notifyDataSetChanged();
				}
			} finally {
				cursor.close();
				db.close();
			}
		} else {
			if(cursor != null) {
				cursor.close();
			}
			
			if(db != null) {
				db.close();
			}
		}
		return mData;
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		getData("");
	}
}
