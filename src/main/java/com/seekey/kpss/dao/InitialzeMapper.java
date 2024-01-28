package com.seekey.kpss.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 建表语句
 */
@Mapper
public interface InitialzeMapper {

    @Insert("${sql}")
    void createTable(String sql);
}
