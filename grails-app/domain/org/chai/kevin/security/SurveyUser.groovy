package org.chai.kevin.security

class SurveyUser extends User {

	// TODO assign several surveys to a user
	// instead of an organisation unit
	int entityId;

	static constraints = {
		entityId (nullable: false)	
	}
	
}
