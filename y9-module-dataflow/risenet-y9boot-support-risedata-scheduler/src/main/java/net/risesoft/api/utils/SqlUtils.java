package net.risesoft.api.utils;

import oshi.jna.platform.mac.SystemB;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.risedata.jdbc.factory.OperationBuilderFactory;
import net.risedata.jdbc.operation.Custom;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.operation.Where;
import net.risedata.jdbc.operation.impl.CustomOperation;

/**
 * @Description : sql 操作utils
 * @ClassName SqlUtils
 * @Author lb
 * @Date 2022/8/8 17:21
 * @Version 1.0
 */
public class SqlUtils {

	public static Operation toInLike(String field, List<String> ins) {
		return new CustomOperation(where -> {
			if (ins.isEmpty() || (ins.size() == 1 && "%%".equals(ins.get(0)))) {
				return false;
			}
			String fieldC = where.getCloum(field);
			where.append("(");
			for (int i = 0; i < ins.size(); i++) {

				if (i != 0) {
					where.append(" or ");
				}
				where.append(fieldC + " like ? ");
				where.add(ins.get(i));
			}
			where.append(")");
			return true;
		});
	}

	/**
	 * 直接追加(???)
	 * 
	 * @param ins
	 * @param where
	 */
	public static void appendIn(List<String> ins, Where where) {

		where.append("(");
		for (int i = 0; i < ins.size(); i++) {
			if (i != 0) {
				where.append(",?");
			} else {
				where.append("?");
			}
			where.add(ins.get(i));
		}
		where.append(")");

	}

	private static int maxSize = 999;

	public static <T> void toInsSql(T[] ids, String sqlPre, StringBuilder sql) {
		sql.append("(");
		int split = ids.length % maxSize == 0 ? ids.length / maxSize : ((ids.length / maxSize) + 1);
		for (int i = 0; i < split; i++) {
			if (i != 0) {
				sql.append(" or ");
			}
			sql.append(sqlPre).append("(");
			T[] oids = Arrays.copyOfRange(ids, i * 999, i == split - 1 ? ids.length : (i + 1) * 999);
			for (int j = 0; j < oids.length; j++) {
				if (j != 0) {
					sql.append(",?");
				} else {
					sql.append("?");
				}
			}
			sql.append(")");
		}
		sql.append(")");

	}

	public static Operation toInLike(String field, String[] ins, boolean isPre) {
		return new CustomOperation(where -> {

			if (ins == null || ins.length == 0 || (ins.length == 1 && "**".equals(ins[0]))) {
				return false;
			}
			String pre = isPre ? "%" : "";
			String fieldC = where.getCloum(field);
			where.append("(");
			for (int i = 0; i < ins.length; i++) {

				if (i != 0) {
					where.append(" or ");
				}
				where.append(fieldC + " like ? ");
				where.add(pre + ins[i].replace("**", "%").replace("*", "%"));
			}
			where.append(")");
			return true;
		});
	}

	public static Map<String, Operation> createInlikeOperaion(String field, String cluom, String[] ins, boolean isPre) {
		return OperationBuilderFactory.builder(field, SqlUtils.toInLike(cluom, ins, isPre));
	}
}
