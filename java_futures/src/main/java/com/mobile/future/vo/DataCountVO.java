package com.mobile.future.vo;

import java.util.Date;

public class DataCountVO {
	private String merchName;
	private String merchCode;
	private Integer open;
	private Integer highest;
	private Integer lowest;
	private Integer close;
	private Integer averagePrice;
	private Integer totalTurnover;
	private Integer totalVolume;
	private Integer orderVolume;
	private Date date;
	public String getMerchName() {
		return merchName;
	}
	public void setMerchName(String merchName) {
		this.merchName = merchName;
	}
	public String getMerchCode() {
		return merchCode;
	}
	public void setMerchCode(String merchCode) {
		this.merchCode = merchCode;
	}
	public Integer getOpen() {
		return open;
	}
	public void setOpen(Integer open) {
		this.open = open;
	}
	public Integer getHighest() {
		return highest;
	}
	public void setHighest(Integer highest) {
		this.highest = highest;
	}
	public Integer getLowest() {
		return lowest;
	}
	public void setLowest(Integer lowest) {
		this.lowest = lowest;
	}
	public Integer getClose() {
		return close;
	}
	public void setClose(Integer close) {
		this.close = close;
	}
	public Integer getAveragePrice() {
		return averagePrice;
	}
	public void setAveragePrice(Integer averagePrice) {
		this.averagePrice = averagePrice;
	}
	public Integer getTotalTurnover() {
		return totalTurnover;
	}
	public void setTotalTurnover(Integer totalTurnover) {
		this.totalTurnover = totalTurnover;
	}
	public Integer getTotalVolume() {
		return totalVolume;
	}
	public void setTotalVolume(Integer totalVolume) {
		this.totalVolume = totalVolume;
	}
	public Integer getOrderVolume() {
		return orderVolume;
	}
	public void setOrderVolume(Integer orderVolume) {
		this.orderVolume = orderVolume;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public DataCountVO() {
	}
	public DataCountVO(String merchName, String merchCode, Integer open,
			Integer highest, Integer lowest, Integer close,
			Integer averagePrice, Integer totalTurnover, Integer totalVolume,
			Integer orderVolume, Date date) {
		super();
		this.merchName = merchName;
		this.merchCode = merchCode;
		this.open = open;
		this.highest = highest;
		this.lowest = lowest;
		this.close = close;
		this.averagePrice = averagePrice;
		this.totalTurnover = totalTurnover;
		this.totalVolume = totalVolume;
		this.orderVolume = orderVolume;
		this.date = date;
	}
	
	
}
