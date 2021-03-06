package org.chai.kevin.dsr;

import grails.plugin.springcache.annotations.Cacheable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Period;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.dsr.DsrTable;
import org.chai.kevin.reports.ReportService.ReportType;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ValueService;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.springframework.transaction.annotation.Transactional;

public class DsrService {
	private static final Log log = LogFactory.getLog(DsrService.class);
	
	private ReportService reportService;
	private ValueService valueService;
	private Set<String> locationSkipLevels;
	
	@Cacheable("dsrCache")
	@Transactional(readOnly = true)
	public DsrTable getDsrTable(Location location, Period period, Set<DataLocationType> types, DsrTargetCategory category, ReportType reportType) {
		if (log.isDebugEnabled())  log.debug("getDsrTable(period="+period+",location="+location+",types="+types+",category="+category+",reportType="+reportType+")");

		Set<LocationLevel> skips = reportService.getSkipReportLevels(locationSkipLevels);
		List<CalculationLocation> calculationLocations = new ArrayList<CalculationLocation>();		
		switch(reportType){
			case MAP:
				calculationLocations.addAll(location.getChildrenEntitiesWithDataLocations(skips, types));
				calculationLocations.addAll(location.getDataLocations(skips, types));
				break;
			case TABLE:
			default:
				calculationLocations.addAll(location.collectTreeWithDataLocations(skips, types));
				calculationLocations.addAll(location.collectDataLocations(types));
		}
		
		List<DsrTarget> targets = category.getAllTargets();
		Map<CalculationLocation, Map<DsrTarget, DataValue>> valueMap = new HashMap<CalculationLocation, Map<DsrTarget, DataValue>>();
		for (DsrTarget target : targets) {
			for(CalculationLocation calculationLocation : calculationLocations){
				if(!valueMap.containsKey(calculationLocation))
					valueMap.put(calculationLocation, new HashMap<DsrTarget, DataValue>());	
				if (target.getData() instanceof Calculation) {
					valueMap.get(calculationLocation).put(target, getDsrValue(target, (Calculation)target.getData(), calculationLocation, period, types));
				}	
				else if (target.getData() instanceof DataElement && calculationLocation instanceof DataLocation) {
					valueMap.get(calculationLocation).put(target, getDsrValue((DataElement)target.getData(), (DataLocation) calculationLocation, period));	
				}
			}
		}
			
		DsrTable dsrTable = new DsrTable(valueMap, targets);
		if (log.isDebugEnabled()) log.debug("getDsrTable(...)="+dsrTable);
		return dsrTable;
	}

	private DataValue getDsrValue(DataElement dataElement, DataLocation dataLocation, Period period){
		return valueService.getDataElementValue(dataElement, dataLocation, period);
	}
	
	private DataValue getDsrValue(DsrTarget target, Calculation calculation, CalculationLocation location, Period period, Set<DataLocationType> types) {
		return (DataValue) valueService.getCalculationValue(calculation, location, period, types);
	}
	
	public List<DsrTargetCategory> getDsrCategoriesWithTargets(ReportProgram program){
		List<DsrTargetCategory> result = new ArrayList<DsrTargetCategory>();
		List<DsrTargetCategory> categories = program.getReportTargets(DsrTargetCategory.class);
		for(DsrTargetCategory category : categories) {
			if (!category.getAllTargets().isEmpty()) result.add(category);
		}	
		return result;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}

	public void setLocationSkipLevels(Set<String> locationSkipLevels) {
		this.locationSkipLevels = locationSkipLevels;
	}
	
	public Set<LocationLevel> getSkipLocationLevels(){
		return reportService.getSkipReportLevels(locationSkipLevels);
	}
}
