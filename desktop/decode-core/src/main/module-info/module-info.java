/**
 * @author yinxueqin
 */
module top.yinlingfeng.xlog.decode.core {
	requires java.base;
	requires org.bouncycastle.provider;
	requires org.apache.commons.lang3;
	requires java.logging;

	exports top.yinlingfeng.xlog.decode.core;
	exports top.yinlingfeng.xlog.decode.core.log;

	opens top.yinlingfeng.xlog.decode.core.log;

	uses java.security.Provider;
}
