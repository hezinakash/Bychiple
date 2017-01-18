package model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="theftPlace")
public class TheftPlace implements Serializable
{
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "theftPlaceId")
    private int theftPlaceId;
	
	@Column(name = "lat")
    private double lat;
	
	@Column(name = "lng")
    private double lng;
	
	@Column(name = "address")
    private String address;
	
	private static final long serialVersionUID = 1L;
	
	public TheftPlace()	{}
	
	public TheftPlace(double lat, double lng, String address)
	{
		this.lat = lat;
		this.lng = lng;
		this.address = address;
	}
}
