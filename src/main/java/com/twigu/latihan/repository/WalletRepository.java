package com.twigu.latihan.repository;

import com.twigu.latihan.entity.User;
import com.twigu.latihan.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long>{
    List<Wallet> findByUser(User user);
    Optional<Wallet> findByIdAndUser(Long id, User user);

    Optional<Wallet> findByNameAndUser(String name, User user);
}
