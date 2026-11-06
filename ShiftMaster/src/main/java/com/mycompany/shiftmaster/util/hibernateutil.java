package com.mycompany.shiftmaster.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class hibernateutil {
    private static final SessionFactory sessionFactory = buildSessionFactory() ;
    
    private static SessionFactory buildSessionFactory() {
        try {
            // Membaca otomatis daro hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        } catch(Throwable ex){
            System.err.println("Gagal membuat SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}