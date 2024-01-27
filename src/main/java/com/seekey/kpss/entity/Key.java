package com.seekey.kpss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.tangzc.mpe.automapper.AutoMapper;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.ColumnId;
import com.tangzc.mpe.autotable.annotation.Table;
import lombok.Data;

import java.util.Date;
@AutoMapper
@Data
@Table(value = "tb_keys", comment = "密码表")
public class Key {
    @ColumnId(value = "id", comment = "主键", type = "int", mode = IdType.AUTO)
    Integer id;
    @Column(value = "group_id", comment = "分组ID", type = "int")
    Integer groupId;
    @Column(value = "key_name", comment = "名称", type = "varchar(255)")
    String keyName;
    @Column(value = "user_name", comment = "用户名", type = "varchar(255)")
    String userName;
    @Column(value = "url", comment = "地址", type = "varchar(255)")
    String url;
    @Column(value = "password", comment = "密码", type = "varchar(255)")
    String password;
    @Column(value = "remark", comment = "详细信息", type = "varchar(2000)")
    String remark;
    @Column(value = "created_time", comment = "创建时间", type = "datetime")
    Date createdTime;
    @Column(value = "updated_time", comment = "更新时间", type = "datetime")
    Date updatedTime;
}
