package msi.gama.util.graph;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author PTaillandier
 * Classe faisant office de chronomètre
 * Pour l'utiliser : 
 * Chrono c = new Chrono();
 * c.start()
 * -----------
 * c.stop();
 * c.getSec(); //ou c.getMilliSec()
 * 
 */
public class Chrono {
	Calendar m_start;

	Calendar m_stop;

	public Chrono() {
		m_start = new GregorianCalendar();
		m_stop = new GregorianCalendar();
	}

	public void start() {
		m_start.setTime(new Date());
	}

	public void stop() {
		m_stop.setTime(new Date());
	}

	/**
	 * @return temps en millisecondes
	 */
	public long getMilliSec() {
		return (m_stop.getTimeInMillis() - m_start.getTimeInMillis());
	}

	/**
	 * @return temps en secondes
	 */
	public long getSec() {
		return (m_stop.getTimeInMillis() - m_start.getTimeInMillis()) / 1000;
	}

	public void printMilliSec() {
		if (getMilliSec() <= 0)
			System.out.println("Vous n'avez pas arrété le chronomètre");
		else
			System.out.println("Temps d'exécution : " + getMilliSec() + " ms");
	}
}