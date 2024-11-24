package com.UIStride.controller;

import com.UIStride.model.Account;
import com.UIStride.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;


    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Account>> createAccount(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email
    ) {
        // Create a new Account object from the request parameters
        Account account = new Account(username, password, email);


        // Validate the account with regex
        if (!account.validate()) {
            return new ResponseEntity<>(
                    new BaseResponse<>(false, "Invalid email or password format", null),
                    HttpStatus.BAD_REQUEST
            );
        }

        // If validation passes, save the account
        Account savedAccount = accountService.saveAccount(account);
        return new ResponseEntity<>(
                new BaseResponse<>(true, "Account created successfully", savedAccount),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<Account>> login(
            @RequestParam String email,
            @RequestParam String password) {

        Optional<Account> account = accountService.findAccountByEmail(email);

        if (account.isEmpty()) {
            return new ResponseEntity<>(
                    new BaseResponse<>(false, "Account not found", null),
                    HttpStatus.NOT_FOUND
            );
        }

        if (!account.get().getPassword().equals(password)) {
            return new ResponseEntity<>(
                    new BaseResponse<>(false, "Incorrect password", null),
                    HttpStatus.UNAUTHORIZED
            );
        }

        return new ResponseEntity<>(
                new BaseResponse<>(true, "Login successful", account.get()),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Account>> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountService.getAccountById(id);

        if (account.isPresent()) {
            return new ResponseEntity<>(
                    new BaseResponse<>(true, "Account found", account.get()),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(
                    new BaseResponse<>(false, "Account not found", null),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<Account>>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(
                new BaseResponse<>(true, "Accounts retrieved successfully", accounts),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Account>> updateAccount(
            @PathVariable Long id, @RequestBody Account updatedAccount) {

        // Validasi akun dengan regex
        if (!updatedAccount.validate()) {
            return new ResponseEntity<>(
                    new BaseResponse<>(false, "Invalid email or password format", null),
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<Account> account = accountService.getAccountById(id);

        if (account.isPresent()) {
            Account savedAccount = accountService.updateAccount(id, updatedAccount);
            return new ResponseEntity<>(
                    new BaseResponse<>(true, "Account updated successfully", savedAccount),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(
                    new BaseResponse<>(false, "Account not found", null),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteAccount(@PathVariable Long id) {
        boolean deleted = accountService.deleteAccount(id);

        if (deleted) {
            return new ResponseEntity<>(
                    new BaseResponse<>(true, "Account deleted successfully", null),
                    HttpStatus.NO_CONTENT
            );
        } else {
            return new ResponseEntity<>(
                    new BaseResponse<>(false, "Account not found", null),
                    HttpStatus.NOT_FOUND
            );
        }
    }
}
