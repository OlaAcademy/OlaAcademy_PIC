package com.sd.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.sd.dao.PicDao;
import com.sd.vo.Trespic;
@Repository
public class PicDaoImpl extends BaseDaoImpl<Trespic, String> implements PicDao {

	
	
	public Trespic query(Trespic trespic){
		Session session = getSession();
		Criteria c=session.createCriteria(Trespic.class);
		c.add(Restrictions.eq("rpGid",trespic.getRpGid()));//eq是等于，gt是大于，lt是小于,or是或
		Trespic t=(Trespic) c.uniqueResult();
//		session.close();
		return t;
	}


}
