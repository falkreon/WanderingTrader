package blue.endless.wtrader.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.splinter.LayoutElementMetrics;
import blue.endless.wtrader.DependencyResolver;
import blue.endless.wtrader.ModInfo;
import blue.endless.wtrader.ModSelection;
import blue.endless.wtrader.Modpack;
import blue.endless.wtrader.loader.VoodooLoader;
import blue.endless.wtrader.provider.DirectModProvider;
import blue.endless.wtrader.provider.ModProvider;
import blue.endless.wtrader.provider.curse.CurseModProvider;

public class TraderGui extends JFrame {
	private static final long serialVersionUID = 3683901432454302841L;
	
	private Modpack pack;
	
	private DefaultListModel<ModSelection> modListModel = new DefaultListModel<>();
	private JList<ModSelection> modsList;
	
	private JComboBox<String> modLoaderMenu;
	private JComboBox<String> mcVersionMenu;
	private JComboBox<String> loaderVersionMenu;
	private JTextField packNameField = new JTextField();
	private JTextField packVersionField = new JTextField();
	private JTextArea authorsField = new JTextArea();
	private JTextArea descriptionField = new JTextArea();
	
	private JPanel cards = new JPanel(new CardLayout());
	
	static Image jarImage;
	static Image unknownImage;
	
	public TraderGui(Modpack pack) {
		this.pack = pack;
		//try {
			
			//SynthLookAndFeel laf = new SynthLookAndFeel();
			//UIManager.setLookAndFeel(laf);
			//SynthLookAndFeel.setStyleFactory(new ColorblockStyleFactory());
			
			
		//} catch (UnsupportedLookAndFeelException e) {
		//	e.printStackTrace ();
		//}
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(800,600));
		
		try {
			InputStream iconStream = TraderGui.class.getClassLoader().getResourceAsStream("icon.png");
			if (iconStream==null) {
				System.out.println("Unable to find resource");
			} else {
				BufferedImage icon = ImageIO.read(iconStream);
				this.setIconImage(icon);
			}
			
			//InputStream jarIconStream = TraderGui.class.getClassLoader().getResourceAsStream("jar.png");
			//if (jarIconStream!=null) jarImage = ImageIO.read(jarIconStream);
			
			//InputStream unknownIconStream = TraderGui.class.getClassLoader().getResourceAsStream("unknown.png");
			//if (unknownIconStream!=null) unknownImage = ImageIO.read(unknownIconStream);
			//jarImage = ImageIO.read(TraderGui.class.getResourceAsStream("jar.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//this.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
		//jarImage = this.getIconImage();
		//this.setIconImage(jarImage);
		
		this.setTitle("Wandering Trader");// - "+pack.getInfo().name+" - "+pack.getInfo().version);
		//this.setTitle("Wandering Trader - Center of the Multiverse - 11.2");
		
		//JPanel cards = new JPanel(new CardLayout());
		this.setContentPane(cards); //We could set our own layout but that screws up LaF applications because we apply LaF so late.
		
		LoadPanel loader = new LoadPanel();
		loader.onNewModpack = this::createModpack;
		loader.onOpenModpack = this::openModpack;
		cards.add(loader, "load");
		
		
		SplinterBox mainPanel = new SplinterBox().withAxis(Axis.HORIZONTAL);
		cards.add(mainPanel, "main");
		
		SplinterBox packInfo = new SplinterBox();
		packInfo.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
		packInfo.setPreferredSize(new Dimension(600, Integer.MAX_VALUE));
		packInfo.setMinimumSize(new Dimension(400, 400));
		JIcon packIcon = new JIcon(Toolkit.getDefaultToolkit().getImage("icon.png"));
		modLoaderMenu = new JComboBox<String>(new String[] {"fabric", "forge"});
		modLoaderMenu.setSelectedItem(pack.getInfo().modLoader);
		mcVersionMenu = new JComboBox<>(new String[] {"1.15.2", "1.15.1", "1.15", "1.14.4", "1.14.3", "1.14.2", "1.14.1", "1.14", "1.12.2", "1.12"});
		mcVersionMenu.setEditable(true);
		mcVersionMenu.setSelectedItem(pack.getInfo().mcVersion);
		loaderVersionMenu = new JComboBox<>(new String[] {"0.7.8+build.184", "0.7.8+build.187"});
		loaderVersionMenu.setEditable(true);
		loaderVersionMenu.setSelectedItem(pack.getInfo().loaderVersion);
		
		LayoutElementMetrics iconMetrics = new LayoutElementMetrics(0, 0);
		iconMetrics.cellsX = 2;
		iconMetrics.fixedMinX = 64;
		iconMetrics.fixedMinY = 64;
		packInfo.add(packIcon, iconMetrics);
		
		packInfo.addComponents(new JLabel("Pack Name:"),    packNameField);
		packInfo.addComponents(new JLabel("Pack Version:"), packVersionField);
		packInfo.addComponents(new JLabel("Mod Loader:"),   modLoaderMenu);
		packInfo.addComponents(new JLabel("MC Version:"),   mcVersionMenu);
		packInfo.addComponents(new JLabel("Loader Version:"), loaderVersionMenu);
		LayoutElementMetrics separatorMetrics = packInfo.nextRow();
		separatorMetrics.cellsX = 2;
		separatorMetrics.fixedMinY = new JSeparator().getPreferredSize().height;
		packInfo.add(new JSeparator(SwingConstants.HORIZONTAL), separatorMetrics);
		
		packInfo.addComponents(new JLabel("Pack Authors:"), authorsField);
		
		packInfo.addComponents(new JLabel("Pack Description:"), descriptionField);
		
		//packInfo.addComponents(new JButton("This is a test"));
		packInfo.addComponents(Box.createVerticalGlue());
		//packInfo.addComponents(new JPanel());
		
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
		
		syncMods();
		modsList = new JList<>(modListModel);
		modsList.setCellRenderer(new ModItemRenderer());
		modsList.setAlignmentX(Component.LEFT_ALIGNMENT);
		//modsList.setPreferredSize(new Dimension(Short.MAX_VALUE, 0));
		modsList.setMinimumSize(new Dimension(500, 0));
		modsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane modsScroller = new JScrollPane(modsList);
		modsScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		modsScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		contentsPanel.add(modsScroller);
		
		JButton addModButton = new JButton("Add Mod");
		addModButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentsPanel.add(addModButton);
		
		contentsPanel.add(Box.createRigidArea(new Dimension(0, 16)));
		
		
		
		//Removed till resource support is a thing
		/*
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
		*/
		
		//Removed till feature support is a thing
		/*
		JLabel featuresLabel = new JLabel("Features");
		featuresLabel.setFont(makeNormalFont(20.0).deriveFont(Font.BOLD));
		featuresLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentsPanel.add(featuresLabel);
		
		
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
		*/
		
		//JScrollPane contentsScroller = new JScrollPane(contentsPanel);
		
		//contentsScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//contentsScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		LayoutElementMetrics scrollPaneMetrics = mainPanel.nextRow();
		scrollPaneMetrics.fixedMinY = -1; //Take up any extra space you can
		//mainPanel.add(contentsScroller, scrollPaneMetrics);
		mainPanel.add(contentsPanel, scrollPaneMetrics);
		
		
		ModSelectPanel addModPanel = new ModSelectPanel();
		this.add(addModPanel, "addMod");
		
		
		addModButton.setAction(new AbstractAction("Add Mod") {
			private static final long serialVersionUID = -8970861151647527752L;

			@Override
			public void actionPerformed(ActionEvent event) {
				showCard("addMod");
				//((CardLayout) cards.getLayout()).show(cards, "addMod");
			}
		});
		
		addModPanel.curseAddModButton.setAction(new AbstractAction("Add Mod") {
			private static final long serialVersionUID = -4152159785202161582L;

			@Override
			public void actionPerformed(ActionEvent event) {
				ModInfo mod = addModPanel.curseModList.getSelectedValue();
				System.out.println("Adding "+mod.id+" to pack.");
				//Pick the right version for this pack
				String targetVersion = DependencyResolver.getMajorMinor("1.12.2");
				String targetLoader = "forge";
				ModInfo.Version bestVersion = null;
				for(ModInfo.Version cur : mod.versions) {
					
					if (DependencyResolver.getMajorMinor(cur.mcVersion).equals(targetVersion) && cur.loaders.contains(targetLoader)) {
						if (bestVersion==null || cur.timestamp>bestVersion.timestamp) bestVersion = cur;
					}
				}
				
				if (bestVersion!=null) {
					ModSelection selection = new ModSelection();
					selection.info = mod;
					selection.cachedVersion = bestVersion;
					
					selection.version = bestVersion.number;
					selection.constraint = ModSelection.Constraint.GREATER_THAN_OR_EQUAL;
					selection.timestamp = bestVersion.timestamp;
					selection.modCacheId = bestVersion.modId;
					
					pack.mods.add(selection);
					syncMods();
				} else {
					JOptionPane.showMessageDialog(TraderGui.this, "Can't find a version of "+mod.id+" that's compatible with this pack!");
				}
				showCard("main");
				//((CardLayout) cards.getLayout()).show(cards, "main");
			}
		});
		
	}
	
	public void showCard(String card) {
		((CardLayout) cards.getLayout()).show(cards, card);
	}
	
	private void syncMods() {
		modListModel.clear();
		for(ModSelection item : pack.mods) {
			modListModel.addElement(item);
		}
		if (modsList!=null) {
			modsList.invalidate();
			modsList.repaint();
		}
	}
	
	private static class ModItemRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 7603059254886882671L;
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (value instanceof ModSelection) {
				ModSelection item = (ModSelection) value;
				
				String modName = "";
				String version = "";
				if (item.info!=null) {
					//Use the filename reported by the ModProvider in its ModInfo
					modName = item.info.name;
				} else {
					//Derive a mod-name from the filename
					modName = item.cachedVersion.fileName;
					int hyphen = modName.indexOf('-');
					if (hyphen!=-1) {
						modName = modName.substring(0, hyphen);
					} else {
						if (modName.endsWith(".jar")) {
							modName = modName.substring(0, modName.length()-4);
						}
					}
				}
				
				if (item.cachedVersion!=null) {
					version = item.cachedVersion.number;
				}
				
				JLabel result = new JLabel(modName+" "+version);
				
				String fileName = "unknown";
				if (item.cachedVersion!=null) {
					fileName = item.cachedVersion.fileName;
				}
				String extension = "";
				if (fileName.lastIndexOf('.')!=-1) extension = fileName.substring(fileName.lastIndexOf('.')+1);
				
				result.setIcon(new ImageIcon(FileIcons.getIcon(extension)));
				
				
				//if (((Modpack.ModItem) value).selection.cachedVersion.fileName.endsWith(".jar")) {
				//	result.setIcon(new ImageIcon(TraderGui.jarImage));
				//}
				result.setFocusable(true);
				if (cellHasFocus) {
					result.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
					result.setOpaque(true);
					result.setBackground(new Color(220, 220, 255));
				} else {
					result.setBorder(null);
				}
				
				result.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						System.out.println("Focused!");
						result.requestFocusInWindow();
					}
				});
				
				result.setPreferredSize(new Dimension(500, 64));
				result.setMaximumSize(new Dimension(500, 64));
				
				return result;
			} else {
				System.out.println("DEFAULTED");
				JLabel result = new JLabel(value.toString());
				//result.setSize(500, 64);
				Dimension d = new Dimension(500, 64);
				result.setPreferredSize(d);
				result.setMaximumSize(d);
				return result;
			}
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
	
	public static class ModItemView extends JPanel {
		private static final long serialVersionUID = 3064929838898888378L;
		
		public JLabel fileItem = new JLabel();
		
		public ModItemView() {
			this.setBackground(Color.WHITE);
			this.setOpaque(true);
		}
		
		public void setMod(ModSelection item) {
			
			String modName = "";
			if (item.info!=null) {
				//Use the filename reported by the ModProvider in its ModInfo
				modName = item.info.name;
			} else {
				//Derive a mod-name from the filename
				modName = item.cachedVersion.fileName;
				int hyphen = modName.indexOf('-');
				if (hyphen!=-1) {
					modName = modName.substring(0, hyphen);
				} else {
					if (modName.endsWith(".jar")) {
						modName = modName.substring(0, modName.length()-4);
					}
				}
			}
			
			String fileName = item.cachedVersion.fileName;
			String extension = "";
			if (fileName.lastIndexOf('.')!=-1) extension = fileName.substring(fileName.lastIndexOf('.')+1);
			
			fileItem.setIcon(new ImageIcon(FileIcons.getIcon(extension)));
			/*
			if (item.selection.cachedVersion.fileName.endsWith(".jar")) {
				fileItem.setIcon(new ImageIcon(TraderGui.jarImage));
			} else {
				fileItem.setIcon(new ImageIcon(TraderGui.unknownImage));
			}*/
		}
		
		public void setComment(String comment) {
			fileItem.setForeground(new Color(100, 180, 100));
			fileItem.setFont(fileItem.getFont().deriveFont(16.0f).deriveFont(Font.ITALIC));
			fileItem.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));
			fileItem.setText(comment);
		}
	}
	
	public void loadPack(Modpack pack) {
		this.pack = pack;
		packNameField.setText(pack.getInfo().name);
		packVersionField.setText(pack.getInfo().version);
		modLoaderMenu.setSelectedItem(pack.getInfo().modLoader);
		mcVersionMenu.setSelectedItem(pack.getInfo().mcVersion);
		loaderVersionMenu.setSelectedItem(pack.getInfo().loaderVersion);
		
		authorsField.setText(pack.getInfo().authors);
		descriptionField.setText(pack.getInfo().description);
		
		syncMods();
		showCard("main");
	}
	
	private void createModpack(File f) {
		//if (!f.exists()) f.mkdirs();
		
		pack = new Modpack();
		pack.packInfo.name = f.getName();
		pack.packInfo.version = "1.0";
		pack.setSaveLocation(f);
		
		this.loadPack(pack);
		showCard("main");
	}
	
	private void openModpack(File f) {
		if (f.isDirectory()) {
			//TODO: Look for identifying files
			
			
		} else {
			if (f.getName().endsWith(".lock.pack.hjson") || f.getName().endsWith(".lock.pack.json")) { //Voodoo pack lockfile
				try {
					
					Modpack pack = VoodooLoader.load(f);
					pack.setSaveLocation(null);
					
					this.loadPack(pack);
					showCard("main");
					pack.saveAs(new File(".", "pack_import.json"));
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (f.getName().endsWith(".json")) {
				try {
					Jankson jankson = Jankson.builder().build();
					
					Modpack pack = jankson.fromJson(jankson.load(f), Modpack.class);
					pack.setSaveLocation(f);
					
					//Re-cache versions
					for(ModSelection selection : pack.mods) {
						//if (modItem.selection==null) continue;
						
						ModInfo modInfo = selection.info;
						for(ModInfo.Version version : modInfo.versions) {
							if (version.timestamp==selection.timestamp) {
								selection.cachedVersion = version;
								break;
							}
						}
					}
					
					//System.out.println("Modpack: "+jankson.toJson(pack).toJson(JsonGrammar.JSON5));
					
					this.loadPack(pack);
					showCard("main");
				} catch (IOException | SyntaxError error) {
					error.printStackTrace();
				}
			}
		}
	}
}
