<g:if test="${location.collectsData()}">
	<!-- DataLocationEntity -->
	<g:if test="${currentLocationTypes != null && !currentLocationTypes.empty && currentLocationTypes.contains(location.type)}">
		<tr>
			<td>
				<span style="margin-left: ${level*20}px;"><g:i18n field="${location.names}"/></span>
			</td>
			<g:each in="${dsrTable.targets}" var="target">
				<td>
					<g:if test="${dsrTable.getReportValue(location, target) != null}">
						${dsrTable.getReportValue(location, target).value}
					</g:if>
					<g:else><g:message code="dsr.report.table.na"/></g:else>
				</td>
			</g:each>
		</tr>
	</g:if>
</g:if>
<g:else>
	<!-- LocationEntity -->
	<g:if test="${locationTree != null && !locationTree.empty && locationTree.contains(location)}">
		<tr class="tree-sign js_foldable">
			<td class="js_foldable-toggle ${location.id == currentLocation.id ? 'toggled': ''}">
				<span style="margin-left: ${level*20}px;"><g:i18n field="${location.names}"/></span>
			</td>
			<g:each in="${dsrTable.targets}" var="target">
				<td></td>
			</g:each>
		</tr>
		<tr class="sub-tree js_foldable-container hidden"
			style="display:${location.id == currentLocation.id ? 'table-row': 'none'};">
			<td class="bucket" colspan="${dsrTable.targets.size()+1}">				
				<table>
					<tbody>
						<g:each in="${location.getChildrenEntities(skipLevels, currentLocationTypes)}" var="child">	
							<g:render template="/dsr/reportProgramTableTree" model="[location:child, level:level+1]"/>
						</g:each>
					</tbody>
				</table>			
			</td>
		</tr>
	</g:if>
</g:else>