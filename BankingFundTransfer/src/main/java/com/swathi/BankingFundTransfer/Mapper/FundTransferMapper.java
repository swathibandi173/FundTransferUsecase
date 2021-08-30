package com.swathi.BankingFundTransfer.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.swathi.BankingFundTransfer.Dto.FundTransferDto;
import com.swathi.BankingFundTransfer.Entity.Account;
import com.swathi.BankingFundTransfer.Entity.Beneficiary;
import com.swathi.BankingFundTransfer.Entity.Transaction;

@Mapper(componentModel="spring")
public interface FundTransferMapper
{
	public static final String DEBIT = "DEBIT";
	
	@Mapping(target = "transactionTime", expression = "java( new java.sql.Timestamp(new java.util.Date().getTime()))")
	@Mapping(target = "fromAccount", expression = "java(account.getAccountNo())")
	@Mapping(target = "toAccount", expression = "java(benefiAcct.getBeneficiaryAccountNo())")
	@Mapping(target = "amount", expression = "java(Double.valueOf(fundTransDto.getTransferAmount()))")
	Transaction mapToTransaction(FundTransferDto fundTransDto, Account account,Beneficiary benefiAcct);
	
	@Mapping(target = "availableBalance", expression = "java(account.getAvailableBalance()-Double.valueOf(fundTransDto.getTransferAmount()))")
	Account mapToAccount(FundTransferDto fundTransDto,Account account);
}