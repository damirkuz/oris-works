//package ru.kuzdikenov.repository;
//
//import org.hibernate.SessionFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Repository;
//import ru.kuzdikenov.model.User;
//
//import java.util.List;
//
//@Repository
//public class UserRepositoryHiber {
//
//    private final SessionFactory sessionFactory;
//
//    public UserRepositoryHiber(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    public List<User> findAll() {
//        return sessionFactory.getCurrentSession().createQuery("from User").list();
//    }
//
//    public void add(User user) {
//        sessionFactory.getCurrentSession().persist(user);
//    }
//}