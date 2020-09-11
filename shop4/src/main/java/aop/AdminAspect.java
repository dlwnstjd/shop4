package aop;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import exception.LoginException;
import logic.User;

@Component	//객체화
@Aspect		//AOP 실행 클래스
@Order(3)	//AOP 실행 순서
public class AdminAspect {
	@Around("execution(* controller.Admin*.*(..))")
	public Object adminLoginCheck(ProceedingJoinPoint joinPoint) throws Throwable{
		User loginUser = null;
		//sessio 객체 가져오는 방법
		for(Object o : joinPoint.getArgs()) {
			if(o instanceof HttpSession) {
				HttpSession session = (HttpSession)o;
				loginUser = (User)session.getAttribute("loginUser");
			}
		}
		if(loginUser == null) {
			throw new LoginException
				("로그인 후 이용가능합니다", "../user/login.shop");
		}	
		if(!loginUser.getUserid().equals("admin")) {
			throw new LoginException
				("관리자만 접속 가능합니다.", "../user/main.shop?id="+loginUser.getUserid());
		}		
			
		return joinPoint.proceed();		
	}
}
