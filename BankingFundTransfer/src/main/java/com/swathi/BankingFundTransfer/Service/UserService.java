package com.swathi.BankingFundTransfer.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.swathi.BankingFundTransfer.Dto.AccountDto;
import com.swathi.BankingFundTransfer.Dto.BeneficiaryDto;
import com.swathi.BankingFundTransfer.Dto.FundTransferDto;
import com.swathi.BankingFundTransfer.Entity.Account;
import com.swathi.BankingFundTransfer.Entity.Address;
import com.swathi.BankingFundTransfer.Entity.Beneficiary;
import com.swathi.BankingFundTransfer.Entity.Transaction;
import com.swathi.BankingFundTransfer.Entity.User;
import com.swathi.BankingFundTransfer.Entity.UserCredentials;
import com.swathi.BankingFundTransfer.Exception.InSufficientFundException;
import com.swathi.BankingFundTransfer.Exception.InvalidCredentialsException;
import com.swathi.BankingFundTransfer.Exception.ResourceNotFoundException;
import com.swathi.BankingFundTransfer.Exception.TransferLimitException;
import com.swathi.BankingFundTransfer.Exception.UserNameAlreadyExistsException;
import com.swathi.BankingFundTransfer.Mapper.AccountMapper;
import com.swathi.BankingFundTransfer.Mapper.BeneficiaryMapper;
import com.swathi.BankingFundTransfer.Mapper.FundTransferMapper;
import com.swathi.BankingFundTransfer.Repository.AccountRepository;
import com.swathi.BankingFundTransfer.Repository.AddressRepository;
import com.swathi.BankingFundTransfer.Repository.BeneficiaryRepository;
import com.swathi.BankingFundTransfer.Repository.TransactionRepository;
import com.swathi.BankingFundTransfer.Repository.UserCredentialsRepository;
import com.swathi.BankingFundTransfer.Repository.UserRepository;
import com.swathi.BankingFundTransfer.Response.AccountCreationAcknowledgement;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private AddressRepository addrRepo;
	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private UserCredentialsRepository userCredRepo;
	@Autowired
	private BeneficiaryRepository beneficiaryRepo;
	@Autowired
	private TransactionRepository transRepo;
	@Autowired
	private BeneficiaryMapper beneficiaryMapper;
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private FundTransferMapper fundTransferMapper;


	
	public ResponseEntity<AccountCreationAcknowledgement> saveUserDetails(AccountDto request)
			throws UserNameAlreadyExistsException {
		logger.info("inside saveCustomerDetails  method");
		Date date = new Date();
		logger.info("Checking for if User Name exists.");
		if (Optional.ofNullable(userRepo.findByUserName(request.getUserName())).isPresent())
			throw new UserNameAlreadyExistsException("User Name already exists.Please try with another user name");
		
		Address  addr =addrRepo.save(accountMapper.mapToAddress(request));
		logger.info("saved address entity");
		
		User user = userRepo.save(accountMapper.mapToUser(request, addr));
		logger.info("saved  user entity");
		
		accountRepo.save(accountMapper.mapToAccount(request, user));
		logger.info("saved account entity");
		
		UserCredentials credentials=userCredRepo.save(accountMapper.mapToCredentials(request, user));
		logger.info("saved customer credentials entity");
		StringBuffer strMsg = new StringBuffer();
		strMsg.append("Your Login details ").append("User Name: " + credentials.getUserName())
				.append(" Password: " + credentials.getPassword());
		return new ResponseEntity<>(new AccountCreationAcknowledgement(strMsg.toString()), HttpStatus.OK);
	}
	
	public ResponseEntity<String> saveBeneficiary(BeneficiaryDto dto, String userName) {
		logger.info("inside saveBeneficiary  method");
		logger.info("Checking for if customer exists.");
		User user = Optional.ofNullable(userRepo.findByUserName(userName))
				.orElseThrow(() -> new ResourceNotFoundException("User", "User Name", userName));
		beneficiaryRepo.save(beneficiaryMapper.map(dto, user));
		return new ResponseEntity<>("Successfully added Beneficiary", HttpStatus.OK);
	}

	public ResponseEntity<String> checkLogindetails(UserCredentials credentials) {
		logger.info("inside checkLoginCredential method");
		logger.info("Checking for if customer credentials");
		
		if (Optional.ofNullable(
				userCredRepo.findByUserNameAndPassword(credentials.getUserName(), credentials.getPassword()))
				.isEmpty()) {
			throw new InvalidCredentialsException("Authetication Failed! Please provide valid User Name or Password ");
		}
		return new ResponseEntity<>("Authetication Success", HttpStatus.OK);
	}
	
	
	public ResponseEntity<String> fundTransfer(FundTransferDto fundTransDto, String userName) {
		User user = Optional.ofNullable(userRepo.findByUserName(userName))
				.orElseThrow(() -> new ResourceNotFoundException("User", "Customer User Name", userName));
		// Check is the account number exists, if does not exists return error message
		// else continue .
		Account fromAccDetails = Optional
				.ofNullable(accountRepo.findByAccountNoAndUserUserId(
						Long.valueOf(fundTransDto.getFromAccount()), user.getUserId()))
				.orElseThrow(() -> new ResourceNotFoundException("Account Details", "Account Number",
						fundTransDto.getFromAccount()));

		Beneficiary toAccDetails = Optional
				.ofNullable(beneficiaryRepo.findByBeneficiaryAccountNoAndUserUserId(
						Long.valueOf(fundTransDto.getToAccount()), user.getUserId()))
				.orElseThrow(() -> new ResourceNotFoundException("Customers Beneficiary Account Details  ", "Account Number",
						fundTransDto.getToAccount()));

		if (Double.valueOf(fundTransDto.getTransferAmount()) > fromAccDetails.getAvailableBalance()) {
			throw new InSufficientFundException(
					"InSufficent balance for the account number::" + fromAccDetails.getAccountNo());
		}

		if (Double.valueOf(fundTransDto.getTransferAmount()) > toAccDetails.getTransferLimit()) {
			throw new TransferLimitException("Transfer Limit Excessed for this benificary account number::"
					+ toAccDetails.getBeneficiaryAccountNo()+" Maximum tranfer Limit: "+toAccDetails.getTransferLimit());
		}

		transRepo.save(fundTransferMapper.mapToTransaction(fundTransDto, fromAccDetails, toAccDetails));
		
		// updating the account details for the given account number
		if (Optional.ofNullable(transRepo).isPresent()) {
			fromAccDetails.setAvailableBalance(
					fromAccDetails.getAvailableBalance() - Double.valueOf(fundTransDto.getTransferAmount()));
			fromAccDetails.setOpeningDeposit(
					fromAccDetails.getOpeningDeposit() - Double.valueOf(fundTransDto.getTransferAmount()));
			accountRepo.save(fundTransferMapper.mapToAccount(fundTransDto,fromAccDetails));
		}

		return new ResponseEntity<>("Transaction Done Successfully ", HttpStatus.OK);
	}

	
}
