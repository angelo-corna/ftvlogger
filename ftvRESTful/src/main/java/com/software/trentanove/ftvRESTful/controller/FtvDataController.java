package com.software.trentanove.ftvRESTful.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.software.trentanove.ftvRESTful.model.FtvData;
import com.software.trentanove.ftvRESTful.service.FtvDataService;

@RestController
public class FtvDataController {
	@Autowired
	FtvDataService ftvDataService;
	 
	@RequestMapping(value="/ftvData", method = RequestMethod.GET, headers="Accept=application/json")
	public ResponseEntity<List<FtvData>> listAllFtvData(){
		List<FtvData> list = ftvDataService.listAllFtvData();
		if(list.size() == 0){
			return new ResponseEntity<List<FtvData>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<FtvData>>(list, HttpStatus.OK);
	}
	 
	/*
	@RequestMapping(value="/add/", method = RequestMethod.POST, headers="Accept=application/json")
	public ResponseEntity<Void> add(@RequestBody FtvData ftvData){
		ftvDataService.addFtvData(ftvData);
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}
	 
	@RequestMapping(value="/update/{date}", method = RequestMethod.PUT, headers="Accept=application/json")
	public ResponseEntity<Void> update(@PathVariable("date") Timestamp date, @RequestBody FtvData ftvData){
		ftvData.setDate(date);
		ftvDataService.updateFtvData(ftvData);
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}
	 
	@RequestMapping(value="/delete/{date}", method = RequestMethod.DELETE, headers="Accept=application/json")
	public ResponseEntity<Void> delete(@PathVariable("date") Timestamp date, @RequestBody FtvData ftvData){
		ftvData.setDate(date);
		ftvDataService.deleteFtvData(ftvData);
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<Void>(headers, HttpStatus.NO_CONTENT);
	}
	*/

	@RequestMapping(value="/getLastFtvData", method = RequestMethod.GET, headers="Accept=application/json")
	public ResponseEntity<FtvData> getLastFtvData(){
		FtvData last = ftvDataService.getLastFtvData();
		if(last == null){
			return new ResponseEntity<FtvData>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<FtvData>(last, HttpStatus.OK);
	}
	
	@RequestMapping(value="/ftvDaylyData", method = RequestMethod.GET, headers="Accept=application/json")
	public ResponseEntity<List<FtvData>> listDaylyFtvData(){
		List<FtvData> list = (List<FtvData>) ftvDataService.getDaylyFtvData();
		if(list.size() == 0){
			return new ResponseEntity<List<FtvData>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<FtvData>>(list, HttpStatus.OK);
	}
}
