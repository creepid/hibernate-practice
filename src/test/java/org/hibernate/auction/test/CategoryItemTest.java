package org.hibernate.auction.test;

import junit.framework.*;
import junit.textui.TestRunner;
import org.hibernate.auction.model.*;
import org.hibernate.auction.persistence.HibernateUtil;

import java.util.*;
import org.apache.log4j.Level;

import org.hibernate.*;
import org.hibernate.auction.util.LogHelper;

public class CategoryItemTest extends TestCaseWithData {
    
    public CategoryItemTest(String x) throws Exception {
        super(x);
    }
    
    protected void setUp() throws Exception {
        LogHelper.disableLogging();
        super.setUp();
        initData();
        LogHelper.setLogging("org.hibernate.SQL", Level.DEBUG);
    }
    
    protected void tearDown() throws Exception {
        HibernateUtil.closeSession();
        super.tearDown();
    }

    // ********************************************************** //
    public void testCompositeQuery() throws Exception {
        System.out.println("********** testCompositeQuery **********");

        // Query for Category and all categorized Items (three tables joined)
        HibernateUtil.beginTransaction();
        Session s = HibernateUtil.getSession();
        
        Query q = s.createQuery("select c from Category as c left join fetch c.categorizedItems as ci join fetch ci.item as i");
        Collection result = new HashSet(q.list());
        assertTrue(result.size() == 2);
        
        HibernateUtil.commitTransaction();
        HibernateUtil.closeSession();

        // Check initialization (should be eager fetched)
        for (Iterator it = result.iterator(); it.hasNext();) {
            Category cat = (Category) it.next();
            for (Iterator it2 = cat.getCategorizedItems().iterator(); it2.hasNext();) {
                assertTrue(it2.next() != null);
            }
        }
    }
    
    public void testDeletionFromItem() throws Exception {
        System.out.println("********** testDeletionFromItem **********");

        // Delete all links for auctionFour by clearing collection
        HibernateUtil.beginTransaction();
        Session s = HibernateUtil.getSession();
        Item i = (Item) s.get(Item.class, auctionFour.getId());
        i.getCategorizedItems().clear();
        HibernateUtil.commitTransaction();
        HibernateUtil.closeSession();

        // Check deletion
        HibernateUtil.beginTransaction();
        s = HibernateUtil.getSession();
        CategorizedItem catItem = (CategorizedItem) s.get(CategorizedItem.class,
                new CategorizedItem.Id(carsLuxury.getId(), auctionFour.getId()));
        assertTrue(catItem == null);
        HibernateUtil.commitTransaction();
        HibernateUtil.closeSession();
    }
    
    public void testDeletionFromCategory() throws Exception {
        System.out.println("********** testDeletionFromCategory **********");

        // Delete all links for auctionFour by clearing collection
        HibernateUtil.beginTransaction();
        Session s = HibernateUtil.getSession();
        Category c = (Category) s.get(Category.class, carsSUV.getId());
        c.getCategorizedItems().clear();
        HibernateUtil.commitTransaction();
        HibernateUtil.closeSession();

        // Check deletion
        HibernateUtil.beginTransaction();
        s = HibernateUtil.getSession();
        CategorizedItem catItem = (CategorizedItem) s.get(CategorizedItem.class,
                new CategorizedItem.Id(carsSUV.getId(), auctionThree.getId()));
        assertTrue(catItem == null);
        HibernateUtil.commitTransaction();
        HibernateUtil.closeSession();
        
    }
    /**
     * -- session.get() It always hit the database and return the real object,
     * an object that represent the database row, not proxy. If no row found ,
     * it return null.
     */
    public void testGetCategory() throws Exception {
        System.out.println("**********  testGetCategory **********");
        
        HibernateUtil.beginTransaction();
        Session s = HibernateUtil.getSession();
        
        Category luxCars = (Category) s.get(Category.class, carsLuxury.getId());
        Category luxStortCars = new Category("Lux sport cars");
        luxStortCars.setParentCategory(luxCars);

        //In session.get(), Hibernate will hit the database to retrieve the 
        //luxCars object and put it as a reference to luxStortCars. 
        //
        //However, this save process is extremely high demand,
        //there may be thousand or million transactions per hour, 
        //do you think is this necessary to hit the database to retrieve 
        //the luxCars object everything save a luxStortCars record? 
        //After all you just need the luxCars Id as a reference to luxStortCars.
        s.save(luxStortCars);
        assertNotNull(luxStortCars.getId());

        //It will always return null , if the identity value is not found in database.
        Category someCategory = (Category) s.get(Category.class, 666l);
        assertNull(someCategory);
    }

    /**
     * -- session.load() It will always return a “proxy” (Hibernate term)
     * without hitting the database. In Hibernate, proxy is an object with the
     * given identifier value, its properties are not initialized yet, it just
     * look like a temporary fake object. If no row found , it will throws an
     * ObjectNotFoundException.
     *
     */
    public void testLoadCategory() throws Exception {
        System.out.println("**********  testLoadCategory **********");
        
        HibernateUtil.beginTransaction();
        Session s = HibernateUtil.getSession();

        //In session.load(), Hibernate will not hit the database 
        //(no select statement in output) to retrieve the uxCars object, 
        //it will return a uxCars proxy object – a fake object with given 
        //identify value. In this scenario, a proxy object is enough for 
        //to save a stock transaction record.
        Category luxCars = (Category) s.load(Category.class, carsLuxury.getId());
        Category luxStortCars = new Category("Lux sport cars");
        luxStortCars.setParentCategory(luxCars);
        
        s.save(luxStortCars);
        assertNotNull(luxStortCars.getId());

        //it will always return a proxy object with the given identity value, 
        //even the identity value is not exists in database. 
        //However, when you try to initialize a proxy by retrieve it’s 
        //properties from database, it will hit the database with select 
        //statement. If no row is found, a ObjectNotFoundException will throw.
        Category someCategory = (Category) s.load(Category.class, 666l);
        try {
            someCategory.getName();
            fail("ObjectNotFoundException must be thrown");
        } catch (ObjectNotFoundException ex) {
            assertTrue(ex.getMessage().contains("No row with the given identifier exists"));
        }
    }

    // ********************************************************** //
    public static Test suite() {
        return new TestSuite(CategoryItemTest.class);
    }
    
    public static void main(String[] args) throws Exception {
        TestRunner.run(suite());
    }
    
}
