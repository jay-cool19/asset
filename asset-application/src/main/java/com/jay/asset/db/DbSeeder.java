package com.jay.asset.db;

import com.jay.asset.core.AddressEntity;
import com.jay.asset.core.AssetEntity;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.UUID;

public class DbSeeder {
    private SessionFactory sessionFactory;
    private AssetDao assetDao;

    public DbSeeder(SessionFactory sessionFactory, AssetDao assetDao) {
        this.sessionFactory = sessionFactory;
        this.assetDao = assetDao;
    }

    @UnitOfWork
    public void seedSomeData() {
        Session session = sessionFactory.openSession();
        try {
            if (assetDao.findAll().isEmpty()) {
                assetDao.save(
                        new AssetEntity(getRandomSerialNumber(), "Model1000",
                                new AddressEntity
                                        .Builder()
                                        .streetName("Storgata")
                                        .houseNumber("10")
                                        .postCode("1234")
                                        .areaName("Oslo")
                                        .build()));
                assetDao.save(
                        new AssetEntity(getRandomSerialNumber(), "Model1000",
                                new AddressEntity
                                        .Builder()
                                        .streetName("Frognerveien")
                                        .houseNumber("55")
                                        .postCode("1240")
                                        .areaName("Oslo")
                                        .build()));
                assetDao.save(
                        new AssetEntity(getRandomSerialNumber(), "Model2000",
                                new AddressEntity
                                        .Builder()
                                        .streetName("Skansebakken")
                                        .houseNumber("42")
                                        .postCode("1011")
                                        .areaName("Oslo")
                                        .build()));
            }
        } finally {
            session.close();
        }
    }

    private String getRandomSerialNumber() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
