package msi.gama.gui.swt.commands;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.quickaccess.QuickAccessElement;
import org.eclipse.ui.internal.quickaccess.QuickAccessProvider;

public class GamaQuickAccessProvider extends QuickAccessProvider {

	class GamaQuickAccessElement extends QuickAccessElement {

		public GamaQuickAccessElement(final QuickAccessProvider provider) {
			super(provider);
		}

		@Override
		public String getLabel() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void execute() {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public String getId() {
		return "gama.search.models";
	}

	@Override
	public String getName() {
		return "Models";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public QuickAccessElement[] getElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuickAccessElement getElementForId(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doReset() {
		// TODO Auto-generated method stub

	}

}
