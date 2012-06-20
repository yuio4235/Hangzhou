package com.as.order.charts;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.as.order.dao.DaleiFenxiDAO;
import com.as.ui.utils.ColorUtils;

public class DaleiFenxiPipeChart extends AbstractChart {
	
	List<DaleiFenxiDAO> mDataSet;
	
	public DaleiFenxiPipeChart(List<DaleiFenxiDAO> dataset) {
		this.mDataSet = dataset;
	}

	@Override
	public GraphicalView execute(Context context) {
		double[] values = new double[mDataSet.size()];
		int[] colors = ColorUtils.getColors(mDataSet.size());
		for(int i=0; i<mDataSet.size(); i++	) {
			values[i] = mDataSet.get(i).getWareAll();
		}
		DefaultRenderer render = buildCategoryRenderer(colors);
		render.setZoomButtonsVisible(true);
		render.setZoomEnabled(true);
		render.setChartTitleTextSize(30);
		render.setLegendTextSize(30);
		render.setChartTitle("大类综合分析");
		render.setLabelsColor(Color.BLACK);
		
		CategorySeries series = new CategorySeries("大类综合分析");
		int k =0;
		for(double value : values)	 {
			series.add(mDataSet.get(k++).getDalei(), value);
		}
		
		return ChartFactory.getPieChartView(context, series, render);
//		return ChartFactory.getPieChartIntent(context, series, render, "大类综合分析");
	}

	@Override
	public String getDesc() {
		return "大类综合分析";
	}

	@Override
	public String getName() {
		return "大类综合分析";
	}

}
