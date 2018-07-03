package com.lcz.security;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.lcz.base.LoginUserUtil;
import com.lcz.entity.User;
import com.lcz.service.ISmsService;
import com.lcz.service.IUserService;

/**
 * Created by codingchaozhang.
 */
public class AuthFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private IUserService userService;

    @Autowired
    private ISmsService smsService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String name = obtainUsername(request);
        if (!Strings.isNullOrEmpty(name)) {
            request.setAttribute("username", name);
            return super.attemptAuthentication(request, response);
        }

        String telephone = request.getParameter("telephone");
        if (Strings.isNullOrEmpty(telephone) || !LoginUserUtil.checkTelephone(telephone)) {
            throw new BadCredentialsException("Wrong telephone number");
        }

        User user = userService.findUserByTelephone(telephone);
        String inputCode = request.getParameter("smsCode");
        String sessionCode = smsService.getSmsCode(telephone);
        if (Objects.equals(inputCode, sessionCode)) {
            if (user == null) { // 如果用户第一次用手机登录 则自动注册该用户
                user = userService.addUserByPhone(telephone);
            }
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } else {
            throw new BadCredentialsException("smsCodeError");
        }
    }
}
