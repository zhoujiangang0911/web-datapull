package com.mobile.future.service.data.impl;

import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.mobile.future.dao.data.IDataDao;
import com.mobile.future.service.data.IDataService;
import com.mobile.future.util.Page;
import com.mobile.future.vo.DataCountVO;
import com.mobile.future.vo.DataMinuteVO;
import com.mobile.future.vo.DataVO;

public class DataServiceImpl implements IDataService {

	private IDataDao dataDaoImpl;
	
	public IDataDao getDataDaoImpl() {
		return dataDaoImpl;
	}

	public void setDataDaoImpl(IDataDao dataDaoImpl) {
		this.dataDaoImpl = dataDaoImpl;
	}


	/**
	 * 向数据库中插入每秒的数据
	 */
	public Integer addDataToTable(List<DataVO> dataVO) throws DataAccessException {
		return dataDaoImpl.addDataToTable(dataVO);
	}

	/**
	 * 向数据库中插入每分钟的数据
	 */
	public Integer addDataToTableEveryMinute(DataMinuteVO data)
			throws DataAccessException {
		return dataDaoImpl.addDataToTableEveryMinute(data);
	}

	/**
	 * 查询每分钟的数据
	 */
	public List<DataVO> getDataEveryMinute(String merchCode,Date time,Date time2)
			throws DataAccessException {
		return dataDaoImpl.getDataEveryMinute(merchCode,time,time2);
	}

	/**
	 * 向数据库中插入每天的数据
	 */
	public Integer addDataToTableEveryDay(DataCountVO data)
			throws DataAccessException {
		return dataDaoImpl.addDataToTableEveryDay(data);
	}

	/**
	 * 查询每天最后一条数据(其实该方法可以查询任何时刻的数据)
	 */
	public DataVO getDataEveryDay(String merchCode, Date time)
			throws DataAccessException {
		return dataDaoImpl.getDataEveryDay(merchCode, time);
	}
	
	/**
	 * 查询全天的分钟数据(这里的时间是用来确定哪一天的)
	 */
	public List<DataMinuteVO> getAllMinuteData(String merchCode,String time)
			throws DataAccessException {
		return dataDaoImpl.getAllMinuteData(merchCode,time);
	}

	/**
	 * 查询实时的最新数据
	 */
	public DataVO getNewestData(String merchCode) throws DataAccessException {
		return dataDaoImpl.getNewestData(merchCode);
	}

	/**
	 * 查询每天的统计数据（该方法同时用于周K和月K）
	 */
	public List<DataCountVO> getKDataCount(String merchCode,String type,Page page) throws DataAccessException {
		return dataDaoImpl.getKDataCount(merchCode,type,page);
	}

	/**
	 * 查询分钟级别的统计数据
	 */
	public List<DataMinuteVO> getMinuteCountData(String merchCode, Page page)
			throws DataAccessException {
		return dataDaoImpl.getMinuteCountData(merchCode, page);
	}

	/**
	 * 获取逐笔交易数据
	 */
	public List<DataVO> getHandicapData(String merchCode,String time)
			throws DataAccessException {
		return dataDaoImpl.getHandicapData(merchCode,time);
	}

	/**
	 * 通过futures表中最后一条数据的时间获取该时间下的各品类数据
	 */
	public List<DataVO> getAllKindsTypeData(String time) throws DataAccessException {
		return dataDaoImpl.getAllKindsTypeData(time);
	}

	/**
	 * 获取futures表中最后一条数据的时间
	 */
	public String getLastDataTime() throws DataAccessException {
		return dataDaoImpl.getLastDataTime();
	}

	/**
	 * 获取实时挂盘数据
	 */
	public DataVO getHangingPlateData(String merchCode,String time)
			throws DataAccessException {
		return dataDaoImpl.getHangingPlateData(merchCode,time);
	}

	/**
	 * 定时删除futures表中一周前的数据，因为数据库数据过多，会造成数据插入时间变长，从而影响数据库的数据采集效果
	 */
	public void deleteFuturesData(String close_time)
			throws DataAccessException {
		dataDaoImpl.deleteFuturesData(close_time);
		
	}

}
