package com.as.ui.utils;

import java.util.Random;

import android.graphics.Color;

public class ColorUtils {

	private static int[] colors = new int[]{
			Color.BLUE,
			Color.CYAN,
			Color.DKGRAY,
			Color.GRAY,
			Color.GREEN,
			Color.LTGRAY,
			Color.MAGENTA,
			Color.RED,
			Color.WHITE,
			Color.YELLOW
	};
	
	public static int[] getColors(int size) {
		Random random = new Random(255);
		int[] myColor = new int[size];
		for(int i=0; i<size; i++) {
			myColor[i] = Color.argb(100, random.nextInt(), random.nextInt(), random.nextInt());
		}
		return myColor;
	}
}
