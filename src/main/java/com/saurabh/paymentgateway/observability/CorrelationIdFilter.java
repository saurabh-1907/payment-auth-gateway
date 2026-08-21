package com.saurabh.paymentgateway.observability;

import jakarta.servlet.*;import jakarta.servlet.http.*;import org.slf4j.MDC;import org.springframework.stereotype.Component;import java.io.IOException;import java.util.UUID;
@Component public class CorrelationIdFilter implements Filter { public static final String HEADER="X-Correlation-Id"; public void doFilter(ServletRequest req,ServletResponse res,FilterChain chain)throws IOException,ServletException{HttpServletRequest r=(HttpServletRequest)req;HttpServletResponse p=(HttpServletResponse)res;String id=r.getHeader(HEADER);if(id==null||id.length()>100)id=UUID.randomUUID().toString();try{MDC.put("correlationId",id);p.setHeader(HEADER,id);chain.doFilter(req,res);}finally{MDC.remove("correlationId");}} }
