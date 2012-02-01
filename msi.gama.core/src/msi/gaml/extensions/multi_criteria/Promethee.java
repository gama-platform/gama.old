/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
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

public class Promethee {

	private Map<String, Double> poidsCrit;
	private Map<String, FonctionPreference> fctPrefCrit;

	public Promethee(final Map<String, Double> poidsCrit,
		final Map<String, FonctionPreference> fctPrefCrit) {
		super();
		this.poidsCrit = poidsCrit;
		this.fctPrefCrit = fctPrefCrit;
	}

	public Promethee(final Collection<String> criteres) {
		poidsCrit = new Hashtable<String, Double>();
		fctPrefCrit = new Hashtable<String, FonctionPreference>();
		Random rand = new Random();
		for ( String crit : criteres ) {
			poidsCrit.put(crit, Double.valueOf(rand.nextInt(10)));
			fctPrefCrit.put(crit, new PreferenceType5(0.1, 0.8));
		}
	}

	public Promethee(final Promethee promethee) {
		poidsCrit = new Hashtable<String, Double>();
		for ( String crit : promethee.poidsCrit.keySet() ) {
			poidsCrit.put(crit, new Double(promethee.poidsCrit.get(crit).doubleValue()));
		}
		fctPrefCrit = new Hashtable<String, FonctionPreference>();
		for ( String crit : promethee.poidsCrit.keySet() ) {
			fctPrefCrit.put(crit, promethee.fctPrefCrit.get(crit).copie());
		}
	}

	public Candidate decision(final LinkedList<Candidate> locations) {
		Candidate meilleureLoc = null;
		double outRankingMax = -Double.MAX_VALUE;
		for ( Candidate loc1 : locations ) {
			double outRankingPlus = 0;
			double outRankingMoins = 0;
			for ( Candidate loc2 : locations ) {
				if ( loc1 == loc2 ) {
					continue;
				}
				double PiXA = 0;
				double PiAX = 0;
				for ( String crit : fctPrefCrit.keySet() ) {
					double poids = poidsCrit.get(crit).doubleValue();
					FonctionPreference fctPref = fctPrefCrit.get(crit);
					double valLoc1 = loc1.getValCriteria().get(crit).doubleValue();
					double valLoc2 = loc2.getValCriteria().get(crit).doubleValue();
					PiXA += poids * fctPref.valeur(valLoc1 - valLoc2);
					PiAX += poids * fctPref.valeur(valLoc2 - valLoc1);
				}
				outRankingPlus += PiXA;
				outRankingMoins += PiAX;
			}
			outRankingPlus /= locations.size() - 1;
			outRankingMoins /= locations.size() - 1;
			double outRanking = outRankingPlus - outRankingMoins;
			if ( outRanking > outRankingMax ) {
				outRankingMax = outRanking;
				meilleureLoc = loc1;
			}
			// System.out.println("Location : " + loc1.getNom() + " : " + outRanking);

		}
		return meilleureLoc;
	}

	@Override
	public String toString() {

		return "Poids : " + poidsCrit;
	}

	public Map<String, FonctionPreference> getFctPrefCrit() {
		return fctPrefCrit;
	}

	public void setFctPrefCrit(final Map<String, FonctionPreference> fctPrefCrit) {
		this.fctPrefCrit = fctPrefCrit;
	}

	public Map<String, Double> getPoidsCrit() {
		return poidsCrit;
	}

	public void setPoidsCrit(final Map<String, Double> poidsCrit) {
		this.poidsCrit = poidsCrit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (fctPrefCrit == null ? 0 : fctPrefCrit.hashCode());
		result = prime * result + (poidsCrit == null ? 0 : poidsCrit.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if ( this == obj ) { return true; }
		if ( obj == null ) { return false; }
		if ( getClass() != obj.getClass() ) { return false; }
		Promethee other = (Promethee) obj;
		if ( fctPrefCrit == null ) {
			if ( other.fctPrefCrit != null ) { return false; }
		} else if ( !fctPrefCrit.equals(other.fctPrefCrit) ) { return false; }
		if ( poidsCrit == null ) {
			if ( other.poidsCrit != null ) { return false; }
		} else if ( !poidsCrit.equals(other.poidsCrit) ) { return false; }
		return true;
	}

}
