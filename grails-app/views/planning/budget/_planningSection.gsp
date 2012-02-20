<div class="budget-edit">
	<div class="diff-title">
		<h5>
			<g:value value="${planningEntry.discriminatorValue}" type="${planningType.discriminatorType}" enums="${planningEntry.enums}"/>
		</h5>
		<h6>
			<g:i18n field="${planningType.headers[section]}"/>
		</h6>
	</div>
	
	<g:form url="[controller:'planning', action:'save', params: [location: location.id, planningType: planningType.id]]">
		<input class="js_always-send" type="hidden" name="lineNumber" value="${planningEntry.lineNumber}"/>
		<input class="js_always-send" type="hidden" name="planningType" value="${planningType.id}"/>
		
		<g:render template="/survey/element/${planningType.getType(section).type.name().toLowerCase()}"  model="[
			value: planningEntry.getValue(section),
			lastValue: null,
			type: planningType.getType(section), 
			suffix: planningEntry.getPrefix(section),
			headerSuffix: section,
			
			// get rid of those in the templates??
			element: planningType,
			validatable: planningEntry.validatable,
			
			readonly: readonly,
			enums: planningEntry.enums
		]"/>
	
		<a class="next gray medium" href="${createLink(controller:'planning', action:'updateBudget', params:[location: location.id, planningType: planningType.id])}">Update budget</a>
	</g:form>
</div>