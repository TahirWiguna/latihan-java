package com.twigu.latihan.controller;

import com.twigu.latihan.entity.Transaction;
import com.twigu.latihan.entity.User;
import com.twigu.latihan.entity.Wallet;
import com.twigu.latihan.helper.MyRes;
import com.twigu.latihan.request.wallet.WalletDepositRequest;
import com.twigu.latihan.request.wallet.WalletRequest;
import com.twigu.latihan.request.wallet.WalletTransferRequest;
import com.twigu.latihan.request.wallet.WalletWithdrawRequest;
import com.twigu.latihan.response.wallet.TransactionResponse;
import com.twigu.latihan.response.wallet.WalletResponse;
import com.twigu.latihan.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @PostMapping()
    public ResponseEntity<MyRes<WalletResponse>> createWallet(@RequestBody WalletRequest req, User user){
        Wallet wallet = walletService.createWallet(req, user);

        WalletResponse walletResponse = walletService.toWalletResponse(wallet);
        return MyRes.created(walletResponse);
    }

    @GetMapping()
    public ResponseEntity<MyRes<List<WalletResponse>>> getAllWallet(User user){
        List<Wallet> wallet = walletService.getAllWallet(user);

        List<WalletResponse> walletResponse = walletService.toWalletResponseList(wallet);
        return MyRes.success(walletResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MyRes<WalletResponse>> getWalletById(@PathVariable Long id, User user){
        Wallet wallet = walletService.findWalletById(id, user);

        WalletResponse walletResponse = walletService.toWalletResponse(wallet);
        return MyRes.success(walletResponse);
    }
    @PutMapping("/deposit/{id}")
    public ResponseEntity<MyRes<WalletResponse>> deposit(User user, @PathVariable Long id, @RequestBody WalletDepositRequest req){
        req.setIdWallet(id);

        Wallet wallet = walletService.deposit(req, user);

        WalletResponse walletResponse = walletService.toWalletResponse(wallet);
        return MyRes.success(walletResponse);
    }

    @PostMapping("/transfer")
    public ResponseEntity<MyRes<TransactionResponse>> transfer(User user, @RequestBody WalletTransferRequest req){
        TransactionResponse trx = walletService.transfer(req, user);
        return MyRes.success(trx);
    }

    @PostMapping("/withdraw/{id}")
    public ResponseEntity<MyRes<TransactionResponse>> withdraw(User user,@PathVariable Long id, @RequestBody WalletWithdrawRequest req){
        req.setId(id);

        TransactionResponse trx = walletService.withdraw(req, user);

        return MyRes.success(trx);
    }

    @GetMapping("/mutation")
    public ResponseEntity<MyRes<List<TransactionResponse>>> mutation(User user){
        List<TransactionResponse> trx = walletService.mutation(user);
        return MyRes.success(trx);
    }
}
