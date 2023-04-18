package com.software.trentanove.ftvSpringRest.model;

import java.sql.Timestamp;

public class FtvData {
	
	private Timestamp date;
	private float ftv_voltage;
	private float ftv_current;
	private float ftv_power;
	private float ftv_frequency;
	private float ftv_energy;
	private float con_voltage;
	private float con_current;
	private float con_power;
	private float con_frequency;
	private float con_energy;
	
	public FtvData() {
		super();
	}
	
	public FtvData(Timestamp date) {
		super();
		this.date = date;
	}
	
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public float getFtv_voltage() {
		return ftv_voltage;
	}
	public void setFtv_voltage(float ftv_voltage) {
		this.ftv_voltage = ftv_voltage;
	}
	public float getFtv_current() {
		return ftv_current;
	}
	public void setFtv_current(float ftv_current) {
		this.ftv_current = ftv_current;
	}
	public float getFtv_power() {
		return ftv_power;
	}
	public void setFtv_power(float ftv_power) {
		this.ftv_power = ftv_power;
	}
	public float getFtv_frequency() {
		return ftv_frequency;
	}
	public void setFtv_frequency(float ftv_frequency) {
		this.ftv_frequency = ftv_frequency;
	}
	public float getFtv_energy() {
		return ftv_energy;
	}
	public void setFtv_energy(float ftv_energy) {
		this.ftv_energy = ftv_energy;
	}
	public float getCon_voltage() {
		return con_voltage;
	}
	public void setCon_voltage(float con_voltage) {
		this.con_voltage = con_voltage;
	}
	public float getCon_current() {
		return con_current;
	}
	public void setCon_current(float con_current) {
		this.con_current = con_current;
	}
	public float getCon_power() {
		return con_power;
	}
	public void setCon_power(float con_power) {
		this.con_power = con_power;
	}
	public float getCon_frequency() {
		return con_frequency;
	}
	public void setCon_frequency(float con_frequency) {
		this.con_frequency = con_frequency;
	}
	public float getCon_energy() {
		return con_energy;
	}
	public void setCon_energy(float con_energy) {
		this.con_energy = con_energy;
	}  

}
