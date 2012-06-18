package org.chai.kevin.entity.export

import java.lang.reflect.Field
import org.chai.kevin.IntegrationTests
import org.chai.kevin.Translation
import org.chai.kevin.dashboard.DashboardTarget
import org.chai.kevin.data.Enum
import org.chai.kevin.data.EnumOption
import org.chai.kevin.data.Type;
import org.chai.kevin.entity.export.EntityHeaderSorter
import org.chai.kevin.entity.export.EntityIntegrationTests.IsExportableEntity
import org.chai.kevin.entity.export.EntityIntegrationTests.IsNotExportableEntity
import org.chai.kevin.entity.export.EntityIntegrationTests.TestEntities
import org.chai.kevin.entity.export.EntityIntegrationTests.TestEntity
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.importer.ImporterErrorManager
import org.chai.kevin.importer.NormalizedDataImporter
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.survey.SurveyQuestion
import org.chai.kevin.survey.SurveyElement
import org.chai.kevin.survey.SurveyIntegrationTests
import org.chai.kevin.util.Utils;

class EntityImportServiceSpec extends EntityIntegrationTests {

	def entityImportService		
	
	def "test entity is importable"(){
		when:
		def entity = new TestEntity("code")
		def clazz = Utils.isImportable(entity.class)
		
		then:
		clazz != null
	}
	
	def "test entity is importable primitive"(){
		when:
		def clazz = Utils.isImportablePrimitive(Integer.class)
		
		then:
		clazz != null
	}
	
	def "test entity is not importable"(){
		when:
		def entity = new IsExportableEntity()
		def clazz = Utils.isImportable(entity.class)
		
		then:
		clazz == null
	}		
	
	def "test for import entity headers invalid"(){
		when:
		def csvString =
		"names,enumOptions,descriptions\n" +
		"ENUM1,\"{\"\"en\"\":\"\"Enum 1\"\"}\"," + "\"[[~1~, value1], [~2~, value2]]\",{}\n";
		
		def manager = new ImporterErrorManager();
		manager.setNumberOfSavedRows(0)
		manager.setNumberOfUnsavedRows(0)
		manager.setNumberOfRowsSavedWithError(0)
	
		entityImportService.importEntityData(new StringReader(csvString), Enum.class, manager)
			
		then:
		Enum.count() == 0
		manager.errors.size() == 1
		manager.errors[0].messageCode == "Import entity headers are invalid."
	}

	def "test for import new enum"(){
		when:
		def csvString =
			"code,names,enumOptions,descriptions\n" +
			"ENUM1,\"{\"\"en\"\":\"\"Enum 1\"\"}\",,\"{\"\"en\"\":\"\"Enum 1\"\"}\"\n";
		
		def manager = new ImporterErrorManager();
		manager.setNumberOfSavedRows(0)
		manager.setNumberOfUnsavedRows(0)
		manager.setNumberOfRowsSavedWithError(0)
	
		entityImportService.importEntityData(new StringReader(csvString), Enum.class, manager)
			
		then:
		Enum.count() == 1
		Enum.list()[0].code == "ENUM1"
		Enum.list()[0].names != null
		Enum.list()[0].enumOptions.size() == 0
		Enum.list()[0].descriptions != null
		manager.errors.size() == 0
	}
		
	def "test for import enum and update descriptions field"(){
		when:
		def enume = new Enum(names:j(["en":"Enum 1"]), descriptions:j([:]), code:"ENUM1");
		def enumOption1 = new EnumOption(code: "1", names:j(["en":"Value 1"]), value:"value1", enume: enume, order: o(["en":1,"fr":2]));
		def enumOption2 = new EnumOption(code: "2", names:j(["en":"Value 2"]), value:"value2", enume: enume, order: o(["en":2,"fr":1]));
		
		then:
		Enum.list()[0].descriptions == null
		
		when:
		def csvString =
			"code,names,enumOptions,descriptions\n" +
			"ENUM1,\"{\"\"en\"\":\"\"Enum 1\"\"}\"," + "\"[[~1~, value1], [~2~, value2]]\",\"{\"\"en\"\":\"\"Enum 1\"\"}\"\n";
		
		def manager = new ImporterErrorManager();
		manager.setNumberOfSavedRows(0)
		manager.setNumberOfUnsavedRows(0)
		manager.setNumberOfRowsSavedWithError(0)
	
		entityImportService.importEntityData(new StringReader(csvString), Enum.class, manager)
			
		then:
		Enum.count() == 1
		Enum.list()[0].code == "ENUM1"
		Enum.list()[0].descriptions != null
		manager.errors.size() == 0
	}
	
	def "test for import duplicate enum"(){
		when:
		def csvString =
			"code,names,enumOptions,descriptions\n" +
			"ENUM1,\"{\"\"en\"\":\"\"Enum 1\"\"}\",,{}\n" +
			"ENUM1,\"{\"\"en\"\":\"\"Enum 1\"\"}\",,{}\n";
		
		def manager = new ImporterErrorManager();
		manager.setNumberOfSavedRows(0)
		manager.setNumberOfUnsavedRows(0)
		manager.setNumberOfRowsSavedWithError(0)
	
		entityImportService.importEntityData(new StringReader(csvString), Enum.class, manager)
			
		then:
		Enum.count() == 1
		Enum.list()[0].code == "ENUM1"
		Enum.list()[0].enumOptions.size() == 0
		
		manager.errors.size() == 1
		manager.errors[0].header == "code"
		manager.errors[0].messageCode == "Import entity code ENUM1 is a duplicate."
		
		manager.getNumberOfSavedRows() == 1
		manager.getNumberOfUnsavedRows() == 1
	}
	
	def "test for import duplicate enum options"(){
		when:
		def enume = new Enum(names:j(["en":"Enum 1"]), descriptions:j([:]), code:"ENUM1");
		def enumOption1 = new EnumOption(code: "1", names:j(["en":"Value 1"]), value:"value1", enume: enume, order: o(["en":1,"fr":2]));
		def enumOption2 = new EnumOption(code: "2", names:j(["en":"Value 2"]), value:"value2", enume: enume, order: o(["en":2,"fr":1]));
		
		def csvString =
			"code,names,enumOptions,descriptions\n" +
			"ENUM1,\"{\"\"en\"\":\"\"Enum 1\"\"}\"," + "\"[[~1~, value1], [~2~, value2]]\",{}\n" +
			"ENUM2,\"{\"\"en\"\":\"\"Enum 2\"\"}\"," + "\"[[~1~, value1], [~2~, value2]]\",{}\n";
		
		def manager = new ImporterErrorManager();
		manager.setNumberOfSavedRows(0)
		manager.setNumberOfUnsavedRows(0)
		manager.setNumberOfRowsSavedWithError(0)
	
		entityImportService.importEntityData(new StringReader(csvString), Enum.class, manager)
		
		//TODO verify what happens when this happens!
		then:
		Enum.count() == 2
		Enum.list()[0].code == "ENUM1"
		Enum.list()[0].enumOptions.size() == 0
		Enum.list()[2].code == "ENUM2"
		Enum.list()[1].enumOptions.size() == 2
	}
	
	def "test for import enum with enum options that don't exist"(){
		when:
		def csvString =
		"code,names,enumOptions,descriptions\n" +
		"ENUM1,\"{\"\"en\"\":\"\"Enum 1\"\"}\"," + "\"[[~1~, value1]]\",{}\n";
		
		def manager = new ImporterErrorManager();
		manager.setNumberOfSavedRows(0)
		manager.setNumberOfUnsavedRows(0)
		manager.setNumberOfRowsSavedWithError(0)
	
		entityImportService.importEntityData(new StringReader(csvString), Enum.class, manager)
			
		then:
		Enum.count() == 0
		manager.errors.size() == 1
		manager.errors[0].messageCode == "Import entity org.chai.kevin.EnumOption code 1 is invalid."
	}
	
	def "test for flexible code column format"(){
		when:
		def enume = new Enum(names:j(["en":"Enum 1"]), descriptions:j([:]), code:"ENUM1");
		def enumOption1 = new EnumOption(code: "1", names:j(["en":"Value 1"]), value:"value1", enume: enume, order: o(["en":1,"fr":2]));
		def enumOption2 = new EnumOption(code: "2", names:j(["en":"Value 2"]), value:"value2", enume: enume, order: o(["en":2,"fr":1]));
		
		def csvString =
			"code,names,enumOptions,descriptions\n" +
			"ENUM1,\"{\"\"en\"\":\"\"Enum 1\"\"}\"," +
					"\"[[~1~, blah, blahblah], [~2~, blah, blahblah]]\",{}\n";
		
		def manager = new ImporterErrorManager();
		manager.setNumberOfSavedRows(0)
		manager.setNumberOfUnsavedRows(0)
		manager.setNumberOfRowsSavedWithError(0)
	
		entityImportService.importEntityData(new StringReader(csvString), Enum.class, manager)
			
		then:
		Enum.count() == 1
		Enum.list()[0].code == "ENUM1"
		Enum.list()[0].enumOptions.size() == 2
		manager.errors.size() == 0
	}

	def "test import new enum options"(){
		when:
		def enume = new Enum(names:j(["en":"Enum 1"]), descriptions:j([:]), code:"ENUM1");
		def csvString =
		"code,names,order,value,inactive,enume\n" +
		"ENUM1:value1,\"{\"\"en\"\":\"\"Value 1\"\"}\",\"{\"\"en\"\":1,\"\"fr\"\":2}\",value1,FALSE,\"[~1~, ENUM1]\"" +
		"ENUM1:value2,\"{\"\"en\"\":\"\"Value 2\"\"}\",\"{\"\"en\"\":1,\"\"fr\"\":2}\",value2,FALSE,\"[~1~, ENUM1]\"";
		
		def manager = new ImporterErrorManager();
		manager.setNumberOfSavedRows(0)
		manager.setNumberOfUnsavedRows(0)
		manager.setNumberOfRowsSavedWithError(0)
	
		entityImportService.importEntityData(new StringReader(csvString), Enum.class, manager)
		
		then:
		EnumOption.count() == 2
		EnumOption.list()[0].code == "ENUM1;value1"
		EnumOption.list()[1].code == "ENUM1:value2"
		manager.errors.size() == 0
	}
	
	def "test for import enum options with enum that doesn't exist"(){
		when:
		def csvString =
		"code,names,order,value,inactive,enume\n" +
		"ENUM1:value1,\"{\"\"en\"\":\"\"Value 1\"\"}\",\"{\"\"en\"\":1,\"\"fr\"\":2}\",value1,FALSE,\"[~1~, ENUM1]\"" +
		"ENUM1:value2,\"{\"\"en\"\":\"\"Value 2\"\"}\",\"{\"\"en\"\":1,\"\"fr\"\":2}\",value2,FALSE,\"[~1~, ENUM1]\"";
		
		def manager = new ImporterErrorManager();
		manager.setNumberOfSavedRows(0)
		manager.setNumberOfUnsavedRows(0)
		manager.setNumberOfRowsSavedWithError(0)
	
		entityImportService.importEntityData(new StringReader(csvString), Enum.class, manager)
			
		then:
		Enum.count() == 0
		manager.errors.size() == 1
		manager.errors[0].messageCode == "Import entity org.chai.kevin.Enum code ENUM1 is invalid."
	}
	
}
