package utils;

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class FileBrowser {

  Shell shell;
  Text text;

  public FileBrowser(Display display) {

  shell = new Shell(display);	  
  init();
  shell.pack();
  shell.setSize(350,60);
  shell.open();
 
  while (!shell.isDisposed()) {
  if (!display.readAndDispatch()) {
  display.sleep();
  }
  }
  display.dispose();
  }
  
  
  private void init() {
  shell.setText("File Browser");
  shell.setLayout(new GridLayout(2, true));
  GridData data = new GridData(GridData.FILL_BOTH);

  text = new Text(shell, SWT.NONE);
  text.setLayoutData(data);
  
  Button button = new Button(shell, SWT.PUSH);
  button.setText("Browse");
  button.addSelectionListener(new SelectionAdapter() {
  public void widgetSelected(SelectionEvent e) {
  FileDialog dialog = new FileDialog(shell, SWT.NULL);
  String path = dialog.open();
  if (path != null) {

  File file = new File(path);
  if (file.isFile())
  displayFiles(new String[] { file.toString()});
  else
  displayFiles(file.list());

  }
  }
  });  
  }
 public void displayFiles(String[] files) {
  for (int i = 0; files != null && i < files.length; i++) {
  text.setText(files[i]);
  text.setEditable(true);
  }
 }
}
