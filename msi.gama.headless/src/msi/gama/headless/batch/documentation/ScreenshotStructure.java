/*******************************************************************************************************
 *
 * ScreenshotStructure.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.batch.documentation;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class ScreenshotStructure.
 */
public class ScreenshotStructure {
	
	/** The display parameters. */
	private List<DisplayParametersStructure> displayParameters = new ArrayList<DisplayParametersStructure>();
	
	/** The id. */
	public String ID;
	
	/**
	 * The Class DisplayParametersStructure.
	 */
	private class DisplayParametersStructure {
		
		/** The Display name. */
		public String DisplayName;
		
		/** The Cycle number. */
		public int CycleNumber;
		
		/**
		 * Instantiates a new display parameters structure.
		 *
		 * @param displayName the display name
		 * @param cycleNumber the cycle number
		 */
		public DisplayParametersStructure(String displayName, int cycleNumber) {
			DisplayName = displayName;
			CycleNumber = cycleNumber;
		}
	}
	
	/**
	 * Instantiates a new screenshot structure.
	 *
	 * @param id the id
	 */
	public ScreenshotStructure(String id) {
		ID = id;
	}
	
	/**
	 * Gets the final step.
	 *
	 * @return the final step
	 */
	public int getFinalStep() {
		int result=1;
		for (int displayId = 0 ; displayId < displayParameters.size(); displayId++) {
			if (result <= displayParameters.get(displayId).CycleNumber) {
				result = displayParameters.get(displayId).CycleNumber + 1;
			}
		}
		return result;
	}
	
	/**
	 * Check display name.
	 *
	 * @param displayNames the display names
	 * @return true, if successful
	 */
	public boolean checkDisplayName(ArrayList<String> displayNames) {
		// return true if all the list of internal "display names" are also present in the list "displayNames".
		for (int displayIdx = 0 ; displayIdx < displayParameters.size() ; displayIdx++) {
			if (!displayNames.contains(displayParameters.get(displayIdx).DisplayName)) {
				System.err.println(displayParameters.get(displayIdx).DisplayName+" display is impossible to find...");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Gets the XML content.
	 *
	 * @param simNumber the sim number
	 * @param modelPath the model path
	 * @param experiment the experiment
	 * @return the XML content
	 */
	public String getXMLContent(String simNumber, String modelPath, String experiment) {
		String result = "";
		
		result += "  <Simulation id=\""+simNumber+"\" sourcePath=\"/"+modelPath+"\" finalStep=\""+getFinalStep()+"\" experiment=\""+experiment+"\">\n";
        result += "    <Outputs>\n";
        
        // browse all the displays
        for (int displayIdx = 0 ; displayIdx < displayParameters.size() ; displayIdx++) {
        	String display = displayParameters.get(displayIdx).DisplayName;
        	result += "      <Output id=\""+displayIdx+1+"\" name=\""+display+"\" framerate=\""+displayParameters.get(displayIdx).CycleNumber+"\" />\n";
        }
        
        result += "    </Outputs>\n";
        result += "  </Simulation>\n";
		
		return result;
	}
	
	/**
	 * Adds the display.
	 *
	 * @param displayName the display name
	 */
	public void addDisplay(String displayName) {
		addDisplay(displayName,10);
	}
	
	/**
	 * Adds the display.
	 *
	 * @param displayName the display name
	 * @param cycleNumber the cycle number
	 */
	public void addDisplay(String displayName, int cycleNumber) {
		displayParameters.add(new DisplayParametersStructure(displayName,cycleNumber));
	}
}
