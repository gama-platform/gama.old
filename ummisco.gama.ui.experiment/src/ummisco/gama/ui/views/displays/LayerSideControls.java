/*******************************************************************************************************
 *
 * LayerSideControls.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.displays;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.interfaces.ItemList;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.layers.AgentLayerStatement;
import msi.gama.outputs.layers.GridLayer;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.outputs.layers.ImageLayer;
import msi.gama.outputs.layers.ImageLayerStatement;
import msi.gama.outputs.layers.SpeciesLayerStatement;
import msi.gama.outputs.layers.charts.ChartLayerStatement;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Strings;
import msi.gaml.types.Types;
import ummisco.gama.ui.controls.ParameterExpandBar;
import ummisco.gama.ui.controls.ParameterExpandItem;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.parameters.AbstractEditor;
import ummisco.gama.ui.parameters.EditorFactory;
import ummisco.gama.ui.parameters.EditorsGroup;
import ummisco.gama.ui.parameters.PointEditor;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Class LayerSideControls.
 *
 * @author drogoul
 * @since 26 avr. 2014
 *
 */
public class LayerSideControls {

	/**
	 * Update if paused.
	 *
	 * @param layer
	 *            the layer
	 * @param container
	 *            the container
	 */
	public static void updateIfPaused(final ILayer layer, final IDisplaySurface container) {
		container.updateDisplay(true);
	}

	/**
	 * Fill.
	 *
	 * @param parent
	 *            the parent
	 * @param view
	 *            the view
	 * @return the composite
	 */
	public Composite fill(final Composite parent, final LayeredDisplayView view) {

		final Composite column = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
		column.setLayoutData(data);
		final GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		column.setLayout(layout);

		final Composite viewersComposite = new Composite(parent, SWT.None);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewersComposite.setLayoutData(data);
		viewersComposite.setLayout(new GridLayout(1, true));

		final ParameterExpandBar propertiesViewer =
				new ParameterExpandBar(viewersComposite, SWT.V_SCROLL, false, false, false, false, null);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		propertiesViewer.setLayoutData(data);
		propertiesViewer.setSpacing(5);
		final Label sep = new Label(viewersComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		sep.setLayoutData(data);
		final ItemList<ILayer> list = view.getDisplayManager();
		final ParameterExpandBar layerViewer =
				new ParameterExpandBar(viewersComposite, SWT.V_SCROLL, false, false, true, true, list);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		layerViewer.setLayoutData(data);
		layerViewer.setSpacing(5);
		// Fill the 2 viewers
		fillGeneralParameters(propertiesViewer, view);
		if (view.isOpenGL()) {
			fillCameraParameters(propertiesViewer, view);
			fillOpenGLParameters(propertiesViewer, view);
			fillKeystoneParameters(propertiesViewer, view);
		}

		for (final ILayer layer : list.getItems()) { fillLayerParameters(layerViewer, layer, view); }

		viewersComposite.layout();
		layerViewer.addListener(SWT.Collapse, e -> viewersComposite.redraw());
		layerViewer.addListener(SWT.Expand, e -> viewersComposite.redraw());
		propertiesViewer.addListener(SWT.Collapse, e -> viewersComposite.redraw());
		propertiesViewer.addListener(SWT.Expand, e -> viewersComposite.redraw());
		parent.layout();
		return viewersComposite;

	}

	/**
	 * Fill open GL parameters.
	 *
	 * @param viewer
	 *            the viewer
	 * @param view
	 *            the view
	 */
	private void fillOpenGLParameters(final ParameterExpandBar viewer, final LayeredDisplayView view) {
		final EditorsGroup contents = createContentsComposite(viewer);
		final IDisplaySurface ds = view.getDisplaySurface();
		final IScope scope = ds.getScope();
		final LayeredDisplayData data = ds.getData();
		EditorFactory.create(scope, contents, "View as wireframe", data.isWireframe(),
				(EditorListener<Boolean>) val -> {
					ds.runAndUpdate(() -> { data.setWireframe(val); });

				});
		EditorFactory.create(scope, contents, "Split layers", data.isLayerSplitted(), (EditorListener<Boolean>) val -> {
			ds.runAndUpdate(() -> { data.setLayerSplitted(val); });

		});
		EditorFactory.create(scope, contents, "Split distance", data.getSplitDistance(), 0d, 1d, .001d, false, true,
				(EditorListener<Double>) val -> {
					// ds.run(() -> {
					data.setSplitDistance(val / 3);
					// });

				});
		createItem(viewer, "OpenGL", null, contents);
	}

	/** The camera orientation. */
	PointEditor cameraPos, cameraTarget;

	/** The preset. */
	// AbstractEditor preset;

	/** The zoom. */
	AbstractEditor zoom;

	/** The rotate. */
	AbstractEditor rotate;

	/** The lock. */
	// AbstractEditor lock;

	/**
	 * Fill camera parameters.
	 *
	 * @param viewer
	 *            the viewer
	 * @param view
	 *            the view
	 */
	private void fillCameraParameters(final ParameterExpandBar viewer, final LayeredDisplayView view) {
		final EditorsGroup contents = createContentsComposite(viewer);
		final IDisplaySurface ds = view.getDisplaySurface();
		final IScope scope = ds.getScope();
		final LayeredDisplayData data = ds.getData();

		// EditorFactory.create(scope, contents, "FreeFly Camera", !data.isArcBallCamera(),
		// (EditorListener<Boolean>) val -> {
		// ds.runAndUpdate(() -> { data.setArcBallCamera(!val); });
		//
		// });
		// boolean cameraLocked = data.isLocked();
		// lock = EditorFactory.create(scope, contents, "Lock camera:", cameraLocked,
		// (EditorListener<Boolean>) newValue -> {
		// cameraPos.setActive(!newValue);
		// cameraTarget.setActive(!newValue);
		// zoom.setActive(!newValue);
		// data.setLocked(newValue);
		// });

		// preset = EditorFactory.choose(scope, contents, "Choose camera:", data.getCameraName(), data.getCameraNames(),
		// (EditorListener<String>) newValue -> {
		// if (newValue.isEmpty()) return;
		// data.setCameraName(newValue);
		// ds.updateDisplay(true);
		// });

		cameraPos = (PointEditor) EditorFactory.create(scope, contents, "Position:", data.getCameraPos().yNegated(),
				(EditorListener<GamaPoint>) newValue -> {
					data.setCameraPos(newValue.yNegated());
					ds.updateDisplay(true);
				});
		cameraTarget = (PointEditor) EditorFactory.create(scope, contents, "Target:", data.getCameraTarget().yNegated(),
				(EditorListener<GamaPoint>) newValue -> {
					data.setCameraTarget(newValue.yNegated());
					ds.updateDisplay(true);
				});
		// preset.setActive(true);
		// cameraPos.setActive(!cameraLocked);
		// cameraTarget.setActive(!cameraLocked);
		// zoom.setActive(!cameraLocked);
		data.addListener((p, v) -> {
			switch (p) {
				case CAMERA_POS:
					cameraPos.getParam().setValue(scope, data.getCameraPos().yNegated());
					cameraPos.updateWithValueOfParameter();
					copyCameraAndKeystoneDefinition(scope, data);
					break;
				case CAMERA_TARGET:
					cameraTarget.getParam().setValue(scope, data.getCameraTarget().yNegated());
					cameraTarget.updateWithValueOfParameter();
					copyCameraAndKeystoneDefinition(scope, data);
					break;
				case CAMERA_PRESET:
					// preset.getParam().setValue(scope, "Choose...");
					// preset.updateWithValueOfParameter();
					boolean locked = data.isLocked();
					// lock.getParam().setValue(scope, locked);
					// lock.updateWithValueOfParameter();
					copyCameraAndKeystoneDefinition(scope, data);
					WorkbenchHelper.asyncRun(() -> {
						cameraPos.setActive(!locked);
						cameraTarget.setActive(!locked);
						zoom.setActive(!locked);
					});
					break;

				default:
					;
			}
		});
		final Label l = new Label(contents, SWT.None);
		l.setText("");
		final Button copy = new Button(contents, SWT.PUSH);
		copy.setText("Copy as facets");
		copy.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		copy.setToolTipText(
				"Copy the definition of the camera properties to the clipboard in a format suitable for pasting them in the definition of a display in GAML");
		copy.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final String text = cameraDefinitionToCopy();
				WorkbenchHelper.copy(text);
			}

		});
		createItem(viewer, "Camera", null, contents);

	}

	/**
	 * Fill keystone parameters.
	 *
	 * @param viewer
	 *            the viewer
	 * @param view
	 *            the view
	 */
	private void fillKeystoneParameters(final ParameterExpandBar viewer, final LayeredDisplayView view) {
		final EditorsGroup contents = createContentsComposite(viewer);
		final IDisplaySurface ds = view.getDisplaySurface();
		final IScope scope = ds.getScope();
		final LayeredDisplayData data = ds.getData();

		final AbstractEditor[] point = new AbstractEditor[4];
		final ICoordinates points = data.getKeystone();
		int i = 0;
		for (@SuppressWarnings ("unused") final GamaPoint p : points) {
			final int j = i;
			i++;
			point[j] = EditorFactory.create(scope, contents, "Point " + j + ":", data.getKeystone().at(j).clone(),
					(EditorListener<GamaPoint>) newValue -> {
						data.getKeystone().at(j).setLocation(newValue);
						data.setKeystone(data.getKeystone());
						ds.updateDisplay(true);
					});
		}

		data.addListener((p, v) -> {
			switch (p) {
				case KEYSTONE:
					for (int k = 0; k < 4; k++) {
						point[k].getParam().setValue(scope, data.getKeystone().at(k));
						point[k].updateWithValueOfParameter();
					}
					data.setAntialias(data.isKeystoneDefined());
					copyCameraAndKeystoneDefinition(scope, data);
					break;
				default:
					;
			}

		});
		final Label l = new Label(contents, SWT.None);
		l.setText("");
		final Button copy = new Button(contents, SWT.PUSH);
		copy.setText("Copy as facet");
		copy.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		copy.setToolTipText(
				"Copy the definition of the keystone values to the clipboard in a format suitable for pasting them in the definition of a display in GAML");
		copy.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final String text = keystoneDefinitionToCopy(scope, data);
				WorkbenchHelper.copy(text);
			}

		});
		createItem(viewer, "Keystone", null, contents);
	}

	/**
	 * Fill general parameters.
	 *
	 * @param viewer
	 *            the viewer
	 * @param view
	 *            the view
	 */
	private void fillGeneralParameters(final ParameterExpandBar viewer, final LayeredDisplayView view) {
		final EditorsGroup contents = createContentsComposite(viewer);
		final IDisplaySurface ds = view.getDisplaySurface();
		final IScope scope = ds.getScope();
		final LayeredDisplayData data = ds.getData();
		final AbstractEditor antialias = EditorFactory.create(scope, contents, "Antialias:", data.isAntialias(),
				(EditorListener<Boolean>) newValue -> {
					data.setAntialias(newValue);
					ds.updateDisplay(true);
				});

		final AbstractEditor background = EditorFactory.create(scope, contents, "Background:",
				data.getBackgroundColor(), (EditorListener<Color>) newValue -> {
					data.setBackgroundColor(new GamaColor(newValue));
					ds.updateDisplay(true);
				});
		EditorFactory.create(scope, contents, "Highlight:", data.getHighlightColor(),
				(EditorListener<Color>) newValue -> {
					data.setHighlightColor(new GamaColor(newValue));
					ds.updateDisplay(true);
				});
		zoom = EditorFactory.create(scope, contents, "Zoom (%):", "", (int) (data.getZoomLevel() * 100), 0, null, 1,
				(EditorListener<Integer>) newValue -> {
					data.setZoomLevel(newValue.doubleValue() / 100d, true, false);
					ds.updateDisplay(true);
				});

		if (view.isOpenGL()) {
			rotate = EditorFactory.create(scope, contents, "Z-axis rotation:", data.getCurrentRotationAboutZ(), null,
					null, 0.1, false, false, (EditorListener<Double>) newValue -> {
						data.setZRotationAngle(newValue);
						// ds.updateDisplay(true);
					});
			EditorFactory.create(scope, contents, "Continuous rotation", data.isContinuousRotationOn(),
					(EditorListener<Boolean>) val -> {
						ds.runAndUpdate(() -> {
							data.setContinuousRotation(val);

						});

					});
		}
		createItem(viewer, "General", null, contents);

		ds.getData().addListener((p, v) -> {
			switch (p) {
				case ZOOM:
					zoom.getParam().setValue(scope, (int) (data.getZoomLevel() * 100));
					zoom.updateWithValueOfParameter();
					break;
				case BACKGROUND:
					background.getParam().setValue(scope, data.getBackgroundColor());
					background.updateWithValueOfParameter();
					break;
				case ROTATION:
					if (rotate != null) {
						rotate.getParam().setValue(scope, (double) v);
						rotate.updateWithValueOfParameter();
					}
					break;
				case ANTIALIAS:
					antialias.getParam().setValue(scope, data.isAntialias());
					antialias.updateWithValueOfParameter();
					break;
				default:
					;

			}

		});

	}

	/**
	 * Creates the item.
	 *
	 * @param viewer
	 *            the viewer
	 * @param name
	 *            the name
	 * @param data
	 *            the data
	 * @param contents
	 *            the contents
	 */
	public static void createItem(final ParameterExpandBar viewer, final String name, final Object data,
			final Composite contents) {
		final ParameterExpandItem i = new ParameterExpandItem(viewer, data, SWT.None, null);
		i.setText(name);
		contents.pack(true);
		contents.layout();
		i.setControl(contents);
		i.setHeight(contents.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		i.setExpanded(false);
	}

	/**
	 * Creates the contents composite.
	 *
	 * @param viewer
	 *            the viewer
	 * @return the editors group
	 */
	public static EditorsGroup createContentsComposite(final ParameterExpandBar viewer) {

		// contents.setBackground(IGamaColors.WHITE.color());
		// final GridLayout layout = new GridLayout(2, false);
		// layout.verticalSpacing = 0;
		// contents.setLayout(layout);
		return new EditorsGroup(viewer, SWT.NONE);
	}

	/**
	 * Fill layer parameters.
	 *
	 * @param viewer
	 *            the viewer
	 * @param layer
	 *            the layer
	 * @param view
	 *            the view
	 */
	private void fillLayerParameters(final ParameterExpandBar viewer, final ILayer layer,
			final LayeredDisplayView view) {
		if (layer.isControllable()) {
			final EditorsGroup contents = createContentsComposite(viewer);
			fill(contents, layer, view.getDisplaySurface());
			createItem(viewer, "Layer " + layer.getName(), layer, contents);
		}
	}

	/**
	 * Fill.
	 *
	 * @param compo
	 *            the compo
	 * @param layer
	 *            the layer
	 * @param container
	 *            the container
	 */
	public void fill(final EditorsGroup compo, final ILayer layer, final IDisplaySurface container) {

		final ILayerStatement definition = layer.getDefinition();

		EditorFactory.create(container.getScope(), compo, "Transparency:",
				layer.getData().getTransparency(container.getScope()), 0.0, 1.0, 0.1, false, true, newValue -> {
					layer.getData().setTransparency(newValue);
					updateIfPaused(layer, container);
				});
		EditorFactory.create(container.getScope(), compo, "Position:", layer.getData().getPosition(),
				(EditorListener<GamaPoint>) newValue -> {
					layer.getData().setPosition(newValue);
					updateIfPaused(layer, container);
				});
		EditorFactory.create(container.getScope(), compo, "Size:", layer.getData().getSize(),
				(EditorListener<GamaPoint>) newValue -> {
					layer.getData().setSize(newValue);
					updateIfPaused(layer, container);
				});

		switch (definition.getType(container.getOutput())) {

			case GRID: {
				EditorFactory.create(container.getScope(), compo, "Draw grid:",
						((GridLayer) layer).getData().drawLines(), (EditorListener<Boolean>) newValue -> {
							((GridLayer) layer).getData().setDrawLines(newValue);
							updateIfPaused(layer, container);
						});
				break;
			}
			case AGENTS: {
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
			case SPECIES: {
				EditorFactory.choose(container.getScope(), compo, "Aspect:",
						((SpeciesLayerStatement) definition).getAspectName(),
						((SpeciesLayerStatement) definition).getAspects(), newValue -> {
							((SpeciesLayerStatement) definition).setAspect(newValue);
							updateIfPaused(layer, container);
						});
				break;
			}
			case IMAGE: {
				if (definition instanceof ImageLayerStatement) {
					EditorFactory.create(container.getScope(), compo, "Image:",
							((ImageLayer) layer).getImageFileName(GAMA.getRuntimeScope()), false, newValue -> {
								((ImageLayer) layer).setImageFileName(GAMA.getRuntimeScope(), newValue);
								updateIfPaused(layer, container);
							});
				}
				break;

			}

			case CHART: {
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
				if (enabled) {
					save.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							((ChartLayerStatement) definition).saveHistory();
						}

					});
				}
				break;
			}
			default:
				break;

		}

	}

	/**
	 * Copy camera and keystone definition.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 */
	private void copyCameraAndKeystoneDefinition(final IScope scope, final LayeredDisplayData data) {
		if (!GamaPreferences.Displays.OPENGL_CLIPBOARD_CAM.getValue()) return;
		final String toCopy = cameraDefinitionToCopy() + " " + Strings.LN
				+ (data.isKeystoneDefined() ? keystoneDefinitionToCopy(scope, data) : "");
		WorkbenchHelper.copy(toCopy);
	}

	/**
	 * Camera definition to copy.
	 *
	 * @return the string
	 */
	private String cameraDefinitionToCopy() {
		StringBuilder text = new StringBuilder(IKeyword.CAMERA).append(" 'default' ").append(IKeyword.LOCATION)
				.append(": ").append(new GamaPoint(cameraPos.getCurrentValue()).withPrecision(4).serialize(false));
		text.append(" ").append(IKeyword.TARGET).append(": ")
				.append(new GamaPoint(cameraTarget.getCurrentValue()).withPrecision(4).serialize(false)).append(";");
		return text.toString();
	}

	/**
	 * Keystone definition to copy.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @return the string
	 */
	private String keystoneDefinitionToCopy(final IScope scope, final LayeredDisplayData data) {
		final IList<GamaPoint> pp = GamaListFactory.create(scope, Types.POINT, data.getKeystone().toCoordinateArray());
		return IKeyword.KEYSTONE + ": " + pp.serialize(false);
	}
}
