<%@ page import="org.chai.kevin.cost.CostRampUp" %>
<table>
    <thead>
        <tr>
            <g:sortableColumn property="id" title="${message(code: 'costRampUp.id.label', default: 'Id')}" />
            <g:sortableColumn property="name" title="${message(code: 'costRampUp.name.label', default: 'Name')}" />
            <th><g:message code="costRampUp.years.label" default="Years" /></th>
        </tr>
    </thead>
    <tbody>
    <g:each in="${entities}" status="i" var="costRampUpInstance">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td><g:link action="show" id="${costRampUpInstance.id}">${fieldValue(bean: costRampUpInstance, field: "id")}</g:link></td>
            <td><g:i18n field="${costRampUpInstance.names}" /></td>
            <td>${fieldValue(bean: costRampUpInstance, field: "years")}</td>
        </tr>
    </g:each>
    </tbody>
</table>