package gama_analyzer;

import java.util.Iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gaml.species.ISpecies;

public class GroupIdRuleList extends GroupIdRule {

	String nom;
	List<GamlAgent> liste = null;

	public List<GamlAgent> getListe() {
		return liste;
	}
	public void setListe(GamaList<GamlAgent> liste) {
		this.liste = liste;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	public IList<IAgent> update(IScope scope, IList<IAgent> liste) {
		liste=(GamaList)scope.getGlobalVarValue(nom);
		return liste;
	}
	
	public IList<IAgent> updatea(IScope scope, IList<IAgent> liste) {

		liste=(IList<IAgent>) new ArrayList<IAgent>();
		Map<String,ISpecies> especes =scope.getModel().getAllSpecies();
		System.out.println("especes= " + especes);
		Iterator<String> it=especes.keySet().iterator();
		while (it.hasNext())
		{
			String s=it.next();
			if (s.equals(nom)) {
			Iterator<IAgent> monde = (Iterator<IAgent>) especes.get(s).iterable(scope).iterator();
			while (monde.hasNext()) {
					liste.add(monde.next()); 
				}
			}
		}
		System.out.println("liste= " + liste);
		return liste;
	}

}
