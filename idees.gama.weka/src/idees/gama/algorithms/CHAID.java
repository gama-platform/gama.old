
/*
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 * CHAID.java Copyright (C) 2007 Giuseppe Manco
 *
 */

package idees.gama.algorithms;

import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NoSupportForMissingValuesException;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Statistics;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;

/**
 * @author Antonio Sanso
 *
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code
 *         Generation - Code and Comments
 */

public class CHAID extends Classifier implements OptionHandler {

	/** Binary splits on nominal attributes? */
	private boolean m_binarySplits = false;

	/** split point? */
	private double split = 0.05;

	/** The node's successors. */
	private CHAID[] m_Successors;

	private Instances originalData;

	/** Attribute used for splitting. */
	private Attribute m_Attribute;

	/** Class value if node is leaf. */
	private double m_ClassValue;

	/** Class distribution if node is leaf. */
	private double[] m_Distribution;

	/** Class attribute of dataset. */
	private Attribute m_ClassAttribute, af;

	private double[] indexFTest;

	public String globalInfo() {
		return "CHAID ";
	}

	@Override
	public Enumeration listOptions() {
		final Vector<Option> newVector = new Vector<Option>(1);
		newVector.addElement(new Option("\tUse binary splits only.", "B", 0, "-B"));

		newVector.addElement(new Option("\tSplit point.\n" + "\t(default 0.05)", "S", 1, "-S <spit point>"));

		return newVector.elements();
	}

	@Override
	public void setOptions(final String[] options) throws Exception {

		final String splitS = Utils.getOption('S', options);
		if (splitS.length() != 0) {
			split = Double.parseDouble(splitS);
		} else
			split = 0.05;
	}

	@Override
	public String[] getOptions() {
		final String[] options = new String[2];
		int current = 0;

		options[current++] = "-S";
		options[current++] = "" + split;
		while (current < options.length) {
			options[current++] = "";
		}
		return options;
	}

	public boolean getBinarySplits() {

		return m_binarySplits;
	}

	public String binarySplitsTipText() {
		return "Whether to use binary splits on nominal attributes when " + "building the trees.";
	}

	public void setBinarySplits(final boolean v) {
		m_binarySplits = v;
	}

	public double getSplitPoint() {
		return split;
	}

	public String splitTipText() {
		return "split point.";
	}

	public void setSplitPoint(final double s) {
		split = s;
	}

	@Override
	public void buildClassifier(Instances data) throws Exception {

		if (!data.classAttribute()
				.isNominal()) { throw new UnsupportedClassTypeException("CHAID: nominal class, please."); }

		final Enumeration<?> enu = data.enumerateInstances();
		while (enu.hasMoreElements()) {
			if (((Instance) enu.nextElement()).hasMissingValue()) { throw new NoSupportForMissingValuesException(
					"CHAID: no missing values, " + "please."); }
		}

		originalData = new Instances(data);
		data = new Instances(data);
		makeTree(data);
	}

	private void setAttributeFather(final Attribute af) {
		this.af = af;
	}

	private void setOriginalData(final Instances od) {
		originalData = od;
	}

	private void makeTree(final Instances data) throws Exception {

		if (data.numInstances() == 0) {
			m_Attribute = null;
			m_ClassValue = Instance.missingValue();
			m_Distribution = new double[data.numClasses()];
			return;
		}

		if (data.numAttributes() == 1)
			data.insertAttributeAt(af, 0);
		final double[] infoChi = new double[data.numAttributes() - 1];
		indexFTest = new double[data.numAttributes() - 1];
		int c = 0;

		final Enumeration<?> attEnum = data.enumerateAttributes();
		while (attEnum.hasMoreElements() && c < data.numAttributes() - 1) {// scorrimento degli attributi
			c++;
			final Attribute att = (Attribute) attEnum.nextElement();
			if (att.isNominal())
				infoChi[att.index()] = computeFChi(data, att, 0);// calcolo della funzione Chi-quadro
			else
				infoChi[att.index()] = computeFChiNumeric(data, att);

		}

		m_Attribute = data.attribute(Utils.minIndex(infoChi));// selezione minimo
		if (split != 0 && infoChi[m_Attribute.index()] > split || infoChi[m_Attribute.index()] == 0
				|| infoChi[m_Attribute.index()] == 1) {// test per l'early stopping
			// non superato==> nodo foglia
			m_Attribute = null;
			///////////////////////////////////////////////////////////////
			//
			m_Distribution = new double[data.numClasses()];
			final Enumeration<?> instEnum = data.enumerateInstances();
			while (instEnum.hasMoreElements()) {
				final Instance inst = (Instance) instEnum.nextElement();
				m_Distribution[(int) inst.classValue()]++;
			}
			Utils.normalize(m_Distribution);
			m_ClassValue = Utils.maxIndex(m_Distribution);
			m_ClassAttribute = data.classAttribute();
			//
			// operazioni di labeling relative alla foglia
			//
			//
			/////////////////////////////////////////////////////////////////
		} else {
			// se non � una foglia crea i figli
			Instances[] splitData;
			if (m_Attribute.isNominal()) {
				splitData = splitData(data, m_Attribute);// crea le partizioni della tabella
				m_Successors = new CHAID[m_Attribute.numValues()];// un figlio per ogni valore dell'attributo
			} else {
				splitData = splitNumeric(data, m_Attribute, indexFTest[m_Attribute.index()]);
				m_Successors = new CHAID[2];
			}
			for (int j = 0; j < m_Successors.length; j++) {
				m_Successors[j] = new CHAID();
				m_Successors[j].setAttributeFather(m_Attribute);// setta il padre al figlio
				m_Successors[j].setOriginalData(originalData);
				m_Successors[j].setSplitPoint(split);
				splitData[j].deleteAttributeAt(m_Attribute.index());
				m_Successors[j].makeTree(splitData[j]);// crea il sotto-albero figlio
			}
		}
	}

	private double computeFChi(final Instances data, final Attribute att, final double conf) throws Exception {

		int[][] contingenza;
		Instances[] splitData;
		if (att.isNominal()) {
			splitData = splitData(data, att);
			contingenza = new int[data.numClasses() + 1][att.numValues() + 1];
		} else {
			splitData = splitNumeric(data, att, conf);
			contingenza = new int[data.numClasses() + 1][3];
		}

		for (int j = 0; j < splitData.length; j++) {
			final int[] inst = countInstances(splitData[j]);
			for (int i = 0; i < data.numClasses(); i++) {
				contingenza[i][j] = inst[i];
			}
		}

		for (int i = 0; i < contingenza.length - 1; i++) {
			for (int j = 0; j < contingenza[0].length - 1; j++) {
				contingenza[i][contingenza[0].length - 1] += contingenza[i][j];
				contingenza[data.numClasses()][j] += contingenza[i][j];
			}
		}

		for (int j = 0; j < contingenza.length - 1; j++) {
			if (contingenza[j][contingenza[0].length - 1] == data.numInstances())
				return 0;
		}

		for (int i = 0; i < contingenza.length - 1; i++)
			contingenza[data.numClasses()][contingenza[0].length - 1] += contingenza[i][contingenza[0].length - 1];

		final double[][] e = new double[data.numClasses() + 1][contingenza[0].length - 1 + 1];

		for (int i = 0; i < contingenza.length - 1; i++) {
			for (int j = 0; j < contingenza[0].length - 1; j++) {
				if (contingenza[data.numClasses()][contingenza[0].length - 1] != 0)
					e[i][j] = (contingenza[i][contingenza[0].length - 1] + 0.0) * contingenza[data.numClasses()][j]
							/ contingenza[data.numClasses()][contingenza[0].length - 1];
			}
		}

		for (int i = 0; i < contingenza.length - 1; i++) {
			for (int j = 0; j < contingenza[0].length - 1; j++) {
				e[i][contingenza[0].length - 1] += e[i][j];
				e[data.numClasses()][j] += e[i][j];
			}
		}

		for (int i = 0; i < contingenza.length - 1; i++)
			e[data.numClasses()][contingenza[0].length - 1] += e[i][contingenza[0].length - 1];

		// gi� calcolate tabella di contingenza e tabella di appoggio (Eij)
		// righe = valori attributi && colonne = valori classi
		final double[][] ris = new double[data.numClasses() + 1][contingenza[0].length - 1 + 1]; // inizializzazione ris

		for (int i = 0; i < contingenza.length - 1; i++) {
			for (int j = 0; j < contingenza[0].length - 1; j++) {
				if (e[i][j] != 0)
					// SUM[(nja-Eja)^2/Eja + (njb-Ejb)^2/Ejb]
					ris[i][j] = Math.pow(contingenza[i][j] - e[i][j], 2) / e[i][j];
			}
		}

		for (int i = 0; i < contingenza.length - 1; i++) {
			for (int j = 0; j < contingenza[0].length - 1; j++) {
				ris[i][contingenza[0].length - 1] += ris[i][j];// sommatoria colonne
				ris[data.numClasses()][j] += ris[i][j];// sommatoria righe
			}
		}

		for (int i = 0; i < contingenza.length - 1; i++)
			ris[data.numClasses()][contingenza[0].length - 1] += ris[i][contingenza[0].length - 1];

		final double distrChi = Statistics.chiSquaredProbability(ris[data.numClasses()][contingenza[0].length - 1], // calcolo
																													// funzione
				contingenza[0].length - 1 - 1); // Chi-quadro

		return distrChi;
	}

	private Instances[] splitData(final Instances data, final Attribute att) {

		final Instances[] splitData = new Instances[att.numValues()];
		final int[] count = new int[att.numValues()];

		final Enumeration<?> instEnum2 = data.enumerateInstances();
		while (instEnum2.hasMoreElements()) {
			final Instance inst = (Instance) instEnum2.nextElement();
			count[(int) inst.value(att)]++;
		}

		for (int j = 0; j < att.numValues(); j++) {
			splitData[j] = new Instances(data, count[j]);
		}

		final Enumeration<?> instEnum = data.enumerateInstances();
		while (instEnum.hasMoreElements()) {
			final Instance inst = (Instance) instEnum.nextElement();
			splitData[(int) inst.value(att)].add(inst);
		}

		for (int i = 0; i < splitData.length; i++) {
			splitData[i].compactify();
		}

		return splitData;
	}

	private int[] countInstances(final Instances data) throws Exception {
		final int[] classCounts = new int[data.numClasses()];
		for (int i = 0; i < data.numClasses(); i++)
			classCounts[i] = 0;
		final Enumeration<?> instEnum = data.enumerateInstances();
		while (instEnum.hasMoreElements()) {
			final Instance inst = (Instance) instEnum.nextElement();
			classCounts[(int) inst.classValue()]++;
		}
		return classCounts;
	}

	public static void main(final String[] args) {
		try {
			System.out.println(Evaluation.evaluateModel(new CHAID(), args));
		} catch (final Exception e) {
			System.err.println(e.getMessage());
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public double classifyInstance(final Instance instance) throws NoSupportForMissingValuesException {

		if (instance.hasMissingValue()) { throw new NoSupportForMissingValuesException(
				"CHAID: no missing values, " + "please."); }

		if (m_Attribute == null) {
			return m_ClassValue;
		} else {
			final Attribute attr = originalData.attribute(m_Attribute.name());
			if (m_Attribute.isNominal())
				return m_Successors[(int) instance.value(attr)].classifyInstance(instance);
			else {
				if (instance.value(m_Attribute) <= indexFTest[m_Attribute.index()])
					return m_Successors[0].classifyInstance(instance);
				else
					return m_Successors[1].classifyInstance(instance);
			}
		}
	}

	@Override
	public double[] distributionForInstance(final Instance instance) throws NoSupportForMissingValuesException {

		if (instance.hasMissingValue()) { throw new NoSupportForMissingValuesException(
				"CHAID: no missing values, " + "please."); }

		if (m_Attribute == null) {
			return m_Distribution;
		} else {
			final Attribute attr = originalData.attribute(m_Attribute.name());
			if (m_Attribute.isNominal())
				return m_Successors[(int) instance.value(attr)].distributionForInstance(instance);
			else {
				if (instance.value(m_Attribute) <= indexFTest[m_Attribute.index()])
					return m_Successors[0].distributionForInstance(instance);
				else
					return m_Successors[1].distributionForInstance(instance);
			}

		}
	}

	private String toString(final int level) {
		final StringBuffer text = new StringBuffer();
		if (m_Attribute == null) {
			if (Instance.isMissingValue(m_ClassValue)) {
				text.append(": null");
			} else {
				text.append(": " + m_ClassAttribute.value((int) m_ClassValue));
			}
		} else {
			if (m_Attribute.isNominal()) {
				for (int j = 0; j < m_Attribute.numValues(); j++) {
					text.append("\n");
					for (int i = 0; i < level; i++) {
						text.append("|  ");
					}
					text.append(m_Attribute.name() + " = " + m_Attribute.value(j));
					text.append(m_Successors[j].toString(level + 1));
				}
			} else {
				for (int j = 0; j < 2; j++) {
					text.append("\n");
					for (int i = 0; i < level; i++) {
						text.append("| ");
					}

					if (j == 0) {
						text.append(m_Attribute.name() + " <= " + indexFTest[m_Attribute.index()]);
					} else
						text.append(m_Attribute.name() + " > " + indexFTest[m_Attribute.index()]);

					text.append(m_Successors[j].toString(level + 1));
				}
			}
		}
		return text.toString();
	}

	@Override
	public String toString() {
		if (m_Distribution == null && m_Successors == null) { return "CHAID: No model built yet."; }
		return "CHAID\n\n" + toString(0);
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	public double computeFChiNumeric(final Instances data, final Attribute att) throws Exception {

		final double splitIndex;
		double currentInfoGain;
		double c;

		data.sort(att);

		Instance indice = data.instance(0);
		double conf = indice.value(att);
		double t = conf;
		currentInfoGain = computeFChi(data, att, conf);
		indexFTest[att.index()] = conf;

		for (int i = 1; i < data.numInstances(); i++) {
			indice = data.instance(i);
			conf = indice.value(att);
			if (conf != t) {
				c = computeFChi(data, att, conf);
				if (c < currentInfoGain) {
					currentInfoGain = c;
					indexFTest[att.index()] = conf;
				}
				t = conf;
			}
		}

		return currentInfoGain;
	}

	public Instances[] splitNumeric(final Instances data, final Attribute att, final double conf) {
		final Instances[] instance = new Instances[2];

		int count = 0;

		final Enumeration<?> instEnum = data.enumerateInstances();
		while (instEnum.hasMoreElements()) {
			final Instance inst = (Instance) instEnum.nextElement();
			final double value = inst.value(att);
			if (value <= conf)
				count++;
		}

		instance[0] = new Instances(data, count);
		instance[1] = new Instances(data, data.numInstances() - count);

		final Enumeration<?> instEnum2 = data.enumerateInstances();
		while (instEnum2.hasMoreElements()) {
			final Instance inst = (Instance) instEnum2.nextElement();
			final double value = inst.value(att);
			if (value <= conf) {
				instance[0].add(inst);
			} else {
				instance[1].add(inst);
			}
		}

		return instance;
	}

}