package com.sms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sms.entities.SmsSysUserrole;

@Repository
public interface SysUserroleRepo extends CrudRepository<SmsSysUserrole, String> {

	@Query(value = "select f from SmsSysUserrole f where f.id.iduser=:iduser")
	List<SmsSysUserrole> findAllByIdUser(@Param("iduser") String iduser);

	@Query(value = "delete from {h-schema}sms_sys_userrole t where t.iduser=:iduser", nativeQuery = true)
	List<SmsSysUserrole> deleteByIduser(@Param("iduser") String iduser);
	
	@Query(value = "select IDROLE from sms_sys_userrole where IDUSER = :userid and rownum <= 1", nativeQuery = true)
	String findByRoleID(@Param(value = "userid") String userid);
}
