package net.sourceforge.dscsim.controller.persistence;

import org.hibernate.*;
import org.hibernate.cfg.*;

/**
 * Utility class as recommended by hibernate documentation.
 * @author ex00573
 */
public class HibernateUtil {

	/*
	 * singleton instance of factory for all threads.
	 */
	private static final SessionFactory sessionFactory;

	static {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
