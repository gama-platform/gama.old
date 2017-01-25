package ummisco.gama.opengl.scene;

import static msi.gama.common.geometry.GeometryUtils.applyToInnerGeometries;
import static msi.gama.common.geometry.GeometryUtils.simplifiedTriangulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUtessellatorCallbackAdapter;
import com.vividsolutions.jts.geom.Polygon;

import jogamp.opengl.glu.tessellator.GLUtessellatorImpl;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.geometry.ICoordinates.IndexedVisitor;
import msi.gama.common.preferences.GamaPreferences;
import ummisco.gama.opengl.JOGLRenderer;

public class TriangulationHelper extends GLUtessellatorCallbackAdapter {

	final GeometryDrawer drawer;
	final JOGLRenderer renderer;
	final GLUtessellatorImpl tobj = (GLUtessellatorImpl) GLU.gluNewTess();
	final IndexedVisitor tessDrawer;
	final IndexedVisitor vertexDrawer;

	private final LoadingCache<Polygon, Collection<Polygon>> TRIANGULATION_CACHE =
			CacheBuilder.newBuilder().initialCapacity(5000).maximumSize(5000).expireAfterAccess(2, TimeUnit.SECONDS)
					.build(new CacheLoader<Polygon, Collection<Polygon>>() {

						@Override
						public Collection<Polygon> load(final Polygon polygon) throws Exception {
							final List<Polygon> TRIANGLES = new ArrayList<>();
							simplifiedTriangulation(polygon, TRIANGLES);
							return TRIANGLES;
						}
					});
	private final boolean useJTS = !GamaPreferences.OpenGL.OPENGL_TRIANGULATOR.getValue();

	public TriangulationHelper(final JOGLRenderer r, final GeometryDrawer geometryDrawer) {
		renderer = r;
		drawer = geometryDrawer;
		vertexDrawer = drawer::_point;
		tessDrawer = (x, y, z, i) -> {
			final double[] data = new double[] { x, y, z };
			tobj.gluTessVertex(data, 0, data);
		};
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, this);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, this);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, this);
	}

	public void drawPolygon(final Polygon p, final ICoordinates vertices, final boolean clockwise) {
		if (useJTS) {
			for (final Polygon tri : TRIANGULATION_CACHE.apply(p)) {
				drawer._shape(drawer.getCoordinates(tri), 3, true, clockwise, false, null);
			}
		} else {
			GLU.gluTessBeginPolygon(tobj, null);
			GLU.gluTessBeginContour(tobj);
			vertices.visit(tessDrawer, -1, clockwise);
			GLU.gluTessEndContour(tobj);
			applyToInnerGeometries(p, geom -> {
				GLU.gluTessBeginContour(tobj);
				drawer.getCoordinates(geom).visit(tessDrawer, -1, !clockwise);
				GLU.gluTessEndContour(tobj);
			});
			GLU.gluTessEndPolygon(tobj);
		}
	}

	// Tesselation callback methods

	@Override
	public void begin(final int type) {
		renderer.getGL().glBegin(type);
	}

	@Override
	public void end() {
		renderer.getGL().glEnd();
	}

	@Override
	public void vertex(final Object vertexData) {
		final double[] v = (double[]) vertexData;
		vertexDrawer.process(v[0], v[1], v[2], 0);
	}

}
