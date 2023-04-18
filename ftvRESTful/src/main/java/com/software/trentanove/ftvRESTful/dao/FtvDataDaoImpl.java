package com.software.trentanove.ftvRESTful.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.software.trentanove.ftvRESTful.model.FtvData;

@Repository
public abstract class FtvDataDaoImpl implements FtvDataDao {
	
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
 
	@Autowired
	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}
	
	private static final class FtvDataMapper implements RowMapper<FtvData>{
		public FtvData mapRow(ResultSet rs, int rowNum) throws SQLException {
			FtvData ftvData = new FtvData();
			ftvData.setDate(rs.getTimestamp("date"));
			ftvData.setFtv_voltage(rs.getFloat("ftv_voltage"));
			ftvData.setFtv_current(rs.getFloat("ftv_current"));
			ftvData.setFtv_power(rs.getFloat("ftv_power"));
			ftvData.setFtv_frequency(rs.getFloat("ftv_frequency"));
			ftvData.setFtv_energy(rs.getFloat("ftv_energy"));
			ftvData.setCon_voltage(rs.getFloat("con_voltage"));
			ftvData.setCon_current(rs.getFloat("con_current"));
			ftvData.setCon_power(rs.getFloat("con_power"));
			ftvData.setCon_frequency(rs.getFloat("con_frequency"));
			ftvData.setCon_energy(rs.getFloat("con_energy"));
			   
			return ftvData;
		}
	}

	private SqlParameterSource getSqlParameterByModel(FtvData ftvData){
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		if(ftvData != null){
			parameterSource.addValue("date", ftvData.getDate());
			parameterSource.addValue("ftv_voltage", ftvData.getFtv_voltage());
			parameterSource.addValue("ftv_current", ftvData.getFtv_current());
			parameterSource.addValue("ftv_power", ftvData.getFtv_power());
			parameterSource.addValue("ftv_frequency", ftvData.getFtv_frequency());
			parameterSource.addValue("ftv_energy", ftvData.getFtv_energy());
			parameterSource.addValue("con_voltage", ftvData.getCon_voltage());
			parameterSource.addValue("con_current", ftvData.getCon_current());
			parameterSource.addValue("con_power", ftvData.getCon_power());
			parameterSource.addValue("con_frequency", ftvData.getCon_frequency());
			parameterSource.addValue("con_energy", ftvData.getCon_energy());
		}
		return parameterSource;
	}
	
	public List<FtvData> listAllFtvData() {
		List<FtvData> list = new ArrayList<FtvData>();
		String sql = "select date, ftv_voltage, ftv_current, ftv_power, ftv_frequency,ftv_energy,"
				+ "con_voltage, con_current, con_power, con_frequency, con_energy from rs485data";
		list = namedParameterJdbcTemplate.query(sql, getSqlParameterByModel(null), new FtvDataMapper());
		return list;
	}

	public void addFtvData(FtvData ftvData) {
		String sql = "INSERT INTO rs458data(date, ftv_voltage, ftv_current, ftv_power, ftv_frequency,ftv_energy,"
				+ "con_voltage, con_current, con_power, con_frequency, con_energy) "
				+ "VALUES(:date, :ftv_voltage, :ftv_current, :ftv_power, :ftv_frequency, :ftv_energy,"
				+ ":con_voltage, :con_current, :con_power, :con_frequency, :con_energy)";
		namedParameterJdbcTemplate.update(sql, getSqlParameterByModel(ftvData));
	}

	public void updateFtvData(FtvData ftvData) {
		String sql = "UPDATE rs485data SET ftv_voltage=:ftv_voltage, ftv_current=:ftv_current, ftv_power=:ftv_power, "
				+ "ftv_frequency=:ftv_frequency,ftv_energy=:ftv_energy,con_voltage=:con_voltage,con_current=:con_current" 
				+" con_power=:con_power, con_frequency=:con_frequency, con_energy=:con_energy where date):date";
		namedParameterJdbcTemplate.update(sql, getSqlParameterByModel(ftvData));
	}

	public void deleteFtvData(FtvData ftvData) {
		String sql = "DELETE FROM rs485 WHERE date=:date";
		namedParameterJdbcTemplate.update(sql, getSqlParameterByModel(ftvData));
	}

	public FtvData getLastFtvData() {
		String sql = "select * from rs485data ORDER BY date DESC LIMIT 1;";
		return namedParameterJdbcTemplate.queryForObject(sql, getSqlParameterByModel(null), new FtvDataMapper());
	}
	
	/*
	public List<FtvData> getDaylyFtvData(Date daylyDate) {
		List<FtvData> list = new ArrayList<FtvData>();
		String sql = "select ftv_power,ftv_energy,con_power,con_energy from rs485data where date > CURDATE() ORDER BY date;";
		list = namedParameterJdbcTemplate.query(sql, getSqlParameterByModel(null), new FtvDataMapper());
		return list;
	}
	*/

}
