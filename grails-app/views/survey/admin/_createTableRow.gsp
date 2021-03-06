<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.tablequestion.tablerow.label')]"/>
		</h3>
		<g:locales />
	</div>
	<div class="forms-container">
		<div class="data-field-column">
			<g:form url="[controller:'tableRow', action:'save', params:[targetURI: targetURI]]" useToken="true">
				<div class="row">
					<label><g:message code="survey.tablequestion.label"/>:</label> 
					<input type="text" value="${i18n(field: row.question.tableNames)}" class="idle-field" disabled />
				</div>

				<input type="hidden" name="question.id" value="${row.question?.id}" />
				
				<g:i18nRichTextarea name="names" bean="${row}" value="${row.names}" label="${message(code:'survey.tablequestion.tablerow.name.label')}" field="names" height="150" width="300" maxHeight="150" />

				<div class="row ${hasErrors(bean:row, field:'surveyElements', 'errors')}">
					<input type="hidden" name="surveyElement" value="_" />
					<g:each in="${row.question.columns}" status="i" var="column">
						<div class="${(i % 2) == 0 ? 'odd' : 'even'}">
							<span class="bold"><g:message code="survey.tablequestion.tablecolumn.label"/>:</span><span> ${i18n(field: column.names)}</span>
							<div>
								<label for="dataElement[${column.id}]"><g:message code="dataelement.label"/>:</label> 
								<input type="text" name="dataElement[${column.id}]" class="data-element-name idle-field" value="${row.surveyElements[column]?.dataElement?.code}" /> 
								<input type="hidden" name="surveyElement[${column.id}].dataElement.id" class="data-element-id idle-field " value="${row.surveyElements[column]?.dataElement?.id}" /> 
								<input type="hidden" name="surveyElement[${column.id}].id" class="idle-field" value="${row.surveyElements[column]?.id}" /> 
								<input type="hidden" name="surveyElement" value="${column.id}" />
							</div>
						</div>
					</g:each>
					<div class="error-list">
						<g:renderErrors bean="${row}" field="surveyElements" />
					</div>
				</div>
				<div class="clear"></div>
				
				<g:input name="code" label="${message(code:'entity.code.label')}" bean="${row}" field="code" />
				<g:input name="order" label="${message(code:'entity.order.label')}" bean="${row}" field="order" />

				<g:selectFromList name="typeCodes" label="${message(code:'entity.datalocationtype.label')}" bean="${row}" field="typeCodeString" 
					from="${types}" value="${row.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>


				<g:if test="${row.id != null}">
					<input type="hidden" name="id" value="${row.id}"></input>
				</g:if>
				<div class="row">
					<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
					<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
				</div>
			</g:form>
		</div>

		<div class="data-search-column">
			<g:form name="search-data-form" class="search-form" url="[controller:'data', action:'getData', params: [include: ['bool', 'enum', 'string', 'number', 'date'], class: 'RawDataElement']]">
				<div class="row">
					<label for="searchText"><g:message code="entity.search.label"/>: </label> 
					<input name="searchText" class="idle-field"></input>
					<button type="submit"><g:message code="default.button.search.label"/></button>
					<div class="clear"></div>
				</div>
			</g:form>
			<ul class="filtered idle-field" id="data"></ul>
		</div>
		<div class="clear"></div>
	</div>
	<div class="clear"></div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		$('input.data-element-name').bind('focus', function(){
			$('*').removeClass('current-data-element');
			$(this).parents('div:first').addClass('current-data-element');
		});	
		getDataElement(function(event){
			$('div.current-data-element').find('input.data-element-name').val($.trim($(this).text()));
			$('div.current-data-element').find('input.data-element-id').val($(this).data('code'));
		});
	});					
</script>