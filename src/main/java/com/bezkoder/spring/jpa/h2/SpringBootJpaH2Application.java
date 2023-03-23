package com.bezkoder.spring.jpa.h2;

import com.bezkoder.spring.jpa.h2.model.Tutorial;
import com.bezkoder.spring.jpa.h2.repository.TutorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@SpringBootApplication
public class SpringBootJpaH2Application {

    @Autowired
    TutorialRepository tutorialRepository;
    @PersistenceContext // or even @Autowired
    private EntityManager entityManager;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootJpaH2Application.class, args);
    }

    /**
     * This Test Case tries to check whether an Optimistic lock exception is thrown or not during dirty read of shared resource
     * Ref1 is read before
     * Ref2 has dirty read , gets exception if Ref2 updates shared resource with obsolete read reference
     * This is possible with @Version annotation on Tutorial.java
     */
    @PostConstruct
    void testIFOptimisticLockExceptionThrownInCaseOfDirtyRead() {
        Tutorial tutorial = new Tutorial();
        Tutorial orig = save(tutorial);
        System.out.println("orig ver " + orig.getVersion());
        Tutorial ref1 = tutorialRepository.findById(orig.getId()).get();
        Tutorial ref2 = tutorialRepository.findById(orig.getId()).get();
        ref1.setTitle("ref1");
        System.out.println("ref1 update ver" + save(ref1).getVersion());
        ref2.setTitle("ref2");
        System.out.println("ref2 update ver" + save(ref2).getVersion());
    }

    @Transactional
    Tutorial save(Tutorial tutorial) {
        System.out.println("Saving Obj "+ tutorial.getVersion());
        return tutorialRepository.save(tutorial);
    }

}
