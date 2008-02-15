package net.sourceforge.dscsim.controller;

import org.hibernate.Session;

import net.sourceforge.dscsim.controller.persistence.HibernateUtil;
import net.sourceforge.dscsim.controller.message.types.*;

public class HibernateTest {

	/**
	 * @param args
	 * @throws CloneNotSupportedException 
	 */
	public static void main(String[] args) throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		
		Dscmessage msg = new Dscmessage();
		
		Position pos = msg.getPosition();
		pos.getLatitude().setDegrees(42);
		pos.getLongitude().setDegrees(72);
		pos.getLatitude().setHemisphere(Latitude.Hemisphere.N);
		pos.getTime().setHours(22);
				
		msg.setUid(java.util.Calendar.getInstance().getTime());
		msg.setSender("0200019083");
			
		session.save(msg);


		session.getTransaction().commit();
		
	}

}
