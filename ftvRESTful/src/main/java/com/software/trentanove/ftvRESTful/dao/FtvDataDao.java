package com.software.trentanove.ftvRESTful.dao;

import java.util.List;
import com.software.trentanove.ftvRESTful.model.FtvData;

public interface FtvDataDao {
	 
	 public List<FtvData> listAllFtvData();
		 
	 public void addFtvData(FtvData ftvData);
	 
	 public void updateFtvData(FtvData ftvData);
	 
	 public void deleteFtvData(FtvData ftvData);
		 
	 public FtvData getLastFtvData();
	 
	 public FtvData getDaylyFtvData();
	 
}
