package msi.gama.gui.viewers.shapefile;

import java.io.*;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.commands.AgentsMenu;
import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.IToolbarDecoratedView;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gaml.types.Types;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.*;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.geotools.swt.SwtMapPane;
import org.geotools.swt.event.MapMouseEvent;
import org.geotools.swt.tool.CursorTool;
import org.geotools.swt.utils.Utils;
import org.opengis.feature.type.*;

public class ShapeFileViewer extends EditorPart implements IToolbarDecoratedView.Zoomable {

	private class DragTool extends CursorTool {

		private Point panePos;
		boolean panning;

		public DragTool() {
			super(CursorTool.ANY_BUTTON);
		}

		@Override
		public void onMousePressed(final MapMouseEvent ev) {
			panePos = ev.getPoint();
			panning = true;
		}

		@Override
		public void onMouseDragged(final MapMouseEvent ev) {
			if ( panning ) {
				Point pos = ev.getPoint();
				if ( !pos.equals(panePos) ) {
					pane.moveImage(pos.x - panePos.x, pos.y - panePos.y);
					panePos = pos;
				}
			}
		}

		@Override
		public void onMouseReleased(final MapMouseEvent ev) {
			if ( panning ) {
				panning = false;
				getMapPane().redraw();
			}
		}

		@Override
		public Cursor getCursor() {
			return null;
		}

		@Override
		public boolean canDraw() {
			return false;
		}

		@Override
		public boolean canMove() {
			return true;
		}
	}

	SwtMapPane pane;
	MapContent content;
	GamaToolbar leftToolbar;
	ShapefileDataStore store;
	IPath path;
	boolean noCRS = false;

	@Override
	public void doSave(final IProgressMonitor monitor) {}

	@Override
	public void doSaveAs() {}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		setSite(site);
		FileEditorInput fi = (FileEditorInput) input;
		path = fi.getPath();
		File f = path.makeAbsolute().toFile();
		try {
			store = new ShapefileDataStore(f.toURI().toURL());
			content = new MapContent();
			SimpleFeatureSource featureSource = store.getFeatureSource();
			Style style = Utils.createStyle(f, featureSource);
			Layer layer = new FeatureLayer(featureSource, style);
			content.addLayer(layer);
		} catch (IOException e) {
			System.out.println("Unable to view file " + path);
		}
		this.setPartName(path.lastSegment());
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(final Composite composite) {
		Composite parent = GamaToolbarFactory.createToolbars(this, composite);
		displayInfoString();
		pane = new SwtMapPane(parent, SWT.NO_BACKGROUND, new StreamingRenderer(), content);
		pane.setBackground(GamaColors.system(SWT.COLOR_WHITE));
		pane.setCursorTool(new DragTool());
		pane.redraw();
	}

	private void displayInfoString() {
		String s;
		GamaUIColor color;

		try {
			FeatureSource fs = store.getFeatureSource();
			color = IGamaColors.OK;
			s = /* "File " + path.lastSegment() + " | " + */fs.getFeatures().size() + " objects";
			try {
				String CRS = fs.getInfo().getCRS().getName().getCode();
				s += " | CRS: " + CRS;
			} catch (Exception e) {
				color = IGamaColors.WARNING;
				s += " | No CRS found";
				noCRS = true;
			}

		} catch (IOException e2) {
			s = "Error in reading file information";
			color = IGamaColors.ERROR;
		}
		ToolItem item = FlatButton.menu(leftToolbar, color, s).item();
		((FlatButton) item.getControl()).addSelectionListener(new SelectionAdapter() {

			Menu menu;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( menu == null ) {
					menu = new Menu(leftToolbar.getShell(), SWT.POP_UP);
					fillMenu();
				}
				Point point = leftToolbar.toDisplay(new Point(e.x, e.y + leftToolbar.getSize().y));
				menu.setLocation(point.x, point.y);
				menu.setVisible(true);

			}

			private void fillMenu() {
				AgentsMenu.separate(menu, "Bounds");
				try {
					ReferencedEnvelope env = store.getFeatureSource().getBounds();
					MenuItem m2 = new MenuItem(menu, SWT.NONE);
					m2.setEnabled(false);
					m2.setText("     - upper corner : " + env.getUpperCorner().getOrdinate(0) + " " +
						env.getUpperCorner().getOrdinate(1));
					m2 = new MenuItem(menu, SWT.NONE);
					m2.setEnabled(false);
					m2.setText("     - lower corner : " + env.getLowerCorner().getOrdinate(0) + " " +
						env.getLowerCorner().getOrdinate(1));
					if ( !noCRS ) {
						env = env.transform(new ProjectionFactory().getTargetCRS(), true);
					}
					m2 = new MenuItem(menu, SWT.NONE);
					m2.setEnabled(false);
					m2.setText("     - dimensions : " + (int) env.getWidth() + "m x " + (int) env.getHeight() + "m");
				} catch (Exception e) {
					e.printStackTrace();
				}
				AgentsMenu.separate(menu);
				AgentsMenu.separate(menu, "Attributes");
				try {
					java.util.List<AttributeDescriptor> att_list = store.getSchema().getAttributeDescriptors();
					for ( AttributeDescriptor desc : att_list ) {
						String type;
						if ( desc.getType() instanceof GeometryType ) {
							type = "geometry";
						} else {
							type = Types.get(desc.getType().getBinding()).toString();
						}
						MenuItem m2 = new MenuItem(menu, SWT.NONE);
						m2.setEnabled(false);
						m2.setText("     - " + desc.getName() + ": " + type);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});

	}

	@Override
	public void setFocus() {
		pane.setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		if ( content != null ) {
			content.dispose();
			content = null;
		}
	}

	/**
	 * Method setToolbars()
	 * @see msi.gama.gui.views.IToolbarDecoratedView#setToolbars(msi.gama.gui.swt.controls.GamaToolbar, msi.gama.gui.swt.controls.GamaToolbar)
	 */
	@Override
	public void setToolbars(final GamaToolbar left, final GamaToolbar right) {
		leftToolbar = left;
	}

	/**
	 * Method getToolbarActionsId()
	 * @see msi.gama.gui.views.IToolbarDecoratedView#getToolbarActionsId()
	 */
	@Override
	public Integer[] getToolbarActionsId() {
		return new Integer[] { SEP, -32 };
	}

	/**
	 * Method createToolItem()
	 * @see msi.gama.gui.views.IToolbarDecoratedView#createToolItem(int, msi.gama.gui.swt.controls.GamaToolbar)
	 */
	@Override
	public void createToolItem(final int code, final GamaToolbar tb) {

		switch (code) {
			case -32:
				tb.button("menu.open.preferences2", "Preferences", "Style preferences", new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						StyleLayer styleLayer = (StyleLayer) content.layers().get(0);
						try {
							ShapeFileStyleOverlay s =
								new ShapeFileStyleOverlay(ShapeFileViewer.this, store.getFeatureSource(), styleLayer,
									SWT.RESIZE);
							s.open();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
				break;

		}
	}

	@Override
	public void zoomIn() {
		ReferencedEnvelope env = pane.getDisplayArea();
		env.expandBy(-env.getWidth() / 10, -env.getHeight() / 10);
		pane.setDisplayArea(env);
	}

	@Override
	public void zoomOut() {
		ReferencedEnvelope env = pane.getDisplayArea();
		env.expandBy(env.getWidth() / 10, env.getHeight() / 10);
		pane.setDisplayArea(env);
	}

	@Override
	public void zoomFit() {
		pane.reset();
	}

	public Control getMapComposite() {
		return pane;
	}

	@Override
	public Control getZoomableControl() {
		return pane;
	}
}
