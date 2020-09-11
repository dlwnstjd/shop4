package controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import exception.LoginException;
import logic.Item;
import logic.Sale;
import logic.SaleItem;
import logic.ShopService;
import logic.User;

@Controller
@RequestMapping("user")
public class UserController {
	@Autowired
	private ShopService service;

	@GetMapping("*")
	public String form(Model model) {
		model.addAttribute(new User());
		return null;
	}

	@PostMapping("userEntry")
	public ModelAndView add(@Valid User user, BindingResult bresult) {
		ModelAndView mav = new ModelAndView();
		if (bresult.hasErrors()) {
			bresult.reject("error.input.user");
			bresult.reject("error.duplicate.user");
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		try {
			service.userInsert(user);
			mav.setViewName("redirect:login.shop");
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
		}
		return mav;
	}

	@PostMapping("login")
	public ModelAndView login(@Valid User user, BindingResult bresult, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		if (bresult.hasErrors()) {
			bresult.reject("error.input.user");
			return mav;
		}
		try {
			User dbUser = service.getUser(user.getUserid());
			// 1. db정보의 id,password 비교
			if (user.getPassword().equals(dbUser.getPassword())) {
				// 2. 일치: session loginUser 정보 저장
				session.setAttribute("loginUser", dbUser);
				mav.setViewName("redirect:main.shop");
			} else {
				// 3. 불일치: 비밀번호 확인 내용 출력
				bresult.reject("error.login.password");
			}
			// 4. db에 해당 id정보가 없는 경우
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			bresult.reject("error.login.id");
		}
		return mav;
	}

	@RequestMapping("logout")
	public String loginChecklogout(HttpSession session) {
		session.invalidate();
		return "redirect:login.shop";
	}

	@RequestMapping("main")
	// login 되어야 실행 가능. 메서드이름을 loginxxx로 지정
	public String loginCheckmain(HttpSession session) {
		return null;
	}

	/*
	 * AOP 설정하기 1. UserController 의 check로 시작하는 메서드에 매개변수가 id, session인 경우 -로그인
	 * 안된경우: 로그인하세요. => login.shop 페이지 이동 -admin이 아니면서, 다른 아이디 정보 조회시. 본인정보만 조회가능
	 * =>main.shop 페이지 이동 2.
	 */
	@RequestMapping("mypage")
	public ModelAndView checkmypage(String id, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		User user = service.getUser(id);
		// sale 테이블에서 saleid, userid, saledate 컬럼값만
		// 저장된 Sale 객체의 List형태로 리턴
		List<Sale> salelist = service.salelist(id);
		System.out.println(salelist);
		for (Sale sa : salelist) {
			List<SaleItem> saleitemlist = service.saleItemList(sa.getSaleid());
			System.out.println("===============================================================");
			System.out.println(saleitemlist);
			int sum = 0;
			for (SaleItem si : saleitemlist) {
				Item item = service.getItem(Integer.parseInt(si.getItemid()));
				System.out.println("===============================================================");
				System.out.println(item);
				si.setItem(item);
				sum += (si.getQuantity() * si.getItem().getPrice());
			}
			sa.setItemList(saleitemlist);
			sa.setTotal(sum);
		}
		mav.addObject("user", user);
		mav.addObject("salelist", salelist);

		return mav;
	}

	@GetMapping(value = { "update", "delete" })
	public ModelAndView checkview(String id, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		User user = service.getUser(id);
		mav.addObject("user", user);
		return mav;
	}
	/*
	 * 1. 유효성 검증 2. 비밀번호 검증: 불일치 유효성 출력으로 error.login.password 코드로 실행 3. 비밀번호 일치
	 * update 실행 로그인정보 수정. 단. admin이 다른사람의 정보 수정시는 로그인 정보 수정 안됨 4. 수정완료 =>
	 * mypage.shop 으로 페이지 이동.
	 */

	@PostMapping("update")
	public ModelAndView update(@Valid User user, BindingResult bresult, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		// 유효성 검증
		if (bresult.hasErrors()) {
			bresult.reject("error.input.user");
			return mav;
		}
		// 비밀번호 검증
		User loginUser = (User) session.getAttribute("loginUser");
		// 로그인한 정보의 비밀번호와 입력된 비밀번호 검증
		if (!user.getPassword().equals(loginUser.getPassword())) {
			bresult.reject("error.login.password");
			return mav;
		}
		// 비밀번호 일치: 수정가능
		try {
			service.update(user);
			mav.setViewName("redirect:mypage.shop?id=" + user.getUserid());
			// 로그인정보수정
			if (loginUser.getUserid().equals(user.getUserid())) {
				session.setAttribute("loginUser", user);
			}
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			bresult.reject("error.user.update");
		}
		return mav;
	}

	/*
	 * 회원 탈퇴 
	 * 1. 비밀번호 검증 불일치: "비밀번호 오류 메시지 출력." delete.shop 이동 
	 * 2. 비밀번호 검증 일치: 회원 db에서 * delete하기 
	 * 본인인 경우: logout 하고, login.shop 페이지 요청 
	 * 관리자인 경우: main.shop 페이지 이동
	 */
	@PostMapping("delete")
	public ModelAndView delete(String userid, String password, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		User loginUser = (User) session.getAttribute("loginUser");	
		
		if(userid.equals("admin")) {
			throw new LoginException
			("관리자는 탈퇴 불가합니다.", "main.shop");
		}
		//관리자 로그인: 관리자비밀번호 검증
		//사용자 로그인: 본인비밀번호 검증
		if(!password.equals(loginUser.getPassword())) {
			throw new LoginException
			("회원탈퇴시 비밀번호가 틀립니다.", "delete.shop?id=" + userid);
		}
		//db삭제
		try {
			service.delete(userid);					
		}catch(Exception e) {
			e.printStackTrace();
			throw new LoginException
			("회원탈퇴시 오류가 발생했습니다.", "delete.shop?id=" + userid);
		}
		if(loginUser.getUserid().equals("admin")) { //관리자인 경우 
			mav.setViewName("redirect:main.shop"); 
		}else { //본인인 경우 service.delete(user);
			session.invalidate();
			throw new LoginException
			(userid+"회원님. 탈퇴 되었습니다.", "login.shop");
		}		 
		return mav;
	}
}
