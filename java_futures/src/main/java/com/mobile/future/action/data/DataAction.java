package com.mobile.future.action.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mobile.future.common.AllDataAction;
import com.mobile.future.common.BaseAction;
import com.mobile.future.service.data.IDataService;
import com.mobile.future.vo.DataCountVO;
import com.mobile.future.vo.DataMinuteVO;
import com.mobile.future.vo.DataVO;

public class DataAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3815252131006810544L;
	private static Log logger = LogFactory.getLog(DataAction.class);
	// 设置日期格式（根据不同需求，需要定义三种格式类型）
	SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat ssdf = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat minute= new SimpleDateFormat("mm");
	// 用来存放上一遍捕获的时间的参数(定义成static是为了下一个线程可以拿到上一个获取时间)
	public static String lastTime;
	//设置一个Calendar类型的全局变量，用来判别整分钟数
	static Calendar calendar;
	//这个值是为了每一个交易日设置每一个分钟值
	static Integer timess=0;
	
	public static Set<String> types =new HashSet<String>();

	private IDataService dataServiceImpl;

	public IDataService getDataServiceImpl() {
		return dataServiceImpl;
	}

	public void setDataServiceImpl(IDataService dataServiceImpl) {
		this.dataServiceImpl = dataServiceImpl;
	}

	/*
	 * 该方法用来将参数存入表中
	 */
	public void addData() {
		//获取标准化数据数组
		String[] strs = AllDataAction.getString();
		/************ 这段是将字符串换成数组，并将时间单列出来，放入数据库 ********************************/
		// 字符串长度
		int line = strs.length;
		// 将时间戳获取到
		String time = strs[line - 1];
		try {
			// 拿到带格式的时间值
			Date date = sdf.parse("2015-11-11 11:11:11");
			// 拿到时分秒的值
			String hour = df.format(date);
			//如果时间参数为空的话，设置当前时间，并将该全局变量的值设置为下一分钟的整数值
			if(calendar==null){
				calendar=Calendar.getInstance();
			}
			// 字符串行数（本质上是同时在线的期货的种类数）
			int lineNum = line / 34;
			for (int i = 0; i < lineNum; i++) {
				if(!strs[i * 34 + 4].equals("0")){
					//向list中加入对象种类。j=1的时候，数据是品种代码
					types.add(strs[i * 34 + 1]);
				}
				if(strs[i * 34 + 4].equals("0")&&types.contains(strs[i * 34 + 1])){
					types.remove(strs[i * 34 + 1]);
				}
			}
			
			


			// 拿到带格式的时分秒的值// 用来判断开盘收盘时间点的参数
			Date dateCompare = df.parse(hour);
			Date d1 = df.parse("09:30:00");
			Date d2 = df.parse("11:30:00");
			Date d3 = df.parse("13:30:00");
			Date d4 = df.parse("15:30:00");
			// 判断获取的数据的时间戳是否出去交易的时间段内
			if (dateCompare.getTime() >= d1.getTime()
					&& dateCompare.getTime() <= d2.getTime()
					|| dateCompare.getTime() >= d3.getTime()
					&& dateCompare.getTime() <= d4.getTime()) {
				// 这句判断本次获取的时间和上次是否一样，如果一样什么都不做，如果不一样，则向数据库插入数据
				if (!time.equals(lastTime)) {
					// 定义一个二维数组
					String datas[] = new String[34];
					//全局变量记住该线程跑进来的时间戳
					lastTime = time;
					List<DataVO> dataVOList=new ArrayList<DataVO>();
					// 通过for循环将全部数据放入
					for (int i = 0; i < lineNum; i++) {
						for (int j = 0; j < 34; j++) {
							datas[j] = strs[i * 34 + j];
						}
						//这个if用来判断某个种类的品种是否参与交易，如果没有参与，开市价为0
						if(!datas[4].equals("0")){
							//实例化vo对象
							DataVO dataVO = new DataVO();
							
							// 调用方法，将这个数组中的参数对应的传入对象中
							dataVO = getDataVOByString(datas, i);
							// 把时间放入dataVO里边
							dataVO.setTime(date);
							dataVOList.add(dataVO);
						}
					}
					//交易时间内也有可能出现没有交易行情的情况，因此需要规避
					if(dataVOList.size()!=0){
						// 插入数据                 怎样插入每分钟数据
						dataServiceImpl.addDataToTable(dataVOList);
					}
					
					
					//交易开始后，收盘前，并且没有设置过timess的值
					if(dateCompare.getTime()>=d1.getTime()&&dateCompare.getTime()!=d4.getTime()&&timess==0){
						//先给calendar设置当天时间
						calendar.setTime(date);
						//秒数设置成0，数据就可以记录成整分钟值
						calendar.set(Calendar.SECOND,0);
						//拿到时间的分钟值
						String min=minute.format(calendar.getTime());
						//拿到下一分钟的时间值
						calendar.set(Calendar.MINUTE,Integer.parseInt(min)+1);
						/*
						将该值设置为1，可以保证当天收盘前程序不会再次跑入这个代码段，
						从而保证每个分时值是由分时的代码段设置的
						*/
						timess=1;
						//每天交易前清一次，保证每天拿到的种类是当天参与交易的
						types.clear();
					}
				}
				
				//获取全局变量中的时间，用来做下边的if判断，判断是否进入了下一分钟
				Date date1=calendar.getTime();
				// 如果进入了下一分钟，并且不是开盘时间
				if (date.getTime()>=date1.getTime()&& !hour.equals("09:30:00")&& !hour.equals("13:30:00")) {
					//获取本分钟的分钟数
					String min1=minute.format(date.getTime());
					//设置本分钟时间
					calendar.setTime(date);
					//设置秒为0，即整点分钟时间
					calendar.set(Calendar.SECOND,0);
					//该分钟数据统计的时间
					Date date2=calendar.getTime();
					//分钟+1，即下一分钟的时间点
					//先把更新后的时间拿到，是为了防止统计分时数据太花时间，而使下一个线程也能跑进来
					calendar.set(Calendar.MINUTE,Integer.parseInt(min1)+1);
					// 查看该时刻前一分钟内的数据并向分钟表插数据
					//这里做判断是因为中午歇盘结束时会产生一条多余的数据
					if(!df.format(date2).equals("13:30:00")){
						this.addDataToMiniteCount(types, date2);
					}
					
				}
			}
			//将日数据表的模块放外边
			if (hour.equals("15:30:00")&&timess==1) {
				//每天数据结束后将该值还原回去
				timess=0;
				//向futures_count表中加入每天统计数据
				this.addDataToEveryDaoCount(types, date);
				//每天这里只跑一遍，所以放在这里不需要做更多判断
				this.deleteFuturesData(date);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 该方法用来将三十四个参数放入VO对象中
	 */
	public DataVO getDataVOByString(String[] s, int i) {
		//实例化一个vo对象
		DataVO v = new DataVO();
		v.setMerchName(s[0]);
		v.setMerchCode(s[1]);
		v.setyClose(Integer.parseInt(s[2]));
		v.setSettlement(Integer.parseInt(s[3]));
		v.settOpen(Integer.parseInt(s[4]));
		v.setHighest(Integer.parseInt(s[5]));
		v.setLowest(Integer.parseInt(s[6]));
		v.setNewest(Integer.parseInt(s[7]));
		v.setVolume(Integer.parseInt(s[8]));
		v.setOrderVolume(Integer.parseInt(s[9]));
		v.setAveragePrice(Integer.parseInt(s[10]));
		v.setTotalTurnover(Integer.parseInt(s[11]));
		v.setTotalVolume(Integer.parseInt(s[12]));
		v.setMixed(Integer.parseInt(s[13]));
		v.setBidPrice1(Integer.parseInt(s[14]));
		v.setSellingPrice1(Integer.parseInt(s[15]));
		v.setPurchases1(Integer.parseInt(s[16]));
		v.setSales1(Integer.parseInt(s[17]));
		v.setBidPrice2(Integer.parseInt(s[18]));
		v.setSellingPrice2(Integer.parseInt(s[19]));
		v.setPurchases2(Integer.parseInt(s[20]));
		v.setSales2(Integer.parseInt(s[21]));
		v.setBidPrice3(Integer.parseInt(s[22]));
		v.setSellingPrice3(Integer.parseInt(s[23]));
		v.setPurchases3(Integer.parseInt(s[24]));
		v.setSales3(Integer.parseInt(s[25]));
		v.setBidPrice4(Integer.parseInt(s[26]));
		v.setSellingPrice4(Integer.parseInt(s[27]));
		v.setPurchases4(Integer.parseInt(s[28]));
		v.setSales4(Integer.parseInt(s[29]));
		v.setBidPrice5(Integer.parseInt(s[30]));
		v.setSellingPrice5(Integer.parseInt(s[31]));
		v.setPurchases5(Integer.parseInt(s[32]));
		v.setSales5(Integer.parseInt(s[33]));
		return v;
	}

	/*
	 * 统计分钟数据并插入表中（个人觉得这个方法还是太2，需要优化）
	 */
	public void addDataToMiniteCount(Set<String> types, Date date) {
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

	/*
	 * 统计每天数据并插入表中
	 */
	public void addDataToEveryDaoCount(Set<String> types, Date date) {
		// 循环获取不同品类对象的参数
			for(String e:types){
				// 获取某一个对象的参数
				DataVO dataVO = dataServiceImpl.getDataEveryDay(e, date);
				//判断最新价不为0，是为了过滤掉没有参与交易的对象种类
				if(dataVO.getNewest()!=0){
					// 实例化每天数据的vo对象
					DataCountVO dataCountVO = new DataCountVO();
					dataCountVO.setMerchName(dataVO.getMerchName());
					dataCountVO.setMerchCode(dataVO.getMerchCode());
					dataCountVO.setOpen(dataVO.gettOpen());
					dataCountVO.setHighest(dataVO.getHighest());
					dataCountVO.setLowest(dataVO.getLowest());
					dataCountVO.setClose(dataVO.getAveragePrice());
					dataCountVO.setAveragePrice(dataVO.getAveragePrice());
					dataCountVO.setTotalVolume(dataVO.getTotalVolume());
					dataCountVO.setTotalTurnover(dataVO.getTotalTurnover());
					dataCountVO.setOrderVolume(dataVO.getOrderVolume());
					dataCountVO.setDate(date);
					// 插入数据
					dataServiceImpl.addDataToTableEveryDay(dataCountVO);
				}
		}
	}
	
	/*
	 * 定时删除futures表中一周前的数据，因为数据库数据过多，会造成数据插入时间变长，从而影响数据库的数据采集效果
	 */
	public void deleteFuturesData(Date date){
		//获取当天时间
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		//减7是获得7天前的时间，删除7天前的数据
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 7);
		//只取当天的日期值
		String time=sdf.format(cal.getTime());
        //调用删除方法
        dataServiceImpl.deleteFuturesData(time);
	}
}
