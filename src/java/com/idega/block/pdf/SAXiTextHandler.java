/*
 * Created on May 31, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.block.pdf;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author aron 
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Stack;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import com.lowagie.text.*;
/**
 * The <CODE>Tags</CODE>-class maps several XHTML-tags to iText-objects.
 */
public class SAXiTextHandler extends DefaultHandler {
    
/** This is the resulting document. */
	protected DocListener document;
    
/** This is a <CODE>Stack</CODE> of objects, waiting to be added to the document. */
	protected Stack stack;
    
/** Counts the number of chapters in this document. */
	protected int chapters = 0;
    
/** This is the current chunk to which characters can be added. */
	protected Chunk currentChunk = null;
    
/** This is the current chunk to which characters can be added. */
	protected boolean ignore = false;
/** This is a flag that can be set, if you want to open and close the Document-object yourself. */
	protected boolean controlOpenClose = true;
/**
 * Constructs a new SAXiTextHandler that will translate all the events
 * triggered by the parser to actions on the <CODE>Document</CODE>-object.
 *
 * @paramdocumentthis is the document on which events must be triggered
 */
    
	public SAXiTextHandler(DocListener document) {
		super();
		this.document = document;
		this.stack = new Stack();
	}
/**
 * Sets the parameter that allows you to enable/disable the control over the Document.open() and Document.close() method.
 * <P>
 * If you set this parameter to true (= default), the parser will open the Document object when the start-root-tag is encounterd
 * and close it when the end-root-tag is met. If you set it to false, you have to open and close the Document object
 * yourself.
 *
 * @param   controlOpenClose    set this to false if you plan to open/close the Document yourself
 */
	public void setControlOpenClose(boolean controlOpenClose) {
		this.controlOpenClose = controlOpenClose;
	}
/**
 * This method gets called when a start tag is encountered.
 *
 * @paramnamethe name of the tag that is encountered
 * @paramattrsthe list of attributes
 */
    
	public void startElement(String uri, String lname, String name, Attributes attrs) {
        
		Properties attributes = new Properties();
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				String attribute = attrs.getQName(i);
				attributes.setProperty(attribute, attrs.getValue(i));
			}
		}
		handleStartingTags(name, attributes);
	}
    
/**
 * This method deals with the starting tags.
 *
 * @param       name        the name of the tag
 * @param       attributes  the list of attributes
 */
    
	public void handleStartingTags(String name, Properties attributes) {
		//System.err.println("Start: " + name);
		if (this.ignore || ElementTags.IGNORE.equals(name)) {
			this.ignore = true;
			return;
		}
        
		// maybe there is some meaningful data that wasn't between tags
		if (this.currentChunk != null) {
			TextElementArray current;
			try {
				current = (TextElementArray) this.stack.pop();
			}
			catch(EmptyStackException ese) {
				current = new Paragraph();
			}
			current.add(this.currentChunk);
			this.stack.push(current);
			this.currentChunk = null;
		}
        
		// chunks
		if (Chunk.isTag(name)) {
			this.currentChunk = new Chunk(attributes);
			return;
		}
        
		// symbols
		if (Entities.isTag(name)) {
			Font f = new Font();
			if (this.currentChunk != null) {
				handleEndingTags(ElementTags.CHUNK);
				f = this.currentChunk.font();
			}
			this.currentChunk = Entities.get(attributes.getProperty(ElementTags.ID), f);
			return;
		}
		// phrases
		if (Phrase.isTag(name)) {
			this.stack.push(new Phrase(attributes));
			return;
		}
        
		// anchors
		if (Anchor.isTag(name)) {
			this.stack.push(new Anchor(attributes));
			return;
		}
        
		// paragraphs and titles
		if (Paragraph.isTag(name) || Section.isTitle(name)) {
			this.stack.push(new Paragraph(attributes));
			return;
		}
        
		// lists
		if (List.isTag(name)) {
			this.stack.push(new List(attributes));
			return;
		}
        
		// listitems
		if (ListItem.isTag(name)) {
			this.stack.push(new ListItem(attributes));
			return;
		}
        
		// cells
		if (Cell.isTag(name)) {
			this.stack.push(new Cell(attributes));
			return;
		}
        
		// tables
		if (Table.isTag(name)) {
			Table table = new Table(attributes);
			float widths[] = table.getProportionalWidths();
			for (int i = 0; i < widths.length; i++) {
				if (widths[i] == 0) {
					widths[i] = 100.0f / widths.length;
				}
			}
			try {
				table.setWidths(widths);
			}
			catch(BadElementException bee) {
				// this shouldn't happen
				throw new ExceptionConverter(bee);
			}
			this.stack.push(table);
			return;
		}
        
		// sections
		if (Section.isTag(name)) {
			Element previous = (Element) this.stack.pop();
			Section section;
			try {
				section = ((Section)previous).addSection(attributes);
			}
			catch(ClassCastException cce) {
				throw new ExceptionConverter(cce);
			}
			this.stack.push(previous);
			this.stack.push(section);
			return;
		}
        
		// chapters
		if (Chapter.isTag(name)) {
			String value; // changed after a suggestion by Serge S. Vasiljev
			if ((value = (String)attributes.remove(ElementTags.NUMBER)) != null){
				 this.chapters = Integer.parseInt(value);
			 }
			else {
			   this.chapters++;
			}
			Chapter chapter = new Chapter(attributes,this.chapters);
			this.stack.push(chapter);
			return;
		}
        
		// images
		if (Image.isTag(name)) {
			try {
				Image img = Image.getInstance(attributes);
				Object current;
				try {
					// if there is an element on the stack...
					current = this.stack.pop();
					// ...and it's a Chapter or a Section, the Image can be added directly
					if (current instanceof Chapter || current instanceof Section || current instanceof Cell) {
						((TextElementArray)current).add(img);
						this.stack.push(current);
						return;
					}
					// ...if not, the Image is wrapped in a Chunk before it's added
					else {
						Stack newStack = new Stack();
						try {
							while (! (current instanceof Chapter || current instanceof Section || current instanceof Cell)) {
								newStack.push(current);
								if (current instanceof Anchor) {
									img.setAnnotation(new Annotation(0, 0, 0, 0, ((Anchor)current).reference()));
								}
								current = this.stack.pop();
							}
							((TextElementArray)current).add(img);
							this.stack.push(current);
						}
						catch(EmptyStackException ese) {
							this.document.add(img);
						}
						while (!newStack.empty()) {
							this.stack.push(newStack.pop());
						}
						return;
					}
				}
				catch(EmptyStackException ese) {
					// if there is no element on the stack, the Image is added to the document
					try {
						this.document.add(img);
					}
					catch(DocumentException de) {
						throw new ExceptionConverter(de);
					}
					return;
				}
			}
			catch(Exception e) {
				throw new ExceptionConverter(e);
			}
		}
        
		// annotations
		if (Annotation.isTag(name)) {
			Annotation annotation = new Annotation(attributes);
			TextElementArray current;
			try {
				try {
					current = (TextElementArray) this.stack.pop();
					try {
						current.add(annotation);
					}
					catch(Exception e) {
						this.document.add(annotation);
					}
					this.stack.push(current);
				}
				catch(EmptyStackException ese) {
					this.document.add(annotation);
				}
				return;
			}
			catch(DocumentException de) {
				throw new ExceptionConverter(de);
			}
		}
        
		// newlines
		if (isNewline(name)) {
			TextElementArray current;
			try {
				current = (TextElementArray) this.stack.pop();
				current.add(Chunk.NEWLINE);
				this.stack.push(current);
			}
			catch(EmptyStackException ese) {
				if (this.currentChunk == null) {
					try {
						this.document.add(Chunk.NEWLINE);
					}
					catch(DocumentException de) {
						throw new ExceptionConverter(de);
					}
				}
				else {
					this.currentChunk.append("\n");
				}
			}
			return;
		}
        
		// newpage
		if (isNewpage(name)) {
			TextElementArray current;
			try {
				current = (TextElementArray) this.stack.pop();
				Chunk newPage = new Chunk("");
				newPage.setNewPage();
				current.add(newPage);
				this.stack.push(current);
			}
			catch(EmptyStackException ese) {
				try {
					this.document.newPage();
				}
				catch(DocumentException de) {
					throw new ExceptionConverter(de);
				}
			}
			return;
		}
        
		// newpage
		if (ElementTags.HORIZONTALRULE.equals(name)) {
			TextElementArray current;
			Graphic hr = new Graphic();
			hr.setHorizontalLine(1.0f, 100.0f);
			try {
				current = (TextElementArray) this.stack.pop();
				current.add(hr);
				this.stack.push(current);
			}
			catch(EmptyStackException ese) {
				try {
					this.document.add(hr);
				}
				catch(DocumentException de) {
					throw new ExceptionConverter(de);
				}
			}
			return;
		}
        
		// documentroot
		if (isDocumentRoot(name)) {
			String key;
			String value;
			for (Iterator i = attributes.keySet().iterator(); i.hasNext(); ) {
				key = (String) i.next();
				value = attributes.getProperty(key);
				try {
					this.document.add(new Meta(key, value));
				}
				catch(DocumentException de) {
					throw new ExceptionConverter(de);
				}
			}
			if (this.controlOpenClose) {
				this.document.open();
			}
		}
	}
    
/**
 * This method gets called when ignorable white space encountered.
 *
 * @paramchan array of characters
 * @paramstartthe start position in the array
 * @paramlengththe number of characters to read from the array
 */
    
	public void ignorableWhitespace(char[] ch, int start, int length) {
		// do nothing: we handle white space ourselves in the characters method
	}
    
/**
 * This method gets called when characters are encountered.
 *
 * @paramchan array of characters
 * @paramstartthe start position in the array
 * @paramlengththe number of characters to read from the array
 */
    
	public void characters(char[] ch, int start, int length) {
        
		if (this.ignore) {
			return;
		}
        
		String content = new String(ch, start, length);
		//System.err.println("'" + content + "'");
        
		if (content.trim().length() == 0) {
			return;
		}
        
		StringBuffer buf = new StringBuffer();
		int len = content.length();
		char character;
		boolean newline = false;
		for (int i = 0; i < len; i++) {
			switch(character = content.charAt(i)) {
				case ' ':
					if (!newline) {
						buf.append(character);
					}
					break;
				case '\n':
					if (i > 0) {
						newline = true;
						buf.append(' ');
					}
					break;
				case '\r':
					break;
				case '\t':
					break;
					default:
						newline = false;
						buf.append(character);
			}
		}
		if (this.currentChunk == null) {
			this.currentChunk = new Chunk(buf.toString());
		}
		else {
			this.currentChunk.append(buf.toString());
		}
	}
    
/**
 * This method gets called when an end tag is encountered.
 *
 * @paramnamethe name of the tag that ends
 */
    
	public void endElement(String uri, String lname, String name) {
		handleEndingTags(name);
	}
    
/**
 * This method deals with the starting tags.
 *
 * @param       name        the name of the tag
 */
    
	public void handleEndingTags(String name) {
        
		//System.err.println("Stop: " + name);
        
		if (ElementTags.IGNORE.equals(name)) {
			this.ignore = false;
			return;
		}
		if (this.ignore) {
			return;
		}
		// tags that don't have any content
		if (isNewpage(name) || Annotation.isTag(name) || Image.isTag(name) || isNewline(name)) {
			return;
		}
        
		try {
			// titles of sections and chapters
			if (Section.isTitle(name)) {
				Paragraph current = (Paragraph) this.stack.pop();
				if (this.currentChunk != null) {
					current.add(this.currentChunk);
					this.currentChunk = null;
				}
				Section previous = (Section) this.stack.pop();
				previous.setTitle(current);
				this.stack.push(previous);
				return;
			}
            
			// all other endtags
			if (this.currentChunk != null) {
				TextElementArray current;
				try {
					current = (TextElementArray) this.stack.pop();
				}
				catch(EmptyStackException ese) {
					current = new Paragraph();
				}
				current.add(this.currentChunk);
				this.stack.push(current);
				this.currentChunk = null;
			}
            
			// chunks
			if (Chunk.isTag(name)) {
				return;
			}
            
			// phrases, anchors, lists, tables
			if (Phrase.isTag(name) || Anchor.isTag(name) || List.isTag(name) || Paragraph.isTag(name)) {
				Element current = (Element) this.stack.pop();
				try {
					TextElementArray previous = (TextElementArray) this.stack.pop();
					previous.add(current);
					this.stack.push(previous);
				}
				catch(EmptyStackException ese) {
					this.document.add(current);
				}
				return;
			}
            
			// listitems
			if (ListItem.isTag(name)) {
				ListItem listItem = (ListItem) this.stack.pop();
				List list = (List) this.stack.pop();
				list.add(listItem);
				this.stack.push(list);
			}
            
			// tables
			if (Table.isTag(name)) {
				Table table = (Table) this.stack.pop();           
				try {
					TextElementArray previous = (TextElementArray) this.stack.pop(); 
					previous.add(table);
					this.stack.push(previous);
				}
				catch(EmptyStackException ese) {
					this.document.add(table);
				}
				return;
			}
            
			// rows
			if (Row.isTag(name)) {
				ArrayList cells = new ArrayList();
				int columns = 0;
				Table table;
				Cell cell;
				while (true) {
					Element element = (Element) this.stack.pop();
					if (element.type() == Element.CELL) {
						cell = (Cell) element;
						columns += cell.colspan();
						cells.add(cell);
					}
					else {
						table = (Table) element;
						break;
					}
				}
				if (table.columns() < columns) {
					table.addColumns(columns - table.columns());
				}
				Collections.reverse(cells);
				String width;
				float[] cellWidths = new float[columns];
				boolean[] cellNulls = new boolean[columns];
				for (int i = 0; i < columns; i++) {
					cellWidths[i] = 0;
					cellNulls[i] = true;
				}
				float total = 0;
				int j = 0;
				for (Iterator i = cells.iterator(); i.hasNext(); ) {
					cell = (Cell) i.next();
					if ((width = cell.cellWidth()) == null) {
						if (cell.colspan() == 1 && cellWidths[j] == 0) {
							try {
								cellWidths[j] = 100f / columns;
								total += cellWidths[j];
							}
							catch(Exception e) {
								// empty on purpose
							}
						}
						else if (cell.colspan() == 1) {
							cellNulls[j] = false;
						}
					}
					else if (cell.colspan() == 1 && width.endsWith("%")) {
						try {
							cellWidths[j] = Float.valueOf(width.substring(0, width.length() - 1) + "f").floatValue();
							total += cellWidths[j];
						}
						catch(Exception e) {
							// empty on purpose
						}
					}
					j += cell.colspan();
					table.addCell(cell);
				}
				float widths[] = table.getProportionalWidths();
				if (widths.length == columns) {
					float left = 0.0f;
					for (int i = 0; i < columns; i++) {
						if (cellNulls[i] && widths[i] != 0) {
							left += widths[i];
							cellWidths[i] = widths[i];
						}
					}
					if (100.0 >= total) {
						for (int i = 0; i < widths.length; i++) {
							if (cellWidths[i] == 0 && widths[i] != 0) {
								cellWidths[i] = (widths[i] / left) * (100.0f - total);
							}
						}
					}
					table.setWidths(cellWidths);
				}
				this.stack.push(table);
			}
            
			// cells
			if (Cell.isTag(name)) {
				return;
			}
            
			// sections
			if (Section.isTag(name)) {
				this.stack.pop();
				return;
			}
            
			// chapters
			if (Chapter.isTag(name)) {
				this.document.add((Element) this.stack.pop());
				return;
			}
            
			// the documentroot
			if (isDocumentRoot(name)) {
				try {
					while (true) {
						Element element = (Element) this.stack.pop();
						try {
							TextElementArray previous = (TextElementArray) this.stack.pop();
							previous.add(element);
							this.stack.push(previous);
						}
						catch(EmptyStackException es) {
							this.document.add(element);
						}
					}
				}
				catch(EmptyStackException ese) {
					// empty on purpose
				}
				if (this.controlOpenClose) {
					this.document.close();
				}
				return;
			}
		}
		catch(DocumentException de) {
			throw new ExceptionConverter(de);
		}
	}
    
/**
 * Checks if a certain tag corresponds with the newpage-tag.
 *
 * @paramtaga presumed tagname
 * @return<CODE>true</CODE> or <CODE>false</CODE>
 */
    
	private boolean isNewpage(String tag) {
		return ElementTags.NEWPAGE.equals(tag);
	}
    
/**
 * Checks if a certain tag corresponds with the newpage-tag.
 *
 * @paramtaga presumed tagname
 * @return<CODE>true</CODE> or <CODE>false</CODE>
 */
    
	private boolean isNewline(String tag) {
		return ElementTags.NEWLINE.equals(tag);
	}
    
/**
 * Checks if a certain tag corresponds with the roottag.
 *
 * @paramtaga presumed tagname
 * @return<CODE>true</CODE> if <VAR>tag</VAR> equals <CODE>itext</CODE>, <CODE>false</CODE> otherwise.
 */
    
	protected boolean isDocumentRoot(String tag) {
		return ElementTags.ITEXT.equals(tag);
	}
}

