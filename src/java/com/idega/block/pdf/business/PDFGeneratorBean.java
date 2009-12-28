package com.idega.block.pdf.business;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.InputSource;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.business.DefaultSpringBean;
import com.idega.core.file.util.MimeTypeUtil;
import com.idega.event.PDFGeneratedEvent;
import com.idega.graphics.generator.business.PDFGenerator;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.servlet.filter.IWBundleResourceFilter;
import com.idega.slide.business.IWSlideService;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IOUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.xml.XmlUtil;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service(PDFGenerator.SPRING_BEAN_NAME_PDF_GENERATOR)
public class PDFGeneratorBean extends DefaultSpringBean implements PDFGenerator {

	private static final Logger LOGGER = Logger.getLogger(PDFGeneratorBean.class.getName());
	
	private ITextRenderer renderer = null;
	private XMLOutputter outputter = null;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private static final String TAG_DIV = "div";
	private static final String ATTRIBUTE_CLASS = "class";
	private static final String ATTRIBUTE_STYLE = "style";
	private static final String ATTRIBUTE_VALUE_DISPLAY_NONE = "display: none;";
	
	public PDFGeneratorBean() {
		try {
			renderer = new ITextRenderer();
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Error creating PDF generator!", e);
		}
		outputter = new XMLOutputter(Format.getPrettyFormat());
	}
	
	private boolean generatePDF(IWContext iwc, Document doc, String fileName, String uploadPath) {
		return upload(iwc, getPDFBytes(iwc, doc), fileName, uploadPath);
	}
	
	private synchronized byte[] getPDFBytes(IWContext iwc, Document doc) {
		if (renderer == null || doc == null) {
			return null;
		}
		
		uploadSourceToSlide(iwc, doc);
		
		//	Rendering PDF
		byte[] memory = null;		
		ByteArrayOutputStream os = null;
		try {
			os = new ByteArrayOutputStream();
			renderer.setDocument(doc, iwc.getServerURL());
			renderer.layout();
			renderer.createPDF(os);
			memory = os.toByteArray();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			IOUtil.close(os);
		}
		
		getApplicationContext().publishEvent(new PDFGeneratedEvent(this, doc));
		return memory;
	}
	
	private boolean upload(IWContext iwc, byte[] memory, String fileName, String uploadPath) {
		//	Checking result of rendering process
		if (memory == null || StringUtil.isEmpty(fileName) || StringUtil.isEmpty(uploadPath)) {
			return false;
		}
		
		//	Checking file name and upload path
		if (!fileName.toLowerCase().endsWith(".pdf")) {
			fileName += ".pdf";
		}
		if (!uploadPath.startsWith(CoreConstants.SLASH)) {
			uploadPath = CoreConstants.SLASH + uploadPath;
		}
		if (!uploadPath.endsWith(CoreConstants.SLASH)) {
			uploadPath = uploadPath + CoreConstants.SLASH;
		}
		
		//	Uploading PDF
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(memory);
			return getSlideService(iwc).uploadFileAndCreateFoldersFromStringAsRoot(uploadPath, fileName, is, MimeTypeUtil.MIME_TYPE_PDF_1, true);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			IOUtil.close(is);
		}
		return false;
	}
	
	public boolean generatePDF(IWContext iwc, UIComponent component, String fileName, String uploadPath, boolean replaceInputs, boolean checkCustomTags) {
		Document document = getDocumentToConvertToPDF(iwc, component, replaceInputs, checkCustomTags);
		if (document == null) {
			return false;
		}
		
		return generatePDF(iwc, document, fileName, uploadPath);
	}
	
	private void uploadSourceToSlide(IWContext iwc, Document document) {
		if (!iwc.getIWMainApplication().getSettings().getBoolean("upload_generated_pdf", Boolean.FALSE)) {
			return;
		}
		
		org.jdom.Document doc = XmlUtil.getJDOMXMLDocument(document);
		if (doc == null) {
			LOGGER.log(Level.WARNING, "Document is null!");
			return;
		}
		
		String htmlContent = getBuilderService(iwc).getCleanedHtmlContent(outputter.outputString(doc), false, false, true);
		if (StringUtil.isEmpty(htmlContent)) {
			LOGGER.log(Level.WARNING, "Document converted to HTML is empty!");
			return;
		}
		
		IWSlideService slide = getSlideService(iwc);
		if (slide == null) {
			return;
		}
		
		LOGGER.warning("Uploading HTML code for PDF... Don't do this when CSS for PDF is made!");
		try {
			slide.uploadFileAndCreateFoldersFromStringAsRoot(CoreConstants.PUBLIC_PATH + CoreConstants.SLASH, "html_for_pdf.html", htmlContent, "text/html", true);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private Document getDocumentToConvertToPDF(IWContext iwc, UIComponent component, boolean replaceInputs, boolean checkCustomTags) {
		if (component == null) {
			return null;
		}
		
		BuilderService builder = getBuilderService(iwc);
		if (builder == null) {
			return null;
		}
		
		org.jdom.Document doc = builder.getRenderedComponent(iwc, component, true, false, false);
		if (doc == null) {
			return null;
		}
		
		if (replaceInputs) {
			doc = getDocumentWithoutInputs(doc);
		}
		if (checkCustomTags) {
			doc = getDocumentWithModifiedTags(doc);
		}
		
		byte[] memory = getDocumentWithFixedMediaType(iwc, doc);
		if (memory == null) {
			return null;
		}
		
		Document document = null;
		InputStream stream = null;
		Reader reader = null;
		try {
			stream = new ByteArrayInputStream(memory);
			reader = new InputStreamReader(stream, CoreConstants.ENCODING_UTF8);
			document = XmlUtil.getDocumentBuilder().parse(new InputSource(reader));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			IOUtil.close(stream);
			IOUtil.closeReader(reader);
		}
		
		return document;
	}

	public boolean generatePDFFromComponent(String componentUUID, String fileName, String uploadPath, boolean replaceInputs, boolean checkCustomTags) {
		if (componentUUID == null) {
			return false;
		}
		
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return false;
		}
		
		BuilderService builder = getBuilderService(iwc);
		if (builder == null) {
			return false;
		}
		
		UIComponent component = builder.findComponentInPage(iwc, String.valueOf(iwc.getCurrentIBPageID()), componentUUID);
		return generatePDF(iwc, component, fileName, uploadPath, replaceInputs, checkCustomTags);
	}

	public boolean generatePDFFromPage(String pageUri, String fileName, String uploadPath, boolean replaceInputs, boolean checkCustomTags) {		
		if (pageUri == null) {
			return false;
		}
		
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return false;
		}
		
		BuilderService builder = getBuilderService(iwc);
		Page page = null;
		try {
			page = builder.getPage(builder.getPageKeyByURI(pageUri));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return generatePDF(iwc, page, fileName, uploadPath, replaceInputs, checkCustomTags);
	}
	
	private byte[] getDocumentWithFixedMediaType(IWApplicationContext iwac, org.jdom.Document document) {
		List<Element> styles = getDocumentElements("link", document);
		if (!ListUtil.isEmpty(styles)) {
			Element head = getDocumentElements("head", document).get(0);
			Element inlineStyles = null;
			StringBuffer stylesBuffer = null;
			List<Element> needless = new ArrayList<Element>();
			
			String mediaAttrName = "media";
			String mediaAttrValueAll = "all";
			String mediaAttrValuePrint = "print";
			String typeAttrName = "type";
			String hrefAttrName = "href";
			Attribute href = null;
			List<String> expectedValues = ListUtil.convertStringArrayToList(new String[] {"text/css"});
			for (Element style: styles) {
				if (doElementHasAttribute(style, typeAttrName, expectedValues)) {
					href = style.getAttribute(hrefAttrName);
					String hrefValue = href.getValue();
					
					String cssContent = null;
					InputStream streamToContent = null;
					try {
						if (hrefValue.startsWith(CoreConstants.WEBDAV_SERVLET_URI)) {
							streamToContent = getSlideService(iwac).getInputStream(hrefValue);
						} else if (hrefValue.startsWith("/idegaweb/bundles/")) {
							File file = IWBundleResourceFilter.copyResourceFromJarToWebapp(getApplication(), hrefValue);
							streamToContent = new FileInputStream(file);
						} else {
							URL url = new URL(hrefValue);
							streamToContent = url.openStream();
						}
						
						cssContent = streamToContent == null ? null : StringHandler.getContentFromInputStream(streamToContent);
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Error getting content from: " + hrefValue, e);
					} finally {
						IOUtil.close(streamToContent);
					}
					
					if (StringUtil.isEmpty(cssContent)) {
						setCustomAttribute(style, mediaAttrName, href == null ? mediaAttrValueAll : href.getValue().endsWith("pdf.css") ?
																									mediaAttrValuePrint : mediaAttrValueAll);
					} else {
						if (inlineStyles == null) {
							inlineStyles = new Element("style");
							head.addContent(inlineStyles);
						}
						if (stylesBuffer == null) {
							stylesBuffer = new StringBuffer();
						}
						
						stylesBuffer.append("\n/* Style from: ").append(hrefValue).append(" */\n").append(cssContent).append("\n");
						
						needless.add(style);
					}
				}
			}
			
			if (stylesBuffer != null && inlineStyles != null) {
				inlineStyles.setText(stylesBuffer.toString());
			}
			
			for (Iterator<Element> needlessStyles = needless.iterator(); needlessStyles.hasNext();) {
				needlessStyles.next().detach();
			}
		}
		
		String htmlContent = getBuilderService(iwac).getCleanedHtmlContent(outputter.outputString(document), false, false, true);
		
		try {
			return htmlContent.getBytes(CoreConstants.ENCODING_UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private org.jdom.Document getDocumentWithoutInputs(org.jdom.Document document) {
		List<Element> needlessElements = new ArrayList<Element>();
		
		//	<input>
		List<Element> inputs = getDocumentElements("input", document);
		String typeAttrName = "type";
		String checkedAttrName = "checked";
		String className = "replaceForInputStyle";
		String valueAttrName = "value";
		Attribute valueAttr = null;
		String value = null;
		boolean needReplace = true;
		//	Inputs we don't want to be replaced
		List<String> typeAttrValues = ListUtil.convertStringArrayToList(new String[] {"button", "image", "password", "reset", "submit"});
		List<String> textTypeValue = ListUtil.convertStringArrayToList(new String[] {"text"});
		List<String> checkedAttrValues = ListUtil.convertStringArrayToList(new String[] {"checked", Boolean.TRUE.toString(), CoreConstants.EMPTY});
		for (Element input: inputs) {
			needReplace = !doElementHasAttribute(input, typeAttrName, typeAttrValues);
			
			if (doElementHasAttribute(input, typeAttrName, Arrays.asList("hidden"))) {
				needlessElements.add(input);
			} else if (needReplace) {
				if (doElementHasAttribute(input, typeAttrName, textTypeValue)) {
					//	Text inputs
					valueAttr = input.getAttribute(valueAttrName);
					value = valueAttr == null ? null : valueAttr.getValue();
					value = StringUtil.isEmpty(value) ? CoreConstants.MINUS : value;
					input.setText(value);
					input.setName(TAG_DIV);
					setCustomAttribute(input, ATTRIBUTE_CLASS, className);
				}
				else {
					//	Radio button or check box
					if (!doElementHasAttribute(input, checkedAttrName, checkedAttrValues)) {
						//	We need to hide not selected options
						setCustomAttributeToNextElement(input, ATTRIBUTE_STYLE, ATTRIBUTE_VALUE_DISPLAY_NONE);
					}
				}
			}
		}
		
		//	Removing needless elements
		for (Iterator<Element> it = needlessElements.iterator(); it.hasNext();) {
			it.next().detach();
		}
		
		return document;
	}
	
	@SuppressWarnings("unchecked")
	private void setCustomAttributeToNextElement(Element element, String attrName, String attrValue) {
		if (element == null) {
			return;
		}
		
		Element parent = element.getParentElement();
		if (parent == null) {
			return;
		}
		
		List<Element> children = parent.getChildren();
		if (ListUtil.isEmpty(children)) {
			return;
		}
		
		Element nextElement = null;
		for (Iterator<Element> childrenIter = children.iterator(); (childrenIter.hasNext() && nextElement == null);) {
			nextElement = childrenIter.next();

			if (nextElement.equals(element) && childrenIter.hasNext()) {
				nextElement = childrenIter.next();
			}
			else {
				nextElement = null;
			}
		}
		
		if (nextElement == null) {
			return;
		}
		
		setCustomAttribute(nextElement, attrName, attrValue);
	}
	
	@SuppressWarnings("unchecked")
	private org.jdom.Document getDocumentWithModifiedTags(org.jdom.Document document) {
		List<String> expectedValues = null;
		
		List<Element> needless = new ArrayList<Element>();
		
		//	<div>
		List<Element> divs = getDocumentElements(TAG_DIV, document);
		if (!ListUtil.isEmpty(divs)) {
			expectedValues = ListUtil.convertStringArrayToList(new String[] {"deselected-case"});
			List<String> buttonAreaClassValue = ListUtil.convertStringArrayToList(new String[] {"fbc_button_area"});
			List<String> errorsClassValue = ListUtil.convertStringArrayToList(new String[] {"xformErrors"});
			List<String> displayNoneAttributeValue = ListUtil.convertStringArrayToList(new String[] {ATTRIBUTE_VALUE_DISPLAY_NONE});	
			for (Element div: divs) {
				if (doElementHasAttribute(div, ATTRIBUTE_CLASS, buttonAreaClassValue)) {
					needless.add(div);
				}
				if (doElementHasAttribute(div, ATTRIBUTE_CLASS, errorsClassValue)) {
					needless.add(div);
				}
				if (doElementHasAttribute(div, ATTRIBUTE_STYLE, displayNoneAttributeValue)) {
					needless.add(div);
				}
			}
		}
		
		//	<legend>
		List<Element> legends = getDocumentElements("legend", document);
		if (!ListUtil.isEmpty(legends)) {
			expectedValues = ListUtil.convertStringArrayToList(new String[] {"label"});
			for (Element legend: legends) {
				if (doElementHasAttribute(legend, ATTRIBUTE_CLASS, expectedValues)) {
					needless.add(legend);
				}
			}
		}
		
		//	<span>
		List<Element> spans = getDocumentElements("span", document);
		if (!ListUtil.isEmpty(spans)) {
			expectedValues = ListUtil.convertStringArrayToList(new String[] {"help-text"});
			for (Element span: spans) {
				if (doElementHasAttribute(span, ATTRIBUTE_CLASS, expectedValues)) {
					needless.add(span);
				} else if (doElementHasAttribute(span, ATTRIBUTE_CLASS, Arrays.asList("selector-prototype"))) {
					needless.add(span);
				} else if (doElementHasAttribute(span, ATTRIBUTE_CLASS, Arrays.asList("alert"))) {
					needless.add(span);
				}
			}
		}
		
		//	<textarea>
		List<Element> textareas = getDocumentElements("textarea", document);
		if (!ListUtil.isEmpty(textareas)) {
			for (Element textarea: textareas) {
				textarea.setName(TAG_DIV);
				textarea.setAttribute(new Attribute(ATTRIBUTE_CLASS, "textAreaReplacementForPDFDocument"));
				String originalText = textarea.getTextNormalize();
				if (StringUtil.isEmpty(originalText)) {
					textarea.setText(CoreConstants.MINUS);
				} else {
					String text = new StringBuilder("<p>").append(originalText).append("</p>").toString();
					while (text.indexOf("src=\"../") != -1) {
						text = StringHandler.replace(text, "../", CoreConstants.EMPTY);
					}
					org.jdom.Document textAreaContent = XmlUtil.getJDOMXMLDocument(text);
					if (textAreaContent != null) {
						try {
							List clonedContent = textAreaContent.cloneContent();
							textarea.removeContent();
							textarea.setContent(clonedContent);
						} catch (Exception e) {
							LOGGER.log(Level.WARNING, "Error setting new content for ex-textarea element: " + text, e);
							textarea.setText(originalText);
						}
					}
				}
			}
		}
		
		//	Links
		List<Element> links = getDocumentElements("a", document);
		if (!ListUtil.isEmpty(links)) {
			for (Element link: links) {
				if (doElementHasAttribute(link, "name", Arrays.asList("chibaform-head")) && ListUtil.isEmpty(link.getChildren())) {
					needless.add(link);
				} else if (doElementHasAttribute(link, ATTRIBUTE_CLASS, Arrays.asList("help-icon"))) {
					needless.add(link);
				}
			}
		}
		
		//	Scripts
		List<Element> scripts = getDocumentElements("script", document);
		if (!ListUtil.isEmpty(scripts)) {
			needless.addAll(scripts);
		}
		
		//	<iframes>
		List<Element> frames = getDocumentElements("iframe", document);
		if (!ListUtil.isEmpty(frames)) {
			for (Element frame: frames) {
				if (ListUtil.isEmpty(frame.getChildren())) {
					needless.add(frame);
				}
			}
		}

		//	<select>
		List<Element> selects = getDocumentElements("select", document);
		if (!ListUtil.isEmpty(selects)) {
			Locale locale = null;
			IWContext iwc = CoreUtil.getIWContext();
			if (iwc != null) {
				locale = iwc.getCurrentLocale();
			}
			if (locale == null) {
				locale = Locale.ENGLISH;
			}
			IWResourceBundle iwrb = null;
			try {
				iwrb = IWMainApplication.getDefaultIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER).getResourceBundle(locale);
			} catch(Exception e) {
				LOGGER.log(Level.WARNING, "Error getting resources bundle by locale: " + locale, e);
			}
			String defaultLabel = "None of the options selected";
			
			for (Element select: selects) {
				if (doElementHasAttribute(select, ATTRIBUTE_CLASS, Arrays.asList("selector-prototype"))) {
					needless.add(select);
				} else {
					Map<String, List<Element>> allOptions = getSelectOptions(select);
					if (allOptions != null && !ListUtil.isEmpty(allOptions.values())) {
						for (List<Element> options: allOptions.values()) {
							//	Getting values for selected options
							Element option = null;
							List<String> selectedOptionsValues = new ArrayList<String>();
							for (Iterator<Element> optionsIter = options.iterator(); optionsIter.hasNext();) {
								option = optionsIter.next();
								if (doElementHasAttribute(option, "selected", Arrays.asList("selected"))) {
									selectedOptionsValues.add(option.getTextNormalize());
								}
							}
							
							if (ListUtil.isEmpty(selectedOptionsValues)) {
								selectedOptionsValues.add(iwrb == null ? defaultLabel :
																		iwrb.getLocalizedString("pdf_generator.none_of_options_selected", defaultLabel));
							}
							
							select.setName(TAG_DIV);
							select.setAttribute(new Attribute(ATTRIBUTE_CLASS, "selectDropdownReplacementForPDFDocument"));
							if (doElementHasAttribute(select, ATTRIBUTE_STYLE, Arrays.asList(ATTRIBUTE_VALUE_DISPLAY_NONE))) {
								select.removeAttribute(ATTRIBUTE_STYLE);
							}
							Element list = new Element("ul");
							select.setContent(Arrays.asList(list));
							Collection<Element> listItems = new ArrayList<Element>(selectedOptionsValues.size());
							for (String value: selectedOptionsValues) {
								Element listItem = new Element("li");
								listItem.setText(value);
								listItems.add(listItem);
							}
							list.setContent(listItems);
						}
					}
				}
			}
		}
		selects = getDocumentElements("select", document);
		if (!ListUtil.isEmpty(selects)) {
			//	Removing empty selects
			needless.addAll(selects);
		}
		
		for (Iterator<Element> needlessIter = needless.iterator(); needlessIter.hasNext();) {
			needlessIter.next().detach();
		}
		
		return document;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, List<Element>> getSelectOptions(Element select) {
		List<Element> options = null;
		List<Element> optionsGroups = select.getChildren("optgroup");
		if (ListUtil.isEmpty(optionsGroups)) {
			options = select.getChildren("option");
			if (ListUtil.isEmpty(options)) {
				return null;
			}
			Map<String, List<Element>> allOptions = new HashMap<String, List<Element>>();
			allOptions.put("allOptions", options);
			return allOptions;
		}
		
		int index = 0;
		Map<String, List<Element>> groupedOptions = new HashMap<String, List<Element>>();
		for (Element optionsGroup: optionsGroups) {
			options = optionsGroup.getChildren("option");
			if (!ListUtil.isEmpty(options)) {
				groupedOptions.put(String.valueOf(index), options);
				index++;
			}
		}
		
		return groupedOptions;
	}
	
	private boolean doElementHasAttribute(Element e, String attrName, List<String> expectedValues) {
		if (e == null || attrName == null || expectedValues == null) {
			return false;
		}
		
		Attribute a = e.getAttribute(attrName);
		if (a == null) {
			return false;
		}
		
		String attrValue = a.getValue();
		if (attrValue == null) {
			return false;
		}
		
		if (expectedValues.contains(attrValue)) {
			return true;
		}
		
		for (String expectedValue: expectedValues) {
			if (attrValue.indexOf(expectedValue) != -1) {
				return true;
			}
		}
		
		return false;
	}
	
	private void setCustomAttribute(Element e, String attrName, String attrValue) {
		if (e == null || attrName == null || attrValue == null) {
			return;
		}
		
		Attribute a = e.getAttribute(attrName);
		if (a == null) {
			a = new Attribute(attrName, attrValue);
			e.setAttribute(a);
		}
		else {
			a.setValue(attrValue);
		}
	}
	
	private List<Element> getDocumentElements(String tagName, org.jdom.Document document) {
		List<Element> elements = XmlUtil.getElementsByXPath(document, tagName, XmlUtil.XHTML_NAMESPACE_ID);
		return ListUtil.isEmpty(elements) ? new ArrayList<Element>(0) : elements;
	}

	public InputStream getStreamToGeneratedPDF(IWContext iwc, UIComponent component, boolean replaceInputs, boolean checkCustomTags) {
		byte[] pdfMemory = getPDFBytes(iwc, getDocumentToConvertToPDF(iwc, component, replaceInputs, checkCustomTags));
		if (pdfMemory == null) {
			return null;
		}
		
		return new ByteArrayInputStream(pdfMemory);
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	private IWSlideService getSlideService(IWApplicationContext iwac) {
		return getServiceInstance(iwac, IWSlideService.class);
	}
	
	private BuilderService getBuilderService(IWApplicationContext iwac) {
		return getServiceInstance(iwac, BuilderService.class);
	}
}