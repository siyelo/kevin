package org.chai.kevin.value;

import grails.plugin.springcache.annotations.CacheFlush;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Period;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocation;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

public class RefreshValueService {

	private final static Log log = LogFactory.getLog(RefreshValueService.class);
	
	private DataService dataService;
	private SessionFactory sessionFactory;
	private ExpressionService expressionService;
	private ValueService valueService;
	
	@Transactional(readOnly = false)
	public void refreshNormalizedDataElement(NormalizedDataElement normalizedDataElement) {
		List<NormalizedDataElement> dependencies = getOrderedDependencies(normalizedDataElement);
		for (NormalizedDataElement dependency : dependencies) {
			refreshNormalizedDataElementOnly(dependency);
		}
	}
	
	private void refreshNormalizedDataElementOnly(NormalizedDataElement normalizedDataElement) {
		if (log.isDebugEnabled()) log.debug("refreshNormalizedDataElement(normalizedDataElement="+normalizedDataElement+")");
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		valueService.deleteValues(normalizedDataElement, null, null);
		for (Iterator<Object[]> iterator = getCombinations(DataLocation.class); iterator.hasNext();) {
			Object[] row = (Object[]) iterator.next();
			DataLocation dataLocation = (DataLocation)row[0];
			Period period = (Period)row[1];
			NormalizedDataElementValue value = expressionService.calculateValue(normalizedDataElement, dataLocation, period);				
			valueService.save(value);
			sessionFactory.getCurrentSession().evict(value);
		}
		normalizedDataElement.setCalculated(new Date());
		dataService.save(normalizedDataElement);
	}
	
	private void refreshCalculationInTransaction(Calculation<?> calculation) {
		refreshCalculation(calculation);
	}
	
	public void refreshCalculation(Calculation<?> calculation) {
		if (log.isDebugEnabled()) log.debug("refreshCalculation(calculation="+calculation+")");
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		valueService.deleteValues(calculation, null, null);
		for (Iterator<Object[]> iterator = getCombinations(CalculationLocation.class); iterator.hasNext();) {
			Object[] row = (Object[]) iterator.next();
			CalculationLocation location = (CalculationLocation)row[0];
			Period period = (Period)row[1];
			refreshCalculation(calculation, location, period);
		}
		calculation.setCalculated(new Date());
		dataService.save(calculation);
	}

	@CacheFlush(caches={"dsrCache", "dashboardCache", "fctCache"})
	public void flushCaches() { }
	
	@Transactional(readOnly = true)
	public void refreshNormalizedDataElements() {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		// TODO get only those who need to be refreshed
		List<NormalizedDataElement> normalizedDataElements = sessionFactory.getCurrentSession().createCriteria(NormalizedDataElement.class).list();
 		
		while (!normalizedDataElements.isEmpty()) {
			NormalizedDataElement normalizedDataElement = normalizedDataElements.remove(0);
			if (normalizedDataElement.getCalculated() == null || normalizedDataElement.needsRefresh()) {
				List<NormalizedDataElement> dependencies = getOrderedDependencies(normalizedDataElement);
				for (NormalizedDataElement dependentElement : dependencies) {
					refreshNormalizedDataElementOnly(dependentElement);
					sessionFactory.getCurrentSession().evict(dependentElement);
					
					// we remove the element from the original list since it already has been updated
					normalizedDataElements.remove(dependentElement);
				}
			}
			normalizedDataElements.remove(normalizedDataElement);
			sessionFactory.getCurrentSession().evict(normalizedDataElement);
		}
	}
	
	private List<NormalizedDataElement> getOrderedDependencies(NormalizedDataElement normalizedDataElement) {
		List<NormalizedDataElement> elements = new ArrayList<NormalizedDataElement>();
		elements.add(normalizedDataElement);
		for (String expression : normalizedDataElement.getExpressions()) {
			Map<String, NormalizedDataElement> dependencies = expressionService.getDataInExpression(expression, NormalizedDataElement.class);
			for (NormalizedDataElement dependency : dependencies.values()) {
				if (dependency != null && !elements.contains(dependency)) elements.add(dependency);
			}
		}
		Collections.reverse(elements);
		return elements;
	}

	@Transactional(readOnly = true)
	public void refreshCalculations() {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		// TODO get only those who need to be refreshed
		List<Calculation<?>> calculations = sessionFactory.getCurrentSession().createCriteria(Calculation.class).list();
		
		for (Calculation<?> calculation : calculations) {
			if (calculation.getCalculated() == null || calculation.needsRefresh()) {
				refreshCalculationInTransaction(calculation);
				sessionFactory.getCurrentSession().evict(calculation);
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void refreshCalculation(Calculation<?> calculation, CalculationLocation location, Period period) {
		valueService.deleteValues(calculation, location, period);
		for (CalculationPartialValue partialValue : expressionService.calculatePartialValues(calculation, location, period)) {
			valueService.save(partialValue);
			sessionFactory.getCurrentSession().evict(partialValue);
		}
	}
	
	@Transactional(readOnly = false)
	public void refreshNormalizedDataElement(NormalizedDataElement dataElement, DataLocation dataLocation, Period period) {
		valueService.deleteValues(dataElement, dataLocation, period);
		List<NormalizedDataElement> dependencies = getOrderedDependencies(dataElement);
		for (NormalizedDataElement dependency : dependencies) {
			valueService.save(expressionService.calculateValue(dependency, dataLocation, period));	
		}
	}

	private <T extends CalculationLocation> Iterator<Object[]> getCombinations(Class<T> clazz) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select location, period " +
				"from "+clazz.getSimpleName()+" location, Period period"
		).setCacheable(true).setReadOnly(true);
		return query.iterate();
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
	
	
}
