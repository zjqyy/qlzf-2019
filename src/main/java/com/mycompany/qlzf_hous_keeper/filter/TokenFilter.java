package com.mycompany.qlzf_hous_keeper.filter;

import com.google.gson.Gson;
import com.mycompany.qlzf_hous_keeper.mapper.TokenPersistence;
import com.mycompany.qlzf_hous_keeper.service.impl.RedisService;
import com.mycompany.qlzf_hous_keeper.tools.OutData;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * @author jianQiaoZhu
 * @version 1.0
 * @date 2019/6/13 17:56
 */
@Component
@ServletComponentScan
@WebFilter(urlPatterns = "/session?*",filterName = "loginFilter")
public class TokenFilter implements Filter {

    private FilterConfig filterConfig = null;
    private final Gson gson = new Gson();
    private RedisService redis_service;
    private TokenPersistence tokenPersistence;

    public TokenFilter() {
    }

    private void doBeforeProcessing(RequestWrapper request, ResponseWrapper response)
            throws IOException, ServletException {
    }

    private void doAfterProcessing(RequestWrapper request, ResponseWrapper response)
            throws IOException, ServletException {
    }

    /**
     * @param request  The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain    The filter chain we are processing
     * @throws IOException      if an input/output error occurs
     * @throws ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setContentType("application/json; charset=utf-8");
            String id = request.getParameter("id");
            PrintWriter out = resp.getWriter();
            Object worker_id = "";
            if ((null != id) && (!id.equals(""))) {
                worker_id = redis_service.get(id);
                if (null == worker_id || worker_id.equals("")) {
                    out.println(gson.toJson(OutData.softwareFormart("请先登录"), Map.class));
                    out.flush();
                    return;
                }
                Map map_sql = new HashMap();
                map_sql.put("id", worker_id);
                List<HashMap> res = tokenPersistence.get_type(map_sql);
                if (res.isEmpty()) {
                    out.println(gson.toJson(OutData.softwareFormart("该工作人员不存在"), Map.class));
                    out.flush();
                    return;
                }
                String department = res.get(0).get("type").toString();
                String role = res.get(0).get("role").toString();
                request.setAttribute("department", department);
                request.setAttribute("role", role);
            }else {
                out.println(gson.toJson(OutData.softwareFormart("用户登录信息不能为空"), Map.class));
                out.flush();
                return;
            }
            request.setAttribute("id", worker_id.toString());
            chain.doFilter(request, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
        }
        ServletContext sc = filterConfig.getServletContext();
        XmlWebApplicationContext cxt = (XmlWebApplicationContext) WebApplicationContextUtils.getWebApplicationContext(sc);
        if (cxt != null && cxt.getBean("Redis_service") != null && redis_service == null) {
            redis_service = (RedisService) cxt.getBean("Redis_service");
        }
        if (cxt != null && cxt.getBean("TokenPersistence") != null && tokenPersistence == null) {
            tokenPersistence = (TokenPersistence) cxt.getBean("TokenPersistence");
        }

    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("Token_Filter()");
        }
        StringBuffer sb = new StringBuffer("Token_Filter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());

    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

    /**
     * This request wrapper class extends the support class
     * HttpServletRequestWrapper, which implements all the methods in the
     * HttpServletRequest interface, as delegations to the wrapped request. You
     * only need to override the methods that you need to change. You can get
     * access to the wrapped request using the method getRequest()
     */
    class RequestWrapper extends HttpServletRequestWrapper {

        public RequestWrapper(HttpServletRequest request) {
            super(request);
        }

        // You might, for example, wish to add a setParameter() method. To do this
        // you must also override the getParameter, getParameterValues, getParameterMap,
        // and getParameterNames methods.
        protected Hashtable localParams = null;

        public void setParameter(String name, String[] values) {

            if (localParams == null) {
                localParams = new Hashtable();
                // Copy the parameters from the underlying request.
                Map wrappedParams = getRequest().getParameterMap();
                Set keySet = wrappedParams.keySet();
                for (Iterator it = keySet.iterator(); it.hasNext(); ) {
                    Object key = it.next();
                    Object value = wrappedParams.get(key);
                    localParams.put(key, value);
                }
            }
            localParams.put(name, values);
        }

        @Override
        public String getParameter(String name) {

            if (localParams == null) {
                return getRequest().getParameter(name);
            }
            Object val = localParams.get(name);
            if (val instanceof String) {
                return (String) val;
            }
            if (val instanceof String[]) {
                String[] values = (String[]) val;
                return values[0];
            }
            return (val == null ? null : val.toString());
        }

        @Override
        public String[] getParameterValues(String name) {

            if (localParams == null) {
                return getRequest().getParameterValues(name);
            }
            return (String[]) localParams.get(name);
        }

        @Override
        public Enumeration getParameterNames() {

            if (localParams == null) {
                return getRequest().getParameterNames();
            }
            return localParams.keys();
        }

        @Override
        public Map getParameterMap() {

            if (localParams == null) {
                return getRequest().getParameterMap();
            }
            return localParams;
        }
    }

    /**
     * This response wrapper class extends the support class
     * HttpServletResponseWrapper, which implements all the methods in the
     * HttpServletResponse interface, as delegations to the wrapped response.
     * You only need to override the methods that you need to change. You can
     * get access to the wrapped response using the method getResponse()
     */
    class ResponseWrapper extends HttpServletResponseWrapper {

        public ResponseWrapper(HttpServletResponse response) {
            super(response);
        }
    }

}
