/**
 * @(#)Base64ImageTagProcessor.java    1.0.0 15:07:14
 *
 * Idega Software hf. Source Code Licence Agreement x
 *
 * This agreement, made this 10th of February 2006 by and between
 * Idega Software hf., a business formed and operating under laws
 * of Iceland, having its principal place of business in Reykjavik,
 * Iceland, hereinafter after referred to as "Manufacturer" and Agura
 * IT hereinafter referred to as "Licensee".
 * 1.  License Grant: Upon completion of this agreement, the source
 *     code that may be made available according to the documentation for
 *     a particular software product (Software) from Manufacturer
 *     (Source Code) shall be provided to Licensee, provided that
 *     (1) funds have been received for payment of the License for Software and
 *     (2) the appropriate License has been purchased as stated in the
 *     documentation for Software. As used in this License Agreement,
 *     Licensee shall also mean the individual using or installing
 *     the source code together with any individual or entity, including
 *     but not limited to your employer, on whose behalf you are acting
 *     in using or installing the Source Code. By completing this agreement,
 *     Licensee agrees to be bound by the terms and conditions of this Source
 *     Code License Agreement. This Source Code License Agreement shall
 *     be an extension of the Software License Agreement for the associated
 *     product. No additional amendment or modification shall be made
 *     to this Agreement except in writing signed by Licensee and
 *     Manufacturer. This Agreement is effective indefinitely and once
 *     completed, cannot be terminated. Manufacturer hereby grants to
 *     Licensee a non-transferable, worldwide license during the term of
 *     this Agreement to use the Source Code for the associated product
 *     purchased. In the event the Software License Agreement to the
 *     associated product is terminated; (1) Licensee's rights to use
 *     the Source Code are revoked and (2) Licensee shall destroy all
 *     copies of the Source Code including any Source Code used in
 *     Licensee's applications.
 * 2.  License Limitations
 *     2.1 Licensee may not resell, rent, lease or distribute the
 *         Source Code alone, it shall only be distributed as a
 *         compiled component of an application.
 *     2.2 Licensee shall protect and keep secure all Source Code
 *         provided by this this Source Code License Agreement.
 *         All Source Code provided by this Agreement that is used
 *         with an application that is distributed or accessible outside
 *         Licensee's organization (including use from the Internet),
 *         must be protected to the extent that it cannot be easily
 *         extracted or decompiled.
 *     2.3 The Licensee shall not resell, rent, lease or distribute
 *         the products created from the Source Code in any way that
 *         would compete with Idega Software.
 *     2.4 Manufacturer's copyright notices may not be removed from
 *         the Source Code.
 *     2.5 All modifications on the source code by Licencee must
 *         be submitted to or provided to Manufacturer.
 * 3.  Copyright: Manufacturer's source code is copyrighted and contains
 *     proprietary information. Licensee shall not distribute or
 *     reveal the Source Code to anyone other than the software
 *     developers of Licensee's organization. Licensee may be held
 *     legally responsible for any infringement of intellectual property
 *     rights that is caused or encouraged by Licensee's failure to abide
 *     by the terms of this Agreement. Licensee may make copies of the
 *     Source Code provided the copyright and trademark notices are
 *     reproduced in their entirety on the copy. Manufacturer reserves
 *     all rights not specifically granted to Licensee.
 *
 * 4.  Warranty & Risks: Although efforts have been made to assure that the
 *     Source Code is correct, reliable, date compliant, and technically
 *     accurate, the Source Code is licensed to Licensee as is and without
 *     warranties as to performance of merchantability, fitness for a
 *     particular purpose or use, or any other warranties whether
 *     expressed or implied. Licensee's organization and all users
 *     of the source code assume all risks when using it. The manufacturers,
 *     distributors and resellers of the Source Code shall not be liable
 *     for any consequential, incidental, punitive or special damages
 *     arising out of the use of or inability to use the source code or
 *     the provision of or failure to provide support services, even if we
 *     have been advised of the possibility of such damages. In any case,
 *     the entire liability under any provision of this agreement shall be
 *     limited to the greater of the amount actually paid by Licensee for the
 *     Software or 5.00 USD. No returns will be provided for the associated
 *     License that was purchased to become eligible to receive the Source
 *     Code after Licensee receives the source code.
 */
package com.idega.block.pdf.presentation.handler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.repository.RepositoryService;
import com.idega.util.CoreConstants;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.tool.xml.Tag;
import com.itextpdf.tool.xml.WorkerContext;
import com.itextpdf.tool.xml.html.HTML;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

/**
 * <p>Parses base64 image link</p>
 * <p>You can report about problems to:
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2015 June 8
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
public class Base64ImageTagProcessor extends com.itextpdf.tool.xml.html.Image {

	private static final Logger LOGGER = java.util.logging.Logger.getLogger(Base64ImageTagProcessor.class.getName());

	protected String getImageType() {
		IWMainApplicationSettings settings = IWMainApplication
				.getDefaultIWMainApplication().getSettings();
		if (settings != null) {
			return settings.getProperty("pdf.hack.image.type", "gif");
		}

		return null;
	}

	/**
	 *
	 * @param image to convert, not <code>null</code>;
	 * @return image converted to bytes or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public byte[] getBytes(BufferedImage image) {
		byte[] imageInBytes = null;
		if (image != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write(image, getImageType(), baos);
				LOGGER.info("Image converted to byte output stream.");
			} catch (IOException e) {
				LOGGER.log(Level.WARNING,
						"Failed to copy image byte stream, cause of: ", e);
			}

			try {
				baos.flush();
				LOGGER.info("Byte stream has been flushed!");
			} catch (IOException e) {
				LOGGER.log(Level.WARNING,
						"Failed to flush stream, cause of: ", e);
			}

			imageInBytes = baos.toByteArray();

			try {
				baos.close();
				LOGGER.info("Byte stream has been closed!");
			} catch (IOException e) {
				LOGGER.log(Level.WARNING,
						"Failed to close bytes stream, cause of: ", e);
			}
		}

		return imageInBytes;
	}

	/**
	 *
	 * @param url for example www.google.com, not <code>null</code>;
	 * @return image found by link or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public BufferedImage getImage(String url) {
		InputStream stream = null;
		if (StringUtil.isEmpty(url) || url.equals("undefined")) {
			LOGGER.warning("URL ('" + url + "') is not provided");
			return null;
		}

		try {
			if (url.indexOf("../") != -1) {
				url = StringHandler.replace(url, "../", CoreConstants.EMPTY);
				if (url.startsWith("content/files/")) {
					url = CoreConstants.SLASH + url;
				}
			}

			if (url.startsWith(CoreConstants.WEBDAV_SERVLET_URI)) {
				RepositoryService repository = ELUtil.getInstance().getBean(RepositoryService.BEAN_NAME);
				stream = repository.getInputStreamAsRoot(url);
			} else {
				URL outsideUrl = new URL(url);
				outsideUrl.openConnection();
				stream = outsideUrl.openStream();
			}

			LOGGER.info("Image was prepared for reading!");
			return ImageIO.read(stream);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to get image by URL: '" + url + "' cause of: ", e);
		}

		return null;
	}

	public String getBase64Source(Tag tag) {
		if (tag != null) {
			Map<String, String> attributes = tag.getAttributes();
			if (attributes != null) {
				String source = attributes.get(HTML.Attribute.SRC);
				LOGGER.info("Image source has been given: " + source);
				if (!StringUtil.isEmpty(source) && !"undefined".equals(source)) {
					if (source.startsWith("data:image/")) {
						return source;
					}

					String encodedString = Base64.encodeBytes(getBytes(getImage(source)));
					if (!StringUtil.isEmpty(encodedString)) {
						return "data:image/" + getImageType() + ";base64," + encodedString;
					}
				}
			}
		}

		return null;
	}

	@Override
	public List<Element> end(
			WorkerContext ctx,
			Tag tag,
			List<Element> currentContent
	) {
	    List<Element> elements = new ArrayList<Element>(1);

	    String src = getBase64Source(tag);
	    if (null != src && src.length() > 0) {
	        Image img = null;
	        if (src.startsWith("data:image/")) {
	            final String base64Data = src.substring(src.indexOf(",") + 1);
	            try {
	                img = Image.getInstance(Base64.decode(base64Data));
	            } catch (Exception e) {
	                LOGGER.log(Level.WARNING, "Failed to decode image: '" + base64Data + "' cause of:", e);
	            }

	            if (img != null) {
	            	LOGGER.info("Image was successfully parsed, preparing to write to PDF");

	                try {
	                    final HtmlPipelineContext htmlPipelineContext = getHtmlPipelineContext(ctx);
	                    elements.add(getCssAppliers().apply(new Chunk((com.itextpdf.text.Image) getCssAppliers().apply(img, tag, htmlPipelineContext), 0, 0, true), tag,
	                        htmlPipelineContext));
	                } catch (Exception e) {
	                    LOGGER.log(Level.WARNING, "Failed to write image to PDF, cause of: ", e);
	                }
	            }
	        }

	        if (img == null) {
	            elements = super.end(ctx, tag, currentContent);
	        }
	    }

	    return elements;
	}
}
