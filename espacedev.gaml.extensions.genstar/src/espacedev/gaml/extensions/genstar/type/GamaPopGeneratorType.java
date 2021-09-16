 /*********************************************************************************************
 *
 * 'GamaRegressionType.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

 package espacedev.gaml.extensions.genstar.type;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gaml.types.GamaType;

@type(name = "gen_population_generator", id = 938373948, wraps = {
		GamaPopGenerator.class }, kind = ISymbolKind.Variable.REGULAR, concept = { IConcept.TYPE },doc = {
				@doc("Represents a population generator that can be used to create agents") })
public class GamaPopGeneratorType extends GamaType<GamaPopGenerator> {
	
	@Override
	public boolean canCastToConst() {
		return true;
	}
	
	public void init(){
		this.init(104, 938373948, "population_generator", GamaPopGenerator.class);
	}

	@Override
	@doc("Cast any gaml variable into a GamaPopGenerator used in generation process")
	public GamaPopGenerator cast(final IScope scope, final Object obj, final Object param, final boolean copy) {
		if (obj instanceof GamaPopGenerator) {
			return (GamaPopGenerator) obj;
		}
		return new GamaPopGenerator();
	}

	@Override
	public GamaPopGenerator getDefault() {
		return null;
	}

}
