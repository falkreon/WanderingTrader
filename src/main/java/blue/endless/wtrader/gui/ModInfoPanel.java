package blue.endless.wtrader.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import blue.endless.wtrader.ModInfo;
import blue.endless.wtrader.ModSelection;
import blue.endless.wtrader.Modpack;

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

	public ModInfoPanel(ModSelection item) {
		this.withAxis(Axis.VERTICAL);
		
		name.setFont(this.getFont().deriveFont(36.0f));
		
		this.addComponents(name, authors);
		this.addComponents(description);
		
		this.setPreferredSize(new Dimension(0, 128));
		
		setModItem(item);
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
	
	public void setModName(String modName) {
		this.name.setText(modName);
	}
	
	public void setModAuthors(String authors) {
		this.authors.setText(authors);
	}
	
	public void setDescription(String desc) {
		this.description.setText(desc);
	}
	
	public void setModItem(ModSelection item) {
		System.out.println("item: "+Jankson.builder().build().toJson(item).toJson(JsonGrammar.JSON5));
		System.out.println("cachedVersion: "+Jankson.builder().build().toJson(item.cachedVersion).toJson(JsonGrammar.JSON5));
		System.out.println("cachedInfo: "+Jankson.builder().build().toJson(item.info).toJson(JsonGrammar.JSON5));
		
		if (item.cachedVersion!=null) {
			this.name.setText(item.cachedVersion.fileName);
		}
		
		if (item.info==null) {
			this.name.setText(item.modCacheId);
		} else {
			this.mod = item.info;
			this.name.setText("XXXXXXXXXXXXXXXXXX");
			System.out.println("Setting name from CachedInfo");
			System.out.println(Thread.currentThread().getName());
			//this.name.setText(item.selection.cachedInfo.name);
			this.authors.setText(item.info.authors);
			this.description.setText(item.info.description);
			this.repaint();
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
