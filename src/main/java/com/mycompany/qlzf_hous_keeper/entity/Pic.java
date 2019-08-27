package com.mycompany.qlzf_hous_keeper.entity;

import com.mycompany.qlzf_hous_keeper.entity.GroupsForJudgeEntityIsOrNotNull.GroupA;
import com.mycompany.qlzf_hous_keeper.entity.GroupsForJudgeEntityIsOrNotNull.GroupB;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/8/21 14:04
 */
public class Pic implements Serializable {
    @NotNull(message = "是否获取压缩图片",groups={GroupB.class})
    private String zip;
    @NotNull(message = "是否获取压缩图片",groups={GroupA.class})
    private String fileName;
    @NotNull(message = "图片id不能为空",groups={GroupB.class})
    private String picId;
}
