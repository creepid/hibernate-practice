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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import junit.framework.Test;
import static junit.framework.TestCase.assertNotNull;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.log4j.Level;
import org.hibernate.Hibernate;
import org.hibernate.Session;
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
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.metadata.ClassMetadata;
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

    /**
     * There are four fetching strategies
     *
     * 1. fetch-“join” = Disable the lazy loading, always load all the
     * collections and entities.
     *
     * 2. fetch-“select” (default) = Lazy load all the collections and entities.
     *
     * 3. batch-size=”N” = Fetching up to ‘N’ collections or entities, *Not
     * record*.
     *
     * 4. fetch-“subselect” = Group its collection into a sub select statement.
     */
    /**
     * fetch=”select” or @Fetch(FetchMode.SELECT). This is the default fetching
     * strategy. it enabled the lazy loading of all it’s related collections.
     * Let see the example…
     *
     */
    /**
     * fetch=”join” or @Fetch(FetchMode.JOIN). The “join” fetching strategy will
     * disabled the lazy loading of all it’s related collections. Let see the
     * example.
     *
     */
    public void testUserFetchingStrategies() {
        System.out.println("******************** testUserFetchingStrategies *******************");
        UserDAO userDAO = new UserDAO();
        User user = userDAO.loadUserById(1l, false);

        for (Iterator iter = user.getItems().iterator(); iter.hasNext();) {
            Item item = (Item) iter.next();
            assertNotNull(item.getId());
            assertNotNull(item.getName());
        }

    }

    /**
     * batch-size=”10″ or @BatchSize(size = 10). This ‘batch size’ fetching
     * strategy is always misunderstanding by many Hibernate developers. Let see
     * the *misunderstand* concept here… The batch-size did nothing here, it is
     * not how batch-size work. See this statement.
     *
     * — Repeat N times until you remember this statement —
     *
     * The batch-size fetching strategy is not define how many records inside in
     * the collections are loaded. Instead, it defines how many collections
     * should be loaded.
     *
     * — Repeat N times until you remember this statement —
     *
     *
     */
    /**
     * No batch-size fetching strategy.
     *
     * If you have 20 users records in the database, the Hibernate’s default
     * fetching strategies will generate 20+1 select statements and hit the
     * database.
     *
     * 1. Select statement to retrieve all the User records.
     *
     * 2. Select its related collection
     *
     * 3. Select its related collection
     *
     * 4. Select its related collection
     *
     * ….
     *
     * 21.Select its related collection
     *
     * The generated queries are not efficient and caused a serious performance
     * issue.
     *
     */
    /**
     * Enabled the batch-size=’10’ fetching strategy.
     *
     * Now, Hibernate will per-fetch the collections, with a select *in*
     * statement. If you have 20 stock records, it will generate 3 select
     * statements.
     *
     * 1. Select statement to retrieve all the Stock records.
     *
     * 2. Select In statement to per-fetch its related collections (10
     * collections a time)
     *
     * 3. Select In statement to per-fetch its related collections (next 10
     * collections a time)
     *
     * With batch-size enabled, it simplify the select statements from 21 select
     * statements to 3 select statements.
     */
    /**
     * fetch=”subselect” or @Fetch(FetchMode.SUBSELECT).
     *
     * This fetching strategy is enable all its related collection in a sub
     * select statement. Let see the same query again.
     *
     * With “subselect” enabled, it will create two select statements.
     *
     * 1. Select statement to retrieve all the Stock records.
     *
     * 2. Select all its related collections in a sub select query.
     */
    @org.junit.Test
    public void testUserBatchItems() {
        System.out.println("******************* testUserBatchItems ********************");
        Session session = HibernateUtil.getSession();
        List<User> list = session.createQuery("from User").list();

        for (User user : list) {
            Set items = user.getItems();

            for (Iterator iter = items.iterator(); iter.hasNext();) {
                Item item = (Item) iter.next();
                assertNotNull(item.getId());
                assertNotNull(item.getName());
            }

        }
    }
    
    public void /*test*/AddNewProperty() {
        System.out.println("******************* testAddNewProperty ********************");
        PersistentClass userMapping = HibernateUtil.getClassMapping(User.class);

        Column column = new Column();
        column.setName("MOTTO");
        column.setNullable(false);
        column.setUnique(true);
        column.setSqlType("VARCHAR");
        userMapping.getTable().addColumn(column);

        SimpleValue value = new SimpleValue();
        value.setTable(userMapping.getTable());
        value.addColumn(column);
        value.setTypeName("string");

        Property prop = new Property();
        prop.setValue(value);
        prop.setName("motto");
        prop.setPropertyAccessorName("field");
        prop.setNodeName(prop.getName());
        userMapping.addProperty(prop);
        
        HibernateUtil.rebuildSessionFactory();

        ClassMetadata metadata = HibernateUtil.getClassMetadata(User.class);
        String[] propNames = metadata.getPropertyNames();
        boolean mottoFound = false;
        for (int i = 0; i < propNames.length; i++) {
            String propName = propNames[i];
            if (propName.equalsIgnoreCase("motto")) {
                mottoFound = true;
                break;
            }
        }

        assertTrue(mottoFound);
    }

    public static Test suite() {
        return new TestSuite(UserTest.class);
    }

    public static void main(String[] args) throws Exception {
        TestRunner.run(suite());
    }

}
