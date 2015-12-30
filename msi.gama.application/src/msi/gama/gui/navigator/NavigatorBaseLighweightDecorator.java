/**
 * Created by drogoul, 11 févr. 2015
 *
 */
package msi.gama.gui.navigator;

import org.eclipse.jface.viewers.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.util.file.IGamaFileMetaData;

/**
 * Class NavigatorBaseLighweightDecorator.
 *
 * @author drogoul
 * @since 11 févr. 2015
 *
 */
public class NavigatorBaseLighweightDecorator implements ILightweightLabelDecorator {

	static final public String ID = "msi.gama.application.decorator";

	@Override
	public void decorate(final Object element, final IDecoration decoration) {
		if ( SwtGui.NAVIGATOR_METADATA.getValue() ) {
			IGamaFileMetaData data = FileMetaDataProvider.getInstance().getMetaData(element, false);
			if ( data == null ) { return; }
			String suffix = data.getSuffix();
			if ( suffix != null && !suffix.isEmpty() ) {
				decoration.addSuffix(" (" + suffix + ")");
			}
			// Object thumbnail = data.getThumbnail();
			// if ( thumbnail != null && thumbnail instanceof ImageDescriptor ) {
			// replaceImage(decoration, (ImageDescriptor) thumbnail);
			// }
		}
	}

	@Override
	public void addListener(final ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return true;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	// private void replaceImage(final IDecoration decoration, final ImageDescriptor thumbnail) {
	// IDecoratorManager mgr = PlatformUI.getWorkbench().getDecoratorManager();
	// boolean doHack =
	// mgr.getBaseLabelProvider("org.eclipse.ui.ContentTypeDecorator") != null &&
	// mgr.getEnabled("org.eclipse.ui.ContentTypeDecorator");
	// if ( !doHack && DecorationContext.DEFAULT_CONTEXT instanceof DecorationContext ) {
	// ((DecorationContext) DecorationContext.DEFAULT_CONTEXT).putProperty(IDecoration.ENABLE_REPLACE,
	// Boolean.TRUE);
	// decoration.addOverlay(thumbnail, IDecoration.REPLACE);
	// } else {
	// decoration.addOverlay(thumbnail, IDecoration.TOP_LEFT);
	// }
	// }
}
