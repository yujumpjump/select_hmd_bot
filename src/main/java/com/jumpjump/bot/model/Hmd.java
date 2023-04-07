package com.jumpjump.bot.model;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "tb_hmd")
public class Hmd {

    @TableId
    private String id;

    private String name;
    private String ly;
    private String server;
    private String clrid;
    private String url;
    private Integer sun=1;
    private LocalDateTime lrtime;
    private String time;
}
