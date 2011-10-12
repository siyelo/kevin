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

import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.AbstractEntityController
import org.chai.kevin.DataService
import org.chai.kevin.ValueService;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class ExpressionController extends AbstractEntityController {

	DataService dataService
	ValueService valueService
	
	def getEntity(def id) {
		return Expression.get(id)
	}
	
	def createEntity() {
		def expression = new Expression()
		expression.type = new Type()
		return expression
	}
	
	def getTemplate() {
		return "/entity/expression/createExpression";
	}
	
	def getModel(def entity) {
		return [expression: entity]
	}

	def validateEntity(def entity) {
		return entity.validate()
	}
	
	def saveEntity(def entity) {
		entity.setTimestamp(new Date());
		entity.save()
	}
	
	def deleteEntity(def entity) {
		// we check if there are calculations
		if (dataService.getCalculations(entity).isEmpty()) { 
			// we delete all the values
			valueService.deleteValues(entity)
			entity.delete()
		}
		else {
			flash.message = "expression.delete.hasvalues";
			flash.default = "Could not delete expression, it still has associated calculations";
		}
	}
	
	def bindParams(def entity) {
		entity.properties = params
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = params.descriptions
	}
	
	def search = {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		params.offset = params.offset ? params.int('offset'): 0
		
		List<Expression> expressions = dataService.searchData(Expression.class, params['q'], [], params);
		
		render (view: '/entity/list', model:[
			entities: expressions,
			template: "expression/expressionList",
			code: "expression.label",
			entityCount: dataService.countData(Expression.class, params['q'], []),
			search: true
		])
	}
	def list = {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		params.offset = params.offset ? params.int('offset'): 0
		
		List<Expression> expressions = Expression.list(params);
		
		render (view: '/entity/list' , model:[
			entities: expressions, 
			entityCount: Expression.count(),
			code: 'expression.label',
			template: 'expression/expressionList'
		])
	}
	
	def getDescription = {
		def expression = Expression.get(params.int('expression'))
		
		if (expression == null) {
			render(contentType:"text/json") {
				result = 'error'
			}
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/templates/expressionDescription', model: [expression: expression])
			}
		}
	}
	
}