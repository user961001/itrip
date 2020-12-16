package cn.bdqn.itripservice.devuser;

import cn.bdqn.itripdao.devuser.DevUserMapper;
import cn.bdqn.itripbeans.pojo.devuser.DevUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DevUserServiceImpl implements DevUserService {
    @Resource
    private DevUserMapper devUserMapper;

    @Override
    public DevUser login(String devCode, String devPassword) throws Exception {
        DevUser user = null;
        user = devUserMapper.getLoginUser(devCode);
        //匹配密码
        if (null != user) {
            if (!user.getDevPassword().equals(devPassword))
                user = null;
        }
        return user;
    }

    @Override
    public boolean regiest(DevUser devUser) throws Exception {
        boolean regiest = false;
        if (devUserMapper.addDecUser(devUser) > 0) {
            regiest = true;
        }
        return regiest;
    }

    @Override
    public boolean getName(String devCode) throws Exception {
        DevUser user = null;
        System.out.println("service---" + devCode);
        user = devUserMapper.getLoginUser(devCode);
        System.out.println(user);
        //匹配密码
        if (null != user) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean update(DevUser devUser) {
        boolean flag = false;
        if(devUserMapper.updateActivated(devUser)>0){
            flag=true;
        }
        return flag;
    }

    @Override
    public DevUser getUserByUserCode(String devCode) throws Exception {
        return devUserMapper.getLoginUser(devCode);
    }

}
