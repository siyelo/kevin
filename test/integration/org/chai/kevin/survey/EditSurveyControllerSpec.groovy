package org.chai.kevin.survey

import org.chai.kevin.location.DataEntity;
import org.chai.kevin.location.LocationEntity;

class EditSurveyControllerSpec extends SurveyIntegrationTests {

	def editSurveyController
	
	def "get survey page with null survey elements"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(objective, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newTableQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def column = newTableColumn(question1, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def row = newTableRow(question1, 1, [(DISTRICT_HOSPITAL_GROUP)], [(column): null])

		def question2 = newSimpleQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])

		def question3 = newCheckboxQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def option = newCheckboxOption(question3, 1, [(DISTRICT_HOSPITAL_GROUP)], null)
		editSurveyController = new EditSurveyController()
		
		when:
		editSurveyController.params.organisation = DataEntity.findByCode(BUTARO).id
		editSurveyController.params.section = section.id
		editSurveyController.sectionPage()
		
		then:
		editSurveyController.modelAndView.model.surveyPage.survey.equals(survey)
		editSurveyController.modelAndView.model.surveyPage.section.equals(section)
		
	}
	
	def "get survey page with valid parameters"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		editSurveyController = new EditSurveyController()
		
		when:
		editSurveyController.params.organisation = DataEntity.findByCode(BUTARO).id
		editSurveyController.params.survey = survey.id
		editSurveyController.surveyPage()
		
		then:
		editSurveyController.modelAndView.model.surveyPage.survey.equals(survey)
	}
	
	
	def "access to view action redirects to active survey if SurveyUser"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataEntity.findByCode(BUTARO).id))
		def period = newPeriod()
		def survey = newSurvey([:], period, true)
		editSurveyController = new EditSurveyController()
		
		when:
		editSurveyController.view()
		
		then:
		editSurveyController.response.redirectedUrl == '/editSurvey/surveyPage/'+DataEntity.findByCode(BUTARO).id+'?survey='+survey.id
	}
	
	def "access to view action redirects to 404 if no active survey with SurveyUser"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataEntity.findByCode(BUTARO).id))
		def period = newPeriod()
		def survey = newSurvey(period)
		editSurveyController = new EditSurveyController()
		
		when:
		editSurveyController.view()
		
		then:
		editSurveyController.response.redirectedUrl == null
	}
	
	def "access to view action redirects to summary page if normal User"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		editSurveyController = new EditSurveyController()
		
		when:
		editSurveyController.view()
		
		then:
		editSurveyController.response.redirectedUrl == '/summary/summaryPage'
	}
	
	def "export survey works"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(objective, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		editSurveyController = new EditSurveyController()
		
		when:
		editSurveyController.params.organisation = LocationEntity.findByCode(RWANDA).id
		editSurveyController.params.survey = survey.id
		editSurveyController.export()
		
		then:
		editSurveyController.response.getContentType() == "application/zip"
	}
	
}
