/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.makarov_s_a.daoproject.tests;

import com.makarov_s_a.daoproject.annotations.Column;
import com.makarov_s_a.daoproject.annotations.Id;
import com.makarov_s_a.daoproject.annotations.Table;
import java.sql.Date;

/**
 *
 * @author makarov_s_a
 */
@Table(name = "JAVABEAN")
public class JavaBean {
	@Id
	@Column(name = "ID1")
	private long firstCompositeId;
	@Id
	@Column(name = "ID2")
	private long secondCompositeId;
	@Column
	private String someStringField;
	private Date someDateField;

	public JavaBean() {
	}

	public JavaBean(JavaBean original) {
		this.firstCompositeId = original.firstCompositeId;
		this.secondCompositeId = original.secondCompositeId;
		this.someStringField = original.someStringField;
	}

	public JavaBean(long firstCompositeId, long secondCompositeId, String someStringField, Date someDateField) {
		this.firstCompositeId = firstCompositeId;
		this.secondCompositeId = secondCompositeId;
		this.someStringField = someStringField;
		this.someDateField = someDateField;
	}

	public long getFirstCompositeId1() {
		return firstCompositeId;
	}

	public void setFirstCompositeId1(long firstCompositeId) {
		this.firstCompositeId = firstCompositeId;
	}

	public long getFirstCompositeId2() {
		return secondCompositeId;
	}

	public void setFirstCompositeId2(long secondCompositeId) {
		this.secondCompositeId = secondCompositeId;
	}

	public String getSomeStringField() {
		return someStringField;
	}

	public void setSomeStringField(String someStringField) {
		this.someStringField = someStringField;
	}

	public Date getSomeDateField() {
		return someDateField;
	}

	public void setSomeDateField(Date someDateField) {
		this.someDateField = someDateField;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (!JavaBean.class.isAssignableFrom(object.getClass())) {
			return false;
		}
		final JavaBean other = (JavaBean) object;
		if (this.firstCompositeId == other.firstCompositeId && this.secondCompositeId == other.secondCompositeId) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = (int) (89 * hash + this.firstCompositeId);
		hash = (int) (89 * hash + this.secondCompositeId);
		return hash;
	}
}
