/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.navigator;

import java.util.Hashtable;
import msi.gama.gui.swt.Activator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.navigator.IDescriptionProvider;

public class NavigatorLabelProvider extends LabelProvider implements IDescriptionProvider {

	/* Cached icons */
	private final Hashtable<Program, Image> iconCache = new Hashtable<Program, Image>();

	@Override
	public String getText(final Object element) {
		if ( element instanceof VirtualProjectFolder ) { return ((VirtualProjectFolder) element)
			.getName() + " ( " + ((VirtualProjectFolder) element).getChildren().length + " )"; }
		if ( element instanceof VirtualModelsFolder ) { return ((VirtualModelsFolder) element)
			.getName() + " ( " + ((VirtualModelsFolder) element).getChildren().length + " )"; }
		if ( element instanceof VirtualSharedModelsFolder ) { return ((VirtualSharedModelsFolder) element)
			.getName() + " ( " + ((VirtualSharedModelsFolder) element).getChildren().length + " )"; }
		if ( element instanceof FileBean ) {
			String name = ((FileBean) element).toString();
			return name.substring(0, name.lastIndexOf("."));
		}
		// } if (element instanceof File) {
		// String name = ((File) element).getName();
		// return name.substring(0, name.lastIndexOf("."));
		// }
		return null;
	}

	@Override
	public Image getImage(final Object element) {
		Image image = null;

		if ( element instanceof VirtualSharedModelsFolder ) {
			ImageDescriptor desc = Activator.getImageDescriptor("icons/folder_library.png");
			image = desc.createImage();
		} else if ( element instanceof VirtualProjectFolder ) {
			ImageDescriptor desc = Activator.getImageDescriptor("icons/folder_workspace.png");
			image = desc.createImage();
		} else if ( element instanceof VirtualModelsFolder ) {
			ImageDescriptor desc = Activator.getImageDescriptor("icons/folder_samples.png");
			image = desc.createImage();
		} else if ( element instanceof FileBean ) {
			if ( ((FileBean) element).hasChildren() ) {
				image =
					PlatformUI.getWorkbench().getSharedImages()
						.getImage(ISharedImages.IMG_OBJ_FOLDER);
			} else {
				FileBean file = (FileBean) element;
				String nameString = file.toString();
				/* Get icon from the fileSystem */
				int dot = nameString.lastIndexOf('.');
				if ( dot != -1 ) {
					/* Find the program using the file extension */
					String extension = nameString.substring(dot);
					Program program = Program.findProgram(extension);

					/* Get icon based on extension */
					if ( program != null ) {
						image = getIconFromProgram(program);
					}
				}
				if ( image == null ) {
					image =
						PlatformUI.getWorkbench().getSharedImages()
							.getImage(ISharedImages.IMG_OBJ_FILE);
				}
			}
		} else {
			return null;
		}

		return image;
	}

	/**
	 * Gets an image for a file associated with a given program
	 * 
	 * @param program the program
	 * @return image
	 */
	private Image getIconFromProgram(final Program program) {
		Image image = iconCache.get(program);
		if ( image == null ) {
			ImageData imageData = program.getImageData();
			if ( imageData != null ) {
				image = new Image(Display.getDefault(), imageData);
				iconCache.put(program, image);
			}
		}
		return image;
	}

	@Override
	public void addListener(final ILabelProviderListener listener) {}

	@Override
	public void dispose() {}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {}

	@Override
	public String getDescription(final Object anElement) {
		return null;
	}

}
