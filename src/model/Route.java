package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name="route")
public class Route implements Serializable
{
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "routeId")
    private int routeId;
	
	@Column(name = "chipId")
    private String chipId;
	
	@Column(name = "routeName")
    private String routeName;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "startDate")
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "endDate")
	private Date endDate;
	
	@Column(name = "routeDistance")
	private Double routeDistance;
	
	@OneToMany(mappedBy="routeObj", cascade=CascadeType.ALL)
    private List<RoutePoint> routePointList;
	
	private static final long serialVersionUID = 1L;
	
	public Route()
	{
		routePointList = new ArrayList<RoutePoint>();
	}
	
	public Route(String chipId, String name, Date startDate, Date endDate, Double routeDistance)
	{
		this.chipId = chipId;
		this.routeName = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.routeDistance = routeDistance;
		routePointList = new ArrayList<RoutePoint>();
	}

	public int getRouteId()
	{
		return routeId;
	}
	
	public String getRouteName()
	{
		return routeName;
	}
	
	public Date getStartDate()
	{
		return startDate;
	}
	
	public Date getEndDate()
	{
		return endDate;
	}
	
	public Double getRouteDistance()
	{
		return routeDistance;
	}
	
	public List<RoutePoint> getRoutePointList() {
		return routePointList;
	}

	public void addRoutePointToList(RoutePoint point) 
	{
		this.routePointList.add(point);
		point.setRoute(this);
	}
}
