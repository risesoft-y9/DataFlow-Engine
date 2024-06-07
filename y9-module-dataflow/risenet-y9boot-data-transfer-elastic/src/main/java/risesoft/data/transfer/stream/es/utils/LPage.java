package risesoft.data.transfer.stream.es.utils;

import java.util.List;

public class LPage<T> {
	/**
	 * 多少条
	 */
	private long total;
	/**
	 * 当前页数
	 */
	private int pageno;
	/**
	 * size
	 */
	private int pagesize;
	/**
	 * 一共多少页
	 */
	private int totalpages;
	/**
	 * 数据
	 */
	private List<T> content;

	/**
	 * @return the total
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(long total) {
		this.total = total;
		if (total == 0) {
			this.totalpages = 1;
			return;
		}
		this.totalpages = (int) (total % pagesize == 0 ? total / pagesize : total / pagesize + 1);
	}

	/**
	 * @return the pageno
	 */
	public int getPageno() {
		return pageno;
	}

	/**
	 * @param pageno the pageno to set
	 */
	public void setPageno(int pageno) {
		this.pageno = pageno;
	}

	/**
	 * @return the pagesize
	 */
	public int getPagesize() {
		return pagesize;
	}

	/**
	 * @param pagesize the pagesize to set
	 */
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	/**
	 * @return the totalpages
	 */
	public int getTotalpages() {
		return totalpages;
	}

	/**
	 * @param totalpages the totalpages to set
	 */
	public void setTotalpages(int totalpages) {
		this.totalpages = totalpages;
	}

	@Override
	public String toString() {
		return "MyPage [total=" + total + ", pageno=" + pageno + ", pagesize=" + pagesize + ", totalpages=" + totalpages
				+ ", contextList=" + content + "]";
	}

	public int getSize() {
		return content.size();
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public long getTotalElements() {
		return total;
	}

}
