package com.example.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.server.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

}
