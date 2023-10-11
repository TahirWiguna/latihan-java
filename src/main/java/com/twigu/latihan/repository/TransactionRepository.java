package com.twigu.latihan.repository;

import com.twigu.latihan.entity.Transaction;
import com.twigu.latihan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Long countByCreatedBy(User user);
//    List<Transaction> findBySenderWalletUserOrReceiverWalletUserOrCreatedBy(User user);

    @Query("SELECT t FROM Transaction t " +
            "LEFT JOIN t.senderWallet sw " +
            "LEFT JOIN t.receiverWallet rw " +
            "WHERE t.createdBy = :user OR sw.user = :user OR rw.user = :user")
    List<Transaction> findByUserRelatedTransactions(@Param("user") User user);
}
