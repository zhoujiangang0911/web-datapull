package com.mobile.future.dao.data.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.mobile.future.dao.data.IDataDao;
import com.mobile.future.util.BaseSqlMapDao;
import com.mobile.future.util.Page;
import com.mobile.future.vo.DataCountVO;
import com.mobile.future.vo.DataMinuteVO;
import com.mobile.future.vo.DataVO;
@SuppressWarnings("unchecked")
public class DataDaoImpl extends BaseSqlMapDao implements IDataDao {

	/**
	 * 向数据库中插入每秒的数据
	 */
	public Integer addDataToTable(List<DataVO> dataVO) throws DataAccessException {
//		Hashtable<String, Object> param = new Hashtable<String, Object>();
//				if (dataList.getMerchName()!= null){
//					param.put("merchName", dataList.getMerchName());
//				}
//				if (dataList.getMerchCode()!= null){
//					param.put("merchCode", dataList.getMerchCode());
//				}
//					param.put("yClose", dataList.getyClose());
//					param.put("settlement", dataList.getSettlement());
//					param.put("tOpen", dataList.gettOpen());
//					param.put("highest", dataList.getHighest());
//					param.put("lowest", dataList.getLowest());
//					param.put("newest", dataList.getNewest());
//					param.put("volume", dataList.getVolume());
//					param.put("orderVolume", dataList.getOrderVolume());
//					param.put("averagePrice", dataList.getAveragePrice());
//					param.put("totalTurnover", dataList.getTotalTurnover());
//					param.put("totalVolume", dataList.getTotalVolume());
//					param.put("mixed", dataList.getMixed());
//					param.put("bidPrice1", dataList.getBidPrice1());
//					param.put("sellingPrice1", dataList.getSellingPrice1());
//					param.put("purchases1", dataList.getPurchases1());
//					param.put("sales1", dataList.getSales1());
//					param.put("bidPrice2", dataList.getBidPrice2());
//					param.put("sellingPrice2", dataList.getSellingPrice2());
//					param.put("purchases2", dataList.getPurchases2());
//					param.put("sales2", dataList.getSales2());
//					param.put("bidPrice3", dataList.getBidPrice3());
//					param.put("sellingPrice3", dataList.getSellingPrice3());
//					param.put("purchases3", dataList.getPurchases3());
//					param.put("sales3", dataList.getSales4());
//					param.put("bidPrice4", dataList.getBidPrice4());
//					param.put("sellingPrice4", dataList.getSellingPrice4());
//					param.put("purchases4", dataList.getPurchases4());
//					param.put("sales4", dataList.getSales4());
//					param.put("bidPrice5", dataList.getBidPrice5());
//					param.put("sellingPrice5", dataList.getSellingPrice5());
//					param.put("purchases5", dataList.getPurchases5());
//					param.put("sales5", dataList.getSales5());
//					param.put("time", dataList.getTime());
		return (Integer)this.smcTemplate.insert("sqlmap_data_mobile.insertDataToFutures", dataVO); 
	}

	/**
	 * 向数据库中插入每分钟的数据
	 */
	public Integer addDataToTableEveryMinute(DataMinuteVO data)
			throws DataAccessException {
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		if (data.getMerchCode()!= null){
			param.put("merchCode", data.getMerchCode());
		}
			param.put("open", data.getOpen());
			param.put("close", data.getClose());
			param.put("highest", data.getHighest());
			param.put("lowest", data.getLowest());
			param.put("averagePrice", data.getAveragePrice());
			param.put("date", data.getTime());
			param.put("orderVolume", data.getOrderVolume());
			param.put("volume", data.getVolume());
			param.put("mixed",data.getMixed());
			param.put("averagePriceRealTime",data.getAveragePriceRealTime());
			param.put("totalVolume",data.getTotalVolume());
			param.put("mixedRate",data.getMixedRate());
		return (Integer)this.smcTemplate.insert("sqlmap_data_mobile.insertDataToFuturesMinuteCount", param); 
	}

	/**
	 * 查询每分钟的数据
	 */
	public List<DataVO> getDataEveryMinute(String merchCode,Date time,Date time2)
			throws DataAccessException {
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		if(merchCode!=null&&merchCode!=""){
			param.put("merchCode", merchCode);
		}
		if(time!=null){
			param.put("open_time", time);
		}if(time2!=null){
			param.put("close_time", time2);
		}
		
		return this.smcTemplate.queryForList("sqlmap_data_mobile.getDataEveryMinute",param);
	}

	/**
	 * 向数据库中插入每天的数据
	 */
	public Integer addDataToTableEveryDay(DataCountVO data)
			throws DataAccessException {
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		if (data.getMerchName()!= null){
			param.put("merchName", data.getMerchName());
		}
		if (data.getMerchCode()!= null){
			param.put("merchCode", data.getMerchCode());
		}
			param.put("tOpen", data.getOpen());
			param.put("close", data.getClose());
			param.put("highest", data.getHighest());
			param.put("lowest", data.getLowest());
			param.put("averagePrice", data.getAveragePrice());
			param.put("date", data.getDate());
			param.put("orderVolume", data.getOrderVolume());
			param.put("totalTurnover", data.getTotalTurnover());
			param.put("totalVolume",data.getTotalVolume());
		return (Integer)this.smcTemplate.insert("sqlmap_data_mobile.insertDataToFuturesCount", param); 
	}

	/**
	 * 查询每天最后一条数据(其实该方法可以查询任何时刻的数据)
	 */
	public DataVO getDataEveryDay(String merchCode, Date time)
			throws DataAccessException {
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		if(merchCode!=null&&merchCode!=""){
			param.put("merchCode", merchCode);
		}
		if(time!=null){
			param.put("time", time);
		}
		return (DataVO)this.smcTemplate.queryForObject("sqlmap_data_mobile.getDataEveryDay",param);
	}
	
	/**
	 * 查询全天的分钟数据(这里的时间是用来确定哪一天的)
	 */
	
	public List<DataMinuteVO> getAllMinuteData(String merchCode,String time)
			throws DataAccessException {
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		if(merchCode!=null&&merchCode!=""){
			param.put("merchCode", merchCode);
		}
		if(time!=null&&time!=""){
			param.put("time", time);
		}
		return this.smcTemplate.queryForList("sqlmap_data_mobile.queryAllMinuteData", param);
	}

	/**
	 * 查询实时的最新数据
	 */
	public DataVO getNewestData(String merchCode) throws DataAccessException {
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		if(merchCode!=null&&merchCode!=""){
			param.put("merchCode", merchCode);
		}
		return (DataVO)this.smcTemplate.queryForObject("sqlmap_data_mobile.getNewestData",param);
	}

	/**
	 * 查询每天的统计数据（该方法同时用于周K和月K）
	 */
	public List<DataCountVO> getKDataCount(String merchCode,String type,Page page) throws DataAccessException {
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		if(merchCode!=null&&merchCode!=""){
			param.put("merchCode", merchCode);
		}
		param.put("starSize", (page.getCurrentPage()-1)*page.getPageSize());
		param.put("endSize", page.getPageSize());
		List<DataCountVO> list=new ArrayList();
		if(type.equals("day")){
			list=this.smcTemplate.queryForList("sqlmap_data_mobile.queryEveryDayData",param);
		}
		if(type.equals("week")){
			list=this.smcTemplate.queryForList("sqlmap_data_mobile.queryEveryWeekData",param);
		}
		if(type.equals("month")){
			list=this.smcTemplate.queryForList("sqlmap_data_mobile.queryEveryMonthData",param);
		}
		return list;
	}
	
	/**
	 * 查询分钟级别的统计数据，试用于5,15,30,60分钟的方法
	 */
	public List<DataMinuteVO> getMinuteCountData(String merchCode,Page page){
		List<DataMinuteVO> list=new ArrayList();
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		if(merchCode!=null&&merchCode!=""){
			param.put("merchCode", merchCode);
		}
		param.put("starSize", (page.getCurrentPage()-1)*page.getPageSize());
		param.put("endSize", page.getPageSize());
		list=this.smcTemplate.queryForList("sqlmap_data_mobile.queryMinuteCountData",param);
		return list;
	}

	/**
	 * 获取逐笔交易数据
	 */
	public List<DataVO> getHandicapData(String merchCode,String time)
			throws DataAccessException {
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		if(merchCode!=null&&merchCode!=""){
			param.put("merchCode", merchCode);
		}
		if(time!=null&&time!=""){
			param.put("time", time);
		}
		return this.smcTemplate.queryForList("sqlmap_data_mobile.queryHandicapData",param);
	}

	/**
	 * 通过futures表中最后一条数据的时间获取该时间下的各品类数据
	 */
	public List<DataVO> getAllKindsTypeData(String time) throws DataAccessException {
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		if(time!=null&&time!=""){
			param.put("time", time);
		}
		return this.smcTemplate.queryForList("sqlmap_data_mobile.getAllKindsTypeData",param);
	}

	/**
	 * 获取futures表中最后一条数据的时间
	 */
	public String getLastDataTime() throws DataAccessException {
		return (String)this.smcTemplate.queryForObject("sqlmap_data_mobile.getLastDataTime");
	}

	/**
	 * 获取实时挂盘数据
	 */
	public DataVO getHangingPlateData(String merchCode,String time)
			throws DataAccessException {
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		if(merchCode!=null&&merchCode!=""){
			param.put("merchCode", merchCode);
		}
		if(time!=null&&time!=""){
			param.put("time", time);
		}
		return (DataVO)this.smcTemplate.queryForObject("sqlmap_data_mobile.queryHangingPlateData",param);
	}

	/**
	 * 定时删除futures表中一周前的数据，因为数据库数据过多，会造成数据插入时间变长，从而影响数据库的数据采集效果
	 */
	public void deleteFuturesData(String close_time)
			throws DataAccessException {
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		if(close_time!=null&&close_time!=""){
			param.put("close_time", close_time);
		}
		this.smcTemplate.delete("sqlmap_data_mobile.deleteFuturesData", param);
		//这里添加对futures表id的编辑
		this.smcTemplate.update("sqlmap_data_mobile.deletePrimaryKey", null);
		this.smcTemplate.update("sqlmap_data_mobile.addNewKey", null);
		this.smcTemplate.update("sqlmap_data_mobile.setPrimaryKey", null);
	}
}
