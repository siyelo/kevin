package org.chai.kevin.survey;

import java.util.Map.Entry;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Translation;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormElementService;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.form.FormValidationService;
import org.chai.kevin.form.FormElement.ElementCalculator;
import org.chai.kevin.form.FormValidationService.ValidatableLocator;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;

@Entity(name = "SurveyElement")
@Table(name = "dhsst_survey_element")
public class SurveyElement extends FormElement {

	private SurveyQuestion surveyQuestion;
	
	@ManyToOne(targetEntity=SurveyQuestion.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public SurveyQuestion getSurveyQuestion() {
		return surveyQuestion;
	}
	
	public void setSurveyQuestion(SurveyQuestion surveyQuestion) {
		this.surveyQuestion = surveyQuestion;
	}
	
	@Transient
	public Survey getSurvey() {
		return surveyQuestion.getSurvey();
	}

	@Transient
	public Set<String> getTypeApplicable(){
		return this.surveyQuestion.getTypeApplicable(this);
	}

	@Transient
	@Override
	public void deepCopy(FormElement copy, SurveyCloner cloner) {
		super.deepCopy(copy, cloner);
		((SurveyElement)copy).setSurveyQuestion(cloner.getQuestion(getSurveyQuestion()));
	}

	@Override
	public String toString() {
		return "SurveyElement [id=" + id + "]";
	}
	
	public static class SurveyElementCalculator extends ElementCalculator {

		private SurveyValueService surveyValueService;
		private List<SurveyEnteredQuestion> affectedQuestions;
		
		public SurveyElementCalculator(List<FormEnteredValue> affectedValues, List<SurveyEnteredQuestion> affectedQuestions, FormValidationService formValidationService, FormElementService formElementService, SurveyValueService surveyValueService, ValidatableLocator validatableLocator) {
			super(affectedValues, formValidationService, formElementService, validatableLocator);
			this.surveyValueService = surveyValueService;
			this.affectedQuestions = affectedQuestions;
		}
		
		public void addAffectedQuestion(SurveyEnteredQuestion question) {
			this.affectedQuestions.add(question);
		}
		
		public List<SurveyEnteredQuestion> getAffectedQuestions() {
			return affectedQuestions;
		}
		
		public SurveyValueService getSurveyValueService() {
			return surveyValueService;
		}
		
	}

}
