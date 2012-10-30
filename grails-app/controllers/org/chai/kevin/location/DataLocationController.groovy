package org.chai.kevin.location;

import org.chai.kevin.AbstractEntityController;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;

class DataLocationController extends AbstractEntityController {

	def locationService
	
	def bindParams(def entity) {
		entity.properties = params
		
		if (params.names!=null) entity.names = params.names
	}

	def getModel(def entity) {
		def locations = []
		if (entity.location != null) locations << entity.location
		[location: entity, types: DataLocationType.list([cache: true]), locations: locations]
	}

	def getEntityClass(){
		return DataLocation.class;
	}
	
	def getEntity(def id) {
		return DataLocation.get(id);
	}

	def createEntity() {
		return new DataLocation();
	}

	def getTemplate() {
		return '/entity/location/createDataLocation'
	}

	def getLabel() {
		return 'datalocation.label';
	}

	def list = {
		adaptParamsForList()
		
		def location = Location.get(params.int('location'))
		def type = DataLocationType.get(params.int('type'))
		
		def locations = null
		if (location != null) locations = DataLocation.findAllByLocation(location, params)
		else if (type != null) locations = DataLocation.findAllByType(type, params)
		else locations = DataLocation.list(params);

		render (view: '/entity/list', model:[
			template:"location/dataLocationList",
			entities: locations,
			entityCount: DataLocation.count(),
			code: getLabel(),
			entityClass: getEntityClass()
		])
	}
	
	def search = {
		adaptParamsForList()
		
		List<DataLocation> locations = locationService.searchLocation(DataLocation.class, params['q'], params)
				
		render (view: '/entity/list', model:[
			template:"location/dataLocationList",
			entities: locations,
			entityCount: locationService.countLocation(DataLocation.class, params['q']),
			entityClass: getEntityClass(),
			code: getLabel()
		])
	}
	
}
