<table class="listing">
	<thead>
		<tr>
			<th/>
			<th>Name</th>
			<th>Code</th>
			<th>Objective</th>
			<th>Data Element</th>
			<th>Category</th>
			<th>Order</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="target">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'dsrTarget', action:'edit', params:[id: target.id])}">
								<g:message code="default.link.edit.label" default="Edit" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'dsrTarget', action:'delete', params:[id: target.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');"><g:message code="default.link.delete.label" default="Delete" /></a>
						</li>
						
					</ul>
				</td>
				<td>
					<g:i18n field="${target.names}"/>
				</td>
				<td>${target.code}</td>
				<td>
					<g:i18n field="${target.objective.names}"/>
				</td>
				<td>${target.dataElement.code}</td>
				<td>
					<g:i18n field="${target.category?.names}"/>
				</td>
				<td>${target.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>