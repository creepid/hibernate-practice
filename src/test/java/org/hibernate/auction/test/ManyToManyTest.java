/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hibernate.auction.test;

import java.util.HashSet;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.log4j.Level;
import org.hibernate.Session;
import org.hibernate.auction.model.Category;
import org.hibernate.auction.model.Stock;
import org.hibernate.auction.persistence.HibernateUtil;
import org.hibernate.auction.util.LogHelper;

/**
 *
 * @author rusakovich
 */
public class ManyToManyTest extends TestCaseWithData {

    public ManyToManyTest(String x) {
        super(x);
        LogHelper.disableLogging();
    }

    public static Test suite() {
        return new TestSuite(ManyToManyTest.class);
    }

    public static void main(String[] args) throws Exception {
        TestRunner.run(suite());
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
        HibernateUtil.closeSession();
    }

    /**
     * The main difficulty in this one-to-one relationship is ensuring both are
     * assigned the same primary key. In StockDetail.hbm.xml, a special foreign
     * identifier generator is declared, it will know get the primary key value
     * from STOCK table. With constrained=”true”, it ensure the Stock must
     * exists.*
     */
    public void testStockAndStockDetailsOneToOne() {
        System.out.println("********** test **********");

        System.out.println("Hibernate many to many (XML Mapping)");
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();

        LogHelper.setLogging("org.hibernate.SQL", Level.DEBUG);

        Stock stock = new Stock();
        stock.setStockCode("7052");
        stock.setStockName("PADINI");

        Category category1 = new Category("CONSUMER");
        Category category2 = new Category("INVESTMENT");

        Set<Category> categories = new HashSet<Category>();
        categories.add(category1);
        categories.add(category2);

        stock.setCategories(categories);

        session.save(stock);

        session.getTransaction().commit();
        System.out.println("Done");
    }

}
