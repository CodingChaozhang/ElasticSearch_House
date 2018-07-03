package com.lcz.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.lcz.entity.Role;
/**
 * 角色数据DAO
 * @author LvChaoZhang
 *
 */
public interface RoleRepository extends CrudRepository<Role, Long> {
	List<Role> findRolesByUserId(Long userId);
}
