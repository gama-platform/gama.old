/*******************************************************************************************************
 *
 * msi.gaml.extensions.multi_criteria.MulticriteriaAnalyzeOperator.java, in plugin msi.gama.core, is part of the source
 * code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.extensions.multi_criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class MulticriteriaAnalyzeOperator {

	final static String MULTICRITERIA = "multicriteria operators";

	@operator (
			value = "weighted_means_DM",
			category = { MULTICRITERIA },
			concept = { IConcept.MULTI_CRITERIA })
	@doc (
			value = "The index of the candidate that maximizes the weighted mean of its criterion values. The first operand is the list of candidates (a candidate is a list of criterion values); the second operand the list of criterion (list of map)",
			special_cases = { "returns -1 is the list of candidates is nil or empty" },
			examples = { @example (
					value = "weighted_means_DM([[1.0, 7.0],[4.0,2.0],[3.0, 3.0]], [[\"name\"::\"utility\", \"weight\" :: 2.0],[\"name\"::\"price\", \"weight\" :: 1.0]])",
					equals = "1") },
			see = { "promethee_DM", "electre_DM", "evidence_theory_DM" })
	public static Integer WeightedMeansDecisionMaking(final IScope scope, final IList<List> cands,
			final IList<Map<String, Object>> criteriaMap) throws GamaRuntimeException {
		if (cands == null || cands.isEmpty()) { return -1; }
		final List<String> criteriaStr = new LinkedList<>();
		final Map<String, Double> weight = new HashMap<>();
		for (final Map<String, Object> critMap : criteriaMap) {
			final String name = (String) critMap.get("name");
			criteriaStr.add(name);
			final Double w = Cast.asFloat(scope, critMap.get("weight"));
			if (w != null) {
				weight.put(name, w);
			} else {
				weight.put(name, 1.0);
			}
		}
		int cpt = 0;
		double utilityMax = -Double.MAX_VALUE;
		final IList<Integer> bestCands = GamaListFactory.create(Types.INT);
		for (final List cand : cands) {
			int i = 0;
			double utility = 0;
			for (final String crit : criteriaStr) {
				utility += weight.get(crit) * Cast.asFloat(scope, cand.get(i));
				i++;
			}
			if (utilityMax == utility) {
				bestCands.add(cpt);
			} else if (utilityMax < utility) {
				bestCands.clear();
				bestCands.add(cpt);
				utilityMax = utility;
			}
			cpt++;
		}
		return bestCands.anyValue(scope);
	}

	public static void buildCombination(final List<String> criteria, final Set<String> currentSol,
			final Set<Set<String>> combinations, final int start, final int end, final int index) {
		if (index == criteria.size()) {
			combinations.add(new HashSet<>(criteria));
			return;
		}

		for (int i = start; i <= end; i++) {
			final Set<String> comb = new HashSet<>(currentSol);
			comb.add(criteria.get(i));
			combinations.add(comb);
			buildCombination(criteria, comb, combinations, i + 1, end, index + 1);
		}
	}

	@operator (
			value = "fuzzy_choquet_DM",
			category = { MULTICRITERIA },
			concept = { IConcept.MULTI_CRITERIA })
	@doc (
			value = "The index of the candidate that maximizes the Fuzzy Choquet Integral value. The first operand is the list of candidates (a candidate is a list of criterion values); the second operand the list of criterion (list of string); the third operand the weights of each sub-set of criteria (map with list for key and float for value)",
			special_cases = { "returns -1 is the list of candidates is nil or empty" },
			examples = { @example (
					value = "fuzzy_choquet_DM([[1.0, 7.0],[4.0,2.0],[3.0, 3.0]], [\"utility\", \"price\", \"size\"],[[\"utility\"]::0.5,[\"size\"]::0.1,[\"price\"]::0.4,[\"utility\", \"price\"]::0.55])",
					equals = "0") },
			see = { "promethee_DM", "electre_DM", "evidence_theory_DM" })
	public static Integer FuzzyChoquetDecisionMaking(final IScope scope, final IList<List> cands,
			final IList<String> criteria, final IMap criteriaWeights) throws GamaRuntimeException {
		if (cands == null || cands.isEmpty()) { return -1; }
		final Map<String, Double> critWeight = new HashMap<>();
		final Map<Set<String>, Double> weight = new HashMap<>();
		for (final Object o : criteriaWeights.keySet()) {
			final Set<String> key = new HashSet<>((List) o);
			final Double val = Cast.asFloat(scope, criteriaWeights.get(o));
			if (key.size() == 1) {
				critWeight.put(new ArrayList<>(key).get(0), val);
			}
			weight.put(key, val);
		}
		for (final String crit : criteria) {
			if (!critWeight.containsKey(crit)) {
				critWeight.put(crit, 1.0);
			}
		}
		final Set<Set<String>> combinations = new HashSet<>();
		buildCombination(criteria, new HashSet<>(), combinations, 0, criteria.size() - 1, 0);
		for (final Set<String> comb : combinations) {
			if (!weight.containsKey(comb)) {
				Double tot = 0.0;
				for (final String c : comb) {
					tot += Cast.asFloat(scope, critWeight.get(c));
				}
				weight.put(comb, tot);
			}
		}
		int cpt = 0;
		double utilityMax = -Double.MAX_VALUE;
		int indexCand = -1;
		boolean first = true;
		for (final List cand : cands) {
			final List orderedList = new ArrayList<>(cand);
			final List<Integer> listOfOrder = new ArrayList<>();
			Collections.sort(orderedList);
			for (final Object val : cand) {
				int index = orderedList.indexOf(val);
				while (listOfOrder.contains(index)) {
					index++;
				}
				// if (listOfOrder.contains(index))
				// index ++;
				listOfOrder.add(index);
			}
			double prev = Cast.asFloat(scope, orderedList.get(0));
			double utility = prev;
			for (int i = 1; i < orderedList.size(); i++) {
				final double val = Cast.asFloat(scope, orderedList.get(i));
				final Set<String> comb = new HashSet<>();
				for (int j = i; j < listOfOrder.size(); j++) {
					comb.add(criteria.get(listOfOrder.indexOf(j)));
				}
				utility += (val - prev) * weight.get(comb);
				prev = val;
			}

			if (first || utilityMax < utility) {
				utilityMax = utility;
				indexCand = cpt;
				first = false;
			}
			cpt++;
		}
		return indexCand;

	}

	@operator (
			value = "promethee_DM",
			category = { MULTICRITERIA },
			concept = { IConcept.MULTI_CRITERIA })
	@doc (
			value = "The index of the best candidate according to the Promethee II method. This method is based on a comparison per pair of possible candidates along each criterion: all candidates are compared to each other by pair and ranked. More information about this method can be found in [http://www.sciencedirect.com/science?_ob=ArticleURL&_udi=B6VCT-4VF56TV-1&_user=10&_coverDate=01%2F01%2F2010&_rdoc=1&_fmt=high&_orig=search&_sort=d&_docanchor=&view=c&_searchStrId=1389284642&_rerunOrigin=google&_acct=C000050221&_version=1&_urlVersion=0&_userid=10&md5=d334de2a4e0d6281199a39857648cd36 Behzadian, M., Kazemzadeh, R., Albadvi, A., M., A.: PROMETHEE: A comprehensive literature review on methodologies and applications. European Journal of Operational Research(2009)]. The first operand is the list of candidates (a candidate is a list of criterion values); the second operand the list of criterion: A criterion is a map that contains fours elements: a name, a weight, a preference value (p) and an indifference value (q). The preference value represents the threshold from which the difference between two criterion values allows to prefer one vector of values over another. The indifference value represents the threshold from which the difference between two criterion values is considered significant.",
			special_cases = { "returns -1 is the list of candidates is nil or empty" },
			examples = { @example (
					value = "promethee_DM([[1.0, 7.0],[4.0,2.0],[3.0, 3.0]], [[\"name\"::\"utility\", \"weight\" :: 2.0,\"p\"::0.5, \"q\"::0.0, \"s\"::1.0, \"maximize\" :: true],[\"name\"::\"price\", \"weight\" :: 1.0,\"p\"::0.5, \"q\"::0.0, \"s\"::1.0, \"maximize\" :: false]])",
					equals = "1") },
			see = { "weighted_means_DM", "electre_DM", "evidence_theory_DM" })
	public static Integer PrometheeDecisionMaking(final IScope scope, final IList<List> cands,
			final IList<Map<String, Object>> criteriaMap) throws GamaRuntimeException {
		if (cands == null || cands.isEmpty()) { return -1; }
		int cpt = 0;
		final LinkedList<Candidate> candidates = new LinkedList<>();
		final List<String> criteriaStr = new LinkedList<>();
		final Map<String, FonctionPreference> fctPrefCrit = new HashMap<>();
		final Map<String, Double> weight = new Hashtable<>();
		for (final Map<String, Object> critMap : criteriaMap) {
			final String name = (String) critMap.get("name");
			criteriaStr.add(name);
			final Double w = Cast.asFloat(scope, critMap.get("weight"));
			if (w != null) {
				weight.put(name, w);
			} else {
				weight.put(name, 1.0);
			}
			String typeFct = "type_5";
			final Object typeObj = critMap.get("type");
			if (typeObj != null) {
				typeFct = typeObj.toString();
			}
			final Object q = critMap.get("q");
			final Object p = critMap.get("p");
			final Object s = critMap.get("s");
			Double pf = 1.0, qf = 0.0, sf = 1.0;
			if (q != null) {
				qf = Cast.asFloat(scope, q);
			}
			if (p != null) {
				pf = Cast.asFloat(scope, p);
			}
			if (s != null) {
				sf = Cast.asFloat(scope, s);
			}

			if (typeFct.equals("type_5")) {
				fctPrefCrit.put(name, new PreferenceType5(qf, pf));
			} else if (typeFct.equals("type_6")) {
				fctPrefCrit.put(name, new PreferenceType6(sf));
			}

		}
		final Promethee promethee = new Promethee(criteriaStr);
		promethee.setFctPrefCrit(fctPrefCrit);
		promethee.setPoidsCrit(weight);

		for (final List cand : cands) {
			final Map<String, Double> valCriteria = new HashMap<>();
			int i = 0;
			for (final String crit : criteriaStr) {
				valCriteria.put(crit, Cast.asFloat(scope, cand.get(i)));
				i++;
			}
			final Candidate c = new Candidate(cpt, valCriteria);
			candidates.add(c);
			cpt++;
		}
		final LinkedList<Candidate> candsFilter = filtering(candidates, new HashMap<String, Boolean>());
		if (candsFilter.isEmpty()) { return scope.getRandom().between(0, candidates.size() - 1); }
		if (candsFilter.size() == 1) {
			return ((Candidate) GamaListFactory.create(scope, Types.NO_TYPE, (Iterable) candsFilter).firstValue(scope))
					.getIndex();
		}
		final Candidate decision = promethee.decision(candsFilter);
		return decision.getIndex();

	}

	@operator (
			value = "electre_DM",
			category = { MULTICRITERIA },
			concept = { IConcept.MULTI_CRITERIA })
	@doc (
			value = "The index of the best candidate according to a method based on the ELECTRE methods. The principle of the ELECTRE methods is to compare the possible candidates by pair. These methods analyses the possible outranking relation existing between two candidates. An candidate outranks another if this one is at least as good as the other one. The ELECTRE methods are based on two concepts: the concordance and the discordance. The concordance characterizes the fact that, for an outranking relation to be validated, a sufficient majority of criteria should be in favor of this assertion. The discordance characterizes the fact that, for an outranking relation to be validated, none of the criteria in the minority should oppose too strongly this assertion. These two conditions must be true for validating the outranking assertion. More information about the ELECTRE methods can be found in [http://www.springerlink.com/content/g367r44322876223/	Figueira,  J., Mousseau, V., Roy, B.: ELECTRE Methods. In: Figueira, J., Greco, S., and Ehrgott, M., (Eds.), Multiple Criteria Decision Analysis: State of the Art Surveys, Springer, New York, 133--162 (2005)]. The first operand is the list of candidates (a candidate is a list of criterion values); the second operand the list of criterion: A criterion is a map that contains fives elements: a name, a weight, a preference value (p), an indifference value (q) and a veto value (v). The preference value represents the threshold from which the difference between two criterion values allows to prefer one vector of values over another. The indifference value represents the threshold from which the difference between two criterion values is considered significant. The veto value represents the threshold from which the difference between two criterion values disqualifies the candidate that obtained the smaller value; the last operand is the fuzzy cut.",
			special_cases = { "returns -1 is the list of candidates is nil or empty" },
			examples = { @example (
					value = "electre_DM([[1.0, 7.0],[4.0,2.0],[3.0, 3.0]], [[\"name\"::\"utility\", \"weight\" :: 2.0,\"p\"::0.5, \"q\"::0.0, \"s\"::1.0, \"maximize\" :: true],[\"name\"::\"price\", \"weight\" :: 1.0,\"p\"::0.5, \"q\"::0.0, \"s\"::1.0, \"maximize\" :: false]],0.7)",
					equals = "0") },
			see = { "weighted_means_DM", "promethee_DM", "evidence_theory_DM" })
	public static Integer electreDecisionMaking(final IScope scope, final IList<List> cands,
			final IList<Map<String, Object>> criteriaMap, final Double cut) throws GamaRuntimeException {
		Double fuzzyCut = cut;
		if (fuzzyCut == null) {
			fuzzyCut = Double.valueOf(0.7);
		}
		if (cands == null || cands.isEmpty()) { return -1; }
		int cpt = 0;
		final List<Candidate> candidates = new ArrayList<>();
		final List<String> criteriaStr = GamaListFactory.create(Types.STRING);
		final Map<String, Double> weight = new HashMap<>();
		final Map<String, Double> preference = new HashMap<>();
		final Map<String, Double> indifference = new HashMap<>();
		final Map<String, Double> veto = new HashMap<>();
		for (final Map<String, Object> critMap : criteriaMap) {
			final String name = (String) critMap.get("name");
			criteriaStr.add(name);
			final Double w = Cast.asFloat(scope, critMap.get("weight"));
			if (w != null) {
				weight.put(name, w);
			} else {
				weight.put(name, 1.0);
			}
			final Object p = critMap.get("p");
			final Object q = critMap.get("q");
			final Object v = critMap.get("v");
			Double pf = 0.5, qf = 0.0, vf = 1.0;

			if (q != null) {
				qf = Cast.asFloat(scope, q);
			}

			indifference.put(name, qf);

			if (p != null) {
				pf = Cast.asFloat(scope, p);
			}
			preference.put(name, pf);

			if (v != null) {
				vf = Cast.asFloat(scope, v);
			}
			veto.put(name, vf);
		}
		final Electre electre = new Electre(criteriaStr);
		electre.setPoids(weight);
		electre.setIndifference(indifference);
		electre.setPreference(preference);
		electre.setVeto(veto);
		electre.setSeuilCoupe(fuzzyCut);

		for (final List cand : cands) {
			final Map<String, Double> valCriteria = new HashMap<>();
			int i = 0;
			for (final String crit : criteriaStr) {
				valCriteria.put(crit, Cast.asFloat(scope, cand.get(i)));
				i++;
			}
			final Candidate c = new Candidate(cpt, valCriteria);
			candidates.add(c);
			cpt++;
		}
		final LinkedList<Candidate> candsFilter = filtering(candidates, new HashMap<String, Boolean>());
		if (candsFilter.isEmpty()) { return scope.getRandom().between(0, candidates.size() - 1); }
		final Candidate decision = electre.decision(candsFilter);
		return decision.getIndex();

	}

	@operator (
			value = "evidence_theory_DM",
			category = { MULTICRITERIA },
			concept = { IConcept.MULTI_CRITERIA })
	@doc (
			examples = { @example (
					value = "evidence_theory_DM([[1.0, 7.0],[4.0,2.0],[3.0, 3.0]], [[\"name\"::\"utility\", \"s1\" :: 0.0,\"s2\"::1.0, \"v1p\"::0.0, \"v2p\"::1.0, \"v1c\"::0.0, \"v2c\"::0.0, \"maximize\" :: true],[\"name\"::\"price\",  \"s1\" :: 0.0,\"s2\"::1.0, \"v1p\"::0.0, \"v2p\"::1.0, \"v1c\"::0.0, \"v2c\"::0.0, \"maximize\" :: true]])",
					equals = "0") },

			usages = { @usage (
					value = "if the operator is used with only 2 operands (the candidates and the criteria), the last parameter (use simple method) is set to true"), })
	public static Integer evidenceTheoryDecisionMaking(final IScope scope, final IList<List> cands,
			final IList<Map<String, Object>> criteriaMap) throws GamaRuntimeException {
		return evidenceTheoryDecisionMaking(scope, cands, criteriaMap, true);
	}

	@operator (
			value = "evidence_theory_DM",
			category = { MULTICRITERIA },
			concept = { IConcept.MULTI_CRITERIA })
	@doc (
			value = "The index of the best candidate according to a method based on the Evidence theory. This theory, which was proposed by Shafer ([http://www.glennshafer.com/books/amte.html Shafer G (1976) A mathematical theory of evidence, Princeton University Press]), is based on the work of Dempster ([http://projecteuclid.org/DPubS?service=UI&version=1.0&verb=Display&handle=euclid.aoms/1177698950 Dempster A (1967) Upper and lower probabilities induced by multivalued mapping. Annals of Mathematical Statistics, vol.  38, pp. 325--339]) on lower and upper probability distributions. The first operand is the list of candidates (a candidate is a list of criterion values); the second operand the list of criterion: A criterion is a map that contains seven elements: a name, a first threshold s1, a second threshold s2, a value for the assertion \"this candidate is the best\" at threshold s1 (v1p), a value for the assertion \"this candidate is the best\" at threshold s2 (v2p), a value for the assertion \"this candidate is not the best\" at threshold s1 (v1c), a value for the assertion \"this candidate is not the best\" at threshold s2 (v2c). v1p, v2p, v1c and v2c have to been defined in order that: v1p + v1c <= 1.0; v2p + v2c <= 1.0.; the last operand allows to use a simple version of this multi-criteria decision making method (simple if true)",
			masterDoc = true,
			special_cases = { "returns -1 is the list of candidates is nil or empty" },
			examples = { @example (
					value = "evidence_theory_DM([[1.0, 7.0],[4.0,2.0],[3.0, 3.0]], [[\"name\"::\"utility\", \"s1\" :: 0.0,\"s2\"::1.0, \"v1p\"::0.0, \"v2p\"::1.0, \"v1c\"::0.0, \"v2c\"::0.0, \"maximize\" :: true],[\"name\"::\"price\",  \"s1\" :: 0.0,\"s2\"::1.0, \"v1p\"::0.0, \"v2p\"::1.0, \"v1c\"::0.0, \"v2c\"::0.0, \"maximize\" :: true]], false)",
					equals = "0") },
			see = { "weighted_means_DM", "electre_DM", "electre_DM" })
	public static Integer evidenceTheoryDecisionMaking(final IScope scope, final IList<List> cands,
			final IList<Map<String, Object>> criteriaMap, final Boolean isSimple) throws GamaRuntimeException {
		if (cands == null || cands.isEmpty()) { return -1; }
		int cpt = 0;
		final boolean simple = isSimple == null ? false : isSimple;
		final Map<String, Boolean> maximizeCrit = new HashMap<>();
		final LinkedList<Candidate> candidates = new LinkedList<>();
		final List<String> criteriaStr = new LinkedList<>();
		final LinkedList<CritereFonctionsCroyances> criteresFC = new LinkedList<>();
		for (final Map<String, Object> critMap : criteriaMap) {
			final String name = (String) critMap.get("name");
			criteriaStr.add(name);
			final Object s1r = critMap.get("s1");
			Double s1 = 0.0, s2 = 1.0, v1Pour = 0.0, v2Pour = 1.0, v1Contre = 0.0, v2Contre = 0.0;
			if (s1r != null) {
				s1 = Cast.asFloat(scope, s1r);
			}
			final Object s2r = critMap.get("s2");
			if (s2r != null) {
				s2 = Cast.asFloat(scope, s2r);
			}
			final Object v1pr = critMap.get("v1p");
			if (v1pr != null) {
				v1Pour = Cast.asFloat(scope, v1pr);
			}
			final Object v2pr = critMap.get("v2p");
			if (v2pr != null) {
				v2Pour = Cast.asFloat(scope, v2pr);
			}
			final Object v1cr = critMap.get("v1c");
			if (v1cr != null) {
				v1Contre = Cast.asFloat(scope, v1cr);
			}
			final Object v2cr = critMap.get("v2c");
			if (v2cr != null) {
				v2Contre = Cast.asFloat(scope, v2cr);
			}
			final Object max = critMap.get("maximize");
			if (max != null && max instanceof Boolean) {
				maximizeCrit.put(name, (Boolean) max);
			}
			final CritereFonctionsCroyances cfc =
					new CritereFctCroyancesBasique(name, s1, v2Pour, v1Pour, v1Contre, v2Contre, s2);
			criteresFC.add(cfc);
		}
		final EvidenceTheory evt = new EvidenceTheory();
		for (final List cand : cands) {
			final Map<String, Double> valCriteria = new HashMap<>();
			int i = 0;
			for (final String crit : criteriaStr) {
				final Double val = Cast.asFloat(scope, cand.get(i));
				valCriteria.put(crit, val);
				i++;
			}
			final Candidate c = new Candidate(cpt, valCriteria);
			candidates.add(c);
			cpt++;
		}
		// DEBUG.LOG("candidates : " + candidates.size());
		final LinkedList<Candidate> candsFilter = filtering(candidates, maximizeCrit);
		if (candsFilter.isEmpty()) {
			return scope.getRandom().between(0, candidates.size() - 1);

		}
		// DEBUG.LOG("candfilter : " + candsFilter);
		final Candidate decision = evt.decision(criteresFC, candsFilter, simple);
		// DEBUG.LOG("decision : " + decision.getIndex());

		return decision.getIndex();

	}

	private static LinkedList<Candidate> filtering(final Collection<Candidate> candidates,
			final Map<String, Boolean> maximizeCrit) {
		final LinkedList<Candidate> cands = new LinkedList<>();
		final LinkedList<Map<String, Double>> paretoVals = new LinkedList<>();
		for (final Candidate c1 : candidates) {
			boolean paretoFront = true;
			for (final Candidate c2 : candidates) {
				if (c1 == c2) {
					continue;
				}
				if (paretoInf(c1, c2, maximizeCrit)) {
					paretoFront = false;
					break;
				}
			}
			if (paretoFront && !paretoVals.contains(c1.getValCriteria())) {
				cands.add(c1);
				paretoVals.add(c1.getValCriteria());
			}
		}
		return cands;
	}

	private static boolean paretoInf(final Candidate c1, final Candidate c2, final Map<String, Boolean> maximizeCrit) {
		int equals = 0;
		for (final String crit : c1.getValCriteria().keySet()) {
			final boolean maximize = !maximizeCrit.containsKey(crit) ? true : maximizeCrit.get(crit);
			final double v1 = c1.getValCriteria().get(crit);
			final double v2 = c2.getValCriteria().get(crit);
			if (maximize) {
				if (v1 > v2) { return false; }
			} else {
				if (v1 < v2) { return false; }
			}
			if (v1 == v2) {
				equals++;
			}
		}
		return equals < c1.getValCriteria().size();
	}

}
