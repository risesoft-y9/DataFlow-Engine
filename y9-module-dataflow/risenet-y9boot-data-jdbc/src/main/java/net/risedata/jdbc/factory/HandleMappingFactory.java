package net.risedata.jdbc.factory;

import java.util.ArrayList;
import java.util.List;

import net.risedata.jdbc.mapping.CastHandleMapping;
import net.risedata.jdbc.mapping.HandleMapping;
import net.risedata.jdbc.mapping.impl.BigHandle;
import net.risedata.jdbc.mapping.impl.BlobHandle;
import net.risedata.jdbc.mapping.impl.BooleanHandle;
import net.risedata.jdbc.mapping.impl.ByteArrayHandle;
import net.risedata.jdbc.mapping.impl.ByteHandle;
import net.risedata.jdbc.mapping.impl.ClobHandle;
import net.risedata.jdbc.mapping.impl.DateHandle;
import net.risedata.jdbc.mapping.impl.DoubleHandle;
import net.risedata.jdbc.mapping.impl.IntHandle;
import net.risedata.jdbc.mapping.impl.LongHandle;
import net.risedata.jdbc.mapping.impl.ShortHandle;
import net.risedata.jdbc.mapping.impl.StringHandle;
import net.risedata.jdbc.search.exception.MappingException;

/**
 * 拿到处理字段映射的handle
 * @author libo
 *2020年10月15日
 */
public class HandleMappingFactory {
    @SuppressWarnings("rawtypes")
	private static final List<HandleMapping> HANDLES = new ArrayList<>();
    @SuppressWarnings("rawtypes")
	private static final List<CastHandleMapping> CASTS = new ArrayList<>();
    private static final IntHandle INTHANDLE = new IntHandle();
    private static final DoubleHandle DOUBLEHANDLE = new DoubleHandle();
    private static final BlobHandle BLOBHANDLE = new BlobHandle();
    private static final BigHandle BIGHANDLE = new BigHandle();
    private static final StringHandle STRINGHANDLE = new StringHandle();
    private static final ClobHandle CLOBHANDLE = new ClobHandle();
    private static final DateHandle DATEHANDLE = new DateHandle();
    private static final LongHandle LONGHANDLE = new LongHandle();
    private static final ShortHandle SHORTHANDLE = new ShortHandle();
    private static final ByteHandle BYTEHANDLE = new ByteHandle();
    private static final BooleanHandle BOOLEANHANDLE = new BooleanHandle();
    private static final ByteArrayHandle BYTEARRAYHANDLE = new ByteArrayHandle();
    static {
    	HANDLES.add(BYTEARRAYHANDLE);
    	HANDLES.add(BOOLEANHANDLE);
    	HANDLES.add(INTHANDLE);
    	HANDLES.add(DOUBLEHANDLE);
    	HANDLES.add(STRINGHANDLE);
    	HANDLES.add(BIGHANDLE);
    	HANDLES.add(BLOBHANDLE);
    	HANDLES.add(CLOBHANDLE);
    	HANDLES.add(DATEHANDLE);
    	HANDLES.add(LONGHANDLE);
    	HANDLES.add(SHORTHANDLE);
    	HANDLES.add(BYTEHANDLE);
    	CASTS.add(BOOLEANHANDLE);
    	CASTS.add(INTHANDLE);
    	CASTS.add(STRINGHANDLE);
    	CASTS.add(SHORTHANDLE);
    	CASTS.add(BYTEHANDLE);
    	CASTS.add(LONGHANDLE);
    	CASTS.add(DOUBLEHANDLE);
    	CASTS.add(DATEHANDLE);
    	CASTS.add(BIGHANDLE);
    
    }
    /**
     * 拿到数据库中处理参数的handle
     * @param type
     * @return
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HandleMapping getInstance(Class<?> type) {
    	for (HandleMapping handleMapping : HANDLES) {
			if(handleMapping.isHandle(type)) {
				return handleMapping;
			}
		}
    	return null;
    }
	/**
	 * 根据一个对象和类型进行解析or强转
	 * @param <T>
	 * @param o
	 * @param fieldType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parse(Object o, Class<T> fieldType) {
		for (CastHandleMapping<?> castHandleMapping : CASTS) {
			if (castHandleMapping.isHandle(fieldType)) {
				return (T) castHandleMapping.toValue(o);
			}
		}
		throw new MappingException(o+" type "+fieldType+"no cast");
	}
}
