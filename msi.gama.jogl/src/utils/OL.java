/*
 * Simple class which create openDialog
 * sample of use: Button open = new Button("open");open.addActionListener(new OL());
 */

package utils;

import java.awt.event.*;
import javax.swing.JFileChooser;

class OL implements ActionListener {

	@Override
	public void actionPerformed(final ActionEvent e) {
		JFileChooser c = new JFileChooser();
		String dir;
		String fileName;
		int r = c.showOpenDialog(null);

		if ( r == JFileChooser.APPROVE_OPTION ) {
			fileName = c.getSelectedFile().getName();
			dir = c.getCurrentDirectory().toString();
			String file = dir + "/" + fileName;
			// here is a place for your code

		}
		if ( r == JFileChooser.CANCEL_OPTION ) {
			fileName = "Cancel";
			dir = "";

		}

	}
}
