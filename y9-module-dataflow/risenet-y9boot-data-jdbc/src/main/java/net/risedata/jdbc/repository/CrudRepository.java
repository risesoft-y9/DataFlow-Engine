package net.risedata.jdbc.repository;



/**
 * 提供基础的crud 方法的代理对象
 * 
 * @author lb
 * @date 2023年3月10日 下午5:31:03
 * @param <T>  操作的对象
 * @param <ID> id
 */
public interface CrudRepository<Entiry, ID> extends Repository<Entiry> {

	/**
	 * 根据id 获取对象
	 * 
	 * @param id
	 * @return
	 */
	Entiry findById(ID id);

	/**
	 * 根据id 删除对象
	 * 
	 * @param id
	 * @return
	 */
	Integer deleteById(ID id);


}
