/*********************************************************************************************
 *
 * 'JDataChoosePage.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;

/**
 * Data chooser wizard page for the {@link JDataStoreWizard data store wizard}.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class JDataChoosePage extends WizardPage implements ISelectionChangedListener {
    public static final String ID = "ummisco.gama.ui.viewers.gis.geotools.data.DataChoosePage";
    private DataStoreFactorySpi selectedFactory;
    private boolean canFlip;

    public JDataChoosePage() {
        super(ID);
        setTitle("Choose DataStore");
        setDescription("Available DataStores on your classpath");
    }

    public void createControl( Composite parent ) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        mainComposite.setLayout(gridLayout);

        List<DataStoreFactorySpi> factoryList = new ArrayList<DataStoreFactorySpi>();
        for( Iterator<DataStoreFactorySpi> iter = DataStoreFinder.getAvailableDataStores(); iter.hasNext(); ) {
            factoryList.add(iter.next());
        }

        TableViewer viewer = new TableViewer(mainComposite);
        GridData viewerGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        viewer.getTable().setLayoutData(viewerGD);
        viewer.addSelectionChangedListener(this);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider(){
            public String getText( Object element ) {
                if (element instanceof DataStoreFactorySpi) {
                    DataStoreFactorySpi factory = (DataStoreFactorySpi) element;
                    return factory.getDisplayName();
                }
                return super.getText(element);
            }
        });

        viewer.setInput(factoryList.toArray());

        setControl(mainComposite);

        canFlip = false;
    }

    public boolean canFlipToNextPage() {
        return canFlip;
    }

    public DataStoreFactorySpi getSelectedFactory() {
        return selectedFactory;
    }

    public void selectionChanged( SelectionChangedEvent event ) {
        ISelection selection = event.getSelection();
        if (selection instanceof StructuredSelection) {
            StructuredSelection sel = (StructuredSelection) selection;
            Object selObj = sel.getFirstElement();
            if (selObj instanceof DataStoreFactorySpi) {
                selectedFactory = (DataStoreFactorySpi) selObj;
            }
        }
        if (selectedFactory != null) {
            canFlip = true;
        }
        getWizard().getContainer().updateButtons();
    }
}
