package com.seekey.kpss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.tangzc.mpe.automapper.AutoMapper;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.ColumnId;
import com.tangzc.mpe.autotable.annotation.Table;
import lombok.Data;

import java.util.Date;

/**
 * 分组表
 */
@AutoMapper
@Data
@Table(value = "tb_groups", comment = "分组表")
public class Group {
    @ColumnId(value = "id", comment = "主键", type = "int", mode = IdType.AUTO)
    private Integer id;
    @Column(value = "group_name", comment = "分组名称", type = "varchar(255)")
    private String name;
    @Column(value = "created_time", comment = "创建时间", type = "datetime")
    private Date createdTime;
    @Column(value = "updated_time", comment = "更新时间", type = "datetime")
    private Date updatedTime;
}
