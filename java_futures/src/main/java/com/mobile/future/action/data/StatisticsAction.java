package com.mobile.future.action.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONObject;

import com.mobile.future.common.AllDataAction;
import com.mobile.future.common.BaseAction;
import com.mobile.future.service.data.IDataService;
import com.mobile.future.util.Page;
import com.mobile.future.util.SHA;
import com.mobile.future.vo.DataCountVO;
import com.mobile.future.vo.DataMinuteVO;
import com.mobile.future.vo.DataVO;

@SuppressWarnings("unchecked")

public class StatisticsAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6934958724983489945L;
	private static Log logger = LogFactory.getLog(StatisticsAction.class);
	
	public static String linkOrNot;
	// 日期格式
	SimpleDateFormat ssdf = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	SimpleDateFormat sdfs = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat minFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
	SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat monthDayFormat = new SimpleDateFormat("yyyyMM");
	SimpleDateFormat minSec = new SimpleDateFormat("mm:ss");
	SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//声明最新数据，盘口数据和分时数据的map，放在内存中
	static Map<String,String> newestDataMap=new HashMap<String,String>();
	static Map<String,String> handicapDataMap=new HashMap<String,String>();
	static Map<String,String> everyMinuteDataMap=new HashMap<String,String>();
	static Map<String,String> allTypeDataMap=new HashMap<String,String>();
	
	private String param;

	public void setParam(String param) {
		this.param = param;
	}

	private IDataService dataServiceImpl;

	public IDataService getDataServiceImpl() {
		return dataServiceImpl;
	}

	public void setDataServiceImpl(IDataService dataServiceImpl) {
		this.dataServiceImpl = dataServiceImpl;
	}

	/*
	 * 获取分钟表中的统计数据
	 */
	public String getEveryMinuteData(String merchCode) {
//		// 获取当前时间
//		Calendar time = Calendar.getInstance();
//		// 转换日期格式
//		String currentTime = ssdf.format(time.getTime()) + " 09:30:00";
		String currentTime=ssdf.format(DataAction.calendar.getTime()) + " 09:30:00";
		// 查询出当天全部分钟数据
		List<DataMinuteVO> list = dataServiceImpl.getAllMinuteData(merchCode,
				currentTime);
		List lists = new ArrayList();
		Map map1 = new HashMap();
		if(!list.isEmpty()){
			// 这里设置一个i，仅仅是为了在list里边放置不同的map，否则放入的map都将相同
			int i = 0;
			for (DataMinuteVO e : list) {
				// 为什么必须放在for循环里边
				Map mapa = new HashMap();
				String times = sdf.format(e.getTime());
				//这里判断第一条数据是不是每天第一条分钟数据，若不是，则需在list中补入所差数据量
				if(i==0&&!times.equals("09:31")){
					/*
					 * 这里判断时间上的差值
					 */
					//第一条数据的时间
					String time =day.format(e.getTime());
					//预期的第一条数据的时间
					String openTime=time.substring(0,11)+"09:31:00";
					//定义分钟差值
					Integer minNum=0;
					try {
						//将两者转换为date类型
						Date date1=day.parse(openTime);
						Date date2=day.parse(time);
						//计算出所差分钟数
						minNum=(int)(date2.getTime()-date1.getTime())/60000;
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
					//这段获取昨收盘价，当没有行情的时候，今日均价即显示该值
					String[] strs = AllDataAction.getString();
					Integer averagePrice=0;
					for (int x = 0; x < strs.length / 34; x++) {
							if(strs[x * 34 + 1].equals(e.getMerchCode())){
								averagePrice=Integer.parseInt(strs[x * 34 + 2]);
							}
					}
					//这个for循环是：差几条数据，循环几次
					for(int x=0;x<minNum;x++){
						Map mapSu = new HashMap();
						mapSu.put("time", "");
						// 加权平均值，接口直接给的
						mapSu.put("weightedIndex", averagePrice);
						// 非加权平均值，即每分钟的平均值
						mapSu.put("nonWeightedIndex", averagePrice);
						// 涨跌额
						mapSu.put("changeAmount", 0);
						// 涨跌幅
						mapSu.put("changeRate", 0);
						mapSu.put("buy", 0);
						mapSu.put("sell", 0);
						// 总成交量(改数据库)
						mapSu.put("volume", 0);
						lists.add(mapSu);
					}
				}
				mapa.put("time", times);
				// 加权平均值，接口直接给的
				mapa.put("weightedIndex", e.getAveragePriceRealTime());
				// 非加权平均值，即每分钟的平均值
				mapa.put("nonWeightedIndex", e.getAveragePrice());
				// 涨跌额
				mapa.put("changeAmount", e.getMixed());
				// 涨跌幅
				mapa.put("changeRate", e.getMixedRate());
				mapa.put("buy", e.getVolume());
				mapa.put("sell", e.getVolume());
				// 总成交量(改数据库)
				mapa.put("volume", e.getTotalVolume());
				lists.add(mapa);
				i++;
			}
		}
		// 给外层map放数据
		if(list.isEmpty()){
			map1.put("status", 2);
			map1.put("error", "该品类未参与交易");
		}else{
			map1.put("status", 0);
			map1.put("error", "成功");
		}
		map1.put("data", lists);
		String everyMinuteData = JSONObject.fromObject(map1).toString();
		return everyMinuteData;
	}

	/*
	 * 获取最新的的数据
	 * 对该方法进行优化，减少对表的查询，能够直接从接口获取数据的就直接获取，这样可以减少交易过程中对数据库
	 * 链接产生压力 
	 */
	public String getNewestData(String merchCode) {
		Map map = new HashMap();
		Map map1 = new HashMap();
		if(linkOrNot.equals("Failse")){
			// 获取最新数据
			DataVO dataVO = dataServiceImpl.getNewestData(merchCode);
			if(dataVO!=null){
				// 这里还需要计算涨跌幅（需要在DataVO里边添加一个字段，数据库不需要更改）
				map.put("price", dataVO.getNewest());
				map.put("changeAmount", dataVO.getMixed());
				// 计算涨跌幅
				float mixedRate = (float) dataVO.getMixed() * 100
						/ (float) dataVO.gettOpen();
				// 涨跌幅取三位
				double rate = Math.floor(mixedRate * 1000) / 1000;
				map.put("changeRate", rate);
				map.put("open", dataVO.gettOpen());
				map.put("high", dataVO.getHighest());
				map.put("low", dataVO.getLowest());
			}
		}else{
			String[] strs = AllDataAction.getString();
			for (int i = 0; i < strs.length / 34; i++) {
				String x = strs[i * 34 + 1];
				if(x.equals(merchCode)){
					
					map.put("price", strs[i * 34 + 7]);//最新价
					map.put("changeAmount", strs[i * 34 + 13]);//涨跌
					// 计算涨跌幅
					float mixedRate = (float) Integer.valueOf(strs[i * 34 + 13]) * 100//涨跌
							/ (float) Integer.valueOf(strs[i * 34 + 4]);//今开市价
					// 涨跌幅取三位
					double rate = Math.floor(mixedRate * 1000) / 1000;
					map.put("changeRate", rate);
					map.put("open", strs[i * 34 + 4]);//今开市价
					map.put("high", strs[i * 34 + 5]);//最高价
					map.put("low", strs[i * 34 + 6]);//最低价
				}
			}
		}
		// 给外层map放数据
		if(map.isEmpty()){
			map1.put("status", 2);
			map1.put("error", "该品类未参与交易");
		}else{
			map1.put("status", 0);
			map1.put("error", "成功");
		}
		map1.put("data", map);
		String newestData = JSONObject.fromObject(map1).toString();
		return newestData;
	}

	// 该方法用来加密
	public String encryption(String data) {

		byte[] encryptData = data.getBytes();
		String encryptedData = "";
		try {
			encryptedData = SHA.encryptSHA(encryptData).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedData;
	}

	public void queryData() {
		// tokey(指定字符串)+时间+随机数
		JSONObject json = JSONObject.fromObject(param);
		// 数据需求
		String need = json.get("act").toString();
		String data = json.get("data").toString();
		JSONObject json1 = JSONObject.fromObject(data);
		// 获取产品编号
		String merchCode = json1.get("code").toString();
		String dataTocline = "";
		//这里加上这句是为了判断是否请求的品类是否参与交易
		if(DataAction.types.contains(merchCode)){
		/*************************************************************************/
		// 获得盘口数据handicap
		if (need.equals("getQuotesData")) {
//			dataTocline = this.getHandicapData(merchCode);
			dataTocline=handicapDataMap.get(merchCode);
		}
		/*************************************************************************/
		// 获取实时数据
		if (need.equals("getRealTimeData")) {
			// 调用获取最新数据的方法
//			dataTocline = this.getNewestData(merchCode);
			dataTocline = newestDataMap.get(merchCode);
		}
		/*************************************************************************/
		// 获取分时数据
		if (need.equals("getTimesData")) {
			// 调用获取分时数据的方法
//			dataTocline = this.getEveryMinuteData(merchCode);
			dataTocline = everyMinuteDataMap.get(merchCode);
		}
		/*************************************************************************/
		// 请求K线数据
		if (need.equals("getKData")) {
			Page pages = new Page();
			// 捕获数据请求种类
			String type = json1.get("type").toString();
			int currentPage = Integer.parseInt(json1.get("page").toString());
			int pageSize = Integer.parseInt(json1.get("pageSize").toString());
			pages.setCurrentPage(currentPage);
			
			if (type.equals("1")) {
				pages.setPageSize(pageSize * 5);
				List<DataMinuteVO> dataMinuteList = new ArrayList();
				// 请求五分钟数据
				dataMinuteList = dataServiceImpl.getMinuteCountData(merchCode,
						pages);
				dataTocline = this.getKMinuteData(dataMinuteList, "5", currentPage);
			}
			if (type.equals("2")) {
				pages.setPageSize(pageSize * 15);
				List<DataMinuteVO> dataMinuteList = new ArrayList();
				// 请求十五分钟数据
				dataMinuteList = dataServiceImpl.getMinuteCountData(merchCode,
						pages);
				dataTocline = this.getKMinuteData(dataMinuteList, "15", currentPage);
			}
			if (type.equals("3")) {
				pages.setPageSize(pageSize * 30);
				List<DataMinuteVO> dataMinuteList = new ArrayList();
				// 请求三十分钟数据
				dataMinuteList = dataServiceImpl.getMinuteCountData(merchCode,
						pages);
				dataTocline = this.getKMinuteData(dataMinuteList, "30", currentPage);
			}
			if (type.equals("4")) {
				pages.setPageSize(pageSize * 60);
				List<DataMinuteVO> dataMinuteList = new ArrayList();
				// 请求六十分钟数据
				dataMinuteList = dataServiceImpl.getMinuteCountData(merchCode,
						pages);
				dataTocline = this.getKMinuteData(dataMinuteList, "60", currentPage);
			}
			if (type.equals("5")) {
				pages.setPageSize(pageSize);
				List<DataCountVO> dataDayList = new ArrayList();
				// 请求一天数据
				dataDayList = dataServiceImpl.getKDataCount(merchCode, "day",
						pages);
				dataTocline = this.getKDayData(dataDayList, currentPage,"");
			}
			if (type.equals("6")) {
				pages.setPageSize(pageSize);
				List<DataCountVO> dataDayList = new ArrayList();
				// 请求每周数据
				dataDayList = dataServiceImpl.getKDataCount(merchCode, "week",
						pages);
				dataTocline = this.getKDayData(dataDayList, currentPage,"");
			}
			if (type.equals("7")) {
				pages.setPageSize(pageSize);
				List<DataCountVO> dataDayList = new ArrayList();
				// 请求每月数据
				dataDayList = dataServiceImpl.getKDataCount(merchCode, "month",
						pages);
				dataTocline = this.getKDayData(dataDayList, currentPage,"month");
			}
		}
		/*************************************************************************/
		// 对结果进行加密
		// dataToCline=this.encryption(dataToCline);
		super.outputResultJson(dataTocline);
	}else{
		dataTocline="{\"error\":\"该品类未参与交易\",\"status\":2,\"data\":[]}";
		super.outputResultJson(dataTocline);
	}
	}

	public String getHandicapData(String merchCode) {
//		String time=DataAction.lastTime.substring(0,10)+ " 09:30:00";
//		Calendar c = Calendar.getInstance();
		String time = ssdf.format(DataAction.calendar.getTime()) + " 09:30:00";
		List list = new ArrayList();
		Map data = new HashMap();
		Map map1 = new HashMap();
		//实时挂盘数据放入map
		Map hangingMap = new HashMap();
		Map buy1 =new HashMap();
		Map buy2 =new HashMap();
		Map buy3 =new HashMap();
		Map sell1 =new HashMap();
		Map sell2 =new HashMap();
		Map sell3 =new HashMap();
		//获取逐笔交易数据
		List<DataVO> handicapList = dataServiceImpl.getHandicapData(merchCode,time);
		//获取实时挂盘数据(这里干脆直接从接口拿)
		/*
		 * 对该方法进行优化，减少对表的查询，能够直接从接口获取数据的就直接获取，这样可以减少交易过程中对数据库
		 * 链接产生压力 
		 */
		if(linkOrNot.equals("Failse")){
			DataVO hangingPlateList = dataServiceImpl.getHangingPlateData(merchCode, time);
			if(hangingPlateList!=null){
				buy1.put("price", hangingPlateList.getBidPrice1());
				buy1.put("volume", hangingPlateList.getPurchases1());
				buy2.put("price", hangingPlateList.getBidPrice2());
				buy2.put("volume", hangingPlateList.getPurchases2());
				buy3.put("price", hangingPlateList.getBidPrice3());
				buy3.put("volume", hangingPlateList.getPurchases3());
				sell1.put("price", hangingPlateList.getSellingPrice1());
				sell1.put("volume", hangingPlateList.getSales1());
				sell2.put("price", hangingPlateList.getSellingPrice2());
				sell2.put("volume", hangingPlateList.getSales2());
				sell3.put("price", hangingPlateList.getSellingPrice3());
				sell3.put("volume", hangingPlateList.getSales3());
			}
		}else{
			//逐笔交易数据放入map
			for (DataVO dataVO : handicapList) {
				Map map = new HashMap();
				 map.put("time", sdfs.format(dataVO.getTime()));
				 map.put("price", dataVO.getNewest());
				 map.put("volume", dataVO.getVolume());
				 if(dataVO.getMixed()>0){
					 map.put("mixed", "Rise");
				 }
				 if(dataVO.getMixed()<0){
					 map.put("mixed", "Fall");
				 }
				 list.add(map);
			}
			//获取标准化数据数组
			String[] strs = AllDataAction.getString();
			for (int i = 0; i < strs.length / 34; i++) {
				String x = strs[i * 34 + 1];
					if(x.equals(merchCode)){
						//14,买1价  15,卖1价  16,买1量  17,卖1量  18,买2价  19,卖2价 
						//20,买2量  21,卖2量  22,买3价  23,卖3价  24,买3量  25,卖3量
						buy1.put("price", strs[i * 34 + 14]);//买1价
						buy1.put("volume", strs[i * 34 + 16]);//买1量
						buy2.put("price", strs[i * 34 + 18]);//买2价
						buy2.put("volume", strs[i * 34 + 20]);//买2量
						buy3.put("price", strs[i * 34 + 22]);//买3价
						buy3.put("volume", strs[i * 34 + 24]);//买3量
						sell1.put("price", strs[i * 34 + 15]);//卖1价
						sell1.put("volume", strs[i * 34 + 17]);//卖1量
						sell2.put("price", strs[i * 34 + 19]);//卖2价
						sell2.put("volume", strs[i * 34 + 21]);//卖2量
						sell3.put("price", strs[i * 34 + 23]);//卖3价
						sell3.put("volume", strs[i * 34 + 25]);//卖3量
					}
			}
		}
			hangingMap.put("buy1", buy1);
			hangingMap.put("buy2", buy2);
			hangingMap.put("buy3", buy3);
			hangingMap.put("sell1", sell1);
			hangingMap.put("sell2", sell2);
			hangingMap.put("sell3", sell3);
		// 给外层map放数据topThree
		data.put("topThree", hangingMap);
		data.put("details", list);
		map1.put("data", data);
		if(list.isEmpty()){
			map1.put("error", "该品类未参与交易");
			map1.put("status", 2);
		}else{
			map1.put("error", "成功");
			map1.put("status", 0);
		}
		String handicapData = JSONObject.fromObject(map1).toString();
		return handicapData;
	}

	/**
	 * 获取每天、每周、每月的K线数据
	 */
	public String getKDayData(List<DataCountVO> dataDayList,int page, String date) {
		Map map1 = new HashMap();
		List lists = new ArrayList();
		for (DataCountVO dataVO : dataDayList) {
			Map map = new HashMap();
			map.put("open", dataVO.getOpen());
			map.put("high", dataVO.getHighest());
			map.put("low", dataVO.getLowest());
			map.put("close", dataVO.getClose());
			map.put("volume", dataVO.getTotalVolume());
			String time="";
			if(date.equals("month")){
				time = monthDayFormat.format(dataVO.getDate());
			}else{
				time = dayFormat.format(dataVO.getDate());
			}
			map.put("time", time);
			lists.add(map);
		}
		// 给外层map放数据
		if(dataDayList.isEmpty()){
			//第一页都没有值，说明没任何交易
			if(page==1){
				map1.put("status", 2);
				map1.put("error", "该品类未参与交易");
			//第一页有值，某一页没有，说明取值到头了
			}else{
				map1.put("status", 1);
				map1.put("error", "已经是最后一页了");
			}
		}else{
			map1.put("status", 0);
			map1.put("error", "成功");
		}
		map1.put("data", lists);
		String kDayData = JSONObject.fromObject(map1).toString();
		return kDayData;
	}

	/**
	 * 全新的获取分钟K线数据的方法
	 */

	public String getKMinuteData(List<DataMinuteVO> dataMinuteList, String type, int page) {
		int i = 0;
		Integer open = 0;
		Integer high = 0;
		Integer low = 0;
		Integer totalVolume = 0;
		Map map1 = new HashMap();
		List lists = new ArrayList();
		for (DataMinuteVO dataVO : dataMinuteList) {

			if (i == 0) {
				open = dataVO.getOpen();
				high = dataVO.getHighest();
				low = dataVO.getLowest();
				if (dataVO.getVolume() != null) {
					totalVolume += dataVO.getVolume();
				}
			}
			if (i != 0) {
				if (high < dataVO.getHighest()) {
					high = dataVO.getHighest();
				}
				if (low > dataVO.getLowest()) {
					low = dataVO.getLowest();
				}
				if (dataVO.getVolume() != null) {
					totalVolume += dataVO.getVolume();
				}
				// 用来判断时间节点的时间参数
				String judgeTime = minSec.format(dataVO.getTime());

				if (type.equals("5")) {
					// 五分钟的需要截取一下其他的不需要
					judgeTime = judgeTime.substring(1);
					if (judgeTime.equals("0:00") || judgeTime.equals("5:00")) {
						Map map = new HashMap();
						map.put("open", open);
						map.put("high", high);
						map.put("low", low);
						map.put("close", dataVO.getClose());
						map.put("volume", totalVolume);
						String time = minFormat.format(dataVO.getTime());
						map.put("time", time);
						lists.add(map);
						// 本周期的收盘等于下个周期的开盘
						open = dataVO.getClose();
						// 最高价释放掉
						high = 0;
						// 最低价用一个很大的数释放掉
						low = dataVO.getHighest() * 10;
						// 交易数释放掉
						totalVolume = 0;
					}
				}
				if (type.equals("15")) {
					if (judgeTime.equals("00:00") || judgeTime.equals("15:00")
							|| judgeTime.equals("30:00")
							|| judgeTime.equals("45:00")) {
						Map map = new HashMap();
						map.put("open", open);
						map.put("high", high);
						map.put("low", low);
						map.put("close", dataVO.getClose());
						map.put("volume", totalVolume);
						String time = minFormat.format(dataVO.getTime());
						map.put("time", time);
						lists.add(map);
						// 本周期的收盘等于下个周期的开盘
						open = dataVO.getClose();
						// 最高价释放掉
						high = 0;
						// 最低价用一个很大的数释放掉
						low = dataVO.getHighest() * 10;
						// 交易数释放掉
						totalVolume = 0;
					}
				}
				if (type.equals("30")) {
					if (judgeTime.equals("30:00") || judgeTime.equals("00:00")) {
						Map map = new HashMap();
						map.put("open", open);
						map.put("high", high);
						map.put("low", low);
						map.put("close", dataVO.getClose());
						map.put("volume", totalVolume);
						String time = minFormat.format(dataVO.getTime());
						map.put("time", time);
						lists.add(map);
						// 本周期的收盘等于下个周期的开盘
						open = dataVO.getClose();
						// 最高价释放掉
						high = 0;
						// 最低价用一个很大的数释放掉
						low = dataVO.getHighest() * 10;
						// 交易数释放掉
						totalVolume = 0;
					}
				}
				if (type.equals("60")) {
					if (judgeTime.equals("30:00")) {
						Map map = new HashMap();
						map.put("open", open);
						map.put("high", high);
						map.put("low", low);
						map.put("close", dataVO.getClose());
						map.put("volume", totalVolume);
						String time = minFormat.format(dataVO.getTime());
						map.put("time", time);
						lists.add(map);
						// 本周期的收盘等于下个周期的开盘
						open = dataVO.getClose();
						// 最高价释放掉
						high = 0;
						// 最低价用一个很大的数释放掉
						low = dataVO.getHighest() * 10;
						// 交易数释放掉
						totalVolume = 0;
					}
				}
			}

			i++;
		}
		Collections.reverse(lists);
		// 给外层map放数据
		if(dataMinuteList.isEmpty()){
			//第一页都没有值，说明没任何交易
			if(page==1){
				map1.put("status", 2);
				map1.put("error", "该品类未参与交易");
			//第一页有值，某一页没有，说明取值到头了
			}else{
				map1.put("status", 1);
				map1.put("error", "已经是最后一页了");
			}
		}else{
			map1.put("status", 0);
			map1.put("error", "成功");
		}
		map1.put("data", lists);
		String kMinuteData = JSONObject.fromObject(map1).toString();
		return kMinuteData;
	}

	public void getAllTypeData(){
		super.outputResultJson(allTypeDataMap.get("allTypeData"));
	}
	
	public String getAllTypesData() {
		Map map1 = new HashMap();
		List list = new ArrayList();
		//如果连接接口失败
		if(linkOrNot.equals("Failse")){
			//获取最后一条数据的时间
			String time =dataServiceImpl.getLastDataTime();
			//获取该时间对应的各品类数据
			List<DataVO> dataList=dataServiceImpl.getAllKindsTypeData(time);
			//用循环将数据放入对象中
			for(DataVO dataVO:dataList){
				Map map = new HashMap();
				map.put("name", dataVO.getMerchName());
				map.put("code", dataVO.getMerchCode());
				map.put("open", "-");
				map.put("high", "-");
				map.put("low", "-");
				map.put("price", "-");
				map.put("money", "-");
				map.put("volume", "-");
				map.put("changeAmount", "-");
				map.put("changeRate", "-");
				if(dataVO.gettOpen()!=0){
					map.put("open", dataVO.gettOpen());
				}
				if(dataVO.getHighest()!=0){
					map.put("high", dataVO.getHighest());
				}
				if(dataVO.getLowest()!=0){
					map.put("low", dataVO.getLowest());
				}
				if(dataVO.getNewest()!=0){
					map.put("price", dataVO.getNewest());
				}
				if(dataVO.getTotalTurnover()!=0){
					map.put("money", dataVO.getTotalTurnover());
				}
				if(dataVO.getTotalVolume()!=0){
					map.put("volume", dataVO.getTotalVolume());
				}
				if(dataVO.getMixed()!=0){
					map.put("changeAmount", dataVO.getMixed());
				}
				if(dataVO.gettOpen()!=0&&dataVO.getMixed()!=0){
					float mixedRate = (float) dataVO.getMixed() * 100
					/ (float) dataVO.gettOpen();
					map.put("changeRate", mixedRate);
				}
				list.add(map);
			}
		}else{
			//直接从接口获取标准化数据数组
			String[] strs = AllDataAction.getString();
			for (int i = 0; i < strs.length / 34; i++) {
				Map map = new HashMap();
				String open = "";
				for (int j = 0; j < 14; j++) {
					String x = strs[i * 34 + j];

					if (x.equals("0")) {
						x = "-";
					}
					if (j == 0) {
						map.put("name", x);
					}
					if (j == 1) {
						map.put("code", x);
					}
					if (j == 4) {
						map.put("open", x);
						open = x;
					}
					if (j == 5) {
						map.put("high", x);
					}
					if (j == 6) {
						map.put("low", x);
					}
					if (j == 7) {
						map.put("price", x);
					}
					if (j == 11) {
						map.put("money", x);
					}
					if (j == 12) {
						map.put("volume", x);
					}
					if (j == 13) {
						map.put("changeAmount", x);
						if (!open.equals("-") && !x.equals("-")) {
							float mixedRate = (float) Integer.parseInt(x) * 100
									/ (float) Integer.parseInt(open);
							map.put("changeRate", mixedRate);
						} else {
							map.put("changeRate", "-");
						}
					}
				}
				list.add(map);
			}
		}
		map1.put("futuresList", list);
		String realTimeData = JSONObject.fromObject(map1).toString();
//		super.outputResultJson(realTimeData);
		return realTimeData;
	}
	
	/***************这段方法用来将查询结果放入内存中******************************************************/
	public void checkFiveSecData(){
		//首页上的行情数据不需要其他条件，必须随时获取
		allTypeDataMap.put("allTypeData", this.getAllTypesData());
		//这里不需要其他判断，收盘后types会变成空
		Set<String> set=DataAction.types;
		if(!set.isEmpty()){
			for(String e: set){
				newestDataMap.put(e, this.getNewestData(e));
				handicapDataMap.put(e, this.getHandicapData(e));
				everyMinuteDataMap.put(e, this.getEveryMinuteData(e));
			}
		}
		}
	/********************************************************************************************/
	
	
/**该区间的代码是用来进行分钟数据恢复的，平时用不上（只针对PG1312两个品类）*******************/
	/**	SimpleDateFormat minute= new SimpleDateFormat("mm");
	SimpleDateFormat changeDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public void changeData(){
		Calendar calendar=Calendar.getInstance();
		List<String> types=new ArrayList(); 
		types.add("PG1312");
		
		
        String t1="2013-12-06 09:30:00";
        String t3="2013-12-06 13:30:00";
		Date date=null;
		Date date3=null;
		try {
			date = changeDateTime.parse(t1);
			date3 = changeDateTime.parse(t3);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		calendar.setTime(date);
		//秒数设置成0，数据就可以记录成整分钟值
		calendar.set(Calendar.SECOND,0);
		//拿到时间的分钟值
		String min=minute.format(calendar.getTime());
		//拿到下一分钟的时间值
		do{
			 min=minute.format(calendar.getTime());
			//拿到下一分钟的时间值
			calendar.set(Calendar.MINUTE,Integer.parseInt(min)+1);
			this.addDataToMiniteCount(types, calendar.getTime());
			if(changeDateTime.format(calendar.getTime()).equals("2013-12-06 11:30:00")){
				calendar.setTime(date3);
			}
		}while(!changeDateTime.format(calendar.getTime()).equals("2013-12-06 15:30:00"));
		
	}
	
	public void addDataToMiniteCount(List<String> types, Date date) {
		Calendar cal=Calendar.getInstance();
		// 将当前时间设置到GregorianCalendar对象中
		cal.setTime(date);
		// 在当前时间上减一分钟
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - 1);
		// 获取带格式的一分钟前的数据
		Date time = cal.getTime();
		Integer j=0;//这个参数只用来判断次数
		Integer open=0;//开盘价
		Integer volume = 0;//交易量
		Integer high = 0;//一分钟最高价
		Integer low = 0;//一分钟最低价
		Integer changePrice=0;//更新的价格
		Integer changeVolume=0;//更新的交易量
		Integer totalPriceOneMinute=0;//每分钟总价
		Integer totalVolumeOneMinute=0;//每分钟总交易量
		String merchCode="";
		Integer tOpen=0;
		Integer averagePrice=0;
		Integer mixed=0;
		Integer orderVolume=0;
		Integer totalVolume=0;
			for(String e:types){
			List<DataVO> list = dataServiceImpl.getDataEveryMinute(e,time, date);
			//判断最新价不为0，是为了过滤掉没有参与交易的对象种类
			if(!list.isEmpty()){
				if(list.get(0).getNewest()!=0){
					// 实例化每分钟数据的vo对象
					DataMinuteVO dataMinuteVO = new DataMinuteVO();
					//每分钟开盘数据
					DataVO dataVO1= dataServiceImpl.getDataEveryDay(e, time);
					//每分钟收盘数据
					DataVO dataVO2= dataServiceImpl.getDataEveryDay(e, date);
					for (DataVO d : list) {
						//这个量发生变化就是说有新交易，若没有，则不管（好几个量都可以用来比）
						if(volume!=d.getTotalVolume()){
							//这行给volume赋值只是为了和下一条数据的对比
							volume=d.getTotalVolume();
							//这里只为在循环中拿到第一个价格
							if(j==0){
								open=d.getNewest();
								high=d.getNewest();
								low=d.getNewest();
								merchCode=d.getMerchCode();
								tOpen=d.gettOpen();
							}
							if(j!=0){
								if(high<d.getNewest()){
									high=d.getNewest();
								}
								if(low>d.getNewest()){
									low=d.getNewest();
								}
								changePrice=d.getNewest();
								changeVolume=d.getVolume();
								averagePrice=d.getAveragePrice();
								mixed=d.getMixed();
								orderVolume=d.getOrderVolume();
								totalVolume=d.getTotalVolume();
								totalPriceOneMinute+=changePrice*changeVolume;
								totalVolumeOneMinute+=changeVolume;
							}
							j++;
						}
					}
					//正常捕获到一分钟的第一秒
					if(dataVO1!=null){	
					dataMinuteVO.setOpen(dataVO1.getNewest());
					//假如没有捕获到某一分钟的第一秒
					}else{
						//将捕获到的该分钟最开始一秒的价格作为开盘（权宜之计）
						dataMinuteVO.setOpen(open);
					}
					//时间
					dataMinuteVO.setTime(date);
					dataMinuteVO.setHighest(high);
					dataMinuteVO.setLowest(low);
					//一分钟内的成交量
					dataMinuteVO.setVolume(totalVolumeOneMinute);
					//如果一分钟最后一秒捕获到了
					if(dataVO2!=null){
						//编号
						dataMinuteVO.setMerchCode(dataVO2.getMerchCode());
						//均价
						dataMinuteVO.setAveragePriceRealTime(dataVO2.getAveragePrice());
						//当前价
						dataMinuteVO.setClose(dataVO2.getNewest());
						//涨跌额
						dataMinuteVO.setMixed(dataVO2.getMixed());
						//还需要一个涨跌幅(改数据库)
						float mixedRate=(float)dataVO2.getMixed()*100/(float)dataVO2.gettOpen();
						dataMinuteVO.setMixedRate(mixedRate);
						//总成交量(改数据库)
						dataMinuteVO.setTotalVolume(dataVO2.getTotalVolume());
						//订货量
						dataMinuteVO.setOrderVolume(dataVO2.getOrderVolume());
						//如果一分钟内没有任何交易，就需要这个判断了
						if(totalVolumeOneMinute==0){
							//一分钟内没有交易，价格无变化，均价即为任意时间点上的价格
							dataMinuteVO.setAveragePrice(dataVO2.getNewest());
						}else{
							dataMinuteVO.setAveragePrice(totalPriceOneMinute/totalVolumeOneMinute);
						}
						//如果没有捕获到，则需要用一分钟内其他时间的数据（权宜之计）
					}else{
						//编号
						dataMinuteVO.setMerchCode(merchCode);
						//均价
						dataMinuteVO.setAveragePriceRealTime(averagePrice);
						//当前价
						dataMinuteVO.setClose(changePrice);
						//涨跌额
						dataMinuteVO.setMixed(mixed);
						//涨跌幅（涨跌额相对于开盘价过小可能会造成该值为0%）
						float mixedRate=(float)mixed*100/(float)tOpen;
						dataMinuteVO.setMixedRate(mixedRate);
						//总成交量
						dataMinuteVO.setTotalVolume(totalVolume);
						//订货量
						dataMinuteVO.setOrderVolume(orderVolume);
						if(totalVolumeOneMinute==0){
							//一分钟内没有交易，价格无变化，均价即为任意时间点上的价格
							dataMinuteVO.setAveragePrice(changePrice);
						}else{
							dataMinuteVO.setAveragePrice(totalPriceOneMinute/totalVolumeOneMinute);
						}
						
					}
					// 循环加入不同品类对象的一分钟参数
					dataServiceImpl.addDataToTableEveryMinute(dataMinuteVO);
				}
			}
			//每一个品类完成循环后释放掉所有参数
			 j=0;//这个参数只用来判断次数
			 open=0;//开盘价
			 high = 0;//一分钟最高价
			 low = 0;//一分钟最低价
			 tOpen=0;
			 totalPriceOneMinute=0;//每分钟总价
			 totalVolumeOneMinute=0;//每分钟总交易量
			 
			 volume = 0;//交易量
			 changePrice=0;//更新的价格
			 changeVolume=0;//更新的交易量
			 averagePrice=0;
			 mixed=0;
			 orderVolume=0;
			 totalVolume=0;
		}
	}
	**/
}
