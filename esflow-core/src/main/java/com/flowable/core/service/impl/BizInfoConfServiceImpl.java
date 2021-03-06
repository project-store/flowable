package com.flowable.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowable.core.bean.BizInfoConf;
import com.flowable.core.common.service.BaseServiceImpl;
import com.flowable.core.dao.BizInfoConfDao;
import com.flowable.core.service.BizInfoConfService;

/**
 * @author 2622
 * @time 2016年5月30日
 * @email lukw@eastcom-sw.com
 */
@Service
@Transactional(readOnly = true)
public class BizInfoConfServiceImpl extends BaseServiceImpl<BizInfoConf> implements BizInfoConfService{

	@Autowired
	private BizInfoConfDao bizInfoConfDao;
	
	@Override
	@Transactional(readOnly = false)
	public void saveOrUpdate(BizInfoConf bizInfoConf){
		
		if(this.check(bizInfoConf))
			this.bizInfoConfDao.saveOrUpdate(bizInfoConf);
	}
	
	@Transactional(readOnly = true)
	private boolean check(BizInfoConf bizInfoConf){
		
		BizInfoConf example = new BizInfoConf();
		example.setBizInfo(bizInfoConf.getBizInfo());
		example.setTaskAssignee(bizInfoConf.getTaskAssignee());
		example.setTaskId(bizInfoConf.getTaskId());
		List<BizInfoConf> list = this.findBizInfoConf(example);
		this.delete(list);
		return true;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<BizInfoConf> findBizInfoConf(BizInfoConf bizInfoConf){
		
		return this.bizInfoConfDao.findBizInfoConf(bizInfoConf);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BizInfoConf getBizInfoConfByBizId(String bizId){
		
		return this.bizInfoConfDao.getBizInfoConfByBizId(bizId);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void turnTask(Map<String, Object> map){
		
		this.bizInfoConfDao.turnTask(map);
	}
}
