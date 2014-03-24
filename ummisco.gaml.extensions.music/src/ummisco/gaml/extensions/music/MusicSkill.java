package ummisco.gaml.extensions.music;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@skill(name = "music")
@doc("Music plug-in")
public class MusicSkill extends Skill {

	// TODO one unique background music for "world" agent
	
	private static Map<IAgent, BasicPlayer> musicPlayerOfAgents = new HashMap<IAgent, BasicPlayer>();

	// TODO change this to a Statement
	@action(name = "play", 
		args = {
			@arg(name = "sound", type = IType.STRING, optional = false, doc = @doc("to be described")),
			@arg(name = "overwrite", type = IType.BOOL, optional = true, doc = @doc("to be describe"))
		},
		doc = @doc(value = "to be described")
	)
	public void playMusic(final IScope scope) throws GamaRuntimeException {
		IAgent currentAgent = getCurrentAgent(scope);
		
		BasicPlayer musicPlayer = musicPlayerOfAgents.get(currentAgent);
		if (musicPlayer == null) {
			musicPlayer = new BasicPlayer();
			musicPlayerOfAgents.put(currentAgent, musicPlayer);
		}
		
		
		String musicFile = scope.getStringArg("sound");
		String musicFilePath = scope.getModel().getRelativeFilePath(musicFile, false);
		
		BasicController control = (BasicController) musicPlayer;
		try {
			control.open(new File(musicFilePath));
			
			control.play();
		} catch (final BasicPlayerException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.getMessage(), scope);
		}
		
		
	}
	
	
	/**
	 * clean-up
	 */
	public void manageMusicPlayers() {
		
	}
	
	
	/**
	 * clean-up
	 */
	public void schedulerDisposed() {
		
	}
}
