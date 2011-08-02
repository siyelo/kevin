<div id="add-column" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create a Table Column </h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	<div>
	<div id="add-column-col">
	<g:form url="[controller:'tableColumn', action:'save']" useToken="true">
	<label class="display-in-block">Table Name :</label>
	<input type="text" value="${i18n(field: column.question.tableNames)}" class="idle-field" disabled />
	<input type="hidden" name="question.id"  value="${column.question?.id}" />
	<div class="error-list"><g:renderErrors bean="${column}" field="question" /></div>
		<g:i18nRichTextarea name="names" bean="${column}" value="${column.names}" label="Option" field="names" height="150"  width="300" maxHeight="150" />
		<g:input name="order" label="Order" bean="${column}" field="order"/>
		<div id="orgunitgroup-block">
				<div
					class="group-list ${hasErrors(bean:column, field:'groupUuidString', 'errors')}">
					<label for="groups" class="display-in-block">Organisation Unit Group:</label>
						<select class="group-list" name="groupUuids" multiple="multiple" size="5" >
							<g:each in="${groups}" var="group">
								<option value="${group.uuid}" ${groupUuids.contains(group.uuid)?'selected="selected"':''}>
						           ${group.name}
					            </option>
							</g:each>
						</select>
					<div class="error-list">
						<g:renderErrors bean="${column}" field="groupUuidString" />
					</div>
				</div>
			</div>
		<g:if test="${column.id != null}">
			<input type="hidden" name="id" value="${column.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="question-form">Save Option</button>
			&nbsp;&nbsp;
			<button id="cancel-button">Cancel</button>
		</div>
	</g:form>
	</div>
	</div>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>
<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();	 
	});					
</script>