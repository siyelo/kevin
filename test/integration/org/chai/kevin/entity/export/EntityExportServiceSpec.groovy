package org.chai.kevin.entity.export

import java.lang.reflect.Field
import org.apache.commons.lang.StringUtils
import org.chai.kevin.IntegrationTests
import org.chai.kevin.Translation
import org.chai.kevin.dashboard.DashboardTarget
import org.chai.kevin.data.Type
import org.chai.kevin.entity.export.EntityHeaderSorter
import org.chai.kevin.entity.export.EntityIntegrationTests.IsExportableEntity
import org.chai.kevin.entity.export.EntityIntegrationTests.IsNotExportableEntity
import org.chai.kevin.entity.export.EntityIntegrationTests.TestEntities
import org.chai.kevin.entity.export.EntityIntegrationTests.TestEntity
import org.chai.kevin.form.FormEnteredValue
import org.chai.kevin.location.DataLocationType
import org.chai.kevin.location.DataLocation
import org.chai.kevin.location.Location
import org.chai.kevin.survey.SurveyQuestion
import org.chai.kevin.survey.SurveyCheckboxQuestion
import org.chai.kevin.survey.SurveySimpleQuestion
import org.chai.kevin.survey.SurveyTableQuestion
import org.chai.kevin.survey.SurveyElement
import org.chai.kevin.survey.SurveyIntegrationTests
import org.chai.kevin.util.Utils

class EntityExportServiceSpec extends EntityIntegrationTests {

	def entityExportService
	
	def "test entity is exportable"(){
		when:
		def ie = newIsExportableEntity()
		def clazz = Utils.isExportable(ie.class)
		
		then:
		clazz != null
	}
	
	def "test entity is exportable primitive"(){
		when:
		def clazz = Utils.isExportablePrimitive(Integer.class)
		
		then:
		clazz != null
	}
	
	def "test entity is not exportable"(){
		when:
		def ne = new IsNotExportableEntity()
		def clazz = Utils.isExportable(ne.class)
		
		then:
		clazz == null		
	}
	
	
	def "test for export entity"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(j(["en":"survey"]), period)
		def program = SurveyIntegrationTests.newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = SurveyIntegrationTests.newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		def file = entityExportService.getExportFile("file", SurveySimpleQuestion.class)
		def zipFile = Utils.getZipFile(file, "file")
		
		then:
		zipFile.exists() == true
		zipFile.length() > 0
	}
	
	def "test entity fields are exportable"(){
		when:
		def ie = newIsExportableEntity("code1", 1)
		List<Field> fields = entityExportService.getFieldHeaders(ie.class)				
		def entityData = entityExportService.getEntityData(ie, fields)
		
		then:
		entityData[0].equals("code1")
		entityData[1].equals("1")
		entityData[2].equals(Utils.formatDate(new Date()))
		entityData[3].equals("")
		
		when:
		ie.trans = new Translation(j(["en":"English", "fr":"French"]))
		entityData = entityExportService.getEntityData(ie, fields)
		
		then:
		entityData[0].equals("code1")
		entityData[1].equals("1")
		entityData[2].equals("30-05-2012")
		entityData[3].equals(ie.trans.toExportString())
	}	
	
	def "test entity fields that are exportable and not exportable"(){
		when:
		def te = newTestEntity("code")
		List<Field> fields = entityExportService.getFieldHeaders(te.class)
		def entityData = entityExportService.getEntityData(te, fields)
		
		then:
		entityData[0].equals("code")
		entityData[1].equals("")
		entityData[2].equals("")
		
		when:
		te.iee = newIsExportableEntity(code: "code1", num: 1)
		te.inee = new IsNotExportableEntity()
		entityData = entityExportService.getEntityData(te, fields)
		
		then:
		entityData[0].equals("code")
		entityData[1].equals("[~code1~]")
		entityData[2].equals(Utils.VALUE_NOT_EXPORTABLE)
	}
	
	def "test entity fields that are exportable lists and not exportable lists"(){
		when:
		def te = newTestEntities("code")					 
		List<Field> fields = entityExportService.getFieldHeaders(te.class)
		def entityData = entityExportService.getEntityData(te, fields)
		
		then:
		entityData[0].equals("code")
		entityData[1].equals("")
		entityData[2].equals("")
		
		when:
		te.listIee = [newIsExportableEntity(code: "code1", num: 1)] 
		te.listInee = [new IsNotExportableEntity(), new IsNotExportableEntity()]		
		entityData = entityExportService.getEntityData(te, fields)
		
		then:
		entityData[0].equals("code")
		entityData[1].equals("[[~code1~]]")
		entityData[2].equals(Utils.VALUE_NOT_EXPORTABLE)
		
		when:
		te.listIee = [newIsExportableEntity("code1", 1), newIsExportableEntity("code2", 2)]
		te.listInee = [new IsNotExportableEntity(), new IsNotExportableEntity()]
		entityData = entityExportService.getEntityData(te, fields)
		
		then:
		entityData[0].equals("code")
		entityData[1].equals("[[~code1~], [~code2~]]")
		entityData[2].equals(Utils.VALUE_NOT_EXPORTABLE)		
	}	
	
	
	def "test for valid export filename"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(j(["en":"survey"]), period)
		def program = SurveyIntegrationTests.newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = SurveyIntegrationTests.newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		def file = entityExportService.getExportFilename(SurveySimpleQuestion.class)
		
		then:
		file.startsWith("SurveySimpleQuestion_")
	}
	
	def "test for export multiple files in zip file"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(j(["en":"survey"]), period)
		def program = SurveyIntegrationTests.newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = SurveyIntegrationTests.newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		def entityClazz = [SurveyCheckboxQuestion.class, SurveySimpleQuestion.class, SurveyTableQuestion.class]
		
		when:
		List<String> filenames = new ArrayList<String>();
		List<File> csvFiles = new ArrayList<File>();
		for(Class clazz : entityClazz){
			String filename = entityExportService.getExportFilename(clazz);
			filenames.add(filename);
			csvFiles.add(entityExportService.getExportFile(filename, clazz));
		}
		String zipFilename = StringUtils.join(filenames, "_")
		def zipFile = Utils.getZipFile(csvFiles, zipFilename)
		
		then:
		zipFilename.startsWith("SurveyCheckboxQuestion__SurveySimpleQuestion__SurveyTableQuestion_")
		zipFile.exists() == true
		zipFile.length() > 0
	}
	
	def "test for entity header sort"(){
		setup:
		def entitySurveyQuestionFieldHeaders = entityExportService.getFieldHeaders(SurveyQuestion.class)
		def entityDashboardTargetFieldHeaders = entityExportService.getFieldHeaders(DashboardTarget.class)
		
		when:
		def surveyQuestionHeaders = entitySurveyQuestionFieldHeaders.collect { it.getName() }
		def dashboardTargetHeaders = entityDashboardTargetFieldHeaders.collect { it.getName() }
		
		then:
		surveyQuestionHeaders.equals(["code", "names", "order", "section", "typeCodeString", "descriptions"])
		dashboardTargetHeaders.equals(["code", "names", "order", "calculation", "program", "weight", "descriptions"])
	}
}
