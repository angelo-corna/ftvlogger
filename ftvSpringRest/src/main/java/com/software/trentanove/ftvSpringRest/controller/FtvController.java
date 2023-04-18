package com.software.trentanove.ftvSpringRest.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.software.trentanove.ftvSpringRest.model.FtvData;

/**
 * Handles requests for the Employee JDBC Service.
 */
@Controller
public class FtvController {
	
	private static final Logger logger = LoggerFactory.getLogger(FtvController.class);
	
	@Autowired
	@Qualifier("dbDataSource")
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@RequestMapping(value = "/lastFtvData", method = RequestMethod.GET)
	public @ResponseBody FtvData getLastFtvData() {
		logger.info("Start getLastFtvData");
		String query = "select date, ftv_voltage, ftv_current, ftv_power, ftv_frequency, ftv_energy, con_voltage, con_current, "
				+ "con_power, con_frequency, con_energy from rs485data ORDER BY date DESC LIMIT 1";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<Map<String,Object>> lastDataRows = jdbcTemplate.queryForList(query);
		Map<String,Object> lastData = lastDataRows.get(0);
		FtvData ftvData = new FtvData();
		ftvData.setDate((Timestamp) lastData.get("date"));
		ftvData.setFtv_voltage(((BigDecimal) lastData.get("ftv_voltage")).floatValue());
		ftvData.setFtv_current(((BigDecimal) lastData.get("ftv_current")).floatValue());
		ftvData.setFtv_power(((BigDecimal) lastData.get("ftv_power")).floatValue());
		ftvData.setFtv_frequency(((BigDecimal) lastData.get("ftv_frequency")).floatValue());
		ftvData.setFtv_energy(((BigDecimal) lastData.get("ftv_energy")).floatValue());
		ftvData.setCon_voltage(((BigDecimal) lastData.get("con_voltage")).floatValue());
		ftvData.setCon_current(((BigDecimal) lastData.get("con_current")).floatValue());
		ftvData.setCon_power(((BigDecimal) lastData.get("con_power")).floatValue());
		ftvData.setCon_frequency(((BigDecimal) lastData.get("con_frequency")).floatValue());
		ftvData.setCon_energy(((BigDecimal) lastData.get("con_energy")).floatValue());
		
		
		return ftvData;
	}

}
