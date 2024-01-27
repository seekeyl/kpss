package com.seekey.kpss.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface InitialzeMapper {

    @Insert("${sql}")
    void createTable(String sql);
}
