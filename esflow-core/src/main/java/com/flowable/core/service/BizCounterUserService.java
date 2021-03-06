package com.flowable.core.service;

import java.util.List;
import java.util.Map;

import com.flowable.core.bean.BizCounterUser;
import com.flowable.core.common.service.IBaseService;
import com.flowable.core.common.utils.PageHelper;

/**
 * 2016年8月23日
 * @author lukw 
 * 下午8:13:52
 * com.eastcom.esflow.service
 * @email lukw@eastcom-sw.com
 */

public interface BizCounterUserService extends IBaseService<BizCounterUser>{
	
	public PageHelper<BizCounterUser> findBizCounterUser(PageHelper<BizCounterUser> page, BizCounterUser user);

	public void deleteUser(BizCounterUser user);

	public void saveUser(List<Map<String, String>> list, String bizId, String taskId);

	public void updateUser(List<Map<String, String>> list, String bizId, String taskId);

}

