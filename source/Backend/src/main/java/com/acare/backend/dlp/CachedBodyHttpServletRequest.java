package com.acare.backend.dlp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * Wrapper cho HttpServletRequest, cho phép ĐỌC BODY NHIỀU LẦN.
 *
 * Vấn đề gốc:
 *   Mặc định, InputStream của HttpServletRequest chỉ đọc được 1 lần.
 *   Sau khi DlpFilter đọc body để quét nhạy cảm → Controller sẽ không đọc được nữa!
 *
 * Giải pháp:
 *   Wrapper này cache toàn bộ body vào byte[] khi khởi tạo.
 *   Mỗi lần gọi getInputStream() hoặc getReader() → trả về stream mới từ cache.
 *
 * Sử dụng trong DlpFilter:
 *   CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
 *   String body = cachedRequest.getCachedBody();   // Đọc lần 1 để quét
 *   filterChain.doFilter(cachedRequest, response); // Controller vẫn đọc được body
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    /** Body đã được cache dưới dạng byte array */
    private final byte[] cachedBody;

    /**
     * Constructor: đọc toàn bộ body từ request gốc và cache lại.
     * Lưu ý: body chỉ được đọc 1 lần ở đây, sau đó mọi lần đọc
     *         đều đọc từ cache (không đụng vào request gốc nữa).
     */
    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        this.cachedBody = request.getInputStream().readAllBytes();
    }

    /**
     * Trả về body dưới dạng String (UTF-8).
     * Dùng trong DlpFilter để quét nội dung nhạy cảm.
     */
    public String getCachedBody() {
        return new String(this.cachedBody);
    }

    /**
     * Override: trả về InputStream MỚI mỗi lần gọi → đọc được nhiều lần.
     * Controller và các filter khác gọi method này sẽ nhận được stream đầy đủ.
     */
    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(this.cachedBody);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Không cần implement cho sync mode
            }

            @Override
            public int read() {
                return byteStream.read();
            }
        };
    }

    /**
     * Override: trả về BufferedReader mới từ cache.
     * Một số framework đọc body qua getReader() thay vì getInputStream().
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
