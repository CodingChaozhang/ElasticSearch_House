package com.lcz.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * 登录验证失败处理器
 * @author LvChaoZhang
 *
 */
public class LoginAuthFailHander extends SimpleUrlAuthenticationFailureHandler {
	private final LoginUrlEntryPoint urlEntryPoint;

	public LoginAuthFailHander(LoginUrlEntryPoint urlEntryPoint) {
		this.urlEntryPoint = urlEntryPoint;
	}
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String targetUrl = this.urlEntryPoint.determineUrlToUseForThisRequest(request, response, exception);
		targetUrl += "?"+exception.getMessage();
		super.setDefaultFailureUrl(targetUrl);
		super.onAuthenticationFailure(request, response, exception);
	}
}
