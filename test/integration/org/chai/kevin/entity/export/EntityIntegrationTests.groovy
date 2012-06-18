package org.chai.kevin.entity.export;

import java.util.Date;
import java.util.List;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Translation;

import java.lang.reflect.Field
import org.chai.kevin.dashboard.DashboardTarget
import org.chai.kevin.data.Type
import org.chai.kevin.form.FormEnteredValue
import org.chai.kevin.location.DataLocation
import org.chai.kevin.survey.SurveyElement
import org.chai.kevin.survey.SurveyIntegrationTests
import org.chai.kevin.survey.SurveyQuestion
import org.chai.kevin.survey.SurveyCheckboxQuestion
import org.chai.kevin.survey.SurveySimpleQuestion
import org.chai.kevin.survey.SurveyTableQuestion
import org.chai.kevin.util.Utils

public class EntityIntegrationTests extends IntegrationTests {

	def entityExportService
	
	//entity export test classes	
	
	public class IsExportableEntity implements Exportable {
		
		public String code;
		public Integer num;
		public Date dat = new Date();
		public Translation trans = new Translation();
		
		public IsExportableEntity() {
			this.code = "";
			this.num = 0;
		}
		
		public IsExportableEntity(String code, Integer num, Date dat) {
			this.code = code;
			this.num = num;
			this.dat = dat;
		}
		
		public String toExportString() {
			return "[" + Utils.formatExportCode(code) + "]";
		}
	
		public Object fromExportString(Object value){
			return (IsExportableEntity) value;
		}
	}	
	
	public class IsNotExportableEntity {
		IsNotExportableEntity() { }
	}	
	
	public class TestEntity implements Exportable, Importable {
		
		public String code;
		public IsExportableEntity iee = new IsExportableEntity();
		public IsNotExportableEntity inee = new IsNotExportableEntity();
		
		public TestEntity(String code) {
			this.code = code;
		}
				
		public String toExportString() {
			return "[" + Utils.formatExportCode(code) + "]";
		}
		
		public Object fromExportString(Object value){
			return (TestEntity) value;
		}
	}
	
	
	public class TestEntities implements Exportable, Importable {
		
		public String code;
		public List<IsExportableEntity> listIee = new ArrayList<IsExportableEntity>();
		public List<IsNotExportableEntity> listInee = new ArrayList<IsNotExportableEntity>();
		
		public TestEntities(String code) {
			this.code = code;
		}
			
		public String toExportString() {
			return "[" + Utils.formatExportCode(code) + "]";
		}
	
		public Object fromExportString(Object value){
			return (TestEntities) value;
		}
	}
	
	def static newIsExportableEntity() {
		return new IsExportableEntity(code: "code", num: 0).save(failOnError: true);
	}
	
	def static newIsExportableEntity(def code, def num) {
		return new IsExportableEntity(code: code, num: num).save(failOnError: true);
	}
	
	def static newTestEntity(def code){
		return new TestEntity(code: code).save(failOnError: true);
	}
	
	def static newTestEntities(def code){
		return new TestEntities(code: code).save(failOnError: true);
	}
}