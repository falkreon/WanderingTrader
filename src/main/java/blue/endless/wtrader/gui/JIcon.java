package blue.endless.wtrader.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

import org.checkerframework.checker.nullness.qual.Nullable;

public class JIcon extends JComponent {
	private static final long serialVersionUID = 8511361780756320216L;
	
	private Image image;
	private int fixedSize = 128;
	
	public JIcon() {
		
	}
	
	public JIcon(Image image) {
		this.image = image;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	@Nullable
	public Image getImage() {
		return this.image;
	}
	
	public JIcon setSize(int sz) {
		this.fixedSize = sz;
		return this;
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(fixedSize, fixedSize);
		
		//if (image==null) return new Dimension(16, 16);
		
		//int sz = Math.max(image.getWidth(this), image.getHeight(this));
		//if (sz<16) sz=16;
		//return new Dimension(sz, sz);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	
	@Override
	public Dimension getMaximumSize() {
		return getMinimumSize();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if (image!=null) {
			Dimension d = this.getSize();
			//int sz = Math.min(d.width, d.height);
			//if (sz>fixedSize) sz=fixedSize;
			int sz = fixedSize;
			int xofs = (d.width-sz) / 2;
			int yofs = (d.height-sz) / 2;
			//TODO: Insets
			g.drawImage(image, xofs, yofs, sz, sz, this);
		}
	}
}
