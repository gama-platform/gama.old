/**
 * Created by drogoul, 4 mars 2012
 * 
 */
package msi.gama.lang.gaml.ui;

import java.util.LinkedHashSet;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import msi.gama.runtime.GAMA;
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
public class GamlEditor extends XtextEditor implements IGamlBuilder.Listener {

	// Copied from SwtGui. See how to factorize this.
	public static Image run = AbstractUIPlugin.imageDescriptorFromPlugin(IGui.PLUGIN_ID,
		"/icons/menu_play.png").createImage();
	public static Image reload = AbstractUIPlugin.imageDescriptorFromPlugin(IGui.PLUGIN_ID,
		"/icons/menu_reload.png").createImage();
	public static final Color COLOR_ERROR = new Color(Display.getDefault(), 0xF4, 0x00, 0x15);
	public static final Color COLOR_OK = new Color(Display.getDefault(), 0x55, 0x8E, 0x1B);
	public static final Color COLOR_WARNING = new Color(Display.getDefault(), 0xFD, 0xA6, 0x00);
	public static final Color COLOR_TEXT = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	private static final int INITIAL_BUTTONS = 10;
	private static Font labelFont;
	Composite toolbar, top, parent;
	Button[] buttons = new Button[INITIAL_BUTTONS];
	Label status;
	LinkedHashSet<String> currentExperiments = new LinkedHashSet();
	int previousNumberOfErrors;

	static {
		FontData fd = Display.getDefault().getSystemFont().getFontData()[0];
		fd.setStyle(SWT.BOLD);
		labelFont = new Font(Display.getDefault(), fd);
	}

	@Override
	public void dispose() {
		GAMA.getGamlBuilder().removeListener(this);
		if ( buttons != null ) {
			for ( Button b : buttons ) {
				if ( b != null && !b.isDisposed() ) {
					b.dispose();
				}
			}
			buttons = null;
		}

		if ( top != null && !top.isDisposed() ) {
			top.dispose();
			top = null;
		}
		if ( toolbar != null && !toolbar.isDisposed() ) {
			toolbar.dispose();
			toolbar = null;
		}
		if ( status != null && !status.isDisposed() ) {
			status.dispose();
			status = null;
		}
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
		top.setLayoutData(data);
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 10;
		layout.marginHeight = 5;
		top.setLayout(layout);

		toolbar = new Composite(top, SWT.None);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		toolbar.setLayoutData(data);
		layout = new GridLayout(INITIAL_BUTTONS + 1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		toolbar.setLayout(layout);

		status = new Label(toolbar, SWT.NONE);
		data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		// data.widthHint = 80;
		data.minimumHeight = SWT.DEFAULT;
		status.setLayoutData(data);
		status.setForeground(COLOR_TEXT);

		for ( int i = 0; i < INITIAL_BUTTONS; i++ ) {
			buttons[i] = new Button(toolbar, SWT.PUSH);
			data = new GridData(SWT.FILL, SWT.FILL, true, true);
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

		getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource state) throws Exception {
				GAMA.getGamlBuilder().addListener(state.getURI(), GamlEditor.this);
				updateExperiments(state);
			}
		});

	}

	private final SelectionListener listener = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent evt) {
			String name = ((Button) evt.widget).getText();
			IModel model = getDocument().readOnly(new IUnitOfWork<IModel, XtextResource>() {

				@Override
				public IModel exec(final XtextResource state) throws Exception {
					return GAMA.getGamlBuilder().build(state);
				}

			});
			if ( model == null ) { return; }
			GuiUtils.openSimulationPerspective();
			GAMA.newExperiment(name, model);
		}

	};

	private void enableButton(final int index, final String text/* , final Image image */) {
		if ( text == null ) { return; }
		buttons[index].setVisible(true);
		buttons[index].setText(text);
		// buttons[index].setImage(image);
		buttons[index].pack();
	}

	private void hideButton(final Button b) {
		b.setVisible(false);
	}

	private void setStatus(final Color c, final String text) {
		top.setBackground(c);
		status.setBackground(c);
		toolbar.setBackground(c);
		status.setText(text);
	}

	private void updateToolbar(final boolean ok) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if ( toolbar == null || toolbar.isDisposed() ) { return; }
				for ( Button b : buttons ) {
					if ( b.isVisible() ) {
						hideButton(b);
					}
				}
				if ( ok ) {
					setStatus(COLOR_OK, "Run experiments:");
					int i = 0;
					for ( String e : currentExperiments ) {
						enableButton(i++, e);
					}
				} else {
					setStatus(COLOR_ERROR, "Error(s) detected");
				}

				toolbar.layout(true);
				toolbar.update();
			}
		});

	}

	private void updateExperiments(final org.eclipse.emf.ecore.resource.Resource r) {
		final LinkedHashSet<String> exp = new LinkedHashSet();
		exp.add(IKeyword.DEFAULT_EXP);

		int status = r.getErrors().size();

		for ( Statement object : ((Model) r.getContents().get(0)).getStatements() ) {
			if ( IKeyword.EXPERIMENT.equals(object.getKey()) ) {
				if ( object instanceof Definition ) {
					String name = ((Definition) object).getName();
					if ( name == null ) {
						name = EGaml.getLabelFromFacet(object, IKeyword.NAME);
					}
					exp.add(name);
				}
			}
		}

		if ( previousNumberOfErrors == status && exp.equals(currentExperiments) ) { return; }
		if ( previousNumberOfErrors > 0 && status > 0 ) { return; }
		previousNumberOfErrors = status;
		currentExperiments = exp;
		updateToolbar(status == 0);
	}

	/**
	 * 
	 */
	// public void updateToolbar() {

	// }

	/**
	 * @see msi.gama.common.interfaces.IGamlBuilder.Listener#validationEnded(boolean)
	 */
	@Override
	public void validationEnded(final org.eclipse.emf.ecore.resource.Resource xtextResource) {
		updateExperiments(xtextResource);
	}

}
