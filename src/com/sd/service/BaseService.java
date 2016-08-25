package com.sd.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.sd.vo.Conditions;
import com.sd.vo.MyPage;

public interface BaseService<T, PK extends Serializable> {
	/**
	 * 根据ID获取实体对象
	 * @param id 记录ID
	 * @return 实体对象
	 */
	public T get(PK id);

	/**
	 * 根据ID获取实体对象
	 * @param id 记录ID
	 * @return 实体对象
	 */
	public T load(PK id);

	/**
	 * 获取所有实体对象集合
	 * @return 实体对象集合
	 */
	public List<T> getAllList();
	
	/**
	 * 根据简单条件查询结果集
	 * @param condition 条件
	 * @param order		排序字段
	 * @param isDesc	false正序、true倒序
	 * @param myPage	分页
	 * @return
	 */
	public List<T> getConditonList(Map<String, String> condition, String order, boolean isDesc, MyPage myPage);
	public List<T> getConditonsList(List<Conditions> conditions,String order, boolean isDesc, MyPage myPage);
	/**
	 * 根据简单条件查询结果集大小
	 * @param condition 条件
	 * @param order		排序字段
	 * @param isDesc	false正序、true倒序
	 * @param myPage	分页
	 * @return
	 */
	public String getConditonCount(Map<String, String> condition);
	public String getConditonsCount(List<Conditions> conditions) ;
	/**
	 * 获取所有实体对象总数
	 * @return 实体对象总数
	 */
	public Long getTotalCount();

	/**
	 * 保存实体对象
	 * @param entity 对象
	 * @return ID
	 */
	public PK save(T entity);

	/**
	 * 更新实体对象
	 * @param entity 对象
	 */
	public void update(T entity);
	
	/**
	 * 删除实体对象
	 * @param entity 对象
	 * @return
	 */
	public void delete(T entity);

	/**
	 * 根据ID删除实体对象
	 * @param id 记录ID
	 */
	public void delete(PK id);

	/**
	 * 根据ID数组删除实体对象
	 * @param ids ID数组
	 */
	public void delete(PK[] ids);

	/**
	 * 刷新session
	 */
	public void flush();

	/**
	 * 清除对象
	 * @param object 需要清除的对象
	 */
	public void evict(Object object);
	
	/**
	 * 清除Session
	 */
	public void clear();
}
