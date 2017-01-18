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
import model.Chip;
import model.User;
import utilities.Constants;
import utilities.JsonObjectUtils;
import utilities.ServletUtils;


@Path("/connection")
public class UserManager 
{
	@Context private ServletContext context;
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	
	@POST
	@Path("/login")
	public void loginUser(@FormParam("email") String email, @FormParam("pass") String password)
	{
		DBManager db = DBManager.getDBManager();
		String message = null;
		boolean errorFound = false;
		JsonObjectUtils loginInDetails = new JsonObjectUtils();
		User userToCheck = null;
		
		List<User> usersFound = db.getUserByEmail(email);
		
		//no such user with this email
		if (usersFound.isEmpty()){
			message = "Login faild, email does not exist";
			errorFound = true;
		}
		else {
			userToCheck = usersFound.get(0);
			if ((userToCheck.getPassword() == null) || (!userToCheck.getPassword().equals(password))){
				message = "Login faild, incorrect password";
				errorFound = true;
			} 
		}
		
		if (!errorFound) {
			message = "Login succeded";
			// add user's id and name to session
			req.getSession().setAttribute(Constants.USER_NAME, userToCheck.getFname());
			req.getSession().setAttribute(Constants.USER_ID, Integer.toString(userToCheck.getID()));
			loginInDetails.setIsSucceded(true);
		}
		
		loginInDetails.setReason(message);
		ServletUtils.doJsonResponse(res, loginInDetails);
	}
	
	@POST
	@Path("/signUp")
	public void signUpUser(@FormParam("chipID") String chipID, @FormParam("nickname") String nickname, 
						   @FormParam("fname") String firstName, @FormParam("lname") String lastName, 
						   @FormParam("email") String email, @FormParam("pass") String password, 
						   @FormParam("phone") String phone)		 
	{
		DBManager db = DBManager.getDBManager();
		JsonObjectUtils signUpDetails = new JsonObjectUtils();
		String message = null;
		
		Chip chipToCheck = db.getChipByID(chipID);

		if (chipToCheck != null){
			message = "chip id already exists";
		}
		else {
			
			List<User> usersFound = db.getUserByEmail(email);
		
			if (!usersFound.isEmpty()){
				message = "email already exists";
			}
			else
			{
				User user = new User(firstName, lastName);
				user.setEmail(email);
				user.setPassword(password);
				user.setPhoneNumber(phone);
				user.addChipIdToList(new Chip(chipID, nickname, user));
				
				user = db.addNewUser(user);
			    //create new configuration for every chip added
			    db.addNewConfiguration(chipID, user.getID());
			    
				// set session to the new user
				req.getSession().setAttribute(Constants.USER_NAME, firstName);
				req.getSession().setAttribute(Constants.USER_ID, Integer.toString(user.getID()));
			
				signUpDetails.setIsSucceded(true);
				message = "Sign up succeded";
			}
		}
		
		signUpDetails.setReason(message);
		ServletUtils.doJsonResponse(res, signUpDetails);
	}

	@POST
	@Path("/userConnectionCheck")
	public void checkIfUserConnected()		 
	{
		userConnectionData userConnectionData = new userConnectionData();
		String userID = (String)req.getSession().getAttribute(Constants.USER_ID);
		String userName;
		
		if (userID != null) {
			userName = (String)req.getSession().getAttribute(Constants.USER_NAME);
			if (userName != null) {
				userConnectionData.setLoggedIn();  
				userConnectionData.setUserName(userName);
			}
		}
		
		ServletUtils.doJsonResponse(res, userConnectionData);
	}
	
	@POST
	@Path("/disconnectUser")
	public void disconnectUser()		 
	{
		req.getSession().invalidate();
	}
	
	class userConnectionData
	{	
		@SuppressWarnings("unused")
		private boolean isLoggedIn;
		@SuppressWarnings("unused")
		private String userName;

		public userConnectionData() { 
			this.isLoggedIn = false;
			this.userName = "";
		}
		
		public void setLoggedIn() { 
			this.isLoggedIn = true;
		}
		
		public void setUserName(String userName){
			this.userName = userName;
		}
	}
}
