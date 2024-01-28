package com.seekey.kpss.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * 用于初始化数据库
 */
@Component
@Slf4j
public class InitialzeDao {
    InitialzeMapper initialzeMapper;

    InitialzeDao(@Autowired InitialzeMapper initialzeMapper) {
        this.initialzeMapper = initialzeMapper;
    }
    public void init() {
        String sql = "CREATE TABLE IF NOT EXISTS TB_GROUPS ("
                    + "ID INTEGER AUTO_INCREMENT, "
                    + "GROUP_NAME VARCHAR(200), "
                    + "CREATED_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "UPDATED_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "CONSTRAINT TB_GROUPS_PK PRIMARY KEY (ID) "
                    + ")";
        initialzeMapper.createTable(sql);

        sql = "CREATE TABLE IF NOT EXISTS TB_KEYS ( " +
                "ID INTEGER AUTO_INCREMENT, " +
                "GROUP_ID INTEGER, " +
                "KEY_NAME VARCHAR(50), " +
                "USER_NAME VARCHAR(100), " +
                "URL VARCHAR(200), " +
                "PASSWORD VARCHAR(2000), " +
                "REMARK VARCHAR(2000), " +
                "CREATED_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "UPDATED_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "CONSTRAINT TB_KEYS_PK PRIMARY KEY (ID), " +
                "CONSTRAINT TB_KEYS_TB_GROUPS_FK FOREIGN KEY (GROUP_ID) REFERENCES TB_GROUPS(ID) " +
                ");";
        initialzeMapper.createTable(sql);

        sql = "CREATE TABLE IF NOT EXISTS TB_SECRET ( " +
                "ID INTEGER AUTO_INCREMENT, " +
                "PRIVATE_KEY VARCHAR(200), " +
                "PUBLIC_KEY VARCHAR(200), " +
                "PASSWORD VARCHAR(50), " +
                "CREATED_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "UPDATED_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "CONSTRAINT TB_SECRET_PK PRIMARY KEY (ID) " +
                ");";
        initialzeMapper.createTable(sql);
    }
}
