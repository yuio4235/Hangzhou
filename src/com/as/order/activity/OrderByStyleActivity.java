package com.as.order.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.as.db.provider.AsContent;
import com.as.db.provider.AsProvider;
import com.as.db.provider.AsContent.SaIndent;
import com.as.db.provider.AsContent.SaIndentColumns;
import com.as.db.provider.AsContent.SaOrderScore;
import com.as.db.provider.AsContent.SaOrderScoreColumns;
import com.as.db.provider.AsContent.SaSizeSet;
import com.as.db.provider.AsContent.SaWareCode;
import com.as.db.provider.AsContent.SaWareColorColumns;
import com.as.db.provider.AsContent.SawarecodeColumns;
import com.as.order.R;
import com.as.order.ui.OrderByStyleFooter;
import com.as.ui.utils.AlertUtils;
import com.as.ui.utils.FileUtils;
import com.as.ui.utils.ListViewUtils;
import com.as.ui.utils.UserUtils;

public class OrderByStyleActivity extends AbstractActivity implements OnRatingBarChangeListener{

	private static final String TAG = "OrderByStyleActivity";
	
	private LinearLayout orderByStyle;
	private ListView orderByStyleList;
	
	private Button searchBtn;
	private EditText searchEt;
	private ImageView displayIv;
	private Button nextImgBtn;
	private Button nextWareCode;
	private Button prevWareCode;
	private TextView swarecodeTv;
	private Button dapeiBtn;
	private Button waveBtn;
	private Button themeBtn;
	private int imgIndex = 0;
	
	private Bitmap[] displayImgs;
	
	private static final int MSG_SEARCH_CODE = 1001;
	private static final int MSG_UPDATE_SAINDENT = 1002;
	private static final int MSG_SAVE_ORDER = 1003;
	private static final int MSG_PEIMA = 1004;
	private static final int MSG_NEXT_WARECODE = 1005;
	
	private View headerView;
	private OrderByStyleFooter footerView;
	private String[] header;
	private List<String> headerList;
	private String[] mForm;
	private List<SaIndent> allIndents;
	private SaIndent currentIndent;
	
	private BaseAdapter listAdapter;
	private BaseAdapter mmAdapter;
	
	private Button peimaBtn;
	private RatingBar rb;
	
	//pagenum 编号
	private TextView tv01;
	//specification 货号
	private TextView tv02;
	//waretypeid 大类
	private TextView tv03;
	//state=>sapara=> paratype ='PD'波段 
	private TextView tv04;
	//waregoto 风格
	private TextView tv05;
	//sepcdef 款式定位
	private TextView tv06;
	//style 主题
	private TextView tv07;
	//retail price 零售价
	private TextView tv08;
	//plandate
	private TextView tv09;
	//卖点 trait
	private TextView tv10;
	
	private static final int ID_UPDATE_INDENT_DIALOG = 2001;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
				case MSG_SEARCH_CODE:
					Cursor c = getContentResolver().query(AsContent.SaWareCode.CONTENT_URI, SaWareCode.CONTENT_PROJECTION, SawarecodeColumns.SPECIFICATION + " = ? OR " + SawarecodeColumns.PAGENUM + " = ? ", new String[]{ searchEt.getText().toString().trim(), searchEt.getText().toString().trim() }, SaWareCode.RECORD_ID);
					try {
						if(c != null && c.moveToFirst()) {
							swarecodeTv.setText(c.getString(SaWareCode.CONTENT_WARECODE_COLUMN));
							rb.setRating(getScoreByWareCode());
							displayImgs = FileUtils.getBitmapsFileCode(OrderByStyleActivity.this, /*swarecodeTv.getText().toString()*/c.getString(SaWareCode.CONTENT_SPECIFICATION_COLUMN));
							Log.e(TAG, "imgs count: " + displayImgs.length + " SPECNO: " + c.getString(SaWareCode.CONTENT_SPECIFICATION_COLUMN));
							if(displayImgs != null && displayImgs.length>0) {
								displayIv.setImageBitmap(displayImgs[0]);
							}
							showOperations();
							header = getHeader();
							initInfo();
							if(headerView != null) {
								orderByStyleList.removeHeaderView(headerView);
							}
							headerView = ListViewUtils.generateListViewHeader(header, OrderByStyleActivity.this);
							orderByStyleList.addHeaderView(headerView);
//							if(allIndents == null) {
//								allIndents = getAllIndentsBySpecification();
//							}
							allIndents = getAllIndentsBySpecification();
							if(footerView != null) {
								orderByStyleList.removeFooterView(footerView);
							}
							footerView = new OrderByStyleFooter(OrderByStyleActivity.this, header.length, allIndents);
							orderByStyleList.addFooterView(footerView);
							if(mmAdapter == null) {
								mmAdapter = new BaseAdapter() {
									
									@Override
									public View getView(int position, View convertView, ViewGroup parent) {
										if(allIndents == null) {
											allIndents = getAllIndentsBySpecification();
										}
//										LinearLayout layout = ListViewUtils.generateRow(allIndents.get(position), header.length-2, OrderByStyleActivity.this);
										final LinearLayout layout = ListViewUtils.generateEditText(allIndents.get(position), header.length-2, OrderByStyleActivity.this);
										for(int i=3;i<(2*header.length+1-3);i+=2) {
											final int etIndex = i;
//											AlertUtils.toastMsg(OrderByStyleActivity.this, "etIndex: " + etIndex);
											final EditText et = (EditText) layout.getChildAt(i);
											et.addTextChangedListener(new TextWatcher() {
												
												int beforeValue = 0;
												int afterValue = 0;
												@Override
												public void onTextChanged(CharSequence s, int start, int before, int count) {
													
												}
												
												@Override
												public void beforeTextChanged(CharSequence s, int start, int count,
														int after) {
													String beforeValueStr = et.getText().toString().trim();
													try {
														beforeValue = Integer.parseInt(beforeValueStr);
													} catch (NumberFormatException e) {
														beforeValue = 0;
													}
//													beforeValue = Integer.parseInt(et.getText().toString().trim());
												}
												
												@Override
												public void afterTextChanged(Editable s) {
													String afterValueStr = et.getText().toString().trim();
													try {
														afterValue = Integer.parseInt(et.getText().toString().trim());
													} catch (NumberFormatException e) {
														e.printStackTrace();
														afterValue = 0;
													}
//													afterValue = Integer.parseInt(TextUtils.isEmpty(et.getText().toString().trim()) ? "0" : et.getText().toString().trim());
													int currentAmount = 0;
													for(int mm = 1; mm<=allIndents.size(); mm++) {
//														LinearLayout currItemLayout = (LinearLayout)orderByStyleList.getItemAtPosition(mm);
														LinearLayout currItemLayout = (LinearLayout) orderByStyleList.getChildAt(mm);
														int currValue = 0;
														try {
															int crrValue = Integer.parseInt(((EditText)currItemLayout.getChildAt(etIndex)).getText().toString().trim());
														} catch (NumberFormatException e) {
															e.printStackTrace();
															currValue = 0;
														}
														currentAmount += currValue;
													}
													Log.e(TAG, "===========================  currentAmount: " + currentAmount + " allIndents size: " + allIndents.size());
													footerView.setTextForItem(etIndex, currentAmount+"");
													footerView.setTextForItem(2*header.length-1, (Integer.parseInt(((TextView)footerView.getChildAt(2*header.length-1)).getText().toString().trim()) - beforeValue + afterValue) + "");
													TextView lastItemForThisRow = (TextView) layout.getChildAt(2*header.length-1);
													lastItemForThisRow.setText((Integer.parseInt(lastItemForThisRow.getText().toString().trim()) - beforeValue + afterValue) + "");
													
												}
											});
										}
										layout.setTag(allIndents.get(position));
										return layout;
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
										if(allIndents == null) {
											allIndents = getAllIndentsBySpecification();
										}
										return allIndents.size();
									}
								};
							}
							orderByStyleList.setAdapter(mmAdapter);
							mmAdapter.notifyDataSetChanged();
							orderByStyleList.setOnItemClickListener(new OnItemClickListener(){

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
//									if(position == 0) {
//										return;
//									}
//									currentIndent = allIndents.get(position-1);
//									
//									final AsDialog updateSaIndentDialog = new AsDialog(currentIndent, OrderByStyleActivity.this, R.style.AsDialog, ListViewUtils.generateListViewHeader(mForm, OrderByStyleActivity.this), ListViewUtils.generateEditTextRow(currentIndent, header.length - 2, OrderByStyleActivity.this));
//									updateSaIndentDialog.setDialogListener(new DialogListener() {
//										
//										@Override
//										public void onOk() {
//											Message updatemsg = mHandler.obtainMessage();
//											updatemsg.what = MSG_SEARCH_CODE;
//											updatemsg.sendToTarget();
//											updateSaIndentDialog.dismiss();
//										}
//										
//										@Override
//										public void onCancel() {
//											updateSaIndentDialog.dismiss();
//										}
//									});
//									updateSaIndentDialog.show();
								}});
							orderByStyleList.setVisibility(ListView.VISIBLE);
							
						} else {
							AlertUtils.toastMsg(OrderByStyleActivity.this, "没有找到指定编号的商品");
						}
					} finally {
						if(c != null) {
							c.close();
						}
					}
					break;
					
				case MSG_UPDATE_SAINDENT:
					showDialog(ID_UPDATE_INDENT_DIALOG);
					break;
					
				case MSG_SAVE_ORDER:
					saveOrder();
					break;
					
				case MSG_PEIMA:
					peima();
					break;
					
				case MSG_NEXT_WARECODE:
					
					break;
					
					default:
						break;
			}
		};
	};
	
	private void showOperations() {
		nextImgBtn.setVisibility(Button.VISIBLE);
		swarecodeTv.setVisibility(TextView.VISIBLE);
		displayIv.setVisibility(ImageView.VISIBLE);
		orderByStyleList.setVisibility(ListView.VISIBLE);
		dapeiBtn.setVisibility(Button.VISIBLE);
		waveBtn.setVisibility(Button.VISIBLE);
		themeBtn.setVisibility(Button.VISIBLE);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		orderByStyle = (LinearLayout) layoutInflater.inflate(R.layout.order_by_style, null);
		mRootView.addView(orderByStyle, FF);
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitleRightBtn("保存订单");
		setTextForTitle(this.getString(R.string.main_order_by_style));

		titleHomeBtn.setVisibility(Button.VISIBLE);
		
		peimaBtn = (Button) findViewById(R.id.peima);
		peimaBtn.setOnClickListener(this);
		rb = (RatingBar) findViewById(R.id.order_by_style_ratting);
		rb.setOnClickListener(this);
		rb.setOnRatingBarChangeListener(this);
		searchBtn = (Button) findViewById(R.id.order_by_style_search_btn);
		searchBtn.setOnClickListener(this);
		searchEt = (EditText) findViewById(R.id.order_by_style_search_code_et);
		searchEt.setSelectAllOnFocus(true);
		displayIv = (ImageView) findViewById(R.id.order_by_style_image_iv);
		nextImgBtn = (Button) findViewById(R.id.order_by_style_next_img_btn);
		nextWareCode = (Button) findViewById(R.id.order_by_style_next_warecode);
		nextWareCode.setOnClickListener(this);
		prevWareCode = (Button) findViewById(R.id.order_by_style_prev_warecode);
		prevWareCode.setOnClickListener(this);
		nextImgBtn.setOnClickListener(this);
		swarecodeTv = (TextView) findViewById(R.id.order_by_style_warecode_tv);
		dapeiBtn = (Button) findViewById(R.id.order_by_style_dapei_dingliang);
		dapeiBtn.setOnClickListener(this);
		waveBtn = (Button) findViewById(R.id.order_by_style_wave_dingliang);
		waveBtn.setOnClickListener(this);
		themeBtn = (Button) findViewById(R.id.order_by_style_theme_dingliang);
		themeBtn.setOnClickListener(this);
		
		tv01 = (TextView) findViewById(R.id.textview01);
		tv02 = (TextView) findViewById(R.id.textview02);
		tv03 = (TextView) findViewById(R.id.textview03);
		tv04 = (TextView) findViewById(R.id.textview04);
		tv05 = (TextView) findViewById(R.id.textview05);
		tv06 = (TextView) findViewById(R.id.textview06);
		tv07 = (TextView) findViewById(R.id.textview07);
		tv08 = (TextView) findViewById(R.id.textview08);
		tv09 = (TextView) findViewById(R.id.textview09);
		tv10 = (TextView) findViewById(R.id.textview10);
		
		orderByStyleList = (ListView) findViewById(R.id.order_by_style_list);
		
		String intentStyleCode = getIntent().getStringExtra("style_code");
		if(!TextUtils.isEmpty(intentStyleCode)) {
			searchEt.setText(intentStyleCode);
			Message searchMsg = mHandler.obtainMessage();
			searchMsg.what = MSG_SEARCH_CODE;
			searchMsg.sendToTarget();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.title_btn_left:
			finish();
			break;
			
		case R.id.order_by_style_search_btn:
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_SEARCH_CODE;
			msg.sendToTarget();
			break;
			
		case R.id.order_by_style_next_img_btn:
			if(displayImgs == null || displayImgs.length<=0) {
				return;
			}
			displayIv.setImageBitmap(displayImgs[(++imgIndex)%(displayImgs.length)]);
			break;
			
		case R.id.title_btn_right:
			SharedPreferences sp2 = PreferenceManager.getDefaultSharedPreferences(this);
			if(sp2.getBoolean("order_locked", false)) {
				AlertUtils.toastMsg(OrderByStyleActivity.this, "订单已经锁定,请联系管理员解锁定");
				return;
			}
			Message saveOrderMsg = mHandler.obtainMessage();
			saveOrderMsg.what = MSG_SAVE_ORDER;
			saveOrderMsg.sendToTarget();
			break;
			
		case R.id.peima:
			SharedPreferences spp1 = PreferenceManager.getDefaultSharedPreferences(this);
			if(spp1.getBoolean("order_locked", false)) {
				AlertUtils.toastMsg(OrderByStyleActivity.this, "订单已经锁定,请联系管理员解锁定");
				return;
			}
			Message peimaMsg = mHandler.obtainMessage();
			peimaMsg.what = MSG_PEIMA;
			peimaMsg.sendToTarget();
			break;
			
		case R.id.order_by_style_next_warecode:
			String specNo = getNextSpecNo(searchEt.getText().toString().trim());
			if(TextUtils.isEmpty(specNo)) {
				return;
			} else {
				searchEt.setText(specNo);
				Message searchMsg = mHandler.obtainMessage();
				searchMsg.what = MSG_SEARCH_CODE;
				searchMsg.sendToTarget();
			}
			break;
			
		case R.id.order_by_style_prev_warecode:
			String prevSpecNo = getPrevSpecNo(searchEt.getText().toString().trim());
			if(TextUtils.isEmpty(prevSpecNo)) {
				return;
			} else {
				searchEt.setText(prevSpecNo);
				Message searchMsg = mHandler.obtainMessage();
				searchMsg.what = MSG_SEARCH_CODE;
				searchMsg.sendToTarget();
			}
			break;
			
		case R.id.order_by_style_dapei_dingliang:
			String sql = " select itemcode from sawaregroup where warecode  = '"+swarecodeTv.getText().toString().trim()+"'";
			SQLiteDatabase db = AsProvider.getWriteableDatabase(OrderByStyleActivity.this);
			Cursor cursor = db.rawQuery(sql, null);
			try {
				if(cursor != null && cursor.getCount() <= 0) {
					AlertUtils.toastMsg(OrderByStyleActivity.this, "该款没有搭配款");
					return;
				}
			} finally {
				if(cursor != null) {
					cursor.close();
				}
				
				if(db != null) {
					db.close();
				}
			}
			Intent dapeiOrderIntent = new Intent(OrderByStyleActivity.this, DapeiOrderDetailActivity.class);
			dapeiOrderIntent.putExtra("warecode", swarecodeTv.getText().toString().trim());
			dapeiOrderIntent.putExtra("from", "o");
			startActivity(dapeiOrderIntent);
			break;
			
		case R.id.order_by_style_ratting:
			break;
			
		case R.id.order_by_style_wave_dingliang:
			Intent boduanIntent = new Intent(OrderByStyleActivity.this, SameBoduanActivity.class);
			boduanIntent.putExtra("boduan", tv04.getText().toString().trim());
			startActivity(boduanIntent);
			break;
			
		case R.id.order_by_style_theme_dingliang:
			if(TextUtils.isEmpty(searchEt.getText().toString().trim())) {
				return;
			}
			Intent themeOrder = new Intent(OrderByStyleActivity.this, ThemeOrderActivity.class);
			themeOrder.putExtra("style", tv07.getText().toString().trim());
			startActivity(themeOrder);
			break;
			
			default:
				break;
		}
	}
	
	private String[] getHeader() {
		String sql = " select sawarecode.specification, showsize.show, showsize.size  "
			+ " from saware_size, sawarecode, showsize "
			+ " where (saware_size.warecode = sawarecode.warecode) and  "
			+ " (saware_size.size = showsize.size) "
			+ " and (sawarecode.flag = showsize.type) and ( sawarecode.specification = '"+searchEt.getText().toString().trim()+"'  OR sawarecode.pagenum = '"+searchEt.getText().toString().trim()+"' )"
			+ " order by showsize.show asc ";
		ArrayList<String> header = new ArrayList<String>();
		SQLiteDatabase db = AsProvider.getWriteableDatabase(OrderByStyleActivity.this);
		Cursor cursor = db.rawQuery(sql, null);
		try  {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					header.add(cursor.getString(2));
					cursor.moveToNext();
				}
			}
		} finally  {
			if(cursor != null) {
				cursor.close();
			}
			if(db != null) {
				db.close();
			}
		}
		if(header.size() > 0) {
			header.add(0, "颜色");
			String[] myForm = new String[header.size()];
			mForm = header.toArray(myForm);
			header.add("合计");
		}
		headerList = header;
		String[] headerArr = new String[header.size()];
		return header.toArray(headerArr);
	}
	
	private void initInfo() {
		String infoSql = " SELECT"
			+ " sawarecode.pagenum, "
			+ " sawarecode.[specification], "
			+ " sawarecode.[waretypeid], "
			+ " sawaretype.[waretypename], "
			+ " sawarecode.[state], "
			+ " sapara.[paraconnent],  "
			+ " sawarecode.[waregoto], "
			+ " sawarecode.[specdef], "
			+ " sawarecode.[style], "
			+ " sawarecode.[retailprice], "
			+ " sawarecode.[plandate], "
			+ " sawarecode.[trait] "
			+ " from sawarecode "
			+ " left join sawaretype on sawarecode.[waretypeid] = sawaretype.waretypeid "
			+ " left join sapara on sapara.[para] = sawarecode.state "
			+ " where sapara.[paratype] = 'PD' AND  "
			+ " (sawarecode.[specification] = '"+searchEt.getText().toString().trim()+"' OR sawarecode.[pagenum] = '"+searchEt.getText().toString().trim()+"')";
		SQLiteDatabase db = AsProvider.getWriteableDatabase(OrderByStyleActivity.this);
		Cursor cursor = db.rawQuery(infoSql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				tv01.setText(cursor.getString(0));
				tv02.setText(cursor.getString(1));
				tv03.setText(cursor.getString(3));
				tv04.setText(cursor.getString(5));
				tv05.setText(cursor.getString(6));
				tv06.setText(cursor.getString(7));
				tv07.setText(cursor.getString(8));
				tv08.setText(cursor.getString(9));
				tv09.setText(cursor.getString(10));
				tv10.setText(cursor.getString(11));
			}
		} finally {
			if(db != null) {
				db.close();
			}
			if(cursor != null) {
				cursor.close();
			}
		}
	}

	private List<SaIndent> getAllIndentsBySpecification() {
		SharedPreferences sp = getSharedPreferences("user_account", Context.MODE_PRIVATE);
		String user_account = sp.getString("user_account", "");
		
		List<SaIndent> saIndents = new ArrayList<SaIndent>();
		String queryColorSql = " select saware_color.[warecode], saware_color.[colorcode], sacolorcode.[colorname], sawarecode.[specification] "
			+ " from saware_color, sacolorcode, sawarecode "
			+ " where saware_color.[warecode] = sawarecode.[warecode] and saware_color.[colorcode] = sacolorcode.[colorcode] and ( sawarecode.[specification] = '"+searchEt.getText().toString().trim()+"'  OR sawarecode.pagenum = '"+searchEt.getText().toString().trim()+"')"
			+ " order by saware_color.[colorcode] ";
		SQLiteDatabase db  = AsProvider.getWriteableDatabase(OrderByStyleActivity.this);
		Cursor cursor = db.rawQuery(queryColorSql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				while(!cursor.isAfterLast()) {
					SaIndent saIndent = new SaIndent();
					saIndent.wareCode = cursor.getString(0);
					saIndent.colorCode = cursor.getString(1);
					saIndent.colorName = cursor.getString(2);
					saIndent.departCode = user_account;
					saIndents.add(saIndent);
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
		
		//填充数量
		for(SaIndent sai : saIndents) {
			Cursor c = getContentResolver().query(SaIndent.CONTENT_URI, SaIndent.CONTENT_PROJECTION, SaIndentColumns.DEPARTCODE + " = ? and " + SaIndentColumns.WARECODE + " = ? and " + SaIndentColumns.COLORCODE + " = ?", new String[]{sai.departCode, sai.wareCode, sai.colorCode}, SaIndentColumns.ID + " asc ");
			try {
				if(c != null && c.moveToFirst()) {
					sai.indentNo = c.getString(SaIndent.CONTENT_INDENTNO_COLUMN);
					sai.s01 = c.getInt(SaIndent.CONTENT_S01_COLUMN);
					sai.s02 = c.getInt(SaIndent.CONTENT_S02_COLUMN);
					sai.s03 = c.getInt(SaIndent.CONTENT_S03_COLUMN);
					sai.s04 = c.getInt(SaIndent.CONTENT_S04_COLUMN);
					sai.s05 = c.getInt(SaIndent.CONTENT_S05_COLUMN);
					sai.s06 = c.getInt(SaIndent.CONTENT_S04_COLUMN);
					sai.s07 = c.getInt(SaIndent.CONTENT_S07_COLUMN);
					sai.s08 = c.getInt(SaIndent.CONTENT_S08_COLUMN);
					sai.s09 = c.getInt(SaIndent.CONTENT_S09_COLUMN);
					sai.s10 = c.getInt(SaIndent.CONTENT_S10_COLUMN);
					sai.s11 = c.getInt(SaIndent.CONTENT_S11_COLUMN);
					sai.s12 = c.getInt(SaIndent.CONTENT_S12_COLUMN);
					sai.s13 = c.getInt(SaIndent.CONTENT_S13_COLUMN);
					sai.s14 = c.getInt(SaIndent.CONTENT_S14_COLUMN);
					sai.s15 = c.getInt(SaIndent.CONTENT_S15_COLUMN);
					sai.s16 = c.getInt(SaIndent.CONTENT_S16_COLUMN);
					sai.s17 = c.getInt(SaIndent.CONTENT_S17_COLUMN);
					sai.s18 = c.getInt(SaIndent.CONTENT_S18_COLUMN);
					sai.s19 = c.getInt(SaIndent.CONTENT_S19_COLUMN);
					sai.s20 = c.getInt(SaIndent.CONTENT_S20_COLUMN);
					sai.wareNum = c.getInt(SaIndent.CONTENT_WARENUM_COLUMN);
				} 
			} finally {
				if(c != null) {
					c.close();
				}
			}
		}
		
		return saIndents;
	}
	
	private void saveOrder() {
		if(allIndents==null) {
			return;
		}
		for(int i=1; i<=allIndents.size(); i++) {
			SaIndent mSaIndent = allIndents.get(i-1);
			LinearLayout currentRow = (LinearLayout) orderByStyleList.getChildAt(i);
			int childCount = currentRow.getChildCount();
			int wareNum = 0;
			List<Integer> dataList = new ArrayList<Integer>();
			
			for(int m=0;m<childCount;m++) {
				if(currentRow.getChildAt(m) instanceof EditText) {
					EditText et = (EditText) currentRow.getChildAt(m);
					wareNum += Integer.parseInt(et.getText().toString().trim());
					dataList.add(Integer.parseInt(et.getText().toString().trim()));
				}
			}
			
			dataList.add(0, 0);
			Integer[] dataArr = new Integer[dataList.size()];
			dataList.toArray(dataArr);
			for(int n=1; n<dataArr.length; n++) {
				try {
					Field field = mSaIndent.getClass().getDeclaredField("s" + (n < 10 ? "0" + n : n));
					field.setInt(mSaIndent, dataArr[n]);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			try {
				Field wareNumField = mSaIndent.getClass().getDeclaredField("wareNum");
				wareNumField.setInt(mSaIndent, wareNum);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			int updated = getContentResolver().update(SaIndent.CONTENT_URI, mSaIndent.toContentValues(), SaIndentColumns.WARECODE+ "=? and " + SaIndentColumns.COLORCODE + "=?", new String[]{mSaIndent.wareCode, mSaIndent.colorCode});
			if(updated >=0) {
				AlertUtils.toastMsg(this, "订单保存成功");
			}
		}
	}
	
	private void peima() {
		if(allIndents == null || allIndents.size() == 0) {
			return;
		}
		SQLiteDatabase db = AsProvider.getWriteableDatabase(OrderByStyleActivity.this);
		String sql = " select flag from sawarecode where warecode = '"+swarecodeTv.getText().toString().trim()+"'";
		Cursor cursor = db.rawQuery(sql, null);
		String sizeGroup = "";
		if(db != null && cursor!= null) {
			try {
				if(cursor.moveToFirst()) {
					sizeGroup = cursor.getString(0);
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
		SaSizeSet sasizeset = SaSizeSet.restoreSaSizeSetWithSiezeGroup(OrderByStyleActivity.this, sizeGroup);
		int i;
		int m;
		int currRowTotal = 0;
		for(i=1; i<=allIndents.size(); i++) {
			LinearLayout currRow = (LinearLayout) orderByStyleList.getChildAt(i);
			int columnIndex = 1;
			int childCount = currRow.getChildCount();
			currRowTotal = 0;
			for(m=0;m<childCount;m++) {
				if(currRow.getChildAt(m) instanceof EditText) {
					EditText et = (EditText)currRow.getChildAt(m);
					try {
						Field field = sasizeset.getClass().getDeclaredField("s" + (columnIndex < 10 ? "0" + columnIndex : columnIndex));
						et.setText(field.getInt(sasizeset) + "");
						currRowTotal += field.getInt(sasizeset);
						columnIndex ++;
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			TextView rowTotalTv = (TextView) currRow.getChildAt(currRow.getChildCount()-2);
			rowTotalTv.setText(currRowTotal + "");
		}
		//last total row
		LinearLayout lastRow = (LinearLayout) orderByStyleList.getChildAt(allIndents.size()+1);
		int lastColumnIndex = 1;
		int lastRowTotal = 0;
		for(i=3;i<lastRow.getChildCount()-2; i+=2) {
			TextView tv = (TextView) lastRow.getChildAt(i);
			try {
				Field field = sasizeset.getClass().getDeclaredField("s" + (lastColumnIndex < 10 ? "0" + lastColumnIndex : lastColumnIndex));
				int value = field.getInt(sasizeset);
				tv.setText(value*allIndents.size() + "");
				lastRowTotal += value*allIndents.size();
				lastColumnIndex++;
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		TextView allTotal = (TextView) lastRow.getChildAt(lastRow.getChildCount()-2);
		allTotal.setText(lastRowTotal + "");
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {

		return null;
	}
	
	private SaWareCode getBasicInfo(String code) {
		return null;
	}
	
	private String getNextSpecNo(String str) {
		Cursor cursor = getContentResolver().query(SaWareCode.CONTENT_URI, SaWareCode.CONTENT_PROJECTION, SawarecodeColumns.SPECIFICATION + " = ? OR " + SawarecodeColumns.PAGENUM + " = ? ", new String[]{str, str}, SawarecodeColumns.ID);
		try {
			if(cursor != null && cursor.moveToFirst()) {
//				int _id = cursor.getInt(SaWareCode.CONTENT_ID_COLUMN);
				int pageNum = cursor.getInt(SaWareCode.CONTENT_PAGENUM_COLUMN);
				Cursor cc = getContentResolver().query(SaWareCode.CONTENT_URI, SaWareCode.CONTENT_PROJECTION, SawarecodeColumns.PAGENUM + " =  ? " , new String[]{ (++pageNum)+"" }, SaWareColorColumns.ID);
				try {
					if(cc!= null && cc.moveToFirst()) {
						return cc.getString(SaWareCode.CONTENT_SPECIFICATION_COLUMN);
					}
				} finally {
					if(cc != null) {
						cc.close();
					}
				}
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
	
	private String getPrevSpecNo(String str) {
		Cursor cursor = getContentResolver().query(SaWareCode.CONTENT_URI, SaWareCode.CONTENT_PROJECTION, SawarecodeColumns.SPECIFICATION + " = ? OR " + SawarecodeColumns.PAGENUM + " = ? ", new String[]{str, str}, SawarecodeColumns.ID);
		try {
			if(cursor != null && cursor.moveToFirst()) {
//				int _id = cursor.getInt(SaWareCode.CONTENT_ID_COLUMN);
				int pageNum = cursor.getInt(SaWareCode.CONTENT_PAGENUM_COLUMN);
				Cursor cc = getContentResolver().query(SaWareCode.CONTENT_URI, SaWareCode.CONTENT_PROJECTION, SawarecodeColumns.PAGENUM + " = ? ", new String[]{ (--pageNum)+"" }, SaWareColorColumns.ID);
				try {
					if(cc!= null && cc.moveToFirst()) {
						return cc.getString(SaWareCode.CONTENT_SPECIFICATION_COLUMN);
					}
				} finally {
					if(cc != null) {
						cc.close();
					}
				}
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		return null;		
	}
	
	private float getScoreByWareCode() {
//		Cursor cursor = getContentResolver().query(SaOrderScore.CONTENT_URI, SaOrderScore.CONTENT_PROJECTION, SaOrderScoreColumns.WARECODE + " = ? ", new String[]{swarecodeTv.getText().toString().trim()}, null);
//		if(cursor != null) {
//			try {
//				if(cursor.moveToFirst()) {
//					return cursor.getFloat(SaOrderScore.CONTENT_SCORE_COLUMN);
//				} else {
//					return 0;
//				}
//			} finally {
//				cursor.close();
//			}
//		} else  {
// 			return 0;
//		}
		SQLiteDatabase db = AsProvider.getWriteableDatabase(OrderByStyleActivity.this);
		String sql = " select score from saorderscore where warecode = '"+swarecodeTv.getText().toString().trim()+"'";
		Cursor cursor = db.rawQuery(sql, null);
		try {
			if(cursor != null && cursor.moveToFirst()) {
				return cursor.getFloat(0);
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
			if(db != null) {
				db.close();
			}
		}
		return 0.0f;
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		if(!TextUtils.isEmpty(swarecodeTv.getText().toString().trim())) {
			String delSql = " delete from saorderscore where warecode = '"+swarecodeTv.getText().toString().trim()+"'";
			SQLiteDatabase db = AsProvider.getWriteableDatabase(OrderByStyleActivity.this);
			if(db != null) {
				db.execSQL(delSql);
				db.close();
			}
			float rtVal = rb.getRating();
			ContentValues rbValues = new ContentValues();
			rbValues.put(SaOrderScoreColumns.DEPARTCODE, UserUtils.getUserAccount(OrderByStyleActivity.this));
			rbValues.put(SaOrderScoreColumns.WARECODE, swarecodeTv.getText().toString().trim());
			rbValues.put(SaOrderScoreColumns.SCORE, rtVal);
			getContentResolver().insert(SaOrderScore.CONTENT_URI, rbValues);
		} else {
			AlertUtils.toastMsg(OrderByStyleActivity.this, "没有指定款号");
		}
	}
}
 