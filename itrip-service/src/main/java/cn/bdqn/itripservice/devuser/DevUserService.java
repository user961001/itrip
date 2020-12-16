package cn.bdqn.itripservice.devuser;

import cn.bdqn.itripbeans.pojo.devuser.DevUser;

public interface DevUserService {
	/**
	 * 用户登录
	 * @param devCode
	 * @param devPassword
	 * @return
	 */
	public DevUser login(String devCode, String devPassword) throws Exception;

	public boolean regiest(DevUser devUser) throws Exception;

	public boolean getName(String devCode) throws Exception;

	public boolean update(DevUser devUser);
	public DevUser getUserByUserCode(String devCode) throws Exception;


}
