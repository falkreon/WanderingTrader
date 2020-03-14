package blue.endless.wtrader.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.text.JTextComponent;

import blue.endless.splinter.LayoutElementMetrics;

public class SplinterBox extends JPanel {
	private static final long serialVersionUID = 1303881739173686220L;
	
	private Axis axis = Axis.VERTICAL;
	private SplinterLayout layout = new SplinterLayout();
	private int nextCell;
	
	public SplinterBox() {
		layout.getLayoutContainerMetrics().setCellPadding(6);
		this.setLayout(layout);
	}
	
	public SplinterBox withAxis(Axis axis) {
		this.axis = axis;
		return this;
	}
	
	@Override
	public Component add(Component comp) {
		int cx = 0;
		int cy = 0;
		if (axis==Axis.VERTICAL) {
			cy = nextCell;
		} else {
			cx = nextCell;
		}
		nextCell++;
		
		LayoutElementMetrics metrics = new LayoutElementMetrics(cx, cy);
		add(comp, metrics);
		
		return comp;
	}
	
	public void addComponents(Component... components) {
		/** TWEAK COMPONENTS TO FIT STYLE */
		
		for(Component comp : components) {
			if (comp instanceof JTextComponent) {
				if (SynthLookAndFeel.getStyleFactory()!=null) {
					SynthStyle style = SynthLookAndFeel.getStyleFactory().getStyle((JComponent) comp, SynthLookAndFeel.getRegion((JComponent) comp));
					Font font = style.getFont(null); //THIS IS A HACK
					if (font!=null) System.out.println(font.getFontName());
					else System.out.println("NULL");
					if (font!=null) comp.setFont(font);
				}
			}
		}
		
		
		int cx = 0;
		int cy = 0;
		if (axis==Axis.VERTICAL) {
			cy = nextCell;
		} else {
			cx = nextCell;
		}
		nextCell++;
		
		for(int i=0; i<components.length; i++) {
			if (axis==Axis.VERTICAL) {
				cx = i;
			} else {
				cy = i;
			}
			LayoutElementMetrics metrics = new LayoutElementMetrics(cx, cy);
			fixMetrics(components[i], metrics);
			
			
			
			add(components[i], metrics);
		}
	}
	
	@Override
	public void add(Component comp, Object constraints) {
		//Fix up the constraints so that new components are still appended
		if (constraints instanceof GridBagConstraints) {
			GridBagConstraints grid = (GridBagConstraints) constraints;
			if (axis == Axis.VERTICAL) {
				nextCell = Math.max(nextCell, grid.gridy+grid.gridheight);
			} else {
				nextCell = Math.max(nextCell, grid.gridx+grid.gridwidth);
			}
		} else if (constraints instanceof LayoutElementMetrics) {
			LayoutElementMetrics metrics = (LayoutElementMetrics) constraints;
			if (axis == Axis.VERTICAL) {
				nextCell = Math.max(nextCell, metrics.cellY+metrics.cellsY);
			} else {
				nextCell = Math.max(nextCell, metrics.cellX+metrics.cellsX);
			}
		}
		
		super.add(comp, constraints);
	}
	
	public LayoutElementMetrics nextRow() {
		if (axis==Axis.VERTICAL) {
			return new LayoutElementMetrics(0,nextCell);
		} else {
			return new LayoutElementMetrics(nextCell, 0);
		}
	}
	
	private static void fixMetrics(Component comp, LayoutElementMetrics metrics) {
		//Dimension minSize = comp.getMinimumSize();
		Dimension naturalSize = comp.getPreferredSize();
		if (comp instanceof JTextArea) {
			naturalSize.width = 16;
		} else if (comp instanceof JPanel) {
			naturalSize.width = -1;
			naturalSize.height = -1;
		} else if (comp instanceof JIcon) {
			naturalSize.width = 300;
			naturalSize.height = 300;
		} else if (comp instanceof JLabel) {
			
		}
		//System.out.println("class: "+comp.getClass().getSimpleName()+" min: "+minSize+" preferred: "+naturalSize);
		int width = reconcileSize(naturalSize.width);
		int height = reconcileSize(naturalSize.height);
		
		metrics.fixedMinX = width;
		metrics.fixedMinY = height;
	}
	
	private static int reconcileSize(int preferred) {
		if (preferred==Short.MAX_VALUE) return -1;
		if (preferred==0) return -1;
		return preferred;
	}
}
