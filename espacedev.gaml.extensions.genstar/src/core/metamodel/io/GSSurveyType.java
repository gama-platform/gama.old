package core.metamodel.io;

/**
 * This {@link Enum} represents the types of data a synthetic population generation could input. Each refer to 
 * specific form of data: 
 * 
 * <p><ul>
 * <li> {@link GSSurveyType#Sample}: individual data <p> e.g. indiv1, age = 22, sex = female ..., indiv2, age = 56, sex = male ..., etc.
 * <li> {@link GSSurveyType#ContingencyTable}: contingent of a category of individual <p> e.g. age(22) = 35968 individuals 
 * <li> {@link GSSurveyType#LocalFrequencyTable}: proportion of category related to one referent category <p> e.g. age(22) = 49% / 51% of male / female
 * <li> {@link GSSurveyType#GlobalFrequencyTable}: proportion of a category of individual <p> e.g. age(22) = 12% of the population
 * </ul><p>
 * 
 * @author kevinchapuis
 *
 */
public enum GSSurveyType {

	Sample,
	ContingencyTable,
	LocalFrequencyTable,
	GlobalFrequencyTable;

}
