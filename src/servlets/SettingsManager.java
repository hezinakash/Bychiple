package servlets;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import controller.DBManager;
import utilities.Constants;
import utilities.ServletUtils;

@Path("/settings")
public class SettingsManager 
{
	@Context private ServletContext context;
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;

	@POST
	@Path("/getSettings")
	public void getSettings()  
	{
		DBManager db = DBManager.getDBManager();
		
		String chipID = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		String userID = (String)req.getSession().getAttribute(Constants.USER_ID);
		
		Boolean smsFlag = db.getSmsFlagByChipId(chipID);
		Boolean electricFlag = db.getElctricFlagByChipId(chipID);
		int weight = db.getUserByID(userID).getWeight();
		
		List<Object> settingsList = new ArrayList<>();
		settingsList.add(smsFlag);
		settingsList.add(electricFlag);
		settingsList.add(weight);
		
		ServletUtils.doJsonResponse(res, settingsList);	
	}
	
	@POST
	@Path("/saveChanges")
	public void saveChanges(@FormParam("selectedChipID") String selectedChipID, 
							@FormParam("smsFlag") String smsFlag, @FormParam("electricFlag") String electricFlag,
							@FormParam("weight") String weight)
	{
		DBManager db = DBManager.getDBManager();
		Boolean isSucess = false;
		
		if (selectedChipID != null )
		{
			//set current chipID to session
			req.getSession().setAttribute(Constants.CURRENT_CHIP_ID, selectedChipID);
					
			//save flags
			db.saveSettingsFlagsByChipId(selectedChipID, Boolean.parseBoolean(smsFlag), Boolean.parseBoolean(electricFlag));
			
			String userID = (String)req.getSession().getAttribute(Constants.USER_ID);
			db.updateUserWeightByID(userID, Integer.parseInt(weight));  
			
			isSucess = true;
		}
		
		ServletUtils.doJsonResponse(res, isSucess);
	}
	
	@POST
	@Path("/getElectricFlagAndWeight")
	public void getElectricFlag()  
	{
		DBManager db = DBManager.getDBManager();
		String chipID = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		String userID = (String)req.getSession().getAttribute(Constants.USER_ID);
		int weight = db.getUserByID(userID).getWeight();
		Boolean electricFlag = db.getElctricFlagByChipId(chipID);
		
		List<Object> electricFlagAndWeight = new ArrayList<>();
		electricFlagAndWeight.add(electricFlag);
		electricFlagAndWeight.add(weight);
		
		ServletUtils.doJsonResponse(res, electricFlagAndWeight);	
	}
}
