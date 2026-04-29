package com.example.examsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.examsystem.entity.AnswerRecord;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AnswerRecordMapper extends BaseMapper<AnswerRecord> {
    // 根据用户ID查询答题记录
    List<AnswerRecord> selectByUserId(Long userId);
    
    // 根据知识点ID查询答题记录
    List<AnswerRecord> selectByKpId(Long kpId);
    
    // 统计用户在指定知识点的答题情况
    List<java.util.Map<String, Object>> selectUserKpAnswerStats(Long userId, Long kpId);
}