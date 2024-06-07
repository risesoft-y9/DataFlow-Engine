package risesoft.data.transfer.plug.data.encrypt;


import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.factory.annotations.ConfigParameter;
import risesoft.data.transfer.core.plug.Plug;
import risesoft.data.transfer.core.record.ColumnDisposeHandlePlug;

/**
 * 对称加密AES
 * 
 * @typeName AESEncrypt
 * @date 2024年3月13日
 * @author lb
 */
public class AESEncryptPlug implements Plug {
	private Cipher cipher;

	public AESEncryptPlug(@ConfigParameter(required = true, description = "密钥") String key,
			@ConfigParameter(required = true, description = "列名") String field, JobContext jobContext) {

		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("UTF-8"), "AES"));
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.CONFIG_ERROR, "创建加密对象失败!" + e.getMessage());
		}
		ColumnDisposeHandlePlug.registerListener(field, (c, r, i) -> {
			Column column;
			try {
				column = new StringColumn(
						Base64.getEncoder().encodeToString(cipher.doFinal(c.asString().getBytes("UTF-8"))),
						c.getName());
				r.setColumn(i, column);
				return column;
			} catch (Exception e) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "对称加密AES失败:" + e.getMessage());
			}
		}, jobContext);
	}

	@Override
	public boolean register(JobContext jobContext) {
		return true;
	}

}
