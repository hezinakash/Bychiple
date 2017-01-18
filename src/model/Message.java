package model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity(name="message")
public class Message implements Serializable
{
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "messageId")
	private int messageId;
	
	@Column(name = "chipId")
    private String chipId;
	
	@Column(name = "userId")
	private int userId;
	
	@Column(name = "title")
    private String title;
	
	@Column(name = "content")
    private String content;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dateAndTime")
	private Date dateAndTime;
	
	@Column(name = "isRead")
    private Boolean isRead;
	
	private static final long serialVersionUID = 1L;
	public static final String WATCH_OUT = "Watch Out";
	public static final String BATTERY = "Low Battery";
	public static final String THEFT_SUSPICION = "Theft Suspicion";
	
	public Message() {}
	
	public Message(int userId, String chipId, String title)
	{
		this.userId = userId;
		this.chipId = chipId;
		this.title = title;
		dateAndTime = new Date();
		this.content = getContentByTitle(title);
		isRead = false;
	}
	
	public Date getDateAndTime() {
		return dateAndTime;
	}
	
	public boolean isRead()
	{
		return isRead;
	}
	
	public void setRead()
	{
		this.isRead = true;
	}

	private String getContentByTitle(String titleMsg)
	{
		String content = "";
		DateFormat outputFormatter = new SimpleDateFormat("h:mm a");
    	String dateStr = outputFormatter.format(dateAndTime);
    	
		if (titleMsg.equals(Message.WATCH_OUT)) {
			content = "The place you want to park was reported as a theft place." +
					  "<br/>We recommend you not to park in this area. <br/><br/>" +
					  "For your convenience, we have added a feature that allows you to watch theft places around you.";
		}
		else if (titleMsg.equals(Message.THEFT_SUSPICION)){
			content = "The system recognized that your bicycle moved more than 5 meters from the place you parked in.<br/>" +
					  "It might be a theft suspicion, please check your bicycle.";
		}
		else if (titleMsg.equals(Message.BATTERY)){
			content = "Your chip battery is low, please charge it.";
		}
		
		content += ("<br/><br/>" + dateStr);
		
		return content;
	}
}
