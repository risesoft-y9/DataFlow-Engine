package net.risedata.jdbc.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import net.risedata.jdbc.annotations.Exclude;
import net.risedata.jdbc.annotations.join.Join;
import net.risedata.jdbc.annotations.join.NULL;
import net.risedata.jdbc.annotations.operation.Operate;
import net.risedata.jdbc.annotations.operation.OperateCollection;
import net.risedata.jdbc.annotations.order.Asc;
import net.risedata.jdbc.annotations.order.Ascs;
import net.risedata.jdbc.annotations.order.Desc;
import net.risedata.jdbc.annotations.order.Descs;
import net.risedata.jdbc.annotations.search.Sync;
import net.risedata.jdbc.annotations.searchable.Searchable;
import net.risedata.jdbc.annotations.update.Check;
import net.risedata.jdbc.annotations.update.UpdateId;
import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.config.model.JoinConfig;
import net.risedata.jdbc.config.model.JoinFieldConfig;
import net.risedata.jdbc.factory.BeanConfigFactory;
import net.risedata.jdbc.factory.ColumnMappingFactory;
import net.risedata.jdbc.factory.ConditionProxyFactory;
import net.risedata.jdbc.factory.HandleMappingFactory;
import net.risedata.jdbc.factory.OperationFactory;
import net.risedata.jdbc.mapping.HandleMapping;
import net.risedata.jdbc.mapping.impl.EntiryHandle;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.operation.OperationInit;
import net.risedata.jdbc.operation.OperationPack;
import net.risedata.jdbc.search.Operations;
import net.risedata.jdbc.search.Order;
import net.risedata.jdbc.search.exception.FieldException;
import net.risedata.jdbc.search.exception.JoinException;
import net.risedata.jdbc.search.exception.NoEntityException;
import net.risedata.jdbc.search.exception.OperationException;
import net.risedata.jdbc.utils.FieldUtils;

/**
 * 用于加载
 * 
 * @author libo 2020年10月20日
 */
public class Load {
	public static void loadBean(Class<?> entiry) {
		BeanConfig bc = new BeanConfig();
		if (BeanConfigFactory.has(entiry.getName())) {
			return;
		}
		Searchable sreachable = AnnotationUtils.findAnnotation(entiry, Searchable.class);
		if (sreachable != null) {
			loadSreachable(sreachable, entiry);
			return;
		}

		Table e = AnnotationUtils.findAnnotation(entiry, Table.class);
		if (e == null) {
			return;
		}
		String tableName = e.name();
		bc.setTableName(tableName);
		bc.setCla(entiry);
		bc.setSync(AnnotationUtils.findAnnotation(entiry, Sync.class) != null);
		if (bc.isSync()) {
			JdbcConfig.isSync = true;
		}
		try {
			bc.setConstructor(entiry.getConstructor());
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();

		}
		Map<String, FieldConfig> fs = new HashMap<String, FieldConfig>();
		bc.setFields(fs);
		Map<String, Field> fieldMap = createFieldMap(entiry);
		loadBean(entiry, bc, fieldMap, null);

	}

	/**
	 * 拿到字段的map 会给字段加上对应的get set
	 * 
	 * @param entiry
	 * @return
	 */
	private static Map<String, Field> createFieldMap(Class<?> entiry) {
		List<Field> fields = FieldUtils.getFields(entiry);
		Map<String, Field> FieldMap = new HashMap<>();
		fields.forEach((Field f) -> {
			if (Modifier.isStatic(f.getModifiers())) {
				return;
			}
			FieldMap.put("get" + f.getName(), f);
			FieldMap.put("set" + f.getName(), f);
		});
		return FieldMap;
	}

	/**
	 * 加载bean
	 * 
	 * @param entiry   加载的对象
	 * @param bc       bean 的配置
	 * @param fieldMap 字段映射 get 的字段 set 的字段
	 */
	private static void loadBean(Class<?> entiry, BeanConfig bc, Map<String, Field> fieldMap,
			Map<String, String> cloumMap) {

		List<Field> fields = FieldUtils.getFields(entiry);
		List<Field> joinFields = new ArrayList<>();
		for (int i = 0; i < fields.size(); i++) {
			Field item = fields.get(i);
			if (Modifier.isStatic(item.getModifiers())) {
				continue;
			}
			item.setAccessible(true);
			if (!loadFieldConfig(bc, item, fieldMap, cloumMap)) {
				joinFields.add(item);
			}

		}

		BeanConfigFactory.putBeanConfig(entiry, bc);
		joinFields.forEach((Field field) -> {
			loadJoin(bc, field, fieldMap);
		});
		for (int i = 0; i < fields.size(); i++) {
			Field item = fields.get(i);
			if (Modifier.isStatic(item.getModifiers())) {
				continue;
			}
			bc.addColumnMapping(ColumnMappingFactory.getInstance(bc, item));
		}
		bc.sort();
	}

	/**
	 * 加载查询配置器
	 * 
	 * @param sreachable
	 */
	private static void loadSreachable(Searchable sreachable, Class<?> entiry) {

		BeanConfig bc = BeanConfigFactory.getInstance(sreachable.value());
		if (bc == null) {
			throw new NoEntityException(sreachable.value() + " undefined entiry object");
		}
		BeanConfig sreachBeanConfig = new BeanConfig();
		bc.setSync(AnnotationUtils.findAnnotation(entiry, Sync.class) != null);
		String tableName = sreachable.tableName();
		sreachBeanConfig.setCla(bc.getCla());
		sreachBeanConfig.setConstructor(bc.getConstructor());
		if (bc.isSync()) {
			JdbcConfig.isSync = true;
		}
		if (StringUtils.isBlank(tableName)) {
			if (sreachable.rows()) {
				sreachBeanConfig.setSelectTableSql(bc.getSelectTableSql(null, false));
				sreachBeanConfig.setDelSql(bc.getDelSql());
				sreachBeanConfig.setCountTableSql(bc.getCountTableSql());
				sreachBeanConfig.setTableFrom(bc.getTableFrom());
				sreachBeanConfig.setRowMappingList(bc.getRowMappingList());
			}
			sreachBeanConfig.setTableName(bc.getTableName());
		} else {
			tableName = tableName.toUpperCase();
			if (sreachable.rows()) {
				sreachBeanConfig
						.setSelectTableSql(bc.getSelectTableSql(null, false).replace(bc.getTableName(), tableName));
				sreachBeanConfig.setDelSql(bc.getDelSql().replace(bc.getTableName(), tableName));
				sreachBeanConfig.setCountTableSql(bc.getCountTableSql().replace(bc.getTableName(), tableName));
				sreachBeanConfig.setTableFrom(bc.getTableFrom().replace(bc.getTableName(), tableName));
				sreachBeanConfig.setRowMappingList(bc.getRowMappingList());
			}
			sreachBeanConfig.setTableName(tableName);
		}

		List<FieldConfig> allFieldConfigs = bc.getAllFields();

		Map<String, String> cloumMap = new HashMap<>();
		Map<String, Field> fieldMap = new HashMap<>();
		allFieldConfigs.forEach((fc) -> {
			cloumMap.put(fc.getFieldName(), fc.getColumn());
		});
		List<Field> bcFields = FieldUtils.getFields(bc.getCla());
		for (Field field : bcFields) {// 拿到get方法 get 使用自己的get
			if (Modifier.isStatic(field.getModifiers())) {// 忽略静态变量
				continue;
			}
			fieldMap.put("set" + field.getName(), field);
		}
		List<Field> fields = FieldUtils.getFields(entiry);
		for (Field field : fields) {// 拿到get方法 get 使用自己的get
			if (Modifier.isStatic(field.getModifiers())) {// 忽略静态变量
				continue;
			}
			fieldMap.put("get" + field.getName(), field);
		}
		loadBean(entiry, sreachBeanConfig, fieldMap, cloumMap);
	}

	/**
	 * 加载正常的字段配置
	 * 
	 * @param bc
	 * @param item
	 * @param methodMap
	 * @param cloumMap
	 * @return
	 */
	private static boolean loadFieldConfig(BeanConfig bc, Field item, Map<String, Field> fieldMap,
			Map<String, String> cloumMap) {
		Column c = item.getAnnotation(Column.class);
		if (item.getAnnotation(Exclude.class) != null) {
			return true;
		}
		if (c == null && (cloumMap == null || !cloumMap.containsKey(item.getName()))) {
			return false;
		}

		String fieldName = item.getName();
		Field getField = getGetField(fieldName, fieldMap, bc.getCla());
		Field setField = getSetField(fieldName, fieldMap, bc.getCla());

		FieldConfig fc = new FieldConfig();
		String column = c == null ? cloumMap.get(item.getName())
				: StringUtils.isBlank(c.name()) ? fieldName.toUpperCase() : c.name().toUpperCase();
		column = column.trim();
		Class<?> type = setField.getType();
		@SuppressWarnings("rawtypes")
		HandleMapping handle = HandleMappingFactory.getInstance(type);
		if (handle == null) {
			throw new FieldException("field" + item.getName() + " type " + type + "no handle");
		}
		Ascs ascs = item.getAnnotation(Ascs.class);
		if (ascs != null) {
			Asc[] as = ascs.value();
			for (int i = 0; i < as.length; i++) {
				Order order = new Order(column, Order.ASC, as[i].leve());
				order.setExpression(as[i].expression());
				bc.addOrder(order);
			}
		} else {
			Asc asc = item.getAnnotation(Asc.class);
			if (asc != null) {
				Order order = new Order(column, Order.ASC, asc.leve());
				order.setExpression(asc.expression());
				bc.addOrder(order);
			}
		}
		Descs descs = item.getAnnotation(Descs.class);
		if (descs != null) {// 添加order
			Desc[] ds = descs.value();
			for (int i = 0; i < ds.length; i++) {
				Order order = new Order(column, Order.DESC, ds[i].leve());
				order.setExpression(ds[i].expression());
				bc.addOrder(order);
			}
		} else {// 检查desc
			Desc desc = item.getAnnotation(Desc.class);

			if (desc != null) {
				Order order = new Order(column, Order.DESC, desc.leve());
				order.setExpression(desc.expression());
				bc.addOrder(order);
			}
		}
		fc.setUpdateCheck(item.getAnnotation(Check.class) != null);
		fc.setTransient((item.getAnnotation(Transient.class) != null && item.getAnnotation(Join.class) == null));
		fc.setHandle(handle);
		fc.setFieldType(type);
		fc.setField(item);
		fc.setFieldName(item.getName());
		fc.setValueField(getField);
		fc.setColumn(column);
		loadCollection(item, fc, bc);
		if (fc.getDefaultOperation() == null) {
			fc.setOperation(OperationFactory.getDefaultOperation(item));// 拿到默认的操作
		}
		// 加载集合操作
		fc.setSetValueField(setField);
		bc.putField(item.getName(), fc);
		if (item.getAnnotation(Id.class) != null) {
			fc.setId(true);
		}
		;
		UpdateId update = item.getAnnotation(UpdateId.class);
		if (update != null) {
			fc.setUpdateId(true);
			fc.setUpdateWhere(update.isUpdate());
			fc.setPlaceholder(update.isPlaceholder());
		}
		return true;
	}

	/**
	 * 加载集合 形式的注解OperateCollection
	 * 
	 * @param field
	 * @param fc
	 * @param bc
	 */
	private static void loadCollection(Field field, FieldConfig fc, BeanConfig bc) {
		OperateCollection ops = field.getAnnotation(OperateCollection.class);
		OperationInit opInit;
		Operation op = null;
		if (ops != null) {
			Operate[] valuess = ops.value();
			if (valuess.length > 0) {
				for (Operate operate : valuess) {
					op = operate.value().value;
					if (op == Operations.PLACEHOLDER) {
						if (operate.operation() != Operations.PLACEHOLDER.getClass()) {
							op = OperationFactory.getOperation(operate.operation());
						} else {
							throw new OperationException(bc.getCla() + " filed " + field.getName()
									+ " Use placeholders but do not define their own operations ");
						}
						if (op instanceof OperationInit) {
							opInit = (OperationInit) op;
							opInit.initial(bc.getCla(), field);
						}
					}
					if (StringUtils.isBlank(operate.expression())) {// 无条件
						if (fc.getDefaultOperation() == null) {
							fc.setDefaultOperation(op);
						} else {
							throw new OperationException(bc.getCla() + " filed " + field.getName()
									+ " There can only be one default operation");
						}
					} else {
						fc.addOperationPack(new OperationPack(operate.expression(), op));
					}
				}

			}
		}
	}

	/**
	 * 拿到 set 的field
	 * 
	 * @param fieldName
	 * @param methodMap
	 * @param boot
	 * @return
	 */
	private static Field getSetField(String fieldName, Map<String, Field> fieldMap, Class<?> boot) {
		String setName = "set" + fieldName;
		Field f = fieldMap.get(setName);
		if (f == null) {
			throw new NullPointerException(fieldName + " is set null");
		}
		f.setAccessible(true);
		return f;
	}

	/**
	 * 拿到get数据的field
	 * 
	 * @param fieldName
	 * @param methodMap
	 * @param boot
	 * @return
	 */
	private static Field getGetField(String fieldName, Map<String, Field> FieldMap, Class<?> boot) {

		Field f = FieldMap.get("get" + fieldName);
		if (f == null) {
			throw new NullPointerException(fieldName + "  is get null");
		}
		return f;
	}

	/**
	 * 加载 join的配置
	 * 
	 * @param bc
	 * @param item
	 * @param methodMap
	 * @return
	 */
	private static boolean loadJoin(BeanConfig bc, Field item, Map<String, Field> fieldMap) {
		Join j = item.getAnnotation(Join.class);
		if (j == null) {
			return false;
		}
		JoinConfig jc = bc.getJoin(j.value());
		if (jc == null || jc.isFunction()) {
			jc = new JoinConfig();
			if (StringUtils.isNotBlank(j.function())) {
				jc.setFunction(true);
			}
			if (j.value() == NULL.class) {// 字段为funcation
				if (StringUtils.isBlank(j.function())) {
					throw new JoinException("joined class no config or function is null");
				}

			} else {
				BeanConfig bc2 = BeanConfigFactory.getInstance(j.value());
				jc.setTableClass(j.value());
				if (bc2 == null) {
					throw new JoinException("joined class no config ");
				} else {

					jc.setJoinBean(bc2);
					jc.setTableName(bc2.getTableName());
				}
			}
			jc.setMyBean(bc);

			bc.addJoin(jc);
		}

		if (j.joinId().length > 0 && StringUtils.isNotBlank(j.joinId()[0])) {
			if (jc.getJoinId() != null) {
				throw new FieldException(item.getName() + "连接表配置" + j.toId() + "一个表的joinid已经被配置了不允许重复配置 在一个字段上配置即可");
			}
			jc.setJoinId(j.joinId());
		}
		if (j.toId().length > 0 && StringUtils.isNotBlank(j.toId()[0])) {
			if (jc.getToId() != null) {
				throw new FieldException(item.getName() + "连接表配置" + j.toId() + "一个表的toid已经被配置了不允许重复配置 在一个字段上配置即可");
			}
			jc.setToId(j.toId());
		}
		if (StringUtils.isNotBlank(j.where())) {
			jc.setWhere(j.where());
		}

		if (StringUtils.isNotBlank(j.field()) || EntiryHandle.isEntiryHandle(item.getType())) {// 连接获取整个entiry对象的问题
			JoinFieldConfig jfc = new JoinFieldConfig();
			Field setField = getSetField(item.getName(), fieldMap, bc.getCla());
			FieldConfig fc = new FieldConfig();
			jfc.setFc(fc);
			fc.setSetValueField(setField);
			fc.setFieldType(setField.getType());
			fc.setField(item);

			fc.setId(false);
			jfc.setFunction(j.function());
			jfc.setAutoFunction(j.isAutoFunction());
			@SuppressWarnings("unchecked")
			HandleMapping<Object> handle = HandleMappingFactory.getInstance(fc.getFieldType());
			if (handle == null) {
				if (EntiryHandle.isEntiryHandle(setField.getType())) {// entiry的配置
					BeanConfig fieldConfig = BeanConfigFactory.getInstance(setField.getType());
					List<FieldConfig> fields = new ArrayList<FieldConfig>();
					FieldConfig ff2;
					for (FieldConfig fc2 : fieldConfig.getFieldlist()) {// 将对象配置中基础的fieldconfig 转换为新的
						ff2 = new FieldConfig();
						ff2.setHandle(fc2.getHandle());
						ff2.setId(fc2.isId());
						ff2.setField(fc2.getField());
						ff2.setSetValueField(fc2.getSetValueField());
						ff2.setFieldType(fc2.getFieldType());
						ff2.setFieldName(fc2.getFieldName());
						ff2.setColumn(fc2.getColumn());
						fields.add(ff2);
					}
					fc.setEntiry(true);
					jfc.setFields(fields);
					fc.setHandle(new EntiryHandle(fields, fieldConfig));

				} else {
					throw new FieldException(item.getName() + " null mapping handle ");
				}
			} else {// 非 entiry去join的表中获取字段配置
				if (j.field().length() > 0) {
					jfc.setColumn(j.field());
				} else {

					if (j.value() != NULL.class) {
						FieldConfig ff = BeanConfigFactory.getInstance(j.value()).getField(j.field());

						if (ff != null) {
							jfc.setColumn(ff.getColumn());
						} else if (jc.isFunction()) {
							jfc.setColumn(j.field());
						} else {
							throw new FieldException(item.getName() + " null joined fieldConfig ");
						}
					} else {
						jfc.setColumn(item.getName());
					}

				}
			}
			if (!fc.isEntiry()) {// 非 entiry
				fc.setHandle(handle);
			}
			jc.addField(jfc);
		}
		if (StringUtils.isNotBlank(j.expression())) {
			if (jc.getCondition() != null) {
				throw new JoinException(item.getName() + " join table expression  exist!!");
			}
			jc.setCondition(ConditionProxyFactory.getInstance(j.expression(), bc.getCla()));// 根据表达式生成
		}
		return true;
	}
}
