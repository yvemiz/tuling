package vip.xiaonuo.biz.modular.studentprofile.param;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 学生档案批量导入参数
 *
 * @author antigravity
 * @date 2026/10/07
 */
@Getter
@Setter
public class BizStudentProfileImportParam {

    /** 学号（必填） */
    @ExcelProperty("学号")
    private String studentNo;

    /** 姓名（选填） */
    @ExcelProperty("姓名")
    private String name;

    /** 专业名称（选填） */
    @ExcelProperty("专业名称")
    private String major;

    /** 班级名称（选填） */
    @ExcelProperty("班级名称")
    private String className;

    /** 年级（选填） */
    @ExcelProperty("年级")
    private String grade;

    /** 技能特长（选填，逗号隔开） */
    @ExcelProperty("技能特长")
    private String skillTags;

    /** 个人简介（选填） */
    @ExcelProperty("个人简介")
    private String bio;
}
