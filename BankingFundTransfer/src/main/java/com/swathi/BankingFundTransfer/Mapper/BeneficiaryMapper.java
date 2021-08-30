package com.swathi.BankingFundTransfer.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.swathi.BankingFundTransfer.Dto.BeneficiaryDto;
import com.swathi.BankingFundTransfer.Entity.Beneficiary;
import com.swathi.BankingFundTransfer.Entity.User;

@Mapper(componentModel = "spring")
public interface BeneficiaryMapper 
{
	@Mapping(target = "beneficiaryId", ignore = true)
	@Mapping(target = "beneficiaryAccountNo", expression = "java(Long.valueOf(beneficiaryDto.getBeneficiaryAccountNo()))")
	@Mapping(target = "transferLimit", expression = "java(Double.valueOf(beneficiaryDto.getTransferLimit()))")
	
	//@Mapping(target = "transactionType", expression = "java(com.training.pbpay.constant.AppConstant.DEBIT)")
	//@Mapping(target = "availableBalance", expression = "java(sourceAccount.getBalance()-transactionRequestDto.getTransactionAmount())")
	Beneficiary map(BeneficiaryDto beneficiaryDto,User user);
	
}