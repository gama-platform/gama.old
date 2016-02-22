package msi.gama.gui.navigator.images;

import java.text.NumberFormat;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.dialogs.PropertyPage;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.viewers.image.ImageViewer;
import msi.gaml.operators.fastmaths.FastMath;

/**
 * A property page for image data objects. The element must either adapt to
 * ImageData, or be an image.
 */
public class ImagePropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	public static final int IMAGE_ASC = 8, IMAGE_PGM = 9;

	public ImagePropertyPage() {
		super();
		noDefaultAndApplyButton();
	}

	@Override
	protected Control createContents(final Composite parent) {
		ImageData imageData = null;
		try {
			imageData = getImageData();
		} catch (SWTException ex) {
			// bad image file, fall through
		} catch (CoreException ex) {
			// unable to read file, fall through
		}
		if ( imageData == null ) {
			Label l = new Label(parent, SWT.LEFT);
			l.setText("Not available");
			return l;
		}

		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		NumberFormat numFormat = NumberFormat.getNumberInstance();

		// width, height, type
		createLabelAndText(main, "Width:", "" + imageData.width + " pixels");
		createLabelAndText(main, "Height:", "" + imageData.height + " pixels");
		createLabelAndText(main, "Type:", ImageContentTypeDescriber.getLongLabel(imageData.type));
		createLabelAndText(main, "Image Size (Uncompressed):", "" + imageData.data.length + " bytes");
		Label sep = new Label(main, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		sep.setLayoutData(gd);

		// depth
		createLabelAndText(main, "Color Depth:", numFormat.format(imageData.depth));

		// transparent (from transparentPixel/alpha/alphaData)
		if ( imageData.transparentPixel >= 0 && imageData.palette != null ) {
			try {
				RGB color = imageData.palette.getRGB(imageData.transparentPixel);
				createLabelAndText(main, "Transparent pixel:",
					"RGB[" + color.red + "," + color.green + "," + color.blue + "]");
			} catch (SWTException ex) {
				createLabelAndText(main, "No transparency", " ");
			}
		} else {
			// show this as a % since it's global
			if ( imageData.alpha >= 0 && imageData.alpha <= 255 ) {
				int pct = (int) FastMath.round(100.0d * (imageData.alpha / 255.0d));
				createLabelAndText(main, "Transparency:", "" + pct + "%");
			} else if ( imageData.alphaData != null ) {
				createLabelAndText(main, "Transparency:", "per pixel");
			} else {
				createLabelAndText(main, "No transparency", " ");
			}
		}

		// scanlinePad?
		// bytesPerLine (scanline)?

		// delayTime (for animated gif)

		return main;
	}

	private void createLabelAndText(final Composite parent, final String label, final String text) {
		Label l = new Label(parent, SWT.LEFT);
		l.setText(label);
		Text t = new Text(parent, SWT.SINGLE | SWT.READ_ONLY);
		t.setBackground(parent.getBackground());
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		t.setText(text);
	}

	/**
	 * Get the image data from the selected element.
	 */
	private ImageData getImageData() throws CoreException, SWTException {
		// just adapt it, this will catch ImageDataEditorInputs
		ImageData data = SwtGui.adaptTo(getElement(), ImageData.class);
		if ( data == null ) {
			// try to get it from the file
			IFile f = SwtGui.adaptTo(getElement(), IFile.class, IFile.class);
			if ( f != null ) {
				// if open in an editor, use that to get the image data.
				for ( IEditorReference ref : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getEditorReferences() ) {
					if ( "msi.gama.gui.images.editor.ImageViewer".equals(ref.getId()) ) {
						IEditorInput input = ref.getEditorInput();
						IFile inputFile = SwtGui.adaptTo(input, IFile.class, IFile.class);
						if ( inputFile != null && f.equals(inputFile) ) {
							IEditorPart editor = ref.getEditor(false);
							if ( editor instanceof ImageViewer ) {
								data = ((ImageViewer) editor).getImageData();
								if ( data != null ) {
									break;
								}
							}
						}
					}
				}
				// load it from the file
				if ( data == null ) {
					data = ImageDataLoader.getImageData(f);
				}
			}
		}
		return data;
	}
}