/*********************************************************************************************
 *
 * 'MaplayerTableViewer.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.geotools.data.FeatureSource;
import org.geotools.map.Layer;
import org.geotools.map.StyleLayer;
import org.geotools.styling.Style;

import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.viewers.gis.geotools.styling.SimpleConfigurator;

/**
 *
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class MaplayerTableViewer extends TableViewer implements ISelectionChangedListener {
	private final List<Layer> layersList = new ArrayList<>();
	private Layer selectedMapLayer;

	private final String[] titles = { "Layer name", "Visible", "Style" };
	private SwtMapPane pane;

	/**
	 * Constructor.
	 *
	 * <p>
	 * <b>Note</b> that after the object is built and before actually using it, the has to be set through the method.
	 * </p>
	 *
	 * @param parent
	 * @param style
	 */
	public MaplayerTableViewer(final Composite parent, final int style) {
		super(parent, style);

		this.setContentProvider(new ArrayContentProvider());
		this.addSelectionChangedListener(this);

		createColumns(parent, this);
		final Table table = this.getTable();
		table.setHeaderVisible(true);
		// table.setLinesVisible(true);

		this.setInput(layersList);
	}

	/**
	 *
	 * @param pane
	 *            the map pane to use.
	 */
	public void setPane(final SwtMapPane pane) {
		this.pane = pane;
	}

	/**
	 * Getter for the loaded list.
	 *
	 * @return the list of map layers.
	 */
	public List<Layer> getLayersList() {
		return layersList;
	}

	/**
	 *
	 * @return the selected layer or <code>null</code>.
	 */
	public Layer getSelectedMapLayer() {
		return selectedMapLayer;
	}

	private void createColumns(final Composite parent, final TableViewer viewer) {

		final int[] bounds = { 120, 50, 50 };

		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(final Object element) {
				if (element instanceof Layer) { return GamaIcons.create(IGamaIcons.FEATURE).image(); }
				return null;
			}

			@Override
			public String getText(final Object element) {
				if (element instanceof Layer) {
					final Layer p = (Layer) element;
					String title = p.getTitle();
					if (title == null || title.length() == 0) {
						@SuppressWarnings ("rawtypes") final FeatureSource featureSource = p.getFeatureSource();
						if (featureSource != null) {
							title = featureSource.getName().getLocalPart().toString();
						}
					}
					return title;
				}
				return null;
			}
		});

		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(final Object element) {
				if (element instanceof Layer) {
					final Layer p = (Layer) element;
					if (p.isVisible()) { return GamaIcons.create(IGamaIcons.CHECKED).image(); }
					return GamaIcons.create(IGamaIcons.UNCHECKED).image();
				}
				return null;
			}

			@Override
			public String getText(final Object element) {
				return null;
			}
		});

		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(final Object element) {
				return GamaIcons.create(IGamaIcons.STYLE).image();
			}

			@Override
			public String getText(final Object element) {
				return null;
			}
		});

	}

	private TableViewerColumn createTableViewerColumn(final String title, final int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent arg0) {
		if (arg0 == null) {
			selectedMapLayer = null;
			return;
		}
		final IStructuredSelection selection = (IStructuredSelection) arg0.getSelection();
		final Object firstElement = selection.getFirstElement();
		if (firstElement instanceof Layer) {
			selectedMapLayer = (Layer) firstElement;
		}
	}

	@Override
	protected void triggerEditorActivationEvent(final ColumnViewerEditorActivationEvent event) {
		super.triggerEditorActivationEvent(event);
		final ViewerCell source = (ViewerCell) event.getSource();
		final int columnIndex = source.getColumnIndex();
		if (columnIndex == 1) {
			final Layer element = (Layer) source.getElement();
			element.setVisible(!element.isVisible());
			refresh();
			pane.redraw();
		} else if (columnIndex == 2) {
			final Layer element = (Layer) source.getElement();
			try {
				doSetStyle(element);
			} catch (final IOException e) {
				e.printStackTrace();
			}
			pane.redraw();
		}
	}

	/**
	 * Show a style dialog to create a new Style for the layer
	 *
	 * @param layer
	 *            the layer to be styled
	 * @throws IOException
	 */
	private void doSetStyle(final Layer layer) throws IOException {
		if (layer instanceof StyleLayer) {
			final StyleLayer styleLayer = (StyleLayer) layer;
			final Style style = SimpleConfigurator.showDialog(this.getTable().getShell(), layer);
			if (style != null) {
				styleLayer.setStyle(style);
			}
		}
	}

	/**
	 *
	 * @param layer
	 *            the layer to add.
	 */
	public void addLayer(final Layer layer) {
		layersList.add(0, layer);
		refresh();
	}

	/**
	 *
	 * @param layer
	 *            the layer to remove.
	 */
	public void removeLayer(final Layer layer) {
		layersList.remove(layer);
		refresh();
	}

	/**
	 * Clears all the layers from the viewer.
	 */
	public void clear() {
		layersList.clear();
		refresh();
	}

}
