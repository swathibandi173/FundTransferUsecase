package com.swathi.BankingFundTransfer.Mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.swathi.BankingFundTransfer.Dto.AccountDto;
import com.swathi.BankingFundTransfer.Entity.Account;
import com.swathi.BankingFundTransfer.Entity.Address;
import com.swathi.BankingFundTransfer.Entity.User;
import com.swathi.BankingFundTransfer.Entity.UserCredentials;

@Mapper(componentModel = "spring")
public interface AccountMapper
{
	@Mapping(target = "addressId", ignore = true)
	Address mapToAddress(AccountDto request);
	
	@Mapping(target = "userId", ignore = true)
	@Mapping(target = "dateOfBirth", expression = "java(java.time.LocalDate.parse(request.getDateOfBirth()))")
	@Mapping(target = "mobileNumber", expression = "java(Long.valueOf(request.mobileNumber()))")
	@Mapping(target = "creationDate", expression = "java(new java.util.Date())")
	@Mapping(target="address",source="address")
	User mapToUser(AccountDto request, Address address);
	
	
	@Mapping(target = "accountId", ignore = true)
	@Mapping(target="user",source="user")
	@Mapping(target = "availableBalance", expression = "java(Double.valueOf(request.getOpeningDeposit()))")
	@Mapping(target = "accountNo", expression = "java(accountNumberGeneration())")
	Account mapToAccount(AccountDto request,User user);
	
	
	
	@Mapping(target = "userCredentialsId", ignore = true)
	@Mapping(target = "userName", expression = "java(user.getUserName())")
	@Mapping(target = "password", expression = "java(passwordGenerator())")
	@Mapping(target="user",source="user")
	UserCredentials mapToCredentials(AccountDto request,User user);
	
	default long accountNumberGeneration() 
	 {
		  while (true)
		  {
		        long num = (long)(Math.random() * 100000000 * 100000000); // had to use this as int's are to small for a 12 digit number.
		        if (String.valueOf(num).length() == 12)
		           return num;
		  }
	  }
	
	default String passwordGenerator()
	{
		String password = UUID.randomUUID().toString().split("-")[1];
		return password;
	}

}
