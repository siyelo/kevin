<table>
	<thead>
		<tr>
		    <th>Id</th>
			<th>Survey</th>
			<th>Description</th>
			<th>Expression</th>
			<th>Number of Survey Elements</th>
			<th>Number of Questions</th>
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="skip">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>${skip.id}</td>
				<td><g:i18n field="${skip.survey.names}" /></td>
				<td><g:i18n field="${skip.descriptions}" /></td>
				<td>${skip.expression}</td>
				<td> 
				   ${skip.skippedSurveyElements.size()}
			    </td>
				<td>${skip.skippedSurveyQuestions.size()}</td>
				<td>
			<div class="dropdown white-dropdown"> 
			     <a class="selected" href="#" data-type="skip-rule">Manage</a>
			<div class="hidden dropdown-list">
				<ul>
					<li class="edit-skip-rule-link">
					<g:link controller="surveySkipRule" action="edit" id="${skip.id}" class="flow-edit">
					    <g:message code="general.text.edit" default="Edit" /> 
					</g:link>
					</li>
					<li class="delete-skip-rule-link">
				    <g:link controller="surveySkipRule" action="delete" id="${skip.id}" class="flow-delete">
				        <g:message code="general.text.delete" default="Delete" /> 
				    </g:link>
					</li>
				</ul>
			</div>
			</div> 		
			</td>
			</tr>
		</g:each>
	</tbody>
</table>