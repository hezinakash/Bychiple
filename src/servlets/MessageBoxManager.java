package servlets;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import controller.DBManager;
import model.Message;
import utilities.Constants;
import utilities.ServletUtils;

@Path("/messageBoxManager")
public class MessageBoxManager
{
	@Context private ServletContext context;
	@Context private HttpServletRequest req;
	@Context private HttpServletResponse res;
	
	@POST
	@Path("/addMessageToInbox")
	public void addMessageToInbox(@FormParam("msg") String titleMsg)
	{
		DBManager db = DBManager.getDBManager();
		String currChipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		String userId = (String)req.getSession().getAttribute(Constants.USER_ID);
		
		db.addNewMessage(userId, currChipId, titleMsg);
	}
	
	@POST
	@Path("/loadMessageBox")
	public void addMessageToInbox()
	{
		DBManager db = DBManager.getDBManager();
		String currChipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		List<Message> messagesList = db.getMessagesListByChipId(currChipId);
		
		Collections.sort(messagesList, new Comparator<Message>() {
			  public int compare(Message msg1, Message msg2) {
			      return msg2.getDateAndTime().compareTo(msg1.getDateAndTime());
			  }
		});

		ServletUtils.doJsonResponse(res, messagesList);
	}
	
	@POST
	@Path("/updateReadMsg")
	public void updateReadMsgInDB(@FormParam("msgId") String msgId)
	{
		DBManager db = DBManager.getDBManager();
		db.updateReadMsg(msgId);
	}
	
	@POST
	@Path("/getNumOfUnreadMsg")
	public void getNumOfUnreadMsg()
	{
		DBManager db = DBManager.getDBManager();
		String currChipId = (String)req.getSession().getAttribute(Constants.CURRENT_CHIP_ID);
		long numOfUnreadMsg = db.getNumOfUnreadMsg(currChipId);
		ServletUtils.doJsonResponse(res, numOfUnreadMsg);
	}
}
