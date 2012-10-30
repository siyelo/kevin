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
package org.chai.kevin.survey

import org.chai.kevin.AbstractEntityController
import org.chai.kevin.Period;
import org.chai.kevin.PeriodSorter
import org.codehaus.groovy.grails.commons.ConfigurationHolder
/**
 * @author Jean Kahigiso M.
 *
 */
class SurveyController extends AbstractEntityController {
	
	def surveyCopyService
	
	def getEntity(def id) {
		return Survey.get(id)
	}

	def createEntity() {
		return new Survey()
	}

	def getLabel() {
		return 'survey.label'
	}
	
	def getTemplate() {
		return "/survey/admin/createSurvey"
	}
	
	def saveEntity(def entity) {
		if (entity.active) {
			// we reset all other planning
			Survey.list().each {
				if (!it.equals(entity)) {
					it.active = false
					it.save()
				}
			}
		}
		super.saveEntity(entity)
	}

	def getModel(def entity) {
		List<Period> periods = Period.list([cache: true])
		if(periods.size()>0) Collections.sort(periods,new PeriodSorter());
		[
			survey: entity,
			periods: periods
		]
	}

	def getEntityClass(){
		return Survey.class;
	}
	
	def bindParams(def entity) {
		entity.properties = params
	}

	def list = {
		adaptParamsForList()
		
		List<Survey> surveys = Survey.list(params);
		if(surveys.size()>0) Collections.sort(surveys,new SurveySorter())

		render (view: '/entity/list', model:[
			template:"survey/surveyList",
			entities: surveys,
			entityCount: Survey.count(),
			code: getLabel(),
			entityClass: getEntityClass()
		])
	}
	
	def copy = {
		def survey = getEntity(params.int('survey'))
		def clone = surveyCopyService.copySurvey(survey)
		
		redirect (controller: 'survey', action: 'list')	
	}

}