/*********************************************************************************************
*
*
* 'GamaMaterial.java', in plugin 'msi.gama.core', is part of the source code of the
* GAMA modeling and simulation platform.
* (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
*
* Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
*
*
**********************************************************************************************/
package msi.gama.util;

import java.util.Map;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IConstantCategory;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
* The Class GamaMaterial.
*
* @author mazarsju
*/
@vars({ @var(name = IKeyword.DAMPER, type = IType.FLOAT, doc = {
		@doc("Returns the shine damper component of the material") }),
		@var(name = IKeyword.REFLECTIVITY, type = IType.FLOAT, doc = {
				@doc("Returns the reflectivity of the material (between 0 and 1)") }) })
public class GamaMaterial implements IValue {
	
	private double damper;
	private double reflectivity;

	public final static Map<String, GamaMaterial> materials = new THashMap<String, GamaMaterial>();
	public final static TIntObjectMap<GamaMaterial> int_materials = TCollections
			.synchronizedMap(new TIntObjectHashMap<GamaMaterial>());

	static {
		final GamaMaterial steel = new NamedGamaMaterial("steelMaterial", 5, 1);
		materials.put("steelMaterial", steel);
//		int_materials.put(steel.getMatId(), steel);
		
		final GamaMaterial gum = new NamedGamaMaterial("gumMaterial", 1, 0);
		materials.put("gumMaterial", gum);
//		int_materials.put(gum.getMatId(), gum);
	}
	
//	/** The steel material. */
//	@constant(value = "steelMaterial", category = {  }, concept = {  }, doc = { @doc("TODO") })
//	public final static GamaMaterial steelMaterial = new GamaMaterial(5,1);
//	/** The gum material. */
//	@constant(value = "gumMaterial", category = {  }, concept = {  }, doc = { @doc("TODO") })
//	public final static GamaMaterial gumMaterial = new GamaMaterial(1,0);

	public static class NamedGamaMaterial extends GamaMaterial {

		final String name;

		NamedGamaMaterial(final String n, final float damper, final float reflectivity) {
			// c must be of length 4.
			super(damper, reflectivity);
			name = n;
		}

		@Override
		public String toString() {
			return "material[" + name + "]";
		}

		@Override
		public String serialize(final boolean includingBuiltIn) {
			return "°" + name;
		}

		@Override
		public String stringValue(final IScope scope) {
			return name;
		}

	}

	public GamaMaterial(final double damper2, final double reflectivity2) {
		this.damper = damper2;
		this.reflectivity = reflectivity2;
	}
	
	public GamaMaterial(final GamaMaterial material) {
		this.damper = material.getDamper();
		this.reflectivity = material.getReflectivity();
	}

	public double getDamper() {
		return damper;
	}

	public double getReflectivity() {
		return reflectivity;
	}

	@Override
	public String toString() {
		return serialize(true);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "material (damper value : " + getDamper() + ", reflectivity value : " + getReflectivity() + ")";
	}

	@Override
	public String stringValue(final IScope scope) {
		return "material (damper value : " + getDamper() + ", reflectivity value : " + getReflectivity() + ")";
	}

	@getter(IKeyword.REFLECTIVITY)
	public Double reflectivity() {
		return reflectivity;
	}
	
	@getter(IKeyword.DAMPER)
	public Double damper() {
		return damper;
	}

	@Override
	public GamaMaterial copy(final IScope scope) {
		return new GamaMaterial(this);
	}

	/**
	 * Method getType()
	 * 
	 * @see msi.gama.common.interfaces.ITyped#getType()
	 */
	@Override
	public IType getType() {
		return Types.MATERIAL;
	}

}

