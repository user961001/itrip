package cn.bdqn.itripcontroller.devuser;

import cn.bdqn.itripbeans.pojo.devuser.DevUser;
import cn.bdqn.itripcommon.util.ActivateCode;
import cn.bdqn.itripcommon.util.SDKTestSendTemplateSMS;
import cn.bdqn.itripcommon.util.SendEmailUtil;
import cn.bdqn.itripservice.devuser.DevUserService;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class LoginController {
    @Resource
    private DevUserService devUserService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //测试登陆
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(Model model, @RequestParam("userName") String userName, @RequestParam("password") String password) throws Exception {
//        DevUser devUser = devUserService.login("test001", "123456");
        DevUser devUser = devUserService.login(userName, password);
        if (devUser != null) {
            model.addAttribute("user", devUser);
            return "index";
        } else {
            return "error";
        }
    }

    // 欢迎页
    @RequestMapping("/")
    public String welcom() {
        return "login";
    }

    // 测试redis
    @RequestMapping("/testRedis")
    public String testRedis() {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set("name", "xiaoming");
        String name = operations.get("name");
        System.out.print(">>>>>>>>>>>>>>" + name);
        return "index";
    }

    @RequestMapping("/registerInit")
    public String registerInit() {
        return "regiest";
    }

    // 实现用户注册
    @RequestMapping("/regiest")
    public ModelAndView regiest(DevUser devUser) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("regiest");
        // 如果用户选择邮箱注册实现邮箱激活
        if (devUser.getDevEmail() != null) {
            // 生成激活码
            String activateCode = ActivateCode.getActivateCode();
            // 设置激活码
            devUser.setActivatCode(activateCode);
            // 设置激活状态
            devUser.setActivated(0);
            // 给用户邮箱发送激活码
            boolean sendResult = SendEmailUtil.sendEmail(devUser.getDevEmail(), activateCode);
            //            // 如果发送成功则跳转到激活画面
            if (sendResult) {
                boolean regiestResult = devUserService.regiest(devUser);
                // 信息注册成功则跳转到激活画面
                if (regiestResult) {
                    ValueOperations<String, Object> operations = redisTemplate.opsForValue();
                    operations.set(devUser.getDevEmail(), activateCode);
                    modelAndView.addObject("message", "success");
                }
            }
        }
        return modelAndView;
    }

    // 实现用户激活
    @RequestMapping("/doActive")
    public String doActive(DevUser devUser) throws Exception {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        // 先从缓存中读取
        devUser.setActivated(1);
        if (operations.get(devUser.getDevEmail()) != null) {
            if (operations.get(devUser.getDevEmail()).equals(devUser.getActivatCode())) {
                // 修改用户的激活状态并跳转到登陆画面
                if (devUserService.update(devUser)) {
                    return "login";
                } else {
                    System.out.println("_______用户状态修改失败");
                    return "error";
                }
            } else {
                // 缓存中不一致的话就从数据库中读取
                System.out.println("验证码不正确");
                return "regiest";

            }

        } else {
            // 如果取到的用户激活码和画面一致则修改用户的激活状态并跳转到登陆画面
            DevUser devUser1 = devUserService.getUserByUserCode(devUser.getDevCode());
            if (null != devUser1) {
                if (devUser1.getActivatCode().equals(devUser.getActivatCode())) {
                    if (devUserService.update(devUser)) {
                        return "login";
                    } else {
                        System.out.println("_______用户状态修改失败");
                        return "error";
                    }
                } else {
                    System.out.println("验证码不正确");
                    return "regiest";
                }
            } else {
                System.out.println("用户名不正确");
                return "regiest";
            }
        }
    }

    @RequestMapping(value = "/username", method = RequestMethod.POST)
    @ResponseBody
    public Object username(String devCode) throws Exception {
        System.out.println(devCode);
        boolean flag = false;
        if (devUserService.getName(devCode)) {
            flag = true;
        }
        return JSONArray.toJSONString(flag);
    }

    @RequestMapping("/send")
    public String SendTemplateSMS() {
        if(SDKTestSendTemplateSMS.SendTemplateSMS("12342","3")){
            return "login";
        }else {
            return "error";
        }
    }
}