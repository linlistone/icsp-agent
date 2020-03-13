package cn.com.yusys.icsp.agent.server.socket;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yusys.icsp.agent.common.beans.FileSplitUploadInfo;
import cn.com.yusys.icsp.agent.common.util.AgentUtil;
import cn.com.yusys.icsp.agent.server.beans.StartParameter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FileSplitUploadServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(FileUploadServerHandler.class);;
	private RandomAccessFile randomAccessFile;

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		if (msg instanceof FileSplitUploadInfo) {
			final FileSplitUploadInfo fileSplitUploadInfo = (FileSplitUploadInfo) msg;
			if ("readying".equals(fileSplitUploadInfo.getTransferType())) {
				String fileDir = fileSplitUploadInfo.getFileDir();
				if (fileDir == null || "".equals(fileDir)) {
					fileDir = StartParameter.parameter.getFilePath();
				}
				final String path = AgentUtil.builder(100, fileDir, File.separator, fileSplitUploadInfo.getFileName());
				logger.info("文件[{}]准备写入!", path);
				final File file = new File(path);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				if (file.exists()) {
					final File renameFile = new File(
							path + "." + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
					logger.warn("文件{}已存在, 重命名为:{}", file.getAbsolutePath(), renameFile.getAbsolutePath());
					file.renameTo(renameFile);
				}
				(this.randomAccessFile = new RandomAccessFile(file, "rw"))
						.setLength(fileSplitUploadInfo.getTotalSize());
				ctx.writeAndFlush("ready complete");
			} else if ("transferring".equals(fileSplitUploadInfo.getTransferType())) {
				logger.info("切分文件{}写入, 当前跳过字节{}", fileSplitUploadInfo.getPartFileName(),
						fileSplitUploadInfo.getStartPos());
				this.randomAccessFile.seek(fileSplitUploadInfo.getStartPos());
				this.randomAccessFile.write(fileSplitUploadInfo.getBytes(), 0, fileSplitUploadInfo.getPartFileSize());
				ctx.writeAndFlush("write complete");
			} else if ("ending".equals(fileSplitUploadInfo.getTransferType())) {
				if (this.randomAccessFile != null) {
					this.randomAccessFile.close();
				}
				logger.info("文件[{}]写入完成!", fileSplitUploadInfo.getFileName());
				ctx.writeAndFlush("end complete");
			}
		} else {
			logger.warn("当前传入的消息[{}]不为文件分割上传消息, 当前已忽略!");
			ctx.close();
		}
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
		logger.error("netty socket通讯异常! cause by:{}", cause.getMessage(), cause);
		if (this.randomAccessFile != null) {
			try {
				this.randomAccessFile.close();
			} catch (IOException ex) {
			}
		}
		ctx.close();
	}

}
