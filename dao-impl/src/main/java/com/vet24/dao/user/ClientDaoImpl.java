package com.vet24.dao.user;


import com.vet24.dao.ReadWriteDaoImpl;
import com.vet24.models.user.Client;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

@Repository
public class ClientDaoImpl extends ReadWriteDaoImpl<Long, Client> implements ClientDao {

    @Override
    public Client getClientByLogin(String login) {
        try {
            return manager
                    .createQuery("from Client where login =:login", Client.class)
                    .setParameter("login", login).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}