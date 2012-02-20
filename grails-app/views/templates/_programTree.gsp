<r:require module="foldable" />

<li class="${current?.id == objective?.id ? 'current':''} foldable">
	
	<% def programLinkParams = new HashMap(linkParams) %>
	<% programLinkParams.remove("dashboardEntity") %>
	<% programLinkParams['objective'] = objective.id+"" %>

	<g:if test="${objective.children == null || objective.children.empty}">
		<a class="dropdown-link parameter" data-type="objective"
			data-location="${objective.id}"
			href="${createLinkByFilter(controller:controller, action:action, params:programLinkParams)}">
			<g:i18n field="${objective.names}"/> </a>		
	</g:if>
	<g:else>
		<a class="foldable-toggle" href="#">(toggle)</a>
		<a class="dropdown-link js_dropdown-link parameter" data-type="objective"
			data-location="${objective.id}"
			href="${createLinkByFilter(controller:controller, action:action, params:programLinkParams)}">
			<g:i18n field="${objective.names}"/> </a>
		<ul class="location-fold" id="location-fold-${objective.id}">
			<g:each in="${objective.children}" var="child">
				<g:render template="/templates/programTree"
					model="[controller: controller, 
					action: action,
					current: current, 
					objective: child,
					linkParams:linkParams]" />
			</g:each>
		</ul>
	</g:else>	
</li>
