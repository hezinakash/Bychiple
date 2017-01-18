package model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="routePoint")
public class RoutePoint implements Serializable
{
	@ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "routeObj")
	private Route routeObj;
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "routePointId")
	private int routePointId;
	
	@Column(name = "lat")
    private double lat;
	
	@Column(name = "lng")
    private double lng;
	
	private static final long serialVersionUID = 1L;
	
	public RoutePoint()	{}
	
	public RoutePoint(Route route, double lat, double lng)
	{
		this.routeObj = route;
		this.lat = lat;
		this.lng = lng;
	}
	
	public void setRoute(Route route)
    {
    	this.routeObj = route;
    	
    	if (!route.getRoutePointList().contains(this))
    	{
    		route.getRoutePointList().add(this);
    	}
    }
	
	public double getLat()
	{
		return lat;
	}
	
	public double getLng()
	{
		return lng;
	}
}
