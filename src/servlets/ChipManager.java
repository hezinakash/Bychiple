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
import model.Chip;
import model.User;
import utilities.ComboboxOption;
import utilities.Constants;
import utilities.JsonObjectUtils;
import utilities.ServletUtils;

@Path("/userChipsManager")
public class ChipManager 
{
	@Context private ServletContext context;
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	
	@POST
	@Path("/addChip")
	public void addChipToUserChipsList(@FormParam("chipID") String chipID, @FormParam("nickname") String nickname)
	{
		DBManager db = DBManager.getDBManager();
		JsonObjectUtils addChipDetails = new JsonObjectUtils();
		String message = null;
		
		//Check if chip already exists in the system with another owner
		Chip chipToCheck = db.getChipByID(chipID);
	    
	    if (chipToCheck != null){
			message = "Chip ID: " + chipID + "  already exists in the system with another owner";
		}
	    else {
	    	//get user connected
	    	String userID = (String)req.getSession().getAttribute(Constants.USER_ID);
	    	User ownerUser = db.getUserByID(userID);
	    	db.addNewChip(ownerUser, chipID, nickname);   
		    //create new configuration when user addes a new chip
		    db.addNewConfiguration(chipID, Integer.parseInt(userID));
		    
		    message = "chip " + chipID +  " was added successfully"; 
	    	addChipDetails.setIsSucceded(true);
	    }
	    
	    addChipDetails.setReason(message);
		ServletUtils.doJsonResponse(res, addChipDetails);
	}
	
	@POST
	@Path("/getUserChipsList")
	public void getUserChipsList()
	{
		DBManager db = DBManager.getDBManager();
		List<ComboboxOption> userChipsList = new ArrayList<>();
		String userID = (String)req.getSession().getAttribute(Constants.USER_ID);
		User ownerUser = db.getUserByID(userID);
	    
	    for (Chip chip : ownerUser.getChipList()) {
	    	ComboboxOption chipOption = new ComboboxOption(chip.getNickname(), chip.getID());
	    	userChipsList.add(chipOption);
		}
		
	    //set the first chip id of the list as a current chip (for refresh too) 
	    if ((String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID) == null) {
	    	req.getSession().setAttribute(Constants.CURRENT_CHIP_ID, ownerUser.getFirstChipID());
	    }
	    
	    userChips uc = new userChips(userChipsList, (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID));
	    ServletUtils.doJsonResponse(res, uc);
	}
	
	@POST
	@Path("/setCurrentChip")
	public void setCurrentChip(@FormParam("chipID") String chipID)
	{
		req.getSession().setAttribute(Constants.CURRENT_CHIP_ID, chipID);
	}
	
	@POST
	@Path("/initCurrentChipID")
	public void initCurrentChipID()  
	{
		DBManager db = DBManager.getDBManager();
		String userID = (String)req.getSession().getAttribute(Constants.USER_ID);
		User ownerUser = db.getUserByID(userID);
		String currChipId = null;
		
		//init the first chip id of the list as a current chip (for refresh too) 
	    if (req.getSession().getAttribute(Constants.CURRENT_CHIP_ID) == null) {
	    	currChipId = ownerUser.getFirstChipID();
	    	req.getSession().setAttribute(Constants.CURRENT_CHIP_ID, currChipId);
	    }
	    else {
	    	currChipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
	    }
	    
	    ServletUtils.doJsonResponse(res, db.IsAdmin(currChipId));
	}
	
	class userChips
	{
		@SuppressWarnings("unused")
		private List<ComboboxOption> userChipsList;
		@SuppressWarnings("unused")
		private String currChipID;
		
		public userChips(List<ComboboxOption> userChipsList, String currChipID)
		{
			this.userChipsList = userChipsList;
			this.currChipID = currChipID;
		}
	}
	
}
