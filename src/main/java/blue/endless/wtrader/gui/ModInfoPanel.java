package blue.endless.wtrader.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import blue.endless.wtrader.ModInfo;

public class ModInfoPanel extends SplinterBox {
	private static final long serialVersionUID = -1062820173404886327L;
	private ModInfo mod;
	
	private JLabel name = new JLabel();
	private JLabel authors = new JLabel();
	private JLabel description = new JLabel();
	
	public ModInfoPanel(ModInfo mod) {
		this.withAxis(Axis.VERTICAL);
		
		name.setFont(this.getFont().deriveFont(36.0f));
		
		this.addComponents(name, authors);
		this.addComponents(description);
		
		this.setPreferredSize(new Dimension(0, 128));
		
		setMod(mod);
	}

	public void setSelected(boolean isSelected) {
		if (isSelected) {
			this.setBackground(new Color(230, 230, 255));
		} else {
			this.setBackground(Color.WHITE);
		}
	}

	public void setFocused(boolean cellHasFocus) {
		if (cellHasFocus) {
			this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		} else {
			this.setBorder(null);
		}
	}
	
	public ModInfo getMod() {
		return this.mod;
	}
	
	public void setMod(ModInfo mod) {
		this.mod = mod;
		
		name.setText(mod.name);
		authors.setText(mod.authors);
		description.setText(mod.description);
	}
}
