package com.practice.zuul1.gateway.filters.zuul.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Luo Bao Ding
 * @since 2018/12/12
 */
public class ExtractKeyZuulPreFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        System.out.println(">>>>>>>>>>>>>>>>>start ExtractKeyZuulPreFilter.run");

        String method = request.getMethod();
        System.out.println("+++++++++++ method = " + method);

        String contentType = request.getContentType();
        System.out.println("+++++++++++ contentType = " + contentType);

        String name = request.getParameter("name");
        System.out.println("+++++++++++ name = " + name);
        Assert.isNull(name, "the parameter 'name' can not be gotten");

        String file = request.getParameter("file");
        System.out.println("++++++++++++ file = " + file);
        Assert.isNull(file, "the parameter 'file' can not be gotten");

        String v1 = request.getParameter("k1");
        System.out.println("++++++++++++ v1 = " + v1);
        System.out.println("^^^^^^^^^^^^^^^^^^^^end ExtractKeyZuulPreFilter.run");


        return null;
    }
}
