package controller;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import exception.BoardException;
import logic.Board;
import logic.ShopService;

@Controller
@RequestMapping("board")
public class BoardController {
	@Autowired
	ShopService service;
	
	@GetMapping("*")
	public ModelAndView getBoard(Integer num,
			HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		Board board = null;
		if(num == null) {
			board = new Board();
		}else {
			boolean readcntable = false;
			if(request.getRequestURI().contains("detail.shop"))
				readcntable = true;
			//board: 파라미터 num에 해당하는 게시물 정보 저장
			board = service.getBoard(num, readcntable);
		}
		mav.addObject("board", board);
		return mav;
	}
	
	@PostMapping("write")
	public ModelAndView write(@Valid Board board, BindingResult bresult,
			HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		if(bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		try {
			service.boardWrite(board,request);
			mav.setViewName("redirect:list.shop");
		}catch(Exception e){
			e.printStackTrace();
			throw new BoardException
			("게시물 등록에 실패했습니다.", "write.shop");
		}		
		return mav;
	}
	
	/*
	 * pageNum: 현재 페이지 번호
	 * maxpage: 최대 페이지
	 * startpage: 보여지는 시작 페이지
	 * endpage: 보여지는 끝 페이지
	 * listcount: 전체 등록된 게시물 건수
	 * boardlist: 화면에 보여질 게시물 객체들
	 * boardno: 화면에 보여지는 게시물 번호
	 */
	@RequestMapping("list")
	public ModelAndView list(Integer pageNum,String searchtype, String searchcontent) {
		ModelAndView mav = new ModelAndView();
		if(pageNum == null || pageNum.toString().equals("")) {
			pageNum =1;
		}
		if(searchtype == null 
			|| searchtype.trim().equals("")
			|| searchcontent == null
			|| searchcontent.trim().equals("")) {//column값이 없음
			searchtype = null;
			searchcontent = null;
		}
		int limit = 10;// 한페이지에 출력할 게시물 건수
		int listcount = service.boardCount(searchtype,searchcontent);	//등록 게시물 건수
		List<Board> boardlist = service.boardlist(pageNum, limit, searchtype,searchcontent);
		int maxpage = (int) ((double) listcount / limit + 0.95);
		int startpage = ((int) (pageNum / 10.0 + 0.9) - 1) * 10 + 1;// 시작페이지번호
		int endpage = startpage + 9;// 종료페이지 번호
		if(endpage > maxpage) endpage = maxpage;
		int boardno = listcount - (pageNum -1) * limit;

		mav.addObject("pageNum", pageNum);
		mav.addObject("maxpage", maxpage);
		mav.addObject("startpage", startpage);
		mav.addObject("endpage", endpage);
		mav.addObject("listcount", listcount);
		mav.addObject("boardlist", boardlist);
		mav.addObject("boardno", boardno);
		mav.addObject("today",	//"20200713"
				new SimpleDateFormat("yyyyMMdd").format(new Date()));
		return mav;
	}
	
	@RequestMapping("imgupload")
	//upload: ckeditor에서 전달해 주는 파일 정보의 이름
	//			<input type="file" name="upload">
	//	CKEditorFuncNum: ckeditor에서 내부에서 사용되는 파라미터
	public String imgupload(MultipartFile upload, Model model,
			String CKEditorFuncNum, HttpServletRequest request) {
		String path = request.getServletContext().getRealPath("/")
					+ "board/imgfile/";	//이미지 저장할 폴더를 지정
		File f = new File(path);
		if(!f.exists()) f.mkdirs();
		if(!upload.isEmpty()) {	//업로드된 이미지 정보가 존재함.
			File file = //업로드될 파일을 저장할 File 객체 지정
					new File(path, upload.getOriginalFilename());
			try {
				upload.transferTo(file);	//업로드 파일 생성
			}catch(Exception e) {
				e.printStackTrace();
			}
		}		
		String fileName = request.getServletContext().getContextPath() 
								+ "/board/imgfile/"
					+ upload.getOriginalFilename();
		model.addAttribute("fileName", fileName);
		model.addAttribute("CKEditorFuncNum", CKEditorFuncNum);
		return "ckedit";		
	}
	
	/*
	 * 1. 파라미터 값 Board 객체 저장. 유효성 검증
	 * 2. 답변글 데이터 추가 => service에서 처리
	 * 	- grp에 해당하는 레코드 grpstep 값보다 큰 grpstep의 값을 grpstep +1
	 * 	- maxnum + 1 값으로 num 값을 저장
	 * 	- grplevel + 1 값으로 grplevel 값을 저장
	 * 	- grpstep + 1 값으로 grpstep 값을 저장
	 * 	- 파라미터값으로 board 테이블에 insert하기
	 * 3. list.shop 페이지 요청
	 */
	@PostMapping("reply")
	public ModelAndView reply(@Valid Board board, BindingResult bresult) {				
		ModelAndView mav = new ModelAndView();
		if(bresult.hasErrors()) {
			Board dbBoard = service.getBoard(board.getNum(), false);
			Map<String, Object> map = bresult.getModel();
			Board b = (Board)map.get("board");
			b.setSubject(dbBoard.getSubject());			
			return mav;
		}
		try {
			/*
			 * board: 화면에서 절달 받은 파라미터 정보 저장
			 * num, grp, grplevel, grpstop: 원글 정보
			 * name, pass, subject, content: 답글 저장될 정보			
			*/
			service.reply(board);
			mav.setViewName("redirect:list.shop");
		}catch(Exception e){
			e.printStackTrace();
			throw new BoardException
			("답글 등록에 실패했습니다.", "reply.shop?num="+board.getNum());
		}		
		return mav;		
	}
	
	/*
	 * 1. 파라미터 값 Board 객체 저장. 유효성 검증.
	 * 2. 입력된 비밀번호와 db의 비밀번호를 비교 비밀번호가 맞는경우 3으로 이동
	 * 		틀린경우 '비밀번호가 틀립니다.', update.shop Get방식으로 호출
	 * 3. 수정정보를 db에 변경
	 * 		-첨부파일 변경: 첨부파일 업로드, fileurl 정보 수정
	 * 4. detail.shop 페이지 요청
	 */
	@PostMapping("update")
	public ModelAndView update(@Valid Board board, BindingResult bresult,
			HttpServletRequest request) {				
		ModelAndView mav = new ModelAndView();
		Board dbBoard = service.getBoard(board.getNum(), false);
		//유효성 검증
		if(bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());	
			return mav;
		}
		/*
		 * board: 화면에서 절달 받은 파라미터 정보 저장
		 * num, fileurl: 원글 정보
		 * name, pass, subject, content: 답글 저장될 정보			
		*/
		System.out.println("pass1: "+ board.getPass());
		System.out.println("pass2: "+ dbBoard.getPass());
		if(!board.getPass().equals(dbBoard.getPass())) {
			throw new BoardException
				("비밀번호가 틀렸습니다.", "update.shop?num="+board.getNum());			
		}
		try {
			service.boardUpdate(board,request);
			mav.setViewName("redirect:detail.shop?num="+ board.getNum());
		}catch(Exception e) {
			e.printStackTrace();
			throw new BoardException
				("게시글 수정 실패했습니다.", "update.shop?num="+board.getNum());
		}
		return mav;		
	}
	
	/*
	 * 1. num, pass 파라미터 저장
	 * 2. db의 비밀번호와 입력된 비밀번호가 틀리면 error.login.password
	 * 		코드 입력=> 유효성 검증 내용 출력
	 * 3. db에서 해당 게시물 삭제.
	 * 		삭제 실패: 게시글 삭제 실패. delete.shop 페이지로 이동
	 * 		삭제 성공: list.shop 페이지 이동
	 */
	@PostMapping("delete")
	public ModelAndView delete(@Valid Board board, BindingResult bresult) {				
		ModelAndView mav = new ModelAndView();
		try {
			Board dbBoard = service.getBoard(board.getNum(), false);
			if(!board.getPass().equals(dbBoard.getPass())) {
				bresult.reject("error.login.password");
				return mav;			
			}
			service.boardDelete(board);
			mav.setViewName("redirect:list.shop");
		}catch(Exception e) {
			e.printStackTrace();
			throw new BoardException
				("게시글 삭제 실패했습니다.", "delete.shop?num="+board.getNum());
		}
		return mav;		
	}
}























