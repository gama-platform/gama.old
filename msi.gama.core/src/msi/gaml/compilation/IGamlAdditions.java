/*******************************************************************************************************
 *
 * msi.gaml.compilation.IGamlAdditions.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation;

import java.util.Arrays;
import java.util.Collections;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.root.PlatformAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaDate;
import msi.gama.util.GamaPair;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.IGamaFile;
import msi.gama.util.graph.IGraph;
import msi.gama.util.matrix.IMatrix;
import msi.gama.util.path.IPath;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.DeprecatedOperators;
import msi.gaml.skills.GridSkill;
import msi.gaml.skills.MovingSkill;
import msi.gaml.species.ISpecies;

public interface IGamlAdditions {

	public static class Children {

		private final Iterable<IDescription> children;

		public Children(final IDescription... descs) {
			if (descs == null || descs.length == 0) {
				children = Collections.emptyList();
			} else {
				children = Arrays.asList(descs);
			}
		}

		public Iterable<IDescription> getChildren() {
			return children;
		}

	}

	int[] AI = new int[0];
	String[] AS = new String[0];
	boolean F = false;
	boolean T = true;
	String PRIM = IKeyword.PRIMITIVE;
	Class<?> IA = IAgent.class;
	Class<?> IT = ITopology.class;
	Class<?> SP = ISpecies.class;
	Class<?> GA = GamlAgent.class;
	Class<?> GC = GamaColor.class;
	Class<?> GP = GamaPair.class;
	Class<?> GS = GamaShape.class;
	Class<?> O = Object.class;
	Class<?> B = Boolean.class;
	Class<?> I = Integer.class;
	Class<?> D = Double.class;
	Class<?> S = String.class;
	Class<?> IE = IExpression.class;
	Class<?> IS = IShape.class;
	Class<?> GM = IMap.class;
	// public final static Class<?> GL = GamaList.class;
	Class<?> P = GamaPoint.class;
	Class<?> IC = IContainer.class;
	Class<?> IL = ILocation.class;
	Class<?> LI = IList.class;
	Class<?> IM = IMatrix.class;
	Class<?> GR = IGraph.class;
	Class<?> IP = IPath.class;
	Class<?> GF = IGamaFile.class;
	Class<?> MSK = MovingSkill.class;
	Class<?> GSK = GridSkill.class;
	Class<?> SC = IScope.class;
	Class<?> GD = GamaDate.class;
	Class<?> SA = SimulationAgent.class;
	Class<?> EA = ExperimentAgent.class;
	Class<?> DO = DeprecatedOperators.class;
	Class<?> PA = PlatformAgent.class;
	Class<?> i = int.class;
	Class<?> d = double.class;
	Class<?> b = boolean.class;

	void initialize() throws SecurityException, NoSuchMethodException;

}
