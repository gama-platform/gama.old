/**
 * Created by drogoul, 31 août 2013
 * 
 */
package msi.gama.gui.views;

import java.util.*;
import java.util.List;
import msi.gama.common.*;
import msi.gama.common.GamaPreferences.Entry;
import msi.gama.common.interfaces.IParameterEditor;
import msi.gama.gui.parameters.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.controls.*;
import msi.gama.kernel.experiment.*;
import msi.gama.runtime.IScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Class GamaPreferencesView.
 * 
 * @author drogoul
 * @since 31 août 2013
 * 
 */
public class GamaPreferencesView {

	Shell parentShell, shell;
	CTabFolder tabFolder;
	final List<IParameterEditor> editors = new ArrayList();
	final Map<String, Object> modelValues = new LinkedHashMap();

	public GamaPreferencesView(final Shell parent) {
		parentShell = parent;
		shell = new Shell(parentShell, SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		final GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = gridLayout.marginHeight = 5;
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 5;
		shell.setLayout(gridLayout);
		buildContents();
	}

	private void buildContents() {
		tabFolder = new CTabFolder(shell, SWT.TOP);
		tabFolder.setBorderVisible(false);
		tabFolder.setBackgroundMode(SWT.INHERIT_DEFAULT);
		tabFolder.setSimple(true); // rounded tabs
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		final Label sep = new Label(this.shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		Map<String, Map<String, List<Entry>>> prefs = GamaPreferences.organizePrefs();
		for ( String tabName : prefs.keySet() ) {
			CTabItem item = new CTabItem(tabFolder, SWT.None);
			item.setText(tabName);
			item.setShowClose(false);
			buildContentsFor(item, prefs.get(tabName));
		}
		buildButtons();

	}

	private void buildContentsFor(final CTabItem tab, final Map<String, List<Entry>> entries) {
		ParameterExpandBar viewer = new ParameterExpandBar(tab.getParent(), SWT.V_SCROLL);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.setLayoutData(data);
		viewer.computeSize(tab.getBounds().x, SWT.DEFAULT);
		viewer.setSpacing(1);
		tab.setControl(viewer);
		for ( String groupName : entries.keySet() ) {
			ParameterExpandItem item = new ParameterExpandItem(viewer, entries.get(groupName), SWT.NONE);
			item.setText(groupName);
			Composite compo = new Composite(viewer, SWT.NONE);
			GridLayout layout = new GridLayout(2, false);
			layout.verticalSpacing = 0;
			compo.setLayout(layout);
			buildGroupContents(compo, entries.get(groupName));
			item.setControl(compo);
			item.setHeight(compo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			item.setImage(SwtGui.action);
			item.setExpanded(true);

		}
	}

	private void buildGroupContents(final Composite compo, final List<Entry> list) {
		for ( final Entry e : list ) {
			modelValues.put(e.getKey(), e.getValue());
			IParameter p = new ParameterWrapper(e) {

				@Override
				public void setValue(final Object value) {
					modelValues.put(e.getKey(), value);
				}

				@Override
				public Object value(final IScope scope) {
					return modelValues.get(e.getKey());
				}

			};
			AbstractEditor ed = EditorFactory.create(compo, p);
			ed.acceptPopup(false);
			editors.add(ed);
		}
		compo.layout();
		compo.pack(true);
	}

	private void buildButtons() {
		final Button buttonRevert = new Button(this.shell, SWT.PUSH);
		buttonRevert.setText("Default");
		final GridData gridDataRevert = new GridData(GridData.BEGINNING, GridData.END, true, false);
		gridDataRevert.widthHint = 100;
		buttonRevert.setLayoutData(gridDataRevert);

		final Button buttonCancel = new Button(this.shell, SWT.PUSH);
		buttonCancel.setText("Cancel");
		final GridData gridDataCancel = new GridData(GridData.END, GridData.END, false, false);
		gridDataCancel.widthHint = 100;
		buttonCancel.setLayoutData(gridDataCancel);
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				shell.setVisible(false);
			}

		});
		final Button buttonOK = new Button(this.shell, SWT.PUSH);
		buttonOK.setText("Save");
		final GridData gridDataOk = new GridData(GridData.END, GridData.END, true, false);
		gridDataOk.widthHint = 100;
		buttonOK.setLayoutData(gridDataOk);
		buttonOK.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				shell.setVisible(false);
				GamaPreferences.setNewPreferences(modelValues);
			}

		});

		this.shell.setDefaultButton(buttonOK);
		buttonRevert.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				GamaPreferences.revertToDefaultValues(modelValues);
				for ( IParameterEditor ed : editors ) {
					ed.updateValue();
				}
			}

		});

	}

	public void open() {
		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		shell.open();

		while (!this.shell.isDisposed() && this.shell.isVisible()) {
			if ( !this.shell.getDisplay().readAndDispatch() ) {
				this.shell.getDisplay().sleep();
			}
		}

	}

}
