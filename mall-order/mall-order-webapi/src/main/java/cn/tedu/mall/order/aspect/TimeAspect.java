package cn.tedu.mall.order.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TimeAspect {
    // 定义切面,目标是当项目所有业务逻辑层方法
    @Pointcut("execution(public * cn.tedu.mall.order.service.*.*(..))")
    public void timer(){}
    // 使用环绕Advice计时比较合理
    @Around("timer()")
    public Object timeRecord(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录开始时间
        long start=System.currentTimeMillis();
        // 调用切面方法
        Object obj=joinPoint.proceed();
        // 记录结束时间
        long end=System.currentTimeMillis();
        // 计算时间差
        long time= end-start;
        // 获得方法名
        String methodName=joinPoint.getSignature().getName();
        // 输出方法用时
        System.out.println(methodName+"方法用时"+time+"ms");
        // 别忘了返回!
        return obj;

    }


}
