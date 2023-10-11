package com.twigu.latihan.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twigu.latihan.entity.User;
import com.twigu.latihan.entity.Wallet;
import com.twigu.latihan.helper.BCrypt;
import com.twigu.latihan.helper.MyRes;
import com.twigu.latihan.repository.TransactionRepository;
import com.twigu.latihan.repository.UserRepository;
import com.twigu.latihan.repository.WalletRepository;
import com.twigu.latihan.request.wallet.WalletDepositRequest;
import com.twigu.latihan.request.wallet.WalletRequest;
import com.twigu.latihan.request.wallet.WalletTransferRequest;
import com.twigu.latihan.request.wallet.WalletWithdrawRequest;
import com.twigu.latihan.response.wallet.TransactionResponse;
import com.twigu.latihan.response.wallet.WalletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User globalUser;

    private User globalUser2;

    private Wallet globalUserWallet;

    private Wallet globalUserWallet2;

    private Wallet globalUser2Wallet;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();

        globalUser = new User();
        globalUser.setName("AsepGlobal");
        globalUser.setEmail("asep.global@gmail.com");
        globalUser.setUsername("asep.global");
        globalUser.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        globalUser.setToken(UUID.randomUUID().toString());
        globalUser.setTokenExpiredAt(System.currentTimeMillis() + 3600 * 1000);
        globalUser = userRepository.save(globalUser);

        globalUserWallet = new Wallet();
        globalUserWallet.setUser(globalUser);
        globalUserWallet.setIsDefault(true);
        globalUserWallet.setName("Wallet Utama");
        globalUserWallet.setBalance(BigDecimal.valueOf(0));
        globalUserWallet = walletRepository.save(globalUserWallet);

        globalUserWallet2 = new Wallet();
        globalUserWallet2.setUser(globalUser);
        globalUserWallet2.setIsDefault(true);
        globalUserWallet2.setName("Wallet Kedua");
        globalUserWallet2.setBalance(BigDecimal.valueOf(10000));
        globalUserWallet2 = walletRepository.save(globalUserWallet2);

        globalUser2 = new User();
        globalUser2.setName("AsepGlobal2");
        globalUser2.setEmail("asep.global2@gmail.com");
        globalUser2.setUsername("asep.global2");
        globalUser2.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        globalUser2.setToken(UUID.randomUUID().toString());
        globalUser2.setTokenExpiredAt(System.currentTimeMillis() + 3600 * 1000);
        globalUser2 = userRepository.save(globalUser2);

        globalUser2Wallet = new Wallet();
        globalUser2Wallet.setUser(globalUser2);
        globalUser2Wallet.setIsDefault(true);
        globalUser2Wallet.setName("Wallet Kedua");
        globalUser2Wallet.setBalance(BigDecimal.valueOf(0));
        globalUser2Wallet = walletRepository.save(globalUser2Wallet);
    }

    @Test
    void createWalletSuccess() throws Exception {
        WalletRequest req = new WalletRequest();
        req.setName("Wallet Ketiga");

        mockMvc.perform(post("/api/wallet").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            MyRes<WalletResponse> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertEquals(res.getData().getName(),req.getName());
        });
    }
    @Test
    void createWalletUnauthorized() throws Exception {
        WalletRequest req = new WalletRequest();
        req.setName("Wallet Kedua");

        mockMvc.perform(post("/api/wallet").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        ).andExpectAll(
                status().isUnauthorized()
        );
    }

    @Test
    void createWalletExist() throws Exception {
        WalletRequest req = new WalletRequest();
        req.setName("Wallet Utama");

        mockMvc.perform(post("/api/wallet").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
                status().isBadRequest()
        );
    }
    @Test
    void createWalletFormValidation() throws Exception {
        WalletRequest req = new WalletRequest();

        mockMvc.perform(post("/api/wallet").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
                status().isUnprocessableEntity()
        );
    }

    @Test
    void getAllWalletSuccess() throws Exception {
        mockMvc.perform(get("/api/wallet")
                .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            MyRes<List<WalletResponse>> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertEquals(res.getData().size(), 2);
        });
    }
    @Test
    void getAllWalletUnauthorized() throws Exception {
        mockMvc.perform(get("/api/wallet")
        ).andExpectAll(
            status().isUnauthorized()
        );
    }

    @Test
    void getWalletByIdSuccess() throws Exception {
        mockMvc.perform(get("/api/wallet/"+globalUser2Wallet.getId())
            .header("X-API-TOKEN",globalUser2.getToken())
        ).andExpectAll(
            status().isOk()
        );
    }

    @Test
    void getWalletByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/wallet/22")
            .header("X-API-TOKEN",globalUser2.getToken())
        ).andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void getWalletBySomeoneId() throws Exception {
        mockMvc.perform(get("/api/wallet/" + globalUserWallet.getId())
            .header("X-API-TOKEN",globalUser2.getToken())
        ).andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void depositSuccess() throws Exception {
        Long count1 = transactionRepository.countByCreatedBy(globalUser);

        WalletDepositRequest req = new WalletDepositRequest();
        req.setAmount(new BigDecimal("50000.00"));

        mockMvc.perform(put("/api/wallet/deposit/" + globalUserWallet.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req))
            .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            MyRes<WalletResponse> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertEquals(req.getAmount(), res.getData().getBalance());

            Long count2 = transactionRepository.countByCreatedBy(globalUser);
            assertEquals(count2,count1 + 1);
        });
    }

    @Test
    void depositUnauthorized() throws Exception {
        mockMvc.perform(put("/api/wallet/deposit/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        );
    }

    @Test
    void depositValidationError() throws Exception {
        WalletDepositRequest req = new WalletDepositRequest();

        mockMvc.perform(put("/api/wallet/deposit/" + globalUserWallet.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req))
            .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
            status().isUnprocessableEntity()
        );
    }

    @Test
    void depositSomeoneWallet() throws Exception {
        WalletDepositRequest req = new WalletDepositRequest();
        req.setAmount(new BigDecimal("50000.00"));

        mockMvc.perform(put("/api/wallet/deposit/" + globalUser2Wallet.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req))
            .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void transferSuccess() throws Exception {
        Long count1 = transactionRepository.countByCreatedBy(globalUser);

        WalletTransferRequest req = new WalletTransferRequest();
        req.setAmount(new BigDecimal("1000.00"));
        req.setFrom(globalUserWallet2.getId());
        req.setTo(globalUser2Wallet.getId());

        mockMvc.perform(post("/api/wallet/transfer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req))
            .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            MyRes<TransactionResponse> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

            Wallet cur = walletRepository.findById(globalUserWallet2.getId()).orElseThrow();
            assertEquals(globalUserWallet2.getBalance().subtract(new BigDecimal("1000.00")),cur.getBalance());

            Wallet target = walletRepository.findById(globalUser2Wallet.getId()).orElseThrow();
            assertEquals(globalUser2Wallet.getBalance().add(new BigDecimal("1000.00")),target.getBalance());

            Long count2 = transactionRepository.countByCreatedBy(globalUser);
            assertEquals(count2,count1 + 1);
        });
    }

    @Test
    void transferUnauthorized() throws Exception {
        WalletTransferRequest req = new WalletTransferRequest();
        req.setAmount(globalUserWallet2.getBalance().add(new BigDecimal("1000")));
        req.setFrom(globalUserWallet2.getId());
        req.setTo(globalUser2Wallet.getId());

        mockMvc.perform(post("/api/wallet/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        ).andExpectAll(
                status().isUnauthorized()
        );
    }

    @Test
    void transferValidationError() throws Exception {
        WalletDepositRequest req = new WalletDepositRequest();

        mockMvc.perform(post("/api/wallet/transfer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req))
            .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
            status().isUnprocessableEntity()
        );
    }

    @Test
    void transferInsufficientBalance() throws Exception {
        WalletTransferRequest req = new WalletTransferRequest();
        req.setAmount(new BigDecimal("100000000.00"));
        req.setFrom(globalUserWallet2.getId());
        req.setTo(globalUser2Wallet.getId());

        mockMvc.perform(post("/api/wallet/transfer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req))
            .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
            status().isBadRequest()
        );
    }

    @Test
    void transferFromSomeoneWallet() throws Exception {
        WalletTransferRequest req = new WalletTransferRequest();
        req.setAmount(new BigDecimal("1000.00"));
        req.setFrom(globalUserWallet2.getId());
        req.setTo(globalUser2Wallet.getId());

        mockMvc.perform(post("/api/wallet/transfer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req))
            .header("X-API-TOKEN",globalUser2.getToken())
        ).andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void withdrawSuccess() throws Exception {
        Long count1 = transactionRepository.countByCreatedBy(globalUser);

        WalletWithdrawRequest req = new WalletWithdrawRequest();
        req.setAmount(new BigDecimal("1000.00"));

        mockMvc.perform(post("/api/wallet/withdraw/"+globalUserWallet2.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req))
            .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
            status().isOk()
        ).andDo(result -> {
            MyRes<TransactionResponse> res = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

            Wallet cur = walletRepository.findById(globalUserWallet2.getId()).orElseThrow();
            assertEquals(globalUserWallet2.getBalance().subtract(new BigDecimal("1000.00")), cur.getBalance());

            Long count2 = transactionRepository.countByCreatedBy(globalUser);
            assertEquals(count2,count1 + 1);
        });
    }

    @Test
    void withdrawUnauthorized() throws Exception {

        mockMvc.perform(post("/api/wallet/withdraw/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        );
    }

    @Test
    void withdrawValidationError() throws Exception {
        WalletDepositRequest req = new WalletDepositRequest();

        mockMvc.perform(post("/api/wallet/transfer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req))
            .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
            status().isUnprocessableEntity()
        );
    }

    @Test
    void withdrawInsufficientBalance() throws Exception {
        WalletWithdrawRequest req = new WalletWithdrawRequest();
        req.setAmount(new BigDecimal("50000000"));

        mockMvc.perform(post("/api/wallet/withdraw/"+globalUserWallet2.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req))
            .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
            status().isBadRequest()
        );
    }

    @Test
    void withdrawFromSomeoneWallet() throws Exception {
        WalletWithdrawRequest req = new WalletWithdrawRequest();
        req.setAmount(new BigDecimal("1000.00"));

        mockMvc.perform(post("/api/wallet/withdraw/3")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req))
            .header("X-API-TOKEN",globalUser2.getToken())
        ).andExpectAll(
            status().isNotFound()
        );
    }

    @Test
    void mutationSuccess() throws Exception {

        mockMvc.perform(get("/api/wallet/mutation")
            .header("X-API-TOKEN",globalUser.getToken())
        ).andExpectAll(
            status().isOk()
        );
    }

    @Test
    void mutationUnauthorized() throws Exception {

        mockMvc.perform(get("/api/wallet/mutation")
        ).andExpectAll(
            status().isUnauthorized()
        );
    }
}