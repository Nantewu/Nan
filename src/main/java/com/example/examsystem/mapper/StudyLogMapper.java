package com.example.examsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.examsystem.entity.StudyLog;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface StudyLogMapper extends BaseMapper<StudyLog> {
    // 根据用户ID查询学习日志
    List<StudyLog> selectByUserId(Long userId);
    
    // 根据学习路径ID查询学习日志
    List<StudyLog> selectByPathId(Long pathId);
    
    // 统计用户的学习情况
    List<java.util.Map<String, Object>> selectUserStudyStats(Long userId);
}