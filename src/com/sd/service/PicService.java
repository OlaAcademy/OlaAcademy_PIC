package com.sd.service;

import com.sd.vo.Trespic;

public interface PicService extends BaseService<Trespic, String> {
	public Trespic query(Trespic trespic);
}
