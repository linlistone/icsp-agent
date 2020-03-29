package cn.com.yusys.icsp.agent.server.socket;


import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author 李文浩
 * @date 2018/9/18
 */
public class HttpDownloadHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public HttpDownloadHandler() {
        super(false);
    }
    private static final Logger logger = LoggerFactory.getLogger(HttpDownloadHandler.class);;

    private String filePath = "/Users/linli/Downloads/common-code-master.zip";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        if (uri.startsWith("/download") && request.method().equals(HttpMethod.GET)) {
            GeneralResponse generalResponse = null;
            File file = new File(filePath);
            try {
                final RandomAccessFile raf = new RandomAccessFile(file, "r");
                long fileLength = raf.length();
                HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
                response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
                ctx.write(response);
                ChannelFuture sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
                sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                    @Override
                    public void operationComplete(ChannelProgressiveFuture future)
                            throws Exception {
                        logger.info("file {} transfer complete.", file.getName());
                        raf.close();
                    }

                    @Override
                    public void operationProgressed(ChannelProgressiveFuture future,
                                                    long progress, long total) throws Exception {
                        if (total < 0) {
                            logger.warn("file {} transfer progress: {}", file.getName(), progress);
                        } else {
                            logger.debug("file {} transfer progress: {}/{}", file.getName(), progress, total);
                        }
                    }
                });
                ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            } catch (FileNotFoundException e) {
                logger.warn("file {} not found", file.getPath());
                generalResponse = new GeneralResponse(HttpResponseStatus.NOT_FOUND, String.format("file %s not found", file.getPath()), null);
                ResponseUtil.response(ctx, request, generalResponse);
            } catch (IOException e) {
                logger.warn("file {} has a IOException: {}", file.getName(), e.getMessage());
                generalResponse = new GeneralResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, String.format("读取 file %s 发生异常", filePath), null);
                ResponseUtil.response(ctx, request, generalResponse);
            }
        } else {
            ctx.fireChannelRead(request);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        logger.warn("{}", e);
        ctx.close();

    }
}
