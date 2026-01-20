package com.ahmadmouslimani.account.mapper;

import com.ahmadmouslimani.account.dto.AccountRequestDTO;
import com.ahmadmouslimani.account.dto.AccountResponseDTO;
import com.ahmadmouslimani.account.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account mapToEntity(AccountRequestDTO dto) {
        if (dto == null) return null;

        Account account = new Account();
        account.setBalance(dto.balance());
        account.setStatus(dto.status());

        return account;
    }

    public AccountResponseDTO mapToDto(Account account) {
        if (account == null) return null;
        return new AccountResponseDTO(
                account.getId(),
                account.getStatus(),
                account.getBalance()
        );
    }
}
