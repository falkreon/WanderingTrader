package blue.endless.wtrader.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import blue.endless.wtrader.ModInfo;
import blue.endless.wtrader.provider.ModProvider;

public class ModSelectPanel extends JPanel {
	private static final long serialVersionUID = 4312519726972935572L;
	
	JTabbedPane tabs = new JTabbedPane();
	JPanel cacheTab = new JPanel();
	DefaultListModel<ModInfo> curseModListModel = new DefaultListModel<>();
	JList<ModInfo> curseModList = new JList<>(curseModListModel);
	JButton curseAddModButton = new JButton("Add Mod");
	
	
	private static Jankson jankson = Jankson.builder().build();
	
	public ModSelectPanel() {
		//tabs.add(cacheTab, "Saved");
		tabs.add(createCurseTab(), "Curse");
		
		this.setLayout(new BorderLayout());
		this.add(tabs, BorderLayout.CENTER);
		
		//tabs.setSelectedIndex(1); //TODO: Only focus Curse tab if there's nothing in the cache
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
		
		//JList<ModInfo> curseModList = new JList<>(modList);
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
		
		
		/*
		ModInfo test = new ModInfo();
		test.name = "Xtones";
		test.provider = "curse";
		test.description = "An \"official\" port of Ztones";
		test.authors = "TehNut, _ForgeUser9211286";*/
		//modList.add(0, test);
		
		result.add(new JScrollPane(curseModList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		result.add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(new JPanel(), BorderLayout.CENTER);
		curseAddModButton = new JButton("Add to Pack");
		bottomPanel.add(curseAddModButton, BorderLayout.EAST);
		
		curseSearchButton.setAction(new AbstractAction("Search") {
			private static final long serialVersionUID = -3740855044287218120L;

			@Override
			public void actionPerformed(ActionEvent event) {
				List<ModInfo> results = ModProvider.get("curse").search(curseSearchField.getText(), null, null);
				
				curseModListModel.clear();
				int i = 0;
				for(ModInfo info : results) {
					System.out.println(jankson.toJson(info).toJson(JsonGrammar.JSON5));
					curseModListModel.add(i, info);
					i++;					
				}
				
			}
		});
		
		
		
		return result;
	}
}
