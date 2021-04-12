package com.perry.cnms.service.impl;

import com.perry.cnms.dao.StudentDao;
import com.perry.cnms.dto.StudentExecution;
import com.perry.cnms.entity.Group;
import com.perry.cnms.entity.StudentData;
import com.perry.cnms.entity.StudentPicture;
import com.perry.cnms.enums.CommonStateEnum;
import com.perry.cnms.enums.StateEnum;
import com.perry.cnms.exceptions.StudentOperateException;
import com.perry.cnms.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: PerryJ
 * @Date: 2020/1/29
 */
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    StudentDao studentDao;
    private Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Override
    public StudentExecution addGroupList(List<Group> groupList) throws StudentOperateException {
        if (groupList == null || groupList.size() == 0) {
            return new StudentExecution(StateEnum.EMPTY_PARAM);
        } else {
            try {
                int effectNum = studentDao.batchInsertGroup(groupList);
//                log.info("group插入" + effectNum + "条数据");
                if (effectNum <= 0) {
                    throw new StudentOperateException("group创建失败");
                } else {
                    return new StudentExecution(StateEnum.SUCCESS);
                }
            } catch (Exception e) {
                throw new StudentOperateException("group批量添加失败" + e.getMessage());
            }

        }
    }

    @Override
    public StudentExecution getGroupList() {

        List<Group> groupList = studentDao.queryGroup("-1", null, null, null);
        if (groupList.size() == 0 || groupList == null) {
            return new StudentExecution(StateEnum.EMPTY_RETURN);
        } else {
            return new StudentExecution(StateEnum.SUCCESS, groupList);
        }
    }

    @Override
    public StudentExecution getGroupListByTeacherId(Integer teacherId) {
        if (teacherId != null && teacherId > 0) {
            String notState = String.valueOf(CommonStateEnum.HISTORY.getState());
            List<Group> groupList = studentDao.queryGroup(notState, teacherId, null, null);
//            log.info("查询group ByTid，共获取" + groupList.size() + "个");
            if (groupList.size() == 0 || groupList == null) {
                return new StudentExecution(StateEnum.EMPTY_RETURN);
            } else {
                return new StudentExecution(StateEnum.SUCCESS, groupList);
            }
        } else {
            return new StudentExecution(StateEnum.NULL_TEACHER_ID);
        }
    }

    @Override
    public StudentExecution getGroupByGroupAccount(String groupAccount) {
        if (groupAccount != null) {
            String notState = String.valueOf(CommonStateEnum.NO_STATE.getState());
            List<Group> groupList = studentDao.queryGroup(notState, null, groupAccount, null);
//            log.info("查询group ByGroupAccount，共获取" + groupList.size() + "个");
            if (groupList.size() == 0 || groupList == null) {
                return new StudentExecution(StateEnum.EMPTY_RETURN);
            } else {
                return new StudentExecution(StateEnum.SUCCESS, groupList.get(0));
            }
        } else {
            return new StudentExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public StudentExecution getGroupByGroupID(Integer groupId) {
        if (groupId != null && groupId > 0) {
            String notState = String.valueOf(CommonStateEnum.NO_STATE.getState());
            List<Group> groupList = studentDao.queryGroup(notState, null, null, groupId);
//            log.info("查询group ByGid，共获取" + groupList.size() + "个");
            if (groupList.size() == 0 || groupList == null) {
                return new StudentExecution(StateEnum.EMPTY_RETURN);
            } else {
                return new StudentExecution(StateEnum.SUCCESS, groupList.get(0));
            }
        } else {
            return new StudentExecution(StateEnum.NULL_TEACHER_ID);
        }
    }

    @Override
    public StudentExecution getGroupIdsByMajorCode(String majorCode) {
        if (majorCode != null && !("".equals(majorCode))) {
            List<Integer> groupIdList = studentDao.queryGroupIdByMajorCode(majorCode);
            if (groupIdList.size() == 0) {
                return new StudentExecution(StateEnum.EMPTY_RETURN);
            } else {
                StudentExecution studentExecution = new StudentExecution(StateEnum.SUCCESS);
                studentExecution.setIdList(groupIdList);
                return studentExecution;
            }
        } else {
            return new StudentExecution(StateEnum.EMPTY_PARAM);

        }
    }

    @Override
    public StudentExecution getNoneAreaMajorCodeByTeacherId(Integer teacherId) {
        if (teacherId != null && teacherId > 0) {
            List<String> majorCodeList = studentDao.queryNoneAreaMajorCodeByTeacherId(teacherId);
            if (majorCodeList.size() == 0) {
                return new StudentExecution(StateEnum.EMPTY_RETURN);
            } else {
                StudentExecution studentExecution = new StudentExecution(StateEnum.SUCCESS);
                studentExecution.setMajorCodeList(majorCodeList);
                return studentExecution;
            }
        } else {
            return new StudentExecution(StateEnum.EMPTY_PARAM);

        }
    }

    @Override
    public StudentExecution getNoneHistoryMajorCodeByTeacherId(Integer teacherId) {
        if (teacherId != null && teacherId > 0) {
            List<Group> majorCodeList = studentDao.queryNoneHistoryMajorCodeByTeacherId(teacherId);
            if (majorCodeList.size() == 0) {
                return new StudentExecution(StateEnum.EMPTY_RETURN);
            } else {
                log.debug("getNoneHistoryMajorCodeByTeacherId--ForDifference");
                StudentExecution studentExecution = new StudentExecution(StateEnum.SUCCESS);
                studentExecution.setGroupList(majorCodeList);
                return studentExecution;
            }
        } else {
            return new StudentExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public StudentExecution updateGroup(Group group) {
        if (group != null) {
            try {
                int effectNum = studentDao.updateGroup(group);
//                log.info("Group更新" + effectNum + "条,ID=" + group.getGroupId());
                if (effectNum <= 0) {
                    throw new StudentOperateException("Group更新失败");
                } else {
                    return new StudentExecution(StateEnum.SUCCESS);
                }
            } catch (Exception e) {
                throw new StudentOperateException("Group更新失败" + e.getMessage());
            }

        } else {
            return new StudentExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public StudentExecution deleteGroupByGroupId(Integer groupId) throws StudentOperateException {
        if (groupId != null && groupId > 0) {

            try {
                int effectNum = studentDao.deleteGroup(groupId,null);
                if (effectNum > 0) {
                    return new StudentExecution(StateEnum.SUCCESS);
                } else {
                    return new StudentExecution(StateEnum.EMPTY_RETURN);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new StudentOperateException("删除小组出错");
            }
        } else {
            return new StudentExecution(StateEnum.NULL_GROUP_ID);
        }
    }

    @Override
    public StudentExecution deleteGroupByMajorCode(String majorCode)throws StudentOperateException {
        if (majorCode != null) {

            try {
                int effectNum = studentDao.deleteGroup(null,majorCode);
                if (effectNum > 0) {
                    return new StudentExecution(StateEnum.SUCCESS);
                } else {
                    return new StudentExecution(StateEnum.EMPTY_RETURN);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new StudentOperateException("删除班级出错");
            }
        } else {
            return new StudentExecution(StateEnum.NULL_GROUP_ID);
        }
    }


    @Override
    public StudentExecution getStudentPictures() {
        List<StudentPicture> studentPictureList = studentDao.queryStudentPicture(null);
//        log.info("查询studentPicture，共获取" + studentPictureList.size() + "个");
        if (studentPictureList.size() == 0) {
            return new StudentExecution(StateEnum.EMPTY_RETURN);
        } else {
            StudentExecution studentExecution = new StudentExecution(StateEnum.SUCCESS);
            studentExecution.setStudentPictureList(studentPictureList);
            return studentExecution;
        }
    }

    @Override
    public StudentExecution getStudentPictureByGroupId(Integer groupId) {
        if (groupId != null && groupId > 0) {
            List<StudentPicture> studentPictureList = studentDao.queryStudentPicture(groupId);
//            log.info("查询studentPicture By Id，共获取" + studentPictureList.size() + "个");
            if (studentPictureList.size() == 0 || studentPictureList == null) {
                return new StudentExecution(StateEnum.EMPTY_RETURN);
            } else {
                StudentExecution studentExecution = new StudentExecution(StateEnum.SUCCESS);
                studentExecution.setStudentPictureList(studentPictureList);
                return studentExecution;
            }
        } else {
            return new StudentExecution(StateEnum.NULL_GROUP_ID);
        }
    }

    @Override
    public StudentExecution addStudentPicture(StudentPicture studentPicture) throws StudentOperateException {
        if (studentPicture == null) {
            return new StudentExecution(StateEnum.EMPTY_PARAM);
        } else {
            try {
                int effectNum = studentDao.insertStudentPicture(studentPicture);
//                log.info("studentPicture插入" + effectNum + "条数据");
                if (effectNum <= 0) {
                    throw new StudentOperateException("studentPicture创建失败");
                } else {
                    return new StudentExecution(StateEnum.SUCCESS);
                }
            } catch (Exception e) {
                throw new StudentOperateException("studentPicture添加失败" + e.getMessage());
            }

        }
    }

    @Override
    public StudentExecution updateStudentPicture(StudentPicture studentPicture) throws StudentOperateException {
        if (studentPicture != null) {
            try {
                int effectNum = studentDao.updateStudentPicture(studentPicture);
//                log.info("StudentPicture更新" + effectNum + "条,ID=" + studentPicture.getPictureId());
                if (effectNum <= 0) {
                    throw new StudentOperateException("area更新失败");
                } else {
                    return new StudentExecution(StateEnum.SUCCESS);
                }
            } catch (Exception e) {
                throw new StudentOperateException("area更新失败" + e.getMessage());
            }

        } else {
            return new StudentExecution(StateEnum.EMPTY_PARAM);
        }
    }

    @Override
    public StudentExecution addStudentData(List<StudentData> studentDataList) throws StudentOperateException {
        if (studentDataList == null || studentDataList.size() == 0) {
            return new StudentExecution(StateEnum.EMPTY_PARAM);
        } else {
            try {
                int effectNum = studentDao.insertStudentData(studentDataList);
//                log.info("studentData插入" + effectNum + "条数据");
                if (effectNum <= 0) {
                    throw new StudentOperateException("studentData创建失败");
                } else {
                    return new StudentExecution(StateEnum.SUCCESS);
                }
            } catch (Exception e) {
                throw new StudentOperateException("studentData批量添加失败" + e.getMessage());
            }

        }
    }

    @Override
    public StudentExecution getStudentDataList(Integer groupId) {
        if (groupId == null || groupId < 0) {
            return new StudentExecution(StateEnum.NULL_GROUP_ID);
        } else {
            List<StudentData> studentDataList = studentDao.queryStudentData(groupId);
//            log.info("查询StudentData，共获取" + studentDataList.size() + "个");
            if (studentDataList.size() == 0) {
                return new StudentExecution(StateEnum.EMPTY_RETURN);
            } else {
                StudentExecution studentExecution = new StudentExecution(StateEnum.SUCCESS);
                studentExecution.setStudentDataList(studentDataList);
                return studentExecution;
            }
        }
    }

    @Override
    public StudentExecution deleteStudentDataByGroupId(Integer groupId) throws StudentOperateException {
        if (groupId != null && groupId > 0) {
            try {
                int effectNum = studentDao.deleteStudentDataByGroupId(groupId);
                if (effectNum > 0) {
                    return new StudentExecution(StateEnum.SUCCESS);
                } else {
                    return new StudentExecution(StateEnum.EMPTY_RETURN);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new StudentOperateException("删除出错");
            }
        } else {
            return new StudentExecution(StateEnum.NULL_GROUP_ID);
        }
    }
}
