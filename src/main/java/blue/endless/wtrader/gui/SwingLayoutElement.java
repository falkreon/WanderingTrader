package blue.endless.wtrader.gui;

import java.awt.Component;

import javax.swing.JTextField;

import blue.endless.splinter.LayoutElement;

public class SwingLayoutElement implements LayoutElement {
	private static int lineHeight = 18;
	private Component component;
	
	public SwingLayoutElement(Component component) {
		this.component = component;
	}

	@Override
	public int getNaturalWidth() {
		//if (component instanceof JLabel) {
			return (int) component.getPreferredSize().getWidth();
		//}
		//return (int) component.getMinimumSize().getWidth();
	}
	
	@Override
	public int getNaturalHeight() {
		if (component instanceof JTextField) return lineHeight;
		
		
		//if (component instanceof JLabel) {
			return (int) component.getPreferredSize().getWidth();
		//}
		//return (int) component.getMinimumSize().getHeight();
	}
	
	public Component getSwingComponent() {
		return component;
	}
}
