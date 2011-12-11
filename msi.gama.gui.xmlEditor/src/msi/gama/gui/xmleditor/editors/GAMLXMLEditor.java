/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.xmleditor.editors;


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