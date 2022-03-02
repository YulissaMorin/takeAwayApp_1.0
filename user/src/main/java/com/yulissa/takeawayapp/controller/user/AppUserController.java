package com.yulissa.takeawayapp.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yulissa.takeawayapp.entity.user.AppUser;
import com.yulissa.takeawayapp.service.user.AppUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  该controller除了微服务内部互相调用返回json数据,其他都返回视图.
 *  因为我不会写前端.前后端分离的实际项目中,应所有都返回json数据,本项目中返回
 *  视图仅为展示目的.
 * </p>
 *
 * @author yulissa
 * @since 2022-01-26
 */
@Api(tags = {""})
@Controller
@RequestMapping("/app-user")
public class AppUserController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private AppUserService appUserService;

    @ResponseBody
    @ApiOperation(value = "删除")
    @PostMapping("/delete")
    public int delete(String username){
        return appUserService.delete(username);
    }

    @ApiOperation(value = "更新")
    @PutMapping("/{id}")
    public int update(AppUser appUser){
        return appUserService.updateData(appUser);
    }

    @ApiOperation(value = "查询分页数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "页码"),
        @ApiImplicitParam(name = "pageCount", value = "每页条数")
    })
    @GetMapping()
    public IPage<AppUser> findListByPage(@RequestParam Integer page,
                                   @RequestParam Integer pageCount){
        return appUserService.findListByPage(page, pageCount);
    }

    @ApiOperation(value = "id查询")
    @GetMapping("{id}")
    public AppUser findById(@PathVariable Long id){
        AppUser user = appUserService.findById(id);
        return user;
    }

    @ResponseBody
    @ApiOperation(value = "新增")
    @PostMapping("/add")
    public int add(AppUser userSecurityInfo){
        return appUserService.add(userSecurityInfo);
    }

    @GetMapping("/sign_in")
    public String signIn(Model model) {
        appUserService.signIn();
        byte[] days = appUserService.getSignedDaysThisWeek();
        model.addAttribute("days", days);
        return "打卡";
    }

    @GetMapping("/my_account")
    public String myAccount(HttpServletRequest request) {
        AppUser appUser = appUserService.findByUsername(request);
        request.setAttribute("appUser",appUser);
        return "my_account";
    }

}
