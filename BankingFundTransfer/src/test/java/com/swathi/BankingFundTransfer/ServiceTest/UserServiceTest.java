package com.swathi.BankingFundTransfer.ServiceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.swathi.BankingFundTransfer.Dto.AccountDto;
import com.swathi.BankingFundTransfer.Dto.BeneficiaryDto;
import com.swathi.BankingFundTransfer.Dto.FundTransferDto;
import com.swathi.BankingFundTransfer.Entity.Account;
import com.swathi.BankingFundTransfer.Entity.Address;
import com.swathi.BankingFundTransfer.Entity.Beneficiary;
import com.swathi.BankingFundTransfer.Entity.User;
import com.swathi.BankingFundTransfer.Entity.UserCredentials;
import com.swathi.BankingFundTransfer.Exception.InvalidCredentialsException;
import com.swathi.BankingFundTransfer.Exception.ResourceNotFoundException;
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
import com.swathi.BankingFundTransfer.Service.UserService;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
	
	@Captor
	private ArgumentCaptor<UserCredentials> postArgumentCaptor;

	@InjectMocks
	UserService userService;

	@Mock
	UserCredentialsRepository userCredRepo;

	@Mock
	UserRepository userRepo;

	@Mock
	AccountRepository accRepo;

	@Mock
	BeneficiaryRepository beneficiaryRepo;

	@Mock
	TransactionRepository transRepo;

	@Mock
	AddressRepository addrRepo;
	
	
	@Mock
	BeneficiaryMapper beneficiaryMapper;
	
	@Mock
	AccountMapper accountMapper;
	
	@Mock
	FundTransferMapper fundTransferMapper;
	

	static UserCredentials userCredentials;
	static User user;
	static Account account;
	static Address addr;
	static AccountDto accountDto;
	static FundTransferDto fundTranferDto;
	static BeneficiaryDto dto;
	static Beneficiary benificary;

	@BeforeAll
	public static void setUp() {
		// mock Customer Credentials details as in database
		userCredentials = new UserCredentials();
		userCredentials.setUserName("AI0326");
		userCredentials.setPassword("bandi");

		
		// mock customer details as in database
		user = new User();
		user.setUserId(1l);
		user.setUserName("AI0326");

		// mock account details as in database
		account = new Account();
		account.setAvailableBalance(5500);
		account.setAccountNo(123456l);

		// mock benificary details as in database
		benificary = new Beneficiary();
		//benificary.setBeneficiaryAccountNo(4567);
		benificary.setUser(user);
		benificary.setTransferLimit(6000);

		// set values for accountopening dto
		accountDto = new AccountDto();
		accountDto.setUserName("AI0327");
		accountDto.setFirstName("Karthik");
		accountDto.setLastName("Reddy");
		accountDto.setDateOfBirth("2018-01-18");
		accountDto.setGender("male");
		accountDto.setMobileNumber("7680092889");
		accountDto.setEmailId("karthik@gmail.com");
		accountDto.setPanCard("AULPK1507H");
		accountDto.setAadharCard("400607192799");
		accountDto.setAddress1("Adress1");
		accountDto.setAddress2("Address2");
		accountDto.setCity("KP");
		accountDto.setState("Hyd");
		accountDto.setZipCode("500072");
		accountDto.setOpeningDeposit("4500");
		accountDto.setBankName("HDFC");
		accountDto.setBranchName("Kukatpally");
		accountDto.setIfscCode("HDFC00314");
		accountDto.setAccountType("Checking");

		// set values for BeneficiaryDTO
		dto = new BeneficiaryDto();
		dto.setBeneficiaryAccountNo("4567");
		dto.setBeneficiaryName("Narsi");
		dto.setIfscCode("HDF00314");
		dto.setTransferLimit("1000");

		// set values for fund transfer dto
		fundTranferDto = new FundTransferDto();
		fundTranferDto.setFromAccount("123456");
		fundTranferDto.setToAccount("4567");
		fundTranferDto.setMessage("Expenses");
		fundTranferDto.setTransferAmount("1000");
		
		addr = new Address();
		addr.setAddressId(2l);
		addr.setAddress1("KP");
		addr.setAddress2("Hyd");
		addr.setState("Telagana");
		addr.setCity("Hyd");
		addr.setZipCode(500072);
	}

	@Test
	@DisplayName("Account Opening - Postive Scenario")
	@Order(1)
	public void accountOpeningTest1() {
		// Event
		
		Mockito.when(accountMapper.mapToAddress(accountDto)).thenReturn(addr);
		
		Mockito.when(addrRepo.save(Mockito.any(Address.class))).thenAnswer(i -> {
			Address address = i.getArgument(0);
			address.setAddressId(1l);
			return address;
		});
		
		Mockito.when(accountMapper.mapToUser(accountDto,addr)).thenReturn(user);
		
		Mockito.when(userRepo.save(Mockito.any(User.class))).thenAnswer(i -> {
			User user = i.getArgument(0);
			user.setUserId(1l);
			return user;
		});
		
		Mockito.when(accountMapper.mapToAccount(accountDto,user)).thenReturn(account);
		
		Mockito.when(accRepo.save(Mockito.any(Account.class))).thenAnswer(i -> {
			Account acct = i.getArgument(0);
			acct.setAccountId(0l);
			return acct;
		});
		
       Mockito.when(accountMapper.mapToCredentials(accountDto,user)).thenReturn(userCredentials);
		
		Mockito.when(userCredRepo.save(Mockito.any(UserCredentials.class))).thenAnswer(i -> {
			UserCredentials userCredentials = i.getArgument(0);
			userCredentials.setUserCredentialsId(1l);
			return userCredentials;
		});
		
	  ResponseEntity<?> result = userService.saveUserDetails(accountDto);
	
	  assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	
	@Test
	@DisplayName("Account Opening - Negative Scenario")
	@Order(2)
	public void accountOpeningTest2() {
		// context
		Mockito.when(userRepo.findByUserName("AI0329")).thenThrow(UserNameAlreadyExistsException.class);
		// Event and outcome
		assertThrows(UserNameAlreadyExistsException.class,()-> userService.saveUserDetails(accountDto));
	}

	@Test
	@DisplayName("Login - Positive Scenario")
	@Order(3)
	public void userServiceAuthenticationTest1() 
	{
		Mockito.when(userCredRepo.findByUserNameAndPassword("AI0326", "bandi")).thenReturn(userCredentials);
		// Event
		ResponseEntity<?> result = userService.checkLogindetails(userCredentials);
		// out come
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	
	@Test
	@DisplayName("Login - Negative Scenario")
	@Order(4)
	public void userServiceAuthenticationTest2()  {
		// context
		Mockito.when(userCredRepo.findByUserNameAndPassword("AI0326", "bandi")).thenReturn(null);
		// Event and outcome
		assertThrows(InvalidCredentialsException.class, ()->userService.checkLogindetails(userCredentials));
	}

	@Test
	@DisplayName("Save Beneficiary   :: Postive Scenario")
	@Order(5)
	public void saveBenificaryTest1()
	{
		//context
		Mockito.when(userRepo.findByUserName(user.getUserName())).thenReturn(user);
		//Event and  out come
		assertEquals(HttpStatus.OK, userService.saveBeneficiary(dto, "AI0326").getStatusCode());
	}
	@Test
	@DisplayName("Save Beneficiary - Negative Scenario")
	@Order(6)
	public void saveBenificaryTest2() throws ResourceNotFoundException {
		Mockito.when(userRepo.findByUserName("AI0326")).thenReturn(null);
		//Event and  out come
		assertThrows(ResourceNotFoundException.class, ()->userService.saveBeneficiary(dto, "AI0326"));
	}

	@Test
	@DisplayName("FundTransfer Transaction - Postive Scenario")
	@Order(7)
	public void fundTransferTest1() 
	{
		// context
		Mockito.when(userRepo.findByUserName("AI0326")).thenReturn(user);		
		Mockito.when(accRepo.findByAccountNoAndUserUserId(Long.valueOf(123456), 1l)).thenReturn(account);
		Mockito.when(beneficiaryRepo.findByBeneficiaryAccountNoAndUserUserId(
				Long.valueOf(4567), 1l)).thenReturn(benificary);
		
        
      	//Event and  out come
		assertEquals(HttpStatus.OK, userService.fundTransfer(fundTranferDto, "AI0326").getStatusCode());
	}
	
	@Test
	@DisplayName("FundTransfer Transaction - Negative Scenario")
	@Order(8)
	public void fundTransferTest2() 
	{
		// context
		
		assertThrows(ResourceNotFoundException.class, ()->userService.fundTransfer(fundTranferDto, "AI0326"));
		
	}

}
