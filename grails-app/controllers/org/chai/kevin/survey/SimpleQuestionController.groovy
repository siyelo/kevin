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

import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.LanguageService;
import org.chai.kevin.Translation;
import org.chai.kevin.util.Utils
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.location.DataEntityType;
import org.apache.commons.lang.math.NumberUtils;

/**
 * @author Jean Kahigiso M.
 *
 */
class SimpleQuestionController extends AbstractEntityController {

	def languageService
	def locationService
	def surveyService
	
	def getEntity(def id) {
		return SurveySimpleQuestion.get(id)
	}
	
	def createEntity() {
		def entity = new SurveySimpleQuestion();
		//FIXME find a better to do this
		if (!params['sectionId.id']) entity.section = SurveySection.get(params.sectionId)
		return entity
	}

	def getLabel() {
		return 'survey.simplequestion.label';
	}
	
	def getTemplate() {
		return "/survey/admin/createSimpleQuestion"
	}

	def getModel(def entity) {
		[
			question: entity,
			types: DataEntityType.list(),
			sections: (entity.section)!=null?entity.survey.sections:null,
			headerPrefixes: entity.surveyElement!=null?entity.surveyElement.dataElement.headerPrefixes:null
		]
	}

	def bindParams(def entity) {
		entity.properties = params
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = params.descriptions
		
		// headers
		params.list('headerList').each { prefix ->
			Translation translation = new Translation()
			languageService.availableLanguages.each { language -> 
				translation[language] = params['headerList['+prefix+'].'+language]
			}
			entity.surveyElement.headers.put(prefix, translation)	
		}
				
		if (entity.surveyElement != null) entity.surveyElement.surveyQuestion = entity
	}
	
	def getDescription = {
		def question = SurveySimpleQuestion.get(params.int('question'))
		
		if (question == null) {
			render(contentType:"text/json") {
				result = 'error'
			}
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/survey/admin/questionDescription', model: [question: question])
			}
		}
	}

}
