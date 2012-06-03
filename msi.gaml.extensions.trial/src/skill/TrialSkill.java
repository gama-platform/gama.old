package skill;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.skills.Skill;

@skill(name = "trial")
public class TrialSkill extends Skill {

	@action(name="trial_write")
	@args(names = { "test" })
	public Object trialWrite(final IScope scope) throws GamaRuntimeException {
		System.out.println("Hello World");
		return null;
	}

}
