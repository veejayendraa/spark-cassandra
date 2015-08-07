package com.vij.spark.cassandra.spark_cassandra;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class TradeData implements Serializable {
	
	public Date getbusinessdate() {
		return businessdate;
	}
	public void setbusinessdate(Date businessdate) {
		this.businessdate = businessdate;
	}
	public int getbookid() {
		return bookid;
	}
	public void setbookid(int bookid) {
		this.bookid = bookid;
	}
	public String getUtid() {
		return utid;
	}
	public void setUtid(String utid) {
		this.utid = utid;
	}
	
	public int getrisktypeid() {
		return risktypeid;
	}
	public void setrisktypeid(int risktypeid) {
		this.risktypeid = risktypeid;
	}
	public double getAmt() {
		return amt;
	}
	public void setAmt(double amt) {
		this.amt = amt;
	}
	Date businessdate;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((businessdate == null) ? 0 : businessdate.hashCode());
		result = prime * result + bookid;
		result = prime * result + risktypeid;
		result = prime * result + ((utid == null) ? 0 : utid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TradeData other = (TradeData) obj;
		if (businessdate == null) {
			if (other.businessdate != null)
				return false;
		} else if (!businessdate.equals(other.businessdate))
			return false;
		if (bookid != other.bookid)
			return false;
		if (risktypeid != other.risktypeid)
			return false;
		if (utid == null) {
			if (other.utid != null)
				return false;
		} else if (!utid.equals(other.utid))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "TradeData [businessdate=" + businessdate + ", bookid="
				+ bookid + ", utid=" + utid +  ", risktypeid=" + risktypeid + ", amt=" + amt + "]";
	}
	int bookid;
	String utid;
	int risktypeid;
	//Set<TenorAmount> tenorAmountSet;
	double amt;
	
	
	public TradeData(Date businessdate, int bookid, String utid,
			 int risktypeid,double amt) {
		super();
		this.businessdate = businessdate;
		this.bookid = bookid;
		this.utid = utid;
		this.risktypeid = risktypeid;
		this.amt = amt;
	}
	
	
	

}


