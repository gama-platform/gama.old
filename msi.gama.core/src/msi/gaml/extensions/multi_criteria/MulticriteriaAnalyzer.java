/*********************************************************************************************
 * 
 * 
 * 'MulticriteriaAnalyzer.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.multi_criteria;

import java.util.*;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.operators.Cast;

@species(name = "multicriteria_analyzer")
public class MulticriteriaAnalyzer extends GamlAgent {

	public MulticriteriaAnalyzer(final IPopulation s) throws GamaRuntimeException {
		super(s);
	}

	@action(name = "weighted_means_DM")
	@args(names = { "candidates", "criteria" })
	public Integer WeightedMeansDecisionMaking(final IScope scope) throws GamaRuntimeException {
		final List<List> cands = scope.getListArg("candidates");
		final List<Map<String, Object>> criteriaMap = scope.getListArg("criteria");
		if ( cands == null || cands.isEmpty() ) { return -1; }
		final List<String> criteriaStr = new LinkedList<String>();
		final Map<String, Double> weight = new HashMap<String, Double>();
		for ( final Map<String, Object> critMap : criteriaMap ) {
			final String name = (String) critMap.get("name");
			criteriaStr.add(name);
			final Double w = Cast.asFloat(scope, critMap.get("weight"));
			if ( w != null ) {
				weight.put(name, w);
			} else {
				weight.put(name, 1.0);
			}
		}
		int cpt = 0;
		double utilityMax = -1;
		int indexCand = -1;
		boolean first = true;
		for ( final List cand : cands ) {
			int i = 0;
			double utility = 0;
			for ( final String crit : criteriaStr ) {
				utility += weight.get(crit) * Cast.asFloat(scope, cand.get(i));
				i++;
			}
			if ( first || utilityMax < utility ) {
				utilityMax = utility;
				indexCand = cpt;
				first = false;
			}
			cpt++;
		}
		return indexCand;

	}

	@action(name = "promethee_DM")
	@args(names = { "candidates", "criteria" })
	public Integer PrometheeDecisionMaking(final IScope scope) throws GamaRuntimeException {
		final List<List> cands = scope.getListArg("candidates");
		final List<Map<String, Object>> criteriaMap = scope.getListArg("criteria");
		if ( cands == null || cands.isEmpty() ) { return -1;

		}
		int cpt = 0;
		final LinkedList<Candidate> candidates = new LinkedList<Candidate>();
		final List<String> criteriaStr = new LinkedList<String>();
		final Map<String, FonctionPreference> fctPrefCrit = new HashMap<String, FonctionPreference>();
		final Map<String, Double> weight = new Hashtable<String, Double>();
		for ( final Map<String, Object> critMap : criteriaMap ) {
			final String name = (String) critMap.get("name");
			criteriaStr.add(name);
			final Double w = Cast.asFloat(scope, critMap.get("weight"));
			if ( w != null ) {
				weight.put(name, w);
			} else {
				weight.put(name, 1.0);
			}
			String typeFct = "type_5";
			final Object typeObj = critMap.get("type");
			if ( typeObj != null ) {
				typeFct = typeObj.toString();
			}
			final Object q = critMap.get("q");
			final Object p = critMap.get("p");
			final Object s = critMap.get("s");
			Double pf = 1.0, qf = 0.0, sf = 1.0;
			if ( q != null ) {
				qf = Cast.asFloat(scope, q);
			}
			if ( p != null ) {
				pf = Cast.asFloat(scope, p);
			}
			if ( s != null ) {
				sf = Cast.asFloat(scope, s);
			}

			if ( typeFct.equals("type_5") ) {
				fctPrefCrit.put(name, new PreferenceType5(qf, pf));
			} else if ( typeFct.equals("type_6") ) {
				fctPrefCrit.put(name, new PreferenceType6(sf));
			}

		}
		final Promethee promethee = new Promethee(criteriaStr);
		promethee.setFctPrefCrit(fctPrefCrit);
		promethee.setPoidsCrit(weight);

		for ( final List cand : cands ) {
			final Map<String, Double> valCriteria = new HashMap<String, Double>();
			int i = 0;
			for ( final String crit : criteriaStr ) {
				valCriteria.put(crit, Cast.asFloat(scope, cand.get(i)));
				i++;
			}
			final Candidate c = new Candidate(cpt, valCriteria);
			candidates.add(c);
			cpt++;
		}
		final LinkedList<Candidate> candsFilter = filtering(candidates, new HashMap<String, Boolean>());
		if ( candsFilter.isEmpty() ) { return scope.getRandom().between(0, candidates.size() - 1); }
		if ( candsFilter.size() == 1 ) { return ((Candidate) GamaList.from((Iterable) candsFilter).firstValue(scope))
			.getIndex(); }
		final Candidate decision = promethee.decision(candsFilter);
		return decision.getIndex();

	}

	@action(name = "electre_DM")
	@args(names = { "candidates", "criteria", "fuzzy_cut" })
	public Integer electreDecisionMaking(final IScope scope) throws GamaRuntimeException {
		final List<List> cands = scope.getListArg("candidates");
		final List<Map<String, Object>> criteriaMap = scope.getListArg("criteria");
		Double fuzzyCut = scope.hasArg("fuzzy_cut") ? scope.getFloatArg("fuzzy_cut") : null;
		if ( fuzzyCut == null ) {
			fuzzyCut = Double.valueOf(0.7);
		}
		if ( cands == null || cands.isEmpty() ) { return -1; }
		int cpt = 0;
		final List<Candidate> candidates = new GamaList<Candidate>();
		final List<String> criteriaStr = new GamaList<String>();
		final Map<String, Double> weight = new HashMap<String, Double>();
		final Map<String, Double> preference = new HashMap<String, Double>();
		final Map<String, Double> indifference = new HashMap<String, Double>();
		final Map<String, Double> veto = new HashMap<String, Double>();
		for ( final Map<String, Object> critMap : criteriaMap ) {
			final String name = (String) critMap.get("name");
			criteriaStr.add(name);
			final Double w = Cast.asFloat(scope, critMap.get("weight"));
			if ( w != null ) {
				weight.put(name, w);
			} else {
				weight.put(name, 1.0);
			}
			final Object p = critMap.get("p");
			final Object q = critMap.get("q");
			final Object v = critMap.get("v");
			Double pf = 0.5, qf = 0.0, vf = 1.0;

			if ( q != null ) {
				qf = Cast.asFloat(scope, q);
			}

			indifference.put(name, qf);

			if ( p != null ) {
				pf = Cast.asFloat(scope, p);
			}
			preference.put(name, pf);

			if ( v != null ) {
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

		for ( final List cand : cands ) {
			final Map<String, Double> valCriteria = new HashMap<String, Double>();
			int i = 0;
			for ( final String crit : criteriaStr ) {
				valCriteria.put(crit, Cast.asFloat(scope, cand.get(i)));
				i++;
			}
			final Candidate c = new Candidate(cpt, valCriteria);
			candidates.add(c);
			cpt++;
		}
		final LinkedList<Candidate> candsFilter = filtering(candidates, new HashMap<String, Boolean>());
		if ( candsFilter.isEmpty() ) { return scope.getRandom().between(0, candidates.size() - 1); }
		final Candidate decision = electre.decision(candsFilter);
		return decision.getIndex();

	}

	@action(name = "evidence_theory_DM")
	@args(names = { "candidates", "criteria", "simple" })
	public Integer evidenceTheoryDecisionMaking(final IScope scope) throws GamaRuntimeException {
		final List<List> cands = scope.getListArg("candidates");
		final List<Map<String, Object>> criteriaMap = scope.getListArg("criteria");
		if ( cands == null || cands.isEmpty() ) { return -1; }
		int cpt = 0;
		Boolean simple = scope.getBoolArg("simple");
		if ( simple == null ) {
			simple = false;
		}
		final Map<String, Boolean> maximizeCrit = new HashMap<String, Boolean>();
		final LinkedList<Candidate> candidates = new LinkedList<Candidate>();
		final List<String> criteriaStr = new LinkedList<String>();
		final LinkedList<CritereFonctionsCroyances> criteresFC = new LinkedList<CritereFonctionsCroyances>();
		for ( final Map<String, Object> critMap : criteriaMap ) {
			final String name = (String) critMap.get("name");
			criteriaStr.add(name);
			final Object s1r = critMap.get("s1");
			Double s1 = 0.0, s2 = 1.0, v1Pour = 0.0, v2Pour = 1.0, v1Contre = 0.0, v2Contre = 0.0;
			if ( s1r != null ) {
				s1 = Cast.asFloat(scope, s1r);
			}
			final Object s2r = critMap.get("s2");
			if ( s2r != null ) {
				s2 = Cast.asFloat(scope, s2r);
			}
			final Object v1pr = critMap.get("v1p");
			if ( v1pr != null ) {
				v1Pour = Cast.asFloat(scope, v1pr);
			}
			final Object v2pr = critMap.get("v2p");
			if ( v2pr != null ) {
				v2Pour = Cast.asFloat(scope, v2pr);
			}
			final Object v1cr = critMap.get("v1c");
			if ( v1cr != null ) {
				v1Contre = Cast.asFloat(scope, v1cr);
			}
			final Object v2cr = critMap.get("v2c");
			if ( v2cr != null ) {
				v2Contre = Cast.asFloat(scope, v2cr);
			}
			final Object max = critMap.get("maximize");
			if ( max != null && max instanceof Boolean ) {
				maximizeCrit.put(name, (Boolean) max);
			}
			final CritereFonctionsCroyances cfc =
				new CritereFctCroyancesBasique(name, s1, v2Pour, v1Pour, v1Contre, v2Contre, s2);
			criteresFC.add(cfc);
		}
		final EvidenceTheory evt = new EvidenceTheory();
		for ( final List cand : cands ) {
			final Map<String, Double> valCriteria = new HashMap<String, Double>();
			int i = 0;
			for ( final String crit : criteriaStr ) {
				final Double val = Cast.asFloat(scope, cand.get(i));
				valCriteria.put(crit, val);
				i++;
			}
			final Candidate c = new Candidate(cpt, valCriteria);
			candidates.add(c);
			cpt++;
		}
		// System.out.println("candidates : " + candidates.size());
		final LinkedList<Candidate> candsFilter = filtering(candidates, maximizeCrit);
		if ( candsFilter.isEmpty() ) { return scope.getRandom().between(0, candidates.size() - 1);

		}
		// System.out.println("candfilter : " + candsFilter);
		final Candidate decision = evt.decision(criteresFC, candsFilter, simple);
		// System.out.println("decision : " + decision.getIndex());

		return decision.getIndex();

	}

	private LinkedList<Candidate> filtering(final Collection<Candidate> candidates,
		final Map<String, Boolean> maximizeCrit) {
		final LinkedList<Candidate> cands = new LinkedList<Candidate>();
		final LinkedList<Map<String, Double>> paretoVals = new LinkedList<Map<String, Double>>();
		for ( final Candidate c1 : candidates ) {
			boolean paretoFront = true;
			for ( final Candidate c2 : candidates ) {
				if ( c1 == c2 ) {
					continue;
				}
				if ( paretoInf(c1, c2, maximizeCrit) ) {
					paretoFront = false;
					break;
				}
			}
			if ( paretoFront && !paretoVals.contains(c1.getValCriteria()) ) {
				cands.add(c1);
				paretoVals.add(c1.getValCriteria());
			}
		}
		return cands;
	}

	private boolean paretoInf(final Candidate c1, final Candidate c2, final Map<String, Boolean> maximizeCrit) {
		int equals = 0;
		for ( final String crit : c1.getValCriteria().keySet() ) {
			final boolean maximize = !maximizeCrit.containsKey(crit) ? true : maximizeCrit.get(crit);
			final double v1 = c1.getValCriteria().get(crit);
			final double v2 = c2.getValCriteria().get(crit);
			if ( maximize ) {
				if ( v1 > v2 ) { return false; }
			} else {
				if ( v1 < v2 ) { return false; }
			}
			if ( v1 == v2 ) {
				equals++;
			}
		}
		return equals < c1.getValCriteria().size();
	}

}
