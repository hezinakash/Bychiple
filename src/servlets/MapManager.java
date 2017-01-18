package servlets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import controller.DBManager;
import model.TheftPlace;
import utilities.Constants;
import utilities.Location;
import utilities.ServletUtils;

@Path("/mapManager")
public class MapManager 
{
	@Context private ServletContext context;
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	
	private static int locIndex = 0; 
	private static boolean isSimulatorBuilded = false;
	private static ArrayList<Location> locationList = new ArrayList<>();
	
	@POST
	@Path("/userCurrLocation")
	public void getUserCurrLocation() 
	{
		Location currLocation = null;
		DBManager db = DBManager.getDBManager();
		String currChipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
			
		if (db.IsAdmin(currChipId)) {
			currLocation = getLocationFromSimulator(currChipId);
		}
		else {     // get the current location that the chip set
			currLocation = (Location)context.getAttribute(currChipId + "_CurrLoc");
		}
		
		ServletUtils.doJsonResponse(res, currLocation);
	}
	
	private Location getLocationFromSimulator(String currChipId)  
	{
		if (!isSimulatorBuilded) {
			buildSimulatorOfLocations();
		}
		
		Location currLocation = locationList.get(locIndex);
	    locIndex++;
	    locIndex = (locIndex == locationList.size()) ? 0 : locIndex;
	    
	    if (currChipId != null)
	    	context.setAttribute(currChipId + Constants.CURRENT_LOC, currLocation);
	    
	    return currLocation;
	}
	
	private void buildSimulatorOfLocations()
	{
		locationList.add(new Location(32.0714053, 34.7847534));
		locationList.add(new Location(32.071087, 34.784858));
		locationList.add(new Location(32.070429, 34.784852));
		locationList.add(new Location(32.070383, 34.784236));
		locationList.add(new Location(32.070388, 34.783442));
		locationList.add(new Location(32.070347, 34.782788));
		locationList.add(new Location(32.070606, 34.782482));
		locationList.add(new Location(32.070897, 34.782182));
		locationList.add(new Location(32.071324, 34.781951));
		locationList.add(new Location(32.071756, 34.781956));
		locationList.add(new Location(32.072465, 34.781940));
		locationList.add(new Location(32.073192, 34.781881));
		locationList.add(new Location(32.073647, 34.781683));
		locationList.add(new Location(32.073761, 34.781114));
		locationList.add(new Location(32.073884, 34.780497));
		locationList.add(new Location(32.074002, 34.779725));
		locationList.add(new Location(32.073970, 34.778953));
		locationList.add(new Location(32.073661, 34.778873));
		locationList.add(new Location(32.073379, 34.778465));
		locationList.add(new Location(32.073615, 34.777741));
		locationList.add(new Location(32.073901, 34.777253));
		locationList.add(new Location(32.074206, 34.776491));
		locationList.add(new Location(32.074629, 34.776459));
		locationList.add(new Location(32.074820, 34.776003));
		locationList.add(new Location(32.074634, 34.775756));
		locationList.add(new Location(32.074198, 34.775493));
		locationList.add(new Location(32.074243, 34.775016));
		locationList.add(new Location(32.074475, 34.774565));
		locationList.add(new Location(32.074811, 34.773921));
		locationList.add(new Location(32.075125, 34.773218));
		
		isSimulatorBuilded = true;
	}

	@POST
	@Path("/saveParkingState")
	public void saveUserParkingLocation(@FormParam("parking") Boolean isParking)
	{
		DBManager db = DBManager.getDBManager();
		String currChipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		
		//update the parking status
		db.updateParkingStatus(currChipId, isParking);
		
		if (isParking)
		{
			Location currLocation = (Location)context.getAttribute(currChipId + Constants.CURRENT_LOC);
			context.setAttribute(currChipId + Constants.PARKING_LOC, currLocation);
			
			List<TheftPlace> theftPlacesFound = db.getTheftPlaces();
			ArrayList<Object> data = new ArrayList<>(Arrays.asList(theftPlacesFound, currLocation));
			ServletUtils.doJsonResponse(res, data);
		}
		else {  // clear the parking location
			context.setAttribute(currChipId + Constants.PARKING_LOC, null);
		}
	}
	
	@POST
	@Path("/checkParkingState")
	public void checkParkingState()
	{
		String currChipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		
		Location currLocation = (Location)context.getAttribute(currChipId + Constants.CURRENT_LOC);
		Location parkingLocation = (Location)context.getAttribute(currChipId + Constants.PARKING_LOC);
		ArrayList<Location> locList = new ArrayList<>();
		
		locList.add(currLocation);
		locList.add(parkingLocation);
		
		ServletUtils.doJsonResponse(res, locList);
	}
	
	@POST
	@Path("/parkingLocation")
	public void getParkingLocation()
	{
		String currChipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		Location parkingLocation = (Location)context.getAttribute(currChipId + Constants.PARKING_LOC);
		
		ServletUtils.doJsonResponse(res, parkingLocation);
	}
}
