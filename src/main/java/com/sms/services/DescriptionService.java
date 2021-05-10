package com.sms.services;

import com.sms.entities.SmsDescription;

public interface DescriptionService {
	Iterable<SmsDescription> findAllByType(String type);
	Iterable<SmsDescription> findAllByTypeByOrderBySequencenoAsc(String type);
	Iterable<SmsDescription> findAllByTypeByOrderBySequencenoDesc(String type);
	public void save(String id, String desc, String type);
	public String getNextIdContentDetail();
	public void deleteById(String id);
}
