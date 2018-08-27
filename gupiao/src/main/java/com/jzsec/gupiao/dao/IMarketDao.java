package com.jzsec.gupiao.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.jzsec.gupiao.entity.Market;

@Repository
public interface IMarketDao {

	Market queryByMarketID(@Param("marketcode")String id);
	boolean updateMarket(Market m);
}
