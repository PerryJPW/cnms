package com.perry.cnms.service.impl;

import com.perry.cnms.dao.TeacherDao;
import com.perry.cnms.dto.TeacherExecution;
import com.perry.cnms.entity.Teacher;
import com.perry.cnms.entity.TeacherData;
import com.perry.cnms.enums.CommonStateEnum;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.exceptions.TeacherOperateException;
import com.perry.cnms.service.TeacherService;
import com.perry.cnms.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/26
 */
@Service
public class TeacherServiceImpl implements TeacherService {
    Logger log = LoggerFactory.getLogger(TeacherServiceImpl.class);

    @Autowired
    TeacherDao teacherDao;

    @Override
    public TeacherExecution getTeacherList() {
        List<Teacher> teacherList = teacherDao.queryTeacher(null, "%", null);
//        log.info("查询teacher，共获取" + teacherList.size() + "个");
        if (teacherList.size() == 0 || teacherList == null) {
            return new TeacherExecution(StateEnum.EMPTY_RETURN);
        } else {
            return new TeacherExecution(StateEnum.SUCCESS, teacherList);
        }
    }

    @Override
    public TeacherExecution getTeacherByTeacherAccount(String teacherAccount) {
        if (teacherAccount != null) {
            List<Teacher> teacherList = null;
            try {
                teacherList = teacherDao.queryTeacher(teacherAccount,
                        String.valueOf(CommonStateEnum.INITIAL.getState()), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            log.info("查询teacher，共获取" + teacherList.size() + "个");
            if (teacherList == null || teacherList.size() == 0) {
                return new TeacherExecution(StateEnum.EMPTY_RETURN);
            } else {
                return new TeacherExecution(StateEnum.SUCCESS, teacherList.get(0));
            }
        } else {
            return new TeacherExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public TeacherExecution getTeacherByTeacherId(Integer teacherId) {
        if (teacherId != null) {
            List<Teacher> teacherList = null;
            try {
                teacherList = teacherDao.queryTeacher(null,
                        String.valueOf(CommonStateEnum.INITIAL.getState()), teacherId);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            log.info("查询teacher，共获取" + teacherList.size() + "个");
            if (teacherList == null || teacherList.size() == 0) {
                return new TeacherExecution(StateEnum.EMPTY_RETURN);
            } else {
                return new TeacherExecution(StateEnum.SUCCESS, teacherList.get(0));
            }
        } else {
            return new TeacherExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public TeacherExecution deleteTeacher(Integer teacherId) throws TeacherOperateException {
        if (teacherId != null && teacherId > 0) {
            int effectNum = teacherDao.deleteTeacher(teacherId);
            if (effectNum > 0) {
                return new TeacherExecution(StateEnum.SUCCESS);
            } else {
                return new TeacherExecution(StateEnum.EMPTY_RETURN);
            }
        } else {
            return new TeacherExecution(StateEnum.NULL_TEACHER_ID);
        }
    }

    @Override
    public TeacherExecution addTeacherList(List<Teacher> teacherList) throws TeacherOperateException {
        if (teacherList != null && teacherList.size() > 0) {
            try {
                int effectNum = teacherDao.batchInsertTeacher(teacherList);
//                log.info("teacher插入" + effectNum + "条数据");
                if (effectNum <= 0) {
                    throw new TeacherOperateException("teacher创建失败");
                } else {
                    return new TeacherExecution(StateEnum.SUCCESS);
                }
            } catch (Exception e) {
                throw new TeacherOperateException("teacher批量添加失败" + e.getMessage());
            }

        } else {
            return new TeacherExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public TeacherExecution updateTeacher(Teacher teacher) throws TeacherOperateException {
        if (teacher != null) {
            try {
                int effectNum = teacherDao.updateTeacher(teacher);
//                log.info("Teacher更新" + effectNum + "条,ID=" + teacher.getTeacherId());
                if (effectNum <= 0) {
                    throw new TeacherOperateException("teacher更新失败");
                } else {
                    return new TeacherExecution(StateEnum.SUCCESS);
                }
            } catch (Exception e) {
                throw new TeacherOperateException("teacher更新失败" + e.getMessage());
            }

        } else {
            return new TeacherExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public TeacherExecution addTeacherData(TeacherData teacherData) throws TeacherOperateException {
        if (teacherData != null && teacherData.getTeacherId() != null) {
            try {
                int effectNum = teacherDao.insertTeacherData(teacherData);
//                log.info("teacherData插入，成功" + effectNum + "条数据");
                if (effectNum <= 0) {
                    throw new TeacherOperateException("teacher创建失败");
                } else {
                    return new TeacherExecution(StateEnum.SUCCESS);
                }
            } catch (Exception e) {
                throw new TeacherOperateException("teacherData添加失败" + e.getMessage());
            }

        } else {
            return new TeacherExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public TeacherExecution getTeacherDataListByTeacherId(Integer teacherId) {
        if (teacherId != null && teacherId > 0) {
            List<TeacherData> teacherDataList = teacherDao.queryTeacherData(teacherId, null);
//            log.info("查询teacherData，共获取" + teacherDataList.size() + "个");
            if (teacherDataList.size() == 0 || teacherDataList == null) {
                return new TeacherExecution(StateEnum.EMPTY_RETURN);
            } else {
                TeacherExecution teacherExecution = new TeacherExecution(StateEnum.SUCCESS);
                teacherExecution.setTeacherDataList(teacherDataList);
                return teacherExecution;
            }
        } else {
            return new TeacherExecution(StateEnum.NULL_TEACHER_ID);
        }
    }

    @Override
    public TeacherExecution getTeacherDataByTeacherDataId(Integer teacherDataId) {
        if (teacherDataId != null && teacherDataId > 0) {
            List<TeacherData> teacherDataList = teacherDao.queryTeacherData(null, teacherDataId);
//            log.info("查询teacherData ByDataID，共获取" + teacherDataList.size() + "个");
            if (teacherDataList == null || teacherDataList.size() == 0) {
                return new TeacherExecution(StateEnum.EMPTY_RETURN);
            } else {
                TeacherExecution teacherExecution = new TeacherExecution(StateEnum.SUCCESS);
                teacherExecution.setTeacherDataList(teacherDataList);
                return teacherExecution;
            }
        } else {
            return new TeacherExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public TeacherExecution deleteTeacherData(Integer teacherDataId) {
        if (teacherDataId != null && teacherDataId > 0) {
            String filePath = teacherDao.queryTeacherData(null, teacherDataId).get(0).getTeacherDataAddress();
            int effectNum = teacherDao.deleteTeacherData(teacherDataId);
            if (effectNum > 0) {
                PathUtil.deleteFileOrPath(filePath);
                return new TeacherExecution(StateEnum.SUCCESS);
            } else {
                return new TeacherExecution(StateEnum.EMPTY_RETURN);
            }
        } else {
            return new TeacherExecution(StateEnum.EMPTY_PARAM);
        }
    }


}
