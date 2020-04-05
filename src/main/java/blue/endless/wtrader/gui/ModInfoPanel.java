package blue.endless.wtrader.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

import blue.endless.splinter.LayoutElementMetrics;
import blue.endless.wtrader.ModInfo;
import blue.endless.wtrader.ModSelection;

public class ModInfoPanel extends SplinterBox {
	private static final long serialVersionUID = -1062820173404886327L;
	private ModInfo mod;
	
	private JIcon icon = new JIcon().setSize(64);
	private JTextArea name = makeLabel();
	private JTextArea authors = makeLabel();
	private JTextArea description = makeLabel();
	
	private ModInfoPanel() {
		this.withAxis(Axis.HORIZONTAL);
		
		icon.setBorder(null);
		
		name.setFont(this.getFont().deriveFont(14.0f).deriveFont(Font.BOLD));
		name.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 1));
		
		authors.setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 1));
		
		description.setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 2));
		
		LayoutElementMetrics iconConstraints = new LayoutElementMetrics(0,0);
		iconConstraints.fixedMinX = 64;
		iconConstraints.fixedMinY = 64;
		this.add(icon, iconConstraints);
		
		LayoutElementMetrics nameConstraints = new LayoutElementMetrics(1,0);
		nameConstraints.fixedMinX = 300;
		this.add(name, nameConstraints);//, constraints);
		LayoutElementMetrics authorsConstraints = new LayoutElementMetrics(2,0);
		authorsConstraints.fixedMinX = 200;
		this.add(authors, authorsConstraints);
		LayoutElementMetrics descriptionConstraints = new LayoutElementMetrics(3,0);
		this.add(description);
		
		//TODO: Control buttons
		
		Dimension d = new Dimension(256, 64);
		this.setPreferredSize(d);
		this.setMinimumSize(d);
		this.setMaximumSize(d);
	}
	
	
	public ModInfoPanel(ModInfo mod) {
		this();
		
		setMod(mod);
		
		if (!mod.versions.isEmpty()) {
			setFile(mod.versions.get(0).fileName);
		}
	}

	public ModInfoPanel(ModSelection item) {
		this();
		
		setModItem(item);
		if (item.cachedVersion==null) {
			if (!item.info.versions.isEmpty()) {
				//Something went wrong, but we have some version info saved. Try to hook up the cachedVersion again
				for (ModInfo.Version version : item.info.versions) {
					if (version.timestamp==item.timestamp || version.number.equals(item.version)) {
						item.cachedVersion = version;
						break;
					}
				}
			}
		}
		
		if (item.cachedVersion!=null) setFile(item.cachedVersion.fileName);
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
		//System.out.println("item: "+Jankson.builder().build().toJson(item).toJson(JsonGrammar.JSON5));
		//System.out.println("cachedVersion: "+Jankson.builder().build().toJson(item.cachedVersion).toJson(JsonGrammar.JSON5));
		//System.out.println("cachedInfo: "+Jankson.builder().build().toJson(item.info).toJson(JsonGrammar.JSON5));
		
		if (item.cachedVersion!=null) {
			this.name.setText(item.cachedVersion.fileName);
		}
		
		if (item.info==null) {
			this.name.setText(item.modCacheId);
		} else {
			this.mod = item.info;
			this.name.setText("XXXXXXXXXXXXXXXXXX");
			System.out.println("Setting name from CachedInfo");
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
	
	public void setFile(String fileName) {
		if (fileName==null || fileName.trim().isEmpty()) return;
		String extension = "";
		if (fileName.lastIndexOf('.')!=-1) extension = fileName.substring(fileName.lastIndexOf('.')+1);
		
		icon.setImage(FileIcons.getIcon(extension));
		//name.setIcon(new ImageIcon(FileIcons.getIcon(extension)));
	}
	
	private static JTextArea makeLabel() {
		JTextArea result = new JTextArea();
		result.setBackground(null);
		result.setEditable(false);
		result.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		result.setLineWrap(true);
		result.setWrapStyleWord(true);
		result.setFocusable(false);
		result.setRows(2);
		
		return result;
	}
}
