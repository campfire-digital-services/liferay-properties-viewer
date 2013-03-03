/**
 * Copyright (C) 2013 Permeance Technologies
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package au.com.permeance.utility.propertiesviewer.portlets;

import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.permission.PortletPermissionUtil;
import com.liferay.portal.theme.PortletDisplay;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

public class PropertiesViewerPortlet extends MVCPortlet {

    private static final String VIEW_PAGE = "/html/portlet/properties-helper/view.jsp";

    public static final String PARAM_EXPORTTYPE = "exportType";
    public static final String PARAM_EXPORTSECTION = "exportSection";
    public static final String PARAM_SEARCH = "search";
    public static final String PARAM_PASSWORDSAFE = "passwordsafe";
    public static final String PARAM_FILE = "file";

    public static final String SECTION_SYSTEM = "system";
    public static final String SECTION_PORTAL = "portal";
    public static final String SECTION_UPLOAD = "upload";

    public static final String TYPE_ALL = "all";
    public static final String TYPE_SEARCH = "search";

    private static final String FILE_FORMATTED = "formatted";
    private static final String FILE_SEARCH = ".search";
    private static final String FILE_PASSWORDSAFE = ".passwordsafe";

    private static final String EXCEPTION_INVALID_OPERATION = "Invalid Operation";
    private static final String PROPERTIES_MIME_TYPE = "text/x-java-properties";
    private static final String CACHE_HEADER_VALUE = "no-cache, no-store";

    @Override
    public void doView(final RenderRequest renderRequest, final RenderResponse renderResponse) throws IOException, PortletException {
        include(VIEW_PAGE, renderRequest, renderResponse);
    }

    @Override
    public void serveResource(final ResourceRequest resourceRequest, final ResourceResponse resourceResponse) throws IOException,
            PortletException {

        try {
            ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
            PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();
            String portletId = portletDisplay.getId();

            PermissionChecker checker = PermissionThreadLocal.getPermissionChecker();
            if (checker != null
                    && (checker.isCompanyAdmin() || checker.isOmniadmin() || PortletPermissionUtil.contains(checker, portletId,
                            ActionKeys.VIEW))) {

                UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(resourceRequest);

                String exportType = GetterUtil.getString(uploadRequest.getParameter(PARAM_EXPORTTYPE), "all");
                String exportSection = GetterUtil.getString(uploadRequest.getParameter(PARAM_EXPORTSECTION), "system");
                String term = GetterUtil.getString(uploadRequest.getParameter(PARAM_SEARCH), StringPool.BLANK);
                boolean passwordSafe = GetterUtil.getBoolean(uploadRequest.getParameter(PARAM_PASSWORDSAFE), false);

                String filename = SECTION_SYSTEM;
                Properties toOutput = PropertiesSearchUtil.createSortedProperties();
                if (SECTION_SYSTEM.equals(exportSection)) {

                    if (TYPE_ALL.equals(exportType) || term.length() == 0) {
                        toOutput = PropertiesSearchUtil.searchSystemProperties(toOutput, StringPool.BLANK);
                    } else {
                        // search
                        filename += FILE_SEARCH;
                        toOutput = PropertiesSearchUtil.searchSystemProperties(toOutput, term);
                    }
                    if (passwordSafe) {
                        filterPasswordSafe(toOutput);
                        filename += FILE_PASSWORDSAFE;
                    }
                } else if (SECTION_PORTAL.equals(exportSection)) {
                    // portal
                    filename = SECTION_PORTAL;
                    if (TYPE_ALL.equals(exportType) || term.length() == 0) {
                        toOutput = PropertiesSearchUtil.searchPortalProperties(toOutput, StringPool.BLANK);
                    } else {
                        // search
                        filename += FILE_SEARCH;
                        toOutput = PropertiesSearchUtil.searchPortalProperties(toOutput, term);
                    }
                    if (passwordSafe) {
                        filterPasswordSafe(toOutput);
                        filename += FILE_PASSWORDSAFE;
                    }
                } else if (SECTION_UPLOAD.equals(exportSection)) {
                    // uploaded
                    filename = FILE_FORMATTED;
                    File uploaded = uploadRequest.getFile(PARAM_FILE);
                    if (uploaded != null) {
                        FileInputStream fis = new FileInputStream(uploaded);
                        try {
                            toOutput.load(fis);
                        } finally {
                            try {
                                if (fis != null) {
                                    fis.close();
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                } else {
                    throw new PortletException(EXCEPTION_INVALID_OPERATION);
                }

                resourceResponse.setContentType(PROPERTIES_MIME_TYPE);
                resourceResponse.addProperty(HttpHeaders.CACHE_CONTROL, CACHE_HEADER_VALUE);
                resourceResponse.addProperty(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".properties");

                toOutput.store(resourceResponse.getPortletOutputStream(), StringPool.BLANK);
            }
        } catch (IOException e) {
            throw e;
        } catch (PortletException e) {
            throw e;
        } catch (Exception e) {
            throw new PortletException(e);
        }
    }

    private static final String PASSWORD = "password";
    private static final String PASSWORDVALUE = "********";

    public static void filterPasswordSafe(final Properties p) {
        for (Object key : p.keySet()) {
            if (key != null && key.toString().toLowerCase().endsWith(PASSWORD)) {
                p.put(key, PASSWORDVALUE);
            }
        }
    }

}
