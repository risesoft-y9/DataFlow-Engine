package net.risesoft.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.user.UserInfo;

@Slf4j
public class CheckUserLoginFilter implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpSession session = request.getSession();

        try {
            UserInfo loginPerson = (UserInfo)session.getAttribute("loginUser");
            if (loginPerson == null) {
                // throw new RuntimeException("No user was found in httpsession !!!");
            } else {

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
