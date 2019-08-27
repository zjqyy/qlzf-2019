package com.mycompany.qlzf_hous_keeper.entity;

import com.mycompany.qlzf_hous_keeper.entity.GroupsForJudgeEntityIsOrNotNull.GroupA;
import com.mycompany.qlzf_hous_keeper.entity.GroupsForJudgeEntityIsOrNotNull.GroupB;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 */
@Setter
@Getter
public class Woker implements Serializable {
    @NotNull(message = "id不能为空",groups={GroupB.class})
    private int id;
    @NotNull(message = "登录名字不能为空",groups = {GroupA.class, GroupB.class,})
    private String name;
    @NotNull(message = "姓名不能为空")
    private String realname;
    @NotNull(message = "密码不能为空",groups = {GroupA.class})
    private String password;
    @NotNull(message = "性别不能为空",groups ={GroupB.class})
    private String sex;
    @NotNull(message = "电话不能为空" ,groups ={GroupB.class})
    private String tel;
    //1.租赁一部；2.租赁2部；
    @NotNull(message = "部门不能为空")
    private int type;
    @NotNull(message = "公司不能为空")
    private String conpany;
    @NotNull(message = "城市不能为空" ,groups ={GroupB.class})
    private String city;
    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    private String e_mail;
    @NotNull(message = "头像id不能为空" ,groups ={GroupB.class})
    private String pic_num;
    private int role;

    @Override
    public String toString() {
        return "Woker{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", realname='" + realname + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", tel='" + tel + '\'' +
                ", type=" + type +
                ", conpany='" + conpany + '\'' +
                ", city='" + city + '\'' +
                ", e_mail='" + e_mail + '\'' +
                ", pic_num='" + pic_num + '\'' +
                ", role=" + role +
                '}';
    }
}
