/**
 * Created by drogoul, 26 avr. 2014
 *
 */
package ummisco.gama.ui.views.displays;

import java.awt.Color;

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
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.IGamaFile;
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

	public LayerSideControls() {
	}

	public static void updateIfPaused(final ILayer layer, final IDisplaySurface container) {
		// if ( layer.isPaused(container) ) {
		container.updateDisplay(true);
		// }
	}

	public static void fill(final Composite compo, final IDisplaySurface container) {
		EditorFactory.create(container.getScope(), compo, "Antialias:", container.getData().isAntialias(),
				new EditorListener<Boolean>() {

					@Override
					public void valueModified(final Boolean newValue) throws GamaRuntimeException {
						container.getData().setAntialias(newValue);
						updateIfPaused(null, container);
					}
				});
		EditorFactory.create(container.getScope(), compo, "Background:", container.getData().getBackgroundColor(),
				new EditorListener<Color>() {

					@Override
					public void valueModified(final Color newValue) {
						container.getData().setBackgroundColor(newValue);
						updateIfPaused(null, container);
					}
				});
		EditorFactory.create(container.getScope(), compo, "Highlight:", container.getData().getHighlightColor(),
				new EditorListener<Color>() {

					@Override
					public void valueModified(final Color newValue) {
						container.getData().setHighlightColor(newValue);
						updateIfPaused(null, container);
					}
				});
		if (container.getOutput().isOpenGL() && container.getOutput().cameraFix) {
			EditorFactory.create(container.getScope(), compo, "Camera Lock:", container.getData().isCameraLock(),
					new EditorListener<Boolean>() {

						@Override
						public void valueModified(final Boolean newValue) throws GamaRuntimeException {
							container.getData().setCameraLock(newValue);
							updateIfPaused(null, container);
						}
					});
		}

	}

	public static void fill(final Composite compo, final ILayer layer, final IDisplaySurface container) {

		final ILayerStatement definition = layer.getDefinition();

		// EditorFactory.create(compo, "Visible:",
		// container.getManager().isEnabled(layer), new
		// EditorListener<Boolean>() {
		//
		// @Override
		// public void valueModified(final Boolean newValue) {
		// container.getManager().enableLayer(layer, newValue);
		// updateIfPaused(layer, container);
		// }
		// });
		EditorFactory.create(container.getScope(), compo, "Transparency:", definition.getTransparency(), 0.0, 1.0, 0.1,
				false, new EditorListener<Double>() {

					@Override
					public void valueModified(final Double newValue) {
						layer.setTransparency(1 - newValue);
						updateIfPaused(layer, container);
					}

				});
		EditorFactory.create(container.getScope(), compo, "Position:", definition.getBox().getPosition(),
				new EditorListener<ILocation>() {

					@Override
					public void valueModified(final ILocation newValue) {
						layer.setPosition(newValue);
						updateIfPaused(layer, container);
					}

				});
		EditorFactory.create(container.getScope(), compo, "Size:", definition.getBox().getSize(),
				new EditorListener<ILocation>() {

					@Override
					public void valueModified(final ILocation newValue) {
						layer.setExtent(newValue);
						updateIfPaused(layer, container);
					}

				});

		switch (definition.getType()) {

		case ILayerStatement.GRID: {
			EditorFactory.create(container.getScope(), compo, "Draw grid:",
					((GridLayerStatement) definition).drawLines(), new EditorListener<Boolean>() {

						@Override
						public void valueModified(final Boolean newValue) throws GamaRuntimeException {
							((GridLayer) layer).setDrawLines(newValue);
							updateIfPaused(layer, container);
						}
					});
			break;
		}
		case ILayerStatement.AGENTS: {
			IExpression expr = null;
			if (definition instanceof AgentLayerStatement) {
				expr = ((AgentLayerStatement) definition).getFacet(IKeyword.VALUE);
			}
			if (expr != null) {
				EditorFactory.createExpression(container.getScope(), compo, "Agents:", expr,
						new EditorListener<IExpression>() {

							@Override
							public void valueModified(final IExpression newValue) throws GamaRuntimeException {
								((AgentLayerStatement) definition).setAgentsExpr(newValue);
								updateIfPaused(layer, container);
							}
						}, Types.LIST);
			}
			break;
		}
		case ILayerStatement.SPECIES: {
			EditorFactory.choose(container.getScope(), compo, "Aspect:",
					((SpeciesLayerStatement) definition).getAspectName(), true,
					((SpeciesLayerStatement) definition).getAspects(), new EditorListener<String>() {

						@Override
						public void valueModified(final String newValue) {
							((SpeciesLayerStatement) definition).setAspect(newValue);
							updateIfPaused(layer, container);
						}
					});
			break;
		}
		// case ILayerStatement.TEXT: {
		// EditorFactory.createExpression(compo, "Expression:",
		// ((TextLayerStatement) definition).getTextExpr(),
		// new EditorListener<IExpression>() {
		//
		// @Override
		// public void valueModified(final IExpression newValue) {
		// ((TextLayerStatement) definition).setTextExpr(newValue);
		// updateIfPaused(layer, container);
		// }
		// }, Types.STRING);
		// EditorFactory.create(compo, "Color:", ((TextLayerStatement)
		// definition).getColor(),
		// new EditorListener<Color>() {
		//
		// @Override
		// public void valueModified(final Color newValue) {
		// ((TextLayerStatement) definition).setColor(newValue);
		// updateIfPaused(layer, container);
		// }
		// });
		// EditorFactory.create(compo, "Font:", ((TextLayerStatement)
		// definition).getFont(),
		// new EditorListener<GamaFont>() {
		//
		// @Override
		// public void valueModified(final GamaFont newValue) {
		// ((TextLayerStatement) definition).setFont(newValue);
		// updateIfPaused(layer, container);
		// }
		// });
		// break;
		// }
		case ILayerStatement.IMAGE: {
			if (definition instanceof ImageLayerStatement) {
				EditorFactory.create(container.getScope(), compo, "Image:",
						((ImageLayerStatement) definition).getImageFileName(), false, new EditorListener<String>() {

							@Override
							public void valueModified(final String newValue) {
								((ImageLayerStatement) definition).setImageFileName(newValue);
								updateIfPaused(layer, container);
							}

						});
			}
			break;

		}
		case ILayerStatement.GIS: {
			EditorFactory.createFile(container.getScope(), compo, "Shapefile:",
					((ImageLayerStatement) definition).getImageFileName(), new EditorListener<IGamaFile>() {

						@Override
						public void valueModified(final IGamaFile newValue) throws GamaRuntimeException {
							((ImageLayerStatement) definition).setGisLayerName(GAMA.getRuntimeScope(),
									newValue.getName());
							updateIfPaused(layer, container);
						}

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
					// TODO Revoir cet �diteur, tr�s laid !
					editor.open();
					updateIfPaused(layer, container);
				}

			});
			final Button save = new Button(compo, SWT.PUSH);
			save.setText("Save...");
			save.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
			save.setToolTipText("Save the chart data as a CSV file");
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
