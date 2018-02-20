package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars({ /* @var(name = "name", type = IType.STRING), */@var(name = "agent", type = IType.AGENT),
		@var(name = "liking", type = IType.FLOAT), @var(name = "dominance", type = IType.FLOAT),
		@var(name = "solidarity", type = IType.FLOAT), @var(name = "familiarity", type = IType.FLOAT),
		@var(name = "trust", type = IType.FLOAT)})

public class SocialLink implements IValue {

	IAgent agent;
	Double liking = 0.0;
	Double dominance = 0.0;
	Double solidarity = 0.0;
	Double familiarity = 0.0;
	Double trust = 0.0;
	private Boolean noLiking = true;
	private Boolean noDominance = true;
	private Boolean noSolidarity = true;
	private Boolean noFamiliarity = true;
	private Boolean noTrust = true;

	@getter("agent")
	public IAgent getAgent() {
		return agent;
	}

	@getter("liking")
	public Double getLiking() {
		return liking;
	}

	@getter("dominance")
	public Double getDominance() {
		return dominance;
	}

	@getter("solidarity")
	public Double getSolidarity() {
		return solidarity;
	}

	@getter("familiarity")
	public Double getFamiliarity() {
		return familiarity;
	}
	
	@getter("trust")
	public Double getTrust() {
		return trust;
	}

	public Boolean getNoLiking() {
		return noLiking;
	}

	public Boolean getNoDominance() {
		return noDominance;
	}

	public Boolean getNoSolidarity() {
		return noSolidarity;
	}

	public Boolean getNoFamiliarity() {
		return noFamiliarity;
	}
	
	public Boolean getNoTrust() {
		return noTrust;
	}

	public void setAgent(final IAgent ag) {
		this.agent = ag;
	}

	public void setLiking(final Double appre) {
		this.liking = appre;
		this.noLiking = false;
	}

	public void setDominance(final Double domi) {
		this.dominance = domi;
		this.noDominance = false;
	}

	public void setSolidarity(final Double solid) {
		this.solidarity = solid;
		this.noSolidarity = false;
	}

	public void setFamiliarity(final Double fami) {
		this.familiarity = fami;
		this.noFamiliarity = false;
	}
	
	public void setTrust(final Double tru) {
		this.trust = tru;
		this.noTrust = false;
	}

	public SocialLink() {
		this.agent = null;
	}

	public SocialLink(final IAgent ag) {
		this.agent = ag;
	}

	public SocialLink(final IAgent ag, final Double appre, final Double domi, final Double solid, final Double fami) {
		this.agent = ag;
		this.liking = appre;
		this.noLiking = false;
		this.dominance = domi;
		this.noDominance = false;
		this.solidarity = solid;
		this.noDominance = false;
		this.familiarity = fami;
		this.noFamiliarity = false;
	}

	public SocialLink(final IAgent ag, final Double appre, final Double domi, final Double solid, final Double fami, final Double tru) {
		this.agent = ag;
		this.liking = appre;
		this.noLiking = false;
		this.dominance = domi;
		this.noDominance = false;
		this.solidarity = solid;
		this.noDominance = false;
		this.familiarity = fami;
		this.noFamiliarity = false;
		this.trust = tru;
		this.noTrust = false;
	}
	
	@Override
	public String toString() {
		return serialize(true);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "(" + agent + "," + liking + "," + dominance + "," + solidarity + "," + familiarity + ")";
	}

	@Override
	public IType<?> getType() {
		return Types.get(SocialLinkType.id);
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "(" + agent + "," + liking + "," + dominance + "," + solidarity + "," + familiarity + ")";
	}

	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return new SocialLink(agent, liking, dominance, solidarity, familiarity);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SocialLink other = (SocialLink) obj;
		if (this.agent != null && other.getAgent() != null) {
			if (!agent.equals(other.getAgent())) {
				return false;
			}
		}
		if (!noLiking && !other.getNoLiking()) {
			if (!liking.equals(other.getLiking())) {
				return false;
			}
		}
		if (!noDominance && !other.getNoDominance()) {
			if (!dominance.equals(other.getDominance())) {
				return false;
			}
		}
		if (!noSolidarity && !other.getNoSolidarity()) {
			if (!solidarity.equals(other.getSolidarity())) {
				return false;
			}
		}
		if (!noFamiliarity && !other.getNoFamiliarity()) {
			if (!familiarity.equals(other.getFamiliarity())) {
				return false;
			}
		}
		if (!noTrust && !other.getNoTrust()) {
			if (!trust.equals(other.getTrust())) {
				return false;
			}
		}		
		return true;
	}

	public boolean equalsInAgent(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SocialLink other = (SocialLink) obj;
		if (agent != null || other.getAgent() != null) {
			if (!agent.equals(other.getAgent())) {
				return false;
			}
		}
		return true;
	}

}
