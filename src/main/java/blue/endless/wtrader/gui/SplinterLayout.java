package blue.endless.wtrader.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager2;
import java.util.HashMap;

import blue.endless.splinter.Layout;
import blue.endless.splinter.LayoutContainer;
import blue.endless.splinter.LayoutContainerMetrics;
import blue.endless.splinter.LayoutElement;
import blue.endless.splinter.LayoutElementMetrics;

public class SplinterLayout implements LayoutManager2, LayoutContainer {
	private LayoutContainerMetrics metrics = new LayoutContainerMetrics();
	private HashMap<SwingLayoutElement, LayoutElementMetrics> layoutComponents = new HashMap<>();
	
	public SplinterLayout() {
		metrics.setCellPadding(2);
	}
	
	@Override
	public void addLayoutComponent(String str, Component component) {
	}

	@Override
	public void layoutContainer(Container container) {
		
		Layout.layout(this, 0, 0, container.getWidth(), container.getHeight(), false);
	}

	@Override
	public Dimension minimumLayoutSize(Container container) {
		return new Dimension(16,16);
	}

	@Override
	public Dimension preferredLayoutSize(Container container) {
		return new Dimension(16,16);
	}

	@Override
	public void removeLayoutComponent(Component component) {
	}

	@Override
	public void addLayoutComponent(Component component, Object constraints) {
		//System.out.println("AddLayoutComponentObj: "+constraints);
		
		if (constraints instanceof GridBagConstraints) {
			GridBagConstraints gb = (GridBagConstraints) constraints;
			LayoutElementMetrics metrics = new LayoutElementMetrics(gb.gridx, gb.gridy, gb.gridwidth, gb.gridheight);
			layoutComponents.put(new SwingLayoutElement(component), metrics);
		} else if (constraints instanceof LayoutElementMetrics) {
			layoutComponents.put(new SwingLayoutElement(component), (LayoutElementMetrics) constraints);
		}
	}

	@Override
	public float getLayoutAlignmentX(Container container) {
		return 0.5f;
	}

	@Override
	public float getLayoutAlignmentY(Container container) {
		return 0.5f;
	}

	@Override
	public void invalidateLayout(Container container) {
		
	}

	@Override
	public Dimension maximumLayoutSize(Container container) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public Iterable<? extends LayoutElement> getLayoutChildren() {
		return layoutComponents.keySet();
	}

	@Override
	public LayoutElementMetrics getLayoutElementMetrics(LayoutElement elem) {
		return layoutComponents.get(elem);
	}

	@Override
	public LayoutContainerMetrics getLayoutContainerMetrics() {
		return metrics;
	}

	@Override
	public void setLayoutValues(LayoutElement elem, int x, int y, int width, int height) {
		if (elem instanceof SwingLayoutElement) {
			((SwingLayoutElement)elem).getSwingComponent().setBounds(x, y, width, height);
		}
	}
	
}
