/**
 * Created by drogoul, 4 mars 2012
 * 
 */
package msi.gama.lang.gaml.ui;

import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.kernel.model.IModel;
import msi.gama.runtime.GAMA;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.text.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

/**
 * The class GamlEditor.
 * 
 * @author drogoul
 * @since 4 mars 2012
 * 
 */
public class GamlEditor extends XtextEditor implements IBuilderListener {

	// Copied from SwtGui. See how to factorize this.
	public static Image run = AbstractUIPlugin.imageDescriptorFromPlugin(IGui.PLUGIN_ID,
		"/icons/menu_play.png").createImage();
	public static Image reload = AbstractUIPlugin.imageDescriptorFromPlugin(IGui.PLUGIN_ID,
		"/icons/menu_reload.png").createImage();
	public static final Color COLOR_ERROR = new Color(Display.getDefault(), 0xF4, 0x00, 0x15);
	public static final Color COLOR_OK = new Color(Display.getDefault(), 0x55, 0x8E, 0x1B);
	public static final Color COLOR_TEXT = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	private static final int INITIAL_BUTTONS = 10;
	private static Font labelFont;
	Composite toolbar, top, parent;
	Button[] buttons = new Button[INITIAL_BUTTONS];
	Label status;
	volatile boolean isUpdatingToolbar = false;
	volatile IModel currentModel;
	volatile int modelCount = 0;

	static {
		FontData fd = Display.getDefault().getSystemFont().getFontData()[0];
		fd.setStyle(SWT.BOLD);
		labelFont = new Font(Display.getDefault(), fd);
	}

	public GamlEditor() {
		GAMA.getGamlBuilder().addListener(this);
	}

	@Override
	public void dispose() {
		GAMA.getGamlBuilder().removeListener(this);
		super.dispose();
	}

	@Override
	public void createPartControl(final Composite parent) {
		this.parent = parent;
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);

		top = new Composite(parent, SWT.None);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		// data.heightHint = 30;
		top.setLayoutData(data);
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 10;
		layout.marginHeight = 5;
		top.setLayout(layout);

		status = new Label(top, SWT.CENTER);
		data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = 120;
		data.minimumHeight = SWT.DEFAULT;
		status.setLayoutData(data);
		status.setForeground(COLOR_TEXT);

		toolbar = new Composite(top, SWT.None);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		// data.heightHint = 30;
		toolbar.setLayoutData(data);
		// toolbar.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		layout = new GridLayout(INITIAL_BUTTONS, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		toolbar.setLayout(layout);
		for ( int i = 0; i < INITIAL_BUTTONS; i++ ) {
			buttons[i] = new Button(toolbar, SWT.PUSH);
			data = new GridData(SWT.FILL, SWT.FILL, true, true);
			// data.exclude = true;
			buttons[i].setLayoutData(data);
			buttons[i].setText("Experiment " + i);
			buttons[i].addSelectionListener(listener);
			buttons[i].setVisible(false);
		}

		// Asking the editor to fill the rest
		Composite parent2 = new Composite(parent, SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		parent2.setLayoutData(data);
		parent2.setLayout(new FillLayout());
		super.createPartControl(parent2);
	}

	private final SelectionListener listener = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent evt) {
			String name = ((Button) evt.widget).getText();
			// IModel model = (IModel) evt.widget.getData("MOD");
			if ( currentModel == null ) { return; }
			GuiUtils.openSimulationPerspective();
			GAMA.newExperiment(name, currentModel);
		}

	};

	private void enableButton(final int index, final String text/* , final Image image */) {
		// ((GridData) buttons[index].getLayoutData()).exclude = false;
		buttons[index].setVisible(true);
		buttons[index].setText(text);
		// buttons[index].setImage(image);
		buttons[index].pack();
	}

	private void hideButton(final Button b) {
		// ((GridData) b.getLayoutData()).exclude = true;
		b.setVisible(false);
	}

	private void setStatus(final Color c, final String text) {
		top.setBackground(c);
		status.setBackground(c);
		toolbar.setBackground(c);
		status.setText(text);
	}

	private void setModel(final IModel model) {
		GuiUtils.debug(this.getTitle() + " changing its current model to " + model);
		if ( currentModel != null && currentModel != GAMA.getModel() ) {
			GuiUtils.debug("     ==> Old model #" + modelCount + " being disposed in " +
				this.getTitle());
			currentModel.dispose();
		}
		if ( model != null ) {
			modelCount++;
			GuiUtils.debug("     ==> New model #" + modelCount +
				" being received and memorized by " + this.getTitle());
		}
		currentModel = model;
	}

	public void updateToolbar(final IModel model) {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				isUpdatingToolbar = true;

				if ( toolbar == null || toolbar.isDisposed() ) {
					isUpdatingToolbar = false;
					return;
				}

				for ( Button b : buttons ) {
					if ( b != null ) {
						hideButton(b);
					}
				}

				if ( model == null ) {
					int errors = getXtextResource().getErrors().size();
					setStatus(COLOR_ERROR, "" + errors + " error(s) found");

				} else {
					setStatus(COLOR_OK, "Run experiments:");
					int i = 0;
					for ( IExperiment e : model.getExperiments() ) {
						enableButton(i, e.getName());
						// buttons[i].setData("MOD", model);
						i++;
					}
				}

				// status.pack();
				toolbar.layout(true);
				// toolbar.pack();
				toolbar.update();
				// parent.layout(true);
				parent.update();
				// GuiUtils.debug("Finished updating toolbar of " + getResource().getLocationURI());
				isUpdatingToolbar = false;
			}
		});

	}

	IDocumentListener docListener = new IDocumentListener() {

		@Override
		public void documentAboutToBeChanged(final DocumentEvent event) {}

		@Override
		public void documentChanged(final DocumentEvent event) {
			GAMA.getGamlBuilder().invalidate(resource);
		}
	};

	/**
	 * @see msi.gama.lang.gaml.validation.IBuilderListener#beforeBuilding(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public void beforeBuilding(final Resource resource) {}

	/**
	 * @see msi.gama.lang.gaml.validation.IBuilderListener#afterBuilding(msi.gama.lang.gaml.gaml.Model,
	 *      msi.gama.kernel.model.IModel)
	 */

	private XtextResource resource;

	public XtextResource getXtextResource() {
		if ( getDocument() == null ) { return null; }
		if ( resource == null ) {
			resource = getDocument().readOnly(new IUnitOfWork<XtextResource, XtextResource>() {

				@Override
				public XtextResource exec(final XtextResource state) throws Exception {
					return state;
				}

			});
		}
		return resource;
	}

	@Override
	public boolean afterBuilding(final Resource ast, final IModel model) {
		if ( ast == getXtextResource() ) {
			if ( !isUpdatingToolbar ) {
				setModel(model);
				updateToolbar(model);
				return true;
			}
			return false;
		}
		return false;
	}

	public void forgetModel() {
		setModel(null);
	}

}
