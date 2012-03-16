/**
 * Created by drogoul, 4 mars 2012
 * 
 */
package msi.gama.lang.gaml.ui;

import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
import msi.gama.runtime.GAMA;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import com.google.inject.Inject;

/**
 * The class GamlEditor.
 * 
 * @author drogoul
 * @since 4 mars 2012
 * 
 */
public class GamlEditor extends XtextEditor implements IBuilderListener {

	@Inject
	private GamlJavaValidator builder;
	// Copied from SwtGui. See how to factorize this.
	public static Image run = AbstractUIPlugin.imageDescriptorFromPlugin(IGui.PLUGIN_ID,
		"/icons/menu_play.png").createImage();
	public static Image reload = AbstractUIPlugin.imageDescriptorFromPlugin(IGui.PLUGIN_ID,
		"/icons/menu_reload.png").createImage();
	public static final Color COLOR_ERROR = new Color(Display.getDefault(), 0xF4, 0x00, 0x15);
	public static final Color COLOR_OK = new Color(Display.getDefault(), 0x55, 0x8E, 0x1B);
	public static final Color COLOR_TEXT = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	private static Font labelFont;
	Composite upper;
	Composite parent;
	ToolBar toolbar;
	Label status;

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
		super.dispose();
		GAMA.getGamlBuilder().removeListener(this);
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

		Composite top = new Composite(parent, SWT.None);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		// data.heightHint = 30;
		top.setLayoutData(data);
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		top.setLayout(layout);

		status = new Label(top, SWT.CENTER);
		data = new GridData(SWT.FILL, SWT.FILL, false, true);
		data.widthHint = 120;
		data.verticalAlignment = SWT.CENTER;
		status.setLayoutData(data);

		upper = new Composite(top, SWT.None);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		// data.heightHint = 30;
		upper.setLayoutData(data);

		Composite parent2 = new Composite(parent, SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		parent2.setLayoutData(data);
		parent2.setLayout(new FillLayout());
		super.createPartControl(parent2);
		//
		// this.getDocument().addDocumentListener(new IDocumentListener() {
		//
		// @Override
		// public void documentAboutToBeChanged(final DocumentEvent event) {}
		//
		// @Override
		// public void documentChanged(final DocumentEvent event) {
		// updateToolbar();
		// }
		//
		// });
		// this.getDocument().addDocumentListener(new IDocumentListener() {
		//
		// @Override
		// public void documentAboutToBeChanged(final DocumentEvent event) {}
		//
		// @Override
		// public void documentChanged(final DocumentEvent event) {
		// updateToolbar();
		// }
		//
		// });
	}

	private final SelectionListener listener = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent evt) {
			String name = (String) evt.widget.getData("EXP");
			IModel model = (IModel) evt.widget.getData("MOD");
			GuiUtils.openSimulationPerspective();
			GAMA.newExperiment(name, model);
		}

	};

	// public IModel build() {
	// XtextDocument doc = (XtextDocument) getDocument();
	// final IModel model = doc.readOnly(new IUnitOfWork<IModel, XtextResource>() {
	//
	// @Override
	// public IModel exec(final XtextResource r) throws Exception {
	// return GAMA.getGamlBuilder().getLastBuild(r);
	// }
	// });
	// return model;
	// }

	public void updateToolbar(final IModel model) {

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				if ( toolbar != null ) {
					toolbar.dispose();
				}
				if ( upper == null || upper.isDisposed() ) { return; }
				toolbar = new ToolBar(upper, SWT.FLAT);
				toolbar.setBackground(Display.getDefault()
					.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				if ( model == null ) {
					Label l = status;
					l.setBackground(COLOR_ERROR);
					l.setForeground(COLOR_TEXT);
					l.setText("Compilation error:  ");
					new ToolItem(toolbar, SWT.SEPARATOR);
					final ToolItem exp = new ToolItem(toolbar, SWT.CHECK | SWT.SMOOTH);
					exp.setText("No experiment available");
				} else {
					IExperiment current = GAMA.getExperiment();
					Label l = status;
					l.setBackground(COLOR_OK);
					l.setForeground(COLOR_TEXT);
					l.setText("Experiments:  ");
					for ( IExperiment e : model.getExperiments() ) {
						new ToolItem(toolbar, SWT.SEPARATOR);
						final ToolItem exp = new ToolItem(toolbar, SWT.CHECK | SWT.SMOOTH);
						if ( current != null && current.getName().equals(e.getName()) &&
							current.getModel().getFileName().equals(e.getModel().getFileName()) ) {
							exp.setImage(reload);
						} else {
							exp.setImage(run);
						}

						ToolItem t = new ToolItem(toolbar, SWT.NONE);
						t.setText(e.getName());
						t.setData("EXP", e.getName());
						t.setData("MOD", model);
						// .setFont(labelFont);
						// exp.setText(e.getName());
						exp.setData("EXP", e.getName());
						exp.setData("MOD", model);
						exp.addSelectionListener(listener);
						t.addSelectionListener(listener);

					}
					new ToolItem(toolbar, SWT.SEPARATOR);

				}
				status.pack();
				toolbar.pack();
				upper.layout(true);
				parent.layout(true);
			}
		});
	}

	/**
	 * @see msi.gama.lang.gaml.validation.IBuilderListener#beforeBuilding(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public void beforeBuilding(final Resource resource) {}

	/**
	 * @see msi.gama.lang.gaml.validation.IBuilderListener#afterBuilding(msi.gama.lang.gaml.gaml.Model,
	 *      msi.gama.kernel.model.IModel)
	 */
	@Override
	public void afterBuilding(final Resource ast, final IModel model) {
		boolean equals = getDocument().readOnly(new IUnitOfWork<Boolean, XtextResource>() {

			@Override
			public Boolean exec(final XtextResource resource) throws Exception {
				return ast.equals(resource);
			}
		});
		if ( equals ) {
			updateToolbar(model);
		}
	}
}
