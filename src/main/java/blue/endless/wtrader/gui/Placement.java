package blue.endless.wtrader.gui;

import blue.endless.splinter.LayoutElementMetrics;

public class Placement {
	private LayoutElementMetrics metrics;
	
	private Placement(int x, int y) {
		metrics = new LayoutElementMetrics(x,y);
	}
	
	
	
	
	
	
	
	
	public static Placement at(int x, int y) {
		return new Placement(x, y);
	}
}
