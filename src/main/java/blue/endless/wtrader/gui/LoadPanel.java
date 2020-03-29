package blue.endless.wtrader.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class LoadPanel extends JPanel {
	private static final long serialVersionUID = 554656518045089639L;
	
	public Consumer<File> onNewModpack;
	public Consumer<File> onOpenModpack;
	
	
	public LoadPanel() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalGlue());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		add(buttonPanel);
		
		add(Box.createVerticalGlue());
		
		
		JButton createButton = new JButton("Create New Modpack");
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("."));
				chooser.setDialogTitle("Create New Modpack");
				chooser.setApproveButtonText("Create");
				
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent fileEvent) {
						if (fileEvent.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
							//System.out.println("Selected: "+test.getSelectedFile());
							if (onNewModpack!=null) onNewModpack.accept(chooser.getSelectedFile());
						}
					}
				});
				
				chooser.showSaveDialog(LoadPanel.this);
			}
		});
		
		JButton loadButton = new JButton("Open / Import Modpack");
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Open Modpack");
				//newChooser.setApproveButtonText("Open");
				
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setCurrentDirectory(new File("."));
				chooser.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent fileEvent) {
						if (fileEvent.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
							System.out.println("Selected: "+chooser.getSelectedFile());
							if (onOpenModpack!=null) onOpenModpack.accept(chooser.getSelectedFile());
						}
					}
				});
				
				chooser.showOpenDialog(LoadPanel.this);
			}
		});
		
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(createButton);
		buttonPanel.add(Box.createHorizontalStrut(64));
		buttonPanel.add(loadButton);
		buttonPanel.add(Box.createHorizontalGlue());
	}
}
