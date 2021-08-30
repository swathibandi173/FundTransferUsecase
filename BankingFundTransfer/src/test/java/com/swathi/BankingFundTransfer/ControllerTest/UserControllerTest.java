package com.swathi.BankingFundTransfer.ControllerTest;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.swathi.BankingFundTransfer.Controller.UserController;
import com.swathi.BankingFundTransfer.Dto.AccountDto;
import com.swathi.BankingFundTransfer.Dto.BeneficiaryDto;
import com.swathi.BankingFundTransfer.Entity.UserCredentials;
import com.swathi.BankingFundTransfer.Exception.InvalidCredentialsException;
import com.swathi.BankingFundTransfer.Exception.ResourceNotFoundException;
import com.swathi.BankingFundTransfer.Exception.UserNameAlreadyExistsException;
import com.swathi.BankingFundTransfer.Response.AccountCreationAcknowledgement;
import com.swathi.BankingFundTransfer.Service.UserService;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {
	

	@Mock
	UserService userService;
	
	@InjectMocks
	UserController userController;
	
	
	static UserCredentials userCredentials;
	static AccountDto accountDto;
	static BeneficiaryDto dto;
	
	@BeforeAll
	public void setUp()
	{
		userCredentials =new UserCredentials();
		userCredentials.setUserName("AI0326");
		userCredentials.setPassword("bandi");
		
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
		
	}
	
	
	@Test
	@DisplayName("Login - Positive Scenario")
	@Order(1)
	public void userControllerAuthenticationTest1()
	{
		//context
		when(userService.checkLogindetails(userCredentials)).thenReturn(new ResponseEntity<>("Login success", HttpStatus.OK));
		
		//Event
		ResponseEntity<?> result = userController.checkLogindetails(userCredentials);
		
		//out come
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	@Test
	@DisplayName("Login - Negative Scenario")
	@Order(2)
	public void userAuthenticationTest2()
	{
		//context
		when(userService.checkLogindetails(userCredentials)).thenThrow(InvalidCredentialsException.class);
		
		//Event and out come
		assertThrows(InvalidCredentialsException.class,()->userController.checkLogindetails(userCredentials));
		
	}
	
	


	@Test
	@DisplayName("Account Opening - Postive Scenario")
	@Order(3)
	public void accountOpeningTest1() {
	
		when(userService.saveUserDetails(accountDto)).thenReturn(new ResponseEntity<>(new AccountCreationAcknowledgement("Sucess"), HttpStatus.OK));
		
		ResponseEntity<?> result = userController.userAccountOpening(accountDto);
	   
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	
	@Test
	@DisplayName("Account Opening - Negative Scenario")
	@Order(4)
	public void accountOpeningTest2() {
		
		when(userService.saveUserDetails(accountDto)).thenThrow(UserNameAlreadyExistsException.class);
	
		assertThrows(UserNameAlreadyExistsException.class,()-> userController.userAccountOpening(accountDto));
	}
	
	
	@Test
	@DisplayName("Save Beneficiary - Postive Scenario")
	@Order(5)
	public void saveBenificaryTest1()
	{
		//context
		Mockito.when(userService.saveBeneficiary(dto, "AI0326")).thenReturn(new ResponseEntity<>("Successfully added Beneficiary", HttpStatus.OK));
		//Event and  out come
		assertEquals(HttpStatus.OK, userController.saveBeneficiaryDetails(dto, "AI0326").getStatusCode());
	}
	@Test
	@DisplayName("Save Beneficiary - Negative Scenario")
	@Order(6)
	public void saveBenificaryTest2() throws ResourceNotFoundException {
		Mockito.when(userService.saveBeneficiary(dto, "AI0326")).thenThrow(ResourceNotFoundException.class);
		//Event and  out come
		assertThrows(ResourceNotFoundException.class, ()->userService.saveBeneficiary(dto, "AI0326"));
	}

}
