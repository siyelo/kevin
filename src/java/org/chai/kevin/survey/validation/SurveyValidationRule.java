package org.chai.kevin.survey.validation;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.chai.kevin.survey.SurveyElement;

@Entity(name="SurveyValidationRule")
@Table(name="dhsst_survey_validation_rule")
public class SurveyValidationRule {

	private Long id;
	private SurveyElement surveyElement;
	private String expression;

	private SurveyValidationMessage validationMessage;
	private List<SurveyElement> dependencies;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity=SurveyElement.class, optional=false)
	@JoinColumn(nullable=false)
	public SurveyElement getSurveyElement() {
		return surveyElement;
	}
	public void setSurveyElement(SurveyElement surveyElement) {
		this.surveyElement = surveyElement;
	}
	
	@Basic(optional=false)
	@Column(nullable=false)
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@ManyToOne(targetEntity=SurveyValidationMessage.class, optional=false)
	@JoinColumn(nullable=false)
	public SurveyValidationMessage getValidationMessage() {
		return validationMessage;
	}
	public void setValidationMessage(SurveyValidationMessage validationMessage) {
		this.validationMessage = validationMessage;
	}
	
	@ManyToMany(targetEntity=SurveyElement.class)
	public List<SurveyElement> getDependencies() {
		return dependencies;
	}
	
	public void setDependencies(List<SurveyElement> dependencies) {
		this.dependencies = dependencies;
	}
	
}