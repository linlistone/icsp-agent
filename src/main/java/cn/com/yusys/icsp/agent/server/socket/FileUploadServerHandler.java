package cn.com.yusys.icsp.agent.server.socket;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yusys.icsp.agent.common.beans.FileUploadInfo;
import cn.com.yusys.icsp.agent.server.beans.StartParameter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FileUploadServerHandler extends ChannelInboundHandlerAdapter
{
    private static final Logger logger;
    private volatile int start;
    private volatile int length;
    private RandomAccessFile randomAccessFile;
    private volatile boolean isComplete;
    
    public FileUploadServerHandler() {
        this.start = 0;
        this.isComplete = true;
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof FileUploadInfo) {
            final FileUploadInfo fileUploadInfo = (FileUploadInfo)msg;
            final byte[] bytes = fileUploadInfo.getBytes();
            this.length = fileUploadInfo.getLength();
            FileUploadServerHandler.logger.info("传输文件:{},已传输:{}M,本次传输长度:{}KB", fileUploadInfo.getFileName(), this.start / 1048576, this.length / 1024);
            if (this.isComplete) {
                final String fileName = fileUploadInfo.getFileName();
                String fileDir = fileUploadInfo.getFileDir();
                if (fileDir == null || "".equals(fileDir)) {
                    fileDir = StartParameter.parameter.getFilePath();
                }
                final String path = fileDir + File.separator + fileName;
                FileUploadServerHandler.logger.info("写入文件的路径为:{}", path);
                final File file = new File(path);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (file.exists()) {
                    final File renameFile = new File(path + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                    FileUploadServerHandler.logger.warn("文件{}已存在, 重命名为:{}", file.getAbsolutePath(), renameFile.getAbsolutePath());
                    file.renameTo(renameFile);
                }
                this.randomAccessFile = new RandomAccessFile(file, "rw");
                this.isComplete = false;
                ctx.writeAndFlush(this.start);
            }
            else if (this.length > 0) {
                this.randomAccessFile.seek(this.start);
                this.randomAccessFile.write(bytes, 0, this.length);
                this.start += this.length;
                ctx.writeAndFlush(this.start);
            }
            else {
                this.randomAccessFile.close();
                ctx.close();
                this.isComplete = true;
            }
        }
        else if (msg instanceof String) {
            try {
                FileUploadServerHandler.logger.info("request echo msg:{}", msg);
                ctx.writeAndFlush(msg);
                this.isComplete = true;
            }
            catch (Exception e) {
                FileUploadServerHandler.logger.error(e.getMessage(), e);
            }
            ctx.close();
        }
        else {
            this.isComplete = true;
            if (Objects.nonNull(this.randomAccessFile)) {
                this.randomAccessFile.close();
            }
            ctx.close();
        }
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        FileUploadServerHandler.logger.error("netty socket通讯异常! cause by:{}", cause.getMessage());
        if (Objects.nonNull(this.randomAccessFile)) {
            try {
                this.randomAccessFile.close();
            }
            catch (IOException e) {
                FileUploadServerHandler.logger.error(e.getMessage());
            }
        }
        ctx.close();
    }
    
    static {
        logger = LoggerFactory.getLogger(FileUploadServerHandler.class);
    }
}
