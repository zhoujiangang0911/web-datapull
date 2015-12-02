package com.mobile.future.dao.data;

import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.mobile.future.util.Page;
import com.mobile.future.vo.DataCountVO;
import com.mobile.future.vo.DataMinuteVO;
import com.mobile.future.vo.DataVO;


public interface IDataDao {

	/**
	 * 向数据库中插入每秒的数据
	 */
	public Integer addDataToTable(List<DataVO> dataVO) throws DataAccessException;
	
	/**
	 * 查询每天最后一条数据(其实该方法可以查询任何时刻的数据)
	 */
	public DataVO getDataEveryDay(String merchCode,Date time) throws DataAccessException;
	
	/**
	 * 向数据库中插入每天的数据
	 */
	public Integer addDataToTableEveryDay(DataCountVO data) throws DataAccessException;
	
	/**
	 * 向数据库中插入每分钟的数据
	 */
	public Integer addDataToTableEveryMinute(DataMinuteVO data) throws DataAccessException;
	
	/**
	 * 查询每分钟的数据
	 */
	public List<DataVO> getDataEveryMinute(String merchCode,Date time,Date time2) throws DataAccessException;
	
	/**
	 * 查询全天的分钟数据(这里的时间是用来确定哪一天的)
	 */
	public List<DataMinuteVO> getAllMinuteData(String merchCode,String time) throws DataAccessException;
	
	/**
	 * 查询实时的最新数据
	 */
	public DataVO getNewestData(String merchCode) throws DataAccessException;
	
	/**
	 * 查询分钟级别的统计数据
	 */
	public List<DataMinuteVO> getMinuteCountData(String merchCode,Page page) throws DataAccessException;
	
	/**
	 * 查询每天的统计数据（该方法同时用于周K和月K）
	 */
	public List<DataCountVO> getKDataCount(String merchCode,String type,Page page) throws DataAccessException;
	
	/**
	 * 获取逐笔交易数据
	 */
	public List<DataVO> getHandicapData(String merchCode,String time) throws DataAccessException;
	
	/**
	 * 获取实时挂盘数据
	 */
	public DataVO getHangingPlateData(String merchCode,String time) throws DataAccessException;
	
	/**
	 * 获取futures表中最后一条数据的时间
	 */
	public String getLastDataTime() throws DataAccessException;
	
	/**
	 * 通过futures表中最后一条数据的时间获取该时间下的各品类数据
	 */
	public List<DataVO> getAllKindsTypeData(String time) throws DataAccessException;
	
	/**
	 * 定时删除futures表中一周前的数据，因为数据库数据过多，会造成数据插入时间变长，从而影响数据库的数据采集效果
	 */
	public void deleteFuturesData (String close_time) throws DataAccessException;
}
