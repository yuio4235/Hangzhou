package com.as.order.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.as.order.R;
import com.as.ui.utils.ListViewUtils;

public class ThemeOrderActivity extends AbstractActivity {

	private LinearLayout themeOrder;
	private TextView themeOrderedAmount;
	private ListView themeOrderList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		themeOrder = (LinearLayout) layoutInflater.inflate(R.layout.theme_order, null);
		mRootView.addView(themeOrder, FF);
		
		themeOrderedAmount = (TextView) findViewById(R.id.theme_order_ordered_amount);
		themeOrderedAmount.setText(Html.fromHtml("<u><i><font color=\"red\">10000</font></i></u>"));
		
		setTextForLeftTitleBtn(this.getString(R.string.title_back));
		setTextForTitle(this.getString(R.string.main_theme_order));
		
		themeOrderList = (ListView) findViewById(R.id.theme_order_list);
		String[] header = new String[]{
				"主题名称", "款数", "款色数", "总订量"
		};
		final List<String[]> dataset = new ArrayList<String[]>();
		dataset.add(new String[]{"都市女人风", "5", "16", "52"});
		dataset.add(new String[]{"都市阳光", "8", "27", "42"});
		dataset.add(new String[]{"都市丽人", "12", "35", "83"});
		dataset.add(new String[]{"步行者", "3", "7", "31"});
		dataset.add(new String[]{"探索者", "6", "18", "53"});
		themeOrderList.addHeaderView(ListViewUtils.generateListViewHeader(header, ThemeOrderActivity.this));
		themeOrderList.setAdapter(new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return ListViewUtils.generateRow(dataset.get(position), ThemeOrderActivity.this);
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
				return dataset.size();
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
			
			default:
				break;
		}
	}

}
