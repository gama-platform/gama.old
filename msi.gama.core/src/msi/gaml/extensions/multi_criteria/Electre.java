/*********************************************************************************************
 *
 * 'Electre.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.extensions.multi_criteria;

import java.util.*;

public class Electre {

	private Map<String, Double> poids = new HashMap<String, Double>();
	private double seuilCoupe = 0.7;
	private Map<String, Double> preference = new HashMap<String, Double>();
	private Map<String, Double> indifference = new HashMap<String, Double>();
	private Map<String, Double> veto = new HashMap<String, Double>();
	private final List<String> critereOrdonnes;

	public Electre(final List<String> critereOrdonnes) {
		super();
		this.critereOrdonnes = critereOrdonnes;
		for ( String param : critereOrdonnes ) {
			poids.put(param, new Double(5));
			preference.put(param, new Double(0.3));
			indifference.put(param, new Double(0.1));
			veto.put(param, new Double(0));
		}
	}

	// TODO UCdetector: Remove unused code:
	// public Electre(final Electre electre) {
	// this.seuilCoupe = electre.seuilCoupe;
	// this.seuilCoupe = electre.seuilCoupe;
	// poids = new GamaMap<String, Double>();
	// for ( String param : electre.poids.keySet() ) {
	// poids.put(param, electre.poids.get(param));
	// preference.put(param, electre.preference.get(param));
	// indifference.put(param, electre.indifference.get(param));
	// veto.put(param, electre.veto.get(param));
	// }
	// critereOrdonnes = electre.critereOrdonnes;
	// }

	public Map<String, Double> getPoids() {
		return poids;
	}

	public void setPoids(final Map<String, Double> poids) {
		this.poids = poids;
		critereOrdonnes.clear();
		critereOrdonnes.addAll(poids.keySet());
		Collections.sort(critereOrdonnes);
	}

	public Candidate decision(final List<Candidate> locations) {
		int relation[][] = new int[locations.size()][locations.size()];

		for ( int i = 0; i < locations.size() - 1; i++ ) {
			Candidate act1 = locations.get(i);
			for ( int j = i + 1; j < locations.size(); j++ ) {
				Candidate act2 = locations.get(j);

				relation[i][j] = 0;
				relation[j][i] = 0;

				String relationPaire = relation(act1, act2);
				if ( relationPaire.equals("A1_P_A2") ) {
					relation[i][j] = 1;
					relation[j][i] = -1;
				} else if ( relationPaire.equals("A2_P_A1") ) {
					relation[i][j] = -1;
					relation[j][i] = 1;
				}
			}
		}

		int max = -999999;
		Candidate candMax = null;
		for ( int i = 0; i < locations.size(); i++ ) {
			int val = 0;
			for ( int j = 0; j < locations.size(); j++ ) {
				val += relation[i][j];
			}
			if ( val > max ) {
				max = val;
				candMax = locations.get(i);
			}
		}
		return candMax;
	}

	private double concordance(final double a1, final double a2, final String crit) {
		double concordance = 0;
		double prefCrit = preference.get(crit).doubleValue();
		double indifCrit = indifference.get(crit).doubleValue();
		double diff = a1 - a2;
		if ( diff > -indifCrit ) {
			concordance = 1;
		} else if ( diff > -prefCrit ) {
			concordance = (diff + prefCrit) / (prefCrit - indifCrit);
		}
		return concordance;
	}

	private double discordance(final double a1, final double a2, final String crit) {
		double prefCrit = preference.get(crit).doubleValue();
		double vetoCrit = veto.get(crit).doubleValue();
		double diff = a1 - a2;
		double discordance = 0;
		if ( diff < -vetoCrit ) {
			discordance = 1;
		} else if ( diff < -prefCrit ) {
			discordance += (diff + prefCrit) / (prefCrit - vetoCrit);
		}
		return discordance;
	}

	private String relation(final Candidate val1, final Candidate val2) {
		// On commence par calculer pour chaque crit�re les concordances et les discordances entre
		// le vecteur courant et le vecteur de ref
		double concordGA1A2 = 0;
		double concordGA2A1 = 0;

		double poidsTot = 0;
		for ( String crit : poids.keySet() ) {
			double poidsCrit = poids.get(crit).doubleValue();
			double a1 = val1.getValCriteria().get(crit).doubleValue();
			double a2 = val2.getValCriteria().get(crit).doubleValue();
			poidsTot += poidsCrit;
			concordGA1A2 += poidsCrit * concordance(a1, a2, crit);
			concordGA2A1 += poidsCrit * concordance(a2, a1, crit);

		}
		concordGA1A2 /= poidsTot;
		concordGA2A1 /= poidsTot;

		double TA1A2 = 1;
		double TA2A1 = 1;
		for ( String crit : poids.keySet() ) {
			double a1 = val1.getValCriteria().get(crit).doubleValue();
			double a2 = val2.getValCriteria().get(crit).doubleValue();

			double discordanceA1A2 = discordance(a1, a2, crit);
			double discordanceA2A1 = discordance(a2, a1, crit);

			if ( discordanceA1A2 > concordGA1A2 ) {
				TA1A2 *= (1 - discordanceA1A2) / (1 - concordGA1A2);
			}
			if ( discordanceA2A1 > concordGA2A1 ) {
				TA2A1 *= (1 - discordanceA2A1) / (1 - concordGA2A1);
			}
		}

		double credibiliteGlobaleA1A2 = concordGA1A2 * TA1A2;
		double credibiliteGlobaleA2A1 = concordGA2A1 * TA2A1;

		// on d�duit enfin de ces valeurs la relation existante entre les deux vecteurs de valeurs
		if ( credibiliteGlobaleA1A2 < seuilCoupe ) {
			if ( credibiliteGlobaleA2A1 < seuilCoupe ) { return "A1_R_A2"; }
			return "A2_P_A1";
		}
		if ( credibiliteGlobaleA2A1 < seuilCoupe ) { return "A1_P_A2"; }
		return "A1_I_A2";
	}

	@Override
	public String toString() {
		String str = this.seuilCoupe + ",";
		for ( String crit : critereOrdonnes ) {
			str +=
				crit + ":" + poids.get(crit) + "," + preference.get(crit) + "," + indifference.get(crit) + "," +
					veto.get(crit);
		}
		return str;
	}

	public double getSeuilCoupe() {
		return seuilCoupe;
	}

	public void setSeuilCoupe(final double seuilCoupe) {
		this.seuilCoupe = seuilCoupe;
	}

	public Map<String, Double> getPreference() {
		return preference;
	}

	public void setPreference(final Map<String, Double> preference) {
		this.preference = preference;
	}

	public Map<String, Double> getIndifference() {
		return indifference;
	}

	public void setIndifference(final Map<String, Double> indifference) {
		this.indifference = indifference;
	}

	public Map<String, Double> getVeto() {
		return veto;
	}

	public void setVeto(final Map<String, Double> veto) {
		this.veto = veto;
	}

	public List<String> getCritereOrdonnes() {
		return critereOrdonnes;
	}

}
