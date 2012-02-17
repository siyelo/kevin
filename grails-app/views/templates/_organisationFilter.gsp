<div class="filter">
	<span class="bold"><g:message code="filter.organisation.label"/></span>
	<span class="dropdown subnav-dropdown">
		<g:if test="${currentOrganisation != null}">
			<a class="location selected" href="#" data-type="organisation" data-organisation="${currentOrganisation.id}">
				<g:i18n field="${currentOrganisation.names}"/>
			</a>
		</g:if>
		<g:else>
			<a class="location selected" href="#" data-type="organisation">
				<g:message code="filter.organisation.noselection.label" default="no location selected"/>
			</a>
		</g:else> 
		<div class="hidden dropdown-list">
			<ul>
				<g:render template="/templates/organisationTree" model="[
					controller: controllerName, 
					action: actionName,
					current: currentOrganisation,
					organisation: organisationRoot,					
					linkParams: linkParams
				]" />
			</ul>
		</div>
	</span>
</div>