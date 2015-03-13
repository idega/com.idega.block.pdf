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

import org.jdom2.Attribute;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.WebApplicationContextUtils;
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
import com.idega.repository.RepositoryService;
import com.idega.servlet.filter.IWBundleResourceFilter;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IOUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.xml.XmlUtil;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service(PDFGenerator.SPRING_BEAN_NAME_PDF_GENERATOR)
public class PDFGeneratorBean extends DefaultSpringBean implements PDFGenerator {

	private static final Logger LOGGER = Logger.getLogger(PDFGeneratorBean.class.getName());

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private RepositoryService repository;

	private static final String TAG_DIV = "div";
	private static final String ATTRIBUTE_CLASS = "class";
	private static final String ATTRIBUTE_STYLE = "style";
	private static final String ATTRIBUTE_VALUE_DISPLAY_NONE = "display: none!important;";

	private ITextRenderer getITextRenderer() {
		try {
			return new ITextRenderer();
		} catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Error creating PDF generator!", e);
		}
		return null;
	}

	private XMLOutputter getXMLOutputter() {
		return new XMLOutputter(XmlUtil.getPrettyFormat(false));
	}

	private boolean generatePDF(IWContext iwc, Document doc, String fileName, String uploadPath) {
		byte[] pdfBytes = getPDFBytes(doc);

		return upload(iwc, pdfBytes, fileName, uploadPath);
	}

	private byte[] getPDFBytes(Document doc) {
		if (doc == null) {
			getLogger().warning("Document is not provided");
			return null;
		}

		ITextRenderer renderer = getITextRenderer();
		if (renderer == null) {
			getLogger().warning(ITextRenderer.class.getName() + " is not available");
			return null;
		}

		uploadSourceToRepository(doc);

		//	Rendering PDF
		byte[] memory = null;
		ByteArrayOutputStream os = null;
		try {
			os = new ByteArrayOutputStream();
			renderer.setDocument(doc, getHost());
			renderer.layout();
			renderer.createPDF(os);
			renderer.finishPDF();
			memory = os.toByteArray();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error rendering:\n" + XmlUtil.getPrettyPrintedDOM(doc), e);
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
			return repository.uploadFileAndCreateFoldersFromStringAsRoot(uploadPath, fileName, is, MimeTypeUtil.MIME_TYPE_PDF_1);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			IOUtil.close(is);
		}
		return false;
	}

	@Override
	public boolean generatePDF(IWContext iwc, UIComponent component, String fileName, String uploadPath, boolean replaceInputs, boolean checkCustomTags) {
		Document document = getDocumentToConvertToPDF(iwc, component, replaceInputs, checkCustomTags);
		if (document == null) {
			return false;
		}

		return generatePDF(iwc, document, fileName, uploadPath);
	}

	private void uploadSourceToRepository(Document document) {
		if (!getApplication().getSettings().getBoolean("upload_generated_pdf", Boolean.FALSE)) {
			return;
		}

		org.jdom2.Document doc = XmlUtil.getJDOMXMLDocument(document);
		if (doc == null) {
			LOGGER.log(Level.WARNING, "Document is null!");
			return;
		}

		String htmlContent = getBuilderService().getCleanedHtmlContent(getXMLOutputter().outputString(doc), false, false, true);
		if (StringUtil.isEmpty(htmlContent)) {
			LOGGER.log(Level.WARNING, "Document converted to HTML is empty!");
			return;
		}

		LOGGER.warning("Uploading HTML code for PDF... Don't do this when CSS for PDF is made!");
		try {
			repository.uploadFileAndCreateFoldersFromStringAsRoot(
					CoreConstants.PUBLIC_PATH + CoreConstants.SLASH,
					getApplication().getSettings().getProperty("html_for_pdf_name", "html_for_pdf.html"),
					htmlContent,
					"text/html"
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Document getDocumentToConvertToPDF(IWContext iwc, UIComponent component, boolean replaceInputs, boolean checkCustomTags) {
		if (component == null)
			return null;

		BuilderService builder = getBuilderService(iwc);
		if (builder == null)
			return null;

		org.jdom2.Document doc = builder.getRenderedComponent(iwc, component, true, false, false);
		if (doc == null)
			return null;

		Map<String, PDFChanger> pdfChangers = null;
		try {
			pdfChangers = WebApplicationContextUtils.getWebApplicationContext(getApplication().getServletContext()).getBeansOfType(PDFChanger.class);
		} catch (Exception e) {}
		if (!MapUtil.isEmpty(pdfChangers))
			for (PDFChanger changer: pdfChangers.values())
				doc = changer.getChangedDocument(doc);

		if (replaceInputs)
			doc = getDocumentWithoutInputs(doc);
		if (checkCustomTags)
			doc = getDocumentWithModifiedTags(doc);

		byte[] memory = getDocumentWithFixedMediaType(doc);
		if (memory == null)
			return null;

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

	@Override
	public boolean generatePDFFromComponent(String componentUUID, String fileName, String uploadPath, boolean replaceInputs, boolean checkCustomTags) {
		if (componentUUID == null)
			return false;

		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null)
			return false;

		BuilderService builder = getBuilderService(iwc);
		if (builder == null)
			return false;

		UIComponent component = builder.findComponentInPage(iwc, String.valueOf(iwc.getCurrentIBPageID()), componentUUID);
		return generatePDF(iwc, component, fileName, uploadPath, replaceInputs, checkCustomTags);
	}

	@Override
	public boolean generatePDFFromPage(String pageUri, String fileName, String uploadPath, boolean replaceInputs, boolean checkCustomTags) {
		if (pageUri == null)
			return false;

		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null)
			return false;

		BuilderService builder = getBuilderService(iwc);
		Page page = null;
		try {
			page = builder.getPage(builder.getPageKeyByURI(pageUri));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return generatePDF(iwc, page, fileName, uploadPath, replaceInputs, checkCustomTags);
	}

	private byte[] getDocumentWithFixedMediaType(org.jdom2.Document document) {
		List<Element> linksFromHead = null;
		boolean success = true;
		while (success && !ListUtil.isEmpty(linksFromHead = getDocumentElements("link", document))) {
			success = doFixMediaType(document, linksFromHead);
		}

		String htmlContent = getBuilderService().getCleanedHtmlContent(getXMLOutputter().outputString(document), false, false, true);
		htmlContent = "<!DOCTYPE html>\n" + htmlContent;
		try {
			return htmlContent.getBytes(CoreConstants.ENCODING_UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean doFixMediaType(org.jdom2.Document document, List<Element> styles) {
		if (ListUtil.isEmpty(styles)) {
			return false;
		}

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
		boolean error = false;
		for (Iterator<Element> stylesIter = styles.iterator(); (!error && stylesIter.hasNext());) {
			Element style = stylesIter.next();
			if (!doElementHasAttribute(style, typeAttrName, expectedValues)) {
				continue;
			}

			href = style.getAttribute(hrefAttrName);
			String hrefValue = href.getValue();
			String cssContent = null;
			InputStream streamToContent = null;
			try {
				if (hrefValue.startsWith(CoreConstants.WEBDAV_SERVLET_URI)) {
					streamToContent = repository.getInputStreamAsRoot(hrefValue);
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
				error = true;
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
		if (error) {
			return false;
		}

		if (stylesBuffer != null && inlineStyles != null) {
			inlineStyles.setText(stylesBuffer.toString());
		}

		for (Iterator<Element> needlessStyles = needless.iterator(); needlessStyles.hasNext();) {
			needlessStyles.next().detach();
		}
		return true;
	}

	private org.jdom2.Document getDocumentWithoutInputs(org.jdom2.Document document) {
		List<Element> needlessElements = new ArrayList<Element>();

		//	<input>
		List<Element> inputs = getDocumentElements("input", document);
		if (ListUtil.isEmpty(inputs)) {
			return document;
		}

		String typeAttrName = "type";
		String checkedAttrName = "checked";
		String className = "replaceForInputStyle";
		String valueAttrName = "value";
		Attribute valueAttr = null;
		String value = null;
		boolean needReplace = true;

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
				} else {
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

	private List<Element> getHiddenElements(Element element, List<Element> emptyElements) {
		if (element == null)
			return emptyElements;

		List<Element> children = element.getChildren();
		Attribute style = element.getAttribute("style");
		if (style != null && "display:none;".equals(style.getValue())) {
			emptyElements.add(element);
		}

		if (!ListUtil.isEmpty(children)) {
			for (Element child: children) {
				emptyElements = getHiddenElements(child, emptyElements);
			}
		}

		return emptyElements;
	}

	private void addCommentsToEmptyElements(Element element) {
		if (element == null)
			return;

		List<Content> content = element.getContent();
		if (ListUtil.isEmpty(content)) {
			element.addContent(new Comment("idegaWeb"));
		} else {
			List<Element> children = element.getChildren();
			for (Element child: children) {
				addCommentsToEmptyElements(child);
			}
		}
	}

	private org.jdom2.Document getDocumentWithModifiedTags(org.jdom2.Document document) {
		addCommentsToEmptyElements(document.getRootElement());

		List<String> expectedValues = null;

		List<Element> needless = new ArrayList<Element>();
		if (getApplication().getSettings().getBoolean("pdf.remove_hidden_elements", Boolean.TRUE))
			needless = getHiddenElements(document.getRootElement(), needless);

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
				else if (doElementHasAttribute(span, ATTRIBUTE_CLASS, Arrays.asList("required-symbol"))) {
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
					String text = "<div>".concat(originalText).concat("</div>");
					while (text.indexOf("src=\"../") != -1) {
						text = StringHandler.replace(text, "../", CoreConstants.EMPTY);
					}
					text = StringHandler.replace(text, "<br data-mce-bogus=\"1\">", CoreConstants.EMPTY);
					text = StringHandler.replace(text, "<br mce-bogus=\"1\">", CoreConstants.EMPTY);
					text = StringHandler.replace(text, "<br mce_bogus=\"1\">", CoreConstants.EMPTY);
					while (text.indexOf("<br>") != -1) {
						text = StringHandler.replace(text, "<br>", CoreConstants.EMPTY);
					}
					org.jdom2.Document textAreaContent = XmlUtil.getJDOMXMLDocument(text);
					if (textAreaContent != null) {
						try {
							List<Content> clonedContent = textAreaContent.cloneContent();
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

	private Map<String, List<Element>> getSelectOptions(Element select) {
		List<Element> options = null;
		List<Element> optionsGroups = getElements("optgroup", select);
		if (ListUtil.isEmpty(optionsGroups)) {
			options = getElements("option", select);
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
			options = getElements("option", optionsGroup);
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

	private List<Element> getDocumentElements(String tagName, org.jdom2.Document document) {
		return getElements(tagName, document.getRootElement());
	}
	private List<Element> getElements(String tagName, Element element) {
		List<Element> elements = XmlUtil.getElementsByXPath(element, tagName, XmlUtil.XHTML_NAMESPACE_ID);
		return ListUtil.isEmpty(elements) ? new ArrayList<Element>(0) : elements;
	}

	@Override
	public InputStream getStreamToGeneratedPDF(IWContext iwc, UIComponent component, boolean replaceInputs, boolean checkCustomTags) {
		return new ByteArrayInputStream(getBytesOfGeneratedPDF(iwc, component, replaceInputs, checkCustomTags));
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	private BuilderService getBuilderService(IWApplicationContext iwac) {
		return getServiceInstance(iwac, BuilderService.class);
	}

	private BuilderService getBuilderService() {
		return getServiceInstance(BuilderService.class);
	}

	@Override
	public byte[] getBytesOfGeneratedPDF(IWContext iwc, UIComponent component, boolean replaceInputs, boolean checkCustomTags) {
		return getPDFBytes(getDocumentToConvertToPDF(iwc, component, replaceInputs, checkCustomTags));
	}
}