package com.sd.vo;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class BaseVo implements Serializable{
	private static final long serialVersionUID = 5647048062154901105L;
	
	public static final String ON_SAVE_METHOD_NAME = "onSave";
	public static final String ON_UPDATE_METHOD_NAME = "onUpdate";

	@Transient
	public void onSave() {
	}

	@Transient
	public void onUpdate() {
	}
}
