package com.yulissa.takeawayapp.controller.useroauth;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yulissa.takeawayapp.constant.ConstantNames;
import com.yulissa.takeawayapp.entity.useroauth.UserSecurityInfo;
import com.yulissa.takeawayapp.service.useroauth.UserSecurityInfoService;
import com.yulissa.takeawayapp.util.CookieUtil;
import com.yulissa.takeawayapp.vo.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yulissa
 * @since 2022-01-29
 */
@Api(tags = {""})
@Controller
@RequestMapping("/user_account")
public class UserSecurityInfoController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private UserSecurityInfoService userSecurityInfoService;

    @HystrixCommand(fallbackMethod = "fallback")
    @GetMapping("/register_confirm/{encodedUsername}/{emailCode}")
    public String registerConfirm(@PathVariable String encodedUsername,@PathVariable String emailCode, Model model) throws Exception {
        ApiResult result = userSecurityInfoService.registerConfirm(encodedUsername, emailCode);
        model.addAttribute("message", result.getMessage());
        return result.isFlag() ? "success" : "fail";
    }

    /**
     * 未添加合法校验注解,因为js已经判断过(js待补充),如果还有错误
     * 肯定是恶意请求,直接抛出非法请求异常即可
      */

    @ApiOperation(value = "新增")
    @PostMapping("/register")
    public String register(UserSecurityInfo userSecurityInfo, HttpServletRequest request, Model model) throws Exception {
        ApiResult result = userSecurityInfoService.register(userSecurityInfo, request);
        model.addAttribute("message", result.getMessage());
        return result.isFlag() ? "success" : "fail";
    }

    @ApiOperation(value = "新增")
    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }

    @ApiOperation(value = "删除")
    @GetMapping("/delete")
    public String delete(Model model, HttpServletRequest request,HttpServletResponse response){
        String username = userSecurityInfoService.delete(request, response);
        model.addAttribute("username", username);
        return "bye";
    }

    @ApiOperation(value = "登出")
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        userSecurityInfoService.logout(request, response);
        return "homepage";
    }

    @ApiOperation(value = "更新")
    @PutMapping("/update")
    public String update( UserSecurityInfo userSecurityInfo){
        ApiResult result = userSecurityInfoService.updateData(userSecurityInfo);
        return result.isFlag() ? "success" : "fail";
    }

    @ApiOperation(value = "登录")
    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, Model model) {
        String username = CookieUtil.readCookie(request, "username");
        if (!"".equals(username)) {
            model.addAttribute("username", username);
        }
        return "login";
    }

    @ApiOperation(value = "登录")
    @PostMapping("/login")
    public String login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = userSecurityInfoService.login(username, password, request, response);
        return result.isFlag() ? "redirect:/user_account/homepage" : "fail";
    }

    @ApiOperation(value = "导航页面")
    @GetMapping("/homepage")
    public String homepage(HttpServletRequest request) {
        if (!StringUtils.isEmpty(CookieUtil.readCookie(request,ConstantNames.TOKEN_NAME))) {
            request.setAttribute("username", CookieUtil.readCookie(request, "username"));
        }
        return "homepage";
    }

    @GetMapping("/getCaptcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        userSecurityInfoService.getCaptcha(request, response);
    }


    @GetMapping("/reset_password/step1")
    public String resetPasswordPage() {
        return "resetPasswordStep1";
    }

    @PostMapping("/reset_password/step1")
    public String resetPasswordStep1(Model model, String email) {
        ApiResult result = userSecurityInfoService.resetPasswordStep1(email);
        model.addAttribute("message", result.getMessage());
        return result.isFlag() ? "success" : "fail";
    }

    @GetMapping("/reset_password/step2/{uid}/{emailCode}")
    public String resetPasswordStep2(Model model, @PathVariable String uid, @PathVariable String emailCode) {
        model.addAttribute("uid", uid);
        model.addAttribute("emailCode", emailCode);
        return "resetPasswordStep2";
    }

    @PostMapping("/reset_password/step2")
    public String resetPasswordStep2(Model model, @RequestParam long uid, @RequestParam String emailCode, @RequestParam String password) {
        ApiResult result = userSecurityInfoService.resetPasswordStep2(uid, emailCode, password);
        model.addAttribute("message", result.getMessage());
        return result.isFlag() ? "success" : "fail";
    }

    public String fallback(@PathVariable String encodedUsername,@PathVariable String emailCode, Model model) throws Exception {
        ApiResult result = userSecurityInfoService.sendRegisterInfoToMq(encodedUsername, emailCode);
        model.addAttribute("message", result.getMessage());
        return result.isFlag() ? "success" : "fail";
    }
}
