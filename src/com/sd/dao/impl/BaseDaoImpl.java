package com.sd.dao.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.sd.dao.BaseDao;
import com.sd.vo.BaseVo;
import com.sd.vo.Conditions;
import com.sd.vo.MyPage;

public class BaseDaoImpl<T, PK extends Serializable> implements BaseDao<T, PK>{
	private Class<T> entityClass;
	protected SessionFactory sessionFactory;
	
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BaseDaoImpl() {
        Class c = getClass();
        Type type = c.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
            this.entityClass = (Class<T>) parameterizedType[0];
        }
	}

	@Resource(name = "sessionFactory")
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public T get(PK id) {
		return (T) getSession().get(entityClass, id);
	}

	@SuppressWarnings("unchecked")
	public T load(PK id) {
		return (T) getSession().load(entityClass, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getAllList() {
		Criteria criteria = getSession().createCriteria(entityClass);
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getConditonList(Map<String, String> condition,String order, boolean isDesc, MyPage myPage) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (String key : condition.keySet()){
			criteria.add(Restrictions.eq(key, condition.get(key)));
		}
		if (StringUtils.isNotBlank(order)){
			if (isDesc)
				criteria.addOrder(Order.desc(order));
			else
				criteria.addOrder(Order.asc(order));
		}
		if (myPage != null){
			int page=myPage.getPage();
			int num=Integer.parseInt(myPage.getRows().get(0).toString());
			criteria.setFirstResult((page-1)*num);
			criteria.setMaxResults(num);
		}
		return criteria.list();
	}
	@SuppressWarnings("unchecked")
	public List<T> getConditonsList(List<Conditions> conditions,String order, boolean isDesc, MyPage myPage) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for(Conditions myConditions:conditions){
			if("like".equals(myConditions.getType())){
				Criterion cron = Restrictions.like(myConditions.getKey(),'%'+myConditions.getValue()+'%');
				criteria.add(cron);
			}else if("timeA".equals(myConditions.getType())){
				criteria.add(Restrictions.ge(myConditions.getKey(),Date.valueOf(myConditions.getValue())));
			}else if("timeB".equals(myConditions.getType())){
				criteria.add(Restrictions.le(myConditions.getKey(),Date.valueOf(myConditions.getValue())));
			}else if("map".equals(myConditions.getType())){
				for(String key:myConditions.getMap().keySet()){
					criteria.add(Restrictions.eq(key, myConditions.getMap().get(key)));
				}
			}
		}
		if (StringUtils.isNotBlank(order)){
			if (isDesc)
				criteria.addOrder(Order.desc(order));
			else
				criteria.addOrder(Order.asc(order));
		}
		if (myPage != null){
			int page=myPage.getPage();
			int num=Integer.parseInt(myPage.getRows().get(0).toString());
			criteria.setFirstResult((page-1)*num);
			criteria.setMaxResults(num);
		}
		return criteria.list();
	}
	public String getConditonCount(Map<String, String> condition) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (String key : condition.keySet()){
			criteria.add(Restrictions.eq(key, condition.get(key)));
		}
		criteria.setProjection(Projections.rowCount());
		return criteria.uniqueResult().toString();
	}
	public String getConditonsCount(List<Conditions> conditions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for(Conditions myConditions:conditions){
			if("like".equals(myConditions.getType())){
				Criterion cron = Restrictions.like(myConditions.getKey(),'%'+myConditions.getValue()+'%');
				criteria.add(cron);
			}else if("timeA".equals(myConditions.getType())){
				criteria.add(Restrictions.ge(myConditions.getKey(),Date.valueOf(myConditions.getValue())));
			}else if("timeB".equals(myConditions.getType())){
				criteria.add(Restrictions.le(myConditions.getKey(),Date.valueOf(myConditions.getValue())));
			}else if("map".equals(myConditions.getType())){
				for(String key:myConditions.getMap().keySet()){
					criteria.add(Restrictions.eq(key, myConditions.getMap().get(key)));
				}
			}
		}
		criteria.setProjection(Projections.rowCount());
		return criteria.uniqueResult().toString();
	}
	public Long getTotalCount() {
		String hql = "select count(*) from " + entityClass.getName();
		return (Long) getSession().createQuery(hql).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public PK save(T entity) {
		if (entity instanceof BaseVo) {
			try {
				Method method = entity.getClass().getMethod(BaseVo.ON_SAVE_METHOD_NAME);
				method.invoke(entity);
				Session sess = getSession();
				PK ret = (PK) sess.save(entity);
				sess.flush();
				return ret;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
	
	public void update(T entity) {
		if (entity instanceof BaseVo) {
			try {
				Method method = entity.getClass().getMethod(BaseVo.ON_UPDATE_METHOD_NAME);
				method.invoke(entity);
				Session sess = getSession();
				sess.update(entity);
				sess.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void delete(T entity) {
		Session sess = getSession();
		sess.delete(entity);
		sess.flush();
	}

	@SuppressWarnings("unchecked")
	public void delete(PK id) {
		Session sess = getSession();
		T entity = (T) sess.load(entityClass, id);
		sess.delete(entity);
		sess.flush();
	}

	@SuppressWarnings("unchecked")
	public void delete(PK[] ids) {
		Session sess = getSession();
		for (PK id : ids) {
			T entity = (T) sess.load(entityClass, id);
			sess.delete(entity);
		}
		sess.flush();
	}
	/**
	 * 根据sql返回特定位置的值
	 * @param sql
	 * @param position 字段位置
	 * @return 结果集第一条position位置的值
	 */
	@SuppressWarnings("unchecked")
	public String getSingleBysql(String sql, int position) {
		if (sql == null || position < 0)
			return "";
		Query query = getSession().createSQLQuery(sql);
		if (StringUtils.isBlank(query.getQueryString()))
			return "";
		List<Object[]> result	= query.list();
		if (result != null && result.size() > 0 && result.get(0) != null){
			if(result.get(0).length > position){
				return result.get(0)[position] == null ? 
						"" : result.get(0)[position].toString();
			}else{
				return "";
			}
		}
		return "";
	}

	public void flush() {
		getSession().flush();
	}

	public void evict(Object object) {
		getSession().evict(object);
	}

	public void clear() {
		getSession().clear();
	}
}
