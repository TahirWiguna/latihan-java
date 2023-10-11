package com.twigu.latihan.service;

import com.twigu.latihan.entity.Transaction;
import com.twigu.latihan.entity.User;
import com.twigu.latihan.entity.Wallet;
import com.twigu.latihan.enums.TrxType;
import com.twigu.latihan.helper.ValidationHelper;
import com.twigu.latihan.repository.TransactionRepository;
import com.twigu.latihan.repository.WalletRepository;
import com.twigu.latihan.request.wallet.WalletDepositRequest;
import com.twigu.latihan.request.wallet.WalletRequest;
import com.twigu.latihan.request.wallet.WalletTransferRequest;
import com.twigu.latihan.request.wallet.WalletWithdrawRequest;
import com.twigu.latihan.response.wallet.TransactionResponse;
import com.twigu.latihan.response.wallet.WalletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ValidationHelper validationHelper;

    public Wallet createWallet(WalletRequest req, User user){
        validationHelper.validate(req);

        if(walletRepository.findByNameAndUser(req.getName(), user).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name already in use");
        }

        Wallet wallet = new Wallet();
        wallet.setName(req.getName());
        wallet.setBalance(BigDecimal.valueOf(0));
        wallet.setUser(user);

        return walletRepository.save(wallet);
    }

    public List<Wallet> getAllWallet(User user){
        return walletRepository.findByUser(user);
    }

    public Wallet findWalletById(Long id, User user){
        return walletRepository.findByIdAndUser(id, user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));
    }

    @Transactional
    public Wallet deposit(WalletDepositRequest req, User user){
        validationHelper.validate(req);

        // Deposit amount to wallet
        Wallet wallet = walletRepository.findByIdAndUser(req.getIdWallet(), user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(req.getAmount()));
        wallet =  walletRepository.save(wallet);

        // Add log to transaction table
        Transaction trx = new Transaction();
        trx.setReceiverWallet(wallet);
        trx.setAmount(req.getAmount());
        trx.setTransactionType(TrxType.DEPOSIT);
        trx.setCreatedBy(user);
        transactionRepository.save(trx);

        return wallet;
    }

    @Transactional
    public TransactionResponse transfer(WalletTransferRequest req, User user){
        validationHelper.validate(req);

        // Transfer balance, and deduct balance
        Wallet from = walletRepository.findByIdAndUser(req.getFrom(), user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet tidak ditemukan"));
        if(req.getAmount().compareTo(from.getBalance()) > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient Balance!");
        }

        Wallet to = walletRepository.findById(req.getTo()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target wallet not found"));

        from.setBalance(from.getBalance().subtract(req.getAmount()));
        to.setBalance(to.getBalance().add(req.getAmount()));

        walletRepository.save(from);
        walletRepository.save(to);

        // Add log to trx
        Transaction trx = new Transaction();
        trx.setSenderWallet(from);
        trx.setReceiverWallet(to);
        trx.setAmount(req.getAmount());
        trx.setTransactionType(TrxType.TRANSFER);
        trx.setCreatedBy(user);
        trx = transactionRepository.save(trx);

        return toTransactionResponse(trx);

    }

    @Transactional
    public TransactionResponse withdraw(WalletWithdrawRequest req, User user){
        validationHelper.validate(req);

        // Withdraw balance
        Wallet wallet = walletRepository.findByIdAndUser(req.getId(), user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet tidak ditemukan"));
        if(req.getAmount().compareTo(wallet.getBalance()) > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient Balance!");
        }

        wallet.setBalance(wallet.getBalance().subtract(req.getAmount()));
        walletRepository.save(wallet);

        // Add log to trx
        Transaction trx = new Transaction();
        trx.setSenderWallet(wallet);
        trx.setAmount(req.getAmount());
        trx.setTransactionType(TrxType.WITHDRAW);
        trx.setCreatedBy(user);
        trx = transactionRepository.save(trx);

        return toTransactionResponse(trx);

    }

    public List<TransactionResponse> mutation(User user){
//        return toTransactionResponseList(transactionRepository.findBySenderWalletUserOrReceiverWalletUserOrCreatedBy(user));
        return toTransactionResponseList(transactionRepository.findByUserRelatedTransactions(user));
    }

    public WalletResponse toWalletResponse(Wallet wallet){
        return WalletResponse.builder()
                .id(wallet.getId())
                .name(wallet.getName())
                .balance(wallet.getBalance())
                .build();
    }

    public List<WalletResponse> toWalletResponseList(List<Wallet> wallets) {
        List<WalletResponse> walletResponses = new ArrayList<>();
        for (Wallet wallet : wallets) {
            walletResponses.add(toWalletResponse(wallet));
        }
        return walletResponses;
    }

    public TransactionResponse toTransactionResponse(Transaction trx){
        String senderName = (trx.getSenderWallet() != null) ? trx.getSenderWallet().getName() : null;
        String receiverName = (trx.getReceiverWallet() != null) ? trx.getReceiverWallet().getName() : null;
//
        return TransactionResponse.builder()
                .id(trx.getId())
                .amount(trx.getAmount())
                .from(senderName)
                .to(receiverName)
                .transactionType(trx.getTransactionType())
                .date(trx.getCreatedAt())
                .build();
    }

    public List<TransactionResponse> toTransactionResponseList(List<Transaction> trxs){
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        for (Transaction trx : trxs) {
            transactionResponses.add(toTransactionResponse(trx));
        }
        return transactionResponses;
    }
}
