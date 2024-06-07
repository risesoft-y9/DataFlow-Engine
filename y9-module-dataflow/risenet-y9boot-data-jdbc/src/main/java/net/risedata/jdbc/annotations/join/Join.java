package net.risedata.jdbc.annotations.join;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;




/**
 * 声明该字段连接一张表 多个字段可以连接同一个表会自动合并字段 一个类中只需要一个字段声明id对应关系即可 如果2个对象直接不是ONE TO ONE 而是
 * ONE TO MORE 则只能配置一个字段 且一个字段必须是统计的函数 例如 count sum 等 field 则会成为count(field)
 * 
 * 演示   
 *   
 *   情况1:   本地表连接字段是目标表的求和 function可以填写任何的函数会自动拼接上()
 *   @Join(value = InstructionGp.class,field = "goal",joinId = {"id"},toId = {"tougaoId"},function = "sum",where="需要添加的条件")
 *                 localField;
 *   情况2: 根据条件连接目标表的字段
 *   @Join(joinId ="instanceid",toId = "instanceId",value =  Docment.class,field="需要的字段不写则为目标表整个对象")
 *    localField;
 *    情况3: 自己写sql 此时的field 可以随意填写一个不影响
 *    @Join(field = "1", function = "case OutBox1_ISBACKRESULT when 'no' then 0 else (SELECT count(1) " + 
					"                          FROM DOCEXCHANGE_INBOX OutBox5" + 
					"                         where OutBox5.INSTANCE_GUID = OutBox.INSTANCE_GUID" + 
					"                           and RECEIVER_GUID in" + 
					"                               ('{1F01F5B7-C4D3-3A45-939F-A078E2BCB5B6}'," + 
					"                                '{AD10B26D-B9C5-494C-AB69-795407864E42}')) end",isAutoFunction = true)
 *       
 * @author libo 2020年10月20日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Join {
	/**
	 * 需要 join 的表 注意 如果存在多个字段需要另外一张表的字段 则 无需在多个字段上配置多个
	 * 
	 * @return
	 */
	Class<?> value() default NULL.class;

	/**
	 * 填写当前表与对方表相连的字段名 非数据库字段名 localTable cloumName 当前表连接对方表的id字段 注意
	 * 同一个entiry必须与toid 一一相连
	 * 
	 * @return
	 */
	String[] joinId() default "";

	/**
	 * 填写当前表与对方表相连的对方表的字段名 非数据库字段名 为对象的字段名
	 * 
	 * 对方表与之相连的字段 注意 同一个entiry 必须与joinid 一一相连
	 * 
	 * @return
	 */
	String[] toId() default "";

	/**
	 * 连接对方表的字段名将对方表的这个字段的数据赋值到此字段上
	 */
	String field() default "";

	/**
	 * 什么情况下join 此表达式中可以写任何java的表达式 结尾不需要加 ; 如果运用到其他的类 需要指明
	 * 
	 * @return
	 */
	String expression() default "";

	/**
	 * 当 非一对一关系时此处为 聚合函数 如果聚合函数中参数不用自动拼接上 function(field) 的话则 需要将isAutoFunction
	 * 调为true 自动拼接模式下 只需要写方法名即可
	 * 
	 * @return
	 */
	String function() default "";

	/**
	 * 是否不自动拼接聚合函数 非自动拼接 模式下 需要手写这段代码的sql
	 * isAutoFunction 为false 的时候会自动变成 
	 * 
	 *    false : 
	 *    ( SELECT "+函数${function}+"("+${field对应的字段名}+") FROM  目标表名 别名 where ${连接条件} ) 别名
	 *    true: 
	 *     (自己写的function${function}) 别名
	 *    
	 * @return
	 */
	boolean isAutoFunction() default false;

	/**
	 * 在 join的表中添加wehre 条件 注意 类名为主表的别名 一个表只能存在一个where
	 */
	String where() default "";
}
