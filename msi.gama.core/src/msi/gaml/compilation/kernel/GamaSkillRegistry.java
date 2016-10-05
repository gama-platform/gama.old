package msi.gaml.compilation.kernel;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.ISkill;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IDescription.DescriptionVisitor;
import msi.gaml.descriptions.SkillDescription;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaSkillRegistry {

	public final static GamaSkillRegistry INSTANCE = new GamaSkillRegistry();
	private final THashMap<String, SkillDescription> skills = new THashMap<>();
	private final THashMap<Class, String> classSkillNames = new THashMap<>();

	private GamaSkillRegistry() {
	}

	public SkillDescription register(final String name, final Class<? extends ISkill> support, final String plugin,
			final String... species) {
		final SkillDescription sd = new SkillDescription(name, support, plugin);
		classSkillNames.put(support, name);
		skills.put(name, sd);
		for (final String spec : species) {
			GamaMetaModel.INSTANCE.addSpeciesSkill(spec, name);
		}
		return sd;

	}

	public SkillDescription get(final String name) {
		return skills.get(name);
	}

	public SkillDescription get(final Class clazz) {
		final String name = classSkillNames.get(clazz);
		if (name == null)
			return null;
		return skills.get(name);
	}

	public ISkill getSkillInstanceFor(final String skillName) {
		final SkillDescription sd = skills.get(skillName);
		return sd == null ? null : sd.getInstance();
	}

	public Class<? extends ISkill> getSkillClassFor(final String skillName) {
		final SkillDescription sd = skills.get(skillName);
		return sd == null ? null : sd.getJavaBase();
	}

	public String getSkillNameFor(final Class skillClass) {
		return classSkillNames.get(skillClass);
	}

	public boolean hasSkill(final String name) {
		return skills.containsKey(name);
	}

	public Collection<String> getAllSkillNames() {
		return skills.keySet();
	}

	public Collection<String> getSkillNames() {
		final Set<String> result = new LinkedHashSet();
		for (final String s : getAllSkillNames()) {
			final SkillDescription c = skills.get(s);
			if (!c.isControl()) {
				result.add(s);
			}
		}
		return result;
	}

	public Collection<String> getArchitectureNames() {
		final Set<String> result = new LinkedHashSet();
		for (final String s : getAllSkillNames()) {
			final SkillDescription c = skills.get(s);
			if (c.isControl()) {
				result.add(s);
			}
		}
		return result;

	}

	public Iterable<? extends IDescription> getVariablesForSkill(final String s) {
		final SkillDescription sd = skills.get(s);
		if (sd == null)
			return Collections.EMPTY_LIST;
		return sd.getAttributes();
	}

	public Iterable<? extends IDescription> getActionsForSkill(final String s) {
		final SkillDescription sd = skills.get(s);
		if (sd == null)
			return Collections.EMPTY_LIST;
		return sd.getActions();
	}

	public void visitSkills(final DescriptionVisitor visitor) {
		skills.forEachValue(visitor);
	}

}
