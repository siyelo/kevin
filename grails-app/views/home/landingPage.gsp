<%@ page import="org.apache.shiro.SecurityUtils" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="main" />
	<title><g:message code="landingpage.title" /></title>
	
	<r:require modules="chosen,richeditor,fieldselection,cluetip,form,dropdown,datepicker,list"/>
</head>
<body>
	<h3 class="landing-heading">Welcome Dime, <span>Please choose one of the three options below to continue</span></h3>
	<div class="main" id="landing-main">
		<%
			if (SecurityUtils.subject.isPermitted('menu:reports') 
				&& SecurityUtils.subject.isPermitted('menu:survey')
				&& SecurityUtils.subject.isPermitted('menu:planning')) {
				size = "three"
			}
			else size = "two"
		%>

		<ul class="landing-options">
			<li class="active">
				<a href="#"><img src="${resource(dir:'images',file:'/icons/icon-reports.png')}" alt="Home"/><span>Reports</span></a>
				<div>
					<img width="240" height="187" class="left" src="${resource(dir:'images',file:'reports.png')}"/>
					<h4>Write and review your Reports</h4>
					<p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
					<p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan.</p>
					<p><a class="next white" href="#">Go to Reports</a></p>
				</div>
			</li>
			<li>
				<a href="#"><img src="${resource(dir:'images',file:'/icons/icon-survey.png')}" alt="Home"/><span>Survey</span></a>
				<div>
					<img width="240" height="187" class="left" src="${resource(dir:'images',file:'reports.png')}"/>
					<h4>Write and review your Surveys</h4>
					<p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
					<p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan.</p>
					<p><a class="next white" href="#">Go to Reports</a></p>
				</div>
			</li>
			<li>
				<a href="#"><img src="${resource(dir:'images',file:'/icons/icon-planning.png')}" alt="Home"/><span>Planning</span></a>
				<div>
					<img width="240" height="187" class="left" src="${resource(dir:'images',file:'reports.png')}"/>
					<h4>Write and review your Plans</h4>
					<p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
					<p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan.</p>
					<p><a class="next white" href="#">Go to Reports</a></p>
				</div>
			</li>
		</ul>
		
		<!-- <shiro:hasPermission permission="menu:reports">
			<g:render template="landingPageItem" model="[
				title: message(code: 'landingpage.reports.label'), image: 'reports.png', text: message(code: 'landingpage.reports.text'),
				class: size, link: createLink(controller: 'dashboard', action: 'view')]"/>
		</shiro:hasPermission>
		
		<shiro:hasPermission permission="menu:survey">
			<g:render template="landingPageItem" model="[
				title: message(code: 'landingpage.survey.label'), image: 'survey.png', text: message(code: 'landingpage.survey.text'),
				class: size, link: createLink(controller: 'editSurvey', action: 'view')]"/>
		</shiro:hasPermission>
		
		<shiro:hasPermission permission="menu:planning">
			<g:render template="landingPageItem" model="[
				title: message(code: 'landingpage.planning.label'), image: 'planning.png', text: message(code: 'landingpage.planning.text'),
				class: size, link: createLink(controller: 'editPlanning', action: 'view')]"/>
		</shiro:hasPermission> -->
		
		<g:if test="${false}">
			<!-- deactivated for now -->
			<shiro:hasPermission permission="menu:admin">
				<g:render template="landingPageItem" model="[
					title: message(code: 'landingpage.admin.label'), image: 'admin.png', text: message(code: 'landingpage.admin.text'),
					class: size, link: createLink(controller: 'admin', action: 'view')]"/>
			</shiro:hasPermission>
		</g:if>
	</div>
	<ul class="landing-more">
		<li class="third">
			<img src="../images/icons/icon-landing-email.png" class="left" />
			<h4>Contact Us</h4>
			<p>contact@dhsst.org</p>
		</li>
		<li class="third">
			<img src="../images/icons/icon-landing-help.png" class="left" />
			<h4>Helpdesk</h4>
			<p>Having problems? Call 144</p>
		</li>
		<li class="third">
			<img src="../images/icons/icon-landing-bug.png" class="left" />
			<h4>Report a bug</h4>
			<p>Go to Redmine to report</p>
		</li>
	</ul>
</body>
</html>