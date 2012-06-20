package com.as.order.activity;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.ui.utils.AlertUtils;

public class OrderSetttingActivity extends AbstractActivity {

	private LinearLayout orderSetting;
	
	private Button insertDataBtn;
	private Button deleteDataBtn;
	private Button updateDataBtn;
	
	private EditText dataEt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		orderSetting = (LinearLayout) layoutInflater.inflate(R.layout.order_settings, null);
		mRootView.addView(orderSetting, FF);
		
		insertDataBtn = (Button) findViewById(R.id.insert_data_btn);
		deleteDataBtn = (Button) findViewById(R.id.delete_data_btn);
		updateDataBtn = (Button) findViewById(R.id.update_data_btn);
		
		insertDataBtn.setOnClickListener(this);
		deleteDataBtn.setOnClickListener(this);
		updateDataBtn.setOnClickListener(this);
		
		dataEt = (EditText) findViewById(R.id.data_et);
		
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitle(this.getString(R.string.main_order_setting));
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.insert_data_btn:
			SQLiteDatabase db = AsProvider.getWriteableDatabase(this);
			try {
				if(db != null) {
					db.beginTransaction();
					db.execSQL(getString(R.string.insert_data_sawarecode));
					db.setTransactionSuccessful();
					AlertUtils.toastMsg(this, "插入数据成功");
				} else {
					AlertUtils.toastMsg(this, "没有数据库连接可用");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				AlertUtils.toastMsg(this, "插入数据失败");
			} finally {
				if(db != null) {
					db.close();
				}
			}
			break;
			
		case R.id.delete_data_btn:
			SQLiteDatabase dbd = AsProvider.getWriteableDatabase(this);
			try {
				if(dbd != null) {
					dbd.beginTransaction();
					dbd.execSQL(dataEt.getText().toString().trim());
					dbd.setTransactionSuccessful();
					AlertUtils.toastMsg(this, "删除数据成功");
				} else {
					AlertUtils.toastMsg(this, "删除数据失败");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				AlertUtils.toastMsg(this, "删除数据失败");
			} finally  {
				if(dbd != null) {
					dbd.close();
				}
			}
			break;
			
		case R.id.update_data_btn:
			SQLiteDatabase dbb = AsProvider.getWriteableDatabase(this);
			try {
				if(dbb != null) {
					dbb.beginTransaction();
					dbb.execSQL(dataEt.getText().toString().trim());
					dbb.setTransactionSuccessful();
					AlertUtils.toastMsg(this, "更新数据成功");
				} else {
					AlertUtils.toastMsg(this, "更新数据失败");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				AlertUtils.toastMsg(this, "更新数据失败");
			} finally  {
				if(dbb != null) {
					dbb.close();
				}
			}
			break;
			
			default:
				break;
		}
	}

}
