package com.sd.dao;

import com.sd.vo.Trespic;

public interface PicDao extends BaseDao<Trespic, String> {
	public Trespic query(Trespic trespic);
}
