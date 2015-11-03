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
import java.util.Locale;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.log4j.Level;
import org.hibernate.auction.dao.ItemDAO;
import org.hibernate.auction.dao.UserDAO;
import org.hibernate.auction.model.Item;
import org.hibernate.auction.model.MonetaryAmount;
import org.hibernate.auction.model.User;
import org.hibernate.auction.persistence.HibernateUtil;
import org.hibernate.auction.util.LogHelper;

/**
 *
 * @author rusakovich
 */
public class UserTest extends TestCaseWithData {

    public UserTest(String x) {
        super(x);
    }

    protected void setUp() throws Exception {
        LogHelper.disableLogging();
        super.setUp();
        initData();
        LogHelper.enableLogging(Level.DEBUG);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
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

    public static Test suite() {
        return new TestSuite(UserTest.class);
    }

    public static void main(String[] args) throws Exception {
        TestRunner.run(suite());
    }

}
