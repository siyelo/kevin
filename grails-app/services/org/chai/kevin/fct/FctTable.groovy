package org.chai.kevin.fct;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.chai.location.CalculationLocation;
import org.chai.kevin.reports.ReportTable;
import org.chai.kevin.value.SumValue;
import org.chai.kevin.value.Value;

public class FctTable extends ReportTable {
	
	public FctTable(Map valueMap, List targetOptions) {
		super(valueMap, targetOptions);
	}
	
	public Double getTotalAverage(CalculationLocation topLevelLocation){
		Double totalAverage = 0d;
		for(FctTargetOption targetOption : indicators){
			if(getTableReportValue(topLevelLocation, targetOption) != null && !getTableReportValue(topLevelLocation, targetOption).getAverage().isNull()){
				Value average = getTableReportValue(topLevelLocation, targetOption).getAverage();
					totalAverage += average.getNumberValue().doubleValue();
			}
		}
		DecimalFormat frmt = new DecimalFormat("#.##");
		return Double.parseDouble(frmt.format(totalAverage));		
	}

}