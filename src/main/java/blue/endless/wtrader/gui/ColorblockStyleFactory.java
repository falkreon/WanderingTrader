package blue.endless.wtrader.gui;

import javax.swing.JComponent;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthStyleFactory;

public class ColorblockStyleFactory extends SynthStyleFactory {
	private SynthStyle defaultStyle = new ColorblockStyle();
	
	@Override
	public SynthStyle getStyle(JComponent comp, Region region) {
		
		
		//System.out.println("GetStyle: "+comp.getClass().getCanonicalName());
		return defaultStyle;
	}
}
