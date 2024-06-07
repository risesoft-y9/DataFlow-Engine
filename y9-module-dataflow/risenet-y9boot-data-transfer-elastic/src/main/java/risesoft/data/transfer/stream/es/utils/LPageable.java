package risesoft.data.transfer.stream.es.utils;


/**
 * {@link Page} 用于分页查询canshu
 * @author libo
 *2020年10月10日
 */
public class LPageable {
   private Integer pageNo;
   private Integer pageSize;
   private LSort sort;
   public LPageable() {
	   
   }
   public LPageable(Integer pageNo, Integer pageSize, LSort sort) {
		super();
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.sort = sort;
   }
   public LPageable(Integer pageNo, Integer pageSize) {
		super();
		this.pageNo = pageNo;
		this.pageSize = pageSize;
    }
	/**
	 * @return the pageNo
	 */
	public Integer getPageNo() {
		return pageNo;
	}
	/**
	 * @param pageNo the pageNo to set
	 */
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	/**
	 * @return the pageSize
	 */
	public Integer getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * @return the sort
	 */
	public LSort getSort() {
		return sort;
	}
	/**
	 * @param sort the sort to set
	 */
	public void setSort(LSort sort) {
		this.sort = sort;
	}
	@Override
	public String toString() {
		return "LPage [pageNo=" + pageNo + ", pageSize=" + pageSize + "]";
	}
  
}
