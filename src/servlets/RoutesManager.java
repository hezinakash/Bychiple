package servlets;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import controller.DBManager;
import model.Route;
import model.RoutePoint;
import utilities.Constants;
import utilities.ListItemDetails;
import utilities.Location;
import utilities.ServletUtils;

@Path("/routesManager")
public class RoutesManager
{
	@Context private ServletContext context;
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	
	@POST
	@Path("/saveRoute")
	public void saveRoute(@FormParam("locList") String points, @FormParam("routeName") String routeName,
			@FormParam("startDateStr") String startDateStr, @FormParam("endDateStr") String endDateStr,
			@FormParam("routeDistance") String routeDistance)
	{
		DBManager db = DBManager.getDBManager();
		String currChipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		Date startDate = new Date(Long.parseLong(startDateStr));
		Date endDate = new Date(Long.parseLong(endDateStr));
		
		String[] pointsArr = points.split(",");
		Route routeToSave = new Route(currChipId, routeName, startDate, endDate, Double.parseDouble(routeDistance));
		db.saveRoute(routeToSave, pointsArr);
	}
	
	@POST
	@Path("/getRoutesHistoryList")
	public void getRoutesHistoryList()
	{
		DBManager db = DBManager.getDBManager();
		List<ListItemDetails> routesList = new ArrayList<>();
		String chipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		List<Route> routesHistory = db.getRoutesListByChipId(chipId);

	    for (Route route : routesHistory) {
	    	DateFormat outputFormatter = new SimpleDateFormat("dd/MM/yyyy");
	    	String dateStr = outputFormatter.format(route.getStartDate());
	    	String routeName = route.getRouteName() + " - " + dateStr;
	    	ListItemDetails routeOption = new ListItemDetails(Integer.toString(route.getRouteId()), routeName);
	    	routesList.add(routeOption);
		}

	    ArrayList<Object> routeDetails = new ArrayList<>();
	    routeDetails.add(routesList);
		routeDetails.add(db.getElctricFlagByChipId(chipId));
	    
	    ServletUtils.doJsonResponse(res, routeDetails);
	}
	
	@POST
	@Path("/getRoutePointsList")
	public void getRoutePointsList(@FormParam("routeId") String routeId)
	{
		DBManager db = DBManager.getDBManager();
		List<Location> routeLoc = new ArrayList<>();
		List<RoutePoint> routePointsList = db.getRoutePointsListByRouteId(routeId);

	    for (RoutePoint point : routePointsList) {
	    	routeLoc.add(new Location(point.getLat(), point.getLng()));
		}

	    ServletUtils.doJsonResponse(res, routeLoc);
	}
	
	@POST
	@Path("/getRouteDetails")
	public void getRouteDetails(@FormParam("routeId") String routeId)
	{
		DBManager db = DBManager.getDBManager();
		String userID = (String)req.getSession().getAttribute(Constants.USER_ID);
		String chipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		int weight = db.getUserByID(userID).getWeight();
		Route route = db.getRouteByRouteId(routeId);
		
		ArrayList<Object> routeAndWeight = new ArrayList<>();
		routeAndWeight.add(route.getStartDate());
		routeAndWeight.add(route.getEndDate());
		routeAndWeight.add(route.getRouteDistance());
		routeAndWeight.add(weight);
		routeAndWeight.add(db.getElctricFlagByChipId(chipId));
		
	    ServletUtils.doJsonResponse(res, routeAndWeight);
	}
}
