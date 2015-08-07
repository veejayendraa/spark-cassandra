package com.vij.spark.cassandra.spark_cassandra;

import java.io.Serializable;
import java.util.Date;

public class BookData implements Serializable {
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
	public int getrisktypeid() {
		return risktypeid;
	}
	public void setrisktypeid(int risktypeid) {
		this.risktypeid = risktypeid;
	}
	public double getAmt() {
		return amt;
	}
	public BookData setAmt(double amt) {
		this.amt = amt;
		return this;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((businessdate == null) ? 0 : businessdate.hashCode());
		result = prime * result + bookid;
		result = prime * result + risktypeid;
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
		BookData other = (BookData) obj;
		if (businessdate == null) {
			if (other.businessdate != null)
				return false;
		} else if (!businessdate.equals(other.businessdate))
			return false;
		if (bookid != other.bookid)
			return false;
		if (risktypeid != other.risktypeid)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "BookData [businessdate=" + businessdate + ", bookid="
				+ bookid + ", risktypeid="
				+ risktypeid + ", amt=" + amt + "]";
	}
	private Date businessdate;
	private int bookid;
	private int risktypeid;
	//Set<TenorAmount> tenorAmountSet;
	private double amt;
	public BookData(Date businessdate, int bookid,
			 int risktypeid,double amt) {
		super();
		this.businessdate = businessdate;
		this.bookid = bookid;
		this.risktypeid = risktypeid;
		this.amt = amt;
	}
	
	public BookData(BookData bd) {
		super();
		this.businessdate = bd.getbusinessdate();
		this.bookid = bd.getbookid();
		this.risktypeid = bd.getrisktypeid();
		
	}
	
}
