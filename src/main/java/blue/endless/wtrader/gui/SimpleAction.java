package blue.endless.wtrader.gui;

import java.awt.event.ActionEvent;
import java.util.function.BooleanSupplier;

import javax.swing.AbstractAction;

public class SimpleAction extends AbstractAction {
	private static final long serialVersionUID = 5086173072523962980L;
	
	private Runnable action;
	private BooleanSupplier enabledFunction;
	
	public SimpleAction(String name, Runnable r) {
		super(name);
		this.action = r;
	}
	
	public SimpleAction enableWhen(BooleanSupplier s) {
		enabledFunction = s;
		return this;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (action!=null) action.run();
	}
	
	public void checkEnabled() {
		if (enabledFunction!=null) {
			boolean shouldEnable = enabledFunction.getAsBoolean();
			if (shouldEnable ^ this.isEnabled()) {
				this.setEnabled(shouldEnable);
			}
		}
	}
}
