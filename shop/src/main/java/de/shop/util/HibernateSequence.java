package de.shop.util;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the hibernate_sequence database table.
 * 
 */
@Entity
@Table(name = "hibernate_sequence")
public class HibernateSequence implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "next_val")
	private String nextVal;

	public HibernateSequence() {
	}

	public String getNextVal() {
		return this.nextVal;
	}

	public void setNextVal(String nextVal) {
		this.nextVal = nextVal;
	}

}