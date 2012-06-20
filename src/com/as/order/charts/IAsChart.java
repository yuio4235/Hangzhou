package com.as.order.charts;

import org.achartengine.GraphicalView;

import android.content.Context;
import android.content.Intent;

public interface IAsChart {

	String NAME = "name";
	String DESC = "desc";
	
	String getName();
	String getDesc();
	GraphicalView execute(Context context);
}
