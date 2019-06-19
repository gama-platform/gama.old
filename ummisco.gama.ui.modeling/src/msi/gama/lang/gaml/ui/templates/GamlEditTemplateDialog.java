/*********************************************************************************************
 *
 * 'GamlEditTemplateDialog.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.templates;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
// XText still needs it
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
// import org.eclipse.text.templates.TemplatePersistenceData;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.ui.codetemplates.ui.internal.CodetemplatesActivator;
import org.eclipse.xtext.ui.codetemplates.ui.preferences.IEditTemplateDialog;
import org.eclipse.xtext.ui.codetemplates.ui.preferences.TemplateDialogMessages;
import org.eclipse.xtext.ui.codetemplates.ui.preferences.TemplatesLanguageConfiguration;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditor;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditorFactory.Builder;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditorModelAccess;
import org.eclipse.xtext.ui.editor.embedded.IEditedResourceProvider;
import org.eclipse.xtext.validation.Issue;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * The class GamlEditTemplateDialog.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */

@SuppressWarnings ("deprecation")
public class GamlEditTemplateDialog extends StatusDialog implements IEditTemplateDialog {

	TemplatePersistenceData data;

	// private Template fTemplate;

	private Text fNameText;
	private Text fDescriptionText;
	// private Label category;
	SourceViewer fPatternEditor;
	private EmbeddedEditorModelAccess partialModelEditor;
	private Button fInsertVariableButton;
	// private Button fAutoInsertCheckbox;
	// private final boolean fIsNameModifiable;

	// private final String[][] fContextTypes;

	private final String languageName;

	private final TemplatesLanguageConfiguration configuration;

	private final IEditedResourceProvider resourceProvider;

	public GamlEditTemplateDialog(final Shell parent, final TemplatePersistenceData data, final boolean edit,
			final ContextTypeRegistry registry, final TemplatesLanguageConfiguration configuration,
			final IEditedResourceProvider resourceProvider, final String languageName) {
		super(parent);
		this.data = data;
		this.configuration = configuration;
		this.resourceProvider = resourceProvider;
		this.languageName = languageName;

		final String title = edit ? TemplateDialogMessages.EditTemplateDialog_title_edit
				: TemplateDialogMessages.EditTemplateDialog_title_new;
		setTitle(title);

		// this.fTemplate = data.getTemplate();
		// fIsNameModifiable = isNameModifiable;

		final List<String[]> contexts = Lists.newArrayList();
		for (final Iterator<TemplateContextType> it =
				Iterators.filter(registry.contextTypes(), TemplateContextType.class); it.hasNext();) {
			final TemplateContextType type = it.next();
			contexts.add(new String[] { type.getId(), type.getName() });
		}
		// fContextTypes = contexts.toArray(new String[contexts.size()][]);
		// fContextTypeRegistry = registry;

	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	public void create() {
		super.create();
		getButton(IDialogConstants.OK_ID).setEnabled(getStatus().isOK());
	}

	@Override
	protected Control createDialogArea(final Composite ancestor) {
		final Composite parent = new Composite(ancestor, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);

		parent.setLayout(layout);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		final ModifyListener listener = e -> doTextWidgetChanged(e.widget);

		createLabel(parent, TemplateDialogMessages.EditTemplateDialog_name);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.numColumns = 4;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);

		fNameText = createText(composite);

		createLabel(composite, "Category:");
		final Label category = new Label(composite, SWT.NONE);

		// category.addModifyListener(listener);

		createLabel(parent, TemplateDialogMessages.EditTemplateDialog_description);

		fDescriptionText = new Text(parent, SWT.BORDER);
		fDescriptionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		fDescriptionText.addModifyListener(listener);

		final Label patternLabel = createLabel(parent, TemplateDialogMessages.EditTemplateDialog_pattern);
		patternLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		fPatternEditor = createEditor(parent);

		final Label filler = new Label(parent, SWT.NONE);
		filler.setLayoutData(new GridData());

		composite = new Composite(parent, SWT.NONE);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData());

		fInsertVariableButton = new Button(composite, SWT.NONE);
		fInsertVariableButton.setLayoutData(getButtonGridData());
		fInsertVariableButton.setText(TemplateDialogMessages.EditTemplateDialog_insert_variable);
		fInsertVariableButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				fPatternEditor.getTextWidget().setFocus();
				fPatternEditor.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {}
		});

		fDescriptionText.setText(data.getTemplate().getDescription());
		fillMenuPath(category);
		fNameText.setText(data.getTemplate().getName());
		fNameText.addModifyListener(listener);
		applyDialogFont(parent);
		return composite;
	}

	private void fillMenuPath(final Label category) {
		String s = data.getId();
		s = s.substring(0, s.lastIndexOf('.')).replace(".", " > ");
		category.setText(s);
	}

	protected void doTextWidgetChanged(final Widget w) {
		if (w == fNameText) {
			partialModelEditor.updatePrefix(getPrefix());
		}
	}

	protected String getContextName() {
		return "Model";
	}

	protected String getContextId() {
		return "msi.gama.lang.gaml.Gaml.Model";
	}

	protected Status createErrorStatus(final String message, final TemplateException e) {
		return new Status(IStatus.ERROR, CodetemplatesActivator.getInstance().getBundle().getSymbolicName(), message,
				e);
	}

	private static GridData getButtonGridData() {
		final GridData data = new GridData(GridData.FILL_HORIZONTAL);
		return data;
	}

	private static Label createLabel(final Composite parent, final String name) {
		final Label label = new Label(parent, SWT.NULL);
		label.setText(name);
		label.setLayoutData(new GridData());
		return label;
	}

	private static Text createText(final Composite parent) {
		final Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	private SourceViewer createEditor(final Composite parent) {
		final SourceViewer viewer = createViewer(parent);
		int numberOfLines = viewer.getDocument().getNumberOfLines();
		if (numberOfLines < 7) {
			numberOfLines = 7;
		} else if (numberOfLines > 14) {
			numberOfLines = 14;
		}

		final Control control = viewer.getControl();
		final GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = convertWidthInCharsToPixels(80);
		data.heightHint = convertHeightInCharsToPixels(numberOfLines);
		control.setLayoutData(data);
		return viewer;
	}

	protected SourceViewer createViewer(final Composite parent) {
		final Builder editorBuilder = configuration.getEmbeddedEditorFactory().newEditor(resourceProvider);
		editorBuilder.processIssuesBy((issues, monitor) -> {
			IStatus result = Status.OK_STATUS;
			final StringBuilder messages = new StringBuilder();
			for (final Issue issue : issues) {
				if (issue.getSeverity() == Severity.ERROR) {
					if (messages.length() != 0) {
						messages.append('\n');
					}
					messages.append(issue.getMessage());
				}
			}
			if (messages.length() != 0) {
				result = createErrorStatus(messages.toString(), null);
			}
			final IStatus toBeUpdated = result;
			getShell().getDisplay().asyncExec(() -> updateStatus(toBeUpdated));
		});
		final EmbeddedEditor handle = editorBuilder.withParent(parent);
		partialModelEditor = handle.createPartialEditor(getPrefix(), data.getTemplate().getPattern(), "", true);
		return handle.getViewer();
	}

	protected String getPrefix() {
		final String contextName = getContextName();
		String name = data.getTemplate().getName();
		if (fNameText != null && !fNameText.isDisposed()) {
			name = fNameText.getText();
		}
		final String prefix = "templates for " + languageName + " '" + name + "'" + " for " + contextName + " >>";
		return prefix;
	}

	@Override
	protected void okPressed() {
		final String name = fNameText == null ? data.getTemplate().getName() : fNameText.getText();
		final Template t = new Template(name, fDescriptionText.getText(), getContextId(), getPattern(), true);
		data = new TemplatePersistenceData(t, true, data.getId());
		super.okPressed();
	}

	public TemplatePersistenceData getData() {
		return data;
	}

	protected String getPattern() {
		return partialModelEditor.getEditablePart();
	}

	@Override
	protected IDialogSettings getDialogBoundsSettings() {
		final String sectionName = getClass().getName() + "_dialogBounds"; //$NON-NLS-1$
		IDialogSettings section = configuration.getDialogSettings().getSection(sectionName);
		if (section == null) {
			section = configuration.getDialogSettings().addNewSection(sectionName);
		}
		return section;
	}

	/**
	 * @see org.eclipse.xtext.ui.codetemplates.ui.preferences.IEditTemplateDialog#getTemplate()
	 */
	@Override
	public Template getTemplate() {
		return data.getTemplate();
	}

}
