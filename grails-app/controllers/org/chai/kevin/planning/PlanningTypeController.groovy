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
package org.chai.kevin.planning

import org.chai.kevin.AbstractEntityController
/**
 * @author Jean Kahigiso M.
 *
 */
class PlanningTypeController extends AbstractEntityController {
	
	def languageService
	
	def getEntity(def id) {
		return PlanningType.get(id)
	}

	def createEntity() {
		return new PlanningType()
	}

	def getLabel() {
		return 'planning.planningtype.label'
	}
	
	def getTemplate() {
		return "/planning/admin/createPlanningType"
	}

	def getModel(def entity) {
		def dataElements = []
		if (entity.formElement != null) dataElements << entity.formElement.dataElement
		def sections = []
		if (entity.formElement != null) sections.addAll entity.sections
		def headerPrefixes = []
		if (entity.formElement?.dataElement?.headerPrefixes != null) headerPrefixes.addAll entity.formElement.dataElement.headerPrefixes
		
		def valuePrefixes = []
		if (entity.formElement != null) valuePrefixes.addAll entity.formElement.dataElement.getValuePrefixes("")
		[
			sections: sections,
			headerPrefixes: headerPrefixes,
			planningType: entity,
			dataElements: dataElements,
			valuePrefixes: valuePrefixes
		]
	}

	def getEntityClass(){
		//TODO return PlanningType.class;
		return null;
	}
	
	def bindParams(def entity) {
		entity.properties = params

		// headers
		if (entity.formElement != null) {
			def headers = [:]
			bindTranslationMap('headerList', headers)
			entity.formElement.headers = headers
		}
		
		// section description
		def sectionDescriptions = [:]
		bindTranslationMap('sectionList', sectionDescriptions)
		entity.sectionDescriptions = sectionDescriptions
	}
	
	def list = {
		adaptParamsForList()
		
		Planning planning = Planning.get(params.int('planning.id'))
		if (planning == null) response.sendError(404)
		else {
			def planningTypes = PlanningType.createCriteria().list(params){eq('planning', planning)}
	
			render (view: '/planning/admin/list', model:[
				template:"planningTypeList",
				entities: planningTypes,
				entityCount: planningTypes.totalCount,
				code: getLabel(),
				entityClass: getEntityClass()
			])
		}
	}
	
}