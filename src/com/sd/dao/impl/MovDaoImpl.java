package com.sd.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.sd.dao.MovDao;
import com.sd.vo.Tresvid;
@Repository
public class MovDaoImpl extends BaseDaoImpl<Tresvid,String> implements MovDao {
}
