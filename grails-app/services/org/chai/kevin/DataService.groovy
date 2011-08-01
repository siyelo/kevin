package org.chai.kevin

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

import org.apache.commons.lang.StringUtils;
import org.chai.kevin.data.Constant;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;

class DataService {

    static transactional = true

	def localeService
	def sessionFactory
	
	Data getData(Long id) {
		return sessionFactory.currentSession.get(Data.class, id)
	}
	
//	DataElement getDataElement(Long id) {
//		if (log.isDebugEnabled()) log.debug("getDataElement(id="+id+")")
//		return DataElement.get(id)
//	}
	
//	Constant getConstant(Long id) {
//		return Constant.get(id)
//	}
	
	def searchConstants(String text) {
		def constants = Constant.list()
		StringUtils.split(text).each { chunk ->
			constants.retainAll { element ->
				DataService.matches(chunk, element.id+"") ||
				DataService.matches(chunk, element.names[localeService.getCurrentLanguage()]) ||
				DataService.matches(chunk, element.code) 
			}
		}
		return constants.sort {it.names[localeService.getCurrentLanguage()]}
	}
	
    def searchDataElements(String text) {
		def dataElements = DataElement.list();
		StringUtils.split(text).each { chunk ->
			dataElements.retainAll { element ->
				DataService.matches(chunk, element.id+"") ||
				DataService.matches(chunk, element.names[localeService.getCurrentLanguage()]) ||
				DataService.matches(chunk, element.code) ||
				DataService.matches(chunk, element.info)
			}
		}
		return dataElements.sort {it.names[localeService.getCurrentLanguage()]}
    }
	
	private static boolean matches(String text, String value) {
		if (value == null) return false;
		return value.matches("(?i).*"+text+".*");
	}
	
}
