/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.commands;

import java.io.*;
import java.util.*;
import msi.gama.environment.GisUtil;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gaml.expressions.MapExpression;
import msi.gaml.operators.Casting;
import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.*;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.*;
import org.opengis.feature.type.FeatureType;
import com.vividsolutions.jts.geom.Geometry;

@symbol(name = ISymbol.SAVE, kind = ISymbolKind.SINGLE_COMMAND)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
@facets({ @facet(name = ISymbol.SPECIES, type = IType.SPECIES_STR, optional = true),
	@facet(name = ISymbol.TYPE, type = IType.STRING_STR, optional = true),
	@facet(name = ISymbol.ITEM, type = IType.NONE_STR, optional = true),
	@facet(name = ISymbol.TO, type = IType.STRING_STR, optional = false),
	@facet(name = ISymbol.WITH, type = { IType.MAP_STR }, optional = true) })
public class SaveCommand extends AbstractCommandSequence {

	private WithCommand att;

	public SaveCommand(final IDescription desc) {
		super(desc);
	}

	@Override
	public Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		IExpression typeExp = getFacet(ISymbol.TYPE);
		IExpression file = getFacet(ISymbol.TO);
		String path = "";
		if ( file == null ) {
			stack.setStatus(ExecutionStatus.failure);
			return null;
		}
		try {
			path =
				stack.getSimulationScope().getModel()
					.getRelativeFilePath(Cast.asString(file.value(stack)), false);
		} catch (GamlException e) {
			e.printStackTrace();
		}
		if ( path.equals("") ) {
			stack.setStatus(ExecutionStatus.failure);
			return null;
		}
		String type = "text";
		if ( typeExp != null ) {
			type = typeExp.value(stack).toString();
		}
		if ( type.equals("shp") ) {
			IExpression item = getFacet(ISymbol.ITEM);
			List<? extends IAgent> agents;
			if ( item == null ) {
				IExpression speciesExpr = getFacet(ISymbol.SPECIES);
				if ( speciesExpr == null ||
					Casting.asSpecies(stack, speciesExpr.value(stack)) == null ) {
					stack.setStatus(ExecutionStatus.failure);
					return null;
				}
				agents =
					stack.getAgentScope()
						.getPopulationFor(Casting.asSpecies(stack, speciesExpr.value(stack)))
						.getAgentsList();
			} else {
				agents = Cast.asList(stack, item.value(stack));
			}
			if ( agents == null ) {
				stack.setStatus(ExecutionStatus.failure);
				return null;
			}
			if ( agents.isEmpty() ) {}
			saveShape(agents, path, stack);
		} else if ( type.equals("text") || type.equals("csv") ) {
			File fileTxt = new File(path);
			IExpression item = getFacet(ISymbol.ITEM);
			// if (fileTxt != null) {
			saveText(type, item, fileTxt, stack);
			// }
		}
		return Cast.asString(file.value(stack));
	}

	public void saveShape(final List<? extends IAgent> agents, final String path, final IScope stack)
		throws GamaRuntimeException {
		GamaMap attributes = null;
		if ( att != null ) {
			MapExpression mapExpr = (MapExpression) att.getFacet(ISymbol.INIT);
			attributes = mapExpr.getElements();
		}
		StringBuilder specs = new StringBuilder();
		for ( IAgent be : agents ) {
			if ( be.getGeometry() != null ) {
				IAgent ag = be;
				specs.append("geom:" + ag.getInnerGeometry().getClass().getSimpleName());
				break;
			}
		}

		try {
			if ( attributes != null ) {
				for ( IExpression e : (Set<IExpression>) attributes.keySet() ) {
					specs.append(',')
						.append(Cast.asString(((IExpression) attributes.get(e)).value(stack)))
						.append(':').append(type(e.getContentType()));
				}
			}
			String featureTypeName = agents.get(0).getSpeciesName();

			saveShapeFile(stack, path, agents, featureTypeName, specs.toString(), attributes);
		} catch (GamaRuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new GamaRuntimeException(e);
		}

	}

	public void saveText(final String type, final IExpression item, final File fileTxt,
		final IScope stack) throws GamaRuntimeException {
		try {
			fileTxt.createNewFile();
			FileWriter fw = new FileWriter(fileTxt, true);

			if ( item != null ) {
				if ( type.equals("text") ) {
					fw.write(Cast.asString(item.value(stack)) +
						System.getProperty("line.separator"));
				} else if ( type.equals("csv") ) {
					item.getContentType();
					if ( item.type().id() == IType.LIST ) {
						GamaList values = Cast.asList(stack, item.value(stack));
						for ( int i = 0; i < values.size() - 1; i++ ) {
							fw.write(Cast.asString(values.get(i)) + ",");
						}
						fw.write(Cast.asString(values.last()) +
							System.getProperty("line.separator"));
					} else {
						fw.write(Cast.asString(item.value(stack)) +
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

	public void setCommands(final List<ICommand> com) {
		for ( ICommand c : com ) {
			if ( c instanceof WithCommand ) {
				att = (WithCommand) c;
				// att.setEnclosingScope(this);
			}
		}
	}

	public String type(final IType gamaName) {
		if ( gamaName.id() == IType.BOOL ) { return "Boolean"; }
		if ( gamaName.id() == IType.FLOAT ) { return "Double"; }
		if ( gamaName.id() == IType.INT ) { return "Integer"; }
		return "String";
	}

	public void saveShapeFile(final IScope scope, final String path,
		final List<? extends IAgent> agents, final String featureTypeName, final String specs,
		final GamaMap attributes) throws IOException, SchemaException, GamaRuntimeException {

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

			// TODO Prévoir un locationConverter pour passer d'un environnement à l'autre

			geom = GisUtil.fromAbsoluteToGis(geom);
			liste.add(geom);
			if ( attributes != null ) {
				for ( Object e : attributes.keySet() ) {
					liste.add(scope.evaluate((IExpression) e, ag));
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

	}
}
