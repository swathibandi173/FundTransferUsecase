package com.swathi.BankingFundTransfer.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.swathi.BankingFundTransfer.Entity.UserCredentials;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials,Long>{
	
	public String findByPassword(String password);
	public UserCredentials findByUserNameAndPassword(String userName,String password);
	
	/*@Query("select c.panCard, c.aadhaarCard ,  c.emailId,c.firstName,c.lastName from User c where  c.panCardNo=:panNo or c.aadhaarCardNo=:aadhaarCardNo"
			+ " or c.emailId =:emailId")
	Optional<String> validateUser(String panNo,String aadhaarCardNo,String emailId);*/

}
