/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hibernate.auction.dao;

import java.util.Collection;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.auction.exceptions.InfrastructureException;
import org.hibernate.auction.model.BillingDetails;
import org.hibernate.auction.persistence.HibernateUtil;
import org.hibernate.criterion.Example;

/**
 *
 * @author rusakovich
 */
public class BillingDetailsDAO {

    public BillingDetailsDAO() {
        HibernateUtil.beginTransaction();
    }

    // ********************************************************** //
    public BillingDetails loadBillingDetailsById(Long billingDetailsId, boolean lock)
            throws InfrastructureException {

        Session session = HibernateUtil.getSession();
        BillingDetails billingDetails = null;
        try {
            if (lock) {
                billingDetails = (BillingDetails) session.load(BillingDetails.class, billingDetailsId, LockMode.UPGRADE);
            } else {
                billingDetails = (BillingDetails) session.load(BillingDetails.class, billingDetailsId);
            }
        } catch (HibernateException ex) {
            throw new InfrastructureException(ex);
        }
        return billingDetails;
    }

    public BillingDetails getBillingDetailsById(Long billingDetailsId, boolean lock)
            throws InfrastructureException {

        Session session = HibernateUtil.getSession();
        BillingDetails billingDetails = null;
        try {
            if (lock) {
                billingDetails = (BillingDetails) session.get(BillingDetails.class, billingDetailsId, LockMode.UPGRADE);
            } else {
                billingDetails = (BillingDetails) session.get(BillingDetails.class, billingDetailsId);
            }
        } catch (HibernateException ex) {
            throw new InfrastructureException(ex);
        }
        return billingDetails;
    }

    // ********************************************************** //
    public Collection findAll()
            throws InfrastructureException {

        Collection billingDetailss;
        try {
            billingDetailss = HibernateUtil.getSession().createCriteria(BillingDetails.class).list();
        } catch (HibernateException ex) {
            throw new InfrastructureException(ex);
        }
        return billingDetailss;
    }

    // ********************************************************** //
    public Collection findByExample(BillingDetails exampleBillingDetails)
            throws InfrastructureException {

        Collection billingDetailss;
        try {
            Criteria crit = HibernateUtil.getSession().createCriteria(BillingDetails.class);
            billingDetailss = crit.add(Example.create(exampleBillingDetails)).list();
        } catch (HibernateException ex) {
            throw new InfrastructureException(ex);
        }
        return billingDetailss;
    }

    // ********************************************************** //
    public void makePersistent(BillingDetails billingDetails)
            throws InfrastructureException {

        try {
            HibernateUtil.getSession().saveOrUpdate(billingDetails);
        } catch (HibernateException ex) {
            throw new InfrastructureException(ex);
        }
    }

    // ********************************************************** //
    public void makeTransient(BillingDetails billingDetails)
            throws InfrastructureException {

        try {
            HibernateUtil.getSession().delete(billingDetails);
        } catch (HibernateException ex) {
            throw new InfrastructureException(ex);
        }
    }

    public void evict(BillingDetails billingDetails)
            throws InfrastructureException {
        try {
            HibernateUtil.getSession().evict(billingDetails);
        } catch (HibernateException ex) {
            throw new InfrastructureException(ex);
        }
    }

}
