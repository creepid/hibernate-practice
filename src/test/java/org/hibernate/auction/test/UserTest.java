/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hibernate.auction.test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.log4j.Level;
import org.hibernate.Hibernate;
import org.hibernate.auction.dao.BillingDetailsDAO;
import org.hibernate.auction.dao.ItemDAO;
import org.hibernate.auction.dao.UserDAO;
import org.hibernate.auction.model.BankAccount;
import org.hibernate.auction.model.BillingDetails;
import org.hibernate.auction.model.CreditCard;
import org.hibernate.auction.model.Item;
import org.hibernate.auction.model.MonetaryAmount;
import org.hibernate.auction.model.User;
import org.hibernate.auction.persistence.HibernateUtil;
import org.hibernate.auction.util.LogHelper;
import org.hibernate.proxy.HibernateProxy;

/**
 *
 * @author rusakovich
 */
public class UserTest extends TestCaseWithData {

    public UserTest(String x) throws Exception {
        super(x);
        LogHelper.disableLogging();
        super.setUp();
        initData();
        LogHelper.setLogging("org.hibernate.SQL", Level.DEBUG);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
        HibernateUtil.closeSession();
    }

    public void testUserWithItems() throws Exception {
        System.out.println("******************** testUserWithItems ********************");

        UserDAO userDAO = new UserDAO();
        assertTrue(userDAO.findAll().size() > 0);

        User user = new User();
        user.setFirstname("Christian");
        user.setAdmin(true);
        user.clearCreatedDate();

        Collection users = userDAO.findByExample(user);

        assertNotNull(users);
        Object[] userArr = users.toArray();
        assertTrue(userArr.length > 0);
        assertTrue(userArr[0] instanceof User);

        User christian = (User) userArr[0];
        assertNotNull(christian.getId());

        Set items = christian.getItems();
        Item item = (Item) items.toArray()[0];
        assertNotNull(item.getId());

        Calendar inThreeDays = GregorianCalendar.getInstance();
        inThreeDays.roll(Calendar.DAY_OF_YEAR, 3);
        Item newItem = new Item("Item One", "An item in the carsLuxury category.",
                christian,
                new MonetaryAmount(new BigDecimal("1.99"), Currency.getInstance(Locale.US)),
                new MonetaryAmount(new BigDecimal("50.33"), Currency.getInstance(Locale.US)),
                new Date(), inThreeDays.getTime());

        christian.addItem(newItem);

        ItemDAO itemDAO = new ItemDAO();
        itemDAO.makePersistent(newItem);

        HibernateUtil.commitTransaction();

    }

    public void testUserItemsInitialization() throws Exception {
        System.out.println("******************** testUserItemsInitialization ********************");
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserById(1l, false);

        assertFalse(Hibernate.isInitialized(user.getItems()));
        Set<Item> items = user.getItems();
        assertFalse(Hibernate.isInitialized(items));

        Item item = (Item) items.toArray()[0];
        assertTrue(Hibernate.isInitialized(items));
        assertNotNull(item.getName());

        User user2 = userDAO.getUserById(1l, false);
        assertTrue(user == user2);

        userDAO.evict(user);

        User user3 = userDAO.getUserById(1l, false);
        assertFalse(user == user3);

        items = user3.getItems();
        assertFalse(Hibernate.isInitialized(items));
        Hibernate.initialize(items);
        assertTrue(Hibernate.isInitialized(items));
    }

    public void testLoadUserWithBillingDetails() {
        System.out.println("******************** testLoadUserWithBillingDetails ********************");
        UserDAO userDAO = new UserDAO();
        User user = userDAO.loadUserById(1l, false);

        Set<BillingDetails> billingDetails = user.getBillingDetails();
        Iterator<BillingDetails> billingIterator = billingDetails.iterator();

        while (billingIterator.hasNext()) {
            BillingDetails details = billingIterator.next();
            if (details instanceof BankAccount) {
                BankAccount account = (BankAccount) details;
                assertEquals("FooBar Rich Bank", account.getBankName());
                assertEquals("234234234234", account.getNumber());
            }

            if (details instanceof CreditCard) {
                CreditCard cc = (CreditCard) details;
                assertEquals("1234567890", cc.getNumber());
            }

            if (details instanceof HibernateProxy) {
                HibernateProxy proxy = (HibernateProxy) details;
                Object proxyImpl = proxy.getHibernateLazyInitializer().getImplementation();
                if (proxyImpl instanceof CreditCard) {
                    CreditCard cc = (CreditCard) proxyImpl;
                    assertEquals("1234567890", cc.getNumber());
                } else {
                    fail("Proxy is not a CreditCard");
                }
            }
        }

        BillingDetails defaultBillingDetails = user.getDefaultBillingDetails();
        if (defaultBillingDetails instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy) defaultBillingDetails;
            Object proxyImpl = proxy.getHibernateLazyInitializer().getImplementation();
            if (proxyImpl instanceof CreditCard) {
                CreditCard cc = (CreditCard) proxyImpl;
                assertEquals("1234567890", cc.getNumber());
            } else {
                fail("Proxy is not a CreditCard");
            }
        } else {
            fail("DefaultBillingDetails is not a proxy");
        }

        //HibernateUtil.closeSession();
        BillingDetailsDAO billingDetailsDAO = new BillingDetailsDAO();
        BillingDetails billingDetails1 = billingDetailsDAO.getBillingDetailsById(1l, false);
        assertNotNull(billingDetails1);
        assertTrue(billingDetails1.getUser() == user);

        user.removeBillingDetails(billingDetails1);
        HibernateUtil.getSession().flush();

        //BillingDetails billingDetails2 = billingDetailsDAO.loadBillingDetailsById(1l, false);
        BillingDetails billingDetails2 = billingDetailsDAO.getBillingDetailsById(1l, false);
        assertNull(billingDetails2);

    }

    public static Test suite() {
        return new TestSuite(UserTest.class);
    }

    public static void main(String[] args) throws Exception {
        TestRunner.run(suite());
    }

}
