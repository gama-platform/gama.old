/*******************************************************************************************************
 *
 * GamlQualifiedNameProvider.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.naming;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.gaml.ArgumentPair;
import msi.gama.lang.gaml.gaml.GamlDefinition;
import msi.gama.lang.gaml.gaml.S_Reflex;
import msi.gama.lang.gaml.gaml.S_SpeciesLayer;
import msi.gama.lang.gaml.gaml.StandaloneModel;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gaml.descriptions.ModelDescription;

/**
 * GAML Qualified Name provider.
 *
 */
public class GamlQualifiedNameProvider extends IQualifiedNameProvider.AbstractImpl {

	/** The Constant NULL. */
	private final static String NULL = "";

	/** The Constant SWITCH. */
	private final static GamlSwitch<String> SWITCH = new GamlSwitch<>() {

		@Override
		public String caseS_Reflex(final S_Reflex s) {
			if (IKeyword.ASPECT.equals(s.getKey())) return s.getName();
			return NULL;
		}

		 @Override
		 public String caseS_SpeciesLayer(final S_SpeciesLayer s) {
		 return NULL;
		 }

		@Override
		public String caseStandaloneModel(final StandaloneModel o) {
			return o.getName() + ModelDescription.MODEL_SUFFIX;
		}

		@Override
		public String defaultCase(final EObject e) {
			return NULL;
		}

		@Override
		public String caseGamlDefinition(final GamlDefinition object) {
			return object.getName();
		}

		@Override
		public String caseArgumentPair(final ArgumentPair object) {
			return object.getOp();
		}

	};

	@Override
	public QualifiedName getFullyQualifiedName(final EObject input) {
		final String string = SWITCH.doSwitch(input);
		if (string == null || NULL.equals(string)) return null;
		return QualifiedName.create(string);
	}

}