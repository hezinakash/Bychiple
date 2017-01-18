package servlets;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import controller.DBManager;
import model.Chip;
import model.ChipMessage;
import utilities.Constants;
import utilities.Location;
import utilities.ServletUtils;

@Path("/hc")
public class HardwareChipManager
{
	final char endAnswer = '|';
	
	@Context private ServletContext context;
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	
	@POST
	@Path("/loc")
	public void setChipLocation(@FormParam("lat") String lat, @FormParam("lng") String lng,
								@FormParam("battery") String battery)
	{	
		//DBManager db = DBManager.getDBManager();
		String chipID = req.getHeader("User-Agent");
		//String chipID = "865067021645117";
		
		updateTimeChipActive(chipID); 
		
		saveLocationOnServer(lat, lng, chipID);
		
		battery = battery.substring(0, battery.length() - 1);
		if (!(battery.startsWith("-1")))
			context.setAttribute(chipID + Constants.BATTERY, battery); 
		
		writeMsgToChip(chipID);    //NEW 7.9
		
		/*ChipMessage message = null;
		String contentMsg = "", phoneNo="";
		
		if (context.getAttribute(chipID + Constants.CHIP_MSG_COUNT) != null)
		{	
			int numOfMessages= (Integer)context.getAttribute(chipID + Constants.CHIP_MSG_COUNT);

			if (numOfMessages > 0 )
			{
				Chip chip = db.getChipByID(chipID);
				
				if (chip != null)
				{
					//check if there is new message		
					phoneNo = chip.getOwner().getPhoneNumber();
					message = db.getChipMessage(chipID);
					
					if (message != null)
					{
						contentMsg = message.getContent();
						numOfMessages--;
						context.setAttribute(chipID + Constants.CHIP_MSG_COUNT, numOfMessages);
					}
				}
			}	
		}
		
		try (PrintWriter out = res.getWriter())
		{   
			String outputMsg;
			if (message != null)
				outputMsg = "MSG:" + contentMsg+ "&" + phoneNo+ endAnswer;	
			else
				outputMsg = "OK count=" + context.getAttribute(chipID + Constants.CHIP_MSG_COUNT) + endAnswer;
			out.print(outputMsg);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} */
	}

	private void writeMsgToChip(String chipID)
	{
		DBManager db = DBManager.getDBManager();
		ChipMessage message = null;
		String contentMsg = "", phoneNo="";
		
		if (context.getAttribute(chipID + Constants.CHIP_MSG_COUNT) != null)
		{	
			int numOfMessages= (Integer)context.getAttribute(chipID + Constants.CHIP_MSG_COUNT);

			if (numOfMessages > 0 )
			{
				Chip chip = db.getChipByID(chipID);
				
				if (chip != null)
				{
					//check if there is new message		
					phoneNo = chip.getOwner().getPhoneNumber();
					message = db.getChipMessage(chipID);
					
					if (message != null)
					{
						contentMsg = message.getContent();
						numOfMessages--;
						context.setAttribute(chipID + Constants.CHIP_MSG_COUNT, numOfMessages);
					}
				}
			}	
		}
		
		try (PrintWriter out = res.getWriter())
		{   
			String outputMsg;
			if (message != null)
				outputMsg = "MSG:" + contentMsg+ "&" + phoneNo+ endAnswer;	
			else
				outputMsg = "OK count=" + context.getAttribute(chipID + Constants.CHIP_MSG_COUNT) + endAnswer;
			out.print(outputMsg);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateTimeChipActive(String chipID)
	{
		if (chipID != null)
	    	context.setAttribute(chipID + Constants.CHIP_UPDATE_TIME, new Date());	
	}

	private void saveLocationOnServer(String lat, String lng, String chipID) 
	{
		if (lat != "" && lng != "")
		{
			Location currLocation = new Location(Double.parseDouble(lat), Double.parseDouble(lng));
		    if (chipID != null)
		    	context.setAttribute(chipID + Constants.CURRENT_LOC, currLocation);
		}
	}

	@POST
	@Path("/addMessage")
	public void addMessage(@FormParam("message") String titleMsg )	
	{
		DBManager db = DBManager.getDBManager();

		String currChipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		String userId = (String)req.getSession().getAttribute(Constants.USER_ID);
		
		Boolean isSmsFlagOn = db.getSmsFlagByChipId(currChipId); 
		if (isSmsFlagOn)	
		{
			//counter for message 
			if (context.getAttribute(currChipId + Constants.CHIP_MSG_COUNT) == null)
				context.setAttribute(currChipId + Constants.CHIP_MSG_COUNT, 0);
			
			int numOfMessages= (Integer)context.getAttribute(currChipId + Constants.CHIP_MSG_COUNT);
			numOfMessages++;
			context.setAttribute(currChipId + Constants.CHIP_MSG_COUNT, numOfMessages);
			
			db.createChipMessage(currChipId, userId, titleMsg);
		}
	}
	
	@GET
	@Path("/chipDetails")
	public void getChipDetails() 
	{
		String battery = null;
		Date chipUpdateTime = null;
		//DBManager db = DBManager.getDBManager();
		List<Object> chipDetailsList = new ArrayList<>();
		
		// get chip battery
		String currChipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		
		if (currChipId != null) {
			battery = getChipBattery(currChipId); 
			chipUpdateTime = getChipUpdateTime(currChipId);
		}
		
		chipDetailsList.add(battery);
		chipDetailsList.add(chipUpdateTime);
		
		/*if (db.IsAdmin(currChipId)) {
		battery = "80";
	}
	else {
		if (context.getAttribute(currChipId + Constants.BATTERY) != null)
			battery  = (String)context.getAttribute(currChipId + Constants.BATTERY);
	}*/
		
		/*if (db.IsAdmin(currChipId)) {
			chipUpdateTime = new Date();
		}
		else {
			if (context.getAttribute(currChipId + Constants.CHIP_UPDATE_TIME) != null)
				chipUpdateTime = (Date) context.getAttribute(currChipId + Constants.CHIP_UPDATE_TIME);
		}*/
		
		ServletUtils.doJsonResponse(res, chipDetailsList);
	}
	
	private Date getChipUpdateTime(String currChipId) 
	{
		Date chipUpdateTime = null;
		DBManager db = DBManager.getDBManager();
		
		if (db.IsAdmin(currChipId)) {
			chipUpdateTime = new Date();
		}
		else {
			if (context.getAttribute(currChipId + Constants.CHIP_UPDATE_TIME) != null)
				chipUpdateTime = (Date) context.getAttribute(currChipId + Constants.CHIP_UPDATE_TIME);
		}
		
		return chipUpdateTime;
	}

	private String getChipBattery(String currChipId)
	{
		String battery = null;
		DBManager db = DBManager.getDBManager();
		
		if (db.IsAdmin(currChipId)) {
			battery = "80";
		}
		else {
			if (context.getAttribute(currChipId + Constants.BATTERY) != null)
				battery  = (String)context.getAttribute(currChipId + Constants.BATTERY);
		}
		
		return battery;
	}
}
