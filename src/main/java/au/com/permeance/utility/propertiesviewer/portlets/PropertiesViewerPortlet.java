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
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.permission.PortletPermissionUtil;
import com.liferay.portal.theme.PortletDisplay;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

public class PropertiesViewerPortlet extends MVCPortlet {

    public static final String VIEW_PAGE = "/html/portlet/properties-helper/view.jsp";

    @Override
    public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
        include(VIEW_PAGE, renderRequest, renderResponse);
    }

    @Override
    public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException, PortletException {

        try {
            ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
            PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();
            String portletId = portletDisplay.getId();

            PermissionChecker checker = PermissionThreadLocal.getPermissionChecker();
            if (checker != null
                    && (checker.isCompanyAdmin() || checker.isOmniadmin() || PortletPermissionUtil.contains(checker, portletId,
                            ActionKeys.VIEW))) {

                String exportType = GetterUtil.getString(resourceRequest.getParameter("exportType"), "all");
                String exportSection = GetterUtil.getString(resourceRequest.getParameter("exportSection"), "system");
                String term = GetterUtil.getString(resourceRequest.getParameter("search"), StringPool.BLANK);
                boolean passwordSafe = GetterUtil.getBoolean(resourceRequest.getParameter("passwordsafe"), false);

                String filename = "system";
                Properties toOutput = new Properties() {
                    // override methods so output properties file is in sorted alphabetical list

                    @Override
                    public Set<Object> keySet() {
                        return Collections.unmodifiableSet(new TreeSet<Object>(super.keySet()));
                    }

                    @Override
                    public synchronized Enumeration<Object> keys() {
                        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
                    }
                };
                if ("system".equals(exportSection)) {

                    if ("all".equals(exportType) || term.length() == 0) {
                        toOutput.putAll(System.getProperties());
                    } else {
                        // search
                        filename += ".search";
                        for (Object key : System.getProperties().keySet()) {
                            if (key.toString().toLowerCase().contains(term)) {
                                toOutput.put(key, System.getProperty(key.toString()));
                            } else {
                                String value = System.getProperty(key.toString());
                                if (value != null && value.toLowerCase().contains(term)) {
                                    toOutput.put(key, System.getProperty(key.toString()));
                                }
                            }
                        }
                    }
                } else {
                    // portal
                    filename = "portal";
                    if ("all".equals(exportType) || term.length() == 0) {
                        toOutput.putAll(PropsUtil.getProperties());
                    } else {
                        // search
                        filename += ".search";
                        for (Object key : PropsUtil.getProperties().keySet()) {
                            if (key.toString().toLowerCase().contains(term)) {
                                toOutput.put(key, PropsUtil.getProperties().get(key));
                            } else {
                                String value = PropsUtil.get(key.toString());
                                if (value != null && value.toLowerCase().contains(term)) {
                                    toOutput.put(key, PropsUtil.getProperties().get(key));
                                }
                            }
                        }
                    }
                }

                if (passwordSafe) {
                    filterPasswordSafe(toOutput);
                    filename += ".password-safe";
                }

                resourceResponse.setContentType("text/x-java-properties");
                resourceResponse.addProperty(HttpHeaders.CACHE_CONTROL, "no-cache, no-store");
                resourceResponse.addProperty(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".properties");

                toOutput.store(resourceResponse.getWriter(), StringPool.BLANK);
            }
        } catch (IOException e) {
            throw e;
            // } catch (PortletException e) {
            // throw e;
        } catch (Exception e) {
            throw new PortletException(e);
        }

    }

    public static void filterPasswordSafe(Properties p) {
        for (Object key : p.keySet()) {
            if (key != null && key.toString().endsWith("password")) {
                p.put(key, "********");
            }
        }
    }

}
