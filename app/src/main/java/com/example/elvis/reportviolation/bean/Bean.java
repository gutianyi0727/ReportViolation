package com.example.elvis.reportviolation.bean;

/**
 * Elvis Gu, May 2018
 * Bean class represents a windowClick
 *
 */
import java.io.Serializable;

public class Bean implements Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private String number;
	private double latitude;
	private double longitude;

	public Bean(int id, String number, double latitude,
                double longitude) {
		super();
		this.id = id;
		this.number = number;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}


	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


}
