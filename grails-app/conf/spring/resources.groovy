/**
* Copyright (c) 2011, Clinton Health Access Initiative.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the <organization> nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import org.chai.kevin.ExpressionService
import org.chai.kevin.JaqlService
import org.chai.kevin.RefreshValueService
import org.chai.kevin.dashboard.DashboardService
import org.chai.kevin.JaqlService
import org.chai.kevin.LanguageService;
import org.chai.location.LocationService;
import org.chai.kevin.dashboard.DashboardValueService
import org.chai.kevin.dashboard.DashboardService
import org.chai.kevin.data.DataService;
import org.chai.kevin.dsr.DsrService
import org.chai.kevin.exports.CalculationExportService;
import org.chai.kevin.exports.DataElementExportService;
import org.chai.kevin.exports.EntityExportService
import org.chai.kevin.exports.SurveyExportService
import org.chai.kevin.fct.FctService
import org.chai.kevin.form.FormValidationService
import org.chai.kevin.planning.PlanningService
import org.chai.kevin.reports.ReportExportService;
import org.chai.kevin.reports.ReportService
import org.chai.kevin.security.ShiroSecurityBridge;
import org.chai.kevin.survey.SurveyCopyService
import org.chai.kevin.survey.SurveyPageService
import org.chai.kevin.survey.summary.SummaryService
import org.chai.kevin.value.ExpressionService
import org.chai.kevin.value.RefreshValueService
import org.chai.kevin.value.ValidationService
import org.chai.kevin.value.ValueService
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.hibernate.SessionFactory;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean

def config = CH.config

Set<String> reportSkipLevels = config.report.skip.levels
Set<String> dashboardSkipLevels = config.dashboard.skip.levels
Set<String> dsrSkipLevels = config.dsr.skip.levels
Set<String> fctSkipLevels = config.fct.skip.levels
Set<String> surveySkipLevels = config.survey.skip.levels
Set<String> surveyExportSkipLevels = config.survey.export.skip.levels

beans = {
	
	validationService(ValidationService){
		jaqlService = ref("jaqlService")		
	}
	
	surveyCopyService(SurveyCopyService) {
		sessionFactory = ref("sessionFactory")
		languageService = ref("languageService")
	}
	
	formValidationService(FormValidationService){
		validationService = ref("validationService")
	}
	
	surveyPageService(SurveyPageService){
		surveyValueService = ref("surveyValueService")
		formElementService = ref("formElementService")
		valueService = ref("valueService")
		dataService = ref("dataService")
		locationService = ref("locationService")
		formValidationService = ref("formValidationService")
		transactionManager = ref("transactionManager")
		sessionFactory = ref("sessionFactory")
		locationSkipLevels = surveySkipLevels
	}
	
	summaryService(SummaryService){
		surveyValueService = ref("surveyValueService")
		locationService = ref("locationService")
	}

	surveyExportService(SurveyExportService){
		locationService = ref("locationService")
		surveyValueService = ref("surveyValueService")
		languageService = ref("languageService")
		enumService = ref("enumService")
		sessionFactory = ref("sessionFactory")
		skipLevels = surveyExportSkipLevels
	}
	
	entityExportService(EntityExportService){
		sessionFactory = ref("sessionFactory")
	}

	reportExportService(ReportExportService){
		languageService = ref("languageService")
		locationService = ref("locationService")
		skipLevels = reportSkipLevels
	}
	
	dashboardService(DashboardService) {
		reportService = ref("reportService")
		sessionFactory = ref("sessionFactory")
		dashboardPercentageService = ref("dashboardPercentageService")
		locationSkipLevels = dashboardSkipLevels
	}
	
	dsrService(DsrService){		
		reportService = ref("reportService")
		valueService = ref("valueService")
		locationSkipLevels = dsrSkipLevels
	}
	
	fctService(FctService){
		reportService = ref("reportService")
		valueService = ref("valueService")
		locationSkipLevels = fctSkipLevels
	}
	
	dashboardPercentageService(DashboardValueService) {
		valueService = ref("valueService")
		dashboardService = ref("dashboardService")
	}

	planningService(PlanningService) {
		formValidationService = ref("formValidationService")
		formElementService = ref("formElementService")
		valueService = ref("valueService")
		dataService = ref("dataService")
		sessionFactory = ref("sessionFactory")
		refreshValueService = ref("refreshValueService")
		locationService = ref("locationService")
	}
	
	// override the spring cache manager to use the same as hibernate
	springcacheCacheManager(EhCacheManagerFactoryBean) {
		shared = true
		cacheManagerName = "Springcache Plugin Cache Manager"
	}
	
	grailsSecurityBridge(ShiroSecurityBridge) {}
	
}