<table class="listing">
  	<thead>
  		<tr>
  			<th/>
  		    <g:sortableColumn property="id" title="${message(code: 'entity.id.label')}" />
  			<g:sortableColumn property="code" params="[q:params.q]" title="${message(code: 'entity.code.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q]" title="${message(code: 'entity.name.label')}" />
  		    <!-- TODO make columns sortable -->
  		    <th><g:message code="entity.type.label"/></th>
  		    <th><g:message code="entity.datatype.label"/></th>
  			<g:sortableColumn property="expression" params="[q:params.q]" title="${message(code: 'calculation.expression.label')}" />  		    
  			<g:sortableColumn property="refreshed" params="[q:params.q]" title="${message(code: 'calculation.lastrefreshed.label')}" />
  			<g:sortableColumn property="lastValueChanged" params="[q:params.q]" title="${message(code: 'calculation.lastvaluechanged.label')}" />
  			<th><g:message code="entity.list.manage.label"/></th>
  		</tr>
  	</thead>
  	<tbody>
  		<g:each in="${entities}" status="i" var="calculation"> 
  			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
  				<td>
  					<ul class="horizontal">
  						<li>
  							<a class="edit-link" href="${createLinkWithTargetURI(controller:calculation.class.simpleName.toLowerCase(), action:'edit', params:[id: calculation.id])}">
  								<g:message code="default.link.edit.label" />
  							</a>
  						</li>
  						<li>
  							<a class="delete-link" href="${createLinkWithTargetURI(controller:calculation.class.simpleName.toLowerCase(), action:'delete', params:[id: calculation.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
  								<g:message code="default.link.delete.label" />
  							</a>
  						</li>
  					</ul>
  				</td>
  				<td>${calculation.id}</td>
  				<td class="calculation-explainer" data-data="${calculation.id}">
  					<a class="cluetip"
  						href="${createLink(controller:'data', action:'getExplainer', params:[id: calculation.id])}"
					 	rel="${createLink(controller: 'data', action:'getDescription', params:[id: calculation.id])}">
  						${calculation.code}
  					</a>
  				</td>	
  				<td><g:i18n field="${calculation.names}" /></td>  				
  				<td><g:message code="${calculation.class.simpleName.toLowerCase()}.label"/></td>
  				<td>${calculation.type.getDisplayedValue(2, 2)}</td>
  				<td>${calculation.expression}</td>
  				<td><g:formatDate format="yyyy-MM-dd HH:mm" date="${calculation.refreshed}"/></td>
  				<td><g:formatDate format="yyyy-MM-dd HH:mm" date="${calculation.lastValueChanged}"/></td>
  				<td>
					<div class="js_dropdown dropdown"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
							<ul>
								<li>
  									<a href="${createLink(controller:'data', action:'dataValueList', params:[data:calculation.id])}">
  										<g:message code="dataelement.viewvalues.label"/>
  									</a>
  								</li>
  								<li>
  									<a href="${createLinkWithTargetURI(controller:'task', action:'create', params:[class:'CalculateTask', dataId:calculation.id])}">
  										<g:message code="dataelement.calculatevalues.label"/>
  									</a>
  								</li>
  								<li>
  									<a href="${createLinkWithTargetURI(controller:'data', action:'deleteValues', params:[data:calculation.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
  										<g:message code="data.deletevalues.label"/>
  									</a>
  								</li>
  							</ul>
  						</div>
  					</div>
  				</td>
  			</tr>
  			<tr class="explanation-row">
  				<td colspan="9">
  					<div class="explanation-cell" id="explanation-${calculation.id}"></div>
  				</td>
  			</tr>
  		</g:each>
  	</tbody>
</table>

<script type="text/javascript">
	
	$(document).ready(function() {
		$('.calculation-explainer').bind('click', function() {
			var calculation = $(this).data('data');
			explanationClick(this, calculation, function(){});
			return false;
		});
	});
	
</script>