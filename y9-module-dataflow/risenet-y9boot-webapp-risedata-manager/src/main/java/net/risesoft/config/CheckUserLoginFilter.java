package net.risesoft.config;

import net.risesoft.model.user.UserInfo;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@Slf4j
public class CheckUserLoginFilter implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
    	HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpSession session = request.getSession();

		try {
			UserInfo loginPerson = (UserInfo) session.getAttribute("loginUser");
			if (loginPerson == null) {
				//throw new RuntimeException("No user was found in httpsession !!!");
			}else {
				
			}
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		}

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}
