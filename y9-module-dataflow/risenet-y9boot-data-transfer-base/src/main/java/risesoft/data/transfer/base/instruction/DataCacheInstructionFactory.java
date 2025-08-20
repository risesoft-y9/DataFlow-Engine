package risesoft.data.transfer.base.instruction;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.FrameworkErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.handle.cache.CacheHandle;
import risesoft.data.transfer.core.instruction.factory.Instruction;
import risesoft.data.transfer.core.instruction.factory.InstructionFactory;
import risesoft.data.transfer.core.start.StartConfiguration;

/**
 * 数据缓存指令工厂 用于数据的缓存获取与添加 包含 get,poll,put,push :使用set会将需要保存的值替换到配置文件,
 * $data{get#key} $data{poll#key#pollsize} set型指令 会保存到缓存中并返回value 包含push 和put
 * $data{put#key#value} push 在插件中使用
 * 
 * @typeName DataCacheInstructionFactory
 * @date 2025年8月15日
 * @author lb
 */
public class DataCacheInstructionFactory implements InstructionFactory, StartConfiguration {

	@Override
	public String getName() {
		return "data";
	}

	@Override
	public Instruction getInstance(String[] args, String config) {
		final String operation = args[0];

		return new Instruction() {

			@Override
			public String executor(String config, JobContext jobContext) {
				CacheHandle cacheHandle = jobContext.getInstance(CacheHandle.class);
				switch (operation) {
				case "get":
					return cacheHandle.get(args[1], String.class);
				case "poll":
					return StringUtils.join(cacheHandle.poll(args[1], Integer.parseInt(args[2]), String.class), ",");
				case "put":
					cacheHandle.put(args[1], args[2]);
					return args[2];
				case "push":
					cacheHandle.push(args[1], args[2]);
					return args[2];
				default:
					throw TransferException.as(CommonErrorCode.CONFIG_ERROR, "无效的操作:" + operation);

				}
			}
		};
	}

}
