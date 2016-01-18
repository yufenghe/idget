
package com.id.get.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.id.get.dao.ISmsDao;
import com.id.get.expand.db.CustomIbatisDAO;
import com.id.get.model.SmsStatusInfo;

/**
 * <br>
 * 类 名: SmsDaoImpl <br>
 * 描 述: 短信服务系统数据访问层实现 <br>
 * 作 者: 付强 <br>
 * 创 建： 2013-12-30 <br>
 * 版 本：v1.0 <br>
 * <br>
 * 历 史: (版本) 作者 时间 注释
 */
@Repository("smsDao")
public class SmsDaoImpl extends CustomIbatisDAO<SmsStatusInfo, Long> implements ISmsDao {
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	/**查询短信发送状态信息**/
	public final static String SQL_QUERY_SMS_STATUS_DETAIL_BASE = "SELECT T.SYSCODE,T.SENDTIME,T.SPID,T.SERVERIP,T.MOBILE,T.TYPE,T.TOTALPART,T.SMSTYPE,T.SEQID,T.CONTENT,P.STATE,P.SPSENDTIME,P.UPDATETIME,P.SPMTNUMBER FROM((SELECT SYSCODE,SENDTIME,SPID,SERVERIP,MOBILE,TYPE,TOTALPART,1 AS SMSTYPE,SPMTID AS SEQID,SENDCONTENT AS CONTENT FROM SMS_DETAIL_INFO) UNION ALL (SELECT SYSCODE,SENDTIME,SPID,SERVERIP,MOBILE,2 AS TYPE,1 AS TOTALPART,2 AS SMSTYPE,SMSID AS SEQID,CHECKCODE AS CONTENT FROM SMS_VOICE_DETAIL_INFO)) T LEFT JOIN SMS_MT_REPORT P ON P.SPMTID=T.SEQID AND P.SPID=T.SPID ";
	/**基本判断语句**/
	public final static String SQL_QUERY_SMS_STATUS_DETAIL_BASE_CONDITION = " WHERE T.SENDTIME>=? AND T.SENDTIME<=DATE_ADD(?, INTERVAL 1 DAY) ";
	/**其他判断语句**/
	public final static String SQL_QUERY_SMS_STATUS_DETAIL_OTHER_CONDITION = " AND T.MOBILE=? ";
	/**排序、查询多少条**/
	public final static String SQL_QUERY_SMS_STATUS_DETAIL_ORDER = " ORDER BY T.SENDTIME DESC LIMIT ?,? ";
	/**查询发送短信状态信息总数**/
	public final static String SQL_QUERY_SMS_STATUS_DETAIL_COUNT_BASE = "SELECT COUNT(1) AS COUNT FROM((SELECT SYSCODE,SENDTIME,SPID,SERVERIP,MOBILE,TYPE,TOTALPART,1 AS SMSTYPE,SPMTID AS SEQID FROM SMS_DETAIL_INFO) UNION ALL (SELECT SYSCODE,SENDTIME,SPID,SERVERIP,MOBILE,1 AS TYPE,1 AS TOTALPART,2 AS SMSTYPE,SMSID AS SEQID FROM SMS_VOICE_DETAIL_INFO)) T LEFT JOIN SMS_MT_REPORT P ON P.SPMTID=T.SEQID AND P.SPID=T.SPID ";



	/* (non-Javadoc)
	 * @see com.uuzz.lottery.sms.dao.ISmsDao#querySmsStatusInfo(java.util.Map)
	 */
	@Override
	public List<Map<String, Object>> querySmsStatusInfo(Map<String, Object> map)
			throws Exception {
		StringBuffer sql = new StringBuffer(SQL_QUERY_SMS_STATUS_DETAIL_BASE);
		sql.append(SQL_QUERY_SMS_STATUS_DETAIL_BASE_CONDITION);
		Object[] args = null;
		if(map.get("phone") != null) {
			sql.append(SQL_QUERY_SMS_STATUS_DETAIL_OTHER_CONDITION);
			
			args = new Object[]{
					map.get("start"),
					map.get("end"),
					map.get("phone"),
					map.get("startIndex"),
					map.get("pageSize")
			};
		}
		else {
			args = new Object[]{
					map.get("start"),
					map.get("end"),
					map.get("startIndex"),
					map.get("pageSize")
			};
		}
		
		sql.append(SQL_QUERY_SMS_STATUS_DETAIL_ORDER);
		return jdbcTemplate.queryForList(sql.toString(), args);
	}
	
	/* (non-Javadoc)
	 * @see com.uuzz.lottery.sms.dao.ISmsDao#querySmsStatusSize(java.util.Map)
	 */
	@Override
	public long querySmsStatusCount(Map<String, Object> map) throws Exception {
		StringBuffer sql = new StringBuffer(SQL_QUERY_SMS_STATUS_DETAIL_COUNT_BASE);
		sql.append(SQL_QUERY_SMS_STATUS_DETAIL_BASE_CONDITION);
		
		Object[] args = null;
		if(map.get("phone") != null) {
			sql.append(SQL_QUERY_SMS_STATUS_DETAIL_OTHER_CONDITION);
			
			args = new Object[]{
					map.get("start"),
					map.get("end"),
					map.get("phone")
			};
		}
		else {
			args = new Object[]{
					map.get("start"),
					map.get("end")
			};
			
		}
		return jdbcTemplate.queryForLong(sql.toString(), args);
	}

	/* (non-Javadoc)
	 * @see com.id.get.dao.ISmsDao#querySmsStatusList(java.util.Map)
	 */
	@SuppressWarnings({"deprecation", "unchecked"})
	@Override
	public List<SmsStatusInfo> querySmsStatusList(Map<String, Object> map)
			throws Exception {
		return getSqlMapClientTemplate().queryForList("selectSmsStatusInfo", map);
	}
}
