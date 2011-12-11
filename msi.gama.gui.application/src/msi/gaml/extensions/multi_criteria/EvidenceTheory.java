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

/**
 * Classe implémentant la prise de décision à l'aide des fonctions de croyance
 * @author PTaillandier
 * 
 */
public class EvidenceTheory {

	/**
	 * Méthode assurant la fusion entre 2 sources
	 * @param m1 : première source
	 * @param m2 : deuxième source
	 * @return les masses de croyance m12
	 */
	private MassesCroyances fusionLesMassesLocalesDeuxSources(final MassesCroyances m1,
		final MassesCroyances m2) {
		// on instancie de nouvelles masses de croyance correspondant ï¿½ la fusion de m1 et m2
		MassesCroyances fusion = new MassesCroyances();
		// on met ï¿½ jour les valeurs de ces masses de croyances
		fusion.setPour(m1.pour * m2.pour + m1.pour * m2.ignorance + m1.ignorance * m2.pour);
		fusion.setContre(m1.contre * m2.contre + m1.contre * m2.ignorance + m1.ignorance *
			m2.contre);
		fusion.setIgnorance(m1.ignorance * m2.ignorance);
		fusion.setConflit(m1.pour * m2.contre + m1.contre * m2.pour + m1.conflit *
			(m2.pour + m2.contre + m2.ignorance + m2.conflit) + m2.conflit *
			(m1.pour + m1.contre + m1.ignorance));
		return fusion;
	}

	/**
	 * Méthode permettant de fusionner des ensembles de masses de croyances
	 * @param masses : ensemble de MassesCroyances : les masses de croyances à fusionner
	 * @return les masses de croyances correspondant à la fusion de l'ensemble des masses
	 */
	private MassesCroyances fusionLesMassesLocales(final Set<MassesCroyances> masses) {
		MassesCroyances fusion = null;
		// Parcours de l'ensemble des masses de croyances
		for ( MassesCroyances mc : masses ) {
			// si ce n'est pas la premiï¿½re MassesCroyances de l'ensemble, on la fusionne avec les
			// prï¿½cedentes
			if ( fusion != null ) {
				fusion = fusionLesMassesLocalesDeuxSources(fusion, mc);
			} else {
				fusion = mc;
			}
		}
		return fusion;
	}

	/**
	 * Méthode assurant la fusion entre hypothèses
	 * @param candidats : dictionnaire des hypothèses : clef : Candidat (hypothèse) -> valeur :
	 *            MassesCroyances (associé à l'hypothèse)
	 * @return la liste des propositions résultantes de la fusion ainsi que leur masse de croyance
	 *         associée
	 */
	private Propositions fusionHypotheses(final Map<Candidate, MassesCroyances> candidats) {
		Propositions fusion = new Propositions(candidats);
		return fusion;
	}

	/**
	 * Mï¿½thode assurant la fusion de l'ensemble des critï¿½res
	 * @param criteres : Ensemble de CritereFonctionsCroyances (les critï¿½res pour cette mï¿½thode)
	 * @param valeursCourantes : dictionnaire des valeurs des critï¿½res pour le jeu de K courant :
	 *            Clef : Strign : nom du critï¿½re -> Valeur : Double : sa valeur
	 * @return MassesCroyances correspondant ï¿½ la fusion des critï¿½res
	 */
	private MassesCroyances fusionCriteres(final Set<CritereFonctionsCroyances> criteres,
		final Map<String, Double> valeursCourantes) {
		Set<MassesCroyances> masses = new HashSet<MassesCroyances>();
		// Parcours des critï¿½res
		for ( CritereFonctionsCroyances cfc : criteres ) {
			double valC = valeursCourantes.get(cfc.getNom()).doubleValue();
			// Pour chaque critï¿½re, on instancie ï¿½ un objet MassesCroyances. Initialisation des
			// masses de croyances
			MassesCroyances mc =
				new MassesCroyances(cfc.masseCroyancePour(valC), cfc.masseCroyanceContre(valC),
					cfc.masseCroyanceIgnorance(valC), 0);
			masses.add(mc);
		}
		// On renvoie les masses de croyances obtenues aprï¿½s fusion des critï¿½res
		return fusionLesMassesLocales(masses);
	}

	/**
	 * Fonction qui permet de dï¿½finir le meilleur candidat
	 * @param criteres : Ensemble de CritereFonctionsCroyances (les critï¿½res pour cette mï¿½thode)
	 * @return le meilleur candidat
	 */
	public Candidate decision(final Set<CritereFonctionsCroyances> criteres,
		final Set<Candidate> cands) {
		// Parcours de l'ensemble des candidats possibles
		Map<Candidate, MassesCroyances> candidats = new HashMap<Candidate, MassesCroyances>();
		for ( Candidate cand : cands ) {
			// on calcul pour chaque candidat, les masses de croyances obtenues aprï¿½s fusion des
			// critï¿½res
			candidats.put(cand, fusionCriteres(criteres, cand.getValCriteria()));
		}
		// on fusionne entre elles les hypothï¿½ses
		Propositions propositions = fusionHypotheses(candidats);

		// on choisit le meilleur candidat (le meilleur intervalle) : celui qui maximise la
		// probabilitï¿½ pignistique
		return choixCandidat(propositions, cands);
	}

	/**
	 * Fonction de choix d'un candidat
	 * @param propositions : l'ensemble des propositions possibles ainsi que leur masse de croyance
	 *            associï¿½
	 * @param candidates : l'ensemble des candidats possibles
	 * @return le meilleur candidat
	 */
	private Candidate choixCandidat(final Propositions propositions, final Set<Candidate> candidates) {
		// Parcours de l'ensemble des candidats
		Candidate bestCand = null;
		double probaMax = -1;
		for ( Candidate cand : candidates ) {
			// On calcul pour chacun la probabilitï¿½ pignistique que ce candidat soit le bon
			double proba = probaPignistic(cand, propositions);
			if ( proba > probaMax ) {
				probaMax = proba;
				bestCand = cand;
			}
		}
		// on renvoit le candidat qui maximise la probabilitï¿½ pignistic
		return bestCand;
	}

	private/**
			 * Calcul la probabilitï¿½ pignistic qu'un candidat soit le bon
			 * @param cand : le candidat correspondant
			 * @param propositions : la liste des propositions
			 * @return
			 */
	double probaPignistic(final Candidate cand, final Propositions propositions) {
		// on calcul le coefficient de normalisation
		double coeffNorm = 1.0 / (1.0 - propositions.getConflit());
		// On parcours la liste des propositions
		double proba = 0;
		for ( Proposition prop : propositions.propositions ) {
			// Si la propositions dit que ce candidat peut ï¿½tre le bon, on la prend on compte dans
			// le calcul de la proba pignistic
			if ( prop.getHypothese().contains(cand) ) {
				proba += prop.getMasseCroyance() / prop.getHypothese().size();
			}
		}
		// on normalise la proba
		return proba * coeffNorm;
	}

	/**
	 * Classe reprï¿½sentant un ensemble de masse de croyance spï¿½cialisï¿½e (pour la thï¿½orie,
	 * voir Appriou... ou un truc du genre.. voir dans ma thï¿½se la ref)
	 * @author PTaillandier
	 * 
	 */
	private class MassesCroyances {

		// masse de croyance reprï¿½sentant le fait qu'un critï¿½re (source) ou un ensemble de
		// critï¿½res
		// pense qu'il faut apparier le jeu de K courant avec l'intervalle
		private double pour;
		// masse de croyance reprï¿½sentant le fait qu'un critï¿½re (source) ou un ensemble de
		// critï¿½res
		// pense qu'il ne faut pas apparier le jeu de K courant avec l'intervalle
		private double contre;
		// masse de croyance reprï¿½sentant le fait qu'un critï¿½re (source) ou un ensemble de
		// critï¿½res
		// ne sait pas s'il faut apparier le jeu de K courant avec l'intervalle
		private double ignorance;
		// masse de croyance reprï¿½sentant le conflit
		private double conflit;

		/**
		 * @param pour
		 * @param contre
		 * @param ignorance
		 * @param conflit
		 */
		public MassesCroyances(final double pour, final double contre, final double ignorance,
			final double conflit) {
			super();
			this.pour = pour;
			this.contre = contre;
			this.ignorance = ignorance;
			this.conflit = conflit;
		}

		/**
		 * Constructeur basique
		 */
		public MassesCroyances() {}

		@Override
		public String toString() {
			return "pour : " + pour + " - contre : " + contre + " - ignorance : " + ignorance +
				" - conflit : " + conflit;
		}

		public void setContre(final double contre) {
			this.contre = contre;
		}

		public void setIgnorance(final double ignorance) {
			this.ignorance = ignorance;
		}

		public void setPour(final double pour) {
			this.pour = pour;
		}

		public void setConflit(final double conflit) {
			this.conflit = conflit;
		}
	}

	/**
	 * Classe reprï¿½sentant une proposition (genre : il faut apparier le jeu de K courant avec cet
	 * intervalle)
	 * @author PTaillandier
	 * 
	 */
	private class Proposition {

		// Ensemble de Candidate : les hypothï¿½ses reprï¿½sente les candidats
		// une proposition peut avoir comme hypothï¿½se {bon, moyen}, c'est ï¿½ dire que le jeu de K
		// courant est pour cette proposition soit bon, soit mauvais
		private final Set<Candidate> hypothese;
		// valeur de la masse de croyance associï¿½e ï¿½ cette proposition
		private double masseCroyance;

		/**
		 * @param hypothese : ensemble de String
		 * @param masseCroyance
		 */
		public Proposition(final Set<Candidate> hypothese, final double masseCroyance) {
			super();
			this.hypothese = hypothese;
			this.masseCroyance = masseCroyance;
		}

		@Override
		public String toString() {
			return this.hypothese.toString();
		}

		public double getMasseCroyance() {
			return masseCroyance;
		}

		public Set<Candidate> getHypothese() {
			return hypothese;
		}

		@Override
		public int hashCode() {
			// code gï¿½nï¿½rï¿½ automatiquement par Eclipse (pour la comparaison de deux
			// propositions)
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + (hypothese == null ? 0 : hypothese.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			// code gï¿½nï¿½rï¿½ automatiquement par Eclipse (pour la comparaison de deux
			// propositions)
			if ( this == obj ) { return true; }
			if ( obj == null ) { return false; }
			if ( getClass() != obj.getClass() ) { return false; }
			final Proposition other = (Proposition) obj;
			if ( hypothese == null ) {
				if ( other.hypothese != null ) { return false; }
			} else if ( !hypothese.equals(other.hypothese) ) { return false; }
			return true;
		}

	}

	/**
	 * Classe qui reprï¿½sente un ensemble de propositions
	 * @author PTaillandier
	 * 
	 */
	private class Propositions {

		// L'ensemble des Proposition
		private Set<Proposition> propositions;

		/**
		 * Constructeur qui va permettre la fusion des hypothï¿½ses et fournir l'ensemble des
		 * propositions aprï¿½s fusion
		 * @param candidats : dictionnaire des diffï¿½rents candidats possibles : Clef : Candidat ->
		 *            Valeur : MassesCroyances associï¿½es au candidat
		 */
		public Propositions(final Map<Candidate, MassesCroyances> candidats) {
			this.propositions = new HashSet<Proposition>();

			// Parcours de l'ensemble des candidats
			for ( Candidate cand : candidats.keySet() ) {
				MassesCroyances mc1 = candidats.get(cand);

				// si c'est le premier candidat que l'on traite, on initialise les propositions avec
				// celui-ci
				if ( propositions.isEmpty() ) {
					initPropositions(cand, mc1, candidats);
				} else {
					// cas oï¿½ des candidats ont dï¿½jï¿½ ï¿½tï¿½ traitï¿½s -> dans ce cas fusion
					// des
					// propositions prï¿½cï¿½dement obtenues avec ces nouvelles propositions

					// initialisation proposition pour : il faut apparier le vecteur de valeurs
					// courant avec cet intervalle
					Set<Candidate> pourSet = new HashSet<Candidate>();
					pourSet.add(cand);
					Proposition propp = new Proposition(pourSet, mc1.pour);

					// initialisation proposition contre : il ne faut pas apparier le vecteur de
					// valeurs courant avec cet intervalle
					// ï¿½quivalent ï¿½ propotion : il faut apparier le vecteur de valeurs courant
					// avec
					// l'un des autres intervalles
					// initialisation proposition ignorance : il faut apparier le vecteur de valeurs
					// courant avec l'un des intervalles
					Set<Candidate> contreSet = new HashSet<Candidate>();
					Set<Candidate> ignoSet = new HashSet<Candidate>();
					for ( Candidate cand2 : candidats.keySet() ) {
						ignoSet.add(cand2);
						if ( cand == cand2 ) {
							continue;
						}
						contreSet.add(cand2);
					}
					Proposition propc = new Proposition(contreSet, mc1.contre);
					Proposition propi = new Proposition(ignoSet, mc1.ignorance);

					// initialisation proposition conflit (deux critï¿½res qui donne des indications
					// contradictoires)
					Proposition propConflit =
						new Proposition(new HashSet<Candidate>(), mc1.conflit);

					Map<Set<Candidate>, Proposition> propositionsTmp =
						new HashMap<Set<Candidate>, Proposition>();
					// on fusionne ces nouvelles propositions avec les propositions dï¿½jï¿½
					// prï¿½sentes
					// dans l'ensemble propositions
					for ( Proposition prop : propositions ) {
						Proposition propFus1 = fusionPropositions(propp, prop);
						ajouteProposition(propositionsTmp, propFus1);
						Proposition propFus2 = fusionPropositions(propc, prop);
						ajouteProposition(propositionsTmp, propFus2);
						Proposition propFus3 = fusionPropositions(propi, prop);
						ajouteProposition(propositionsTmp, propFus3);
						Proposition propFus4 = fusionPropositions(propConflit, prop);
						ajouteProposition(propositionsTmp, propFus4);
					}
					propositions = new HashSet<Proposition>();
					propositions.addAll(propositionsTmp.values());
				}
			}
		}

		/**
		 * Mï¿½thode qui permet d'ajouter une proposition ï¿½ un dictionnaire de propositions
		 * @param propositionsTmp : dictionnaire de propositions : Clef : String : le nom de la
		 *            proposition -> Valeur : Proposition : proposition correspondante
		 * @param propFus : la nouvelle proposition
		 */
		public void ajouteProposition(final Map<Set<Candidate>, Proposition> propositionsTmp,
			final Proposition propFus) {
			// s'il y a dï¿½jï¿½ une proposition similaire (avec le mï¿½me nom) dans le dictionnaire
			// propositionsTmp, on la rï¿½cupï¿½re
			Proposition propExiste = propositionsTmp.get(propFus.getHypothese());
			// si il n'y en a pas, on ajoute directement la nouvelle proposition
			if ( propExiste == null ) {
				propositionsTmp.put(propFus.getHypothese(), propFus);
			} else {
				propExiste.masseCroyance += propFus.masseCroyance;
				propositionsTmp.put(propExiste.getHypothese(), propExiste);
			}
		}

		/**
		 * Mï¿½thode qui permet de fusionner deux propositions
		 * @param prop1
		 * @param prop2
		 * @return la proposition fusionnï¿½
		 */
		public Proposition fusionPropositions(final Proposition prop1, final Proposition prop2) {
			Proposition propFus = null;
			Set<Candidate> fusSet = new HashSet<Candidate>();
			// La proposition obtenue aprï¿½s fusion a pour ensemble d'hypothï¿½ses, l'intersection
			// entre l'ensemble d'hypothï¿½se de prop1 et de prop2
			for ( Candidate hyp : prop1.getHypothese() ) {
				if ( prop2.getHypothese().contains(hyp) ) {
					fusSet.add(hyp);
				}
			}
			// On instancie cette nouvelle proposition avec la valeur de masse de croyance
			// correspondante
			propFus = new Proposition(fusSet, prop1.getMasseCroyance() * prop2.getMasseCroyance());
			return propFus;
		}

		/**
		 * Initialisation de l'ensemble de propositions ï¿½ partir d'un candidat (d'une hypothï¿½se)
		 * @param cand : un candidat (une hypothï¿½se)
		 * @param mc1 : les masses de croyance associï¿½es ï¿½ cette hypothï¿½se
		 * @param candidats : dictionnaire des diffï¿½rents candidats possibles : Clef : Intervalle
		 *            -> Valeur : MassesCroyances associï¿½es ï¿½ l'intervalle
		 */
		public void initPropositions(final Candidate cand, final MassesCroyances mc1,
			final Map<Candidate, MassesCroyances> candidats) {
			// initialisation proposition pour : ce candidat est le meilleur
			Set<Candidate> pourSet = new HashSet<Candidate>();

			Proposition propp = new Proposition(pourSet, mc1.pour);
			propositions.add(propp);

			// initialisation proposition contre : ce candidat n est pas le meilleur
			// ï¿½quivalent ï¿½ propotion : l'un des autres candidats est meilleur
			// initialisation proposition ignorance : l'un des candidats est le meilleur
			Set<Candidate> contreSet = new HashSet<Candidate>();
			Set<Candidate> ignoSet = new HashSet<Candidate>();
			for ( Candidate c : candidats.keySet() ) {
				ignoSet.add(c);
				if ( c != cand ) {
					contreSet.add(c);
				}
			}

			Proposition propc = new Proposition(contreSet, mc1.contre);
			propositions.add(propc);
			Proposition propi = new Proposition(ignoSet, mc1.ignorance);
			propositions.add(propi);

			// initialisation proposition conflit (deux critï¿½res qui donne des indications
			// contradictoires)
			Proposition propConflit = new Proposition(new HashSet<Candidate>(), mc1.conflit);
			propositions.add(propConflit);
		}

		/**
		 * @return la valeur du conflit pour l'ensemble des propositions
		 */
		public double getConflit() {
			for ( Proposition prop : propositions ) {
				if ( prop.hypothese.isEmpty() ) { return prop.masseCroyance; }
			}
			return 0;
		}

		@Override
		public String toString() {
			return "propositions : " + propositions;
		}

	}

}
