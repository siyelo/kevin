package org.chai.kevin

import grails.plugin.spock.IntegrationSpec;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;

class OrganisationServiceSpec extends IntegrationSpec {

	def organisationService;
	
	def setup() {
		Initializer.createDummyStructure()
	}
	
	def "get organisations of level"() {
		expect:
		organisationService.getOrganisationsOfLevel(level).containsAll (getOrganisations(expectedOrganisations))
		
		where:
		level	| expectedOrganisations
		1		| ["Rwanda"]
		2		| ["North"]
		3		| ["Burera"]
		4		| ["Kivuye HC", "Butaro DH"]
	}
	
	def "get organisation tree until level"() {
		
		when:
		def organisationTree = organisationService.getOrganisationTreeUntilLevel(level)
		
		then:
		def organisation = getOrganisation("Rwanda")
		organisationTree == organisation
		assertIsLoaded(organisationTree, level)
		
		where:
		level << [1, 2, 3 ,4]
	}
	
	def "get children level for level"() {
		when:
		def organisationUnitLevel = OrganisationUnitLevel.findByLevel(level);
		def children = organisationService.getChildren(organisationUnitLevel);
		
		then:
		children.containsAll getOrganisationUnitLevels(expectedLevels);
		getOrganisationUnitLevels(expectedLevels).containsAll children
		
		where:
		level	| expectedLevels
		1		| [2, 3, 4]
		2		| [3, 4]
		3		| [4]
		4		| []
	}
	
	def "get children of level for organisation"() {
		when:
		def organisation = getOrganisation(organisationName)
		def organisationUnitLevel = OrganisationUnitLevel.findByLevel(level)
		def organisations = organisationService.getChildrenOfLevel(organisation, organisationUnitLevel)
		
		then:
		organisations.containsAll getOrganisations(expectedOrganisations)
		getOrganisations(expectedOrganisations).containsAll organisations
		
		where:
		organisationName	| level	| expectedOrganisations
		"Rwanda"			| 2		| ["North", "Kigali City", "West", "East", "South"]
		"Rwanda"			| 3		| ["Burera", "Nyarugenge", "Gasabo", "Kicukiro"]
		"Rwanda"			| 4		| ["Butaro DH", "Kivuye HC"]
		
	}
	
	def "get level for organisation"() {
		when:
		def organisation = getOrganisation(organisationName)
		def level = organisationService.getLevel(organisation)
		
		then:
		level == OrganisationUnitLevel.findByLevel(expectedLevel)
		organisation.getLevel() == OrganisationUnitLevel.findByLevel(expectedLevel)
		
		where:
		organisationName	| expectedLevel
		"Rwanda"			| 1
		"North"				| 2
		"Burera"			| 3
		"Butaro DH"			| 4
	}
	
	def assertIsLoaded(def organisation, def level) {
		def success = true;
		organisation.children.each { 
			if (!assertIsLoaded(it, level)) success = false
		}
		if (getLevel(organisation) == level) {
			if (organisation.children != null) success = false
		}
		else {
			if (organisation.children == null) success = false
		}
		return success;
	}	
	def organisationUnitService;
	def getLevel(def organisation) {
		return organisationUnitService.getLevelOfOrganisationUnit(organisation.organisationUnit)
	}
	
	static def getOrganisationUnitLevels(def levels) {
		def result = []
		for (def level : levels) {
			result.add OrganisationUnitLevel.findByLevel(new Integer(level).intValue())
		}
		return result;
	}
	
	static def getOrganisation(def name) {
		return new Organisation(OrganisationUnit.findByName(name))
	}
	
	static def getOrganisations(def names) {
		def result = []
		for (String name : names) {
			result.add(getOrganisation(name))
		}
		return result
	}
	
}
