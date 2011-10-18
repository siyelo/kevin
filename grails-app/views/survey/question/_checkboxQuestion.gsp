<g:set var="organisationUnitGroup" value="${surveyPage.organisation.organisationUnitGroup}"/>

<div id="question-${question.id}" class="question question-checkbox" data-question="${question.id}">
	<g:i18n field="${question.names}" />
	<ul>
		<g:each in="${question.getOptions(organisationUnitGroup)}" var="option">
			<g:set var="surveyElement" value="${option.surveyElement}"/>

		    <li id="element-${surveyElement?.id}" class="survey-element">
		    	<g:if test="${surveyElement != null}">
					<g:set var="dataElement" value="${surveyElement.dataElement}"/>
					<g:set var="enteredValue" value="${surveyPage.elements[surveyElement]}" />
				
					<g:render template="/survey/element/${dataElement.type.type.name().toLowerCase()}" model="[
						value: enteredValue.value, 
						lastValue: enteredValue.lastValue,
						type: dataElement.type, 
						suffix:'',
						surveyElement: surveyElement, 
						enteredValue: enteredValue, 
						readonly: readonly,
						isCheckbox: true
					]"/>
					<g:i18n field="${option.names}"/></span></span>
				</g:if>
				<g:else>
					No survey element for this option.
				</g:else>
				<div class="clear"></div>
			</li>
		</g:each>
	</ul>
</div>