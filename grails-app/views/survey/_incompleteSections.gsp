<g:set value="${surveyPage.getIncompleteSections(surveyPage.objective)}" var="incompleteSections"/>
<g:if test="${!incompleteSections.isEmpty()}">
	<div>
		<g:message code="survey.objective.incomplete.text" default="The following sections are incomplete, please go back and complete them" />:
		<ul>
			<g:each in="${incompleteSections}" var="section">
				<li>
					<a href="${createLink(controller:'editSurvey', action:'sectionPage', params:[section:section.id, location: surveyPage.location.id])}">
						<g:i18n field="${section.names}"/>
					</a>
				</li>
			</g:each>
		</ul>
	</div>
</g:if>