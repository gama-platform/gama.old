/*********************************************************************************************
 * 
 *
 * 'NavigatorLabelProvider.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator;

import java.util.Hashtable;
import msi.gama.gui.swt.IGamaIcons;
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
		if ( element instanceof UserProjectsFolder ) { return ((UserProjectsFolder) element).getName() + " ( " +
			((UserProjectsFolder) element).getChildren().length + " )"; }
		if ( element instanceof ModelsLibraryFolder ) { return ((ModelsLibraryFolder) element).getName() + " ( " +
			((ModelsLibraryFolder) element).getChildren().length + " )"; }
		if ( element instanceof VirtualSharedModelsFolder ) { return ((VirtualSharedModelsFolder) element).getName() +
			" ( " + ((VirtualSharedModelsFolder) element).getChildren().length + " )"; }
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
			image = IGamaIcons.FOLDER_SHARED.image();
		} else if ( element instanceof UserProjectsFolder ) {
			image = IGamaIcons.FOLDER_USER.image();
		} else if ( element instanceof ModelsLibraryFolder ) {
			image = IGamaIcons.FOLDER_BUILTIN.image();
		} else if ( element instanceof FileBean ) {
			if ( ((FileBean) element).hasChildren() ) {
				image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
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
					image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
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
