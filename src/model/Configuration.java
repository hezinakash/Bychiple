package model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity(name="configuration")
public class Configuration implements Serializable
{
	@Id
	@Column(name = "chipId")
    private String chipId;
	
	@Column(name = "userId")
	private int userId;
	
	@Column(name = "parking")
    private Boolean isParking;
	
	@Column(name = "electric")
    private Boolean isElectric;
	
	@Column(name = "sendSMSFlag")
    private Boolean sendSMSFlag;
	
	private static final long serialVersionUID = 1L;
	
	public Configuration(){} //Must be default constructor !
	
	public Configuration(String chipId, int userId)
	{
		this.chipId = chipId;
		this.userId = userId;
		this.isParking = false;
		this.isElectric = false;
		this.sendSMSFlag = true;
	}
	
	public void setParking(Boolean parking)
	{
		isParking = parking;
	}
	
	public Boolean isParking()
	{
		return isParking;
	}
	
	public void setElectric(Boolean electric)
	{
		isElectric = electric;
	}
	
	public Boolean isElectric()
	{
		return isElectric;
	}
	
	public Boolean getSendSMSFlag()
	{
		return sendSMSFlag;
	}
	
	public void setSendSMSFlag(Boolean sendSMSFlag)
	{
		this.sendSMSFlag = sendSMSFlag;
	}
}
