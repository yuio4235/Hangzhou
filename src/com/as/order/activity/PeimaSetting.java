package com.as.order.activity;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.order.ui.AsListDialog;
import com.as.order.ui.ListDialogListener;
import com.as.ui.utils.AlertUtils;
import com.as.ui.utils.CommonDataUtils;
import com.as.ui.utils.DialogUtils;
import com.as.ui.utils.ListViewUtils;

public class PeimaSetting extends AbstractActivity implements OnTouchListener {

	private LinearLayout mLayout;
	
	private ListView mList;
	private EditText mSizeGroup;
	
	private View header;
	private BaseAdapter mAdpater;
	private String[] mData = new String[]{};
	
	private boolean isSizeGroupShow = false;
	private int columns = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLayout = (LinearLayout) layoutInflater.inflate(R.layout.peima_setting, null);
		mRootView.addView(mLayout, FF);
		
		mList = (ListView) findViewById(R.id.as_list);
		mSizeGroup = (EditText) findViewById(R.id.sizegroup);
		mSizeGroup.setOnTouchListener(this);
		
		setTextForTitle("配码设置");
		setTextForLeftTitleBtn("返回");
		setTextForTitleRightBtn("保存");
		
		mAdpater = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return ListViewUtils.generatePeimaRow(mData, columns, PeimaSetting.this);
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
				return 1;
			}
		};
	}
	
	@Override
	public void onClick(View v) {
		switch((v.getId())) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.title_btn_right:
			save();
			break;
			
			default:
				break;
		}
	}
	
	private void save() {
		LinearLayout row = (LinearLayout)mList.getChildAt(1);
		if(row == null) {
			return;
		}
		if(row.getChildCount()<=3) {
			return;
		}
		
		ContentValues values = new ContentValues();
		int index = 0;
		for(int i=3; i<=row.getChildCount()-2; i+=2) {
			EditText et = (EditText) row.getChildAt(i);
			index++;
			int value;
			try {
				value = Integer.parseInt(et.getText().toString().trim());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				value = 0;
			}
			values.put("s"+(index < 10 ? "0" + index : index), value);
		}
		SQLiteDatabase db = AsProvider.getWriteableDatabase(PeimaSetting.this);
		int res = db.update("sasizeset", values, "sizegroup = ?", new String[]{ getSizeTypeBySizeGroupName(mSizeGroup.getText().toString().trim())});
		if(res > 0) {
			AlertUtils.toastMsg(PeimaSetting.this, "更新成功");
		} else if(res == 0) {
			AlertUtils.toastMsg(PeimaSetting.this, "配码输入数据没有改变");
		}else {
			AlertUtils.toastMsg(PeimaSetting.this, "更新失败");
		}
	}
	
	private String getSizeTypeBySizeGroupName(String groupName) {
		String sql = "select para from sapara where paratype = 'CM' and paraconnent = '"+groupName+"' ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(PeimaSetting.this);
		Cursor cursor = db.rawQuery(sql, null);
		if(db != null && cursor != null) {
			try {
				if(cursor.moveToFirst()) {
					return cursor.getString(0);
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
		return "";
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()) {
		case R.id.sizegroup:
			if(!isSizeGroupShow) {
				final AsListDialog sizeGroupDialog = DialogUtils.makeListDialog(PeimaSetting.this, mSizeGroup, CommonDataUtils.getSizeGroup(PeimaSetting.this));
				sizeGroupDialog.setDialogListener(new ListDialogListener() {
					
					@Override
					public void onClick(String text) {
						mSizeGroup.setText(text);
						sizeGroupDialog.dismiss();
						
						/*display listview*/
						//===================================================
						if(header != null) {
							mList.removeHeaderView(header);
						}
						header = ListViewUtils.generateListViewHeader(getSizeBySizeGroup(mSizeGroup.getText().toString().trim()), PeimaSetting.this);
						mList.addHeaderView(header);
						mData = getPeimaValue(mSizeGroup.getText().toString().trim());
						mAdpater.notifyDataSetChanged();
						mList.setAdapter(mAdpater);
						//===================================================
						
						
						isSizeGroupShow = false;
					}
					
					@Override
					public void onCancel() {
						mSizeGroup.setText("");
						sizeGroupDialog.dismiss();
						isSizeGroupShow = false;
					}
				});
				sizeGroupDialog.show();
				isSizeGroupShow = true;
			}
			break;
			
			default:
				break;
		}
		return false;
	}
	
	private String[] getSizeBySizeGroup(String sizeGroup) { 
		ArrayList<String> sizes = new ArrayList<String>();
		String sql = " select size from showsize where type = ( select para from sapara where paratype = 'CM' and paraconnent  = '"+sizeGroup+"') order by show asc ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(PeimaSetting.this);
		Cursor cursor = db.rawQuery(sql, null);
		if(db != null && cursor != null) {
			try {
				if(cursor.moveToFirst()) {
					while(!cursor.isAfterLast()) {
						sizes.add(cursor.getString(0));
						cursor.moveToNext();
					}
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
		sizes.add(0, sizeGroup);
		String[] sizeArr = new String[sizes.size()];
		columns = sizeArr.length;
		return sizes.toArray(sizeArr);
	}
	
	private String[] getPeimaValue(String sizeGroup) {
		ArrayList<String> values = new ArrayList<String>();
		String sql = " select s01, s02, s03, s04, s05, s06, s07, s08, s09, s10, s11, s12, s13, s14, s15, s16, s17, s18, s19, s20 from sasizeset where sizegroup = ( select para from sapara where paratype = 'CM' and paraconnent = '"+sizeGroup+"' ) ";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(PeimaSetting.this);
		Cursor cursor = db.rawQuery(sql, null);
		if(db != null && cursor != null) {
			try {
				if(cursor.moveToFirst()) {
					for(int i=0; i<20; i++) {
						values.add(cursor.getString(i));
					}
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
		values.add(0, sizeGroup);
		String[] valArr = new String[values.size()];
		return values.toArray(valArr);
	}

}
