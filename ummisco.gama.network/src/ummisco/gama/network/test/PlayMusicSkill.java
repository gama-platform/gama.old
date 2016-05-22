package ummisco.gama.network.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;
import ummisco.gama.network.skills.INetworkSkill;

@skill(name = IPlayMusic.MUSIC_SKILL, concept = { IConcept.NETWORK, IConcept.COMMUNICATION, IConcept.SKILL })
public class PlayMusicSkill extends Skill implements IPlayMusic  {

		public int volume = 200;
	    
	    private Synthesizer synthetiseur;
	    private MidiChannel canal;
	    
	    public PlayMusicSkill(){
	        
	        try {
	            //On récupère le synthétiseur, on l'ouvre et on obtient un canal
	            synthetiseur = MidiSystem.getSynthesizer();
	            synthetiseur.open();
	        } catch (MidiUnavailableException ex) {
	            Logger.getLogger(PlayMusicSkill.class.getName()).log(Level.SEVERE, null, ex);
	        }
	        canal = synthetiseur.getChannels()[0];
	        
	        //On initialise l'instrument 0 (le piano) pour le canal
	    canal.programChange(0);
	    }
	    
	    //Joue la note dont le numéro est en paramètre
	    public void note_on(int note){
	        canal.noteOn(note, volume);
	    }
	    //Arrête de jouer la note dont le numéro est en paramètre
	    public void note_off(int note){
	        canal.noteOff(note);
	    }
	    //Set le type d'instrument dont le numéro MIDI est précisé en paramètre
	    public void set_instrument(int instru){
	        canal.programChange(instru);
	    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PlayMusicSkill mon_instru = new PlayMusicSkill();
		mon_instru.note_on(75);
		mon_instru.note_on(50);
		mon_instru.note_on(25);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}

}
