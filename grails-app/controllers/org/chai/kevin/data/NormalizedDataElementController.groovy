package org.chai.kevin.data

/*
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

import org.chai.kevin.AbstractEntityController
import org.chai.kevin.Period
import org.chai.location.DataLocationType
import org.chai.kevin.planning.PlanningCost
import org.chai.kevin.reports.AbstractReportTarget;
import org.chai.kevin.value.Status
import org.chai.kevin.util.Utils

class NormalizedDataElementController extends AbstractEntityController {

	def dataService
	def valueService
	def locationService
	
	def getEntity(def id) {
		return NormalizedDataElement.get(id)
	}
	
	def createEntity() {
		def normalizedDataElement = new NormalizedDataElement()
		normalizedDataElement.type = new Type()
		return normalizedDataElement
	}

	def getLabel() {
		return "normalizeddataelement.label";
	}
		
	def getTemplate() {
		return "/entity/data/createNormalizedDataElement";
	}
	
	def getModel(def entity) {
		return [
			normalizedDataElement: entity,
			periods: Period.list([cache: true]),
			types: DataLocationType.list([cache: true])
		]
	}

	def getEntityClass(){
		return NormalizedDataElement.class;
	}
	
	def validateEntity(def entity) {
		boolean valid = entity.validate()
		if (params['typeBuilderError'] != null) {
			// if there has been an error binding type, typeBuilderError will hold the error message
			entity.errors.rejectValue('type', 'data.type.invalid', [params['typeBuilderError']] as Object[], 'Syntax error: [{0}]')
			valid = false
		}
		return valid
	}
	
	def saveEntity(def entity) {
		entity.lastValueChanged = new Date()
		entity.timestamp = new Date()
		entity.save(flush: true)
	}
	
	def deleteEntity(def entity) {
		// we check if there are associated date
		if (!dataService.getReferencingData(entity).isEmpty()) {
			flash.message = message(code: "normalizeddataelement.delete.hasreferencingdata", default: "Could not delete element, some other data still reference this element.")
		}
		// TODO check if there is associated planning
		else if (!PlanningCost.findAllByDataElement(entity).isEmpty()) {
			flash.message = message(code: "normalizeddataelement.delete.hasreferencingplanningcost", default: "Could not delete element, some other data still reference this element.")
		}
		else if (!AbstractReportTarget.findAllByData(entity).isEmpty()) {
			flash.message = message(code: "data.delete.hasreporttargets", default: "Could not delete element, some reports use this data element.")
		}
		else {
			valueService.deleteValues(entity, null, null)
			super.deleteEntity(entity)
		}
	}
	
	def bindParams(def entity) {
		entity.properties = params
		
		// bind expression map
		def expressionMap = [:]
		Period.list([cache: true]).each { period ->
			def periodMap = [:]
			DataLocationType.list([cache: true]).each { group ->
				def expression = params['expressionMap['+period.id+']['+group.code+']']
				periodMap[group.code] = expression==null?'':expression
			}
			// we bind the expression map last so everything is refreshed
			expressionMap[period.id+''] = periodMap
		}
		entity.expressionMap = expressionMap
		
		if (entity.id == null || !valueService.getNumberOfValues(entity)) {
			try {
				if (log.debugEnabled) log.debug("binding type: "+params['typeBuilderString'])
				
				// returns null if params['typeBuilderString'] is null	
				def type = Utils.buildType(params['typeBuilderString'])
				entity.type = type
			} catch (Exception e) {
				// we get here if params['typeBuilderString'] is garbage (syntax error)
				params['typeBuilderError'] = e.getMessage()
			}
			if (log.debugEnabled) log.debug("entity typeString: "+entity.typeString+", is null: "+(entity.typeString == null))
		}
	}
	
	def search = {
		adaptParamsForList()
		
		def normalizedDataElements = dataService.searchData(NormalizedDataElement.class, params['q'], [], params);
		
		render (view: '/entity/list', model:[
			entities: normalizedDataElements,
			entityCount: normalizedDataElements.totalCount,
			entityClass: getEntityClass(),
			template: "data/normalizedDataElementList",
			code: getLabel(),
			search: true
		])
	}
	
	def list = {
		adaptParamsForList()
		List<NormalizedDataElement> normalizedDataElements = NormalizedDataElement.list(params);
		
		render (view: '/entity/list' , model:[
			entities: normalizedDataElements, 
			entityCount: NormalizedDataElement.count(),
			template: 'data/normalizedDataElementList',
			code: getLabel(),
			entityClass: getEntityClass(),
			targetURI: getTargetURI()
		])
	}

}
