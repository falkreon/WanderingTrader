package blue.endless.wtrader.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.time.Instant;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import blue.endless.splinter.LayoutElementMetrics;
import blue.endless.wtrader.ModInfo;

public class TraderGui extends JFrame {
	private static final long serialVersionUID = 3683901432454302841L;

	public TraderGui() {
		
		//try {
			
			//SynthLookAndFeel laf = new SynthLookAndFeel();
			//UIManager.setLookAndFeel(laf);
			//SynthLookAndFeel.setStyleFactory(new ColorblockStyleFactory());
			
			
		//} catch (UnsupportedLookAndFeelException e) {
		//	e.printStackTrace ();
		//}
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(300,300));
		
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
		
		this.setTitle("Wandering Trader - Center of the Multiverse - 11.2");
		
		JPanel cards = new JPanel(new CardLayout());
		this.setContentPane(cards); //We could set our own layout but that screws up LaF applications because we apply LaF so late.
		SplinterBox mainPanel = new SplinterBox().withAxis(Axis.HORIZONTAL);
		cards.add(mainPanel, "main");
		
		SplinterBox packInfo = new SplinterBox();
		packInfo.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
		packInfo.setPreferredSize(new Dimension(600, Integer.MAX_VALUE));
		packInfo.setMinimumSize(new Dimension(400, 400));
		JIcon packIcon = new JIcon(Toolkit.getDefaultToolkit().getImage("icon.png"));
		JComboBox<String> modLoaderMenu = new JComboBox<String>(new String[] {"Fabric", "Forge"});
		JComboBox<String> mcVersionMenu = new JComboBox<>(new String[] {"1.15.2", "1.15.1", "1.15", "1.14.4", "1.14.3", "1.14.2", "1.14.1", "1.14"});
		JComboBox<String> loaderVersionMenu = new JComboBox<>(new String[] {"0.7.8+build.184"});
		
		LayoutElementMetrics iconMetrics = new LayoutElementMetrics(0, 0);
		iconMetrics.cellsX = 2;
		iconMetrics.fixedMinX = 64;
		iconMetrics.fixedMinY = 64;
		packInfo.add(packIcon, iconMetrics);
		
		packInfo.addComponents(new JLabel("Pack Name:"),    new JTextField("Center of the Multiverse"));
		packInfo.addComponents(new JLabel("Pack Version:"), new JTextField("11.2"));
		packInfo.addComponents(new JLabel("Mod Loader:"),   modLoaderMenu);
		packInfo.addComponents(new JLabel("MC Version:"),   mcVersionMenu);
		packInfo.addComponents(new JLabel("Loader Version:"), loaderVersionMenu);
		LayoutElementMetrics separatorMetrics = packInfo.nextRow();
		separatorMetrics.cellsX = 2;
		separatorMetrics.fixedMinY = new JSeparator().getPreferredSize().height;
		packInfo.add(new JSeparator(SwingConstants.HORIZONTAL), separatorMetrics);
		
		packInfo.addComponents(new JLabel("Pack Authors:"), new JTextArea("AnsuzThurisaz\nOther People"));
		
		packInfo.addComponents(new JLabel("Pack Description:"), new JTextArea("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla maximus diam at libero tincidunt, quis dapibus est scelerisque. Fusce vel felis eleifend, egestas nisl nec, volutpat elit. Curabitur congue lectus tincidunt elementum viverra."));
		
		packInfo.addComponents(new JButton("This is a test"));
		
		packInfo.addComponents(new JPanel());
		//modLoaderMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
		//mcVersionMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		LayoutElementMetrics packInfoMetrics = mainPanel.nextRow();
		packInfoMetrics.fixedMinX = 300;
		mainPanel.add(packInfo, packInfoMetrics);
		
		mainPanel.addComponents(new JSeparator(SwingConstants.VERTICAL));
		
		JPanel contentsPanel = new JPanel();
		contentsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentsPanel.setLayout(new BoxLayout(contentsPanel, BoxLayout.Y_AXIS));
		
		
		JLabel packContentsLabel = new JLabel("Pack Contents");
		packContentsLabel.setFont(makeNormalFont(24.0).deriveFont(Font.BOLD));
		packContentsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		//packContentsLabel.setFont(packContentsLabel.getFont().deriveFont(Font.BOLD, packContentsLabel.getFont().getSize()+8));
		contentsPanel.add(packContentsLabel);
		
		JLabel modsLabel = new JLabel("Mods");
		modsLabel.setFont(makeNormalFont(20.0).deriveFont(Font.BOLD));
		modsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentsPanel.add(modsLabel);
		
		/*
		String[] columnNames = new String[] { "Name", "Version", "Side", "Category", "Author(s)", "Project Link" };
		Object[][] tableCells = new Object[1][6];
		tableCells[0][0] = "Thermal Expansion";
		tableCells[0][1] = "1.0";
		tableCells[0][2] = "Both";
		tableCells[0][3] = "Technology";
		tableCells[0][4] = "Team CoFH";
		tableCells[0][5] = "https://www.curseforge.com/minecraft/mc-mods/thermal-expansion";
		JTable modsTable = new JTable(tableCells, columnNames);*/
		
		ListTableModel<ModInfo.Version> modsTableModel = new ListTableModel<>();
		JTable modsTable = new JTable(modsTableModel);
		modsTableModel.addColumn("Name", it->it.modId);
		modsTableModel.addColumn("Version", it->it.number);
		modsTableModel.addColumn("Released", it->Instant.ofEpochMilli(it.timestamp).toString());
		modsTableModel.addColumn("Loaders", it->it.loaders.toString());
		
		modsTable.setAlignmentX(Component.LEFT_ALIGNMENT);
		modsTable.getTableHeader().setAlignmentX(Component.LEFT_ALIGNMENT);
		contentsPanel.add(modsTable.getTableHeader());
		contentsPanel.add(modsTable);
		
		JButton addModButton = new JButton("Add Mod");
		addModButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentsPanel.add(addModButton);
		
		contentsPanel.add(Box.createRigidArea(new Dimension(0, 16)));
		
		
		JLabel resourcesLabel = new JLabel("Resources");
		resourcesLabel.setFont(makeNormalFont(20.0).deriveFont(Font.BOLD));
		resourcesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentsPanel.add(resourcesLabel);
		
		String[] resourceColumnNames = new String[] { "Name", "Version", "Author(s)", "Project Link" };
		Object[][] resourceCells = new Object[][] {
			{ "SPHax Even More Awful Edition", "12.2", "Some Douche", "https://example.com/" }
		};
		JTable resourcesTable = new JTable(resourceCells, resourceColumnNames);
		resourcesTable.setAlignmentX(Component.LEFT_ALIGNMENT);
		resourcesTable.getTableHeader().setAlignmentX(Component.LEFT_ALIGNMENT);
		contentsPanel.add(resourcesTable.getTableHeader());
		contentsPanel.add(resourcesTable);
		
		JButton addResourceButton = new JButton("Add Resource Pack");
		addResourceButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentsPanel.add(addResourceButton);
		
		contentsPanel.add(Box.createRigidArea(new Dimension(0, 16)));
		
		
		JLabel featuresLabel = new JLabel("Features");
		featuresLabel.setFont(makeNormalFont(20.0).deriveFont(Font.BOLD));
		featuresLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentsPanel.add(featuresLabel);
		
		//String[] featureColumnNames = new String[] { "Name", "Version", "Reccommended", "Description", "Author(s)", "Project Link" };
		//Object[][] featureCells = new Object[][] {
		//	{ "LLOverlay Reloaded", "6", "[ NYI ]", "Adds a light level overlay", "Someone", "https://example.com/" }
		//};
		ListTableModel<ModInfo.Version> featuresModel = new ListTableModel<>();
		featuresModel.addColumn("Name", (it)->it.modId);
		featuresModel.addColumn("Version", it->it.number);
		featuresModel.addColumn("Reccommended", it->new JCheckBox()); //TODO: This doesn't work because the TableModel reports its class as Object
		ModInfo.Version dummy = new ModInfo.Version();
		dummy.modId = "asdfasdf";
		dummy.number = "1.0";
		featuresModel.addRow(dummy);
		
		JTable featuresTable = new JTable(featuresModel);
		//JTable featuresTable = new JTable(featureCells, featureColumnNames);
		featuresTable.setAlignmentX(Component.LEFT_ALIGNMENT);
		featuresTable.getTableHeader().setAlignmentX(Component.LEFT_ALIGNMENT);
		contentsPanel.add(featuresTable.getTableHeader());
		contentsPanel.add(featuresTable);
		
		JButton addFeatureButton = new JButton("Add Feature");
		addFeatureButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentsPanel.add(addFeatureButton);
		
		
		
		JScrollPane contentsScroller = new JScrollPane(contentsPanel);
		//contentsScroller.setMinimumSize(new Dimension(300, 16));
		
		contentsScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contentsScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		LayoutElementMetrics scrollPaneMetrics = mainPanel.nextRow();
		scrollPaneMetrics.fixedMinY = -1; //Take up any extra space you can
		mainPanel.add(contentsScroller, scrollPaneMetrics);
		
		
		
		ModSelectPanel addModPanel = new ModSelectPanel();
		this.add(addModPanel, "addMod");
		
		
		addModButton.setAction(new AbstractAction("Add Mod") {
			private static final long serialVersionUID = -8970861151647527752L;

			@Override
			public void actionPerformed(ActionEvent event) {
				((CardLayout) cards.getLayout()).show(cards, "addMod");
			}
		});
		
		addModPanel.curseAddModButton.setAction(new AbstractAction("Add Mod") {
			private static final long serialVersionUID = -4152159785202161582L;

			@Override
			public void actionPerformed(ActionEvent event) {
				ModInfo mod = addModPanel.curseModList.getSelectedValue();
				System.out.println("Adding "+mod.id+" to pack.");
				//Pick the right version for this pack
				String targetVersion = "1.12.2";
				String targetLoader = "forge";
				ModInfo.Version bestVersion = null;
				for(ModInfo.Version cur : mod.versions) {
					if (cur.mcVersion.equals(targetVersion) && cur.loaders.contains(targetLoader)) {
						if (bestVersion==null || cur.timestamp>bestVersion.timestamp) bestVersion = cur;
					}
				}
				
				if (bestVersion!=null) {
					modsTableModel.addRow(bestVersion);
				} else {
					JOptionPane.showMessageDialog(TraderGui.this, "Can't find a version of "+mod.id+" that's compatible with this pack!");
				}
			}
			
		});
		/*
		JPanel searchBar = new JPanel();
		//searchBar.setBackground(new Color(0x110022));
		searchBar.setLayout(new BorderLayout());
		JTextField searchField = new JTextField();
		searchField.setEditable(true);
		//searchField.setBackground(new Color(0x110022));
		//searchField.setForeground(new Color(0xEEDDFF));
		searchBar.add(searchField, BorderLayout.CENTER);
		JButton searchButton = new JButton("Search");
		//searchButton.setBackground(Color.BLUE);
		//searchButton.setForeground(Color.WHITE);
		//searchButton.setMinimumSize(new Dimension(100, 32));
		searchBar.add(searchButton, BorderLayout.EAST);
		this.getContentPane().add(searchBar, BorderLayout.NORTH);*/
	}
	
	private static class GridBuilder {
		private GridBagConstraints result = new GridBagConstraints();
		private GridBuilder(int x, int y) {
			result.gridx = x;
			result.gridy = y;
			result.ipadx = 2;
			result.ipady = 2;
			result.insets = new Insets(2,2,2,2);
			result.fill = GridBagConstraints.BOTH;
		}
		
		
		public GridBuilder weightX(int weight) {
			result.weightx = weight;
			return this;
		}
		
		public GridBuilder weightY(int weight) {
			result.weighty = weight;
			return this;
		}
		
		public GridBuilder span(int x, int y) {
			result.gridwidth = x;
			result.gridheight = y;
			return this;
		}
		
		public GridBuilder center() {
			result.fill = GridBagConstraints.NONE;
			result.anchor = GridBagConstraints.CENTER;
			return this;
		}
		
		public GridBagConstraints build() {
			return result;
		}
		
		public static GridBuilder at(int x, int y) {
			return new GridBuilder(x, y);
		}
	}
	
	private static Font makeNormalFont(double pointSize) {
		String[] fontPreferences = { "Liberation Sans", "Ubuntu", "Arial", Font.SANS_SERIF };
		
		for(String preferred : fontPreferences) {
			for(String s : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
				if (s.equals(preferred)) {
					//System.out.println("Selected "+s+" as display font.");
					return sizeFontForDisplay(new Font(s, Font.PLAIN, (int)pointSize), pointSize);
				}
				//System.out.println(s);
			}
		}
		
		//System.out.println("Selected Font.SANS_SERIF as display font.");
		return sizeFontForDisplay(new Font(Font.SANS_SERIF, Font.PLAIN, (int)pointSize), pointSize);
	}
	
	private static Font sizeFontForDisplay(Font f, double points) {
		int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		//System.out.println("DPI: "+dpi);
		if (dpi<72) dpi=72;
		
		int scaledSize = (int) ( points * (dpi/72.0) );
		return f.deriveFont(scaledSize);
	}
}
