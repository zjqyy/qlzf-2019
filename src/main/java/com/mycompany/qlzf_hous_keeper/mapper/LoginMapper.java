package com.mycompany.qlzf_hous_keeper.mapper;

import com.mycompany.qlzf_hous_keeper.entity.Woker;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Mapper
public interface LoginMapper {

     List<HashMap> checkUser(Woker woker) ;

     List<HashMap> get_myInfo(Map map);

     void update_tel(Woker map);
}
