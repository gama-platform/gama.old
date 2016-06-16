package msi.gama.gui.navigator.images;

import java.text.NumberFormat;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import msi.gama.gui.swt.SwtGui;

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
		} catch (final SWTException ex) {
			// bad image file, fall through
		} catch (final CoreException ex) {
			// unable to read file, fall through
		}
		if ( imageData == null ) {
			final Label l = new Label(parent, SWT.LEFT);
			l.setText("Not available");
			return l;
		}

		final Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		final NumberFormat numFormat = NumberFormat.getNumberInstance();

		// width, height, type
		createLabelAndText(main, "Width:", "" + imageData.width + " pixels");
		createLabelAndText(main, "Height:", "" + imageData.height + " pixels");
		createLabelAndText(main, "Type:", ImageContentTypeDescriber.getLongLabel(imageData.type));
		createLabelAndText(main, "Image Size (Uncompressed):", "" + imageData.data.length + " bytes");
		final Label sep = new Label(main, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		sep.setLayoutData(gd);

		// depth
		createLabelAndText(main, "Color Depth:", numFormat.format(imageData.depth));

		// transparent (from transparentPixel/alpha/alphaData)
		if ( imageData.transparentPixel >= 0 && imageData.palette != null ) {
			try {
				final RGB color = imageData.palette.getRGB(imageData.transparentPixel);
				createLabelAndText(main, "Transparent pixel:",
					"RGB[" + color.red + "," + color.green + "," + color.blue + "]");
			} catch (final SWTException ex) {
				createLabelAndText(main, "No transparency", " ");
			}
		} else {
			// show this as a % since it's global
			if ( imageData.alpha >= 0 && imageData.alpha <= 255 ) {
				final int pct = (int) Math.round(100.0d * (imageData.alpha / 255.0d));
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
		final Label l = new Label(parent, SWT.LEFT);
		l.setText(label);
		final Text t = new Text(parent, SWT.SINGLE | SWT.READ_ONLY);
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
			final IFile f = SwtGui.adaptTo(getElement(), IFile.class, IFile.class);
			if ( f != null ) {
				data = ImageDataLoader.getImageData(f);
			}
		}
		return data;
	}
}