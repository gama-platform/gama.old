/*******************************************************************************************************
 *
 * GenstarAdderOperators.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package espacedev.gaml.extensions.genstar.old;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.AttributeFactory;
import core.metamodel.io.GSSurveyWrapper;
import core.metamodel.value.IValue;
import core.util.exception.GSIllegalRangedData;
import espacedev.gaml.extensions.genstar.type.GamaPopGenerator;
import espacedev.gaml.extensions.genstar.utils.GenStarConstant;
import espacedev.gaml.extensions.genstar.utils.GenStarConstant.GenerationAlgorithm;
import espacedev.gaml.extensions.genstar.utils.GenStarGamaUtils;
import msi.gama.common.util.FileUtils;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gaml.types.IType;

/**
 * The Class GenstarAdderOperators.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class GenstarAdderOperators {
	
	/**
	 * With generation algo.
	 *
	 * @param gen the gen
	 * @param algo the algo
	 * @return the gama pop generator
	 */
	@operator(value = "with_generation_algo", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "define the algorithm used for the population generation among: IS (independant hypothesis Algorothm) and simple_draw (simple draw of entities in a sample)",
			examples = @example(value = "my_pop_generator with_generation_algo \"simple_draw\"", test = false))
	@no_test
	public static GamaPopGenerator withGenerationAlgo(GamaPopGenerator gen, String algo) {
		if (gen == null) { gen = new GamaPopGenerator(); }
		GenerationAlgorithm alg = GenStarConstant.GenerationAlgorithm.getAlgorithm(algo);
		gen.setGenerationAlgorithm(alg.getDefault());
		return gen;
	}
	
	/**
	 * Adds the census file.
	 *
	 * @param scope the scope
	 * @param gen the gen
	 * @param path the path
	 * @param type the type
	 * @param csvSeparator the csv separator
	 * @param firstRowIndex the first row index
	 * @param firstColumnIndex the first column index
	 * @return the gama pop generator
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	@operator(value = "add_census_file", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add a census data file defined by its path (string), its type (\"ContingencyTable\", \"GlobalFrequencyTable\", \"LocalFrequencyTable\" or  \"Sample\"), its separator (string), the index of the first row of data (int) and the index of the first column of data (int) to a population_generator",
	examples = @example(value = "add_census_file(pop_gen, \"../data/Age_Couple.csv\", \"ContingencyTable\", \";\", 1, 1)", test = false))
	@no_test
	public static GamaPopGenerator addCensusFile(IScope scope, GamaPopGenerator gen, String path, String type, 
			String csvSeparator, int firstRowIndex, int firstColumnIndex) throws GamaRuntimeException {
		Path completePath = Paths.get(FileUtils.constructAbsoluteFilePath(scope, path, false));
		gen.getInputFiles().add(new GSSurveyWrapper(completePath, GenStarGamaUtils.toSurveyType(type), 
				csvSeparator.isEmpty() ? ',':csvSeparator.charAt(0), firstRowIndex, firstColumnIndex));
		return gen;
	}
	
	
	
	/**
	 * Adds the mapper.
	 *
	 * @param scope the scope
	 * @param gen the gen
	 * @param referentAttributeName the referent attribute name
	 * @param dataType the data type
	 * @param values the values
	 * @return the gama pop generator
	 */
	@operator(value = "add_mapper", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add a mapper between source of data for a attribute to a population_generator. "
			+ "A mapper is defined by the name of the attribute, the datatype of attribute (type), "
			+ "the corresponding value (map<list,list>) and the type of attribute (\"unique\" or \"range\")",
		examples = @example(value = " add_mapper(pop_gen, \"Age\", int, [[\"0 to 18\"]::[\"1 to 10\",\"11 to 18\"], "
				+ "[\"18 to 100\"]::[\"18 to 50\",\"51 to 100\"] , \"range\");", test = false))
	@no_test
	public static GamaPopGenerator addMapper(IScope scope, GamaPopGenerator gen, String referentAttributeName, IType dataType, GamaMap values ) {
		return addMapper(scope, gen,referentAttributeName, dataType, values, false);
	}
	

	/**
	 * Adds the mapper.
	 *
	 * @param scope the scope
	 * @param gen the gen
	 * @param referentAttributeName the referent attribute name
	 * @param dataType the data type
	 * @param values the values
	 * @param ordered the ordered
	 * @return the gama pop generator
	 */
	// TODO : remove the type ... 
	@operator(value = "add_mapper", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add a mapper between source of data for a attribute to a population_generator. "
			+ "A mapper is defined by the name of the attribute, the datatype of attribute (type), "
			+ "the corresponding value (map<list,list>) and the type of attribute (\"unique\" or \"range\")",
	examples = @example(value = " add_mapper(pop_gen, \"Age\", int, [[\"0 to 18\"]::[\"1 to 10\",\"11 to 18\"], "
			+ "[\"18 to 100\"]::[\"18 to 50\",\"51 to 100\"] , \"range\");", test = false))
	@no_test
	public static GamaPopGenerator addMapper(IScope scope, GamaPopGenerator gen, String referentAttributeName, IType dataType, GamaMap values, Boolean ordered) {
		if (gen == null) { gen = new GamaPopGenerator(); }
		if (referentAttributeName == null) return gen;
		
		AttributeFactory attf = AttributeFactory.getFactory();		

		Attribute<? extends IValue> referentAttribute = gen.getInputAttributes().getAttribute(referentAttributeName);
		
		if(referentAttribute != null) {	

			try {	
				String name = referentAttribute.getAttributeName() + "_" + (gen.getInputAttributes().getAttributes().size() + 1);
				gen.getInputAttributes().addAttributes(attf.createSTSMappedAttribute(name, GenStarGamaUtils.toDataType(dataType, ordered), referentAttribute, values));

				// We lose an information in the case it is an aggregatre 
				// Si keys ont 1 seules element -> aggregation
				
			//	dd.addAttributes(attf.createRangeAggregatedAttribute("Age_2", new GSDataParser()
			//			.getRangeTemplate(mapperA1.keySet().stream().toList()),
			//			referentAgeAttribute, mapperA1));
				
			} catch (GSIllegalRangedData e) {
				throw GamaRuntimeException.error("Wrong type in the record." + e.getMessage(), scope);
			}				
		}
		return gen;
	}
	

	
	/**
	 * Adds the attribute.
	 *
	 * @param scope the scope
	 * @param gen the gen
	 * @param name the name
	 * @param dataType the data type
	 * @param value the value
	 * @return the gama pop generator
	 */
	@operator(value = "add_attribute", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add an attribute defined by its name (string), its datatype (type), its list of values (list) to a population_generator",
			examples = @example(value = "add_attribute(pop_gen, \"Sex\", string,[\"Man\", \"Woman\"])", test = false))
	@no_test
	public static GamaPopGenerator addAttribute(IScope scope, GamaPopGenerator gen, String name, IType dataType, IList value) {
		return addAttribute(scope, gen, name, dataType, value, false);
	}
	
	/**
	 * Adds the attribute.
	 *
	 * @param scope the scope
	 * @param gen the gen
	 * @param name the name
	 * @param ranges the ranges
	 * @param lowest the lowest
	 * @param highest the highest
	 * @return the gama pop generator
	 */
	@operator(value = "add_range_attribute", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add a rangee attribute defined by its name (string), the list of ranges (list) to a population_generator",
			examples = @example(value = "add_attribute(pop_gen, \"Sex\", string,[\"Man\", \"Woman\"])", test = false))
	@no_test
	public static GamaPopGenerator addAttribute(IScope scope, GamaPopGenerator gen, String name, IList ranges, int lowest, int highest) {
		if (gen == null) { gen = new GamaPopGenerator(); }
		Attribute<? extends IValue> newAttribute = null;
		try {
			newAttribute = gen.getAttf().createRangeAttribute(name, ranges, lowest, highest);
		} catch (GSIllegalRangedData | NullPointerException e) {
			GamaRuntimeException.create(e, scope);
		}
		gen.getInputAttributes().addAttributes(newAttribute);
		return gen;
	}

	/**
	 * Adds the attribute.
	 *
	 * @param scope the scope
	 * @param gen the gen
	 * @param name the name
	 * @param dataType the data type
	 * @param value the value
	 * @param record the record
	 * @param recordType the record type
	 * @return the gama pop generator
	 */
	@operator(value = "add_attribute", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add an attribute defined by its name (string), its datatype (type), its list of values (list) "
			+ "and attributeType name (type of the attribute among \"range\" and \"unique\") to a population_generator", 
			examples = @example(value = "add_attribute(pop_gen, \"iris\", string, liste_iris, \"unique\")", test = false))
	@no_test
	public static GamaPopGenerator addAttribute(IScope scope, GamaPopGenerator gen, String name, IType dataType, 
			IList value, String record, IType recordType) {
		return addAttribute(scope, gen, name, dataType, value, false, record, recordType);
	}	

	/**
	 * Adds the attribute.
	 *
	 * @param scope the scope
	 * @param gen the gen
	 * @param name the name
	 * @param dataType the data type
	 * @param value the value
	 * @param ordered the ordered
	 * @param record the record
	 * @param recordType the record type
	 * @return the gama pop generator
	 */
	@operator(value = "add_attribute", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add an attribute defined by its name (string), its datatype (type), its list of values (list) to a population_generator",
			examples = @example(value = "add_attribute(pop_gen, \"Sex\", string,[\"Man\", \"Woman\"])", test = false))
	@no_test
	public static GamaPopGenerator addAttribute(IScope scope, GamaPopGenerator gen, String name, IType dataType, 
			IList value, Boolean ordered, String record, IType recordType) {
		if (gen == null) { gen = new GamaPopGenerator(); }
		
		GamaPopGenerator genPop = addAttribute(scope, gen, name, dataType, value, ordered);
		genPop.getInputAttributes().addRecords();
		try {
			genPop.getInputAttributes().addRecords(
				gen.getAttf().createRecordAttribute(
					record, 
					GenStarGamaUtils.toDataType(recordType,false)/*GSEnumDataType.Integer*/, 
					genPop.getInputAttributes().getAttribute(name)
				)
			);
		} catch (GSIllegalRangedData e) {
			throw GamaRuntimeException.error("Wrong type for the record. " + e.getMessage(), scope);
		}
	
		return genPop;
		
	}	
	
	/**
	 * Adds the attribute.
	 *
	 * @param scope the scope
	 * @param gen the gen
	 * @param name the name
	 * @param dataType the data type
	 * @param value the value
	 * @param ordered the ordered
	 * @return the gama pop generator
	 */
	@operator(value = "add_attribute", can_be_const = true, category = { "Gen*" }, concept = { "Gen*"})
	@doc(value = "add an attribute defined by its name (string), its datatype (type), its list of values (list) and "
			+ "record name (name of the attribute to record) to a population_generator", 
			examples = @example(value = "add_attribute(pop_gen, \"iris\", string,liste_iris, \"unique\", \"P13_POP\")", test = false))
	@no_test
	public static GamaPopGenerator addAttribute(IScope scope, GamaPopGenerator gen, String name, IType dataType, IList value, Boolean ordered) {
		if (gen == null) { gen = new GamaPopGenerator(); }
		
		try {
			Attribute<? extends IValue> newAttribute = 
					gen.getAttf().createAttribute(name, GenStarGamaUtils.toDataType(dataType,ordered), value);
			
			gen.getInputAttributes().addAttributes(newAttribute);
			 
			 // TODO : � revoir les records ..........
		//	 if (record != null && ! record.isEmpty()) {
		//		 gen.getInputAttributes().addRecords()
		//				 gen.getAttf()
		//				 .createIntegerRecordAttribute(record,newAttribute,Collections.emptyMap()));
			
			/*Attribute<? extends IValue> attIris = gen.getAttf()
					.createAttribute(name, toDataType(dataType,ordered), value);
			 gen.getInputAttributes().addAttributes(attIris);
			 if (record != null && ! record.isEmpty()) {
				 gen.getRecordAttributes().addAttributes(gen.getAttf()
						 .createRecordAttribute("population", GSEnumDataType.Integer,
								 attIris, Collections.emptyMap()));*/
		//	 }
		} catch (GSIllegalRangedData e) {
			throw GamaRuntimeException.error("Wrong type in the record." + e.getMessage(), scope);
		}
		return gen;
	}
	
	/**
	 * Adds the marginals.
	 *
	 * @param scope the scope
	 * @param gen the gen
	 * @param names the names
	 * @return the gama pop generator
	 */
	@operator(value = "add_marginals", category = { "Gen*" }, concept = { "Gen*" })
	@doc(value = "add a list of marginals (name of the attributes) to fit the population with, in any CO based algorithm",
			examples = @example(value = "add_marginals(pop_gen, [\"gender\",\"age\"]);"))
	@no_test
	public static GamaPopGenerator addMarginals(IScope scope, GamaPopGenerator gen, IList names) {
		if (gen == null) { gen = new GamaPopGenerator(); }
		gen.setMarginals(gen.getInputAttributes().getAttributes().stream()
				.filter(att -> names.contains(att.getAttributeName()))
				.toList());
		return gen;
	}
	
}