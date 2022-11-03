package cn.tedu.mall.common.security;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.pojo.domain.CsmallAuthenticationInfo;
import cn.tedu.mall.common.restful.ResponseCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 在security上下文中获得用户信息
 *
 * @author Meettry
 * @date 2022/11/2 9:36
 */
public class UserInfo {

    // 业务逻辑层中有获得当前登录用户信息的需求
    // 我们的项目会在控制器方法运行前运行的过滤器中.解析前端传入的JWT
    // 将解析获得的用户信息保存在SpringSecurity上下文中
    // 这里可以编写方法从SpringSecurity上下文中获得信息
    public static CsmallAuthenticationInfo getUserInfo(){
        // 编写SpringSecurity上下文中获取用户信息的代码
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();

        //为了逻辑严谨性,判断SpringSecurity上下文是否为null
        if (authenticationToken == null) {
            throw new CoolSharkServiceException(ResponseCode.UNAUTHORIZED,"您没有登录");
        }
        // 确定authenticationToken不为null
        // 就可以从中获取用户信息了
        return (CsmallAuthenticationInfo) authenticationToken.getCredentials();
    }

    // 业务逻辑层的方法实际上都只需要用户的id即可
    // 我们可以再编写一个方法,从用户中获得id
    public static Long getUserId(){
        return getUserInfo().getId();
    }
}
