/*********************************************************************************************
 *
 * 'NewFileWizardPage.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.wizards;

import java.util.Arrays;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import msi.gaml.operators.Strings;

/**
 * The "New" wizard page allows setting the container for the new file as well as the file name. The page will only
 * accept file name without the extension OR with the extension that matches the expected one.
 */

public class NewFileWizardPage extends NewModelWizardPage {

	private Text descriptionText;
	private Button yesButton;
	private String typeOfModel = "Empty";

	public NewFileWizardPage(final ISelection selection) {
		super(selection);
		setTitle("Model file");
		setDescription("This wizard creates a new model file.");
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		createContainerSection(container);
		createLabel(container, "&Choose a template:");

		final Composite middleComposite = new Composite(container, SWT.NULL);
		FillLayout fillLayout = new FillLayout();
		middleComposite.setLayout(fillLayout);
		Arrays.asList("Empty", "Skeleton", "Test").forEach(s -> {
			final Button b = new Button(middleComposite, SWT.RADIO);
			b.setText(s);
			if (s.equals("Empty"))
				b.setSelection(true);
			b.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					typeOfModel = Strings.toLowerCase(((Button) e.widget).getText());
					updateStatus(null);
					dialogChanged();
					descriptionText.setText(typeOfModel.equals("test") ? "A model dedicated to run unit tests" : "");
				}

			});
		});

		createLabel(container, null);
		createFileNameSection(container);
		createAuthorSection(container);
		createNameSection(container);
		createLabel(container, null);
		createLabel(container, "&Model description:");
		descriptionText = new Text(container, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		descriptionText.setBounds(0, 0, 250, 100);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.verticalSpan = 4;
		descriptionText.setLayoutData(gd);

		/*
		 * Need to add seven empty labels in order to push next controls after the descriptionText box.
		 */
		// TODO Dirty!! Change the way to do this
		for (int i = 0; i < 7; i++) {
			createLabel(container, null);
		}

		createLabel(container, "&Create a html template \nfor the model description ?");

		final Composite compo = new Composite(container, SWT.NULL);
		fillLayout = new FillLayout();
		compo.setLayout(fillLayout);

		yesButton = new Button(compo, SWT.RADIO);
		yesButton.setText("Yes");
		final Button noButton = new Button(compo, SWT.RADIO);
		noButton.setText("No");
		noButton.setSelection(true);
		yesButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent se) {
				dialogChanged();
			}
		});

		/* Finished adding the custom control */
		initialize("New Model.gaml");
		dialogChanged();
		setControl(container);
	}

	/** Gets the model name of the new file */
	@Override
	public String getDescription() {
		return descriptionText.getText();
	}

	/** Return true if the user wants a html template, and false otherwise */
	public Boolean getValueHtmlTemplate() {
		return yesButton.getSelection();
	}

	/** Return the type of model (empty, skeleton or example) */
	public String getTypeOfModel() {
		return typeOfModel;

	}

	@Override
	public String getExtension() {
		return ".gaml";
	}

	@Override
	public String gamlType() {
		return "Model";
	}
}