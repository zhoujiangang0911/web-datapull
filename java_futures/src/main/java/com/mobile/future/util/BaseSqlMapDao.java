package com.mobile.future.util;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class BaseSqlMapDao extends SqlMapClientDaoSupport {

	protected SqlMapClientTemplate smcTemplate = getSqlMapClientTemplate();
	protected String dbLink;
	protected String userName;

	public String getDbLink() {
		return this.dbLink;
	}

	public void setDbLink(String dbLink) {
		this.dbLink = dbLink;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
