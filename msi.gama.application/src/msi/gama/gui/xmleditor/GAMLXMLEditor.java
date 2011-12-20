/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.xmleditor;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;
import org.eclipse.wst.sse.ui.StructuredTextEditor;


@SuppressWarnings("restriction")
public class GAMLXMLEditor extends XMLMultiPageEditorPart {
	
	public void highlightLine(final int lineNumber) throws BadLocationException {
		IDocument doc = (IDocument) getAdapter(IDocument.class);
		StructuredTextEditor editor =
				(StructuredTextEditor) getAdapter(ITextEditor.class);
		IDocumentProvider provider = editor.getDocumentProvider();
		IAnnotationModelExtension model =
				(IAnnotationModelExtension) provider.getAnnotationModel(editor.getEditorInput());
		IRegion r = doc.getLineInformation(lineNumber - 1);
		Annotation a =
				new Annotation("msi.gama.annotation.error", false,
						"");

		((IAnnotationModel) model).addAnnotation(a, new Position(r.getOffset(),
				r.getLength()));
	}

	public void refresh() {
		StructuredTextEditor editor =
				(StructuredTextEditor) getAdapter(ITextEditor.class);
		IDocumentProvider provider = editor.getDocumentProvider();
		IAnnotationModelExtension model =
				(IAnnotationModelExtension) provider.getAnnotationModel(editor.getEditorInput());
		model.removeAllAnnotations();
		setActivePage(1);
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		refresh();
		super.doSave(monitor);
	}
}