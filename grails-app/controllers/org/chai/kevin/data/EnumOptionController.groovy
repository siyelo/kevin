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
package org.chai.kevin.data


import org.chai.kevin.AbstractEntityController
import org.chai.kevin.LanguageService;

/**
 * @author Jean Kahigiso M.
 *
 */
class EnumOptionController extends AbstractEntityController {

	def languageService
	def enumService
	def sessionFactory
	
	def getEntity(def id){
		return EnumOption.get(id)
	}
	
	def createEntity(){  
		return new EnumOption();
	}
	
	def getLabel() {
		return 'enum.enumoption.label'
	}
	
	def getTemplate() {
		return "/entity/data/createEnumOption"
	}
	
	def getModel(def entity) {
		[option: entity]
	}
	
	def getEntityClass(){
		return EnumOption.class;
	}
	
	def saveEntity(def entity) {
		super.saveEntity(entity)
		
		// refresh cache
		entity.enume.addToEnumOptions(entity)
	}
	
	def deleteEntity(def entity) {
		entity.enume.removeFromEnumOptions(entity)
		entity.delete()
	}
	
	def bindParams(def entity) {
		entity.properties = params
	}
	
	def list = {
		adaptParamsForList();
		Enum enume = Enum.get(params.int('enume.id'));
		
		if (enume == null) {
			response.sendError(404)
		}
		else {
			def options = enume.enumOptions.sort({it.orders});
			options.sort({it.getOrders(languageService.currentLocale)})
			
			def max = Math.min(params['offset']+params['max'], options.size())
			
			render (view: '/entity/list', model:[
				entities: options.asList().subList(params['offset'], max),
				template: "data/enumOptionList",
				entityCount: options.size(),
				code: getLabel(),
				entityClass: getEntityClass()
			])
		}
	}
	
	def search = {
		adaptParamsForList();
		Enum enume = Enum.get(params.int('enume.id'));
		
		if (enume == null) {
			response.sendError(404)
		}
		else {
			def options = enumService.searchEnumOption(enume,params['q'],params);
			
			render (view: '/entity/list', model:[
				entities: options,
				template: "data/enumOptionList",
				entityCount: options.totalCount,
				entityClass: getEntityClass(),
				q:params['q'],
				code: getLabel()
			])
		}
	}
		
}
