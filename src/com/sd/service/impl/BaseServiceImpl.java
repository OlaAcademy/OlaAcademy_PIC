package com.sd.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.sd.dao.BaseDao;
import com.sd.service.BaseService;
import com.sd.vo.Conditions;
import com.sd.vo.MyPage;

@Transactional
public class BaseServiceImpl<T, PK extends Serializable> implements BaseService<T, PK> {
	private BaseDao<T, PK> baseDao;

	public BaseDao<T, PK> getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao<T, PK> baseDao) {
		this.baseDao = baseDao;
	}
	
	@Transactional(readOnly = true)
	public T get(PK id) {
		return baseDao.get(id);
	}

	@Transactional(readOnly = true)
	public T load(PK id) {
		return baseDao.load(id);
	}

	@Transactional(readOnly = true)
	public List<T> getAllList() {
		return baseDao.getAllList();
	}
	@Transactional(readOnly = true)
	public List<T> getConditonList(Map<String, String> condition, String order, boolean isDesc, MyPage myPage) {
		return baseDao.getConditonList(condition, order, isDesc, myPage);
	}	
	@Transactional(readOnly = true)
	public List<T> getConditonsList(List<Conditions> conditions, String order, boolean isDesc, MyPage myPage) {
		return baseDao.getConditonsList(conditions, order, isDesc, myPage);
	}
	@Transactional(readOnly = true)
	public String getConditonCount(Map<String, String> condition) {
		return baseDao.getConditonCount(condition);
	}
	@Transactional(readOnly = true)
	public String getConditonsCount(List<Conditions> conditions) {
		return baseDao.getConditonsCount(conditions);
	}	
	
	@Transactional(readOnly = true)
	public Long getTotalCount() {
		return baseDao.getTotalCount();
	}

	@Transactional
	public PK save(T entity) {
		return baseDao.save(entity);
	}

	@Transactional
	public void update(T entity) {
		baseDao.update(entity);
	}
	
	@Transactional
	public void delete(T entity) {
		baseDao.delete(entity);
	}

	@Transactional
	public void delete(PK id) {
		baseDao.delete(id);
	}

	@Transactional
	public void delete(PK[] ids) {
		baseDao.delete(ids);
	}

	@Transactional(readOnly = true)
	public void flush() {
		baseDao.flush();
	}

	@Transactional(readOnly = true)
	public void evict(Object object) {
		baseDao.evict(object);
	}
	
	@Transactional(readOnly = true)
	public void clear() {
		baseDao.clear();
	}
}
