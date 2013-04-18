/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.statements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.ExecutionStatus;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gama.util.graph.IGraph;
import msi.gama.util.graph.writer.AvailableGraphWriters;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.Facets.Facet;
import msi.gaml.types.IType;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

@symbol(name = IKeyword.SAVE, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, with_args = true, remote_context = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.ACTION })
@facets(value = { @facet(name = IKeyword.TYPE, type = IType.ID, optional = true),
	@facet(name = IKeyword.DATA, type = IType.NONE_STR, optional = true),
	@facet(name = IKeyword.REWRITE, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.TO, type = IType.STRING, optional = false),
	@facet(name = IKeyword.WITH, type = { IType.MAP }, optional = true) }, omissible = IKeyword.DATA)
public class SaveStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	private Arguments init;

	public SaveStatement(final IDescription desc) {
		super(desc);
	}

	// TODO rewrite this with the GamaFile framework

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		String typeExp = getLiteral(IKeyword.TYPE);
		IExpression file = getFacet(IKeyword.TO);
		IExpression rewriteExp = getFacet(IKeyword.REWRITE);

		String path = "";
		if ( file == null ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		path =
			scope.getSimulationScope().getModel()
				.getRelativeFilePath(Cast.asString(scope, file.value(scope)), false);
		if ( path.equals("") ) {
			scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		String type = "text";
		if ( typeExp != null ) {
			type = typeExp;
		}
		if ( type.equals("shp") ) {
			IExpression item = getFacet(IKeyword.DATA);
			List<? extends IAgent> agents;
			if ( item == null ) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			agents = Cast.asList(scope, item.value(scope));
			if ( agents == null ) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			if ( agents.isEmpty() ) {}
			saveShape(agents, path, scope);
		} else if ( type.equals("text") || type.equals("csv") ) {
			File fileTxt = new File(path);
			if ( rewriteExp != null ) {
				boolean rewrite = Cast.asBool(scope, rewriteExp.value(scope));
				if ( rewrite ) {
					if ( fileTxt.exists() ) {
						fileTxt.delete();
					}
				}
			}
			try {
				fileTxt.createNewFile();
			} catch (GamaRuntimeException e) {
				throw e;
			} catch (IOException e) {
				throw new GamaRuntimeException(e);
			}
			IExpression item = getFacet(IKeyword.DATA);
			saveText(type, item, fileTxt, scope);
			
		} else if (AvailableGraphWriters.getAvailableWriters().contains(type.trim().toLowerCase())) {
			
			IExpression item = getFacet(IKeyword.DATA);
			IGraph g;
			if ( item == null ) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			g = Cast.asGraph(scope, item);
			if (g == null ) {
				scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			AvailableGraphWriters
				.getGraphWriter(type.trim().toLowerCase())
				.writeGraph(scope, g, null, path);
			
		} else {
		
			throw new GamaRuntimeException("Unable to save, because this format is not recognized ('"+type+"')");
		}
		return Cast.asString(scope, file.value(scope));
	}

	public void saveShape(final List<? extends IAgent> agents, final String path, final IScope scope)
		throws GamaRuntimeException {
		Map<String, String> attributes = new GamaMap();
		computeInits(scope, attributes);
		StringBuilder specs = new StringBuilder(agents.size() * 20);
		for ( IAgent be : agents ) {
			if ( be.getGeometry() != null ) {
				IAgent ag = be;
				specs.append("geom:" + ag.getInnerGeometry().getClass().getSimpleName());
				break;
			}
		}

		try {
			for ( String e : attributes.keySet() ) {
				specs.append(',').append(e).append(':')
					.append(typeJava(agents.get(0).getAttribute(attributes.get(e))));
			}
			String featureTypeName = agents.get(0).getSpeciesName();
			saveShapeFile(scope, path, agents, featureTypeName, specs.toString(), attributes);
		} catch (GamaRuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new GamaRuntimeException(e);
		}

	}

	public void saveText(final String type, final IExpression item, final File fileTxt,
		final IScope scope) throws GamaRuntimeException {
		try {
			FileWriter fw = new FileWriter(fileTxt, true);

			if ( item != null ) {
				if ( type.equals("text") ) {
					fw.write(Cast.asString(scope, item.value(scope)) +
						System.getProperty("line.separator"));
				} else if ( type.equals("csv") ) {
					item.getContentType();
					if ( item.getType().id() == IType.LIST ) {
						IList values = Cast.asList(scope, item.value(scope));
						for ( int i = 0; i < values.size() - 1; i++ ) {
							fw.write(Cast.asString(scope, values.get(i)) + ",");
						}
						fw.write(Cast.asString(scope, values.last(scope)) +
							System.getProperty("line.separator"));
					} else {
						fw.write(Cast.asString(scope, item.value(scope)) +
							System.getProperty("line.separator"));
					}
				}
			}
			fw.close();
		} catch (GamaRuntimeException e) {
			throw e;
		} catch (IOException e) {
			throw new GamaRuntimeException(e);
		}

	}

	public String typeJava(final Object obj) {
		if ( obj instanceof Boolean ) { return "Boolean"; }
		if ( obj instanceof Double ) { return "Double"; }
		if ( obj instanceof Integer ) { return "Integer"; }
		return "String";
	}

	public String type(final IType gamaName) {
		if ( gamaName.id() == IType.BOOL ) { return "Boolean"; }
		if ( gamaName.id() == IType.FLOAT ) { return "Double"; }
		if ( gamaName.id() == IType.INT ) { return "Integer"; }
		return "String";
	}

	private void computeInits(final IScope scope, final Map<String, String> values)
		throws GamaRuntimeException {
		if ( init == null ) { return; }
		for ( Facet f : init.entrySet() ) {
			if ( f != null ) {
				values.put(f.getValue().toString(), f.getKey());
			}
		}
	}

	public void saveShapeFile(final IScope scope, final String path,
		final List<? extends IAgent> agents, final String featureTypeName, final String specs,
		final Map<String, String> attributes) throws IOException, SchemaException,
		GamaRuntimeException {

		ShapefileDataStore store = new ShapefileDataStore(new File(path).toURI().toURL());
		SimpleFeatureType type = DataUtilities.createType(featureTypeName, specs);

		store.createSchema(type);
		FeatureStore<FeatureType, Feature> featureStore =
			(FeatureStore) store.getFeatureSource(featureTypeName);
		Transaction t = new DefaultTransaction();
		FeatureCollection collection = FeatureCollections.newCollection();

		int i = 1;

		for ( IAgent ag : agents ) {
			List<Object> liste = new GamaList<Object>();
			Geometry geom = (Geometry) ag.getInnerGeometry().clone();

			// TODO Pr�voir un locationConverter pour passer d'un environnement � l'autre

			geom = scope.getWorldScope().getGisUtils().inverseTransform(geom);
			liste.add(geom);
			if ( attributes != null ) {
				for ( Object e : attributes.values() ) {
					liste.add(ag.getAttribute(e.toString()));
				}
			}

			SimpleFeature simpleFeature =
				SimpleFeatureBuilder.build(type, liste.toArray(), String.valueOf(i++));
			collection.add(simpleFeature);
		}

		featureStore.addFeatures(collection);
		t.commit();
		t.close();
		store.dispose();
		writePRJ(scope, path);
	}

	private void writePRJ(IScope scope, final String path) {
		CoordinateReferenceSystem crs = scope.getWorldScope().getGisUtils().getCrs();
		if ( crs != null ) {
			try {
				FileWriter fw = new FileWriter(path.replace(".shp", ".prj"));
				fw.write(crs.toString());
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setFormalArgs(final Arguments args) {
		init = args;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {
		// TODO Auto-generated method stub
	}
}
