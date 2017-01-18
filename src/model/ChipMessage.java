package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;


@Entity(name="chipMessage")
public class ChipMessage implements Serializable
{
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "messageId")
	private int messageId;
	
	@Column(name = "chipId")
    private String chipId;
	
	@Column(name = "content")
    private String content;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dateAndTime")
	private Date dateAndTime;
	
	private static final long serialVersionUID = 1L;
	//Constant Messages Type
	public static final String THEFT_SUSPICION = "Theft Suspicion !!!";
	
	public ChipMessage() {}
	
	public ChipMessage(String chipId, String content)
	{
		this.chipId = chipId;
		this.content = content;
		this.dateAndTime = new Date();
	}
	
	public Date getDateAndTime() {
		return dateAndTime;
	}
	
	public int getMessageId()
	{
		return this.messageId; 
	}
	
	public String getContent()
	{
		return this.content;
	}

	public static String getContentByTitle(String titleMsg, String userName) 
	{
		String content = "";
		
		if (titleMsg.equals(ChipMessage.THEFT_SUSPICION)) {
			content = "Hi " + userName + ".\nYour Bicycle may be stolen. To follow them use Bychiple App";
		}
		return content;
	}  
}
