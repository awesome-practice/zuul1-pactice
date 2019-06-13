package com.practice.zuul1.gateway.filters.zuul.pre;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import org.apache.catalina.connector.RequestFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.http.MediaType;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Collections;

/**
 * multipart successful read auth param
 *
 * @author Luo Bao Ding
 * @since 2018/6/15
 */
public class ParsePostBodyKeyZuulFilter extends ZuulFilter implements InitializingBean {
    private final static Logger LOGGER = LoggerFactory.getLogger(ParsePostBodyKeyZuulFilter.class);
    private final ObjectReader reader = new ObjectMapper().reader();
    //[[[[[[[[[[[[[[[[[[  read request body
    private WebDataBinderFactory dataBinderFactory;

    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    private HandlerMethodArgumentResolverComposite argumentResolvers;
    private MethodParameter[] parameters;

    private HandlerMethod handlerMethod;
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 服务于获取auth参数的method name, com.practice.zuul1.gateway.filters.pre.SimpleFilter#authParam(java.lang.String, java.lang.String)
     */
    public static final String METHOD_AUTH_PARAM = "authParam";

    //============= multipart
    private DispatcherServlet dispatcherServlet;
    //    private MultipartResolver multipartResolver;

    //    ]]]]]]]]]]]]]]]]]
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();

        String contentType = request.getContentType();
        if (contentType == null) {
            contentType = "";
        }
        int semicolon = contentType.indexOf(';');
        if (semicolon >= 0) {
            contentType = contentType.substring(0, semicolon).trim();
        } else {
            contentType = contentType.trim();
        }
        contentType = contentType.toLowerCase();


        try {

           /*
            Object[] authValues = getMethodArgumentValues(request, response);
            String sign = (String) authValues[0];
            String uid = (String) authValues[1];
            System.out.println("sign = " + sign);
            System.out.println("uid = " + uid);
            */

            String sign = "";
            String uid = "";


//[[[[[[[[[[[[[[[[[[[[
            String method = request.getMethod();
            if ("POST".equals(method.toUpperCase())) {
                RequestFacade requestFacade = null;
                HttpServletRequestWrapper requestWrapper = (HttpServletRequestWrapper) request;
                HttpServletRequest wrappedRequest = requestWrapper.getRequest();
                if (MediaType.MULTIPART_FORM_DATA_VALUE.equals(contentType)) {
//                StandardMultipartHttpServletRequest multipartHttpServletRequest = (StandardMultipartHttpServletRequest) wrappedRequest;
                    AbstractMultipartHttpServletRequest multipartHttpServletRequest = (AbstractMultipartHttpServletRequest) wrappedRequest;
                    requestFacade = (RequestFacade) multipartHttpServletRequest.getRequest();
                    sign = requestFacade.getParameter("sign");
                    uid = requestFacade.getParameter("uid");
                } else if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(contentType)) {
                    requestFacade = (RequestFacade) wrappedRequest;
                    sign = requestFacade.getParameter("sign");
                    uid = requestFacade.getParameter("uid");
                } else if (MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
//                    Object[] authValues = getMethodArgumentValues(request, response);
                    /*InputStream inputStream = (InputStream) currentContext.get("requestEntity");
                    if (inputStream == null) {
                        inputStream = requestWrapper.getInputStream();
                    }*/
                    InputStream inputStream = requestWrapper.getInputStream();

                    String jsonStr = StreamUtils.copyToString(inputStream, Charset.forName("UTF-8"));
                    JsonNode jsonNode = reader.readTree(jsonStr);
//                    JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                    sign = jsonNode.get("sign").asText();
                    uid = jsonNode.get("uid").asText();
                }


            }

//            ]]]]]]]]]]]]]]]]]]]]

            System.out.println("sign = " + sign);
            System.out.println("uid = " + uid);

        } catch (Exception e) {
            e.printStackTrace();
        }

        LOGGER.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
        return null;
    }

    //   [[[[[[[[[[[[[[[[[[[[[[  read auth param
    public void authParam(String sign, String uid) {
    }

    private Object[] getMethodArgumentValues(HttpServletRequest request, HttpServletResponse response,
                                             Object... providedArgs) throws Exception {

        ServletWebRequest webRequest;
        webRequest = new ServletWebRequest(request, response);

//  ========== urlencoded
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
//        modelFactory.initModel(webRequest, mavContainer, invocableMethod);
        mavContainer.setIgnoreDefaultModelOnRedirect(true);

        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            GenericTypeResolver.resolveParameterType(parameter, handlerMethod.getClass());
            args[i] = resolveProvidedArgument(parameter, providedArgs);
            if (args[i] != null) {
                continue;
            }
            if (this.argumentResolvers.supportsParameter(parameter)) {
                try {
                    args[i] = this.argumentResolvers.resolveArgument(
                            parameter, mavContainer, webRequest, this.dataBinderFactory);
                } catch (Exception ex) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(getArgumentResolutionErrorMessage("Error resolving argument", i), ex);
                    }
                    throw ex;
                }
            }
        }
        return args;
    }
/*
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        for (HandlerMapping hm : this.handlerMappings) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(
                        "Testing handler map [" + hm + "] in DispatcherServlet with name '" + getServletName() + "'");
            }
            HandlerExecutionChain handler = hm.getHandler(request);
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }
*/


    private String getArgumentResolutionErrorMessage(String message, int index) {
        MethodParameter param = getMethodParameters()[index];
        message += " [" + index + "] [type=" + param.getParameterType().getName() + "]";
        return getDetailedErrorMessage(message);
    }


    private String getDetailedErrorMessage(String message) {
        StringBuilder sb = new StringBuilder(message).append("\n");
        sb.append("HandlerMethod details: \n");
        sb.append("Controller [").append(handlerMethod.getBeanType().getName()).append("]\n");
        sb.append("Method [").append(handlerMethod.getMethod().toGenericString()).append("]\n");
        return sb.toString();
    }

    private MethodParameter[] getMethodParameters() {
        return this.parameters.clone();
    }

    private Object resolveProvidedArgument(MethodParameter parameter, Object... providedArgs) {
        if (providedArgs == null) {
            return null;
        }
        for (Object providedArg : providedArgs) {
            if (parameter.getParameterType().isInstance(providedArg)) {
                return providedArg;
            }
        }
        return null;
    }

    @Autowired
    public void setRequestMappingHandlerAdapter(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;

    }

    @Autowired
    public void setDispatcherServlet(DispatcherServlet dispatcherServlet) {
        this.dispatcherServlet = dispatcherServlet;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Field field = ReflectionUtils.findField(RequestMappingHandlerAdapter.class, "argumentResolvers");
        boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            this.argumentResolvers = (HandlerMethodArgumentResolverComposite) ReflectionUtils.getField(field, this.requestMappingHandlerAdapter);
            this.handlerMethod = new HandlerMethod(this, getClass().getMethod(METHOD_AUTH_PARAM, String.class, String.class));
            this.parameters = this.handlerMethod.getMethodParameters();
//        this.dataBinderFactory = new DefaultDataBinderFactory(new ConfigurableWebBindingInitializer());
//        this.dataBinderFactory = null;
            this.dataBinderFactory = new ServletRequestDataBinderFactory(Collections.emptyList(), new ConfigurableWebBindingInitializer());
        } finally {
            field.setAccessible(accessible);
        }
//        multipartResolver = dispatcherServlet.getMultipartResolver();

    }

//    ================ multipart ===========

/*    protected HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
        if (this.multipartResolver != null && this.multipartResolver.isMultipart(request)) {
           *//* if (WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class) != null) {
                LOGGER.debug("Request is already a MultipartHttpServletRequest - if not in a forward, " +
                        "this typically results from an additional MultipartFilter in web.xml");
            }
            else if (request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) instanceof MultipartException) {
                LOGGER.debug("Multipart resolution failed for current request before - " +
                        "skipping re-resolution for undisturbed error rendering");
            }
            else {
                return this.multipartResolver.resolveMultipart(request);
            }*//*
            return this.multipartResolver.resolveMultipart(request);

        }
        // If not returned before: return original request.
        return request;
    }*/

//    ]]]]]]]]]]]]]]]]]]]]]]]

}
