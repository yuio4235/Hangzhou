package com.as.order.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.as.db.provider.AsContent;
import com.as.db.provider.AsProvider;
import com.as.db.provider.AsContent.SaWareCode;
import com.as.db.provider.AsContent.SaWareGroup;
import com.as.db.provider.AsContent.SaWareGroupColumns;
import com.as.order.R;
import com.as.order.dao.DapeiOrderDAO;
import com.as.ui.utils.ListViewUtils;

public class DapeiOrderActivity extends AbstractActivity {

	private static final String TAG = "DapeiOrderActivity";
	
	private LinearLayout dapeiOrder;
	private TextView totalDapeiAmount;
	private TextView orderedDapeiAmount;
	private TextView unOrderedDapeiAmount;
	private ListView dapeiList;
	
	private BaseAdapter mAdapter;
	
	private List<DapeiOrderDAO> mDataSet = new ArrayList<DapeiOrderDAO>();
	private Map<String, Integer> mItemCodeMap = new HashMap<String, Integer>();
	
	private int currPage = 0;
	private int totalPage = 0;
	
	private Button firstPage;
	private Button prevPage;
	private Button nextPage;
	private Button lastPage;
	
	private EditText mDapeiSearchText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dapeiOrder = (LinearLayout) layoutInflater.inflate(R.layout.dapei_order, null);
		mRootView.addView(dapeiOrder, FF);
		
		setTextForTitleRightBtn(getString(R.string.app_query));
		
		firstPage = (Button) findViewById(R.id.first_page);
		prevPage = (Button) findViewById(R.id.prev_page);
		nextPage = (Button) findViewById(R.id.next_page);
		lastPage = (Button) findViewById(R.id.last_page);
		
		firstPage.setOnClickListener(this);
		prevPage.setOnClickListener(this);
		nextPage.setOnClickListener(this);
		lastPage.setOnClickListener(this);

		mDapeiSearchText = (EditText) findViewById(R.id.dapei_order_query_text);
		
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitle(this.getString(R.string.main_dape_order));
		
		dapeiList = (ListView) findViewById(R.id.dapei_order_list);
		String[] header = new String[]{
				"搭配组号", "搭配名称", "货号1", "货号2", "货号3", "总订量"
		};
		final List<String[]> dataset = new ArrayList<String[]>();
		dataset.add(new String[]{"D001", "都市女人风", "2103118", "2103108", "2103008", "32"});
		dataset.add(new String[]{"D002", "都市阳光", "2113105", "2113205", "2103008", "42"});
		dataset.add(new String[]{"D003", "都市丽人", "2303003", "2323003", "2113305", "23"});
		dataset.add(new String[]{"D004", "步行者", "2113118", "2113018", "2313003", "11"});
		dataset.add(new String[]{"D005", "探索者", "2203119", "2203519", "", "13"});
		
		dapeiList.addHeaderView(ListViewUtils.generateListViewHeader(header, DapeiOrderActivity.this));
		
		mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Log.e(TAG, "mDataSize: " + mDataSet.size());
				if(mDataSet.get(currPage*15 + position) == null) {
					Log.e(TAG, "=================== positon: " + position);
				}
				return ListViewUtils.generateDapeiOrderRow(mDataSet.get(currPage*15 + position), DapeiOrderActivity.this);
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
				int count = 0;
				if(currPage == 0) {
					count = mDataSet.size() < 15 ? mDataSet.size() : 15;
				}
				
				if(currPage > 0) {
					if((currPage + 1)*15 > mDataSet.size()) {
						count = mDataSet.size()%15;
					} else {
						count = 15;
					}
				}
				return count;
			}
		};
		
		dapeiList.setAdapter(mAdapter);
		dapeiList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(position == 0) {
					return;
				}
				Log.e(TAG, "position: " + position);
				DapeiOrderDAO dao = mDataSet.get(currPage*15 + position-1);
				Intent intent = new Intent(DapeiOrderActivity.this, DapeiOrderDetailActivity.class);
				intent.putExtra("itemCode", dao.getItemCode());
				intent.putExtra("groupName", dao.getGroupName());
				intent.putExtra("from", "d");
				//Bundle mBundle = new Bundle();
//				mBundle.putString("itemCode", dao.getItemCode());
//				mBundle.putString("groupName", dao.getGroupName());
//				mBundle.putSerializable("warecodes", (Serializable)dao.getWareCodes());
//				intent.putExtras(mBundle);
				startActivity(intent);
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		String warecode = intent.getStringExtra("warecode");
		Log.e(TAG, "================ dapei order warecode: " + warecode);
		if(TextUtils.isEmpty(warecode)) {
			Log.e(TAG, "====== null warecode ========");
			initData("");
		} else {
			initData(TextUtils.isEmpty(warecode) ? "" : warecode);
		}
	}
	
	private void initData(String sc) {
		StringBuilder sb = new StringBuilder();
		if(!TextUtils.isEmpty(sc)) {
			String sql = " select itemcode from sawaregroup where warecode = '"+sc+"'";
			SQLiteDatabase db = AsProvider.getWriteableDatabase(DapeiOrderActivity.this);
			Cursor cc = db.rawQuery(sql, null);
			try {
				if(cc != null && cc.moveToFirst()) {
					while(!cc.isAfterLast()) {
						sb.append("'"+cc.getString(0)+"', ");
						cc.moveToNext();
					}
				}
			} finally {
				if(cc != null) {
					cc.close();
				}
				
				if(db != null) {
					db.close();
				}
			}
		}
		Cursor cursor = null;
		if(TextUtils.isEmpty(sb.toString())) {
			cursor = getContentResolver().query(AsContent.SaWareGroup.CONTENT_URI, SaWareGroup.CONTENT_PROJECTION, null, null, SaWareGroupColumns.ITEMCODE + " asc ");
		} else {
			cursor = getContentResolver().query(AsContent.SaWareGroup.CONTENT_URI, SaWareGroup.CONTENT_PROJECTION, TextUtils.isEmpty(sb.toString()) ? (SaWareGroupColumns.ITEMCODE + " in ( " + sb.toString() + " ) ") : ( SaWareGroupColumns.ITEMCODE + " in ( " + sb.substring(0, sb.length()-2) + " ) " ), null, SaWareGroupColumns.ITEMCODE + " asc ");
		}
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					String itemCode = cursor.getString(SaWareGroup.CONTENT_ITEMCODE_COLUMN);
					String groupName = cursor.getString(SaWareGroup.CONTENT_GROUPNAME_COLUMN);
					//搭配组已经存在，则取出搭配组，添加warecode
					if(mItemCodeMap.containsKey(itemCode)) {
						Log.e(TAG, "exist itemCode: " + itemCode);
						List<SaWareCode> sawarecodes = mDataSet.get((Integer)mItemCodeMap.get(itemCode)).getWareCodes();
						String warecode = cursor.getString(SaWareGroup.CONTENT_WARECODE_COLUMN);
						SaWareCode sawarecode = SaWareCode.restoreSaWareCodeWithWareCode(DapeiOrderActivity.this, warecode);
						if(sawarecode != null) {
							sawarecodes.add(sawarecode);
						}
						mDataSet.get((Integer)mItemCodeMap.get(itemCode)).setWareCodes(sawarecodes);
					} else {
						//新的搭配组
						//要先添加搭配组，然后添加款式，然后添加index
						DapeiOrderDAO dao = new DapeiOrderDAO();
						dao.setGroupName(groupName);
						dao.setItemCode(itemCode);
						List<SaWareCode> sawarecodes = new ArrayList<SaWareCode>();
						dao.setWareCodes(sawarecodes);
						if(mDataSet == null) {
							mDataSet = new ArrayList<DapeiOrderDAO>();
						}
						String warecode = cursor.getString(SaWareGroup.CONTENT_WARECODE_COLUMN);
						SaWareCode sawarecode = SaWareCode.restoreSaWareCodeWithWareCode(DapeiOrderActivity.this, warecode);
						sawarecodes.add(sawarecode);
						dao.setWareCodes(sawarecodes);
						mDataSet.add(dao);
						mItemCodeMap.put(itemCode, mDataSet.size()-1);
					}
					cursor.moveToNext();
				}
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		
		Log.e(TAG, "mDataSet size: " + mDataSet.size());
		
		if(mDataSet.size()%15==0){
			totalPage = mDataSet.size()/15;
		} else {
			totalPage = mDataSet.size()/15 + 1;
		}
		
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.first_page:
			currPage = 0;
			mAdapter.notifyDataSetChanged();
			break;
			
		case R.id.prev_page:
			if(currPage <= 0) {
				return;
			} else {
				currPage --;
				mAdapter.notifyDataSetChanged();
			}
			break;
			
		case R.id.next_page:
			if(currPage >= totalPage -1) {
				return;
			} else {
				currPage ++;
				mAdapter.notifyDataSetChanged();
			}
			break;
			
		case R.id.last_page:
			currPage = totalPage-1;
			mAdapter.notifyDataSetChanged();
			break;
			
			default:
				break;
		}
	}

}
