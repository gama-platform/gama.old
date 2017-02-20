/*********************************************************************************************
 *
 * 'LayerSideControls.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.views.displays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.layers.AgentLayerStatement;
import msi.gama.outputs.layers.GridLayer;
import msi.gama.outputs.layers.GridLayerStatement;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.outputs.layers.ImageLayerStatement;
import msi.gama.outputs.layers.SpeciesLayerStatement;
import msi.gama.outputs.layers.charts.ChartLayerStatement;
import msi.gama.runtime.GAMA;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.parameters.EditorFactory;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Class LayerSideControls.
 *
 * @author drogoul
 * @since 26 avr. 2014
 *
 */
public class LayerSideControls {

	public LayerSideControls() {}

	public static void updateIfPaused(final ILayer layer, final IDisplaySurface container) {
		// if ( layer.isPaused(container) ) {
		container.updateDisplay(true);
		// }
	}

	public static void fill(final Composite compo, final ILayer layer, final IDisplaySurface container) {

		final ILayerStatement definition = layer.getDefinition();

		EditorFactory.create(container.getScope(), compo, "Transparency:", definition.getTransparency(), 0.0, 1.0, 0.1,
				false, newValue -> {
					layer.setTransparency(1 - newValue);
					updateIfPaused(layer, container);
				});
		EditorFactory.create(container.getScope(), compo, "Position:", definition.getBox().getPosition(),
				(EditorListener<ILocation>) newValue -> {
					layer.setPosition(newValue);
					updateIfPaused(layer, container);
				});
		EditorFactory.create(container.getScope(), compo, "Size:", definition.getBox().getSize(),
				(EditorListener<ILocation>) newValue -> {
					layer.setExtent(newValue);
					updateIfPaused(layer, container);
				});

		switch (definition.getType()) {

			case ILayerStatement.GRID: {
				EditorFactory.create(container.getScope(), compo, "Draw grid:",
						((GridLayerStatement) definition).drawLines(), (EditorListener<Boolean>) newValue -> {
							((GridLayer) layer).setDrawLines(newValue);
							updateIfPaused(layer, container);
						});
				break;
			}
			case ILayerStatement.AGENTS: {
				IExpression expr = null;
				if (definition instanceof AgentLayerStatement) {
					expr = ((AgentLayerStatement) definition).getFacet(IKeyword.VALUE);
				}
				if (expr != null) {
					EditorFactory.createExpression(container.getScope(), compo, "Agents:", expr, newValue -> {
						((AgentLayerStatement) definition).setAgentsExpr(newValue);
						updateIfPaused(layer, container);
					}, Types.LIST);
				}
				break;
			}
			case ILayerStatement.SPECIES: {
				EditorFactory.choose(container.getScope(), compo, "Aspect:",
						((SpeciesLayerStatement) definition).getAspectName(), true,
						((SpeciesLayerStatement) definition).getAspects(), newValue -> {
							((SpeciesLayerStatement) definition).setAspect(newValue);
							updateIfPaused(layer, container);
						});
				break;
			}
			case ILayerStatement.IMAGE: {
				if (definition instanceof ImageLayerStatement) {
					EditorFactory.create(container.getScope(), compo, "Image:",
							((ImageLayerStatement) definition).getImageFileName(), false, newValue -> {
								((ImageLayerStatement) definition).setImageFileName(newValue);
								updateIfPaused(layer, container);
							});
				}
				break;

			}
			case ILayerStatement.GIS: {
				EditorFactory.createFile(container.getScope(), compo, "Shapefile:",
						((ImageLayerStatement) definition).getImageFileName(), newValue -> {
							((ImageLayerStatement) definition).setGisLayerName(GAMA.getRuntimeScope(),
									newValue.getName(GAMA.getRuntimeScope()));
							updateIfPaused(layer, container);
						});
				break;
			}
			case ILayerStatement.CHART: {
				final Button b = new Button(compo, SWT.PUSH);
				b.setText("Properties");
				b.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
				b.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						// FIXME Editor not working for the moment
						final Point p = b.toDisplay(b.getLocation());
						p.y = p.y + 30;
						final SWTChartEditor editor = new SWTChartEditor(WorkbenchHelper.getDisplay(),
								((ChartLayerStatement) definition).getChart(), p);
						editor.open();
						updateIfPaused(layer, container);
					}

				});
				final Button save = new Button(compo, SWT.PUSH);
				final boolean enabled = ((ChartLayerStatement) definition).getDataSet().keepsHistory();
				save.setText(enabled ? "Save..." : "No history");
				save.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
				save.setToolTipText(
						"Save the chart data as a CSV file when memorization of values is enabled in the preferences or via the 'memorize:' facet");
				save.setEnabled(enabled);
				if (enabled)
					save.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							((ChartLayerStatement) definition).saveHistory();
						}

					});
				break;
			}

		}

	}
}
