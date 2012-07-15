package com.as.order.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.order.dao.ThemeOrderDAO;
import com.as.order.ui.AsListDialog;
import com.as.order.ui.ListDialogListener;
import com.as.ui.utils.CommonDataUtils;
import com.as.ui.utils.DialogUtils;
import com.as.ui.utils.ListViewUtils;

public class ThemeOrderActivity extends AbstractActivity implements OnTouchListener{

	private LinearLayout themeOrder;
	private TextView themeOrderedAmount;
	private ListView themeOrderList;
	private EditText themeEt;
	
	private Button prevPageBtn;
	private Button nextPageBtn;
	
	private boolean isThemeListDialogShow = false;
	
	private int pageSize = 10;
	int pageNum = 0;
	int totalPage = 0;
	private List<ThemeOrderDAO> mData = new ArrayList<ThemeOrderDAO>();
	private BaseAdapter mAdapter;
	
	private int totalStyleCount = 0;
	private int totalStyleColorCount = 0;
	private int totalWareNum = 0;
	
	View footer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		themeOrder = (LinearLayout) layoutInflater.inflate(R.layout.theme_order, null);
		mRootView.addView(themeOrder, FF);
		
		titleHomeBtn.setVisibility(Button.VISIBLE);
		
		prevPageBtn = (Button) findViewById(R.id.prev_page);
		nextPageBtn = (Button) findViewById(R.id.next_page);
		
		prevPageBtn.setOnClickListener(this);
		nextPageBtn.setOnClickListener(this);
		
		themeEt = (EditText) findViewById(R.id.theme_order_et);
		themeEt.setOnTouchListener(this);
		
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitle(this.getString(R.string.main_theme_order));
		setTextForTitleRightBtn("查询");
		
		themeOrderList = (ListView) findViewById(R.id.theme_order_list);
		String[] header = new String[]{
				"主题名称", "款数", "款色数", "总订量"
		};
//		final List<String[]> dataset = new ArrayList<String[]>();
//		dataset.add(new String[]{"都市女人风", "5", "16", "52"});
//		dataset.add(new String[]{"都市阳光", "8", "27", "42"});
//		dataset.add(new String[]{"都市丽人", "12", "35", "83"});
//		dataset.add(new String[]{"步行者", "3", "7", "31"});
//		dataset.add(new String[]{"探索者", "6", "18", "53"});
		themeOrderList.addHeaderView(ListViewUtils.generateListViewHeader(header, ThemeOrderActivity.this));
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return ListViewUtils.generateRow(mData.get(position).toArray(), ThemeOrderActivity.this);
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
				if(mData.size() < pageSize) {
					return mData.size();
				} else if((pageNum + 1)*pageSize > mData.size()) {
					return mData.size()%pageSize;
				}
				return mData.size();
			}
		};
		themeOrderList.setAdapter(mAdapter);
		
		themeOrderList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(position == 0) {
					return;
				}
				if(position == mAdapter.getCount()+1) {
					return;
				}
				Intent intent = new Intent(ThemeOrderActivity.this, ThemeOrderDetail.class);
				intent.putExtra("style", mData.get(pageNum*pageSize + (position-1)).getStyle());
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		String style = intent.getStringExtra("style");
		if(TextUtils.isEmpty(style)) {
			getData("");
		} else {
			themeEt.setText(style);
			getData(style);
		}
		getData(style);
		if(footer != null) {
			themeOrderList.removeFooterView(footer);
		}
		footer = ListViewUtils.generateRow(new String[]{
				"合计", "" + totalStyleCount, "" + totalStyleColorCount, "" + totalWareNum
		}, ThemeOrderActivity.this);
		themeOrderList.addFooterView(footer);
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.title_btn_right:
			getData(getWhere());
			if(footer != null) {
				themeOrderList.removeFooterView(footer);
			}
			footer = ListViewUtils.generateRow(new String[]{
					"合计", "" + totalStyleCount, "" + totalStyleColorCount, "" + totalWareNum
			}, ThemeOrderActivity.this);
			themeOrderList.addFooterView(footer);
			mAdapter.notifyDataSetChanged();
			break;
			
			
			default:
				break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
		case R.id.theme_order_et:
			if(!isThemeListDialogShow) {
				final AsListDialog themeDialog = DialogUtils.makeListDialog(ThemeOrderActivity.this, themeEt, CommonDataUtils.getThemes(ThemeOrderActivity.this));
				themeDialog.setDialogListener(new ListDialogListener() {
					
//					public void onOk() {
//
//					}
					
					@Override
					public void onCancel() {
						themeDialog.dismiss();
						isThemeListDialogShow = false;
					}

					@Override
					public void onClick(String text) {
//						StringBuffer sb = new StringBuffer();
////						String[] selectedData = themeDialog.getSelectedData();
//						for(int i=0; i<selectedData.length; i++) {
//							sb.append(selectedData[i] + ",");
//						}
//						themeEt.setText((sb.toString().trim().length()== 0)? ""	: sb.toString().trim().substring(0, sb.toString().trim().length()-1));
						themeEt.setText(text);
						themeDialog.dismiss();
						isThemeListDialogShow = false;						
					}
				});
				themeDialog.show();
				isThemeListDialogShow = true;
			}
			break;
			
			default:
				break;
		}
		return false;
	}

	private void getData(String style) {
		String sql = " select "
			+ " distinct a.style, "
			+ " (select count(warecode) from sawarecode where warecode in (select  "
			+ " warecode from sawarecode where sawarecode.style=A.style)) kuanshu ,"
			+ " (select COUNT(*) from  saware_color where WareCode in(select warecode  "
			+ " from sawarecode where sawarecode.style=A.style) )    kuanseshu, "
			+ " (select SUM(warenum) from saindent where  WareCode in(select warecode  "
			+ " from sawarecode where sawarecode.style=A.style) )   dingliang "
			+ " from "
			+ " sawarecode A  where 1=1 "
			+ (TextUtils.isEmpty(style) ? "" : " and A.style = '"+style+"' ")
			+ " group by A.style ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(ThemeOrderActivity.this);
		mData.clear();
		if(db != null) {
			Cursor cursor = db.rawQuery(sql, null);
			if( cursor != null) {
				totalStyleCount = 0;
				totalStyleColorCount = 0;
				totalWareNum = 0;
				totalPage = (cursor.getCount()%pageSize == 0) ? cursor.getCount()/pageSize : (cursor.getCount()/pageSize + 1);
				try {
					if(cursor.moveToFirst()) {
						while(!cursor.isAfterLast()) {
							ThemeOrderDAO dao = new ThemeOrderDAO();
							dao.setStyle(cursor.getString(0));
							dao.setStyleCount(cursor.getInt(1));
							dao.setStyleColorCount(cursor.getInt(2));
							dao.setWareNum(cursor.getInt(3));
							mData.add(dao);
							totalStyleCount += dao.getStyleCount();
							totalStyleColorCount += dao.getStyleColorCount();
							totalWareNum += dao.getWareNum();
							cursor.moveToNext();
						}
					}
 				} finally {
 					cursor.close();
 					db.close();
 				}
			}
		}
//		mAdapter.notifyDataSetChanged();
	}
	
	private String getWhere() {
		StringBuilder sb = new StringBuilder();
		String styleStr = themeEt.getText().toString().trim();
		if(!TextUtils.isEmpty(styleStr) && !(CommonDataUtils.ALL_OPT.equals(styleStr))) {
			sb.append(styleStr);
		}
		return sb.toString();
	}
}
