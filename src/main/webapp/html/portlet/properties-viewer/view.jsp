<%--
Copyright (C) 2013 Permeance Technologies

This program is free software: you can redistribute it and/or modify it under the terms of the
GNU General Public License as published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If
not, see <http://www.gnu.org/licenses/>.
--%>
<%@page import="com.liferay.portal.util.PortalUtil"%>
<%@page import="com.liferay.portal.kernel.dao.search.ResultRow"%>
<%@page import="com.liferay.portal.kernel.util.StringUtil"%>
<%@page import="com.liferay.portal.kernel.util.PropsUtil"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.kernel.util.ListUtil"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@page import="au.com.permeance.utility.propertiesviewer.portlets.PropertiesSearchUtil"%>
<%@page import="au.com.permeance.utility.propertiesviewer.portlets.PropertiesViewerConstants"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.Map" %>
<%@page import="java.util.TreeMap" %>
<%@page import="java.util.Properties" %>
<%@page import="javax.portlet.PortletURL" %>
<%@ include file="init.jsp"%>


<%
	ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
	pageContext.setAttribute("themeDisplay", themeDisplay); // for liferay-ui:icon-help to work

	String tabs3 = GetterUtil.getString(request.getParameter("tabs3"), "");
	String term = GetterUtil.getString(request.getParameter("term"), "").toLowerCase();
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setParameter("tabs3", tabs3);
	portletURL.setParameter("term", term);

	PortletURL renderURL = renderResponse.createRenderURL();
	renderURL.setParameter("tabs3", tabs3);
	
	int sectionId = 0;
%>

<script type="text/javascript">
function propsbrowser_showsection(sectionId) {
    document.getElementById(sectionId).style.display = "";
}
</script>

<liferay-ui:tabs
	names="system-properties,portal-properties"
	param="tabs3"
	url="<%=portletURL.toString() %>"
/>

<c:choose>
	<c:when test='<%= tabs3.equals("portal-properties") %>'>

		<aui:form action="<%= renderURL.toString() %>" >
			<aui:input name="term" value="<%=HtmlUtil.escape(term) %>" label="search" inlineLabel="left" inlineField="true"/>
			<input type="submit" value="<liferay-ui:message key='search' />"/>
		</aui:form>


		<%
		List<String> headerNames = new ArrayList<String>();

		headerNames.add("property");
		headerNames.add("value");

		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURL, headerNames, null);

		Properties portalProps = PropertiesSearchUtil.createSortedProperties();

		portalProps = PropertiesSearchUtil.searchPortalProperties(portalProps, term);		
		
		List results = ListUtil.fromCollection(portalProps.entrySet());		

		searchContainer.setTotal(results.size());

		results = ListUtil.subList(results, searchContainer.getStart(), searchContainer.getEnd());

		searchContainer.setResults(results);

		List resultRows = searchContainer.getResultRows();

		for (int i = 0; i < results.size(); i++) {
			Map.Entry entry = (Map.Entry)results.get(i);

			String property = (String)entry.getKey();
			String value = (String)entry.getValue();

			ResultRow row = new ResultRow(entry, property, i);

			// Property
			
			String propertyDisp = StringEscapeUtils.escapeHtml(property);
			if(term != null && term.length() > 0) {
			    propertyDisp = propertyDisp.replace(term, "<span style=\"background-color: yellow\">" + term + "</span>");
			}
			row.addText(propertyDisp);

			// Value

			if(value.length() > 80) {
				StringBuilder builder = new StringBuilder();
				String valueDisp = StringEscapeUtils.escapeHtml(StringUtil.shorten(value, 80));
				if(term != null && term.length() > 0) {
				    valueDisp = valueDisp.replace(term, "<span style=\"background-color: yellow\">" + term + "</span>");
				}
				builder.append(valueDisp);
				builder.append("<a href='#' onclick=\"propsbrowser_showsection('propsbrowser_section");
				builder.append(Integer.toString(sectionId));
				builder.append("'); return false;\">show</a><br/>");
				builder.append("<textarea style='display: none; overflow: auto' readonly id='propsbrowser_section");
				builder.append(Integer.toString(sectionId));
				builder.append("'>");
				builder.append(StringEscapeUtils.escapeHtml(value));
				builder.append("</textarea>");
				row.addText(builder.toString());
				sectionId++;
			} else {
				String valueDisp = StringEscapeUtils.escapeHtml(value);
				if(term != null && term.length() > 0) {
				    valueDisp = valueDisp.replace(term, "<span style=\"background-color: yellow\">" + term + "</span>");
				}
				row.addText(valueDisp);
			}
			
			// Add result row

			resultRows.add(row);
		}
		%>

		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
		<br/>
		<portlet:resourceURL var="resourceUrl" />
		
		<aui:form action="${resourceUrl}" method="post">
			<input type="hidden" name="<portlet:namespace/><%=PropertiesViewerConstants.PARAM_SEARCH %>" value="<%=HtmlUtil.escape(term) %>"/>
			<input type="hidden" name="<portlet:namespace/><%=PropertiesViewerConstants.PARAM_EXPORTSECTION %>" value="<%=PropertiesViewerConstants.SECTION_PORTAL %>"/>
			<input type="hidden" name="<portlet:namespace/><%=PropertiesViewerConstants.PARAM_EXPORTTYPE %>" id="<portlet:namespace/><%=PropertiesViewerConstants.PARAM_EXPORTTYPE %>" value="<%=PropertiesViewerConstants.TYPE_ALL %>"/>
			<b><liferay-ui:message key='export-portal-properties:' /></b> <liferay-ui:icon-help message="export-help" /> &nbsp;&nbsp;&nbsp;
			<input type="submit" onclick="document.getElementById('<portlet:namespace/><%=PropertiesViewerConstants.PARAM_EXPORTTYPE %>').value = '<%=PropertiesViewerConstants.TYPE_ALL %>';" value="<liferay-ui:message key='export-all' />"/> &nbsp;&nbsp;
			<input type="submit" onclick="document.getElementById('<portlet:namespace/><%=PropertiesViewerConstants.PARAM_EXPORTTYPE %>').value = '<%=PropertiesViewerConstants.TYPE_SEARCH %>';" value="<liferay-ui:message key='export-search' />"/> &nbsp;&nbsp;
			<input name="<portlet:namespace/><%=PropertiesViewerConstants.PARAM_PASSWORDSAFE %>" value="true" type="checkbox" />
			<liferay-ui:message key='password-safe' /> <liferay-ui:icon-help message="password-safe-message" />
		</aui:form>		

		<aui:form action="${resourceUrl}" method="post" enctype="multipart/form-data">
			<input type="hidden" name="<portlet:namespace/><%=PropertiesViewerConstants.PARAM_EXPORTSECTION %>" value="<%=PropertiesViewerConstants.SECTION_UPLOAD %>"/>
			<b><liferay-ui:message key='format-properties:' /></b> <liferay-ui:icon-help message="format-properties-help" /> &nbsp;&nbsp;&nbsp;
			<aui:input name="<%=PropertiesViewerConstants.PARAM_FILE %>" type="file" style="width: auto;" label="file" inlineLabel="left" inlineField="true" />
			<input type="submit"  value="<liferay-ui:message key='format' />"/> &nbsp;&nbsp;
		</aui:form>
				
	</c:when>
	<c:otherwise>
	
		<aui:form action="<%= renderURL.toString() %>" >
			<aui:input name="term" value="<%=HtmlUtil.escape(term) %>" label="search" inlineLabel="left" inlineField="true"/>
			<input type="submit" value="<liferay-ui:message key='search' />"/>
		</aui:form>


		<%
		List<String> headerNames = new ArrayList<String>();

		headerNames.add("property");
		headerNames.add("value");

		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURL, headerNames, null);

		Properties portalProps = PropertiesSearchUtil.createSortedProperties();

		portalProps = PropertiesSearchUtil.searchSystemProperties(portalProps, term);

		List results = ListUtil.fromCollection(portalProps.entrySet());

		searchContainer.setTotal(results.size());

		results = ListUtil.subList(results, searchContainer.getStart(), searchContainer.getEnd());

		searchContainer.setResults(results);

		List resultRows = searchContainer.getResultRows();

		for (int i = 0; i < results.size(); i++) {
			Map.Entry entry = (Map.Entry)results.get(i);

			String property = (String)entry.getKey();
			String value = (String)entry.getValue();

			ResultRow row = new ResultRow(entry, property, i);

			// Property
			String propertyDisp = StringEscapeUtils.escapeHtml(property);
			if(term != null && term.length() > 0) {
			    propertyDisp = propertyDisp.replace(term, "<span style=\"background-color: yellow\">" + term + "</span>");
			}
			row.addText(propertyDisp);

			// Value

			if(value.length() > 80) {
				StringBuilder builder = new StringBuilder();
				String valueDisp = StringEscapeUtils.escapeHtml(StringUtil.shorten(value, 80));
				if(term != null && term.length() > 0) {
				    valueDisp = valueDisp.replace(term, "<span style=\"background-color: yellow\">" + term + "</span>");
				}
				builder.append(valueDisp);
				builder.append("<a href='#' onclick=\"propsbrowser_showsection('propsbrowser_section");
				builder.append(Integer.toString(sectionId));
				builder.append("'); return false;\">more</a><br/>");
				builder.append("<textarea style='display: none; overflow: auto; width: 100%;' readonly id='propsbrowser_section");
				builder.append(Integer.toString(sectionId));
				builder.append("'>");
				builder.append(StringEscapeUtils.escapeHtml(value));
				builder.append("</textarea>");
				row.addText(builder.toString());
				sectionId++;
			} else {
				String valueDisp = StringEscapeUtils.escapeHtml(value);
				if(term != null && term.length() > 0) {
				    valueDisp = valueDisp.replace(term, "<span style=\"background-color: yellow\">" + term + "</span>");
				}
				row.addText(valueDisp);
			}			

			// Add result row

			resultRows.add(row);
		}
		%>

		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
		<br/>
		<portlet:resourceURL var="resourceUrl" />
		
		<aui:form action="${resourceUrl}" method="post">
			<input type="hidden" name="<portlet:namespace/><%=PropertiesViewerConstants.PARAM_SEARCH %>" value="<%=HtmlUtil.escape(term) %>"/>
			<input type="hidden" name="<portlet:namespace/><%=PropertiesViewerConstants.PARAM_EXPORTSECTION %>" value="<%=PropertiesViewerConstants.SECTION_SYSTEM %>"/>
			<input type="hidden" name="<portlet:namespace/><%=PropertiesViewerConstants.PARAM_EXPORTTYPE %>" id="<portlet:namespace/><%=PropertiesViewerConstants.PARAM_EXPORTTYPE %>" value="<%=PropertiesViewerConstants.TYPE_ALL %>"/>
			<b><liferay-ui:message key='export-system-properties:' /></b> <liferay-ui:icon-help message="export-help" /> &nbsp;&nbsp;&nbsp;
			<input type="submit" onclick="document.getElementById('<portlet:namespace/><%=PropertiesViewerConstants.PARAM_EXPORTTYPE %>').value = '<%=PropertiesViewerConstants.TYPE_ALL %>';" value="<liferay-ui:message key='export-all' />"/> &nbsp;&nbsp;
			<input type="submit" onclick="document.getElementById('<portlet:namespace/><%=PropertiesViewerConstants.PARAM_EXPORTTYPE %>').value = '<%=PropertiesViewerConstants.TYPE_SEARCH %>';" value="<liferay-ui:message key='export-search' />"/> &nbsp;&nbsp;
			<input name="<portlet:namespace/><%=PropertiesViewerConstants.PARAM_PASSWORDSAFE %>" value="true" type="checkbox" />
			<liferay-ui:message key='password-safe' /> <liferay-ui:icon-help message="password-safe-message" />
		</aui:form>		

		<aui:form action="${resourceUrl}" method="post" enctype="multipart/form-data">
			<input type="hidden" name="<portlet:namespace/><%=PropertiesViewerConstants.PARAM_EXPORTSECTION %>" value="<%=PropertiesViewerConstants.SECTION_UPLOAD %>"/>
			<b><liferay-ui:message key='format-properties:' /></b> <liferay-ui:icon-help message="format-properties-help" /> &nbsp;&nbsp;&nbsp;
			<aui:input name="<%=PropertiesViewerConstants.PARAM_FILE %>" type="file" style="width: auto;" label="file" inlineLabel="left" inlineField="true" />
			<input type="submit"  value="<liferay-ui:message key='format' />"/> &nbsp;&nbsp;
		</aui:form>
				
	</c:otherwise>
</c:choose>