/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.practice.hibernate;

import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 *
 * @author mirash
 */
public class DataSourceTest {

    private DataSource ds;

    @Before
    public void setUp() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        ds = builder.setType(EmbeddedDatabaseType.H2)
                .addScript("db/sql/create-db.sql")
                .addScript("db/sql/insert-data.sql")
                .build();
    }

    @Test
    public void testDataSource() {
        System.out.println("**** testDataSource ****");
        Assert.assertNotNull(ds);
    }
}
