package com.seekey.kpss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.tangzc.mpe.automapper.AutoMapper;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.ColumnId;
import com.tangzc.mpe.autotable.annotation.Table;
import lombok.Data;

import java.util.Date;
/**
 * 秘钥表
 */
@AutoMapper
@Data
@Table(value = "tb_secret", comment = "秘钥表")
public class Secret {
    @ColumnId(value = "id", comment = "主键", type = "int", mode = IdType.AUTO)
    private Integer id;
    @Column(value = "private_key", comment = "私钥", type = "varchar(255)")
    private String privateKey;
    @Column(value = "public_key", comment = "公钥", type = "varchar(255)")
    private String publicKey;
    @Column(value = "password", comment = "密码", type = "varchar(255)")
    private String password;
    @Column(value = "created_time", comment = "创建时间", type = "datetime")
    Date createdTime;
    @Column(value = "updated_time", comment = "更新时间", type = "datetime")
    Date updatedTime;
}
