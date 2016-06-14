package msi.gama.lang.gaml.ui.editbox;

import java.util.ArrayList;
import java.util.List;


public class MarkupBuilder2 extends MarkupBoxBuilder {
	int lineNr;
	
	protected void addLine(int start, int end, int offset, boolean empty) {
		Line l = new Line();
		l.nbr = lineNr;
		l.start = start;
		l.end = end;
		l.offset = offset;
		l.empty = empty;
		lineNr++;
		detectType(l);
		
		addLine(l);
	}
	
	private void detectType(Line l) {
		if (l.empty) {
			l.type = LineType.EMPTY_LINE;
			return;
		}
		String line = text.substring(l.start, l.end).trim();
		int closeOpen = countCloseOpen(line);
		if (closeOpen > 0) {
			if (line.length() > 2 && line.charAt(0) == '<' && line.charAt(1) != '/' && line.charAt(1) != '!' && line.charAt(1) != '%' && !line.endsWith("/>")) {
				l.type = LineType.OPEN_TAG;
				l.tag = getWord(line, 1);
				return;
			}
		} else if (closeOpen < 0) {
			int idx = line.lastIndexOf("</");
			if (idx > -1 && idx < line.length() - 3) {
				l.type = LineType.CLOSE_TAG;
				l.tag = getWord(line, idx + 2);
				return;
			}
		}
		l.type = LineType.TEXT;
	}

	private void addLine(Line l) {
		switch(l.type){
			case OPEN_TAG:
				openTag(l);
				emptyPrevLine = false;
				break;
			case CLOSE_TAG:
				closeTag(l);
				emptyPrevLine = false;
				break;
			case TEXT:
				freeText(l);
				closeParent(l);
				emptyPrevLine = false;
				break;
			case EMPTY_LINE:
				emptyLine(l);
				emptyPrevLine = true;
				break;
		}
	}

	private void closeParent(Line l) {
		String t = text.substring(l.start,l.end).trim();
		if (t.endsWith("/>") && !t.contains("<")){
			Box b = currentbox;
			if (isType(b, LineType.OPEN_TAG) && !isClosed(b))
				data(b).isClosed = true;
			else if (isType(b, LineType.TEXT)){
				b = currentbox.parent;
				if (isType(b, LineType.OPEN_TAG) && !isClosed(b))
					data(b).isClosed = true;
			}
		}
	}

	private void emptyLine(Line l) {
	}

	private void closeTag(Line l) {
		Box b = findClosingBox(l,currentbox);
		if (b==null) {
			freeText(l);
			return;
		}
		
		data(b).isClosed = true;
		collapseOpenBoxes(b);
		currentbox = b;
		extendbox(currentbox, l);
	}

	private void collapseOpenBoxes(Box b) {
		List<Box> children = new ArrayList<Box>(b.children());
		for (Box c : children)
			collapseOpenBoxes(c);
		
		if (data(b).type == LineType.OPEN_TAG && !data(b).isClosed && b.children().isEmpty()){
			if (b.parent != null && b.parent.data != null && !data(b.parent).isClosed && data(b.parent).type == LineType.OPEN_TAG){
				b.parent.children().remove(b);
				b.parent.hasChildren = !b.parent.children().isEmpty();
				boxes.remove(b);
			}
		}
			
	}

	private Box findClosingBox(Line l, Box box) {
		if (box == null)
			return null;
		if (isOpening(box,l.tag))
			return box;
		if (box.parent != null)
			for (Box b : box.parent.children())
				if (b!=box && isOpening(b,l.tag))
					return b;
		return findClosingBox(l, box.parent);
	}

	private boolean isOpening(Box box, String tag) {
		BoxData d = data(box);
		return d!= null && tag != null && d.type == LineType.OPEN_TAG && !d.isClosed && d.tag != null && tag.equals(d.tag);
	}

	private BoxData data(Box box) {
		return (BoxData) box.data;
	}

	private void freeText(Line l) {
		if (currentbox.offset < l.offset){
			Box b = isClosed(currentbox) ? currentbox.parent : currentbox;
			currentbox = newbox(l,b);
		}else if (currentbox.offset == l.offset && (emptyPrevLine || isType(currentbox,LineType.OPEN_TAG))){
			Box b = isClosed(currentbox) || isType(currentbox,LineType.TEXT) ? currentbox.parent : currentbox;
			currentbox = newbox(l,b);
		}else if (currentbox.offset == l.offset) {
			extendbox(currentbox,l);
		}else if (isType(currentbox,LineType.OPEN_TAG) && !isClosed(currentbox)){
			Box b = isType(currentbox,LineType.TEXT) ? currentbox.parent : currentbox; 
			currentbox = newbox(l,b);
		}else{
			currentbox = currentbox.parent;
			freeText(l);
		}
	}

	private boolean isClosed(Box b){
		return b.data != null && data(b).isClosed;
	}
	
	private boolean isType(Box b, LineType type) {
		return b != null && b.data != null && data(b).type == type;
	}

	private void openTag(Line l) {
		BoxData d = data(currentbox);
		if (d!=null && d.type == LineType.OPEN_TAG && !d.isClosed || currentbox.parent == null || currentbox.parent.data == null )
			currentbox = newbox(l,currentbox);
		else{ 
			currentbox = currentbox.parent;
			openTag(l);
		}
	}

	private void extendbox(Box box, Line l) {
		box.end = l.end;
		if (box.tabsStart < 0 && lineHasStartTab)
			box.tabsStart = l.start;
		updateEndsOffset(l, box);
		updateParentEnds(box);
	}

	protected void updateEndsOffset(Line l, Box b) {
		int n = l.end - l.start + l.offset;
		if (n >= b.maxLineLen) {
			b.maxLineLen = n;
			b.maxEndOffset = b.end;
		}
		
		int off = b.offset - l.offset;
		if (off > 0) {
			b.tabsStart = l.start;
			b.offset = l.offset;
		}
		
	}
	

	protected void updateParentEnds(Box box) {
		Box b = box.parent;
		while (b != null) {
			if (b.end < box.end)
				b.end = box.end;
			if (b.maxLineLen <= box.maxLineLen) {
				b.maxEndOffset = box.maxEndOffset;
				b.maxLineLen = box.maxLineLen;
			}
			b = b.parent;
		}

	}
	
	private Box newbox(Line l, Box parent) {
		Box b = newbox(l.start, l.end, l.offset, parent);
		if (parent!=null)
			parent.addChild(b);
		BoxData d = new BoxData();
		d.type = l.type;
		d.tag = l.tag;
		b.data = d;
		updateParentEnds(b);
		return b;
	}

	enum LineType{
		OPEN_TAG,CLOSE_TAG,TEXT,EMPTY_LINE;
	}
	
	class Line {
		int nbr;
		int start,end,offset;
		boolean empty;
		LineType type;
		String tag;
		@Override
		public String toString() {
			return text.substring(start, end);
		}
	}
	
	class BoxData {
		LineType type;
		String tag;
		boolean isClosed;
		int minOffsetLineWithTabs = Integer.MAX_VALUE;
		int startLineWithTabs = -1;
		int minOffsetLineWithNoTabs = Integer.MAX_VALUE;
		int startLineWithNoTabs = -1;
	}
}
