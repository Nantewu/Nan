package com.example.examsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.examsystem.entity.LearningPath;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface LearningPathMapper extends BaseMapper<LearningPath> {
    // 根据用户ID查询学习路径
    List<LearningPath> selectByUserId(Long userId);
    
    // 根据状态查询学习路径
    List<LearningPath> selectByStatus(Long userId, String status);
    
    // 查询用户的当前学习路径
    LearningPath selectCurrentPath(Long userId);
}