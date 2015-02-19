package com.team2.jax.certificates;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.spaceprogram.simplejpa.EntityManagerFactoryImpl;

public class CertificateRepositorySimple implements CertificateRepository {
    private static Map<String, String> properties = new HashMap<String, String>();
    static {
        //properties.put("lobBucketName", S3StorageManager.getKey().toLowerCase() + "-travellog-lob" + StageUtils.getResourceSuffixForCurrentStage());
    }

    private static EntityManagerFactoryImpl factory = new EntityManagerFactoryImpl("Certificate", properties);
    EntityManager em = factory.createEntityManager();
    
	public Certificate findByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public Certificate create(Certificate certificate) {
		// TODO Auto-generated method stub
		return null;
	}
    
    
}
