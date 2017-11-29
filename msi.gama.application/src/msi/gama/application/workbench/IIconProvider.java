package msi.gama.application.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public interface IIconProvider {

	ImageDescriptor desc(String name);

	Image image(String name);

}
