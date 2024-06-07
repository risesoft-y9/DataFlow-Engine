package net.risedata.jdbc.mapping.columns;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import net.risedata.jdbc.annotations.factory.Factory;
import net.risedata.jdbc.commons.map.LMap;
import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.exception.InstanceException;
import net.risedata.jdbc.executor.search.SearchExecutor;
import net.risedata.jdbc.factory.impl.SpringApplicationFactory;
import net.risedata.jdbc.mapping.ColumnMapping;
import net.risedata.jdbc.repository.model.Type;
import net.risedata.jdbc.repository.parse.ReturnTypeFactory;


/**
 * 一对多 columns 操作类 args 为 ['当前类连接id','目标类id']
 * 
 * @author lb
 *
 */
@Factory(SpringApplicationFactory.class)
public class OneToManyColumnMapping implements ColumnMapping {



	private Field field;

	private String sourceId;

	private String targetId;

	private boolean isList;

	private Class<?> typeClass;

	@Autowired
	private SearchExecutor searchExecutor;

	public OneToManyColumnMapping() {

	}

	/**
	 * @param bc
	 * @param field
	 * @param sourceId
	 * @param targetId
	 * @param isList
	 * @param typeClass
	 */
	public OneToManyColumnMapping(Field field, String sourceId, String targetId, boolean isList,
			Class<?> typeClass, SearchExecutor searchExecutor) {
		this.field = field;
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.isList = isList;
		this.typeClass = typeClass;
		this.searchExecutor = searchExecutor;
	}

	@Override
	public ColumnMapping create(BeanConfig bc, Field field, String[] args) {
		if (args.length == 2) {
			Type type = ReturnTypeFactory.parseType(field.getGenericType().getTypeName());
			Class<?> typeClass = null;
			if (type.isList()) {
				typeClass = type.getGeneralClass();
			} else {
				typeClass = type.getType();
			}
			return new OneToManyColumnMapping( field, args[0], args[1], type.isList(), typeClass, searchExecutor);
		} else {
			throw new InstanceException("create OneToManyColumnMapping args 错误  ['当前类连接id','目标类id']");
		}
	}

	@Override
	public void handle(Object value, BeanConfig bc) {
		try {
			if (this.isList) {
				List<?> list = searchExecutor.searchForList(this.typeClass, null,
						LMap.createMap(this.targetId, bc.getValue(value, this.sourceId)), this.typeClass);
				field.set(value, list);
			} else {
				Object object = searchExecutor.findOne(this.typeClass,
						LMap.createMap(this.targetId, bc.getValue(value, this.sourceId)), null, false, this.typeClass);
				field.set(value, object);
			}
		} catch (Exception e) {
			throw new InstanceException("处理一对多操作出错 "+ e.getMessage());
		}

	}

}
