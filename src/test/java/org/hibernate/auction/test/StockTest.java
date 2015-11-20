/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hibernate.auction.test;

import java.util.Date;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import junit.framework.Test;
import org.apache.log4j.Level;
import org.hibernate.Session;
import org.hibernate.auction.model.Stock;
import org.hibernate.auction.model.StockDetail;
import org.hibernate.auction.persistence.HibernateUtil;
import org.hibernate.auction.util.LogHelper;

/**
 *
 * @author rusakovich
 */
public class StockTest extends TestCaseWithData {

    public StockTest(String x) {
        super(x);
        LogHelper.disableLogging();
    }

    public static Test suite() {
        return new TestSuite(StockTest.class);
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
        System.out.println("********** testStockAndStockDetailsOneToOne **********");

        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();

        Stock stock = new Stock();

        stock.setStockCode("4715");
        stock.setStockName("GENM");

        StockDetail stockDetail = new StockDetail();
        stockDetail.setCompName("GENTING Malaysia");
        stockDetail.setCompDesc("Best resort in the world");
        stockDetail.setRemark("Nothing Special");
        stockDetail.setListedDate(new Date());

        stock.setStockDetail(stockDetail);
        stockDetail.setStock(stock);

        session.save(stock);
        session.getTransaction().commit();

        session.flush();
        session.close();

        LogHelper.setLogging("org.hibernate.SQL", Level.DEBUG);
        session = HibernateUtil.getSessionFactory().openSession();
        Stock oneStock = (Stock) session.load(Stock.class, 1);

        assertNotNull(oneStock);
        assertEquals("GENM", oneStock.getStockName());
        assertNotNull(oneStock.getStockDetail());
        assertEquals("GENTING Malaysia", oneStock.getStockDetail().getCompName());

    }

}
