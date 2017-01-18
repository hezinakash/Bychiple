package model;

import javax.persistence.Entity;
import java.io.Serializable;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;


@Entity(name="user")
public class User implements Serializable
{
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "userId")
	private int userId;
	
	@Column(name = "fName")
    private String fname;
	
	@Column(name = "lName")
    private String lname;
	
	@Column(name = "email")
    private String email;
	
	@Column(name = "password")
    private String password;
	
	@Column(name = "phoneNo")
    private String phoneNumber;
	
	@Column(name = "weight")
    private int weight;
	
	@OneToMany(mappedBy="ownerUser", cascade=CascadeType.ALL)
    private List<Chip> chipList;
    
	private static final long serialVersionUID = 1L;

	public User() //Must be default constructor !
	{
		this.chipList = new ArrayList<Chip>();
	}   

	public User(String fname, String lname) 
	{
		this.fname = fname;
		this.lname = lname;
		this.chipList = new ArrayList<Chip>();
		this.weight = 60;
	}
	
	public String getFname() {
		return fname;
	}
	
	public int getID () {
		return userId;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public List<Chip> getChipList() {
		return chipList;
	}

	public void addChipIdToList(Chip newChip) 
	{
		chipList.add(newChip);
		newChip.setOwner(this);
	}
	
	public String getFirstChipID()
	{
		return chipList.get(0).getID();
	}
	
	public int getWeight() {
		return weight;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
