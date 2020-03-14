package blue.endless.wtrader.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.wtrader.ModInfo;
import blue.endless.wtrader.RestQuery;
import blue.endless.wtrader.provider.curse.CurseModInfo;

public class ModSelectPanel extends JPanel {
	private static final long serialVersionUID = 4312519726972935572L;
	
	JTabbedPane tabs = new JTabbedPane();
	JPanel cacheTab = new JPanel();
	DefaultListModel<ModInfo> modList = new DefaultListModel<>();
	
	private static Jankson jankson = Jankson.builder().build();
	
	public ModSelectPanel() {
		tabs.add(cacheTab, "Saved");
		tabs.add(createCurseTab(), "Curse");
		
		this.setLayout(new BorderLayout());
		this.add(tabs, BorderLayout.CENTER);
		
		tabs.setSelectedIndex(1); //TODO: Only focus Curse tab if there's nothing in the cache
	}
	
	private JPanel createCurseTab() {
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());
		
		JPanel curseSearchPanel = new JPanel();
		curseSearchPanel.setLayout(new BorderLayout());
		JTextField curseSearchField = new JTextField();
		curseSearchPanel.add(curseSearchField, BorderLayout.CENTER);
		JButton curseSearchButton = new JButton("Search Curse");
		curseSearchPanel.add(curseSearchButton, BorderLayout.EAST);
		result.add(curseSearchPanel, BorderLayout.NORTH);
		
		JList<ModInfo> curseModList = new JList<>(modList);
		curseModList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 7603059254886882671L;
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				if (value instanceof ModInfo) {
					ModInfoPanel panel = new ModInfoPanel((ModInfo) value);
					panel.setSelected(isSelected);
					panel.setFocused(cellHasFocus);
					return panel;
				} else {
					return new JLabel(value.toString());
				}
			}
		});
		
		
		
		ModInfo test = new ModInfo();
		test.name = "Xtones";
		test.provider = "curse";
		test.description = "An \"official\" port of Ztones";
		test.authors = "TehNut, _ForgeUser9211286";
		modList.add(0, test);
		
		result.add(curseModList, BorderLayout.CENTER);
		
		curseSearchButton.setAction(new AbstractAction("Search") {
			private static final long serialVersionUID = -3740855044287218120L;

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					JsonElement result = RestQuery.start("https://addons-ecs.forgesvc.net/api/v2/addon/search?gameId=432&searchFilter="+curseSearchField.getText());
					if (result instanceof JsonArray) {
						JsonArray results = (JsonArray) result;
						for(JsonElement elem : results) {
							if (elem instanceof JsonObject) {
								ModInfo info = jankson.fromJson((JsonObject) elem, CurseModInfo.class).toModInfo();
								System.out.println(jankson.toJson(info).toJson(JsonGrammar.JSON5));
							}
						}
					}
					
					//System.out.println(result);
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
				
			}
		});
		
		return result;
	}
}
