/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.extensions.multi_criteria;

import java.util.*;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;

@species(name = "multicriteria_analyzer")
public class MulticriteriaAnalyzer extends GamlAgent {

	public MulticriteriaAnalyzer(final ISimulation sim, final IPopulation s)
		throws GamaRuntimeException {
		super(sim, s);
	}

	@action(name="weighted_means_DM")
	@args(names = { "candidates", "criteria" })
	public Integer WeightedMeansDecisionMaking(final IScope scope) throws GamaRuntimeException {
		List<List<Double>> cands = scope.getListArg("candidates");
		List<Map<String, Object>> criteriaMap = scope.getListArg("criteria");
		if ( cands == null || cands.isEmpty() ) { return -1;

		}
		List<String> criteriaStr = new LinkedList<String>();
		Map<String, Double> weight = new HashMap<String, Double>();
		for ( Map<String, Object> critMap : criteriaMap ) {
			String name = (String) critMap.get("name");
			criteriaStr.add(name);
			Object w = critMap.get("weight");
			if ( w instanceof Integer ) {
				weight.put(name, ((Integer) w).doubleValue());
			} else if ( w instanceof Double ) {
				weight.put(name, ((Double) w).doubleValue());
			} else {
				weight.put(name, 1.0);
			}
		}
		int cpt = 0;
		double utilityMax = -1;
		int indexCand = -1;
		boolean first = true;
		for ( List<Double> cand : cands ) {
			int i = 0;
			double utility = 0;
			for ( String crit : criteriaStr ) {
				utility += weight.get(crit) * cand.get(i);
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

	@action(name="promethee_DM")
	@args(names = { "candidates", "criteria" })
	public Integer PrometheeDecisionMaking(final IScope scope) throws GamaRuntimeException {
		List<List<Double>> cands = scope.getListArg("candidates");
		List<Map<String, Object>> criteriaMap = scope.getListArg("criteria");
		if ( cands == null || cands.isEmpty() ) { return -1;

		}
		int cpt = 0;
		LinkedList<Candidate> candidates = new LinkedList<Candidate>();
		List<String> criteriaStr = new LinkedList<String>();
		Map<String, FonctionPreference> fctPrefCrit = new HashMap<String, FonctionPreference>();
		Map<String, Double> weight = new Hashtable<String, Double>();
		for ( Map<String, Object> critMap : criteriaMap ) {
			String name = (String) critMap.get("name");
			criteriaStr.add(name);
			Object w = critMap.get("weight");
			if ( w instanceof Integer ) {
				weight.put(name, ((Integer) w).doubleValue());
			} else if ( w instanceof Double ) {
				weight.put(name, ((Double) w).doubleValue());
			} else {
				weight.put(name, 1.0);
			}
			String typeFct = "type_5";
			Object typeObj = critMap.get("type");
			if ( typeObj != null ) {
				typeFct = typeObj.toString();
			}
			Object q = critMap.get("q");
			Object p = critMap.get("p");
			Object s = critMap.get("s");
			Double pf = 1.0, qf = 0.0, sf = 1.0;
			if ( q != null ) {
				if ( q instanceof Double ) {
					qf = (Double) q;
				} else if ( q instanceof Integer ) {
					qf = ((Integer) q).doubleValue();
				}
			}
			if ( p != null ) {
				if ( p instanceof Double ) {
					pf = (Double) p;
				} else if ( p instanceof Integer ) {
					pf = ((Integer) p).doubleValue();
				}
			}
			if ( s != null ) {
				if ( s instanceof Double ) {
					sf = (Double) s;
				} else if ( s instanceof Integer ) {
					sf = ((Integer) s).doubleValue();
				}
			}

			if ( typeFct.equals("type_5") ) {
				fctPrefCrit.put(name, new PreferenceType5(qf, pf));
			} else if ( typeFct.equals("type_6") ) {
				fctPrefCrit.put(name, new PreferenceType6(sf));
			}

		}
		Promethee promethee = new Promethee(criteriaStr);
		promethee.setFctPrefCrit(fctPrefCrit);
		promethee.setPoidsCrit(weight);

		for ( List<Double> cand : cands ) {
			Map<String, Double> valCriteria = new HashMap<String, Double>();
			int i = 0;
			for ( String crit : criteriaStr ) {
				valCriteria.put(crit, cand.get(i));
				i++;
			}
			Candidate c = new Candidate(cpt, valCriteria);
			candidates.add(c);
			cpt++;
		}
		LinkedList<Candidate> candsFilter = filtering(candidates, new HashMap<String, Boolean>());
		if ( candsFilter.isEmpty() ) { return GAMA.getRandom().between(0, candidates.size() - 1); }
		if ( candsFilter.size() == 1 ) { return new GamaList<Candidate>(candsFilter).first()
			.getIndex(); }
		Candidate decision = promethee.decision(candsFilter);
		return decision.getIndex();

	}

	@action(name="electre_DM")
	@args(names = { "candidates", "criteria", "fuzzy_cut" })
	public Integer electreDecisionMaking(final IScope scope) throws GamaRuntimeException {
		List<List<Double>> cands = scope.getListArg("candidates");
		List<Map<String, Object>> criteriaMap = scope.getListArg("criteria");
		Double fuzzyCut = scope.hasArg("fuzzy_cut") ? scope.getFloatArg("fuzzy_cut") : null;
		if ( fuzzyCut == null ) {
			fuzzyCut = Double.valueOf(0.7);
		}
		if ( cands == null || cands.isEmpty() ) { return -1; }
		int cpt = 0;
		List<Candidate> candidates = new GamaList<Candidate>();
		List<String> criteriaStr = new GamaList<String>();
		Map<String, Double> weight = new HashMap<String, Double>();
		Map<String, Double> preference = new HashMap<String, Double>();
		Map<String, Double> indifference = new HashMap<String, Double>();
		Map<String, Double> veto = new HashMap<String, Double>();
		for ( Map<String, Object> critMap : criteriaMap ) {
			String name = (String) critMap.get("name");
			criteriaStr.add(name);
			Object w = critMap.get("weight");
			if ( w instanceof Integer ) {
				weight.put(name, ((Integer) w).doubleValue());
			} else if ( w instanceof Double ) {
				weight.put(name, ((Double) w).doubleValue());
			} else {
				weight.put(name, 1.0);
			}
			Object p = critMap.get("p");
			Object q = critMap.get("q");
			Object v = critMap.get("v");
			Double pf = 0.5, qf = 0.0, vf = 1.0;

			if ( q instanceof Double ) {
				qf = (Double) q;
			} else if ( q instanceof Integer ) {
				qf = ((Integer) q).doubleValue();
			}
			indifference.put(name, qf);

			if ( p instanceof Double ) {
				pf = (Double) p;
			} else if ( p instanceof Integer ) {
				pf = ((Integer) p).doubleValue();
			}
			preference.put(name, pf);

			if ( v instanceof Double ) {
				vf = (Double) v;
			} else if ( v instanceof Integer ) {
				vf = ((Integer) v).doubleValue();
			}
			veto.put(name, vf);
		}
		Electre electre = new Electre(criteriaStr);
		electre.setPoids(weight);
		electre.setIndifference(indifference);
		electre.setPreference(preference);
		electre.setVeto(veto);
		electre.setSeuilCoupe(fuzzyCut);

		for ( List<Double> cand : cands ) {
			Map<String, Double> valCriteria = new HashMap<String, Double>();
			int i = 0;
			for ( String crit : criteriaStr ) {
				valCriteria.put(crit, cand.get(i));
				i++;
			}
			Candidate c = new Candidate(cpt, valCriteria);
			candidates.add(c);
			cpt++;
		}
		LinkedList<Candidate> candsFilter = filtering(candidates, new HashMap<String, Boolean>());
		if ( candsFilter.isEmpty() ) { return GAMA.getRandom().between(0, candidates.size() - 1); }
		Candidate decision = electre.decision(candsFilter);
		return decision.getIndex();

	}

	@action(name="evidence_theory_DM")
	@args(names = { "candidates", "criteria", "simple" })
	public Integer evidenceTheoryDecisionMaking(final IScope scope) throws GamaRuntimeException {
		List<List> cands = scope.getListArg("candidates");
		List<Map<String, Object>> criteriaMap = scope.getListArg("criteria");
		if ( cands == null || cands.isEmpty() ) { return -1; }
		int cpt = 0;
		Boolean simple = scope.getBoolArg("simple");
		if ( simple == null ) {
			simple = false;
		}
		Map<String, Boolean> maximizeCrit = new HashMap<String, Boolean>();
		LinkedList<Candidate> candidates = new LinkedList<Candidate>();
		List<String> criteriaStr = new LinkedList<String>();
		LinkedList<CritereFonctionsCroyances> criteresFC =
			new LinkedList<CritereFonctionsCroyances>();
		for ( Map<String, Object> critMap : criteriaMap ) {
			String name = (String) critMap.get("name");
			criteriaStr.add(name);
			Object s1r = critMap.get("s1");
			Double s1 = 0.0, s2 = 1.0, v1Pour = 0.0, v2Pour = 1.0, v1Contre = 0.0, v2Contre = 0.0;
			if ( s1r != null ) {
				if ( s1r instanceof Integer ) {
					s1 = ((Integer) s1r).doubleValue();
				} else if ( s1r instanceof Double ) {
					s1 = (Double) s1r;
				}
			}
			Object s2r = critMap.get("s2");
			if ( s2r != null ) {
				if ( s2r instanceof Integer ) {
					s2 = ((Integer) s2r).doubleValue();
				} else if ( s2r instanceof Double ) {
					s2 = (Double) s2r;
				}
			}
			Object v1pr = critMap.get("v1p");
			if ( v1pr != null ) {
				if ( v1pr instanceof Integer ) {
					v1Pour = ((Integer) v1pr).doubleValue();
				} else if ( v1pr instanceof Double ) {
					v1Pour = (Double) v1pr;
				}
			}
			Object v2pr = critMap.get("v2p");
			if ( v2pr != null ) {
				if ( v2pr instanceof Integer ) {
					v2Pour = ((Integer) v2pr).doubleValue();
				} else if ( v2pr instanceof Double ) {
					v2Pour = (Double) v2pr;
				}
			}
			Object v1cr = critMap.get("v1c");
			if ( v1cr != null ) {
				if ( v1cr instanceof Integer ) {
					v1Contre = ((Integer) v1cr).doubleValue();
				} else if ( v1cr instanceof Double ) {
					v1Contre = (Double) v1cr;
				}
			}
			Object v2cr = critMap.get("v2c");
			if ( v2cr != null ) {
				if ( v2cr instanceof Integer ) {
					v2Contre = ((Integer) v2cr).doubleValue();
				} else if ( v2cr instanceof Double ) {
					v2Contre = (Double) v2cr;
				}
			}
			Object max = critMap.get("maximize");
			if ( max != null && max instanceof Boolean ) {
				maximizeCrit.put(name, (Boolean) max);
			}
			// System.out.println(name + " s1 : " + s1 + " s2: " + s2 + " v1Pour : " + v1Pour +
			// " v2Pour : " + v2Pour + " v1Contre : " + v1Contre + " v2Contre : " + v2Contre);
			CritereFonctionsCroyances cfc =
				new CritereFctCroyancesBasique(name, s1, v2Pour, v1Pour, v1Contre, v2Contre, s2);
			criteresFC.add(cfc);
		}
		EvidenceTheory evt = new EvidenceTheory();
		for ( List cand : cands ) {
			Map<String, Double> valCriteria = new HashMap<String, Double>();
			int i = 0;
			for ( String crit : criteriaStr ) {
				Object val = cand.get(i);
				if ( val instanceof Integer ) {
					valCriteria.put(crit, ((Integer) val).doubleValue());
				} else {
					valCriteria.put(crit, (Double) val);
				}
				i++;
			}
			Candidate c = new Candidate(cpt, valCriteria);
			candidates.add(c);
			cpt++;
		}
		// System.out.println("candidates : " + candidates.size());
		LinkedList<Candidate> candsFilter = filtering(candidates, maximizeCrit);
		if ( candsFilter.isEmpty() ) { return GAMA.getRandom().between(0, candidates.size() - 1);

		}
		// System.out.println("candfilter : " + candsFilter);
		Candidate decision = evt.decision(criteresFC, candsFilter, simple);
		// System.out.println("decision : " + decision.getIndex());

		return decision.getIndex();

	}

	private LinkedList<Candidate> filtering(final Collection<Candidate> candidates,
		final Map<String, Boolean> maximizeCrit) {
		LinkedList<Candidate> cands = new LinkedList<Candidate>();
		LinkedList<Map<String, Double>> paretoVals = new LinkedList<Map<String, Double>>();
		for ( Candidate c1 : candidates ) {
			boolean paretoFront = true;
			for ( Candidate c2 : candidates ) {
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

	private boolean paretoInf(final Candidate c1, final Candidate c2,
		final Map<String, Boolean> maximizeCrit) {
		int equals = 0;
		for ( String crit : c1.getValCriteria().keySet() ) {
			boolean maximize = !maximizeCrit.containsKey(crit) ? true : maximizeCrit.get(crit);
			double v1 = c1.getValCriteria().get(crit);
			double v2 = c2.getValCriteria().get(crit);
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
