/*******************************************************************************************************
 *
 * Promethee.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.extensions.multi_criteria;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import msi.gama.runtime.GAMA;

/**
 * The Class Promethee.
 */
public class Promethee {

	/** The poids crit. */
	private Map<String, Double> poidsCrit;
	
	/** The fct pref crit. */
	private Map<String, FonctionPreference> fctPrefCrit;

	/**
	 * Instantiates a new promethee.
	 *
	 * @param poidsCrit the poids crit
	 * @param fctPrefCrit the fct pref crit
	 */
	public Promethee(final Map<String, Double> poidsCrit, final Map<String, FonctionPreference> fctPrefCrit) {
		super();
		this.poidsCrit = poidsCrit;
		this.fctPrefCrit = fctPrefCrit;
	}

	/**
	 * Instantiates a new promethee.
	 *
	 * @param criteres the criteres
	 */
	public Promethee(final Collection<String> criteres) {
		poidsCrit = new Hashtable<>();
		fctPrefCrit = new Hashtable<>();
		for (final String crit : criteres) {
			poidsCrit.put(crit, Double.valueOf(GAMA.getCurrentRandom().between(0, 9)));
			fctPrefCrit.put(crit, new PreferenceType5(0.1, 0.8));
		}
	}

	/**
	 * Instantiates a new promethee.
	 *
	 * @param promethee the promethee
	 */
	public Promethee(final Promethee promethee) {
		poidsCrit = new Hashtable<>();
		for (final String crit : promethee.poidsCrit.keySet()) {
			poidsCrit.put(crit, promethee.poidsCrit.get(crit));
		}
		fctPrefCrit = new Hashtable<>();
		for (final String crit : promethee.poidsCrit.keySet()) {
			fctPrefCrit.put(crit, promethee.fctPrefCrit.get(crit).copie());
		}
	}

	/**
	 * Decision.
	 *
	 * @param locations the locations
	 * @return the candidate
	 */
	public Candidate decision(final LinkedList<Candidate> locations) {
		Candidate meilleureLoc = null;
		double outRankingMax = -Double.MAX_VALUE;
		for (final Candidate loc1 : locations) {
			double outRankingPlus = 0;
			double outRankingMoins = 0;
			for (final Candidate loc2 : locations) {
				if (loc1 == loc2) {
					continue;
				}
				double PiXA = 0;
				double PiAX = 0;
				for (final String crit : fctPrefCrit.keySet()) {
					final double poids = poidsCrit.get(crit).doubleValue();
					final FonctionPreference fctPref = fctPrefCrit.get(crit);
					final double valLoc1 = loc1.getValCriteria().get(crit).doubleValue();
					final double valLoc2 = loc2.getValCriteria().get(crit).doubleValue();
					PiXA += poids * fctPref.valeur(valLoc1 - valLoc2);
					PiAX += poids * fctPref.valeur(valLoc2 - valLoc1);
				}
				outRankingPlus += PiXA;
				outRankingMoins += PiAX;
			}
			outRankingPlus /= locations.size() - 1;
			outRankingMoins /= locations.size() - 1;
			final double outRanking = outRankingPlus - outRankingMoins;
			if (outRanking > outRankingMax) {
				outRankingMax = outRanking;
				meilleureLoc = loc1;
			}
			// DEBUG.LOG("Location : " + loc1.getNom() + " : " + outRanking);

		}
		return meilleureLoc;
	}

	@Override
	public String toString() {

		return "Poids : " + poidsCrit;
	}

	/**
	 * Gets the fct pref crit.
	 *
	 * @return the fct pref crit
	 */
	public Map<String, FonctionPreference> getFctPrefCrit() {
		return fctPrefCrit;
	}

	/**
	 * Sets the fct pref crit.
	 *
	 * @param fctPrefCrit the fct pref crit
	 */
	public void setFctPrefCrit(final Map<String, FonctionPreference> fctPrefCrit) {
		this.fctPrefCrit = fctPrefCrit;
	}

	/**
	 * Gets the poids crit.
	 *
	 * @return the poids crit
	 */
	public Map<String, Double> getPoidsCrit() {
		return poidsCrit;
	}

	/**
	 * Sets the poids crit.
	 *
	 * @param poidsCrit the poids crit
	 */
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
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final Promethee other = (Promethee) obj;
		if (fctPrefCrit == null) {
			if (other.fctPrefCrit != null) { return false; }
		} else if (!fctPrefCrit.equals(other.fctPrefCrit)) { return false; }
		if (poidsCrit == null) {
			if (other.poidsCrit != null) { return false; }
		} else if (!poidsCrit.equals(other.poidsCrit)) { return false; }
		return true;
	}

}
