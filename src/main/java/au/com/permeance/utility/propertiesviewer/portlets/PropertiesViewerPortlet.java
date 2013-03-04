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

/**
 * This is the main portlet class
 * 
 * @author Chun Ho <chun.ho@permeance.com.au>
 * 
 */
public class PropertiesViewerPortlet extends MVCPortlet {

    private static final String VIEW_PAGE = "/html/portlet/properties-viewer/view.jsp";

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

                String exportType = GetterUtil.getString(uploadRequest.getParameter(PropertiesViewerConstants.PARAM_EXPORTTYPE), "all");
                String exportSection = GetterUtil.getString(uploadRequest.getParameter(PropertiesViewerConstants.PARAM_EXPORTSECTION),
                        "system");
                String term = GetterUtil.getString(uploadRequest.getParameter(PropertiesViewerConstants.PARAM_SEARCH), StringPool.BLANK);
                boolean passwordSafe = GetterUtil.getBoolean(uploadRequest.getParameter(PropertiesViewerConstants.PARAM_PASSWORDSAFE),
                        false);

                String filename = PropertiesViewerConstants.SECTION_SYSTEM;
                Properties toOutput = PropertiesSearchUtil.createSortedProperties();
                if (PropertiesViewerConstants.SECTION_SYSTEM.equals(exportSection)) {

                    if (PropertiesViewerConstants.TYPE_ALL.equals(exportType) || term.length() == 0) {
                        toOutput = PropertiesSearchUtil.searchSystemProperties(toOutput, StringPool.BLANK);
                    } else {
                        // search
                        filename += PropertiesViewerConstants.FILE_SEARCH;
                        toOutput = PropertiesSearchUtil.searchSystemProperties(toOutput, term);
                    }
                    if (passwordSafe) {
                        filterPasswordSafe(toOutput);
                        filename += PropertiesViewerConstants.FILE_PASSWORDSAFE;
                    }
                } else if (PropertiesViewerConstants.SECTION_PORTAL.equals(exportSection)) {
                    // portal
                    filename = PropertiesViewerConstants.SECTION_PORTAL;
                    if (PropertiesViewerConstants.TYPE_ALL.equals(exportType) || term.length() == 0) {
                        toOutput = PropertiesSearchUtil.searchPortalProperties(toOutput, StringPool.BLANK);
                    } else {
                        // search
                        filename += PropertiesViewerConstants.FILE_SEARCH;
                        toOutput = PropertiesSearchUtil.searchPortalProperties(toOutput, term);
                    }
                    if (passwordSafe) {
                        filterPasswordSafe(toOutput);
                        filename += PropertiesViewerConstants.FILE_PASSWORDSAFE;
                    }
                } else if (PropertiesViewerConstants.SECTION_UPLOAD.equals(exportSection)) {
                    // uploaded
                    filename = PropertiesViewerConstants.FILE_FORMATTED;
                    File uploaded = uploadRequest.getFile(PropertiesViewerConstants.PARAM_FILE);
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
                    throw new PortletException(PropertiesViewerConstants.EXCEPTION_INVALID_OPERATION);
                }

                resourceResponse.setContentType(PropertiesViewerConstants.PROPERTIES_MIME_TYPE);
                resourceResponse.addProperty(HttpHeaders.CACHE_CONTROL, PropertiesViewerConstants.CACHE_HEADER_VALUE);
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

    public static void filterPasswordSafe(final Properties p) {
        for (Object key : p.keySet()) {
            if (key != null && key.toString().toLowerCase().endsWith(PropertiesViewerConstants.PASSWORD)) {
                p.put(key, PropertiesViewerConstants.PASSWORDVALUE);
            }
        }
    }

}
