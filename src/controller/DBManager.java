package controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import model.Chip;
import model.ChipMessage;
import model.Configuration;
import model.Message;
import model.Route;
import model.RoutePoint;
import model.TheftPlace;
import model.User;
import utilities.Constants;
import utilities.EntityManagerUtil;
import utilities.Location;

public class DBManager
{
	private static DBManager DBManagerInstance = null;
	
	private DBManager() {}
	
	public static DBManager getDBManager()
    {
        if (DBManagerInstance == null)
        {
        	DBManagerInstance = new DBManager();
        }

        return DBManagerInstance;
    }

	public Chip getChipByID(String chipID)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		em.getTransaction().begin();
	    Chip chip = (Chip) em.find(Chip.class, chipID);
	    em.getTransaction().commit();
	    
	    return chip;
	}

	public User getUserByID(String userID)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		em.getTransaction().begin();
	    User user = (User) em.find(User.class, Integer.parseInt(userID));
	    em.getTransaction().commit();
	    
	    return user;
	}
	
	public void addNewChip(User ownerUser, String chipID, String nickname)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
	
		em.getTransaction().begin();
	    Chip chip = new Chip(chipID, nickname, ownerUser);
	    //Add a new chip to logged in user chips list
	    ownerUser.addChipIdToList(chip);
	    em.merge(chip);
	    em.getTransaction().commit();
	}
	
	public void addNewConfiguration(String chipID, int userID)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		Configuration conf = new Configuration(chipID, userID);
		
		em.getTransaction().begin();
		em.merge(conf);
		em.getTransaction().commit();
	}
	
	public void updateParkingStatus(String chipID, boolean isParking)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		em.getTransaction().begin();
		Configuration conf = (Configuration) em.find(Configuration.class, chipID);
		conf.setParking(isParking);
		em.getTransaction().commit();
	}

	@SuppressWarnings("unchecked")
	public List<TheftPlace> getTheftPlaces() 
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		Query query = em.createQuery("Select t FROM theftPlace t");
		
		return query.getResultList();
	}

	public void addNewMessage(String userId, String currChipId, String titleMsg) 
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		Message msg = new Message(Integer.parseInt(userId), currChipId, titleMsg);
		em.getTransaction().begin();
		em.merge(msg);
		em.getTransaction().commit();
	}

	@SuppressWarnings("unchecked")
	public List<Message> getMessagesListByChipId(String chipId) 
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		Query query = em.createQuery("SELECT m FROM message m WHERE m.chipId = :chipId");
		query.setParameter("chipId", chipId);
		
		return query.getResultList();
	}

	public void updateReadMsg(String msgId) 
	{
		EntityManager em = EntityManagerUtil.getEntityManager();

		em.getTransaction().begin();
	    Message msg = (Message) em.find(Message.class, Integer.parseInt(msgId));
	    msg.setRead();
	    em.getTransaction().commit();
	}

	public long getNumOfUnreadMsg(String currChipId) 
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		Query query = em.createQuery("SELECT COUNT(m.chipId) FROM message m WHERE m.chipId = :chipId and m.isRead = :isRead");
		query.setParameter("chipId", currChipId);
		query.setParameter("isRead", false);
		
		return (long)query.getSingleResult();
	}

	public void saveRoute(Route routeToSave, String[] pointsArr) 
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		for (int i = 0; i < pointsArr.length; i+=2)
		{
			Double lat = Double.parseDouble(pointsArr[i]);
			Double lng = Double.parseDouble(pointsArr[i+1]);
			routeToSave.addRoutePointToList(new RoutePoint(routeToSave, lat, lng));;
		}
		
		em.getTransaction().begin();
	    em.merge(routeToSave);
	    em.getTransaction().commit();
	}

	@SuppressWarnings("unchecked")
	public List<Route> getRoutesListByChipId(String chipId) 
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		Query query = em.createQuery("SELECT r FROM route r WHERE r.chipId = :chipId");
		query.setParameter("chipId", chipId);
		
		return query.getResultList();
	}

	public List<RoutePoint> getRoutePointsListByRouteId(String routeId) 
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		em.getTransaction().begin();
	    Route routeToFind = (Route) em.find(Route.class, Integer.parseInt(routeId));
	    em.getTransaction().commit();
	    
	    return routeToFind.getRoutePointList();
	}

	public Route getRouteByRouteId(String routeId) 
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		Query query = em.createQuery("SELECT r FROM route r WHERE r.routeId = :routeId");
		query.setParameter("routeId", Integer.parseInt(routeId));
		
		@SuppressWarnings("unchecked")
		List<Route> routeList = query.getResultList();
		
		return routeList.get(0);
	}
	
	public void addNewTheftPlace(Location parkingLocation, String address) 
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		//check if the theft place already exists
		Query query = em.createQuery("SELECT COUNT(t.theftPlaceId) FROM theftPlace t WHERE t.lat = :lat and t.lng = :lng");
		query.setParameter("lat", parkingLocation.getLat());
		query.setParameter("lng", parkingLocation.getLng());
		long numOfTheftPlaces = (long)query.getSingleResult();
				
		if (numOfTheftPlaces == 0)
		{
			TheftPlace newTheftPlace = new TheftPlace(parkingLocation.getLat(), parkingLocation.getLng(), address);
			em.getTransaction().begin();
			em.merge(newTheftPlace);
			em.getTransaction().commit();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getUserByEmail(String email) 
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		Query query = em.createQuery("Select u FROM user u WHERE u.email = :email");
		query.setParameter("email", email);
		
		return query.getResultList();
	}

	public User addNewUser(User user) 
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		em.getTransaction().begin();
	    user = em.merge(user);
	    em.getTransaction().commit();
	    
	    return user;
	}
	
	public ChipMessage getChipMessage(String chipID)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		ChipMessage message = null;

		Query query = em.createQuery("SELECT cm FROM chipMessage cm WHERE cm.chipId = :chipId ORDER BY cm.dateAndTime ASC");
		query.setParameter("chipId", chipID);
		@SuppressWarnings("unchecked")
		List<ChipMessage> messagesFoundList = query.getResultList();
				
		if (messagesFoundList.size() > 0)
			message = messagesFoundList.get(0);  //get the first message!
		
		if (message != null) {
			em.getTransaction().begin();
			em.remove(message);		//delete chipMessage
			em.getTransaction().commit();
		}
		
		return message;
	}

	public Boolean getSmsFlagByChipId(String chipID)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		Configuration conf = (Configuration) em.find(Configuration.class, chipID);
		return conf.getSendSMSFlag();
	}

	public void createChipMessage(String chipId, String userId, String titleMsg)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		em.getTransaction().begin();
		User user = (User) em.find(User.class, Integer.parseInt(userId));
		String content = ChipMessage.getContentByTitle(titleMsg, user.getFname());
		ChipMessage chipMessage = new ChipMessage(chipId, content);
		
		em.merge(chipMessage);
		em.getTransaction().commit();
	}
	
	public Boolean getElctricFlagByChipId(String chipID) 
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		Configuration conf = (Configuration) em.find(Configuration.class, chipID);
		return conf.isElectric();
	}
	
	public void saveSettingsFlagsByChipId(String chipID, Boolean smsFlag, Boolean electricFlag)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		em.getTransaction().begin();
		Configuration conf = (Configuration) em.find(Configuration.class, chipID);
		conf.setSendSMSFlag(smsFlag);
		conf.setElectric(electricFlag);	
		em.getTransaction().commit();		
	}
	
	public boolean IsAdmin(String currChipId) 
	{
		return currChipId.equals(Constants.ADMIN_CHIP_ID);
	}
	
	public void updateUserWeightByID(String userID, int weight)
	{
		EntityManager em = EntityManagerUtil.getEntityManager();
		
		em.getTransaction().begin();
		User currUser = (User) em.find(User.class, Integer.parseInt(userID));
		currUser.setWeight(weight);	
		em.getTransaction().commit();
	}
}
