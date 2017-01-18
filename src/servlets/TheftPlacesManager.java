package servlets;

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


@Path("/theftPlacesManager")
public class TheftPlacesManager 
{
	@Context private ServletContext context;
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	
	@POST
	@Path("/addTheftPlace")
	public void addNewTheftPlace(@FormParam("address") String address)
	{
		DBManager db = DBManager.getDBManager();
		String currChipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		Location parkingLocation = (Location)context.getAttribute(currChipId + Constants.PARKING_LOC);
		db.addNewTheftPlace(parkingLocation, address);
	}
	
	@POST
	@Path("/getTheftPlaces")
	public void getTheftPlaces()
	{
		DBManager db = DBManager.getDBManager();
		List<TheftPlace> theftPlacesFound = db.getTheftPlaces();
		ServletUtils.doJsonResponse(res, theftPlacesFound);
	}
}
