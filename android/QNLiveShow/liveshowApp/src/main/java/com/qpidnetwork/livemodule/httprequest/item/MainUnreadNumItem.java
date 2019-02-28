package com.qpidnetwork.livemodule.httprequest.item;

/**
 *  获取主界面未读数量Item
 * @author Hunter Mun
 *
 */
public class MainUnreadNumItem {

	public MainUnreadNumItem(){

	}

	/**
	 * 获取主界面未读数量Item
	 * @param showTicketUnreadNum						节目未读数量
	 * @param loiUnreadNum					意向信未读数量
	 * @param emfUnreadNum					EMF未读数量
	 * @param privateMessageUnreadNum		私信未读数量
	 * @param bookingUnreadNum				预约未读数量
	 * @param backpackUnreadNum				背包未读数量
	 */
	public MainUnreadNumItem(int showTicketUnreadNum,
                             int loiUnreadNum,
							 int emfUnreadNum,
							 int privateMessageUnreadNum,
							 int bookingUnreadNum,
							 int backpackUnreadNum){
		this.showTicketUnreadNum = showTicketUnreadNum;
		this.loiUnreadNum = loiUnreadNum;
		this.emfUnreadNum = emfUnreadNum;
		this.privateMessageUnreadNum = privateMessageUnreadNum;
		this.bookingUnreadNum = bookingUnreadNum;
		this.backpackUnreadNum = backpackUnreadNum;
	}
	
	public int showTicketUnreadNum;
	public int loiUnreadNum;
	public int emfUnreadNum;
	public int privateMessageUnreadNum;
	public int bookingUnreadNum;
	public int backpackUnreadNum;

	@Override
	public String toString() {
		return "MainUnreadNumItem[showTicketUnreadNum:"+showTicketUnreadNum
				+ " loiUnreadNum:"+loiUnreadNum
				+ " emfUnreadNum:"+emfUnreadNum
				+ " privateMessageUnreadNum:"+privateMessageUnreadNum
				+ " bookingUnreadNum:"+bookingUnreadNum
				+ " backpackUnreadNum:"+backpackUnreadNum
				+ "]";
	}
}