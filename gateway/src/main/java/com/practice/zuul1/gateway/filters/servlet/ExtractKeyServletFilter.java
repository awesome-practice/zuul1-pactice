package com.practice.zuul1.gateway.filters.servlet;

import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Luo Bao Ding
 * @since 2018/12/12
 */
public class ExtractKeyServletFilter extends OncePerRequestFilter {

    public ExtractKeyServletFilter(){
        super();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println(">>>>>>>>>>>>>>>start: ExtractKeyServletFilter.doFilterInternal");

        String name = request.getParameter("name");
        System.out.println("ExtractKeyServletFilter: name = " + name);
        Assert.isNull(name, "the parameter 'name' can not be gotten");

        String file = request.getParameter("file");
        Assert.isNull(file, "the parameter 'file' can not be gotten");
        filterChain.doFilter(request, response);

        System.out.println("^^^^^^^^^^^^^^^^^^end: ExtractKeyServletFilter.doFilterInternal");

    }
}
