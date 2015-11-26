/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hibernate.auction.test;

import java.util.Date;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.log4j.Level;
import org.hibernate.Session;
import org.hibernate.auction.model.Stock;
import org.hibernate.auction.model.StockDailyRecord;
import org.hibernate.auction.persistence.HibernateUtil;
import org.hibernate.auction.util.LogHelper;

/**
 *
 * @author rusakovich
 */
public class OneToManyTest extends TestCaseWithData {

    public OneToManyTest(String x) {
        super(x);
        LogHelper.disableLogging();
    }

    public static Test suite() {
        return new TestSuite(OneToManyTest.class);
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

        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();

        LogHelper.setLogging("org.hibernate.SQL", Level.DEBUG);

        Stock stock = new Stock();
        stock.setStockCode("7052");
        stock.setStockName("PADINI");
        session.save(stock);

        StockDailyRecord stockDailyRecords = new StockDailyRecord();
        stockDailyRecords.setPriceOpen(new Float("1.2"));
        stockDailyRecords.setPriceClose(new Float("1.1"));
        stockDailyRecords.setPriceChange(new Float("10.0"));
        stockDailyRecords.setVolume(3000000L);
        stockDailyRecords.setDate(new Date());

        stockDailyRecords.setStock(stock);
        stock.getStockDailyRecords().add(stockDailyRecords);

        session.save(stockDailyRecords);

        session.getTransaction().commit();

    }

}
