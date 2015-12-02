package com.mobile.future.vo;

import java.util.Date;

public class DataMinuteVO {
	private String merchCode;
	private Integer open;
	private Integer close;
	private Integer highest;
	private Integer lowest;
	private Integer averagePrice;
	private Date time;
	private Integer orderVolume;
	private Integer volume;
	private Integer mixed;
	private Integer averagePriceRealTime;
	private Integer totalVolume;
	private float mixedRate;
	
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
	public Integer getClose() {
		return close;
	}
	public void setClose(Integer close) {
		this.close = close;
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
	public Integer getAveragePrice() {
		return averagePrice;
	}
	public void setAveragePrice(Integer averagePrice) {
		this.averagePrice = averagePrice;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Integer getOrderVolume() {
		return orderVolume;
	}
	public void setOrderVolume(Integer orderVolume) {
		this.orderVolume = orderVolume;
	}
	public Integer getVolume() {
		return volume;
	}
	public void setVolume(Integer volume) {
		this.volume = volume;
	}
	public Integer getMixed() {
		return mixed;
	}
	public void setMixed(Integer mixed) {
		this.mixed = mixed;
	}
	public Integer getAveragePriceRealTime() {
		return averagePriceRealTime;
	}
	public void setAveragePriceRealTime(Integer averagePriceRealTime) {
		this.averagePriceRealTime = averagePriceRealTime;
	}
	public Integer getTotalVolume() {
		return totalVolume;
	}
	public void setTotalVolume(Integer totalVolume) {
		this.totalVolume = totalVolume;
	}
	public float getMixedRate() {
		return mixedRate;
	}
	public void setMixedRate(float mixedRate) {
		this.mixedRate = mixedRate;
	}
	public DataMinuteVO() {
	}
	public DataMinuteVO(String merchCode, Integer open, Integer close,
			Integer highest, Integer lowest, Integer averagePrice, Date time,
			Integer orderVolume, Integer volume, Integer mixed,
			Integer averagePriceRealTime, Integer totalVolume, float mixedRate) {
		super();
		this.merchCode = merchCode;
		this.open = open;
		this.close = close;
		this.highest = highest;
		this.lowest = lowest;
		this.averagePrice = averagePrice;
		this.time = time;
		this.orderVolume = orderVolume;
		this.volume = volume;
		this.mixed = mixed;
		this.averagePriceRealTime = averagePriceRealTime;
		this.totalVolume = totalVolume;
		this.mixedRate = mixedRate;
	}
	
}
