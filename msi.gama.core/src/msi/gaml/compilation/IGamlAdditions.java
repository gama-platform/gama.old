/*********************************************************************************************
 * 
 * 
 * 'IGamlAdditions.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import java.util.Collections;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaPair;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.file.IGamaFile;
import msi.gama.util.graph.IGraph;
import msi.gama.util.matrix.IMatrix;
import msi.gama.util.path.IPath;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.skills.GridSkill;
import msi.gaml.skills.MovingSkill;
import msi.gaml.species.ISpecies;

public interface IGamlAdditions {

	public static class ChildrenProvider {

		private final Iterable<IDescription> children;

		public ChildrenProvider(final Iterable<IDescription> descs) {
			children = descs;
		}

		public Iterable<IDescription> getChildren() {
			return children == null ? Collections.EMPTY_LIST : children;
		}

	}

	public final static int[] AI = new int[0];
	public final static String[] AS = new String[0];
	public final static boolean F = false;
	public final static boolean T = true;
	public final static Class<?> IA = IAgent.class;
	public final static Class<?> IT = ITopology.class;
	public final static Class<?> SP = ISpecies.class;
	public final static Class<?> GA = GamlAgent.class;
	public final static Class<?> GC = GamaColor.class;
	public final static Class<?> GP = GamaPair.class;
	public final static Class<?> GS = GamaShape.class;
	public final static Class<?> O = Object.class;
	public final static Class<?> B = Boolean.class;
	public final static Class<?> I = Integer.class;
	public final static Class<?> D = Double.class;
	public final static Class<?> S = String.class;
	public final static Class<?> IE = IExpression.class;
	public final static Class<?> IS = IShape.class;
	public final static Class<?> GM = GamaMap.class;
	public final static Class<?> GL = GamaList.class;
	public final static Class<?> P = GamaPoint.class;
	public final static Class<?> IC = IContainer.class;
	public final static Class<?> IL = ILocation.class;
	public final static Class<?> LI = IList.class;
	public final static Class<?> IM = IMatrix.class;
	public final static Class<?> GR = IGraph.class;
	public final static Class<?> IP = IPath.class;
	public final static Class<?> GF = IGamaFile.class;
	public final static Class<?> MSK = MovingSkill.class;
	public final static Class<?> GSK = GridSkill.class;

	public void initialize() throws SecurityException, NoSuchMethodException;

}
