package com.lcz.service.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.lcz.base.LoginUserUtil;
import com.lcz.entity.Role;
import com.lcz.entity.User;
import com.lcz.repository.RoleRepository;
import com.lcz.repository.UserRepository;
import com.lcz.service.IUserService;
import com.lcz.service.ServiceResult;
import com.lcz.web.dto.UserDTO;

@Service
public class UserServiceImpl implements IUserService  {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	private final Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

	@Override
	public User findUserByName(String userName) {
		User user= userRepository.findByName(userName);
		if (user==null) {
			return null;
		}
		List<Role> roles = roleRepository.findRolesByUserId(user.getId());
		if (roles==null||roles.isEmpty()) {
			throw new DisabledException("权限非法");
		}
		List<GrantedAuthority> authorities=new ArrayList<>();
		roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
	    user.setAuthorityList(authorities);
	    return user;
	}

	@Override
	public ServiceResult<UserDTO> findById(Long userId) {
		 User user = userRepository.findOne(userId);
		 if (user == null) {
            return ServiceResult.notFound();
		 }
		 UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		 return ServiceResult.of(userDTO);
	}
	 @Override
	    public User findUserByTelephone(String telephone) {
	        User user = userRepository.findUserByPhoneNumber(telephone);
	        if (user == null) {
	            return null;
	        }
	        List<Role> roles = roleRepository.findRolesByUserId(user.getId());
	        if (roles == null || roles.isEmpty()) {
	            throw new DisabledException("权限非法");
	        }

	        List<GrantedAuthority> authorities = new ArrayList<>();
	        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
	        user.setAuthorityList(authorities);
	        return user;
	    }

	    @Override
	    @Transactional
	    public User addUserByPhone(String telephone) {
	        User user = new User();
	        user.setPhoneNumber(telephone);
	        user.setName(telephone.substring(0, 3) + "****" + telephone.substring(7, telephone.length()));
	        Date now = new Date();
	        user.setCreateTime(now);
	        user.setLastLoginTime(now);
	        user.setLastUpdateTime(now);
	        user = userRepository.save(user);

	        Role role = new Role();
	        role.setName("USER");
	        role.setUserId(user.getId());
	        roleRepository.save(role);
	        user.setAuthorityList(Lists.newArrayList(new SimpleGrantedAuthority("ROLE_USER")));
	        return user;
	    }

	    @Override
	    @Transactional
	    public ServiceResult modifyUserProfile(String profile, String value) {
	        Long userId = LoginUserUtil.getLoginUserId();
	        if (profile == null || profile.isEmpty()) {
	            return new ServiceResult(false, "属性不可以为空");
	        }
	        switch (profile) {
	            case "name":
	                userRepository.updateUsername(userId, value);
	                break;
	            case "email":
	                userRepository.updateEmail(userId, value);
	                break;
	            case "password":
	                userRepository.updatePassword(userId, this.passwordEncoder.encodePassword(value, userId));
	                break;
	            default:
	                return new ServiceResult(false, "不支持的属性");
	        }
	        return ServiceResult.success();
	    }
}
