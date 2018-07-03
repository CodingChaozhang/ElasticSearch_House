package com.lcz.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.lcz.entity.User;
import com.lcz.service.IUserService;

/**
 * 自定义认证实现
 * @author LvChaoZhang
 *
 */
public class AuthProvider implements AuthenticationProvider{
	@Autowired
	private IUserService userService;
	
	private final Md5PasswordEncoder passwordEncoder=new Md5PasswordEncoder();
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username=authentication.getName();
		String inputPassword=(String) authentication.getCredentials();
		User user = userService.findUserByName(username);
		if (user==null) {
			throw new AuthenticationCredentialsNotFoundException("authError");
		}
		if (this.passwordEncoder.isPasswordValid(user.getPassword(), inputPassword, user.getId())) {
			return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		}else {
			throw new BadCredentialsException("authError");
		}
		
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

}
