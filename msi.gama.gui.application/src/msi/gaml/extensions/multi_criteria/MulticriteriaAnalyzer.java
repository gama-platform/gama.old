/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.extensions.multi_criteria;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.util.*;
import msi.gaml.agents.GamlAgent;

@species("multicriteria_analyzer")
public class MulticriteriaAnalyzer extends GamlAgent {

	public MulticriteriaAnalyzer(final ISimulation sim, final IPopulation s)
		throws GamaRuntimeException {
		super(sim, s);
	}

	@action("weighted_means_DM")
	@args({ "candidates", "criteria" })
	public Integer WeightedMeansDecisionMaking(final IScope scope) throws GamaRuntimeException {
		List<List<Double>> cands = Cast.asList(scope.getArg("candidates"));
		List<Map<String, Object>> criteriaMap = Cast.asList(scope.getArg("criteria"));
		if ( cands == null || cands.isEmpty() ) { return -1;

		}
		List<String> criteriaStr = new GamaList<String>();
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

	@action("promethee_DM")
	@args({ "candidates", "criteria" })
	public Integer PrometheeDecisionMaking(final IScope scope) throws GamaRuntimeException {
		List<List<Double>> cands = Cast.asList(scope.getArg("candidates"));
		List<Map<String, Object>> criteriaMap = Cast.asList(scope.getArg("criteria"));
		if ( cands == null || cands.isEmpty() ) { return -1;

		}
		int cpt = 0;
		Set<Candidate> candidates = new HashSet<Candidate>();
		List<String> criteriaStr = new GamaList<String>();
		Map<String, FonctionPreference> fctPrefCrit = new Hashtable<String, FonctionPreference>();
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
		Set<Candidate> candsFilter = filtering(candidates);
		if ( candsFilter.isEmpty() ) { return GAMA.getRandom().between(0, candidates.size() - 1); }
		if ( candsFilter.size() == 1 ) { return new GamaList<Candidate>(candsFilter).first()
			.getIndex(); }
		Candidate decision = promethee.decision(candsFilter);
		return decision.getIndex();

	}

	@action("electre_DM")
	@args({ "candidates", "criteria", "fuzzy_cut" })
	public Integer electreDecisionMaking(final IScope scope) throws GamaRuntimeException {
		List<List<Double>> cands = Cast.asList(scope.getArg("candidates"));
		List<Map<String, Object>> criteriaMap = Cast.asList(scope.getArg("criteria"));
		Double fuzzyCut =
			scope.hasArg("fuzzy_cut") ? Cast.asFloat(scope.getArg("fuzzy_cut")) : null;
		if ( fuzzyCut == null ) {
			fuzzyCut = Double.valueOf(0.7);
		}
		if ( cands == null || cands.isEmpty() ) { return -1; }
		int cpt = 0;
		List<Candidate> candidates = new GamaList<Candidate>();
		List<String> criteriaStr = new GamaList<String>();
		Map<String, Double> weight = new Hashtable<String, Double>();
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
		Set<Candidate> candsFilter = filtering(candidates);
		if ( candsFilter.isEmpty() ) { return GAMA.getRandom().between(0, candidates.size() - 1); }
		Candidate decision = electre.decision(new GamaList<Candidate>(candsFilter));
		return decision.getIndex();

	}

	@action("evidence_theory_DM")
	@args({ "candidates", "criteria" })
	public Integer evidenceTheoryDecisionMaking(final IScope scope) throws GamaRuntimeException {
		List<List<Double>> cands = Cast.asList(scope.getArg("candidates"));
		List<Map<String, Object>> criteriaMap = Cast.asList(scope.getArg("criteria"));
		if ( cands == null || cands.isEmpty() ) { return -1; }
		int cpt = 0;
		Set<Candidate> candidates = new HashSet<Candidate>();
		List<String> criteriaStr = new GamaList<String>();
		Set<CritereFonctionsCroyances> criteresFC = new HashSet<CritereFonctionsCroyances>();
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
			CritereFonctionsCroyances cfc =
				new CritereFctCroyancesBasique(name, s1, v2Pour, v1Pour, v1Contre, v2Contre, s2);
			criteresFC.add(cfc);
		}
		EvidenceTheory evt = new EvidenceTheory();
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
		Set<Candidate> candsFilter = filtering(candidates);
		if ( candsFilter.isEmpty() ) { return GAMA.getRandom().between(0, candidates.size() - 1);

		}
		Candidate decision = evt.decision(criteresFC, candsFilter);
		return decision.getIndex();

	}

	private Set<Candidate> filtering(final Collection<Candidate> candidates) {
		Set<Candidate> cands = new HashSet<Candidate>();
		for ( Candidate c1 : candidates ) {
			boolean paretoFront = true;
			for ( Candidate c2 : candidates ) {
				if ( c1 == c2 ) {
					continue;
				}
				if ( paretoInf(c1, c2) ) {
					paretoFront = false;
					break;
				}
			}
			if ( paretoFront ) {
				cands.add(c1);
			}
		}
		return cands;
	}

	private boolean paretoInf(final Candidate c1, final Candidate c2) {
		int equals = 0;
		for ( String crit : c1.getValCriteria().keySet() ) {
			double v1 = c1.getValCriteria().get(crit);
			double v2 = c2.getValCriteria().get(crit);
			if ( v1 > v2 ) { return false; }
			if ( v1 == v2 ) {
				equals++;
			}
		}
		return equals < c1.getValCriteria().size();
	}

}
