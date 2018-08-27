package com.jzsec.gupiao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.jzsec.gupiao.entity.Match;

@Repository
public interface IMatchDao {

	List<Match> queryByCustid(@Param("matchdate")String matchdate, @Param("custid")String custid);
	
	boolean addMatch(Match m);
}
