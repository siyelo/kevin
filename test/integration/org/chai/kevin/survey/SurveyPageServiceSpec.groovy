package org.chai.kevin.survey

import org.chai.kevin.LanguageService
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.location.DataLocationType
import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.RawDataElementValue;

class SurveyPageServiceSpec extends SurveyIntegrationTests {

	def surveyPageService
	def languageService
	
	def sessionFactory
	
	protected void tearDown() {
		super.tearDown()
		surveyPageService.languageService = languageService
	}
	
	def "test submit survey"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newFormEnteredValue(element, period, DataLocation.findByCode(KIVUYE), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(KIVUYE), false, true);
		newSurveyEnteredSection(section, period, DataLocation.findByCode(KIVUYE), false, true);
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(KIVUYE), false, true, false);
		def submitted = surveyPageService.submitAll(DataLocation.findByCode(KIVUYE), null, survey, null)
		
		then:
		submitted == true
		SurveyEnteredProgram.list()[0].closed == true
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.equals(v("1"))
	}
	
	def "test submit program"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newFormEnteredValue(element, period, DataLocation.findByCode(KIVUYE), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredSection(section, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(KIVUYE), false, true, false)
		def submitted = surveyPageService.submitAll(DataLocation.findByCode(KIVUYE), null, null, program)
				
		then:
		submitted == true
		SurveyEnteredProgram.list()[0].closed == true
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.equals(v("1"))
	}
	
	def "test submit all survey"(){
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))				
		
		when:
		newFormEnteredValue(element, period, DataLocation.findByCode(KIVUYE), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredSection(section, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(KIVUYE), false, true, false)
				
		newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(BUTARO), false, true)
		newSurveyEnteredSection(section, period, DataLocation.findByCode(BUTARO), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(BUTARO), false, true, false)
		
		then:
		SurveyEnteredProgram.list()[0].closed == false
		SurveyEnteredProgram.list()[1].closed == false
		
		when:
		def submitAll = surveyPageService.submitAll(Location.findByCode(BURERA), null, survey, null)
		
		then:
		submitAll == true
		
		SurveyEnteredProgram.count() == 2		
		SurveyEnteredProgram.list()[0].closed == true
		SurveyEnteredProgram.list()[1].closed == true
		SurveyEnteredProgram.list().collect { it.dataLocation }.equals([DataLocation.findByCode(KIVUYE), DataLocation.findByCode(BUTARO)])
		
		RawDataElementValue.count() == 2
		RawDataElementValue.list()[0].value.numberValue == 1
		RawDataElementValue.list()[1].value.numberValue == 1
		s(RawDataElementValue.list().collect { it.location }).equals(s([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)]))
	}
	
	def "test submit all program"(){
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))				
		
		when:
		newFormEnteredValue(element, period, DataLocation.findByCode(KIVUYE), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredSection(section, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(KIVUYE), false, true, false)
				
		newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(BUTARO), false, true)
		newSurveyEnteredSection(section, period, DataLocation.findByCode(BUTARO), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(BUTARO), false, true, false)
		
		then:
		SurveyEnteredProgram.list()[0].closed == false
		SurveyEnteredProgram.list()[1].closed == false
		
		when:
		def submitAll = surveyPageService.submitAll(Location.findByCode(BURERA), null, null, program)
		
		then:
		submitAll == true
		
		SurveyEnteredProgram.count() == 2
		SurveyEnteredProgram.list()[0].closed == true
		SurveyEnteredProgram.list()[1].closed == true
		SurveyEnteredProgram.list().collect { it.dataLocation }.equals([DataLocation.findByCode(KIVUYE), DataLocation.findByCode(BUTARO)])
		
		RawDataElementValue.count() == 2
		RawDataElementValue.list()[0].value.numberValue == 1
		RawDataElementValue.list()[1].value.numberValue == 1
		s(RawDataElementValue.list().collect { it.location }).equals(s([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)]))
	}
	
	def "test submit all survey with warning and invalid values"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		newFormEnteredValue(element, period, DataLocation.findByCode(KIVUYE), v("1"))
		
		def rule1 = newFormValidationRule(CODE(1), element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 10", true, [])
		def rule2 = newFormValidationRule(CODE(2), element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 100")		
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): "5"])
		def submitAll = surveyPageService.submitAll(Location.findByCode(BURERA), null, survey, null)		
		
		then:
		submitAll == true
		SurveyEnteredProgram.list()[0].closed == true		
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.numberValue == 5
		RawDataElementValue.list()[0].value.getAttribute("invalid").contains(rule1.id+"")
		RawDataElementValue.list()[0].value.getAttribute("invalid").contains(rule2.id+"")
	}
	
	def "test submit all program with warning and invalid values"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		newFormEnteredValue(element, period, DataLocation.findByCode(KIVUYE), v("1"))
		
		def rule1 = newFormValidationRule(CODE(1), element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 10", true, [])
		def rule2 = newFormValidationRule(CODE(2), element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 100")
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): "5"])
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def submitAll = surveyPageService.submitAll(Location.findByCode(BURERA), null, null, program)
		
		then:
		submitAll == true
		SurveyEnteredProgram.list()[0].closed == true
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.numberValue == 5
		RawDataElementValue.list()[0].value.getAttribute("invalid").contains(rule1.id+"")
		RawDataElementValue.list()[0].value.getAttribute("invalid").contains(rule2.id+"")
	}
	
	def "test submit all survey with types"(){		
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))				
		
		when:
		newFormEnteredValue(element, period, DataLocation.findByCode(KIVUYE), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredSection(section, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(KIVUYE), false, true, false)
				
		newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(BUTARO), false, true)
		newSurveyEnteredSection(section, period, DataLocation.findByCode(BUTARO), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(BUTARO), false, true, false)
		
		then:
		SurveyEnteredProgram.list()[0].closed == false
		SurveyEnteredProgram.list()[1].closed == false
		
		when:
		def types = new HashSet([DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def submitAll = surveyPageService.submitAll(Location.findByCode(BURERA), types, survey, null)
		
		then:
		submitAll == true
		
		SurveyEnteredProgram.count() == 2
		SurveyEnteredProgram.list()[0].closed == true
		SurveyEnteredProgram.list()[1].closed == false		
		SurveyEnteredProgram.list().collect { it.dataLocation }.equals([DataLocation.findByCode(KIVUYE), DataLocation.findByCode(BUTARO)])
		
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.numberValue == 1
		RawDataElementValue.list()[0].location.equals(DataLocation.findByCode(KIVUYE))
	}
	
	def "test submit all program with types"(){
			setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))				
		
		when:
		newFormEnteredValue(element, period, DataLocation.findByCode(KIVUYE), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredSection(section, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(KIVUYE), false, true, false)
				
		newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(BUTARO), false, true)
		newSurveyEnteredSection(section, period, DataLocation.findByCode(BUTARO), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(BUTARO), false, true, false)
		
		then:
		SurveyEnteredProgram.list()[0].closed == false
		SurveyEnteredProgram.list()[1].closed == false
		
		when:
		def types = new HashSet([DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def submitAll = surveyPageService.submitAll(Location.findByCode(BURERA), types, null, program)
		
		then:
		submitAll == true
		
		SurveyEnteredProgram.count() == 2
		SurveyEnteredProgram.list()[0].closed == true
		SurveyEnteredProgram.list()[1].closed == false		
		SurveyEnteredProgram.list().collect { it.dataLocation }.equals([DataLocation.findByCode(KIVUYE), DataLocation.findByCode(BUTARO)])
		
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.numberValue == 1
		RawDataElementValue.list()[0].location.equals(DataLocation.findByCode(KIVUYE))
	}
	
	def "test submit program with skipped element"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		newSurveyProgram(CODE(1), survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(CODE(2), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_MAP(["key1":Type.TYPE_NUMBER(),"key2":Type.TYPE_NUMBER()])))
		
		when:
		newFormEnteredValue(element, period, DataLocation.findByCode(KIVUYE), new Value("{\"value\":[{\"map_value\":{\"skipped\":\"33\",\"value\":null},\"map_key\":\"key1\"},{\"map_value\":{\"value\":10},\"map_key\":\"key2\"}]}"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredSection(section, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(KIVUYE), false, true, false)
				
		then:
		surveyPageService.submitAll(DataLocation.findByCode(KIVUYE), null, null, program) == true
		RawDataElementValue.count() == 1
	}
	
	def "get survey skip levels"(){
		setup:
		setupLocationTree()
		
		//survey location filter skip levels
		when:
		def locationSkipLevels = surveyPageService.getSkipLocationLevels()
		
		then:
		locationSkipLevels.size() == 1
		locationSkipLevels.contains(LocationLevel.findByCode(SECTOR))
	}	
	
	def "test modify"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		newSurveyProgram(CODE(2), survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): "10"])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 10
	}
	
	def "test modify with skipped question"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		newSurveyProgram(CODE(2), survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(CODE(2), section, 2, [(HEALTH_CENTER_GROUP)])
		
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))
		def skipRule = newSurveySkipRule(CODE(1), survey, "\$"+element1.id+" == 1", [:], [question2])
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element1], [("elements["+element1.id+"].value"): "1"])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 1
		SurveyEnteredQuestion.count() == 2
		SurveyEnteredQuestion.list().find {it.question.equals(question2)}.skippedRules.equals(new HashSet([skipRule]))
	}
	
	def "test modify with skipped question referring to non existing element"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		newSurveyProgram(CODE(2), survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(CODE(2), section, 2, [(HEALTH_CENTER_GROUP)])
		
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))
		def skipRule = newSurveySkipRule(CODE(1), survey, "\$"+element1.id+" == 1", [(element2):""], [])
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element1], [("elements["+element1.id+"].value"): "1"])
		
		then:
		FormEnteredValue.count() == 2
		FormEnteredValue.list()[0].value.numberValue == 1
		FormEnteredValue.list()[1].validatable.isSkipped("") == true
		SurveyEnteredQuestion.count() == 2
	}
	
	def "test warning"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		newSurveyProgram(CODE(2), survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def rule = newFormValidationRule(CODE(1), element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 10", true, [])
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): "5"])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 5
		FormEnteredValue.list()[0].value.getAttribute("invalid") == rule.id+""
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): "5", ("elements["+element.id+"].value[warning]"): ""+rule.id])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 5
		FormEnteredValue.list()[0].value.getAttribute("invalid") == rule.id+""
		FormEnteredValue.list()[0].value.getAttribute("warning") == rule.id+""
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): "4", ("elements["+element.id+"].value[warning]"): ""+rule.id])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 4
		FormEnteredValue.list()[0].value.getAttribute("invalid") == rule.id+""
	}
	
	def "test warning and invalid values"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		newSurveyProgram(CODE(2), survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def rule1 = newFormValidationRule(CODE(1), element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 10", true, [])
		def rule2 = newFormValidationRule(CODE(2), element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 100")
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): "5"])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 5
		FormEnteredValue.list()[0].value.getAttribute("invalid").contains(rule1.id+"")
		FormEnteredValue.list()[0].value.getAttribute("invalid").contains(rule2.id+"")
	}
	
	def "test invalid values with checkbox option"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		newSurveyProgram(CODE(2), survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newCheckboxQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_BOOL()))
		def element2 = newSurveyElement(question1, newRawDataElement(CODE(2), Type.TYPE_BOOL()))
		
		def option1 = newCheckboxOption(CODE(1), question1, 1, [(HEALTH_CENTER_GROUP)], element1)
		def option2 = newCheckboxOption(CODE(2), question1, 2, [(HEALTH_CENTER_GROUP)], element2)
		
		def question2 = newSimpleQuestion(CODE(2), section, 1, [(HEALTH_CENTER_GROUP)])
		def element3 = newSurveyElement(question2, newRawDataElement(CODE(3), Type.TYPE_BOOL()))
		
		def rule1 = newFormValidationRule(CODE(1), element3, "", [(HEALTH_CENTER_GROUP)], "\$"+element3.id+" and \$"+element1.id, true, [])
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element3, element2], [("elements["+element2.id+"].value"): "1", ("elements["+element3.id+"].value"): "1"])
		
		then:
		FormEnteredValue.count() == 3
		FormEnteredValue.list().find{it.formElement == element1}.value.booleanValue == false
		FormEnteredValue.list().find{it.formElement == element3}.value.getAttribute("invalid").contains(rule1.id+"")
	}
	
	def "test modify does not touch unmodified values"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		newSurveyProgram(CODE(2), survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_NUMBER())))
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): ["[0]", "[1]"]])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.listValue.size() == 2
		FormEnteredValue.list()[0].value.listValue[0].isNull()
		FormEnteredValue.list()[0].value.listValue[1].isNull()
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value[0]"): "5", ("elements["+element.id+"].value.indexes"): ["[0]", "[1]"]])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.listValue.size() == 2
		FormEnteredValue.list()[0].value.listValue[0].numberValue == 5
		FormEnteredValue.list()[0].value.listValue[1].isNull()
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value[1]"): "10", ("elements["+element.id+"].value.indexes"): ["[0]", "[1]"]])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.listValue.size() == 2
		FormEnteredValue.list()[0].value.listValue[0].numberValue == 5
		FormEnteredValue.list()[0].value.listValue[1].numberValue == 10
	}

	def "test refresh without surveyelement"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		newSurveyProgram(CODE(2), survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		
		when:
		surveyPageService.refreshSectionForDataLocation(DataLocation.findByCode(KIVUYE), section, false)
		
		then:
		FormEnteredValue.count() == 0
		SurveyEnteredQuestion.count() == 1
		SurveyEnteredSection.count() == 1
		SurveyEnteredSection.list()[0].totalQuestions == 1
		SurveyEnteredSection.list()[0].completedQuestions == 1
	}
	
	def "test refresh does not set user id and timestamp"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		surveyPageService.refreshSurveyForDataLocation(DataLocation.findByCode(KIVUYE), survey, false, false)
		
		then:
		FormEnteredValue.count() == 1
		SurveyEnteredQuestion.count() == 1
		SurveyEnteredSection.count() == 1
		SurveyEnteredQuestion.list()[0].userUuid == null
		SurveyEnteredQuestion.list()[0].timestamp == null
		SurveyEnteredSection.list()[0].userUuid == null
		SurveyEnteredSection.list()[0].timestamp == null
		SurveyEnteredProgram.list()[0].userUuid == null
		SurveyEnteredProgram.list()[0].timestamp == null
		FormEnteredValue.list()[0].userUuid == null
		FormEnteredValue.list()[0].timestamp == null
	}
		
	
	def "test refresh sets correct number of completed questions"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		surveyPageService.refreshSurveyForDataLocation(DataLocation.findByCode(KIVUYE), survey, false, false)
		
		then:
		FormEnteredValue.count() == 1
		SurveyEnteredQuestion.count() == 1
		SurveyEnteredSection.count() == 1
		SurveyEnteredSection.list()[0].totalQuestions == 1
		SurveyEnteredSection.list()[0].completedQuestions == 0
		SurveyEnteredProgram.list()[0].totalQuestions == 1
		SurveyEnteredProgram.list()[0].completedQuestions == 0
		
		when:
		FormEnteredValue.list()[0].delete(flush: true)
		newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), Value.VALUE_NUMBER(1d))
		surveyPageService.refreshSurveyForDataLocation(DataLocation.findByCode(KIVUYE), survey, false, false)
		
		then:
		FormEnteredValue.count() == 1
		SurveyEnteredQuestion.count() == 1
		SurveyEnteredSection.count() == 1
		SurveyEnteredSection.list()[0].totalQuestions == 1
		SurveyEnteredSection.list()[0].completedQuestions == 1
		SurveyEnteredProgram.list()[0].totalQuestions == 1
		SurveyEnteredProgram.list()[0].completedQuestions == 1
		
		when: "invalid"
		FormEnteredValue.list()[0].delete(flush: true)
		def enteredValue = newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), Value.VALUE_NUMBER(1d))
		enteredValue.value.setAttribute("invalid", "1")
		enteredValue.save(failOnError: true)
		surveyPageService.refreshSurveyForDataLocation(DataLocation.findByCode(KIVUYE), survey, false, false)
		
		then:
		FormEnteredValue.count() == 1
		SurveyEnteredQuestion.count() == 1
		SurveyEnteredSection.count() == 1
		SurveyEnteredSection.list()[0].totalQuestions == 1
		SurveyEnteredSection.list()[0].completedQuestions == 0
		SurveyEnteredProgram.list()[0].totalQuestions == 1
		SurveyEnteredProgram.list()[0].completedQuestions == 0
		
		when: "invalid but skipped value"
		FormEnteredValue.list()[0].delete(flush: true)
		enteredValue = newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), Value.VALUE_NUMBER(1d))
		enteredValue.value.setAttribute("invalid", "1")
		enteredValue.value.setAttribute("skipped", "2")
		enteredValue.save(failOnError: true)
		surveyPageService.refreshSurveyForDataLocation(DataLocation.findByCode(KIVUYE), survey, false, false)
		
		then:
		FormEnteredValue.count() == 1
		SurveyEnteredQuestion.count() == 1
		SurveyEnteredSection.count() == 1
		SurveyEnteredSection.list()[0].totalQuestions == 1
		SurveyEnteredSection.list()[0].completedQuestions == 1
		SurveyEnteredProgram.list()[0].totalQuestions == 1
		SurveyEnteredProgram.list()[0].completedQuestions == 1
		
		when: "invalid but skipped question"
		def skipRule = newSurveySkipRule(CODE(1), survey, "true", [:], [:])
		FormEnteredValue.list()[0].delete(flush: true)
		enteredValue = newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), Value.VALUE_NUMBER(1d))
		enteredValue.save(failOnError: true)
		def enteredQuestion = SurveyEnteredQuestion.list()[0]
		enteredQuestion.addToSkippedRules(skipRule)
		enteredQuestion.save(failOnError: true)
		surveyPageService.refreshSurveyForDataLocation(DataLocation.findByCode(KIVUYE), survey, false, false)
		
		then:
		FormEnteredValue.count() == 1
		SurveyEnteredQuestion.count() == 1
		SurveyEnteredSection.count() == 1
		SurveyEnteredSection.list()[0].totalQuestions == 1
		SurveyEnteredSection.list()[0].completedQuestions == 1
		SurveyEnteredProgram.list()[0].totalQuestions == 1
		SurveyEnteredProgram.list()[0].completedQuestions == 1
	}
	
	def "test refresh with reset flag erases old values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), v("1"))
		surveyPageService.refreshSectionForDataLocation(DataLocation.findByCode(KIVUYE), section, true)
		
		then:
		FormEnteredValue.list()[0].value.equals(Value.NULL_INSTANCE())
	}
	
	def "test refresh without reset flag keeps old values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), v("1"))
		surveyPageService.refreshSectionForDataLocation(DataLocation.findByCode(KIVUYE), section, false)
		
		then:
		FormEnteredValue.list()[0].value.equals(v("1"))
	}
	
	def "test refresh with reset flag erases unused entered values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newSurveyEnteredQuestion(question1, period, DataLocation.findByCode(BUTARO), false, true)
		newFormEnteredValue(element1, period, DataLocation.findByCode(BUTARO), v("1"))
		newSurveyEnteredSection(section, period, DataLocation.findByCode(BUTARO), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(BUTARO), false, true, false)
		surveyPageService.refreshSurveyForDataLocation(DataLocation.findByCode(BUTARO), survey, false, true)
		sessionFactory.currentSession.flush()
		
		then:
		SurveyEnteredQuestion.count() == 0
		FormEnteredValue.count() == 0
		SurveyEnteredSection.count() == 0
		SurveyEnteredProgram.count() == 0
	}
	
	def "test refresh without reset flag keeps unused entered values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newSurveyEnteredQuestion(question1, period, DataLocation.findByCode(BUTARO), false, true)
		newFormEnteredValue(element1, period, DataLocation.findByCode(BUTARO), v("1"))
		newSurveyEnteredSection(section, period, DataLocation.findByCode(BUTARO), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(BUTARO), false, true, false)
		surveyPageService.refreshSurveyForDataLocation(DataLocation.findByCode(BUTARO), survey, false, false)
		sessionFactory.currentSession.flush()
		
		then:
		SurveyEnteredQuestion.count() == 1
		FormEnteredValue.count() == 1
		SurveyEnteredSection.count() == 1
		SurveyEnteredProgram.count() == 1
	}
		
	def "test program order"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program1 = newSurveyProgram(CODE(1), survey, 2, [(HEALTH_CENTER_GROUP)])
		def program2 = newSurveyProgram(CODE(2), survey, 1, [(HEALTH_CENTER_GROUP)])
		
		when:
		def surveyPage = surveyPageService.getSurveyPage(DataLocation.findByCode(KIVUYE), survey)
		
		then:
		surveyPage.programs.equals(program2, program1)
	}
	
	def "test checkbox option order"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newCheckboxQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def option1 = newCheckboxOption(CODE(1), question, 2, [(HEALTH_CENTER_GROUP)], null)
		def option2 = newCheckboxOption(CODE(2), question, 1, [(HEALTH_CENTER_GROUP)], null)
		
		when:
		def surveyPage = surveyPageService.getSurveyPage(DataLocation.findByCode(KIVUYE), section)
		
		then:
		surveyPage.getOptions(question).equals([option2, option1])
	}
	
	def "test copy data"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		
		def period1 = newPeriod()
		def period2 = newPeriod(2006)
		
		def survey = newSurvey(CODE(1), period2)
		survey.lastPeriod = period1
		survey.save()
		
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		
		newRawDataElementValue(dataElement, period1, DataLocation.findByCode(KIVUYE), v("1"))
				
		when:
		surveyPageService.copyData(DataLocation.findByCode(KIVUYE), element)
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 1d
	}
		
	def "test copy data dows not copy if data already exist"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		
		def period1 = newPeriod()
		def period2 = newPeriod(2006)
		
		def survey = newSurvey(CODE(1), period2)
		survey.lastPeriod = period1
		survey.save()
		
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		
		newRawDataElementValue(dataElement, period1, DataLocation.findByCode(KIVUYE), v("1"))
		newFormEnteredValue(element, period2, DataLocation.findByCode(KIVUYE), v("2"))
		
		when:
		surveyPageService.copyData(DataLocation.findByCode(KIVUYE), element)
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 2d
	}
}
