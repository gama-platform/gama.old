package msi.gama.gui.viewers.image;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Save As dialog that also includes a drop-down for the image type.
 */
class SaveImageAsDialog extends SaveAsDialog {
    // These 3 arrays need to stay in sync
    private static int[] IMAGE_TYPES = {
            SWT.IMAGE_PNG, SWT.IMAGE_GIF, SWT.IMAGE_JPEG, SWT.IMAGE_BMP,
            SWT.IMAGE_ICO, SWT.IMAGE_TIFF
    };

    private static String[] IMAGE_LABELS = {
            Messages.SaveImageAsDialog_saveAsPNGLabel,
            Messages.SaveImageAsDialog_saveAsGIFLabel,
            Messages.SaveImageAsDialog_saveAsJPEGLabel,
            Messages.SaveImageAsDialog_saveAsBMPLabel,
            Messages.SaveImageAsDialog_saveAsICOLabel,
            Messages.SaveImageAsDialog_saveAsTIFFLabel
    };

    private static String[] IMAGE_EXTS = {
            "png", //$NON-NLS-1$
            "gif", //$NON-NLS-1$
            "jpg", //$NON-NLS-1$
            "bmp", //$NON-NLS-1$
            "ico", //$NON-NLS-1$
            "tiff" //$NON-NLS-1$
    };

    /**
     * The initial index of IMAGE_TYPES to select.
     */
    private int initialImageTypeIndex = 0;

    /**
     * The selected index of IMAGE_TYPES
     */
    private int selectedImageTypeIndex = -1;

    private Combo imageTypeCombo;

    public SaveImageAsDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite)super.createDialogArea(parent);

        // create a composite with standard margins and spacing to put
        // out image specific stuff
        Group composite = new Group(parentComposite, SWT.NONE);
        composite.setText(Messages.SaveImageAsDialog_saveAsImageInfoGroupLabel);
        GridLayout layout = new GridLayout();
        // TODO: this isn't leaving any margin around the edge of the Group
        // in the dialog
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.numColumns = 2;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        composite.setFont(parentComposite.getFont());

        Label l = new Label(composite, SWT.RIGHT);
        l.setText(Messages.SaveImageAsDialog_saveAsTypeComboLabel);
        GridData gd = new GridData();
        l.setLayoutData(gd);
        imageTypeCombo = new Combo(composite, SWT.BORDER | SWT.READ_ONLY |
                SWT.DROP_DOWN);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        imageTypeCombo.setLayoutData(gd);

        // initialize the imageType drop-down
        // TODO: filter by what's actually going to work when saving
        for (int i = 0; i < IMAGE_LABELS.length; i++) {
            imageTypeCombo.add(IMAGE_LABELS[i]);
        }
        if (initialImageTypeIndex >= 0 &&
                initialImageTypeIndex < IMAGE_TYPES.length) {
            imageTypeCombo.select(initialImageTypeIndex);
            selectedImageTypeIndex = initialImageTypeIndex;
        }
        else {
            imageTypeCombo.select(0);
            selectedImageTypeIndex = 0;
        }
        // change the filename extension when the imageType is changed
        imageTypeCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = imageTypeCombo.getSelectionIndex();
                IPath path = Path.fromPortableString(getFileName());
                path = path.removeFileExtension().addFileExtension(IMAGE_EXTS[index]);
                setFileName(path.toPortableString());
            }
        });

        return parentComposite;
    }

    @Override
    protected void okPressed() {
        // save off the selected image type
        selectedImageTypeIndex = imageTypeCombo.getSelectionIndex();

        super.okPressed();
    }

    /**
     * Set the initial filename and image type. This must be called before
     * {@code}create(){@code}.
     * 
     * @param basename
     *            the file basename, no extension.
     * @param type
     *            the SWT.IMAGE_ type.
     */
    public void setOriginalName(String basename, int type) {
        for (int i = 0; i < IMAGE_TYPES.length; i++) {
            if (type == IMAGE_TYPES[i]) {
                initialImageTypeIndex = i;
                basename += "." + IMAGE_EXTS[i]; //$NON-NLS-1$
                break;
            }
        }
        setOriginalName(basename);
    }

    /**
     * Set the initial file and path and image type. This must be called before
     * {@code}create(){@code}.
     * 
     * @param origfile
     *            the original file.
     * @param type
     *            the SWT.IMAGE_ type.
     */
    public void setOriginalFile(IFile origfile, int type) {
        for (int i = 0; i < IMAGE_TYPES.length; i++) {
            if (type == IMAGE_TYPES[i]) {
                initialImageTypeIndex = i;
                String newname = origfile.getFullPath().removeFileExtension().addFileExtension(
                        IMAGE_EXTS[i]).lastSegment();
                origfile = origfile.getParent().getFile(Path.fromPortableString(newname));
                break;
            }
        }
        setOriginalFile(origfile);
    }

    /**
     * Get the selected image type.
     */
    public int getSaveAsImageType() {
        if (selectedImageTypeIndex >= 0 &&
                selectedImageTypeIndex < IMAGE_TYPES.length) {
            return IMAGE_TYPES[selectedImageTypeIndex];
        }
        else {
            return IMAGE_TYPES[0];
        }
    }

    /**
     * Get the selected image type.
     */
    public String getSaveAsImageExt() {
        if (selectedImageTypeIndex >= 0 &&
                selectedImageTypeIndex < IMAGE_TYPES.length) {
            return IMAGE_EXTS[selectedImageTypeIndex];
        }
        else {
            return IMAGE_EXTS[0];
        }
    }
}