package com.sms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sms.entities.SmsDescription;
import com.sms.repositories.DescriptionRepo;

@Service("descriptionService")
public class DescriptionServiceImpl implements DescriptionService {

	@Autowired
	private DescriptionRepo descriptionRepo;

	@Override
	public Iterable<SmsDescription> findAllByType(String type) {
		return descriptionRepo.findAllByType(type);
	}
	
	@Override
	public Iterable<SmsDescription> findAllByTypeByOrderBySequencenoAsc(String type) {
		return descriptionRepo.findAllByTypeByOrderBySequencenoAsc(type);
	}
	
	@Override
	public Iterable<SmsDescription> findAllByTypeByOrderBySequencenoDesc(String type) {
		return descriptionRepo.findAllByTypeByOrderBySequencenoDesc(type);
	}
	
	@Override
	public void save(String id, String desc, String type) {

		SmsDescription fdsDescription = new SmsDescription();
		fdsDescription.setId(id);
		fdsDescription.setDescription(desc);
		fdsDescription.setType(type);
		descriptionRepo.save(fdsDescription);
	}
	
	@Override
	public String getNextIdContentDetail() {
		return descriptionRepo.getNextIdContentDetail();
	}
	
	@Override
	public void deleteById(String id) {
		SmsDescription fdsDescription = new SmsDescription();
		fdsDescription.setId(id);
		descriptionRepo.delete(fdsDescription);
	}

}
