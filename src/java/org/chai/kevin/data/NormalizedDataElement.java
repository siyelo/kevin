package org.chai.kevin.data;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import org.chai.kevin.value.NormalizedDataElementValue;
import org.hisp.dhis.period.Period;

@Entity(name="NormalizedDataElement")
@Table(name="dhsst_normalized_data_element")
public class NormalizedDataElement extends DataElement<NormalizedDataElementValue> {

	// json text example : {"1":{"DH":"$1 + $2"}, "2":{"HC":"$1 + $2 + $3"}}
	private ExpressionMap expressionMap = new ExpressionMap();
	private Date calculated;
	
	@AttributeOverrides({
		@AttributeOverride(name="jsonText", column=@Column(name="expressionMap", nullable=false))
	})
	public ExpressionMap getExpressionMap() {
		return expressionMap;
	}
	
	public void setExpressionMap(ExpressionMap expressionMap) {
		this.expressionMap = expressionMap;
	}
	
	@Column(nullable=true)
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	public Date getCalculated() {
		return calculated;
	}
	
	public void setCalculated(Date calculated) {
		this.calculated = calculated;
	}
	
	@Transient
	public String getExpression(Period period, String groupUuid) {
		return expressionMap.get(period.getId()+"").get(groupUuid);
	}

	@Override
	@Transient
	public Class<NormalizedDataElementValue> getValueClass() {
		return NormalizedDataElementValue.class;
	}
	
}