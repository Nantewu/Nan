package com.example.examsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.examsystem.entity.WrongQuestion;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface WrongQuestionMapper extends BaseMapper<WrongQuestion> {
    // 根据用户ID查询错题
    List<WrongQuestion> selectByUserId(Long userId);
    
    // 根据知识点查询错题
    List<WrongQuestion> selectByKnowledgePoint(Long userId, String knowledgePoint);
    
    // 根据模块查询错题
    List<WrongQuestion> selectByModule(Long userId, String module);
    
    // 统计用户错题数量
    int countByUserId(Long userId);
    
    // 统计用户各知识点的错题数量
    List<java.util.Map<String, Object>> countByKnowledgePoint(Long userId);
}