package com.sms.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.sms.entities.SmsDescription;

public interface DescriptionRepo extends CrudRepository<SmsDescription, Long> {
	Iterable<SmsDescription> findAllByType(String type);
	
//	Iterable<FdsDescription> findAllByTypeByOrderBySequencenoAsc(String type);
	
	@Query(nativeQuery = true, value = "SELECT * FROM FPT.SMS_DESCRIPTION WHERE TYPE = :type ORDER BY SEQUENCENO ASC")
	public Iterable<SmsDescription> findAllByTypeByOrderBySequencenoAsc(@Param(value = "type") String type);
	
	@Query(nativeQuery = true, value = "SELECT * FROM FPT.SMS_DESCRIPTION WHERE TYPE = :type ORDER BY SEQUENCENO DESC")
	public Iterable<SmsDescription> findAllByTypeByOrderBySequencenoDesc(@Param(value = "type") String type);
	
	@Query(nativeQuery = true, value = "SELECT 'A' || (MAX(SUBSTR(ID,2,2))+1) FROM FPT.SMS_DESCRIPTION WHERE ID LIKE 'A%' AND TYPE <> 'ACTION' AND ID<'A98'")
	public String getNextIdContentDetail();
}
