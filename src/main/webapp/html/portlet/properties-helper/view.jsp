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
<%@page import="com.liferay.portal.kernel.util.ListUtil"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.Map" %>
<%@page import="java.util.TreeMap" %>
<%@page import="javax.portlet.PortletURL" %>
<%@ include file="init.jsp"%>


<%
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
			<aui:input name="term" value="<%=term %>" label="search-within-property-key" inlineLabel="left" inlineField="true"/>
			<input type="submit" value="<liferay-ui:message key='search' />"/>
		</aui:form>
		<br/>

		<%
		List<String> headerNames = new ArrayList<String>();

		headerNames.add("property");
		headerNames.add("value");

		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURL, headerNames, null);

		Map portalProps = new TreeMap();

		if(term.length() == 0) {		
			portalProps.putAll(PropsUtil.getProperties());
		} else {
			for(Object key: PropsUtil.getProperties().keySet()) {
			    if(key.toString().toLowerCase().contains(term)) {
			        portalProps.put(key, PropsUtil.getProperties().get(key));
			    }
			}
		}
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
			
			row.addText(StringEscapeUtils.escapeHtml(property));

			// Value

			if(value.length() > 80) {
				StringBuilder builder = new StringBuilder();
				builder.append(StringEscapeUtils.escapeHtml(StringUtil.shorten(value, 80)));
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
				row.addText(StringEscapeUtils.escapeHtml(value));
			}
			
			// Add result row

			resultRows.add(row);
		}
		%>

		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
	</c:when>
	<c:otherwise>
	
		<aui:form action="<%= renderURL.toString() %>" >
			<aui:input name="term" value="<%=term %>" label="search-within-property-key" inlineLabel="left" inlineField="true"/>
			<input type="submit" value="<liferay-ui:message key='search' />"/>
		</aui:form>
		<br/>

		<%
		List<String> headerNames = new ArrayList<String>();

		headerNames.add("property");
		headerNames.add("value");

		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURL, headerNames, null);

		Map portalProps = new TreeMap();

		if(term.length() == 0) {		
			portalProps.putAll(System.getProperties());
		} else {
			for(Object key: System.getProperties().keySet()) {
			    if(key.toString().toLowerCase().contains(term)) {
			        portalProps.put(key, System.getProperties().get(key));
			    }
			}
		}

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

			row.addText(StringEscapeUtils.escapeHtml(property));

			// Value

			if(value.length() > 80) {
				StringBuilder builder = new StringBuilder();
				builder.append(StringEscapeUtils.escapeHtml(StringUtil.shorten(value, 80)));
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
				row.addText(StringEscapeUtils.escapeHtml(value));
			}			

			// Add result row

			resultRows.add(row);
		}
		%>

		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
	</c:otherwise>
</c:choose>