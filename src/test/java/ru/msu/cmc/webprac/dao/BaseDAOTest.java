package ru.msu.cmc.webprac.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import ru.msu.cmc.webprac.utils.HibernateUtil;

public abstract class BaseDAOTest {

    @BeforeSuite
    public void setUpHibernate() {
        SessionFactory sf = new Configuration()
                .configure("hibernate-test.cfg.xml")
                .buildSessionFactory();
        HibernateUtil.setSessionFactory(sf);
    }

    @AfterSuite
    public void tearDownHibernate() {
        HibernateUtil.shutdown();
    }
}
