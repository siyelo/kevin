<div id="add-enum-option" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Enum Option</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'enumOption', action:'save', params:[targetURI: targetURI]]" useToken="true">
	<input type="hidden" name="enume.id" value="${option.enume.id}"/>
	<g:i18nTextarea name="names" bean="${option}" value="${option.names}" label="Option" field="names" height="100"  width="300" maxHeight="100" />
	<g:input name="value" label="Value" bean="${option}" field="value"/>
	
	<div class="row">
		<label>Inactive</label>
		<g:checkBox name="inactive" value="${option.inactive}" />
	</div>
		
	<g:input name="order" label="Order" bean="${option}" field="order"/>
		<g:if test="${option.id != null}">
			<input type="hidden" name="id" value="${option.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form">Save Enum Option</button>
			<a href="${createLink(uri: targetURI)}">cancel</a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
