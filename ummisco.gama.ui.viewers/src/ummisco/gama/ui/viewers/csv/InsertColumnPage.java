/* Copyright 2011 csvedit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ummisco.gama.ui.viewers.csv;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 *
 * @author fhenri
 *
 */
public class InsertColumnPage extends Dialog {

    private String[] columnTitle;
    private String newColumnName;
    private ArrayList<String> selectedColumn;

    /**
     * @param parentShell
     * @param columns
     */
    public InsertColumnPage (
            Shell parentShell,
            String[] columns) {
        super(parentShell);
        this.columnTitle = columns;
        this.newColumnName = "";
        this.selectedColumn = new ArrayList<String>(columnTitle.length);
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea (Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);
        /*
        final Label nameLabel = new Label(container, SWT.NONE);
        nameLabel.setLayoutData(new GridData(GridData.END,
                GridData.CENTER, false, false));
        nameLabel.setText("Column:");

        final List columnList = new List(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
        columnList.setBounds(40, 20, 220, 100);
        columnList.setItems(columnTitle);

        columnList.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected (SelectionEvent e) {
                        selectedColumn.clear();
                        selectedColumn.addAll(Arrays.asList(columnList.getSelection()));
                    }});
        */
        final Label filterLabel = new Label(container, SWT.NONE);
        filterLabel.setLayoutData(new GridData(GridData.BEGINNING,
                GridData.BEGINNING, false, false, 2, 1));
        filterLabel.setText("Please Give the name of the new column to be created");

        final Text newColumnNameField = new Text(container, SWT.BORDER);
        newColumnNameField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
        newColumnNameField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                newColumnName = newColumnNameField.getText();
            }
        });

        return container;
    }

    /**
     * @return
     */
    public String getColumnNewName () {
        return newColumnName;
    }

    /**
     * @return
     */
    public String[] getColumnSelected () {
        return selectedColumn.toArray(new String[selectedColumn.size()]);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell (Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Insert Column");
    }
}
