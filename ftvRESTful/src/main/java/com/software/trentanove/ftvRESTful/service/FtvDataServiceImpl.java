package com.software.trentanove.ftvRESTful.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.software.trentanove.ftvRESTful.dao.FtvDataDao;
import com.software.trentanove.ftvRESTful.model.FtvData;

@Service
public class FtvDataServiceImpl implements FtvDataService {
	 
	 FtvDataDao ftvDataDao;
	 
	 @Autowired
	 public void setFtvDataDao(FtvDataDao ftvDataDao) {
		this.ftvDataDao = ftvDataDao;
	 }

	 public List<FtvData> listAllFtvData() {
		return ftvDataDao.listAllFtvData();
	 }

	 public void addFtvData(FtvData ftvData) {
		ftvDataDao.addFtvData(ftvData);
	 }

	 public void updateFtvData(FtvData ftvData) {
		ftvDataDao.updateFtvData(ftvData);
	 }

	 public void deleteFtvData(FtvData ftvData) {
		ftvDataDao.deleteFtvData(ftvData);
	 }

	 public FtvData getLastFtvData() {
		 return ftvDataDao.getLastFtvData();
	 }

}