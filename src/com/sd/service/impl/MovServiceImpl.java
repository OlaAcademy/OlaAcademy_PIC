package com.sd.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.sd.dao.MovDao;
import com.sd.service.MovService;
import com.sd.vo.Tresvid;
@Service
public class MovServiceImpl extends BaseServiceImpl<Tresvid,String> implements MovService {
	@Resource
	private MovDao movDao;
	@Resource
	public void setBaseDao(MovDao movDao) {
		super.setBaseDao(movDao);
	}
}
