/*
 * Rect.java
 *
 *
 * The Salamander Project - 2D and 3D graphics libraries in Java Copyright (C) 2004 Mark McKay
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Mark McKay can be contacted at mark@kitfox.com. Salamander and other projects can be found at http://www.kitfox.com
 *
 * Created on January 26, 2004, 5:25 PM
 */

package msi.gama.ext.svgsalamander;

import static java.lang.Float.parseFloat;

import java.awt.geom.GeneralPath;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class Path extends ShapeElement<GeneralPath> {

	static Pattern pattern = Pattern.compile("([MmLlHhVvAaQqTtCcSsZz])|([-+]?((\\d*\\.\\d+)|(\\d+))([eE][-+]?\\d+)?)");

	@Override
	public void build() throws SVGException {
		int fillRule = GeneralPath.WIND_NON_ZERO;
		String d = "";
		final StyleAttribute sty = new StyleAttribute();
		final String fillRuleStrn = getStyle(sty.setName("fill-rule")) ? sty.getStringValue() : "nonzero";
		fillRule = fillRuleStrn.equals("evenodd") ? GeneralPath.WIND_EVEN_ODD : GeneralPath.WIND_NON_ZERO;
		if (getPres(sty.setName("d"))) { d = sty.getStringValue(); }
		final PathCommand[] commands = parsePathList(d);
		int numKnots = 2;
		for (final PathCommand command : commands) {
			numKnots += command.getNumKnotsAdded();
		}
		final GeneralPath path = new GeneralPath(fillRule, numKnots);
		final BuildHistory hist = new BuildHistory();
		for (final PathCommand cmd : commands) {
			cmd.appendPath(path, hist);
		}
		shape = path;
	}

	static protected float next(final LinkedList<String> l) {
		return parseFloat(l.removeFirst());
	}

	protected PathCommand[] parsePathList(final String list) {
		final Matcher matchPathCmd = pattern.matcher(list);
		// Tokenize
		final LinkedList<String> tokens = new LinkedList<>();
		while (matchPathCmd.find()) {
			tokens.addLast(matchPathCmd.group());
		}
		final LinkedList<PathCommand> cmdList = new LinkedList<>();
		char curCmd = 'Z';
		while (tokens.size() != 0) {
			final String curToken = tokens.removeFirst();
			final char initChar = curToken.charAt(0);
			if (initChar >= 'A' && initChar <= 'Z' || initChar >= 'a' && initChar <= 'z') {
				curCmd = initChar;
			} else {
				tokens.addFirst(curToken);
			}
			PathCommand cmd = null;

			switch (curCmd) {
				case 'M':
					cmd = new MoveTo(false, next(tokens), next(tokens));
					curCmd = 'L';
					break;
				case 'm':
					cmd = new MoveTo(true, next(tokens), next(tokens));
					curCmd = 'l';
					break;
				case 'L':
					cmd = new LineTo(false, next(tokens), next(tokens));
					break;
				case 'l':
					cmd = new LineTo(true, next(tokens), next(tokens));
					break;
				case 'H':
					cmd = new Horizontal(false, next(tokens));
					break;
				case 'h':
					cmd = new Horizontal(true, next(tokens));
					break;
				case 'V':
					cmd = new Vertical(false, next(tokens));
					break;
				case 'v':
					cmd = new Vertical(true, next(tokens));
					break;
				case 'A':
					cmd = new Arc(false, next(tokens), next(tokens), next(tokens), next(tokens) == 1f,
							next(tokens) == 1f, next(tokens), next(tokens));
					break;
				case 'a':
					cmd = new Arc(true, next(tokens), next(tokens), next(tokens), next(tokens) == 1f,
							next(tokens) == 1f, next(tokens), next(tokens));
					break;
				case 'Q':
					cmd = new Quadratic(false, next(tokens), next(tokens), next(tokens), next(tokens));
					break;
				case 'q':
					cmd = new Quadratic(true, next(tokens), next(tokens), next(tokens), next(tokens));
					break;
				case 'T':
					cmd = new QuadraticSmooth(false, next(tokens), next(tokens));
					break;
				case 't':
					cmd = new QuadraticSmooth(true, next(tokens), next(tokens));
					break;
				case 'C':
					cmd = new Cubic(false, next(tokens), next(tokens), next(tokens), next(tokens), next(tokens),
							next(tokens));
					break;
				case 'c':
					cmd = new Cubic(true, next(tokens), next(tokens), next(tokens), next(tokens), next(tokens),
							next(tokens));
					break;
				case 'S':
					cmd = new CubicSmooth(false, next(tokens), next(tokens), next(tokens), next(tokens));
					break;
				case 's':
					cmd = new CubicSmooth(true, next(tokens), next(tokens), next(tokens), next(tokens));
					break;
				case 'Z':
				case 'z':
					cmd = new Terminal();
					break;
				default:
					throw new RuntimeException("Invalid path element");
			}
			cmdList.add(cmd);
		}
		final PathCommand[] retArr = new PathCommand[cmdList.size()];
		cmdList.toArray(retArr);
		return retArr;
	}

}
