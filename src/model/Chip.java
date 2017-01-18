package model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity(name="chip")
public class Chip implements Serializable
{
	@ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "ownerUser")
	private User ownerUser;
	
	@Id
	@Column(name = "chipId")
    private String chipId;
	
	@Column(name = "nickname")
    private String nickname;
 
	private static final long serialVersionUID = 1L;

	public Chip() {}//Must be default constructor

	public Chip(String id, String nickname, User ownerUser) 
	{
		this.chipId = id;
		this.nickname = nickname;
		this.ownerUser = ownerUser;
	}
	
    public String getID() 
    {
        return this.chipId;
    }

    public User getOwner()
    {
    	return this.ownerUser;
    }
    
    public void setOwner(User owner)
    {
    	this.ownerUser = owner;
    	if (!owner.getChipList().contains(this))
    	{
    		owner.getChipList().add(this);
    	}
    }
    
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}

