package com.sd.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sd.dao.PicDao;
import com.sd.service.PicService;
import com.sd.vo.Trespic;

@Service
public class PicServiceImpl extends BaseServiceImpl<Trespic, String> implements
		PicService {
	@Resource
	private PicDao picDao;

	@Resource
	public void setBaseDao(PicDao picDao) {
		super.setBaseDao(picDao);
	}

	public Trespic query(Trespic trespic) {
		return picDao.query(trespic);
	}
}
