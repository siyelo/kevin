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
package org.chai.kevin.dsr;
/**
 * @author Jean Kahigiso M.
 *
 */

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Translatable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity(name = "DsrTargetCategory")
@Table(name = "dhsst_dsr_target_category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DsrTargetCategory extends Translatable {

	private Integer id;
	private Integer order;
	private List<DsrTarget> targets = new ArrayList<DsrTarget>();

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Basic
	@Column(name="ordering")
	public Integer getOrder() {
		return order;
	}

	@OneToMany(targetEntity = DsrTarget.class, mappedBy = "category")
	public List<DsrTarget> getTargets() {
		return targets;
	}
	
	public List<DsrTarget> getTargetsForObjective(DsrObjective objective) {
		List<DsrTarget> result = new ArrayList<DsrTarget>();
		for (DsrTarget dsrTarget : getTargets()) {
			if (dsrTarget.getObjective().equals(objective)) result.add(dsrTarget);
		}
		return result;
	}

	public void setTargets(List<DsrTarget> targets) {
		this.targets = targets;
	}
	@Transient
	public void addTarget(DsrTarget target) {
		target.setCategory(this);
		targets.add(target);
	}
}