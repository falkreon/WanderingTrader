package blue.endless.wtrader.gui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import blue.endless.wtrader.Progress;

public class ProgressPanel extends JPanel {
	private static final long serialVersionUID = 1671296458693221184L;
	
	private JLabel message = new JLabel();
	private JProgressBar progressBar = new JProgressBar();
	
	public ProgressPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalGlue());
		
		Dimension d = new Dimension(600, 48);
		
		message.setAlignmentX(CENTER_ALIGNMENT);
		message.setMinimumSize(d);
		message.setPreferredSize(d);
		message.setMaximumSize(d);
		
		progressBar.setAlignmentX(CENTER_ALIGNMENT);
		progressBar.setMinimumSize(d);
		progressBar.setPreferredSize(d);
		progressBar.setMaximumSize(d);
		
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setValue(0);
		
		add(message);
		add(progressBar);
		
		add(Box.createVerticalGlue());
	}
	
	public void accept(Progress p) {
		message.setText(p.msg);
		progressBar.setMinimum(p.min);
		progressBar.setMaximum(p.max);
		progressBar.setValue(p.value);
		this.paint(this.getGraphics());
	}
}
