package org.rhine.cat.spring.boot.internal.servlet;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.util.UrlParser;
import org.rhine.cat.spring.boot.common.CatConstants;
import org.rhine.cat.spring.boot.internal.TraceContext;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 网关HTTP服务调用链追踪
 */
public class HttpTraceFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        Transaction t = Cat.newTransaction(CatConstants.TYPE_WEB, getRequestURI(request));
        try {
            Cat.Context context = TraceContext.getContext();
            context.addProperty(Cat.Context.ROOT, request.getHeader(CatConstants.CAT_HTTP_HEADER_ROOT_MESSAGE_ID));
            context.addProperty(Cat.Context.PARENT, request.getHeader(CatConstants.CAT_HTTP_HEADER_PARENT_MESSAGE_ID));
            context.addProperty(Cat.Context.CHILD, request.getHeader(CatConstants.CAT_HTTP_HEADER_CHILD_MESSAGE_ID));
            Cat.logEvent(CatConstants.TYPE_WEB, request.getRequestURI());
            Cat.logRemoteCallClient(context);
            MDC.put(CatConstants.LOG_TRACE_ID, context.getProperty(Cat.Context.ROOT));
            filterChain.doFilter(req, resp);
            t.setStatus(Transaction.SUCCESS);
        } catch (ServletException | IOException e) {
            t.setStatus(e);
            Cat.logError(e);
            throw e;
        } catch (Throwable e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new RuntimeException(e);
        } finally {
            t.complete();
            TraceContext.remove();
        }

    }

    private String getRequestURI(HttpServletRequest req) {
        return UrlParser.format(req.getRequestURI());
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
