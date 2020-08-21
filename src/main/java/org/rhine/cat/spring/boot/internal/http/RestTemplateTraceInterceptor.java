package org.rhine.cat.spring.boot.internal.http;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.util.UrlParser;
import org.rhine.cat.spring.boot.common.CatConstants;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RestTemplateTraceInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest req, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        Transaction t = Cat.newTransaction(CatConstants.HTTP_REQUEST, getRequestURI(request));
        logRequestClientInfo(request, CatConstants.HTTP_REQUEST);
        logRequestPayload(request, CatConstants.HTTP_REQUEST);
        ClientHttpResponse response;
        try {
            response = execution.execute(req, body);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw e;
        } finally {
            t.complete();
        }
        return response;
    }

    private String getRequestURI(HttpServletRequest request) {
        return UrlParser.format(request.getRequestURI());
    }

    private void logRequestClientInfo(HttpServletRequest req, String type) {
        StringBuilder sb = new StringBuilder(1024);
        String ip = "";
        String ipForwarded = req.getHeader("x-forwarded-for");

        if (ipForwarded == null) {
            ip = req.getRemoteAddr();
        } else {
            ip = ipForwarded;
        }

        sb.append("IPS=").append(ip);
        sb.append("&VirtualIP=").append(req.getRemoteAddr());
        sb.append("&Server=").append(req.getServerName());
        sb.append("&Referer=").append(req.getHeader("referer"));
        sb.append("&Agent=").append(req.getHeader("user-agent"));

        Cat.logEvent(type, type + ".Server", Message.SUCCESS, sb.toString());
    }

    private void logRequestPayload(HttpServletRequest req, String type) {
        StringBuilder sb = new StringBuilder(256);

        sb.append(req.getScheme().toUpperCase()).append('/');
        sb.append(req.getMethod()).append(' ').append(req.getRequestURI());

        String qs = req.getQueryString();

        if (qs != null) {
            sb.append('?').append(qs);
        }

        Cat.logEvent(type, type + ".Method", Message.SUCCESS, sb.toString());
    }
}
